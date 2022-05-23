package cn.tedu.mall.seckill.timer.job;

import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.utils.RedisBloomUtils;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
public class SeckillBloomInitialJob implements Job {

    @Autowired
    private RedisBloomUtils redisBloomUtils;
    @Autowired
    private SeckillSpuMapper seckillSpuMapper;

    // 将我们商城中秒杀商品的数据,保存在布隆过滤器中
    // 保存两批,保证布隆过滤器在实际运行过程中,没有空档期
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 我们设计的秒杀是每天一批,所有按照日期做key就可以
        String bloomTodayKey= SeckillCacheUtils.getBloomFilterKey(LocalDate.now());
        String bloomTomorrowKey=
                SeckillCacheUtils.getBloomFilterKey(LocalDate.now().plusDays(1));
        // 实际开发按秒杀时间去查询此批次的spuId集合
        // 学习过程中,因为无法频繁修改数据库中秒杀开始结束时间的数据,我们只能将所有spuId保存在布隆过滤器中
        Long[] spuIds=seckillSpuMapper.selectAllSeckillSpuIds();
        // 创建要保存到布隆过滤器的数组
        String[] spuIdStrs=new String[spuIds.length];
        // 遍历spuIds,将id值转换为字符串赋值到spuIdStrs
        for(int i=0;i<spuIds.length;i++){
            spuIdStrs[i]=spuIds[i]+"";
        }
        redisBloomUtils.bfmadd(bloomTodayKey,spuIdStrs);
        redisBloomUtils.bfmadd(bloomTomorrowKey,spuIdStrs);
        log.info("将两个批次的spuId信息保存到布隆过滤器中");

    }
}
