package cn.tedu.mall.order.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.common.security.UserInfo;
import cn.tedu.mall.order.mapper.OmsOrderItemMapper;
import cn.tedu.mall.order.mapper.OmsOrderMapper;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.order.service.IOmsOrderService;
import cn.tedu.mall.order.utils.IdGeneratorUtils;
import cn.tedu.mall.pojo.order.dto.OrderAddDTO;
import cn.tedu.mall.pojo.order.dto.OrderItemAddDTO;
import cn.tedu.mall.pojo.order.dto.OrderListTimeDTO;
import cn.tedu.mall.pojo.order.dto.OrderStateUpdateDTO;
import cn.tedu.mall.pojo.order.model.OmsCart;
import cn.tedu.mall.pojo.order.model.OmsOrder;
import cn.tedu.mall.pojo.order.model.OmsOrderItem;
import cn.tedu.mall.pojo.order.vo.OrderAddVO;
import cn.tedu.mall.pojo.order.vo.OrderDetailVO;
import cn.tedu.mall.pojo.order.vo.OrderListVO;
import cn.tedu.mall.product.service.order.IForOrderSkuService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.index.fielddata.IndexFieldDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 秒杀业务需要调用订单方法,所以需要加DubboService注解
 *
 * @author Meettry
 * @date 2022/11/3 9:49
 */
@Service
@Slf4j
@DubboService
public class OmsOrderServiceImpl implements IOmsOrderService {

    @DubboReference
    private IForOrderSkuService dubboSkuService;
    @Autowired
    private IOmsCartService omsCartService;
    @Autowired
    private OmsOrderMapper omsOrderMapper;
    @Autowired
    private OmsOrderItemMapper omsOrderItemMapper;

    @GlobalTransactional
    @Override
    // 这个方法dubbo调用了Product模块的方法,操作了数据库,有分布式事务的需求
    // 所以要使用注解激活Seata分布式事务的功能
    public OrderAddVO addOrder(OrderAddDTO orderAddDTO) {
        // 第一部分:收集信息,准备数据
        // 实例化OmsOrder对象
        OmsOrder order = new OmsOrder();
        // 当前方法参数orderAddDTO有很多order需要的同名属性,直接赋值接口
        BeanUtils.copyProperties(orderAddDTO,order);
        // orderAddDTO中属性比order要少,缺少的属性要我们自己赋值生成
        // 可以编写一个专门的方法来进行数据的收集
        loadOrder(order);

        // 将当前订单中的包含的商品新增到数据库中的order_item表
        // 获取当前订单中包含的所有商品的集合
        List<OrderItemAddDTO> orderItemAddDTOs = orderAddDTO.getOrderItems();
        if (orderItemAddDTOs == null || orderItemAddDTOs.isEmpty()) {
            // 订单中没有商品,无法生成订单
            throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,"订单中必须至少包含一件商品");
        }
        // 将集合泛型OrderItemAddDTO转换为OmsOrderItem
        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        for (OrderItemAddDTO orderItemAddDTO : orderItemAddDTOs) {
            OmsOrderItem omsOrderItem = new OmsOrderItem();
            BeanUtils.copyProperties(orderItemAddDTO,omsOrderItem);
            // 将orderItemAddDTO没有的id与orderId属性赋值给omsOrderItem
            omsOrderItem.setOrderId(order.getId());
            Long itemId = IdGeneratorUtils.getDistributeId("order_item");
            omsOrderItem.setId(itemId);
            // 添加到集合
            omsOrderItems.add(omsOrderItem);

            // 第二部分:执行操作数据库的指令
            // 减少库存,删除购物车,加订单,加订单项
            // 当前循环是订单中的一件商品,我们可以在此处对这个商品进行库存的减少
            // 当前对象属性中是包含skuId和要购买的商品数量的,所以可以执行库存的修改
            // 1.减少库存
            // 先获取skuId
            Long skuId=omsOrderItem.getSkuId();
            // 修改库存是Dubbo调用的
            int rows=dubboSkuService.reduceStockNum(
                    skuId,omsOrderItem.getQuantity());
            // 判断rows(数据库受影响的行数)的值
            if(rows==0){
                log.warn("商品库存不足,skuId:{}",skuId);
                // 库存不足不能继续生成订单,抛出异常,终止事务进行回滚
                throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,
                        "库存不足!");
            }
            // 2.删除勾选的购物车商品
            OmsCart omsCart = new OmsCart();
            omsCart.setUserId(order.getUserId());
            omsCart.setSkuId(skuId);
            // 执行删除
            omsCartService.removeUserCarts(omsCart);
        }
        // 3.新增订单
        // omsOrderMapper直接调用新增订单的方法
        omsOrderMapper.insertOrder(order);

        // 4.新增订单项
        omsOrderItemMapper.insertOrderItemList(omsOrderItems);

        // 第三部分:准备返回值
        OrderAddVO orderAddVO = new OrderAddVO();
        orderAddVO.setId(order.getId());
        orderAddVO.setSn(order.getSn());
        orderAddVO.setPayAmount(order.getAmountOfActualPay());
        orderAddVO.setCreateTime(order.getGmtOrder());
        return orderAddVO;

    }

    /**
     * 为order对象补全属性值的方法
     * @param order
     */
    private void loadOrder(OmsOrder order) {
        // 为没有补全属性的对象,进行生成或手动赋值
        // 给id赋值,订单业务不使用数据库自增id接口,
        Long id = IdGeneratorUtils.getDistributeId("order");
        order.setId(id);

        // 生成订单号,直接使用UUID即可
        order.setSn(UUID.randomUUID().toString());
        // 赋值给UserId
        // 以后秒杀业务调用此方法,userId属性总是会被赋值的
        if (order.getId() == null) {
            // 从SpringSecurity上下文获得用户id
            order.setId(UserInfo.getUserId());
        }
        // 为订单状态赋值
        // 订单状态如果为null,将其设置为默认值0,表示未支付
        if (order.getState() == null) {
            order.setState(0);
        }
        // 为了保证下单时间和数据创建时间,最后修改省时间一致
        // 给他们赋相同的值
        LocalDateTime now = LocalDateTime.now();
        order.setGmtOrder(now);
        order.setGmtCreate(now);
        order.setGmtModified(now);

        // 验算实际支付金额
        // 计算公式: 实际金额=原价-优惠+运费
        // 数据类型使用BigDecimal,防止浮点偏移
        BigDecimal price = order.getAmountOfOriginalPrice();
        BigDecimal freight = order.getAmountOfFreight();
        BigDecimal disCount = order.getAmountOfDiscount();
        BigDecimal actualPay = price.subtract(disCount).add(freight);
        order.setAmountOfActualPay(actualPay);


    }

    @Override
    public void updateOrderState(OrderStateUpdateDTO orderStateUpdateDTO) {

    }

    @Override
    public JsonPage<OrderListVO> listOrdersBetweenTimes(OrderListTimeDTO orderListTimeDTO) {
        return null;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long id) {
        return null;
    }
}
