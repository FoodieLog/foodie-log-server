package com.foodielog.server._core.customValid.valid;

import com.foodielog.server._core.customValid.validator.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "비밀번호 형식에 맞지 않습니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}