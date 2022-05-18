package cn.tedu.mall.search.controller;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.pojo.search.entity.SpuEntity;
import cn.tedu.mall.search.service.ISearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

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

    // 实现根据关键字查询Es中数据的搜索功能
    // @GetMapping不写()或()里只有""表示当前方法路径就是类上定义的路径
    // localhost:10008/search
    @GetMapping
    @ApiOperation("实现根据关键字查询Es中数据的搜索功能")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "搜索关键字",name="keyword",dataType = "string",required = true),
            @ApiImplicitParam(value = "页码",name="page",dataType = "int",required = true),
            @ApiImplicitParam(value = "每页条数",name="pageSize",dataType = "int",required = true)
    })
    public JsonResult<JsonPage<SpuEntity>> searchByKeyword(
                        String keyword,Integer page,Integer pageSize){
        JsonPage<SpuEntity> list=searchService.search(keyword,page,pageSize);
        return JsonResult.ok(list);
    }



}
