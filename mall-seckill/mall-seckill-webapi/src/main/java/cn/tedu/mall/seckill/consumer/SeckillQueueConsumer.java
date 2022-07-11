package cn.tedu.mall.seckill.consumer;

import cn.tedu.mall.pojo.seckill.model.Success;
import cn.tedu.mall.seckill.config.RabbitMqComponentConfiguration;
import cn.tedu.mall.seckill.mapper.SeckillSkuMapper;
import cn.tedu.mall.seckill.mapper.SuccessMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 必须交由Spring容器管理
@Component
// RabbitMQ监听器声明
@RabbitListener(queues = {RabbitMqComponentConfiguration.SECKILL_QUEUE})
public class SeckillQueueConsumer {

    // 将业务需要的对象都装配过来
    @Autowired
    private SuccessMapper successMapper;
    @Autowired
    private SeckillSkuMapper skuMapper;
    // 编写监听队列调用的方法
    // 保证方法的参数和发送时参数类型一致
    @RabbitHandler
    public void process(Success success){
        // 扣库存
        // 扣库存操作不会在并发高时和数据库同步,只会在服务器较闲时,影响数据库
        // 真正的实时库存保存在Redis中
        skuMapper.updateReduceStockBySkuId(success.getSkuId(),
                                            success.getQuantity());
        // 新增success
        successMapper.saveSuccess(success);

        // 如果上面方法有失败情况
        // 需要在下面发送异常消息,可能发送给秒杀模块处理
        // 也可以直接发送给死信队列,让人工处理
    }


}
