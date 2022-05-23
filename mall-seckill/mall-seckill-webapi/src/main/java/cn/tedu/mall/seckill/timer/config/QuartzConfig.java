package cn.tedu.mall.seckill.timer.config;

import cn.tedu.mall.seckill.timer.job.SeckillBloomInitialJob;
import cn.tedu.mall.seckill.timer.job.SeckillInitialJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class QuartzConfig {
    // 两个触发,一个是触发缓存预热(保存库存和随机码)
    // 另一个是布隆过滤器的创建

    // 实际开发中,布隆过滤器可以按批次创建,我们为了看到运行效果,还是1分钟一次
    // 缓存预热是查询5分钟之后要秒杀的商品,我们也是每分钟运行一次

    @Bean
    public JobDetail seckillInitialJobDetail(){
        log.info("启动5分钟后秒杀商品的预热任务");
        return JobBuilder.newJob(SeckillInitialJob.class) // 参数是要运行的job的类反射
                .withIdentity("SeckillInitialJob") // 设置job的名字
                .storeDurably() // 不要求注册时必须关联触发器
                .build();
    }
    @Bean
    public Trigger seckillInitialJobTrigger(){
        log.info("缓存预热在测试阶段使用每分钟0秒时运行一次");
        // 创建Cron表达式对象
        CronScheduleBuilder cronScheduleBuilder=
                    CronScheduleBuilder.cronSchedule("0 0/1 * * * ?");
        // 返回触发器对象
        return TriggerBuilder.newTrigger()
                .forJob(seckillInitialJobDetail()) // Trigger绑定jobDetail对象
                .withIdentity("seckillInitialJobTrigger") // 给触发器起名字
                .withSchedule(cronScheduleBuilder) // 设置触发器Cron表达式
                .build();
    }

    // 布隆过滤器的注册
    @Bean
    public JobDetail seckillBloomFilterInitialJobDetail(){
        log.info("启动秒杀时按批次加载布隆过滤器的任务");
        return JobBuilder.newJob(SeckillBloomInitialJob.class)
                .withIdentity("SeckillBloomInitialJob")
                .storeDurably()
                .build();
    }
    // 布隆过滤器也是实际开发中按批次查询即可,测试开发过程中,仍然每分钟运行
    @Bean
    public Trigger seckillBloomFilterTrigger(){
        log.info("加载本批次和下个批次所有spuId到布隆过滤器");
        // Cron表达式根据实际需求修改即可
        CronScheduleBuilder cronScheduleBuilder=
                CronScheduleBuilder.cronSchedule("0 0/1 * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(seckillBloomFilterInitialJobDetail())
                .withIdentity("seckillBloomFilterTrigger")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
