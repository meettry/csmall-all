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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.seata.spring.annotation.GlobalTransactional;
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
    // 利用Dubbo获得product模块修改库存的功能
    @DubboReference
    private IForOrderSkuService dubboSkuService;
    @Autowired
    private IOmsCartService cartService;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    // 根据提供的订单信息,生成订单
    // Seata约束的分布式事务起点
    @GlobalTransactional
    @Override
    public OrderAddVO addOrder(OrderAddDTO orderAddDTO) {
        // 在连接数据库操作前,一定要先把所有数据准备好
        // 1.将订单实体的所有属性赋值OmsOrder
        OmsOrder omsOrder=new OmsOrder();
        // 将参数orderAddDTO所有同名属性赋值给omsOrder
        BeanUtils.copyProperties(orderAddDTO,omsOrder);
        // orderAddDTO中数据并不全面,我们需要更详细的信息才能新增订单
        // 因为收集信息代码较多,单独编写一个方法实现
        loadOrder(omsOrder);
        // 2.遍历订单中包含的所有商品的集合,也保证所有属性被赋值OmsOrderItem
        // 获得orderAddDTO对象中的所有商品sku集合,判断是否为空
        List<OrderItemAddDTO> orderItemAddDTOs=orderAddDTO.getOrderItems();
        if(orderItemAddDTOs==null || orderItemAddDTOs.isEmpty()){
            // 如果为null或集合中没有元素,抛出异常,终止新增订单
            throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,"订单中必须有商品");
        }
        // 我们的目标是将订单中的商品增到oms_order_item表中
        // 我们持有的持久层方法参数是List<OmsOrderItem>
        // 我们先在获得的是List<OrderItemAddDTO>,类型不一致,
        // 我们需要讲OrderItemAddDTO转换成OmsOrderItem,保存到一个新的集合里
        List<OmsOrderItem> omsOrderItems=new ArrayList<>();
        // 遍历参数中包含的所有商品列表
        for(OrderItemAddDTO addDTO: orderItemAddDTOs){
            // 先是转换我们的OrderItemAddDTO为OmsOrderItem
            OmsOrderItem orderItem=new OmsOrderItem();
            // 同名属性赋值
            BeanUtils.copyProperties(addDTO,orderItem);
            // 和Order一样OrderItem也有属性要单独复制
            loadOrderItem(orderItem);
            // 根据上面方法获得的omsOrder的订单id,给当前订单项的订单id属性赋值
            orderItem.setOrderId(omsOrder.getId());
            // 到此为止,我们的订单和循环遍历的订单中的订单项都已经赋好值,下面就要开始进行数据库操作了!
            // 将收集好信息的orderItem对象添加到omsOrderItems集合中
            omsOrderItems.add(orderItem);
            // 3.遍历中所有值被赋值后,修改集合中所有商品的库存,并从购物车中删除这些商品
            // 减少库存数
            // 获得skuId
            Long skuId=orderItem.getSkuId();
            // 获取减少的商品的数量
            Integer quantity=orderItem.getQuantity();
            // dubbo调用减少库存的方法
            int rows=dubboSkuService.reduceStockNum(skuId,quantity);
            // 如果rows值为0,表示这次修改没有修改任何行,一般因为库存不足导致的
            if(rows==0){
                log.warn("商品skuId:{},库存不足",skuId);
                // 抛出异常,Seata组件可以另之前循环过程中已经新增到数据库的信息回滚
                throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,"库存不足");
            }
            // 删除购物车中商品
            // 删除购物车商品的方法需要OmsCart实体类
            OmsCart omsCart=new OmsCart();
            omsCart.setSkuId(skuId);
            omsCart.setUserId(omsOrder.getUserId());
            cartService.removeUserCarts(omsCart);
        }
        // 4.将订单信息新增到数据(包括OrderItem和Order)
        // 新增OrderItem对象,利用mapper中批量新增的方法
        orderItemMapper.insertOrderItems(omsOrderItems);
        // 新增订单表
        orderMapper.insertOrder(omsOrder);

        // 最后要保证用户能够看到订单详情
        // 我们不需要返回订单的所有信息,因为前端包含大部分订单信息
        // 我们只需要返回后端生成的一些数据即可
        // OrderAddVO完成这个功能
        OrderAddVO addVO=new OrderAddVO();
        addVO.setId(omsOrder.getId());
        addVO.setSn(omsOrder.getSn());
        addVO.setCreateTime(omsOrder.getGmtCreate());
        addVO.setPayAmount(omsOrder.getAmountOfActualPay());
        // 别忘了返回正确的对象
        return addVO;
    }

    private void loadOrderItem(OmsOrderItem orderItem) {
        if(orderItem.getId()==null){
            Long id=IdGeneratorUtils.getDistributeId("order_item");
            orderItem.setId(id);
        }
        // 必须包含skuid信息,才能确定商品信息
        if(orderItem.getSkuId()==null){
            throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,
                                            "订单中商品必须包含skuId");
        }
    }


    // 新增订单业务中需要的收集Order信息的方法
    private void loadOrder(OmsOrder omsOrder) {
        // 针对OmsOrder对象为空的值进行收集或生成
        // 判断id是否为空
        if(omsOrder.getId()==null){
            // Leaf获得分布式id
            Long id= IdGeneratorUtils.getDistributeId("order");
            omsOrder.setId(id);
        }
        // 判断userId是否为空
        if(omsOrder.getUserId()==null){
            // 从SpringSecurity容器中获得jwt解析而来的用户id
            omsOrder.setUserId(getUserId());
        }
        // 判断sn
        if(omsOrder.getSn()==null){
            omsOrder.setSn(UUID.randomUUID().toString());
        }
        // 判断state
        if (omsOrder.getState()==null){
            // 如果订单状态为null默认是新生成的订单,状态为0:未支付
            omsOrder.setState(0);
        }
        // 下面要保证订单的生成实际,订单数据的创建实际和最后修改时间一致
        // 我们手动获取当前系统时间,统一给他们赋值
        if(omsOrder.getGmtOrder()==null){
            LocalDateTime now=LocalDateTime.now();
            omsOrder.setGmtOrder(now);
            omsOrder.setGmtCreate(now);
            omsOrder.setGmtModified(now);
        }
        // 下面是系统计算金额,前端实际上有基本计算显示,但是前安全性相对差,后端还要计算
        // 计算基本逻辑 原价+运费-优惠=最终价格
        // 判断运费,默认为0
        if(omsOrder.getAmountOfFreight()==null){
            // 默认运费为0
            omsOrder.setAmountOfFreight(new BigDecimal(0.0));
        }
        // 判断优惠,默认为0
        if(omsOrder.getAmountOfDiscount()==null){
            omsOrder.setAmountOfDiscount(new BigDecimal(0.0));
        }
        // 获取传递过来的原价信息,如果原价为空,抛出异常
        if(omsOrder.getAmountOfOriginalPrice()==null){
            throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,"没有提供订单原价");
        }
        // 计算实际支付金额
        // 原价+运费-优惠=最终价格
        BigDecimal originalPrice=omsOrder.getAmountOfOriginalPrice();
        BigDecimal freight=omsOrder.getAmountOfFreight();
        BigDecimal discount=omsOrder.getAmountOfDiscount();
        BigDecimal actualPay=originalPrice.add(freight).subtract(discount);
        // 赋值给实际支付属性
        omsOrder.setAmountOfActualPay(actualPay);


    }

    // 根据订单id 修改订单状态
    @Override
    public void updateOrderState(OrderStateUpdateDTO orderStateUpdateDTO) {
        // 参数orderStateUpdateDTO包含订单id和状态码
        // 我们修改订单的方法参数是OmsOrder,所以需要实例化这个类型对象并赋值
        OmsOrder omsOrder=new OmsOrder();
        BeanUtils.copyProperties(orderStateUpdateDTO,omsOrder);
        // 指定修改订单的方法
        // 因为现在OmsOrder中只有id和state属性,所以不会修改其他列
        orderMapper.updateOrderById(omsOrder);
    }






    // 查询当前登录用户在指定时间范围内(默认一个月内)所有订单
    // 订单包含订单信息和订单项信息两个方面(xml的sql语句是关联查询)
    @Override
    public JsonPage<OrderListVO> listOrdersBetweenTimes(OrderListTimeDTO orderListTimeDTO) {
        // 因为默认为最近一个月内,如果没有起始和结束时间,需要我们自动添加
        // 要检查起始时间和结束时间是否合理,我们单独编写方法校验上面业务
        validaTimeAndLoadTimes(orderListTimeDTO);
        // 时间验证通过,开始进程查询
        // 获得当前用户id
        Long userId=getUserId();
        // 将userId赋值给参数
        orderListTimeDTO.setUserId(userId);
        // 设置分页条件
        PageHelper.startPage(orderListTimeDTO.getPage(),orderListTimeDTO.getPageSize());
        // 执行查询
        List<OrderListVO> list=orderMapper.selectOrdersBetweenTimes(orderListTimeDTO);
        // 别忘了返回
        return JsonPage.restPage(new PageInfo<>(list));

    }

    private void validaTimeAndLoadTimes(OrderListTimeDTO orderListTimeDTO) {
        // 取出起始和结束时间对象
        LocalDateTime start=orderListTimeDTO.getStartTime();
        LocalDateTime end=orderListTimeDTO.getEndTime();
        // 如果start和end中有任何一个为null,默认查询一个月内
        if(start==null || end==null){
            // 起始时间是当前时间减一个月minusMonths就是减月份的意思,1就是一个月
            start=LocalDateTime.now().minusMonths(1);
            // 默认结束时间是当前时间
            end=LocalDateTime.now();
            // 赋值给orderListTimeDTO参数
            orderListTimeDTO.setStartTime(start);
            orderListTimeDTO.setEndTime(end);
        }else{
            // 如果是国际的时间判断,需要添加时区修正来判断时间
            // 判断结束时间大于起始时间,否则发生异常
            if(end.toInstant(ZoneOffset.of("+8")).toEpochMilli()<
                start.toInstant(ZoneOffset.of("+8")).toEpochMilli()){
                throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,
                        "结束时间应该大于起始时间");
            }
        }

    }

    @Override
    public OrderDetailVO getOrderDetail(Long id) {
        return null;
    }


    public CsmallAuthenticationInfo getUserInfo(){
        // 获得SpringSecurity容器对象
        UsernamePasswordAuthenticationToken authenticationToken=
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.
                        getContext().getAuthentication();
        // 判断获取的容器信息是否为空
        if(authenticationToken!=null){
            // 如果容器中有内容,证明当前容器中有登录用户信息
            // 我们获取这个用户信息并返回
            CsmallAuthenticationInfo csmallAuthenticationInfo=
                    (CsmallAuthenticationInfo)authenticationToken.getCredentials();
            return csmallAuthenticationInfo;
        }
        throw new CoolSharkServiceException(ResponseCode.UNAUTHORIZED,"没有登录信息");
    }
    // 业务逻辑层中大多数方法都是获得用户id,所以编写一个返回用户id的方法
    public Long getUserId(){
        return getUserInfo().getId();
    }
}
