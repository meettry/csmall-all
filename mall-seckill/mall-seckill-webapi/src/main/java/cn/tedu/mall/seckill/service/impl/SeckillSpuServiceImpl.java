package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.pojo.product.vo.SpuDetailStandardVO;
import cn.tedu.mall.pojo.product.vo.SpuStandardVO;
import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuDetailSimpleVO;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuVO;
import cn.tedu.mall.product.service.seckill.IForSeckillSkuService;
import cn.tedu.mall.product.service.seckill.IForSeckillSpuService;
import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.service.ISeckillSpuService;
import cn.tedu.mall.seckill.utils.RedisBloomUtils;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SeckillSpuServiceImpl implements ISeckillSpuService {

    @Autowired
    private SeckillSpuMapper seckillSpuMapper;
    // 利用product模块根据spuid查询spu信息的方法,保证返回的spu列表包含详细信息
    @DubboReference
    private IForSeckillSpuService dubboSeckillSpuService;

    @Override
    public JsonPage<SeckillSpuVO> listSeckillSpus(Integer page, Integer pageSize) {
        // 查询分页结果,使用PageHelper设置分页查询内容
        PageHelper.startPage(page,pageSize);
        List<SeckillSpu> seckillSpus=seckillSpuMapper.selectSeckillSpus();
        // 下面进行数据封装
        // 准备一个包含更详细信息返回值类型的泛型集合
        List<SeckillSpuVO> seckillSpuVOs=new ArrayList<>();
        for(SeckillSpu seckillSpu: seckillSpus){
            // 根据spuid查询到spu详情
            Long spuId=seckillSpu.getSpuId();
            // 根据spuId 查询Spu详情(Dubbo调用)
            SpuStandardVO spuStandardVO=
                dubboSeckillSpuService.getSpuById(spuId);
            // 实例化包含spu详情和秒杀详情的对象
            SeckillSpuVO seckillSpuVO=new SeckillSpuVO();
            // 将spu详情赋值给SeckillSpuVO对象
            BeanUtils.copyProperties(spuStandardVO,seckillSpuVO);
            // 取出秒杀价
            BigDecimal seckillListPrice=seckillSpu.getListPrice();
            // 向seckillSpuVO中赋值秒杀相关的属性,秒杀价,秒杀开始时间,秒杀结束时间
            seckillSpuVO.setSeckillListPrice(seckillListPrice);
            seckillSpuVO.setStartTime(seckillSpu.getStartTime());
            seckillSpuVO.setEndTime(seckillSpu.getEndTime());
            // 将既有秒杀信息,又有spu详情的seckillSpuVO添加到集合中
            seckillSpuVOs.add(seckillSpuVO);
        }
        // 将分页查询结果PageInfo转换为JsonPage返回
        return JsonPage.restPage(new PageInfo<>(seckillSpuVOs));
    }

    // 添加操作Redis的注入
    @Autowired
    private RedisTemplate redisTemplate;
    // 添加布隆过滤器的注入
    @Autowired
    private RedisBloomUtils redisBloomUtils;

    // 根据SpuId查询Spu信息,需要考虑各种秒杀业务特征
    @Override
    public SeckillSpuVO getSeckillSpu(Long spuId) {
        // 获取当前批次的布隆过滤器的key
        String bloomFilterKey= SeckillCacheUtils.getBloomFilterKey(LocalDate.now());
        log.info("当前批次布隆过滤器的key值为:{}",bloomFilterKey);
        // 用户要查询的SpuId是否在布隆过滤器中
        boolean exists=redisBloomUtils.bfexists(bloomFilterKey,spuId+"");
        if(!exists){
            // 如果布隆过滤器中没有要查询的spuId
            throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,"您访问的商品不存在");
        }
        // 布隆过滤器判断spu数据存在,准备查询
        // 实例化SeckillSpuVO用于最终返回
        SeckillSpuVO seckillSpuVO=null;
        // 判断Redis中是否包含SpuId为当前Id的seckillSpuVO对象
        // 先确定SpuId对应的Key
        String seckillSpuVOKey=SeckillCacheUtils.getSeckillSpuVOKey(spuId);
        // "mall:seckill:spu:vo:2"
        // 执行判断
        if(redisTemplate.hasKey(seckillSpuVOKey)){
            // 如果Redis中包含当前SpuId的对象,直接返回即可
            seckillSpuVO=(SeckillSpuVO) redisTemplate.boundValueOps(seckillSpuVOKey).get();
        }else{
            // 如果Redis中没有这个SpuId对应的对象,就需要到数据库查询然后保存到Redis
            // 先查询秒杀spu表中基本信息
            SeckillSpu seckillSpu=seckillSpuMapper.selectSeckillSpuBySpuId(spuId);
            // 因为布隆过滤器判断有的数据不一定有,所以在这里判断查询结果是否为空
            if(seckillSpu==null){
                // 进入这个if,表示真的是误判数据,抛出异常
                throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,
                        "您访问的商品不存在(布隆过滤器误判)");
            }
            // 根据dubbo查询pms表中当前spuId的常规信息
            SpuStandardVO spuStandardVO=dubboSeckillSpuService.getSpuById(spuId);
            // 实例化seckillSpuVO对象
            seckillSpuVO=new SeckillSpuVO();
            // 开始向SeckillSpuVO对象中赋值,它包含秒杀spu和常规spu的所有信息
            // 常规Spu赋值
            BeanUtils.copyProperties(spuStandardVO,seckillSpuVO);
            // 给SeckillSpuVO赋值秒杀相关属性值
            seckillSpuVO.setSeckillListPrice(seckillSpu.getListPrice());
            seckillSpuVO.setStartTime(seckillSpu.getStartTime());
            seckillSpuVO.setEndTime(seckillSpu.getEndTime());
            // seckillSpuVO信息全了 下面要保存到Redis中,以便再查询这个spu直接从Redis中获取
            redisTemplate.boundValueOps(seckillSpuVOKey).set(seckillSpuVO,
                    1000*60*60*72+ RandomUtils.nextInt(1000*60*60*2), TimeUnit.MILLISECONDS);
        }
        // 判断当前时间是否在这个商品的秒杀时间内
        // 获得当前时间
        LocalDateTime nowTime=LocalDateTime.now();
        // 判断当前时间是否在开始时间之后
        Duration afterStart=Duration.between(nowTime,seckillSpuVO.getStartTime());
        // 判断结束时间是否在当前时间之后
        Duration beforeEnd=Duration.between(seckillSpuVO.getEndTime(),nowTime);
        if(afterStart.isNegative() && beforeEnd.isNegative()){
            // 如果当前商品确实在秒杀时间段内
            // 我们将Redis中该商品对应的随机码添加到seckillSpuVO的url属性里
            String randCodeKey=SeckillCacheUtils.getRandCodeKey(spuId);
            seckillSpuVO.setUrl(
                    "/seckill/"+redisTemplate.boundValueOps(randCodeKey).get());
        }
        // 别忘了返回
        return seckillSpuVO;
    }

    // 定义Redis中保存SpuDetail的key前缀
    private static final String SECKILL_SPU_DETAIL_VO_PREFIX="seckill:spu:detail:vo:";
    // 根据SpuId查询SpuDetail详情
    @Override
    public SeckillSpuDetailSimpleVO getSeckillSpuDetail(Long spuId) {
        // 先获得当前SpuId对应detal详情的key
        String seckillSpuDetailVOKey=SECKILL_SPU_DETAIL_VO_PREFIX+spuId;
        // 声明SeckillSpuDetailSimpleVO类型对象,赋值null
        SeckillSpuDetailSimpleVO seckillSpuDetailSimpleVO=null;
        // 判断Redis中是否包含这个key
        if(redisTemplate.hasKey(seckillSpuDetailVOKey)){
            seckillSpuDetailSimpleVO=(SeckillSpuDetailSimpleVO)redisTemplate
                    .boundValueOps(seckillSpuDetailVOKey).get();
        }else{
            // Redis中没有这个数据,就要到数据库中查询
            // 借助Dubbo提供的方法到pms数据库中按spuId查询spu_detail
            SpuDetailStandardVO spuDetailStandardVO=
                dubboSeckillSpuService.getSpuDetailById(spuId);
            // 判断spuId查询出的对象不能为空
            if(spuDetailStandardVO==null){
                throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,"您访问的商品不存在");
            }
            // 商品存在,开始进行数据封装,先实例化对象
            seckillSpuDetailSimpleVO=new SeckillSpuDetailSimpleVO();
            // 相同属性赋值
            BeanUtils.copyProperties(spuDetailStandardVO,seckillSpuDetailSimpleVO);
            // 将查询出的detail数据保存到Redis,以便后面访问
            redisTemplate.boundValueOps(seckillSpuDetailVOKey).set(
                    seckillSpuDetailSimpleVO,
                    1000*60*60*72+RandomUtils.nextInt(1000*60*60*2),
                    TimeUnit.MILLISECONDS);
        }
        return seckillSpuDetailSimpleVO;
    }
}
