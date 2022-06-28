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
@Api(tags="前台显示分类功能")
public class FrontCategoryController {
    // 装配业务逻辑层代码
    @Autowired
    private IFrontCategoryService frontCategoryService;

    // 获得分类树信息的方法
    @GetMapping("/all")
    @ApiOperation("获得分类树信息的方法")
    public JsonResult<FrontCategoryTreeVO<FrontCategoryEntity>> categoryTree(){
        // 调用返回即可
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO=
                frontCategoryService.categoryTree();
        return JsonResult.ok(treeVO);
    }


}
