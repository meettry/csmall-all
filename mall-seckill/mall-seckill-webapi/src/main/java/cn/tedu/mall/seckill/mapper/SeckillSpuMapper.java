package cn.tedu.mall.seckill.mapper;

import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeckillSpuMapper {

    // 查询秒杀商品列表
    List<SeckillSpu> findSeckillSpus();

    // 根据给定的时间查询正在秒杀的商品
    List<SeckillSpu> findSeckillSpusByTime(LocalDateTime time);

    // 查询所有秒杀商品的SpuId数据
    Long[] findAllSeckillSpuIds();

    // 根据SpuId查询秒杀Spu实体
    SeckillSpu findSeckillSpuById(Long spuId);



}
