package com.xxxx.seckill.config;

import com.xxxx.seckill.pojo.User;

/**
 * Author: asus
 * Date: 2022/12/20 13:10
 */
public class UserContext {
    /*
     * 每个线程绑定自己的值
     *
     *
     *
     * */
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user) {
        userHolder.set(user);
    }

    public static User getUser() {
        return userHolder.get();
    }

}
