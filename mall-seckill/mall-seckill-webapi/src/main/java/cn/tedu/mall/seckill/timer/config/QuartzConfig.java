package cn.tedu.mall.seckill.timer.config;

import cn.tedu.mall.seckill.timer.job.SeckillInitialJob;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.recycler.Recycler;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class QuartzConfig {

    // 声明JobDetail保存到Spring容器
    @Bean
    public JobDetail initJobDetail(){
        log.info("预热任务绑定!");
        return JobBuilder.newJob(SeckillInitialJob.class)
                .withIdentity("initSeckill")
                .storeDurably()
                .build();
    }

    // 定义Quartz的触发,保存到Spring容器
    @Bean
    public Trigger initSeckillTrigger(){
        log.info("预热触发器运行");
        // 学习过程中,每分钟加载一次方便测试,实际开发时,根据业务编写正确Cron表达式即可
        // Cron表达式
        CronScheduleBuilder cronScheduleBuilder=
                CronScheduleBuilder.cronSchedule("0 0/1 * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(initJobDetail())
                .withIdentity("initSeckillTrigger")
                .withSchedule(cronScheduleBuilder)
                .build();
    }




}
