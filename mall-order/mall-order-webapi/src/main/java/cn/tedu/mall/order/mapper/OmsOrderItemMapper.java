package cn.tedu.mall.order.mapper;

import cn.tedu.mall.pojo.order.model.OmsOrderItem;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Meettry
 * @date 2022/11/2 16:48
 */
@Repository
public interface OmsOrderItemMapper {

    /**
     * 新增订单项的方法(一个订单可能包括多个商品)
     * @param omsOrderItems 商品列表
     * @return
     */
    int insertOrderItemList(List<OmsOrderItem> omsOrderItems);
}
