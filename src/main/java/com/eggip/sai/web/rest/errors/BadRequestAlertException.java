package com.eggip.sai.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;

public class BadRequestAlertException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 5143239792137348174L;
    private final String entityName;
    private final String errorKey;

    public BadRequestAlertException(String title, String entityName, String errorKey) {
        this(Problem.DEFAULT_TYPE, title, entityName, errorKey);
    }

    public BadRequestAlertException(URI type, String title, String entityName, String errorKey) {
        super(type, title, Status.BAD_REQUEST);
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

}
