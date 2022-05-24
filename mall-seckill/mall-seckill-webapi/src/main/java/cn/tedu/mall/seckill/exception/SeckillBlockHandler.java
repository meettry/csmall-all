package cn.tedu.mall.seckill.exception;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.pojo.seckill.dto.SeckillOrderAddDTO;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.spring.web.json.Json;

// 秒杀限流类
@Slf4j
public class SeckillBlockHandler {

    // 类中声明限流的方法,返回值必须和限流的控制器方法一致
    // 参数也需要和限流的控制器方法一致,还需要额外添加一个BlockException的异常参数
    // 定义成静态的可以直接使用类名调用,不用实例化对象
    public static JsonResult seckillBlock(String randCode,
                 SeckillOrderAddDTO seckillOrderAddDTO, BlockException e){
        log.error("限流模块生效");
        return JsonResult.failed(ResponseCode.INTERNAL_SERVER_ERROR,"服务器忙");
    }



}
