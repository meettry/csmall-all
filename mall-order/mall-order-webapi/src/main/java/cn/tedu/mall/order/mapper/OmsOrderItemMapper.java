package cn.tedu.mall.order.mapper;

import cn.tedu.mall.pojo.order.model.OmsOrderItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OmsOrderItemMapper {

    // 新增订单业务时,需要一个能够新增oms_order_item表信息的方法
    // 因为一个订单中的商品可能有多个,所以我们新增方法的参数是List
    // 在Xml中试下list的遍历,实现连接一次数据库新增多条数据
    void insertOrderItems(List<OmsOrderItem> omsOrderItems);

}
