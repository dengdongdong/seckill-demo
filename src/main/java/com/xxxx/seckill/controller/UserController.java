package com.xxxx.seckill.controller;


import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.rabbitmq.MQSender;
import com.xxxx.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 一叶知秋
 * @since 2022-10-22
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private MQSender mqSender;

    /**
     * 用户信息（测试）
     *
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        log.info("info user{};{}", user.getId(), user.getNickname());
        return RespBean.success(user);
    }

    /**
     * 测试发送rabbitMQ的消息
     */
    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        // mqSender.sender("Hello");
    }

    /**
     * dec:fanout模式
     */
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq01() {
        // mqSender.sender("Hello");
    }

    /**
     * dec:direct模式
     */
    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void mq02() {
        // mqSender.sender01("Hello,Red");
    }

    /**
     * dec:direct 模式
     */
    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void mq03() {
        // mqSender.sender02("Hello,Green");
    }

    /**
     * dec:topic 模式
     */
    @RequestMapping("/mq/topic01")
    @ResponseBody
    public void mq04() {
        // mqSender.sender03("Hello,Red");
    }


    /**
     * dec:topic 模式
     */
    @RequestMapping("/mq/topic02")
    @ResponseBody
    public void mq05() {
        // mqSender.sender04("Hello,Green");
    }

    /**
     * dec:header 模式
     */
    @RequestMapping("/mq/header01")
    @ResponseBody
    public void mq06() {
        // mqSender.sender05("Hello,header01");
    }

    /**
     * dec:header 模式
     */
    @RequestMapping("/mq/header02")
    @ResponseBody
    public void mq07() {
        // mqSender.sender06("Hello,header02");
    }

}
