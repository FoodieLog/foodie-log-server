package com.foodielog.server._core.customValid.validator;

import com.foodielog.server._core.customValid.valid.ValidNickName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NickNameValidator implements ConstraintValidator<ValidNickName, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.length() >= 2 && isValidString(value);
    }

    private boolean isValidString(String value) {
        // 첫 글자는 한글 또는 영문, 뒤로는 한글, 영문, 숫자, 밑줄(_)만 포함 하는 2글자 이상 되는지 검사
        String regex = "^[가-힣a-zA-Z][가-힣a-zA-Z0-9_]*$";
        return value.matches(regex);
    }
}