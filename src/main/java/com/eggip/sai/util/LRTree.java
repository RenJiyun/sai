package com.eggip.sai.util;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Try;
import com.jnape.palatable.lambda.adt.Unit;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Try.*;

/**
 * @see https://www.sitepoint.com/hierarchical-data-database-2/
 * @see https://www.cnblogs.com/answercard/p/4961218.html
 */

@Component
public class LRTree implements ApplicationContextAware {

    @Autowired
    @PersistenceContext
    private EntityManager em;

    private ApplicationContext applicationContext;

    @Autowired
    private PlatformTransactionManager transactionManager;


    /**
     * 直接调用getDirectt
     * @param cargoClass
     * @param <T>
     * @return
     */
    public <T extends LRTreeNode<T>> Try<Maybe<T>> getTreeSlow(Class<T> cargoClass) {
        Try<Maybe<T>> root = getRoot(cargoClass);
        return root.fmap(maybe -> {
            if (maybe.toOptional().isPresent()) {
                doGetTreeSlow(cargoClass, maybe.orElse(null));
            }
            return maybe;
        });
    }


    private <T extends LRTreeNode<T>> void doGetTreeSlow(Class<T> cargoClass, T parent) {
        List<T> children = getDirectChildren(cargoClass, parent).orThrow().orElse(new ArrayList<>());
        if (children.size() > 0) {
            for (T t : children) {
                doGetTreeSlow(cargoClass, t);
            }

            parent.setChildren(children);
        }
    }


    public <T extends LRTreeNode<T>> Try<Maybe<List<T>>> getTreeFast(Class<T> cargoClass) {
        return null;
    }



    /**
     * 获取根节点
     * @param cargoClass
     * @param <T>
     * @return
     */
    public <T extends LRTreeNode<T>> Try<Maybe<T>> getRoot(Class<T> cargoClass) {
        if (cargoClass == null)
            return failure(new IllegalArgumentException("cargoClass is null"));

        Query query = em.createQuery(String.format("select c from %s c where c.level_ = 1", cargoClass.getSimpleName()));

        return trying(() -> (T) query.getSingleResult()).<Try<Maybe<T>>>match(
                t -> t instanceof NoResultException ? success(nothing()) : failure(t), s -> success(just(s)));
    }


    /**
     * 获取父节点
     */
    @SuppressWarnings("unchecked")
    public <T extends LRTreeNode<T>> Try<Maybe<T>> getParent(Class<T> cargoClass, T child) {
        if (child == null)
            return failure(new IllegalArgumentException("child is null"));
        if (cargoClass == null)
            return failure(new IllegalArgumentException("cargoClass is null"));

        Query query = em.createQuery(String.format("from %s where left_ < %s and right_ > %s and level_ = %s",
                cargoClass.getSimpleName(), child.getLeft_(), child.getRight_(), child.getLevel_() - 1));

        return trying(() -> (T) query.getSingleResult()).<Try<Maybe<T>>>match(
                t -> t instanceof NoResultException ? success(nothing()) : failure(t), s -> success(just(s)));
    }

    /**
     * 获取所有祖先节点
     * 
     * @param distantFirst 若为true，则祖父节点排在前面，否则父节点排在前面
     */
    @SuppressWarnings("unchecked")
    public <T extends LRTreeNode<T>> Try<Maybe<List<T>>> getAncestors(Class<T> cargoClass, T child,
            boolean distantFirst) {
        if (child == null)
            return failure(new IllegalArgumentException("child is null"));
        if (cargoClass == null)
            return failure(new IllegalArgumentException("cargoClass is null"));

        Query query = em.createQuery(String.format("from %s where left_ < %s and right_ > %s order by left_ %s",
                cargoClass.getSimpleName(), child.getLeft_(), child.getRight_(), distantFirst ? "asc" : "desc"));

        return trying(() -> (List<T>) query.getResultList()).<Try<Maybe<List<T>>>>match(t -> failure(t),
                s -> s == null || s.size() == 0 ? success(nothing()) : success(just(s)));
    }

