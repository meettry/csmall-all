package cn.tedu.mall.front.controller;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.front.service.IFrontProductService;
import cn.tedu.mall.pojo.product.vo.SpuDetailStandardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Meettry
 * @date 2022/11/1 14:40
 */
@RestController
@Api(tags = "前台spuDetail模块")
@RequestMapping("/front/spu/detail")
public class FrontSpuDetailController {
    @Autowired
    private IFrontProductService frontProductService;

    @GetMapping("/{spuId}")
    @ApiOperation("根据spuId查询spuDetail对象")
    @ApiImplicitParam(name = "spuId",value = "spuId",example = "1")
    public JsonResult<SpuDetailStandardVO> getSpuDetailBySpuId(@PathVariable Long spuId){
        SpuDetailStandardVO spuDetail = frontProductService.getSpuDetail(spuId);
        return JsonResult.ok(spuDetail);
    }

}
