package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.pojo.product.vo.SkuStandardVO;
import cn.tedu.mall.pojo.seckill.model.SeckillSku;
import cn.tedu.mall.pojo.seckill.vo.SeckillSkuVO;
import cn.tedu.mall.product.service.seckill.IForSeckillSkuService;
import cn.tedu.mall.seckill.mapper.SeckillSkuMapper;
import cn.tedu.mall.seckill.service.ISeckillSkuService;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SeckillSkuServiceImpl implements ISeckillSkuService {
    @Autowired
    private SeckillSkuMapper skuMapper;
    // Dubbo查询sku详细信息的生产者
    @DubboReference
    private IForSeckillSkuService dubboSkuService;
    // 保存到Redis的支持
    @Autowired
    private RedisTemplate redisTemplate;


    // 根据SpuId查询秒杀Sku列表
    @Override
    public List<SeckillSkuVO> listSeckillSkus(Long spuId) {
        // 调用根据spuId查询所有Sku列表的方法
        List<SeckillSku> seckillSkus=skuMapper.findSeckillSkusBySpuId(spuId);
        // 声明包含sku详情和秒杀信息类型泛型的List
        List<SeckillSkuVO> seckillSkuVOs=new ArrayList<>();
        // 遍历数据库查询出来的所有sku列表
        for(SeckillSku sku:seckillSkus){
            // 声明既包含秒杀信息,又包含详情的sku对象
            SeckillSkuVO seckillSkuVO= null;
            // 获取skuId保存在变量中
            Long skuId= sku.getSkuId();
            // 在检查Redis是否包含对象前,先准备Key
            String seckillSkuVOKey= SeckillCacheUtils.getSeckillSkuVOKey(skuId);
            // 判断Redis是否包含Key
            if(redisTemplate.hasKey(seckillSkuVOKey)){
                seckillSkuVO=(SeckillSkuVO)redisTemplate
                        .boundValueOps(seckillSkuVOKey).get();
            }else{
                // Redis中没有这个key,需要我们从数据库查询后,保存在Redis中
                // Dubbo根据skuId查询sku详情信息
                SkuStandardVO skuStandardVO=dubboSkuService.getById(skuId);
                // 将seckillSkuVO实例化后,将详情赋值给它
                seckillSkuVO=new SeckillSkuVO();
                BeanUtils.copyProperties(skuStandardVO,seckillSkuVO);
                // 将秒杀信息也赋值给seckillSkuVO,这样它就包含详情信息和秒杀信息了
                seckillSkuVO.setStock(sku.getSeckillStock());
                seckillSkuVO.setSeckillPrice(sku.getSeckillPrice());
                seckillSkuVO.setSeckillLimit(sku.getSeckillLimit());
                // 将对象保存到Redis中
                redisTemplate.boundValueOps(seckillSkuVOKey).set(
                        seckillSkuVO,
                        60*60*24*1000+ RandomUtils.nextInt(2*60*60*1000),
                        TimeUnit.MILLISECONDS);
            }
            seckillSkuVOs.add(seckillSkuVO);
        }
        // 别忘了返回!!
        return seckillSkuVOs;
    }
}
