package cn.tedu.mall.front.controller;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.front.service.IFrontProductService;
import cn.tedu.mall.pojo.product.vo.AttributeStandardVO;
import cn.tedu.mall.pojo.product.vo.SpuListItemVO;
import cn.tedu.mall.pojo.product.vo.SpuStandardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Path;
import java.util.List;

@RestController
@RequestMapping("/front/spu")
@Api(tags = "前台商品Spu模块")
public class FrontSpuController {

    @Autowired
    private IFrontProductService frontProductService;
    // localhost:10004/front/spu/list/3
    // 根据分类id分页查询spu列表
    @GetMapping("/list/{categoryId}")
    @ApiOperation("根据分类id分页查询spu列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value ="分类id" ,name ="categoryId" ,required = true ,dataType ="long" ),
            @ApiImplicitParam(value ="页码" ,name ="page" ,required = true ,dataType ="int" ),
            @ApiImplicitParam(value ="每页条数" ,name ="pageSize" ,required = true ,dataType ="int" )
    })
    public JsonResult<JsonPage<SpuListItemVO>> listSpuByCategoryIdPage(
            @PathVariable("categoryId") Long categoryId,Integer page,Integer pageSize){
        JsonPage<SpuListItemVO> jsonPage=
                frontProductService.listSpuByCategoryId(categoryId,page,pageSize);
        return JsonResult.ok(jsonPage);
    }

    // localhost:10004/front/spu/3
    // 根据spuid查询spu数据
    @GetMapping("/{id}")
    @ApiOperation("根据前台Spuid查询spu信息")
    @ApiImplicitParams(
            @ApiImplicitParam(value = "spuid",name="id",required = true,dataType = "long")
    )
    public JsonResult<SpuStandardVO> getFrontSpuById(
                @PathVariable Long id){
        SpuStandardVO spuStandardVO=frontProductService.getFrontSpuById(id);
        return JsonResult.ok(spuStandardVO);
    }

    //根据Spuid查询所有属性
    @GetMapping("/template/{spuId}")
    @ApiOperation("根据Spuid查询所有属性")
    @ApiImplicitParams(
            @ApiImplicitParam(value = "spuId",name = "spuId",required = true,dataType = "long")
    )
    public JsonResult<List<AttributeStandardVO>> getSpuAttributeBySpuId(
            @PathVariable Long spuId){
        List<AttributeStandardVO> list=frontProductService.getSpuAttributesBySpuId(spuId);
        return JsonResult.ok(list);
    }








}
