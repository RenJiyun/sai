package com.eggip.sai.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

public class ExampleException extends AbstractThrowableProblem {
    
    private static final long serialVersionUID = 1551267296750703333L;

    public ExampleException() {
        super(Problem.DEFAULT_TYPE, "example exception", Status.BAD_REQUEST);
    }
}