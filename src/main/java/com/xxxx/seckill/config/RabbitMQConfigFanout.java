// package com.xxxx.seckill.config;
//
// import com.rabbitmq.client.AMQP;
// import org.springframework.amqp.core.Binding;
// import org.springframework.amqp.core.BindingBuilder;
// import org.springframework.amqp.core.FanoutExchange;
// import org.springframework.amqp.core.Queue;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// import javax.management.Query;
//
// /**
//  * rabbitMQ配置类
//  * Author: asus
//  * Date: 2022/12/15 10:22
//  */
// @Configuration
// public class RabbitMQConfigFanout {
//
//     private static final String QUEUE01 = "queue_fanout01";
//     private static final String QUEUE02 = "queue_fanout02";
//     private static final String EXCHANGE = "queueExchange";//交换机（绑定两个队列）
//
//     @Bean
//     public Queue queue() {
//         //返回队列，默认开启持久化
//         return new Queue("queue", true);
//
//     }
//
//     @Bean
//     public Queue queue01() {
//         return new Queue(QUEUE01);
//     }
//
//     @Bean
//     public Queue queue02() {
//         return new Queue(QUEUE02);
//     }
//
//     //交换机
//     @Bean
//     public FanoutExchange fanoutExchange() {
//         return new FanoutExchange(EXCHANGE);
//     }
//
//     @Bean
//     public Binding binding01() {
//         //交换机绑定queue01队列
//         return BindingBuilder.bind(queue01()).to(fanoutExchange());
//     }
//
//     @Bean
//     public Binding binding02() {
//         //交换机绑定queue02队列
//         return BindingBuilder.bind(queue02()).to(fanoutExchange());
//     }
//
//
// }
