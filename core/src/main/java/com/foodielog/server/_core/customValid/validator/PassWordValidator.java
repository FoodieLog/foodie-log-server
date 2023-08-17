package com.foodielog.server._core.customValid.validator;

import com.foodielog.server._core.customValid.valid.ValidPassWord;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PassWordValidator implements ConstraintValidator<ValidPassWord, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && isValidString(value);
    }

    private boolean isValidString(String value) {
        // 영문 대소문자, 숫자, 특수문자는 각각 최소한 하나씩 포함, 총 8~16자
        // 사용 가능한 특수문자 ~ ․! @ # $ % ^ & * ( ) _ - + =  [ ] [ ] | \ ; :‘ “ < > , . ? /
        String regex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~․!@#\\$%^&*()_\\-+=\\[\\]{}|\\\\;:‘“<>,.?/])[a-zA-Z\\d~․!@#\\$%^&*()_\\-+=\\[\\]{}|\\\\;:‘“<>,.?/]{8,16}$";
        return value.matches(regex);
    }
}

