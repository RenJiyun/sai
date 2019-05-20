# 函数
1. 输出完全由输入决定，同样的输入得到同样的输出
2. 没有副作用，副作用的指的是对外界的影响，比如改变全局状态，打印，抛出异常等
```
// 举一些例子

// 函数
public int fun(int a) {
    return a + 1;
}

// 不是函数，因为打印了东西
public int fun(String s) {
    System.out.println(s);
    return s.length;
}


// 不是函数，因为会抛出异常
public int fun(int a) {
    if (a == 1) {
        return a + 1;
    } else {
        throw new Exception();
    }
}

```

#### 定义记号
```
public int fun(String s) {
    if (s == null) return 0;
    return a.length;
}
```
将上述函数记作: String -> int
一般地，我们将A -> B看成是A到B函数，翻译成Java代码为：
```
public B fun(A a) {
    // do something and return a B
    return new B();
}
```

## 函数的可组合性
假设现在有两个函数
* A -> B
* B -> C
将两个函数组合，就可以得到新的函数A -> C，用代码表示如下：
```
public B fun1(A a) {
    return new B();
}

public C fun2(B b) {
    return new C();
}

// 组合一下
public C fun3(A a) {
    return fun2(fun1(a));
}
```
实现这个组合并不难，但是不够优雅，这里强调的是函数的可组合性，而这个性质是函数固有的，因此把代码重构一下，把这个组合的特性直接用代码表示出来：
```
// 现在讨论的都是一元函数，就从它开始
// 用泛型表示该函数的输入类型是A，输出类型是B，A -> B
@FunctionalInterface
public interface Fn1<A, B> {
    public B apply(A a);

    default public Fn1<A, C> compose(Fn1<B, C> fn) {
        return a -> fn.apply(apply(a));
    }
}
```
有了这样一个接口后，上述的fun3就可以重新写为：
```
public B fun1(A a) {
    return new B();
}

public C fun2(B b) {
    return new C();
}

public C fun3(A a) {
    return fun1.compose(fun2).apply(a);
}
```
当然上述代码还不能工作，因为fun1和fun2本身并不算Fn1类型，这里只是为了说明组合性质的用法。

#### 函数式编程和面向对象编程的区别：
面向对象编程在设计上是自顶向下的，先设计一个大的模块，模块分解成各个组件，组件再由对象构成，大的对象还会继续往下分解成小的对象。而函数式编程是自底向上的，先设计简单的函数，然后通过函数之间的组合形成更大的函数，最终的函数就能实现复杂的功能。因为，函数的可组合性质对于函数式编程来讲十分重要。


---
***到此为止，我们拥有的东西：***
```
@FunctionalInterface
public interface Fn1<A, B> {
    public B apply(A a);

    default public Fn1<A, C> compose(Fn1<B, C> fn) {
        return a -> fn.apply(apply(a));
    }
}
```



## 一群函数
Fn1<A, B>是用泛型表示的，我们来给实例化一下：
```
Fn1<String, Integer> fn1 = (s) -> s == null ? 0 : s.length;
Fn1<String, Integer> fn2 = (s) -> s == null ? -1 : s.indexOf("a");
```
在上面的代码中，定义了两个函数，但是这两个函数的类型都是：String -> Integer，*在数学上，会将String称为函数的定义域，int称为函数的值域*。这里也可以看到，其实从String到Integer的函数有无数个，或者说我们可以给接口```Fn1<String, Integer>```提供无数个实现，也就是一群函数。

## 两群函数
既然从```Fn1<String, Integer>```衍生出一群函数，那同样的，从```Fn1<Integer, Boolean>```也可以实例化出另一群函数。有趣的是，我们可以通过组合这两群函数形成新的一群函数，而这群函数的类型是```Fn1<String, Boolean>```，用伪代码可以表示如下：
```
Fn1<String, Boolean> = Fn1<String, Integer>.compose(Fn1<Integer, Boolean>) 
```

---

# 换个角度，新世界
上面已经介绍了函数的组合性质，这里我们从另一个角度或者说另一个概念来看待组合这个性质。这里还是将几个主角重新登场一下：
```
public B fun1(A a) {
    return new B();
}

public C fun2(B b) {
    return new C();
}

public C fun3(A a) {
    return fun1.compose(fun2).apply(a);
}
```
*我们假定所有函数都来自于```Fn1<A, B>```结构*


之前我们都是在讲组合，现在换一个词，叫**映射**。函数`fun1`和`fun2`组合后变成了`fun3`，如果换成映射的语言，也可以说成是：
> 函数`fun1`在函数`fun2`的**映射**下变成了`fun3`


