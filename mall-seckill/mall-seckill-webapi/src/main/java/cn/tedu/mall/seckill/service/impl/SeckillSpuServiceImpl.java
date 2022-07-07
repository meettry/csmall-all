package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.pojo.product.vo.SpuDetailStandardVO;
import cn.tedu.mall.pojo.product.vo.SpuStandardVO;
import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuDetailSimpleVO;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuVO;
import cn.tedu.mall.product.service.seckill.IForSeckillSpuService;
import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.service.ISeckillSpuService;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bouncycastle.pqc.math.linearalgebra.RandUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SeckillSpuServiceImpl implements ISeckillSpuService {

    // 查询秒杀spu表的数据
    @Autowired
    private SeckillSpuMapper seckillSpuMapper;
    // 秒杀spu表中没有商品详细介绍,需要根据spuid借助Dubbo查询product模块
    // pms数据库中的spu表,获得商品信息
    @DubboReference
    private IForSeckillSpuService dubboSeckillSpuService;

    @Override
    public JsonPage<SeckillSpuVO> listSeckillSpus(Integer page, Integer pageSize) {
        // 分页查询秒杀表中spu信息
        PageHelper.startPage(page,pageSize);
        List<SeckillSpu> seckillSpus=seckillSpuMapper.findSeckillSpus();
        // 我们返回给页面的,应该是包含商品详细信息的对象,不能只是SeckillSpu中的信息
        // 业务逻辑层返回值泛型类型SeckillSpuVO中包含秒杀信息和商品详细信息,返回它的集合可以满足查询需要
        List<SeckillSpuVO> seckillSpuVOs=new ArrayList<>();
        // 循环遍历秒杀列表,根据秒杀商品列表中元素的spuId查询spu详情
        for(SeckillSpu seckillSpu: seckillSpus){
            // 获得SpuId
            Long spuId=seckillSpu.getSpuId();
            // dubbo调用查询商品详情
            SpuStandardVO spuStandardVO=dubboSeckillSpuService.getSpuById(spuId);
            // 实例化包含秒杀信息和商品信息的对象
            SeckillSpuVO seckillSpuVO=new SeckillSpuVO();
            // 将商品详情信息赋值给同名属性
            BeanUtils.copyProperties(spuStandardVO,seckillSpuVO);
            // 为了防止已有的值被意外覆盖,我们剩下的属性单独赋值
            // 赋值秒杀价
            seckillSpuVO.setSeckillListPrice(seckillSpu.getListPrice());
            // 赋值秒杀的开始时间和结束时间
            seckillSpuVO.setStartTime(seckillSpu.getStartTime());
            seckillSpuVO.setEndTime(seckillSpu.getEndTime());
            // 将包含商品详情和秒杀信息的seckillSpuVO对象保存在循环前定义的集合中
            seckillSpuVOs.add(seckillSpuVO);
        }
        // 翻页返回查询结果
        return JsonPage.restPage(new PageInfo<>(seckillSpuVOs));
    }
    // 根据SpuId查询Spu详情
    @Override
    public SeckillSpuVO getSeckillSpu(Long spuId) {
        // 先判断参数spuId是否在布隆过滤器中
        // 如果不在直接返回/抛出异常(后期会实现)

        // 声明一个返回值用于返回
        SeckillSpuVO seckillSpuVO=null;
        // 获取当前SpuId对应的Redis的Key
        // mall:seckill:spu:vo:1
        String seckillSpuKey= SeckillCacheUtils.getSeckillSpuVOKey(spuId);
        // 执行判断这个Key是否已经保存在Redis中
        if(redisTemplate.hasKey(seckillSpuKey)){
            // 如果在redis中,直接获取
            seckillSpuVO=(SeckillSpuVO)redisTemplate
                            .boundValueOps(seckillSpuKey).get();
        }else{
            // 如果Redis中没有个这个Key
            // 先按spuId查询秒杀spu信息
            SeckillSpu seckillSpu=seckillSpuMapper.findSeckillSpuById(spuId);
            // 当前商品是否存在
            if(seckillSpu==null){
                throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,
                        "您访问的商品不存在");
            }
            // 上面SeckillSpu对象只有秒杀信息,没有商品信息
            // 商品信息需要根据Dubbo来查询pms数据库
            SpuStandardVO spuStandardVO=dubboSeckillSpuService
                                                    .getSpuById(spuId);
            // 将商品详情赋值给seckillSpuVO,先实例化
            seckillSpuVO=new SeckillSpuVO();
            BeanUtils.copyProperties(spuStandardVO,seckillSpuVO);
            // 秒杀信息手动赋值
            seckillSpuVO.setSeckillListPrice(seckillSpu.getListPrice());
            seckillSpuVO.setStartTime(seckillSpu.getStartTime());
            seckillSpuVO.setEndTime(seckillSpu.getEndTime());
            // 查询出的对象保存在Redis中
            redisTemplate.boundValueOps(seckillSpuKey)
                    .set(seckillSpuVO,60*60*24*1000+
                            RandomUtils.nextInt(1000*60*60*2),
                            TimeUnit.MILLISECONDS);
        }
        // 判断当前Spu是否在秒杀时间段内
        // 获得当前时间
        LocalDateTime nowTime= LocalDateTime.now();
        // 判断当前时间是否在秒杀开始之后
        // between比较两个时间参数
        // 前大后小返回negative
        Duration afterTime=Duration.between(nowTime,seckillSpuVO.getStartTime());
        // 判断当前时间是否在秒杀结束之前
        Duration beforeTime=Duration.between(seckillSpuVO.getEndTime(),nowTime);
        // 判断两个Duration是否同时为"negative",
        // 如果是,表示当前时间确实再当前spu商品的秒杀时间段内
        if(afterTime.isNegative() && beforeTime.isNegative()){
            //从Redis中获得随机码赋值给seckillSpuVO的url属性
            String randCodeKey=SeckillCacheUtils.getRandCodeKey(spuId);
            seckillSpuVO.setUrl("/seckill/"+redisTemplate
                                .boundValueOps(randCodeKey).get());
        }
        // 别忘了返回
        // 前端根据seckillSpuVO对象的url属性是否为null判断是否可以进行秒杀购买操作
        // 如果为空,提示无法购买,如果有值并赋值了随机码,就可以进行下一步提交操作
        return seckillSpuVO;
    }







    // 常量类中每没有定义Detail用的常量Key,我们声明一个
    public static final String
            SECKILL_SPU_DETAIL_VO_PREFIX="seckill:spu:detail:vo:";
    @Autowired
    private RedisTemplate redisTemplate;
    // 根据SpuId查询detail详细信息
    @Override
    public SeckillSpuDetailSimpleVO getSeckillSpuDetail(Long spuId) {
        // 将spuId拼接在常量后返回
        String seckillDetailKey=SECKILL_SPU_DETAIL_VO_PREFIX+spuId;
        // 声明返回值类型对象,初值为null即可
        SeckillSpuDetailSimpleVO seckillSpuDetailSimpleVO=null;
        // 判断Redis中是否已经有这个Key
        if(redisTemplate.hasKey(seckillDetailKey)){
            // 如果Redis中已经有这个key,就获得这个key的值赋值给上面声明的实体类
            seckillSpuDetailSimpleVO=(SeckillSpuDetailSimpleVO)redisTemplate
                    .boundValueOps(seckillDetailKey).get();
        }else{
            // 如果Redis中没有这个key,我们就需要从数据库中查询后新增到Redis中
            // dubbo调用根据spuId查询detail的方法
            SpuDetailStandardVO spuDetailStandardVO=
                    dubboSeckillSpuService.getSpuDetailById(spuId);
            // 判断当前SpuId查询的对象不能为空
            if(spuDetailStandardVO==null){
                // 可以将null保存到Redis中
                // 抛出异常
                throw new CoolSharkServiceException(
                        ResponseCode.NOT_FOUND,"您查找的商品不存在");
            }
            // 确认商品存在实例化返回值类型的对象
            seckillSpuDetailSimpleVO=new SeckillSpuDetailSimpleVO();
            // 同名属性赋值
            BeanUtils.copyProperties(spuDetailStandardVO,seckillSpuDetailSimpleVO);
            // 保存到Redis中
            redisTemplate.boundValueOps(seckillDetailKey)
                    .set(seckillSpuDetailSimpleVO,
                            60*60*24+ RandomUtils.nextInt(60*60*2),
                            TimeUnit.SECONDS);
        }
        // 返回查询出的对象
        return seckillSpuDetailSimpleVO;
    }
}
