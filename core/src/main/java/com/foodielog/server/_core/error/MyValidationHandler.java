package com.foodielog.server._core.error;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import com.foodielog.server._core.error.exception.Exception400;

@Aspect
@Component
public class MyValidationHandler {
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping() {
    }

    @Before("postMapping() || putMapping()")
    public void validationAdvice(JoinPoint jp) {
        Object[] args = jp.getArgs();
        for (Object arg : args) {
            if (arg instanceof Errors) {
                Errors errors = (Errors) arg;

                if (errors.hasErrors()) {
                    throw new Exception400(
                            errors.getFieldErrors().get(0).getField(),
                            errors.getFieldErrors().get(0).getDefaultMessage()
                    );
                }
            }
        }
    }
}