    /**
     * 获取所有子节点
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends LRTreeNode<T>> Try<Maybe<List<T>>> getChildren(Class<T> cargoClass, T parent) {
        if (parent == null)
            return failure(new IllegalArgumentException("parent is null"));
        if (cargoClass == null)
            return failure(new IllegalArgumentException("cargoClass is null"));

        Query query = em.createQuery(String.format("from %s where left_ between %s and %s order by left_ asc",
                cargoClass.getSimpleName(), parent.getLeft_(), parent.getRight_()));

        return trying(() -> (List<T>) query.getResultList()).<Try<Maybe<List<T>>>>match(t -> failure(t),
                s -> s == null || s.size() == 0 ? success(nothing()) : success(just(s)));

    }

    /**
     * 获取所有直属子节点
     * 
     * @param <T>
     * @param cargoClass
     * @param parent
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends LRTreeNode<T>> Try<Maybe<List<T>>> getDirectChildren(Class<T> cargoClass, T parent) {
        if (parent == null)
            return failure(new IllegalArgumentException("parent is null"));
        if (cargoClass == null)
            return failure(new IllegalArgumentException("cargoClass is null"));

        Query query = em
                .createQuery(String.format("from %s where left_ between %s and %s and level_ = %s order by left_ asc",
                        cargoClass.getSimpleName(), parent.getLeft_(), parent.getRight_(), parent.getLevel_() + 1));

        return trying(() -> (List<T>) query.getResultList()).<Try<Maybe<List<T>>>>match(t -> failure(t),
                s -> s == null || s.size() == 0 ? success(nothing()) : success(just(s)));
    }

    /**
     * 获取子节点数量
     * 
     * @param <T>
     * @param parent
     * @return
     */
    public <T extends LRTreeNode<T>> Try<Integer> getChildrenNum(T parent) {
        if (parent == null)
            return failure(new IllegalArgumentException("parent is null"));

        return success((parent.getLeft_() - parent.getRight_() - 1) / 2);
    }

    /**
     * 获取直属子节点数量
     * 
     * @param <T>
     * @param parent
     * @return
     */
    public <T extends LRTreeNode<T>> Try<Integer> getDirectChildrenNum(Class<T> cargoClass, T parent) {
        if (parent == null)
            return failure(new IllegalArgumentException("parent is null"));
        if (cargoClass == null)
            return failure(new IllegalArgumentException("cargoClass is null"));

        Query query = em.createQuery(
                String.format("select count(*) from %s e where e.left_ between %s and %s and e.level_ = %s",
                        cargoClass.getSimpleName(), parent.getLeft_(), parent.getRight_(), parent.getLevel_() + 1));

        return trying(() -> (Integer) query.getSingleResult());
    }

    /**
     * 获取所有兄弟节点
     */
    @SuppressWarnings("unchecked")
    public <T extends LRTreeNode<T>> Try<Maybe<List<T>>> getSlibings(Class<T> cargoClass, T me) {
        if (me == null)
            return failure(new IllegalArgumentException("me is null"));
        if (cargoClass == null)
            return failure(new IllegalArgumentException("cargoClass is null"));

        return getParent(cargoClass, me)
                .flatMap(maybeParent -> maybeParent.match(unit -> success(nothing()), parent -> {
                    Query query = em.createQuery(String.format(
                            "from %s where left_ between %s and %s and level_ = %s and left_ <> %s and right_ <> %s order by left_ asc",
                            parent.getLeft_(), parent.getRight_(), me.getLevel_(), me.getLeft_(), me.getRight_()));
                    return trying(() -> (List<T>) query.getResultList()).<Try<Maybe<List<T>>>>match(t -> failure(t),
                            s -> s == null || s.size() == 0 ? success(nothing()) : success(just(s)));
                }));

    }

