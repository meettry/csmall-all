package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.pojo.product.vo.SpuStandardVO;
import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuDetailSimpleVO;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuVO;
import cn.tedu.mall.product.service.seckill.IForSeckillSpuService;
import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.service.ISeckillSpuService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SeckillSpuServiceImpl implements ISeckillSpuService {

    // 查询秒杀spu表的数据
    @Autowired
    private SeckillSpuMapper seckillSpuMapper;
    // 秒杀spu表中没有商品详细介绍,需要根据spuid借助Dubbo查询product模块
    // pms数据库中的spu表,获得商品信息
    @DubboReference
    private IForSeckillSpuService dubboSeckillSpuService;

    @Override
    public JsonPage<SeckillSpuVO> listSeckillSpus(Integer page, Integer pageSize) {
        // 分页查询秒杀表中spu信息
        PageHelper.startPage(page,pageSize);
        List<SeckillSpu> seckillSpus=seckillSpuMapper.findSeckillSpus();
        // 我们返回给页面的,应该是包含商品详细信息的对象,不能只是SeckillSpu中的信息
        // 业务逻辑层返回值泛型类型SeckillSpuVO中包含秒杀信息和商品详细信息,返回它的集合可以满足查询需要
        List<SeckillSpuVO> seckillSpuVOs=new ArrayList<>();
        // 循环遍历秒杀列表,根据秒杀商品列表中元素的spuId查询spu详情
        for(SeckillSpu seckillSpu: seckillSpus){
            // 获得SpuId
            Long spuId=seckillSpu.getSpuId();
            // dubbo调用查询商品详情
            SpuStandardVO spuStandardVO=dubboSeckillSpuService.getSpuById(spuId);
        }

        return null;
    }

    @Override
    public SeckillSpuVO getSeckillSpu(Long spuId) {
        return null;
    }

    @Override
    public SeckillSpuDetailSimpleVO getSeckillSpuDetail(Long spuId) {
        return null;
    }
}
