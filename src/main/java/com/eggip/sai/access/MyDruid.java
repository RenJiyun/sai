package com.eggip.sai.access;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource.JoinType;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitorAdapter;
import com.alibaba.druid.util.JdbcConstants;

public class MyDruid {
    public static void main(String[] args) {
        final String dbType = JdbcConstants.SQL_SERVER;
        String sql = "select * from a left join b on a.id = b.id left join d on a.id = d.id";
        SQLSelectStatement select = (SQLSelectStatement) SQLUtils.parseSingleStatement(sql, dbType);
        select.accept(new SQLServerASTVisitorAdapter() {


            /* @Override
            public boolean visit(SQLExprTableSource x) {
                if (x.getExpr() instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) x.getExpr();
                    System.out.println(sqlIdentifierExpr.getName());
                }
                return true;
            } */

            @Override
            public boolean visit(SQLJoinTableSource x) {
                SQLObject parent = x.getParent(); 
                SQLJoinTableSource join = new SQLJoinTableSource();
                SQLExprTableSource joinedTable = new SQLExprTableSource();
                joinedTable.setExpr("c");
                join.setRight(joinedTable);
                join.setLeft(x);
                join.setJoinType(JoinType.LEFT_OUTER_JOIN);
                join.setParent(parent);
                if (parent instanceof SQLSelectQueryBlock) {
                    ((SQLSelectQueryBlock) parent).setFrom(join);
                }
                
                return true;
            }

        });

        System.out.println(select.toString());


    }
}