    /**
     * 获取同一层级的所有其他节点
     * 
     * @param <T>
     * @param cargoClass
     * @param me
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends LRTreeNode<T>> Try<Maybe<List<T>>> getSlibingsAndCousins(Class<T> cargoClass, T me) {
        if (me == null)
            return failure(new IllegalArgumentException("me is null"));
        if (cargoClass == null)
            return failure(new IllegalArgumentException("cargoClass is null"));

        Query query = em.createQuery(
                String.format("from %s where left_ <> %s and right_ <> %s and level_ = %s order by left_ asc",
                        cargoClass.getSimpleName(), me.getLeft_(), me.getRight_(), me.getLevel_()));

        return trying(() -> (List<T>) query.getResultList()).<Try<Maybe<List<T>>>>match(t -> failure(t),
                s -> s == null || s.size() == 0 ? success(nothing()) : success(just(s)));
    }



    /**
     * 获取所有叶子节点
     * @param <T>
     * @param cargoClass
     * @return
     */
    public <T extends LRTreeNode<T>> Try<Maybe<List<T>>> getAllLeaves(Class<T> cargoClass) {
        // TODO
        return null;
    }


    /**
     * 获取指定层级上的所有节点
     * @param <T>
     * @param cargoClass
     * @param level
     * @param isReverse 是否倒数
     * @return
     */
    public <T extends LRTreeNode<T>> Try<Maybe<List<T>>> getAllNodeAtLevel(Class<T> cargoClass, int level, boolean isReverse) {
        // TODO
        return null;
    }
    


    /**
     * 获取树的深度
     * @param <T>
     * @param cargoClass
     * @return
     */
    public <T extends LRTreeNode<T>> Try<Integer> getDepth(Class<T> cargoClass) {
        // TODO
        return null;
    }




    /**
     * 计算自身所处的层级，根节点为1
     * @param <T>
     * @param cargoClass
     * @param me
     * @return
     */
    private <T extends LRTreeNode<T>> Try<Integer> calculateLevel(Class<T> cargoClass, T me) {
        if (me == null)
            return failure(new IllegalArgumentException("me is null"));
        if (cargoClass == null)
            return failure(new IllegalArgumentException("cargoClass is null"));

        Query query = em.createQuery(String.format("select count(*) from %s e where e.left_ <= %s and e.right_ >= %s",
                cargoClass.getSimpleName(), me.getLeft_(), me.getRight_()));

        return trying(() -> (Integer) query.getSingleResult());
    }

    
    public <T extends LRTreeNode<T>> Try<Unit> insert(Class<T> cargoClass, T parent, T node) {
        if (node == null)
            return failure(new IllegalArgumentException("node is null"));
        if (cargoClass == null)
            return failure(new IllegalArgumentException("cargoClass is null"));

        return trying(() -> {
            TransactionStatus transaction = transactionManager.getTransaction(null);


            if (parent != null) {
                Query query = em.createQuery(String.format("update %s e set e.left_ = e.left_ + 2 where e.left_ >= %s",
                        cargoClass.getSimpleName(), parent.getRight_()));
                query.executeUpdate();
                query = em.createQuery(String.format("update %s e set e.right_ = e.right_ + 2 where e.right_ >= %s",
                        cargoClass.getSimpleName(), parent.getRight_()));
                query.executeUpdate();
            }

            JpaRepository<T, ?> cargoRepository = applicationContext.getBean(node.getCargoRepositoryClass());
            node.setLeft_(parent == null ? 1 : parent.getRight_());
            node.setRight_(parent == null ? 2 : parent.getRight_() + 1);
            node.setLevel_(parent == null ? 1 : parent.getLevel_() + 1);
            cargoRepository.save(node);
            transactionManager.commit(transaction);
        }).match(
            t -> {
                TransactionStatus transaction = transactionManager.getTransaction(null);
                if (transaction != null && !transaction.isCompleted())
                    em.getTransaction().rollback(); 
                return failure(t);
            },
            __ -> success(__)
        );
    }

    
    public <T extends LRTreeNode<T>> Try<Unit> delete(Class<T> cargoClass, T node) {
        // TODO
        return null;
    }



    @Getter
    @Setter
    @MappedSuperclass
    public static abstract class LRTreeNode<T extends LRTreeNode<T>> {

        @Column(name = "left_")
        private int left_;

        @Column(name = "right_")
        private int right_;

        @Column(name = "level_")
        private int level_;

        @Transient
        private List<T> children;

        protected abstract Class<? extends JpaRepository<T, ?>> getCargoRepositoryClass();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


  




}