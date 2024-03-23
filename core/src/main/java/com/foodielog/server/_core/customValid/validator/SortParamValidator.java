package com.foodielog.server._core.customValid.validator;

import com.foodielog.server._core.customValid.valid.ValidSortParam;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SortParamValidator implements ConstraintValidator<ValidSortParam, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equalsIgnoreCase("latest") || value.equalsIgnoreCase("popular");
    }
}