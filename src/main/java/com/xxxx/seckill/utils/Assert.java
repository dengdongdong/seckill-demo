package com.xxxx.seckill.utils;

import com.xxxx.seckill.exception.GlobalException;
import com.xxxx.seckill.vo.RespBeanEnum;

import java.util.Objects;

/**
 * Author: asus
 * Date: 2022/10/22 19:53
 */
public class Assert {

    private static boolean isBlank(final CharSequence cs) {

        int strLin;
        if (cs == null || (strLin = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLin; i++) {
            //
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static void nonBlank(CharSequence cs, RespBeanEnum respBeanEnum) {
        if (isBlank(cs)) {
            throw new GlobalException(respBeanEnum);
        }
    }

    public static void notNull(Object obj, RespBeanEnum respBeanEnum) {
        if (Objects.isNull(obj)) {
            throw new GlobalException(respBeanEnum);
        }
    }

    public static void isTrue(boolean expression, RespBeanEnum respBeanEnum) {
        if (expression) {
            throw new GlobalException(respBeanEnum);
        }
    }
}
