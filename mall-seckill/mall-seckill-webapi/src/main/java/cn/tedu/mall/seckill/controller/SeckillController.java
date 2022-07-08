package cn.tedu.mall.seckill.controller;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.pojo.seckill.dto.SeckillOrderAddDTO;
import cn.tedu.mall.pojo.seckill.vo.SeckillCommitVO;
import cn.tedu.mall.seckill.exception.SeckillBlockHandler;
import cn.tedu.mall.seckill.exception.SeckillFallBack;
import cn.tedu.mall.seckill.service.ISeckillService;
import cn.tedu.mall.seckill.utils.SeckillCacheUtils;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill")
@Api(tags = "提交秒杀模块")
public class SeckillController {

    @Autowired
    private ISeckillService seckillService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/{randCode}")
    @ApiOperation("验证随机码并提交秒杀订单")
    @ApiImplicitParam(value = "随机码",name = "randCode",required = true,
            dataType = "string")
    @PreAuthorize("hasRole('ROLE_user')")
    // Sentinel限流和降级的配置
    @SentinelResource(value = "seckill",
    blockHandlerClass = SeckillBlockHandler.class,blockHandler = "seckillBlock",
    fallbackClass = SeckillFallBack.class,fallback = "seckillFall")
    public JsonResult<SeckillCommitVO> commitSeckill(
         @PathVariable String randCode, SeckillOrderAddDTO seckillOrderAddDTO){
        // 先获取spuId
        Long spuId=seckillOrderAddDTO.getSpuId();
        // 确定spuId对应随机码的key
        String randCodeKey= SeckillCacheUtils.getRandCodeKey(spuId);
        // 判断Redis中是否有randCodeKey
        if(redisTemplate.hasKey(randCodeKey)){
            // 如果有这个Key 判断随机码是否正确
            // 获取随机码
            String redisRandCode=redisTemplate
                                .boundValueOps(randCodeKey).get()+"";
            //判断redisRandCode是否为null
            if (redisRandCode==null){
                // Redis中随机码丢失,服务器内部错误
                throw new CoolSharkServiceException(
                        ResponseCode.INTERNAL_SERVER_ERROR,
                        "服务器内部异常,请联系管理员");
            }
            // 判断Redis的随机码和参数随机码是否一致,防止投机购买
            if(!redisRandCode.equals(randCode)){
                // 如果不一致,认为是投机购买,抛出异常
                throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,
                        "没有这个商品");
            }
            // 调用业务逻辑层提交订单
            SeckillCommitVO seckillCommitVO=
                    seckillService.commitSeckill(seckillOrderAddDTO);
            return JsonResult.ok(seckillCommitVO);
        }else{
            // 没有Key对应随机码,秒杀列表中没有这个商品
            throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,
                    "没有指定商品");
        }
    }


}

