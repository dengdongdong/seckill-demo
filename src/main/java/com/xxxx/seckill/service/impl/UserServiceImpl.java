package com.xxxx.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxxx.seckill.mapper.UserMapper;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IUserService;
import com.xxxx.seckill.utils.Assert;
import com.xxxx.seckill.utils.CookieUtil;
import com.xxxx.seckill.utils.MD5Util;
import com.xxxx.seckill.utils.UUIDUtil;
import com.xxxx.seckill.vo.LoginVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 一叶知秋
 * @since 2022-10-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 登录
     *
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        System.out.println("loginVo" + loginVo);
        // if (StringUtils.isAnyBlank(mobile, password)) {
        //     return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        // }
        // //手机号码校验
        // if(!ValidatorUtil.isMobile(mobile)){
        //     return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        // }
        User user = userMapper.selectById(mobile);
        Assert.notNull(user, RespBeanEnum.LOGIN_ERROR);
        // if (Objects.isNull(user)) //等价
        //     throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        //     // return RespBean.error(RespBeanEnum.ERROR);
        // }
        Assert.isTrue(!MD5Util.fromPassToDbPass(password, user.getSlat()).equalsIgnoreCase(user.getPassword()), RespBeanEnum.LOGIN_ERROR);
        // if(!MD5Util.fromPassToDbPass(password, user.getSlat()).equalsIgnoreCase(user.getPassword())){//等价于
        //     throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        //         // return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        // }

        //生成cookie
        String ticket = UUIDUtil.uuid();
        //将用户信息存入redis
        redisTemplate.opsForValue().set("user:" + ticket, user);
        //将用户信息存入session
        // request.getSession().setAttribute(ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userTicket)) {
            return null;
        }
        //解决分布式缓存不一致的问题，使用单独集中式缓存。
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (Objects.nonNull(user)) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    /**
     * 变更新密码
     *
     * @param userTicket
     * @param password
     * @return
     */
    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        Assert.notNull(user, RespBeanEnum.MOBILE_NOT_EXIST);
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSlat()));
        int result = userMapper.updateById(user);
        if (result == 1) {
            redisTemplate.delete("user" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
