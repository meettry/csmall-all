package cn.tedu.mall.order.mapper;

import cn.tedu.mall.pojo.order.dto.OrderListTimeDTO;
import cn.tedu.mall.pojo.order.model.OmsOrder;
import cn.tedu.mall.pojo.order.vo.OrderListVO;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * 查询当前用户指定时间范围内的所有订单信息(包括订单项)
     * @param orderListTimeDTO
     * @return
     */
    List<OrderListVO> selectOrdersBetweenTimes(OrderListTimeDTO orderListTimeDTO);

    /**
     * 利用动态sql,实现对订单内容的修改
     * @param omsOrder 必须包含id,id不可修改,如果其他属性部位空,就修改为当前值
     * @return
     */
    int updateOrderById(OmsOrder omsOrder);
}
