package com.xxxx.seckill.rabbitmq;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.sun.org.apache.regexp.internal.RE;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.utils.JsonUtil;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import com.xxxx.seckill.vo.SeckillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Author: asus
 * Date: 2022/12/15 10:34
 */
@Service
@Slf4j
public class MQReceiver {

    // @RabbitListener(queues = "queue")
    // public void receive(Object msg) {
    //     log.info("接收消息:" + msg);
    // }
    //
    // @RabbitListener(queues = "queue_fanout01")
    // public void receive01(Object msg) {
    //     log.info("QUEUE01接收消息。" + msg);
    // }
    //
    // @RabbitListener(queues = "queue_fanout02")
    // public void receive02(Object msg) {
    //     log.info("QUEUE02接收消息。" + msg);
    // }
    //
    // // ================================================
    // @RabbitListener(queues = "queue_direct01")
    // public void receive03(Object msg) {
    //     log.info("QUEUE01接收消息。" + msg);
    // }
    //
    // @RabbitListener(queues = "queue_direct02")
    // public void receive04(Object msg) {
    //     log.info("QUEUE02接收消息。" + msg);
    // }
    //
    // //topic模式
    // @RabbitListener(queues = "queue_topic01")
    // public void receive05(Object msg) {
    //     log.info("QUEUE01接收消息：" + msg);
    // }
    //
    // //topic模式
    // @RabbitListener(queues = "queue_topic02")
    // public void receive06(Object msg) {
    //     log.info("Queue02接收消息：" + msg);
    // }
    //
    // //header 模式
    // @RabbitListener(queues = "queue_header01")
    // public void receive07(Message message) {
    //     log.info("QUEUE01 接收Message对象：" + message);
    //     log.info("QUEUE01 接收消息：" + new String(message.getBody()));
    // }
    //
    // //header 模式
    // @RabbitListener(queues = "queue_header02")
    // public void receive08(Message message) {
    //     log.info("QUEUE02 接收Message对象：" + message);
    //     log.info("QUEUE02 接收消息：" + new String(message.getBody()));
    // }
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        log.info("接收消息:" + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        User user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodsId();
        //判断库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount() < 1) {
            return;
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (Objects.nonNull(seckillOrder)) {
            return;
        }
        //下单
        orderService.secKill(user, goodsVo);
    }
}
