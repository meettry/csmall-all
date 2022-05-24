package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.pojo.seckill.model.SeckillSku;
import cn.tedu.mall.pojo.seckill.vo.SeckillSkuVO;
import cn.tedu.mall.product.service.seckill.IForSeckillSkuService;
import cn.tedu.mall.seckill.mapper.SeckillSkuMapper;
import cn.tedu.mall.seckill.service.ISeckillSkuService;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
            }
        }
        return null;
    }
}
