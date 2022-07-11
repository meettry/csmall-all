package cn.tedu.mall.seckill.mapper;

import cn.tedu.mall.pojo.seckill.model.Success;
import org.springframework.stereotype.Repository;

@Repository
public interface SuccessMapper {

    // 新增Success对象到数据库的方法
    void saveSuccess(Success success);



}
