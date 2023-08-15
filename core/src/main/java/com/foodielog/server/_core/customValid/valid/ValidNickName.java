package com.foodielog.server._core.customValid.valid;

import com.foodielog.server._core.customValid.validator.NickNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NickNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNickName {
    String message() default "올바른 형식의 닉네임 이어야 합니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}