package cn.tedu.mall.order.mapper;

import cn.tedu.mall.pojo.order.model.OmsOrder;
import org.springframework.stereotype.Repository;

/**
 * @author Meettry
 * @date 2022/11/2 17:29
 */
@Repository
public interface OmsOrderMapper {

    /**
     * 新增订单的方法
     * @param omsOrder 订单数据
     * @return
     */
    int insertOrder(OmsOrder omsOrder);
}
