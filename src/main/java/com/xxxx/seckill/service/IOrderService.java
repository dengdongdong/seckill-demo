package com.xxxx.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxxx.seckill.pojo.Order;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.OrderDetailVo;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 一叶知秋
 * @since 2022-11-04
 */
public interface IOrderService extends IService<Order> {

    Order secKill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);

    String createSeckillPath(User user, Long goodsId);

    /**
     * 校验秒杀地址
     *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    boolean checkPath(User user, Long goodsId, String path);
}
