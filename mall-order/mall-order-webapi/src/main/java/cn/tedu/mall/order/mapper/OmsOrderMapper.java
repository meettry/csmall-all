package cn.tedu.mall.order.mapper;

import cn.tedu.mall.pojo.order.model.OmsOrder;
import org.springframework.stereotype.Repository;

@Repository
public interface OmsOrderMapper {
    // 新增订单对象到数据库的方法
    void insertOrder(OmsOrder order);

}
