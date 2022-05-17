package cn.tedu.mall.search.controller;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.search.service.ISearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "搜索商品模块")
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private ISearchService searchService;

    @PostMapping("/load/data")
    @ApiOperation("ES加载spu所有数据")
    public JsonResult loadData(){
        searchService.loadSpuByPage();
        return JsonResult.ok("运行无异常");
    }



}
