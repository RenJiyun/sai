package com.eggip.sai.web.rest.errors;

import static com.eggip.sai.AppConstants.PROD;

import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.violations.ConstraintViolationProblem;


@ControllerAdvice
public class ExceptionTranslator implements ProblemHandling {

    private static final String MESSAGE_KEY = "message";
    private static final String PATH_KEY = "path";
    private static final String VIOLATIONS_KEY = "violations";
    private static final String FIELD_ERRORS_KEY = "fieldErrors";


    @Autowired
    private Environment env;


    // 非生产环境均将打印错误栈帧
    @Override
    public boolean isCausalChainsEnabled() {
        if (env.getActiveProfiles()[0].equals(PROD)) return false;
        return true;
    }

    /**
     * @see ThrowableProblem
     * @see ConstraintViolationProblem https://opensource.zalando.com/problem/constraint-violation/
     * @see DefaultProblem
     * 在zalando/problem和在zalando/problem-spring-web/problem-violations中，
     * 对于类ThrowableProblem通过了两个默认实现
     */
    @Override
    public ResponseEntity<Problem> process(ResponseEntity<Problem> entity, NativeWebRequest request) {
        if (entity == null) {
            return entity;
        }
        Problem problem = entity.getBody();

        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return entity;
        }
        ProblemBuilder builder = Problem.builder()
                .withType(problem.getType())
                .withStatus(problem.getStatus())
                .withTitle(problem.getTitle())
                .with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI());

        if (problem instanceof ConstraintViolationProblem) {
            builder
                .with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations())
                .with(MESSAGE_KEY, "error.validation");
        } else {
            builder
                .withCause(((DefaultProblem) problem).getCause())
                .withDetail(problem.getDetail())
                .withInstance(problem.getInstance());
            problem.getParameters().forEach(builder::with);
            if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
                builder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
            }
        }
        return new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode());
    }



    @Override
    public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, NativeWebRequest request) {
        BindingResult result = ex.getBindingResult();
        Problem problem = Problem.builder()
                            .withTitle("Method argument not valid")
                            .withStatus(defaultConstraintViolationStatus())
                            .with(MESSAGE_KEY, "err.validation")
                            .with(FIELD_ERRORS_KEY, result.getFieldErrors()).build();
        return create(ex, problem, request);
    }



    // 可以针对Java中常见的异常进行定制
    @ExceptionHandler
    public ResponseEntity<Problem> handleNoSuchElementException(NoSuchElementException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
                        .withStatus(Status.NOT_FOUND).build();
        return create(ex, problem, request);
    }

}