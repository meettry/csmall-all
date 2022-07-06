package cn.tedu.mall.seckill.controller;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuVO;
import cn.tedu.mall.seckill.service.ISeckillSpuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill/spu")
@Api(tags = "秒杀Spu模块")
public class SeckillSpuController {
    @Autowired
    private ISeckillSpuService seckillSpuService;

    @GetMapping("/list")
    @ApiOperation("分页查询秒杀列表Spu信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码",name="page",required = true,dataType = "int"),
            @ApiImplicitParam(value = "每页条数",name="pageSize",required = true
                                                                    ,dataType = "int")
    })
    // 查询秒杀列表不需要登录
    public JsonResult<JsonPage<SeckillSpuVO>> listSeckillSpus(
                                        Integer page,Integer pageSize){
        JsonPage<SeckillSpuVO> list=seckillSpuService.listSeckillSpus(page,pageSize);
        return JsonResult.ok(list);
    }


}
