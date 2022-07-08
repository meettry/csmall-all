package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.pojo.domain.CsmallAuthenticationInfo;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.order.service.IOmsOrderService;
import cn.tedu.mall.pojo.order.dto.OrderAddDTO;
import cn.tedu.mall.pojo.order.dto.OrderItemAddDTO;
import cn.tedu.mall.pojo.order.vo.OrderAddVO;
import cn.tedu.mall.pojo.seckill.dto.SeckillOrderAddDTO;
import cn.tedu.mall.pojo.seckill.vo.SeckillCommitVO;
import cn.tedu.mall.seckill.service.ISeckillService;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SeckillServiceImpl implements ISeckillService {

    // 需要普通订单生成的方法,dubbo调用
    @DubboReference
    private IOmsOrderService dubboOrderService;
    // 减少Redis库存,是操作Redis字符串类型的数据
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    // 秒杀订单提交成功,需要发送到消息队列后续处理
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     *
     * 1.从Redis中判断是否有库存
     * 2.要判断当前用户是否为重复购买
     * 3.秒杀订单转换成普通订单,需要使用dubbo在order模块完成
     * 4.用消息队列(RabbitMQ)的方式将秒杀成功信息保存在success表中
     */

    @Override
    public SeckillCommitVO commitSeckill(SeckillOrderAddDTO seckillOrderAddDTO) {
        // 第一阶段 从Redis中判断是否有库存和检查重复购买
        // 防止超卖:Redis中预热了sku的库存,判断用户请求的skuid是否有库存
        // 如果有可以购买,如果没有阻止购买
        Long skuId=seckillOrderAddDTO.getSeckillOrderItemAddDTO().getSkuId();
        // 获得秒杀用户的Id
        Long userId=getUserId();
        // 获得skuId和userId就能明确知道谁买了什么商品
        // 秒杀业务规定,一个用户只能购买一个sku一次
        // 我们将该用户和sku的购买关系保存到Redis中,以防止重复购买
        // 生成用户和此sku关联的key
        String reSeckillKey= SeckillCacheUtils.
                        getReseckillCheckKey(skuId,userId);
        // mall:seckill:reseckill:2:1
        // 向这个key中保存一个数据(一般保存一个1表示购买了一次),可以使用自增的方式
        // increment()我们调用这个方法实现两个功能
        // 1.如果key为空,会自动将这个key保存值为1
        // 2.如果key不为空,会自增当前的值 1->2   2->3   3->4
        // 最后将这个数值返回给seckillTimes
        Long seckillTimes=stringRedisTemplate
                                .boundValueOps(reSeckillKey).increment();
        if(seckillTimes>1){
            // 购买次数超过1,证明不是第一次购买,抛出异常,终止业务
            throw new CoolSharkServiceException(
                            ResponseCode.FORBIDDEN,"您已经购买过该商品");
        }
        // 运行到此处证明用户符合购买资格(之前没买过)
        // 下面检查是否有库存,先确定库存的Key
        String seckillSkuCountKey=SeckillCacheUtils.getStockKey(skuId);
        // 从Redis中获得库存
        // 调用decrement()方法,将库存数减1,返回的数值,就是减1后的库存数
        Long leftStock=stringRedisTemplate
                        .boundValueOps(seckillSkuCountKey).decrement(1);
        // leftStock表示当前用户购买完之后剩余的库存数
        // 如果是0表示次用户购买完库存为0,所以只有返回值小于0时才是没有库存了
        if(leftStock<0){
            throw new CoolSharkServiceException(ResponseCode.NOT_ACCEPTABLE,
                                            "对不起您购买的商品已经无货");
        }
        // 运行到此处,表示用户可以生成订单,进入第二阶段
        // 第二阶段 开始生成订单秒杀订单转换成普通订单
        // 我们现获得的秒杀订单参数seckillOrderAddDTO
        // 这个参数信息也是前端收集并发送到后端的,它的信息量和普通订单发送的内容基本相同
        // 我们可以直接dubbo调用order模块新订单的业务来完成
        // 通过调用转换方法将seckillOrderAddDTO转换为OrderAddDTO类型对象
        OrderAddDTO orderAddDTO=convertSeckillOrderToOrder(seckillOrderAddDTO);
        // 转换过程中没有UserId需要手动赋值
        orderAddDTO.setUserId(userId);
        // 信息全了,发送Dubbo请求调用Order模块新增订单
        OrderAddVO orderAddVO=dubboOrderService.addOrder(orderAddDTO);




        // 第三阶段 消息队列(RabbitMQ)发送消息
        return null;
    }

    // 将秒杀订单转换为普通订单的方法
    private OrderAddDTO convertSeckillOrderToOrder(SeckillOrderAddDTO seckillOrderAddDTO) {
        // 实例化OrderAddDTO
        OrderAddDTO orderAddDTO=new OrderAddDTO();
        // 先将属性一致的值赋值到orderAddDTO
        BeanUtils.copyProperties(seckillOrderAddDTO,orderAddDTO);
        // SeckillOrderAddDTO秒杀订单中只可能对应一个OrderItem对象
        // 但是普通订单OrderAddDTO对象中可能包含多个OrderItem对象
        // 所以OrderAddDTO对象中的OrderItem是一个List,而秒杀订单是单个对象
        // 我们需要讲秒杀订单的单个对象添加到OrderAddDTO对象中OrderItem的集合里
        // 实例化OrderItemAddDTO
        OrderItemAddDTO orderItemAddDTO=new OrderItemAddDTO();
        // 将SeckillOrderItemAddDTO同名属性赋值给orderItemAddDTO
        BeanUtils.copyProperties(
            seckillOrderAddDTO.getSeckillOrderItemAddDTO(),orderItemAddDTO);
        // 要想将赋好值的orderItemAddDTO对象添加到orderAddDTO的集合中
        // 需要先实例化这个类型的集合
        List<OrderItemAddDTO> orderItemAddDTOs=new ArrayList<>();
        // 将orderItemAddDTO添加到集合中
        orderItemAddDTOs.add(orderItemAddDTO);
        // 再将集合赋值到orderAddDTO对象中
        orderAddDTO.setOrderItems(orderItemAddDTOs);
        // 最终返回转换完成的orderAddDTO对象
        return orderAddDTO;
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
