package com.xxxx.seckill.controller;


import com.xxxx.seckill.pojo.Order;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.utils.Assert;
import com.xxxx.seckill.vo.OrderDetailVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 一叶知秋
 * @since 2022-11-04
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService iOrderService;
    /**
     * 订单详情
     *
     * @return
     */
    @RequestMapping(value = "/orderDetail",method = RequestMethod.GET)
    @ResponseBody
    public RespBean oderDetail(User user,Long orderId) {
        Assert.notNull(user, RespBeanEnum.SESSION_ERROR);
        OrderDetailVo detail = iOrderService.detail(orderId);
        detail.setUser(user);
        return RespBean.success(detail);
    }
}
