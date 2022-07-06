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

    @Override
    public SeckillSpuVO getSeckillSpu(Long spuId) {
        return null;
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
