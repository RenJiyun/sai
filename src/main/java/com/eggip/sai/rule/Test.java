package com.eggip.sai.rule;


import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;

public class Test {
    public static void main(String[] args) throws MalformedGoalException, NoSolutionException {
        Prolog engine = new Prolog();
        SolveInfo info = engine.solve("append([1], [2, 3], X).");
        System.out.println(info.getSolution());
    }
}
