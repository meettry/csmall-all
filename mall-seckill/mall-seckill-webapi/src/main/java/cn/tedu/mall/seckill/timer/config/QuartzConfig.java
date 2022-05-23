package cn.tedu.mall.seckill.timer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class QuartzConfig {
    // 两个触发,一个是触发缓存预热(保存库存和随机码)
    // 另一个是布隆过滤器的创建

    // 实际开发中,布隆过滤器可以按批次创建,我们为了看到运行效果,还是1分钟一次
    // 缓存预热是查询5分钟之后要秒杀的商品,我们也是每分钟运行一次


}
