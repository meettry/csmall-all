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
import io.swagger.annotations.ApiImplicitParams;
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
@Api(tags = "秒杀发起模块")
public class SeckillController {

    @Autowired
    private ISeckillService seckillService;
    @Autowired
    private RedisTemplate redisTemplate;

    // 验证随机码并调用秒杀生成订单的控制器方法
    @PostMapping("/{randCode}")
    @ApiOperation("验证随机码并调用秒杀生成订单的控制器方法")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "随机码",name = "randCode",required = true,dataType = "string")
    })
    // Sentinel限流和降级方法的指定
    @SentinelResource(value = "seckill",
            blockHandlerClass = SeckillBlockHandler.class,blockHandler = "seckillBlock",
            fallbackClass = SeckillFallBack.class,fallback = "seckillFall")
    @PreAuthorize("hasRole('ROLE_user')")
    public JsonResult<SeckillCommitVO> commitSeckill(
            @PathVariable String randCode, SeckillOrderAddDTO seckillOrderAddDTO){
        // 获得SpuId
        Long spuId=seckillOrderAddDTO.getSpuId();
        // 从redis中获得当前spuId对应的随机码
        String randCodeKey= SeckillCacheUtils.getRandCodeKey(spuId);
        if(redisTemplate.hasKey(randCodeKey)){
            // 如果redis中包含当spuId的key
            // 获取redis中对应的key的随机码
            String redisRandCode= redisTemplate.boundValueOps(randCodeKey).get()+"";
            if(redisRandCode==null){
                // 如果redis中邀请码丢失,服务器内部错误
                throw new CoolSharkServiceException(ResponseCode.INTERNAL_SERVER_ERROR,
                        "缓存随机码为空,联系网站管理员");
            }
            // 判断路径中的随机码和Redis中的随机码是否一致
            if(!redisRandCode.equals(randCode)){
                // 如果不一致,我们认为本次请求是有人攻击行为
                throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,"没有这商品");
            }
            // 一切正常,提交信息到业务逻辑层
            SeckillCommitVO seckillCommitVO=
                    seckillService.commitSeckill(seckillOrderAddDTO);
            return JsonResult.ok(seckillCommitVO);
        }else{
            // 没有key对应,就是秒杀商品列表中没有这个spuId
            throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,"没有这商品");
        }

    }



}
