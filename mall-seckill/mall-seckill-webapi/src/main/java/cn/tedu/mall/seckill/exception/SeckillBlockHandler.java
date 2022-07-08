package cn.tedu.mall.seckill.exception;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.pojo.seckill.dto.SeckillOrderAddDTO;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

// 秒杀执行业务限流异常处理类
@Slf4j
public class SeckillBlockHandler {

    // 声明的限流方法,返回值必须和控制器方法一致
    // 参数是包含控制器方法参数前提下,额外添加BlockException异常参数
    // 这个方法我们定义为静态方法,方便调用者不实例化对象,直接用类名调用
    public static JsonResult seckillBlock(String randCode,
                 SeckillOrderAddDTO seckillOrderAddDTO, BlockException e){
        log.error("请求被限流");
        return JsonResult.failed(ResponseCode.INTERNAL_SERVER_ERROR,
                "服务器忙");
    }


}
