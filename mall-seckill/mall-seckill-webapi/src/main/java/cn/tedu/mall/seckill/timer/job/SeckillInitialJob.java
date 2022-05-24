package cn.tedu.mall.seckill.timer.job;

import cn.tedu.mall.pojo.seckill.model.SeckillSku;
import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import cn.tedu.mall.seckill.mapper.SeckillSkuMapper;
import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SeckillInitialJob implements Job {
    // 对Redis的操作
    @Autowired
    private RedisTemplate redisTemplate;
    // 上面的RedisTemplate是保存对象使用的,它内部会将对象序列化,将二进制的对象保存到Redis中
    // 二进制数据无法完成数值增减操作,不满足我们对库存操作的要求
    // 所有再声明一个以字符串格式操作数据库保存在Redis的对象
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SeckillSkuMapper seckillSkuMapper;
    @Autowired
    private SeckillSpuMapper seckillSpuMapper;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 当前方法运行目标:
        // 找到即将开始秒杀的spu商品列表
        // 保存秒杀商品的库存到Redis
        // 保存秒杀商品的随机码到Redis
        // 创建一个5分钟之后的时间
        LocalDateTime time=LocalDateTime.now().plusMinutes(5);
        // 查询5分钟后所有秒杀商品的信息
        List<SeckillSpu> seckillSpus=seckillSpuMapper.selectSeckillSpusInTime(time);
        // 遍历这个集合进行后续操作
        for(SeckillSpu seckillSpu : seckillSpus){
            // 根据spuId查询sku列表
            Long spuId=seckillSpu.getSpuId();
            List<SeckillSku> seckillSkus=
                    seckillSkuMapper.selectSeckillSkusBySpuId(spuId);
            // 遍历当前spu所有sku列表
            for(SeckillSku seckillSku: seckillSkus){
                log.info("开始为"+seckillSku.getSkuId()+"商品的库存保存到Redis");
                // 获得保存到Redis的key
                // "seckill:sku:id:stock:100"  "8"
                // 上面的key名称不好记忆,我们使用SeckillCacheUtils类来简化封装
                // 获得当前sku对应的库存的key
                String seckillSkuStockKey=
                        SeckillCacheUtils.getStockKey(seckillSku.getSkuId());
                if(redisTemplate.hasKey(seckillSkuStockKey)){
                    // 如果redis中已经有这个key,表达式当前不是第一次运行缓存这个sku了,什么都不做
                    log.info(seckillSku.getSkuId()+"商品已经缓存到Redis中");
                }else{
                    // 利用stringRedisTemplate保存库存数,方便减少
                    stringRedisTemplate.boundValueOps(seckillSkuStockKey)
                            .set(seckillSku.getSeckillStock()+"",1, TimeUnit.DAYS);
                    log.info("库存不存在,为{}商品创建库存",seckillSku.getSkuId());
                }
            }
            // 为Spu生成一个随机码
            // 如果一个请求要购买秒杀商品,必须知道购买这个商品的路径,我们后端必须做验证
            // 在秒杀时间段内才能购买,如果非秒杀时间段,有大量请求访问
            // 我们也要逐一判断商品是否在秒杀时间段内,拖慢服务器性能
            // 解决方案是,我们请求商品的路径不使用id,而使用随机生成的一个数字(随机码)
            // 例如:seckill/spu/64571 才能访问spu商品详情
            // 先获取spu对应的随机码key
            String randomCodeKey=SeckillCacheUtils.getRandCodeKey(spuId);
            // 同样判断随机码是否已经生成过
            if(!redisTemplate.hasKey(randomCodeKey)){
                // 如果redis中,没有当前spu对应的随机码,就需要生成一个
                int randCode= RandomUtils.nextInt(900000)+100000;
                log.info("spuId为{}的商品随机码生成为{}",spuId,randCode);
                // 随机码保存到Redis
                redisTemplate.boundValueOps(randomCodeKey).set(randCode,1,TimeUnit.DAYS);
            }
        }
        String randomCodeKey=SeckillCacheUtils.getRandCodeKey(2L);
        System.out.println(redisTemplate.boundValueOps(randomCodeKey).get()+"-------------------------------------");

    }
}
