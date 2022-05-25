package cn.tedu.mall.seckill.consumer;

import cn.tedu.mall.pojo.seckill.model.Success;
import cn.tedu.mall.seckill.config.RabbitMqComponentConfiguration;
import cn.tedu.mall.seckill.mapper.SeckillSkuMapper;
import cn.tedu.mall.seckill.mapper.SuccessMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
// RabbitListener指定监听的队列名称
@RabbitListener(queues = {RabbitMqComponentConfiguration.SECKILL_QUEUE})
public class SeckillQueueConsumer {

    @Autowired
    private SuccessMapper successMapper;
    @Autowired
    private SeckillSkuMapper seckillSkuMapper;
    // 当监听的队列名称有数据\信息时
    // 会自动运行下面的方法,方法名无关,主要关联@RabbitHandler注解
    // 参数应该和发送消息的参数类型一致
    @RabbitHandler
    public void process(Success success){
        // 扣库存
        seckillSkuMapper.updateReduceStockBySkuId(
                success.getSkuId(),success.getQuantity());
        // 新增Success
        successMapper.saveSuccess(success);
    }
}
