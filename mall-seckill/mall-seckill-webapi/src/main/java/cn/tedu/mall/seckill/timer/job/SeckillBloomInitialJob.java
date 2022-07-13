package cn.tedu.mall.seckill.timer.job;

import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.utils.RedisBloomUtils;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class SeckillBloomInitialJob implements Job {

    @Autowired
    private RedisBloomUtils redisBloomUtils;
    @Autowired
    private SeckillSpuMapper seckillSpuMapper;

    // 我们设计加载两批秒杀商品,防止瞬间的空档期
    // 也是能够让用户查询下一批秒杀商品的需求
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 先确定两个批次的Key
        // 我们的设计是每天一批秒杀,所以用日期做Key就可以
        String bloomTodayKey=
             SeckillCacheUtils.getBloomFilterKey(LocalDate.now());
        String bloomTomorrowKey=
             SeckillCacheUtils.getBloomFilterKey(LocalDate.now().plusDays(1));
        // 实际开发中按秒杀时间去查询此批次的SpuId集合
        // 但是学习过程中,因为无法频繁修改数据库秒杀开始结束时间,
        // 所以我们将所有的spuId都保存在布隆过滤器里
        // 查询当前秒杀表中所有信息
        Long[] spuIds=seckillSpuMapper.findAllSeckillSpuIds();
        // 创建布隆过滤器(将Long[]转换成String[]再添加到布隆过滤器中)
        // 创建长度相同的String数组
        String[] spuIdStrs=new String[spuIds.length];
        // 将spuIds中的值赋值到spuIdStrs
        for(int i=0;i<spuIdStrs.length;i++){
            spuIdStrs[i]=spuIds[i]+"";
        }
        // 保存到布隆过滤器中
        // 实际开发保存的是当前Key对应的批次的
        redisBloomUtils.bfmadd(bloomTodayKey,spuIdStrs);
        redisBloomUtils.bfmadd(bloomTomorrowKey,spuIdStrs);
        System.out.println("两个批次的布隆过滤器加载完成");

    }
}
