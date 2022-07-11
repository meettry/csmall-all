package cn.tedu.mall.seckill.mapper;

import cn.tedu.mall.pojo.seckill.model.SeckillSku;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeckillSkuMapper {

    // 根据SpuId查询Sku列表
    List<SeckillSku> findSeckillSkusBySpuId(Long spuId);

    // 根据skuId修改库存数
    void updateReduceStockBySkuId(@Param("skuId") Long skuId,
                                  @Param("quantity") Integer quantity);

}
