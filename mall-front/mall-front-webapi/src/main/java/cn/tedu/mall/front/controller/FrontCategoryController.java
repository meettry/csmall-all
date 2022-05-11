package cn.tedu.mall.front.controller;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.front.service.IFrontCategoryService;
import cn.tedu.mall.pojo.front.entity.FrontCategoryEntity;
import cn.tedu.mall.pojo.front.vo.FrontCategoryTreeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/front/category")
@Api(tags = "前台分类树模块")
public class FrontCategoryController {
    // 当前控制器为了显示分类树结果
    @Autowired
    private IFrontCategoryService frontCategoryService;

    //获得所有分类树结果的方法
    @GetMapping("/all")
    @ApiOperation("查询三级分类树")
    public JsonResult<FrontCategoryTreeVO<FrontCategoryEntity>> categoryTree(){
        // 调用业务逻辑层方法
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO=frontCategoryService.categoryTree();
        return JsonResult.ok(treeVO);
    }


}
