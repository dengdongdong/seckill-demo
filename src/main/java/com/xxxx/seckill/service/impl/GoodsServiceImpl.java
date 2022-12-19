package com.xxxx.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxxx.seckill.mapper.GoodsMapper;
import com.xxxx.seckill.pojo.Goods;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 一叶知秋
 * @since 2022-11-04
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 获取商品列表
     * @return
     */
    @Override
    public List<GoodsVo> findGoodsVo() {
        List<GoodsVo> goodsVo = goodsMapper.findGoodsVo();
        return goodsVo;
    }

    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        GoodsVo goodsVo = goodsMapper.findGoodsVoByGoodsId(goodsId);
        return goodsVo;
    }
}
