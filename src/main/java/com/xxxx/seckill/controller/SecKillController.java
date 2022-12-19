package com.xxxx.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxxx.seckill.pojo.Order;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.rabbitmq.MQSender;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.service.ISeckillOrderService;
import com.xxxx.seckill.utils.Assert;
import com.xxxx.seckill.utils.JsonUtil;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import com.xxxx.seckill.vo.SeckillMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author: asus
 * Date: 2022/11/4 18:51
 */
@Controller
@RequestMapping("/secKill")
public class SecKillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> redisScript;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    /**
     * 秒杀
     * windows优化之前：QPS： 962
     * windows优化之前：QPS： 167
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSecKill2")
    public String doSecKill2(Model model, User user, Long goodsId) {
        if (Objects.isNull(user)) {
            return "login";
        }
        model.addAttribute("user", user);
        //查询秒杀商品库存
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFil";
        }
        model.addAttribute("goods", goods);
        QueryWrapper<SeckillOrder> queryWrapper = new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId())
                .eq("goods_id", goods.getId());
        //查询秒杀订单库;看是否重复抢购行为
        SeckillOrder seckillOrder = seckillOrderService.getOne(queryWrapper);
        if (Objects.nonNull(seckillOrder)) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFil";
        }
        // 下单抢购
        Order order = orderService.secKill(user, goods);
        model.addAttribute("order", order);
        return "orderDetail";
    }

    /**
     * 秒静态化
     * windows优化之前：QPS： 962
     * windows优化之前：QPS： 167
     * windows优化之后：QPS： 1011.4632501685772
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/doSecKill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(Model model, User user, Long goodsId) {
        if (Objects.isNull(user)) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        // 下单实现异步处理//判断是否重复抢购
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (Objects.nonNull(seckillOrder)) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //内存标记，减少Redis的访问
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存（decrement和increment具有原子性操作）
        // Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        Long stock = (Long) redisTemplate.execute(redisScript,
                Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        System.out.println("stock:" + stock);
        if (stock < 0) {
            EmptyStockMap.put(goodsId, true);
            // valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 请求入队，立即返回排队中
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        //发送订单消息
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);

        // // model.addAttribute("user", user);
        //
        // //查询秒杀商品库存
        // GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        // if (goods.getStockCount() < 1) {
        //     // model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
        //     return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        // }
        // model.addAttribute("goods", goods );
        //
        // //查询秒杀订单库;看是否重复抢购行为（在高并发的情况下，一个人可能抢到两家商品：解决办法：在数据库中使用用户id和商品id组成联合唯一性索引。）
        // // QueryWrapper<SeckillOrder> queryWrapper = new QueryWrapper<SeckillOrder>()
        // //         .eq("user_id", user.getId())
        // //         .eq("goods_id", goods.getId());
        // // SeckillOrder seckillOrder = seckillOrderService.getOne(queryWrapper);
        // //缓存中获取用户的秒杀订单
        // SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getId());
        // if (Objects.nonNull(seckillOrder)) {
        //     // model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
        //     return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        // }
        //
        // // 下单抢购
        // Order order = orderService.secKill(user, goods);
        // // model.addAttribute("order", order);
        // return RespBean.success(order);
    }

    /**
     * desc:
     *
     * @param user
     * @param goodsId
     * @return orderId：成功；-1：失败；0：排队中。
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        Assert.notNull(user, RespBeanEnum.SESSION_ERROR);
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * desc:把商品库存数量加载到redis中
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> voList = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(voList)) {
            return;
        }
        voList.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });

    }
}
