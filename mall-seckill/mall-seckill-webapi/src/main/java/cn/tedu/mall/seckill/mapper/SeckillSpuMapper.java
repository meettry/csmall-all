package cn.tedu.mall.seckill.mapper;

import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeckillSpuMapper {

    // 声明查询秒杀spu商品列表的方法
    List<SeckillSpu> selectSeckillSpus();

}
