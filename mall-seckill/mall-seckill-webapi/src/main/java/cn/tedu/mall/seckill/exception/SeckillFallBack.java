package cn.tedu.mall.seckill.exception;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.pojo.seckill.dto.SeckillOrderAddDTO;
import lombok.extern.slf4j.Slf4j;

// Sentinel秒杀降级方法
@Slf4j
public class SeckillFallBack {

    // 返回值类型和参数列表要求和限流方法基本一致
    // 只是降级方法是因为控制器发生了异常,我们的降级方法可以接收这个异常来处理,或给出提示
    // 所有异常类型不限
    public static JsonResult seckillFall(String randCode,
           SeckillOrderAddDTO seckillOrderAddDTO,Throwable throwable){
        log.error("秒杀业务降级!");
        return JsonResult.failed(ResponseCode.INTERNAL_SERVER_ERROR,
                "服务器内部错误,请稍后再试");
    }
}
