package cn.tedu.mall.search.controller;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.pojo.search.entity.SpuEntity;
import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import cn.tedu.mall.search.service.ISearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@Api(tags = "搜索模块")
public class SearchController {
    @Autowired
    private ISearchService searchService;

    // 搜索模块最主要的功能就是实现搜索
    // 面对这种功能对应的控制器方法,可以不写任何路径
    // 按下面@GetMapping注解表示当前控制路径为localhost:10008/search
    @GetMapping
    @ApiOperation("根据关键字查询ES中的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "搜索关键字",name = "keyword",dataType = "string",
                                                    required = true),
            @ApiImplicitParam(value = "页码",name = "page",dataType = "int",
                    required = true),
            @ApiImplicitParam(value = "每页条数",name = "pageSize",dataType = "int",
                    required = true)
    })
    public JsonResult<JsonPage<SpuEntity>> searchByKeyword(
            String keyword,
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize
            ){
        JsonPage<SpuEntity> list=searchService.search(keyword,page,pageSize);
        return JsonResult.ok(list);

    }

}
