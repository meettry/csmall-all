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
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillSkuMapper seckillSkuMapper;
    @DubboReference
    private IForSeckillSkuService dubboSkuService;

    @Override
    public List<SeckillSkuVO> listSeckillSkus(Long spuId) {
        // 按SpuId从seckill_sku表中查询对应的sku列表
        List<SeckillSku> seckillSkus=seckillSkuMapper.selectSeckillSkusBySpuId(spuId);
        // 声明返回类型的集合对象
        List<SeckillSkuVO> seckillSkuVOs=new ArrayList<>();
        for(SeckillSku seckillSku : seckillSkus){
            SeckillSkuVO seckillSkuVO=null;
            // 获得当前skuId
            Long skuId=seckillSku.getSkuId();
            // 获得skuId对应Redis的Key
            String seckillSkuVOKey= SeckillCacheUtils.getSeckillSkuVOKey(skuId);
            // 判断Redis中是否有这个key
            if(redisTemplate.hasKey(seckillSkuVOKey)){
                seckillSkuVO =(SeckillSkuVO)redisTemplate
                                .boundValueOps(seckillSkuVOKey).get();
            }else{
                // 如果Redis中没有对应的key
                // 先查询常规sku表中的信息
                SkuStandardVO skuStandardVO=dubboSkuService.getById(skuId);
                // 实例化seckillSkuVO
                seckillSkuVO=new SeckillSkuVO();
                // 将常规表中信息赋值给seckillSkuVO
                BeanUtils.copyProperties(skuStandardVO,seckillSkuVO);
                // 将秒杀信息也赋值到seckillSkuVO
                seckillSkuVO.setStock(seckillSku.getSeckillStock());
                seckillSkuVO.setSeckillPrice(seckillSku.getSeckillPrice());
                seckillSkuVO.setSeckillLimit(seckillSku.getSeckillLimit());
                // 将信息保存在Redis,以便后续获取
                redisTemplate.boundValueOps(seckillSkuVOKey).set(seckillSkuVO,
                        1000*60*60*72+ RandomUtils.nextInt(1000*60*60*5),
                                TimeUnit.MILLISECONDS);
            }
            // 将查询好的seckillSkuVO对象新增到集合中
            seckillSkuVOs.add(seckillSkuVO);
        }
        // 千万别忘了返回集合对象!!!!
        return seckillSkuVOs;
    }
}
