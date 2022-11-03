package cn.tedu.mall.front.controller;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.front.service.IFrontCategoryService;
import cn.tedu.mall.pojo.front.entity.FrontCategoryEntity;
import cn.tedu.mall.pojo.front.vo.FrontCategoryTreeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Meettry
 * @date 2022/10/31 15:27
 */
@RestController
@Slf4j
@RequestMapping("/front/category")
@Api(tags = "前台分类查询")
public class CategoryController {
    @Autowired
    private IFrontCategoryService frontCategoryService;

    @GetMapping("/all")
    @ApiOperation("查询获得三级分类树对象")
    public JsonResult<FrontCategoryTreeVO<FrontCategoryEntity>> getTreeVO(){
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO = frontCategoryService.categoryTree();
        return JsonResult.ok(treeVO);
    }
}
