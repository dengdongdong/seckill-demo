package com.xxxx.seckill.controller;

import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IUserService;
import com.xxxx.seckill.vo.DetailVo;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 商品列表
 * Author: asus
 * Date: 2022/10/22 23:26
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 跳转商品列表页
     * <p>
     * windows优化前：qps:1391.3/s   150000访问量
     * linux优化前：  qps:231.2/s    150000访问量
     *
     * @param model
     * @param user  用户信息
     * @return
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String toGoodsList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        // if (StringUtils.isBlank(ticket)) {
        //     return "login";
        // }
        // //获取缓存中的登录信息
        // // User user = (User) request.getAttribute(ticket);
        // //从redis中获取登录信息
        // User user = iUserService.getUserByCookie(ticket, request, response);
        //
        // if (Objects.isNull(user)) {
        //     return "login";
        // }


        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isBlank(html)) {
            return html;
        }

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        //如果缓存为空，手动渲染并存入redis返回
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //使用thyme渲染HTML页面
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (StringUtils.isNoneBlank(html)) {
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    /**
     * 获取商品详情
     *
     * @param model
     * @param user
     * @param goodsId
     * @return 页面缓存：应用场景（变更比较少）。
     */
    @RequestMapping(value = "/toDetail2/{goodsId}", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String toDetail2(Model model, User user, @PathVariable() Long goodsId, HttpServletRequest request, HttpServletResponse response) {

        ValueOperations valueOperations = redisTemplate.opsForValue();
        //redis中获取，如果不为空，直接返回页面
        String html = (String) valueOperations.get("goodsDetail" + goodsId);
        if (StringUtils.isNoneBlank(html)) {
            return html;
        }
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000.0);
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;//秒杀已经结束
            remainSeconds = -1;
        } else {
            secKillStatus = 1;//秒杀进行中
            remainSeconds = 0;
        }
        model.addAttribute("user", user);
        model.addAttribute("goods", goodsVo);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if (StringUtils.isNoneBlank(html)) {
            valueOperations.set("goodsDetail" + goodsId, html, 60, TimeUnit.SECONDS);

        }
        return html;
        // return "goodsDetail";
    }

    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(Model model, User user, @PathVariable() Long goodsId) {

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000.0);
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;//秒杀已经结束
            remainSeconds = -1;
        } else {
            secKillStatus = 1;//秒杀进行中
            remainSeconds = 0;
        }
        // model.addAttribute("user", user);
        // model.addAttribute("goods", goodsVo);
        // model.addAttribute("secKillStatus", secKillStatus);
        // model.addAttribute("remainSeconds", remainSeconds);
        // WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        // return "goodsDetail";
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);
        return RespBean.success(detailVo);
    }


}
