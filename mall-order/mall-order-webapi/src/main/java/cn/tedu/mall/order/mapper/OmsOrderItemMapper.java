package cn.tedu.mall.order.mapper;

import cn.tedu.mall.pojo.order.model.OmsOrderItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OmsOrderItemMapper {
    // 对oms_order_item(订单商品表)表进行新增的方法
    // 因为一个订单可能有多个商品,所以我们可以使用批量新增的语句
    void insertOrderItems(List<OmsOrderItem> omsOrderItems);
}
