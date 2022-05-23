package cn.tedu.mall.seckill.mapper;

import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeckillSpuMapper {

    // 声明查询秒杀spu商品列表的方法
    List<SeckillSpu> selectSeckillSpus();

    // 查询给定时间正在进行秒杀活动的spu列表
    List<SeckillSpu> selectSeckillSpusInTime(LocalDateTime time);

    // 查询所有秒杀商品的spuid列表
    Long[] selectAllSeckillSpuIds();

    // 根据SpuId查询spu信息
    SeckillSpu selectSeckillSpuBySpuId(Long spuId);



}
