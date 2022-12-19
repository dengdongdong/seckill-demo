package com.xxxx.seckill.vo;

import com.xxxx.seckill.pojo.Goods;
import com.xxxx.seckill.pojo.Order;
import com.xxxx.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: asus
 * Date: 2022/12/14 10:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {
    private Order order;
    private Goods goods;
    private User user;

}
