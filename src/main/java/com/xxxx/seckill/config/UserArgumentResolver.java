package com.xxxx.seckill.config;

import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IUserService;
import com.xxxx.seckill.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: asus
 * Date: 2022/11/4 11:20
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private IUserService userService;

    //条件判断:返回true则执行下边的方法，返回false则不执行

    /**
     * 该方法用于判断Controller中方法参数中是否有符合条件的参数：
     * 有则进入下一个方法resolveArgument；
     * 没有则跳过不做处理
     *
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 获取传入参数的类型
        Class<?> clazz = parameter.getParameterType();
        // 如果参数类型有为User类的则符合,进入resolveArgument方法
        return User.class == clazz;
    }

    /**
     * 该方法在上一个方法同通过之后调用：在这里可以进行处理，根据情况返回对象——返回的对象将被赋值到Controller的方法的参数中
     *
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return UserContext.getUser();
    }
}
