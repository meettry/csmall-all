package cn.tedu.mall.order.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.pojo.domain.CsmallAuthenticationInfo;
import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.order.mapper.OmsOrderItemMapper;
import cn.tedu.mall.order.mapper.OmsOrderMapper;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.order.service.IOmsOrderService;
import cn.tedu.mall.order.utils.IdGeneratorUtils;
import cn.tedu.mall.pojo.order.dto.OrderAddDTO;
import cn.tedu.mall.pojo.order.dto.OrderListTimeDTO;
import cn.tedu.mall.pojo.order.dto.OrderStateUpdateDTO;
import cn.tedu.mall.pojo.order.model.OmsOrder;
import cn.tedu.mall.pojo.order.vo.OrderAddVO;
import cn.tedu.mall.pojo.order.vo.OrderDetailVO;
import cn.tedu.mall.pojo.order.vo.OrderListVO;
import cn.tedu.mall.product.service.order.IForOrderSkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@DubboService
@Service
@Slf4j
public class OmsOrderServiceImpl implements IOmsOrderService {

    // 利用Dubbo获得能够减少库存数的服务,在product模块
    @DubboReference
    private IForOrderSkuService dubboSkuService;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private IOmsCartService cartService;

    @Override
    public OrderAddVO addOrder(OrderAddDTO orderAddDTO) {
        // 首先先实例化能够新增到数据库的OmsOrder类
        OmsOrder omsOrder=new OmsOrder();
        // 业务逻辑层参数OrderAddDTO的数据有限,其他OmsOrder需要的数据我们需要自己收集
        BeanUtils.copyProperties(orderAddDTO,omsOrder);
        // 因为收集OmsOrder数据代码较多,所以我们单独编写一个方法实现
        loadOrder(omsOrder);


        return null;
    }
    // 生成订单方法中需要的方法,能够将omsOrder对象中可能为空的数据赋值
    private void loadOrder(OmsOrder omsOrder) {
        // 判断出id是否为空
        if(omsOrder.getId()==null){
            // 从leaf获得分布式id
            Long id= IdGeneratorUtils.getDistributeId("order");
            omsOrder.setId(id);
        }
        // 判断userId并为其赋值
        if(omsOrder.getUserId()==null){
            // jwt解析结果中获得userId
            Long userId=getUserId();
            omsOrder.setUserId(userId);
        }
        // 判断sn是否为空并赋值
        if(omsOrder.getSn()==null){
            // sn是给用户看的订单编号,使用UUID生成即可
            omsOrder.setSn(UUID.randomUUID().toString());
        }
        // 判断state并赋值
        if(omsOrder.getState()==null){
            // state:0表示订单未支付,新创建的订单都是未支付的
            omsOrder.setState(0);
        }
        // 订单生成时 订单创建时间,数据创建时间和最后修改时间,是同一时刻
        // 所以我们手动赋值,保证他们数据一致
        if(omsOrder.getGmtCreate()==null){
            omsOrder.setGmtCreate(LocalDateTime.now());
        }
        if(omsOrder.getGmtOrder()==null){
            omsOrder.setGmtOrder(omsOrder.getGmtCreate());
        }
        if(omsOrder.getGmtModified()==null){
            omsOrder.setGmtModified(omsOrder.getGmtCreate());
        }
        // 下面是金额的判断和计算
        // 基本逻辑是原始金额加运费减优惠计算出最终金额
        if(omsOrder.getAmountOfDiscount()==null){
            // 为了防止计算时发生浮点偏移的误差,我们使用BigDecimal类型
            omsOrder.setAmountOfDiscount(new BigDecimal(0.0));
        }
        // 运费的判断
        if(omsOrder.getAmountOfFreight()==null){
            omsOrder.setAmountOfFreight(new BigDecimal(0.0));
        }
        // 开始计算订单金额
        // 如果没有初始订单金额的数值,表示前端传值不正确,直接发生异常
        if (omsOrder.getAmountOfOriginalPrice()==null){
            throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,"请确定购买商品的原始金额");
        }
        // 判断实际支付金额
        if (omsOrder.getAmountOfActualPay()==null){
            // 实际金额=原始金额+运费-优惠
            // 获得各项金额
            BigDecimal originalPrice=omsOrder.getAmountOfOriginalPrice();
            BigDecimal discount=omsOrder.getAmountOfDiscount();
            BigDecimal freight=omsOrder.getAmountOfFreight();
            BigDecimal actualPay=originalPrice.add(freight).subtract(discount);
            // 赋值给当前属性
            omsOrder.setAmountOfActualPay(actualPay);
        }
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

    // 业务逻辑层中可能有多个方法需要获得当前用户信息
    // 我们可以定义一个方法实现从SpringSecurity中获得用户信息
    public CsmallAuthenticationInfo getUserInfo(){
        // 从SpringSecurity框架的容器中,获得当前用户的authenticationToken对象
        UsernamePasswordAuthenticationToken authenticationToken=
                (UsernamePasswordAuthenticationToken) SecurityContextHolder
                        .getContext().getAuthentication();
        // 判断获取的对象是不是null
        if(authenticationToken!=null){
            // 如果不是空,就是登录成功了,从authenticationToken对象中获得当前登录用户
            CsmallAuthenticationInfo csmallAuthenticationInfo=
                    (CsmallAuthenticationInfo)authenticationToken.getCredentials();
            return csmallAuthenticationInfo;
        }
        throw new CoolSharkServiceException(ResponseCode.UNAUTHORIZED,"没有登录信息");
    }
    // 单纯获得当前登录用户id的方法
    public Long getUserId(){
        return getUserInfo().getId();
    }
}
