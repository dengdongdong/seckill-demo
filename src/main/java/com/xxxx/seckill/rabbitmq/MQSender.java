package com.xxxx.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Author: asus
 * Date: 2022/12/15 10:29
 */
@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // public void sender(Object msg) {
    //     log.info("发送消息:" + msg);
    //     rabbitTemplate.convertAndSend("queueExchange", "", msg);
    // }
    //
    // ////direct模式
    // public void sender01(Object msg) {
    //     log.info("发送red的消息:" + msg);
    //     rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
    // }
    //
    // //direct模式，发消息时，携带路由key；根据路由key匹配队列，接收消息
    // public void sender02(Object msg) {
    //     log.info("发送green的消息:" + msg);
    //     rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
    // }
    //
    // //topic模式
    // public void sender03(Object msg) {
    //     log.info("发送消息（queue01接收）：" + msg);
    //     rabbitTemplate.convertAndSend("topicExchange", "queue.red.message", msg);
    // }
    //
    // //topic模式
    // public void sender04(Object msg) {
    //     log.info("发送消息（被两个queue接收）：" + msg);
    //     rabbitTemplate.convertAndSend("topicExchange", "green.queue.red.message", msg);
    // }
    //
    // //header 模式
    // public void sender05(String msg) {
    //     log.info("发送消息（被两个queue接收）：" + msg);
    //     MessageProperties messageProperties = new MessageProperties();
    //     messageProperties.setHeader("color", "red");
    //     messageProperties.setHeader("speed", "fast");
    //     Message message = new Message(msg.getBytes(), messageProperties);
    //     rabbitTemplate.convertAndSend("headerExchange", "", message);
    // }
    //
    // //header 模式
    // public void sender06(String msg) {
    //     log.info("发送消息（被QUEUE01接收）：" + msg);
    //     MessageProperties messageProperties = new MessageProperties();
    //     messageProperties.setHeader("color", "red");
    //     messageProperties.setHeader("speed", "normal");
    //     Message message = new Message(msg.getBytes(), messageProperties);
    //     rabbitTemplate.convertAndSend("headerExchange", "", message);
    // }

    /**
     * 发送秒杀消息
     *
     * @param message
     */

    public void sendSeckillMessage(String message) {
        log.info("发送消息：" + message);
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.message", message);

    }




}
