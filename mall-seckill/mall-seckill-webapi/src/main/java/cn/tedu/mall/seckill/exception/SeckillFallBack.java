package cn.tedu.mall.seckill.exception;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.pojo.seckill.dto.SeckillOrderAddDTO;
import lombok.extern.slf4j.Slf4j;

// Sentinel秒杀降级方法
@Slf4j
public class SeckillFallBack {

    // 参数和返回值的要求基本和秒杀限流方法一致
    // 只是降级方法是因为控制器发生了异常才会运行的,我们使用Throwable类型类接收
    public static JsonResult seckillFall(String randCode,
              SeckillOrderAddDTO seckillOrderAddDTO,Throwable throwable){
        log.error("秒杀业务降级");
        return JsonResult.failed(ResponseCode.INTERNAL_SERVER_ERROR,
                throwable.getMessage());
    }


}
