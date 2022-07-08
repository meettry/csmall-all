package cn.tedu.mall.seckill.controller;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.pojo.seckill.dto.SeckillOrderAddDTO;
import cn.tedu.mall.pojo.seckill.vo.SeckillCommitVO;
import cn.tedu.mall.seckill.exception.SeckillBlockHandler;
import cn.tedu.mall.seckill.exception.SeckillFallBack;
import cn.tedu.mall.seckill.service.ISeckillService;
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

        return null;
    }


}

