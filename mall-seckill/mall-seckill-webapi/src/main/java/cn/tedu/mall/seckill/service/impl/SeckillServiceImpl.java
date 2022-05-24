package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.pojo.domain.CsmallAuthenticationInfo;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.order.service.IOmsOrderService;
import cn.tedu.mall.pojo.order.dto.OrderAddDTO;
import cn.tedu.mall.pojo.order.dto.OrderItemAddDTO;
import cn.tedu.mall.pojo.order.vo.OrderAddVO;
import cn.tedu.mall.pojo.seckill.dto.SeckillOrderAddDTO;
import cn.tedu.mall.pojo.seckill.model.Success;
import cn.tedu.mall.pojo.seckill.vo.SeckillCommitVO;
import cn.tedu.mall.seckill.config.RabbitMqComponentConfiguration;
import cn.tedu.mall.seckill.service.ISeckillService;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.lucene.search.CollectionTerminatedException;
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
public class SeckillServiceImpl implements ISeckillService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @DubboReference
    private IOmsOrderService dubboOrderService;

    /**
     *  秒杀提交方法
     *  1.获得Redis库存,如果有库存减库存,没库存给提示
     *  2.收集各方面信息,形成订单对象,利用Dubbo将订单信息提交到oms_order等表里
     *  3.发送消息队列,将订单相关信息发送给RabbitMQ后续再处理
     */
    @Override
    public SeckillCommitVO commitSeckill(SeckillOrderAddDTO seckillOrderAddDTO) {
        // 第一阶段:检查重复购买和库存数
        // 获得用户秒杀购买的skuId
        Long skuId=seckillOrderAddDTO.getSeckillOrderItemAddDTO().getSkuId();
        // 获得秒杀商品的用户id
        Long userId=getUserId();
        // 一个用户id和一个skuId就能确定一个用户买了什么商品
        // 秒杀业务要求一个用户只能购买一次一个skuId
        // 我们会将用户id和skuId的购买关系保存在Redis中,以便判断是否重复购买
        // 声明重复秒杀的判断key
        String reSeckillKey= SeckillCacheUtils.getReseckillCheckKey(skuId,userId);
        // 向当前保存购买关系的key中使用自增方法
        // 如果redis中没有这个key,默认情况下,自增后结果为1返回给seckillTimes
        // 但是如果之前已经出现过key,再自增的话至少为2,返回给seckillTimes
        Long seckillTimes=stringRedisTemplate.boundValueOps(reSeckillKey).increment();
        // 这里就可以判断这个key对应的Redis的值(seckillTimes)是否>1,如果>1,证明之前购买过
        if(seckillTimes>10000){
            //如果seckillTimes>1,抛出异常,提示已经购买过
            throw new CoolSharkServiceException(ResponseCode.FORBIDDEN,"一个用户只能购买一种商品一次");
        }
        // 没有进if表示是第一次购买,进入减库存环节
        // 先获得当前库存的值,利用SkuId,获取对应的Key,获得库存数
        String seckillSkuKey=SeckillCacheUtils.getStockKey(skuId);
        // 从Redis中获得库存数
        // decrement方法和上面increment方法相反,会返回当前库存减一之后的值
        Long leftStock=stringRedisTemplate.boundValueOps(seckillSkuKey).decrement(1);
        // leftStock是当前用户购买秒杀商品之后,该商品剩余的库存
        // 如果该值小于0,表示当前用户购买是已经无货
        if(leftStock<0){
            throw new CoolSharkServiceException(
                            ResponseCode.NOT_ACCEPTABLE,"您选购的商品已经无货");
        }
        // 用户第一次购买的同时,还有货
        // 第二阶段:开始生成订单
        // 我们现在业务逻辑层获得的参数是SeckillOrderAddDTO类型
        // 能够利用Dubbo新增到order模块的订单类型是OrderAddDTO
        // 它们类型不同,而且属性结果不完全一致,需要进行针对性的转换,
        // 而这个转换过程我们可以编写一个方法完成
        OrderAddDTO orderAddDTO=convertSeckillOrderToOrder(seckillOrderAddDTO);
        // 转换过程中,没有userId数据,需要转换后手动赋值
        orderAddDTO.setUserId(userId);
        // Dubbo调用order业务逻辑层方法,完成订单的新增
        OrderAddVO orderAddVO=dubboOrderService.addOrder(orderAddDTO);
        // 到此为止新增订单完成
        // 进入第三阶段: rabbitMQ消息队列的发送
        // 我们要通过发送RabbitMQ消息的方式完成Success对象保存到数据库的功能
        // 简单来说我们只需要将Success对象赋上值,然后将它发送到消息队列中就完成任务了
        Success success=new Success();
        // SeckillOrderItemAddDTO和Success有比较多的同名属性,可以赋值
        BeanUtils.copyProperties(
                seckillOrderAddDTO.getSeckillOrderItemAddDTO(),success);
        // 还缺少一些主要属性(实际开发可以根据需求再添加)
        success.setUserId(userId);
        success.setOrderSn(orderAddVO.getSn());
        // 利用RabbitTemplate将success对象发送到RabbitMQ中,等待消息队列处理
        rabbitTemplate.convertAndSend(RabbitMqComponentConfiguration.SECKILL_EX,
                RabbitMqComponentConfiguration.SECKILL_RK,success);
        // 声明最终返回类型SeckillCommitVO
        SeckillCommitVO seckillCommitVO=new SeckillCommitVO();
        // 它的属性和新增订单的返回值OrderAddVO完全一致,直接赋值即可
        BeanUtils.copyProperties(orderAddVO,seckillCommitVO);
        // 别忘了返回
        return seckillCommitVO;
    }

    private OrderAddDTO convertSeckillOrderToOrder(SeckillOrderAddDTO seckillOrderAddDTO) {
        // 实例化返回值对象
        OrderAddDTO orderAddDTO=new OrderAddDTO();
        // 先将参数seckillOrderAddDTO中所有的属性赋值到orderAddDTO同名属性中
        BeanUtils.copyProperties(seckillOrderAddDTO,orderAddDTO);
        // seckillOrderAddDTO中还包含订单商品详情的对象SeckillOrderItemAddDTO
        // 我们要将SeckillOrderItemAddDTO赋值给常规的订单商品详情类型orderItemAddDTO
        // 所以实例化OrderItemAddDTO对象
        OrderItemAddDTO orderItemAddDTO=new OrderItemAddDTO();
        // 将SeckillOrderItemAddDTO对象的同名属性赋值给orderItemAddDTO
        BeanUtils.copyProperties(seckillOrderAddDTO.getSeckillOrderItemAddDTO(),
                                    orderItemAddDTO);
        // 又因为常规订单对象OrderAddDTO中包含的订单详情是个集合
        // 所以实例化一个集合
        List<OrderItemAddDTO> orderItemAddDTOs=new ArrayList<>();
        // 将已经赋值完成的orderItemAddDTO对象添加到集合中
        orderItemAddDTOs.add(orderItemAddDTO);
        // 将集合赋值到常规订单对象orderAddDTO中
        orderAddDTO.setOrderItems(orderItemAddDTOs);
        // 最终返回常规订单对象(转换完成)
        return orderAddDTO;
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
