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
import com.baomidou.mybatisplus.extension.api.R;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.models.auth.In;
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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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

    // 当前生成订单的方法为分布式事务
    // Seata保证整个业务逻辑层运行过程中的所有数据库操作要么都成功,要么都失败
    @GlobalTransactional
    @Override
    public OrderAddVO addOrder(OrderAddDTO orderAddDTO) {
        // 首先先实例化能够新增到数据库的OmsOrder类
        OmsOrder omsOrder=new OmsOrder();
        // 业务逻辑层参数OrderAddDTO的数据有限,其他OmsOrder需要的数据我们需要自己收集
        BeanUtils.copyProperties(orderAddDTO,omsOrder);
        // 因为收集OmsOrder数据代码较多,所以我们单独编写一个方法实现
        loadOrder(omsOrder);
        // 获得当前订单中所有商品的sku
        List<OrderItemAddDTO> orderItemDTOs=orderAddDTO.getOrderItems();
        if(orderItemDTOs==null|| orderItemDTOs.isEmpty()){
            throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,"没有选中购物车中商品");
        }
        // 上面集合泛型为OrderItemAddDTO,但新增到数据库表的实体类为OmsOrderItem
        // 所以实例化一个OmsOrderItem泛型集合,以备使用
        List<OmsOrderItem> omsOrderItems=new ArrayList<>();
        // 遍历当前订单用包含的所有商品的集合
        for(OrderItemAddDTO orderItem : orderItemDTOs){
            // 遍历操作目标
            // 将OmsOrderItem需要但是OrderItemAddDTO没有的属性补充上
            OmsOrderItem omsOrderItem=new OmsOrderItem();
            // 同名属性赋值
            BeanUtils.copyProperties(orderItem,omsOrderItem);
            // 定义一个收集OrderItem属性的方法(代码不多,卸载这里也行,课程中仍然定义方法)
            loadOrderItem(omsOrderItem);
            // 赋值订单id
            omsOrderItem.setOrderId(omsOrder.getId());
            // 将补充好属性的OmsOrderItem对象添加到omsOrderItems集合中
            omsOrderItems.add(omsOrderItem);
            // 减少库存数
            // 获得skuId
            Long skuId=omsOrderItem.getSkuId();
            // 获得商品数量
            Integer quantity=omsOrderItem.getQuantity();
            // dubbo调用减少库存的方法
            int result=dubboSkuService.reduceStockNum(skuId,quantity);
            // result是减少库存方法执行后影响数据库的行数,如果是0,表示没有数据变化,一般是库存不足导致的
            if(result==0){
                log.warn("商品skuId:{},库存不足",skuId);
                // 抛出异常,Seata回滚
                throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,"库存不足");
            }
            // 删除购物车中信息
            OmsCart omsCart=new OmsCart();
            omsCart.setSkuId(skuId);
            omsCart.setUserId(omsOrder.getUserId());
            cartService.removeUserCarts(omsCart);
        }
        // 执行将所有订单中商品新增到oms_order_item表中的操作
        orderItemMapper.insertOrderItems(omsOrderItems);
        // 新增生成好的订单到oms_order表
        orderMapper.insertOrder(omsOrder);
        // 到此为止,新增完成了
        // 下面要完成用户生成订单预览页面,这个页面中大部分信息可以前端自己处理
        // 我们需要将一些有后端生成的代码发送给前端,这个类是OrderAddVO
        OrderAddVO addVO=new OrderAddVO();
        addVO.setId(omsOrder.getId());
        addVO.setSn(omsOrder.getSn());
        addVO.setCreateTime(omsOrder.getGmtCreate());
        addVO.setPayAmount(omsOrder.getAmountOfActualPay());
        // 别忘了将生成的信息返回!!!
        return addVO;
    }

    private void loadOrderItem(OmsOrderItem omsOrderItem) {
        // 判断id并赋值
        if(omsOrderItem.getId()==null){
            Long id=IdGeneratorUtils.getDistributeId("order_item");
            omsOrderItem.setId(id);
        }
        // 商品没有skuid的抛异常Seata会回滚
        if (omsOrderItem.getSkuId()==null){
            throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,"订单中商品不能缺失SKU");
        }
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

    // 修改订单状态的业务逻辑层
    @Override
    public void updateOrderState(OrderStateUpdateDTO orderStateUpdateDTO) {
        // OrderStateUpdateDTO参数只有id和state俩个属性,支持SpringValidation验证
        OmsOrder omsOrder=new OmsOrder();
        BeanUtils.copyProperties(orderStateUpdateDTO,omsOrder);
        // 这里运行的修改只能是按id修改订单状态
        orderMapper.updateOrderById(omsOrder);
    }

    // 查询当前登录用户在指定时间范围内(默认一个月内)所有订单
    // 每个订单对象中还包含订单中商品信息对象集合
    @Override
    public JsonPage<OrderListVO> listOrdersBetweenTimes(OrderListTimeDTO orderListTimeDTO) {
        // 本业务的逻辑中比较特殊的是时间的判断,单独编写一个方法效验时间或添加默认时间
        validTimesAndLoadTimes(orderListTimeDTO);
        // 获得userId
        Long userId=getUserId();
        // 将userId赋值给orderListTimeDTO
        orderListTimeDTO.setUserId(userId);
        // 开启分页查询
        // 如果想要判断分页信息的可以这里编写代码
        PageHelper.startPage(orderListTimeDTO.getPage(),orderListTimeDTO.getPageSize());
        // 执行查询
        List<OrderListVO> list=orderMapper.selectOrdersBetweenTimes(orderListTimeDTO);
        //别忘了返回list!!!
        return JsonPage.restPage(new PageInfo<>(list));
    }

    private void validTimesAndLoadTimes(OrderListTimeDTO orderListTimeDTO) {
        // 选取出起始时间对象和结束时间对象
        LocalDateTime start=orderListTimeDTO.getStartTime();
        LocalDateTime end=orderListTimeDTO.getEndTime();
        // 如果strat或end有任何一个为空,默认查询进一个月的订单
        if(start==null||end==null){
            // 起始时间定一个月之前
            start=LocalDateTime.now().minusMonths(1);
            // 结束数据是现在
            end=LocalDateTime.now();
            // 将修改好的起始和结束时间赋值给orderListTimeDTO
            orderListTimeDTO.setStartTime(start);
            orderListTimeDTO.setEndTime(end);
        }else{
            //如果 起始时间和结束时间都有值,判断结束时间要是小于起始时间要发生异常
            if(end.toInstant(ZoneOffset.of("+8")).toEpochMilli()<
                    start.toInstant(ZoneOffset.of("+8")).toEpochMilli()){
                // 发生异常,程序终止给出提示
                throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,"查询的结束时间应大于起始时间");
            }
        }
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
