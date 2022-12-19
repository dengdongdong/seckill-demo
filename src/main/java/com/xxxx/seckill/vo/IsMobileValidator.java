package com.xxxx.seckill.vo;

import com.xxxx.seckill.utils.ValidatorUtil;
import com.xxxx.seckill.validator.IsMobile;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Author: asus
 * Date: 2022/10/22 19:00
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    //初始化FALSE
    private boolean required = false;

    @Override
    public boolean isValid(String mobile, ConstraintValidatorContext constraintValidatorContext) {

        if (required) {
            return ValidatorUtil.isMobile(mobile);
        } else { //非必填
            if (StringUtils.isBlank(mobile)) {
                return true;
            } else {
                return ValidatorUtil.isMobile(mobile);
            }
        }
    }

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }
}
