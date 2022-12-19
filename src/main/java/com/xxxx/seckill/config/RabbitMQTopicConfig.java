package com.xxxx.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * dec:rabbitMQ配置类---topic模式
 * Author: asus
 * Date: 2022/12/16 10:05
 */
@Configuration
public class RabbitMQTopicConfig {

    // private static final String QUEUE01 = "queue_topic01";
    // private static final String QUEUE02 = "queue_topic02";
    // private static final String EXCHANGE = "topicExchange";
    // private static final String ROUTINGKEY01 = "#.queue.#";
    // private static final String ROUTINGKEY02 = "*.queue.#";

    // @Bean
    // public Queue queue01() {
    //     return new Queue(QUEUE01);
    // }
    //
    // @Bean
    // public Queue queue02() {
    //     return new Queue(QUEUE02);
    // }
    //
    // //交换机
    // @Bean
    // public TopicExchange topicExchange() {
    //     return new TopicExchange(EXCHANGE);
    // }
    //
    // //交换机绑定队列queue01并使用ROUTINGKEY01的主题
    // @Bean
    // public Binding binding01() {
    //     return BindingBuilder.bind(queue01()).to(topicExchange()).with(ROUTINGKEY01);
    // }
    //
    // //交换机绑定队列queue02并使用ROUTINGKEY02的主题
    // @Bean
    // public Binding binding02() {
    //     return BindingBuilder.bind(queue02()).to(topicExchange()).with(ROUTINGKEY02);
    // }

    private static final String QUEUE = "seckillQueue";
    private static final String EXCHANGE = "seckillExchange";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.#");
    }


}
