package cn.tedu.mall.seckill.timer.job;

import cn.tedu.mall.pojo.seckill.model.SeckillSku;
import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import cn.tedu.mall.seckill.mapper.SeckillSkuMapper;
import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SeckillInitialJob implements Job {

    @Autowired
    private SeckillSpuMapper spuMapper;
    @Autowired
    private SeckillSkuMapper skuMapper;
    // 查询出来的数据要保存到Redis中
    @Autowired
    private RedisTemplate redisTemplate;

    // 上面的RedisTemplate对象是向Redis中保存对象用的,内部会将数据序列化后,
    // 以二进制的格式保存在Redis中,读写速度确实快,但是数据无法修改
    // 这种设计无法在Redis内部完成对象属性或值的修改
    // 我们的库存是一个数字,Redis支持直接在Redis内部对数字进行增减,减少java操作
    // 而前提是必须保存字符串格式数据,而不是二进制格式
    // 我们需要声明一个用字符串操作Redis的对象
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //我们做的是预热,在秒杀还没有开始的时候,将要开始参与秒杀的商品库存保存到redis
        // 这个方法运行时,距离下次秒杀开始还有5分钟
        // 所以我们创建一个5分钟之后的时间,查询5分钟后要参与秒杀的商品
        LocalDateTime time=LocalDateTime.now().plusMinutes(5);
        // 查询这个时间的所有秒杀商品
        List<SeckillSpu> seckillSpus=spuMapper.findSeckillSpusByTime(time);
        // 遍历seckillSpus(查询到的所有秒杀商品)
        // 目标是将这些商品对应的sku库存保存到Redis
        // 为了方便随机数生成定一个对象
        Random ran=new Random();
        for(SeckillSpu spu:seckillSpus){
            // spu 是一个商品品类,库存不在spu中,而是sku保存库存
            // 所以要根据spuId查询Sku集合
            List<SeckillSku> seckillSkus=skuMapper
                        .findSeckillSkusBySpuId(spu.getSpuId());
            // 遍历当前spu的所有sku列表
            for(SeckillSku sku: seckillSkus){
                log.info("开始将{}号商品的库存保存到Redis",sku.getSkuId());
                // 在编程过程中,涉及RedisKey值时,最好声明常量,如果再业务中使用大量Key值
                // 建议创建一个保存RedisKey值常量的类,SeckillCacheUtils类就是
                // mall:seckill:sku:stock:1
                //      ↑↑↑↑↑
                String skuStockKey=
                    SeckillCacheUtils.getStockKey(sku.getSkuId());
                // 检查当前Redis中是否已经包含这个Key
                if(redisTemplate.hasKey(skuStockKey)){
                    // 如果Redis已经包含了这个key(可能是前一场秒杀就包含的商品)
                    log.info("{}号商品已经在缓存中",sku.getSkuId());
                }else{
                    // 不在缓存中的,就要将库存数据保存到Redis中
                    // 利用stringRedisTemplate保存,方便减少库存数
                    stringRedisTemplate.boundValueOps(skuStockKey)
                            .set(sku.getSeckillStock()+"",
                                    60*60*4+ran.nextInt(60*30),
                                    TimeUnit.SECONDS);
                    log.info("成功为{}号商品添加库存",sku.getSkuId());
                }

            }
        }



    }
}
