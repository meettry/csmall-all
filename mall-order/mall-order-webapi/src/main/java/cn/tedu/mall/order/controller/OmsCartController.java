package cn.tedu.mall.order.controller;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.order.utils.WebConsts;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
import cn.tedu.mall.pojo.order.dto.CartUpdateDTO;
import cn.tedu.mall.pojo.order.vo.CartStandardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oms/cart")
@Api(tags = "购物车管理模块")
public class OmsCartController {
    @Autowired
    private IOmsCartService omsCartService;

    // 新增购物车信息的控制层方法
    @PostMapping("/add")
    @ApiOperation("新增购物车信息")
    // 判断当前用户是否具有普通用户权限ROLE_user
    // sso模块登录时,会在用户的权限列表中添加ROLE_user权限
    @PreAuthorize("hasRole('ROLE_user')")
    // cartAddDTO参数是需要经过SpringValidation框架验证的
    // @Validated就是激活框架验证功能,如果cartAddDTO不满足验证要求,会自动运行
    // 统一由异常处理类中的BingingException异常处理
    public JsonResult addCart(@Validated CartAddDTO cartAddDTO){
        omsCartService.addCart(cartAddDTO);
        return JsonResult.ok("成功添加到购物车");
    }

    // 分页查询当前用户购物车中的信息
    @GetMapping("/list")
    @ApiOperation("分页查询当前用户购物车中的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码",name = "page",dataType = "int",example = "1"),
            @ApiImplicitParam(value = "每页条数",name = "pageSize",
                                        dataType = "int",example = "5")
    })
    @PreAuthorize("hasRole('ROLE_user')")
    public JsonResult<JsonPage<CartStandardVO>> listCartByPage(
            // 当控制器参数可能为空,当空时,我们要给它赋默认值时,可以用下面的格式
            @RequestParam(required = false,defaultValue = WebConsts.DEFAULT_PAGE)
                                                                        Integer page,
            @RequestParam(required = false,defaultValue = WebConsts.DEFAULT_PAGE_SIZE)
                                                                        Integer pageSize
    ){
        // 控制层调用业务逻辑层代码
        JsonPage<CartStandardVO> jsonPage=omsCartService.listCarts(page,pageSize);
        return JsonResult.ok(jsonPage);

    }


    @PostMapping("/delete")
    @ApiOperation("根据用户选择的购物车商品删除(支持批量)")
    @ApiImplicitParam(value = "删除购物车的id",name="ids",required = true,
                                    dataType = "array")
    @PreAuthorize("hasRole('ROLE_user')")
    public JsonResult removeCartsByIds(Long[] ids){
        omsCartService.removeCart(ids);
        return JsonResult.ok();
    }

    // 根据用户id清空购物车
    @PostMapping("/delete/all")
    @ApiOperation("根据用户id清空购物车")
    @PreAuthorize("hasRole('ROLE_user')")
    public JsonResult removeCartsByUserId(){
        omsCartService.removeAllCarts();;
        return JsonResult.ok("购物车已清空");
    }

    // 修改购物车数量
    @PostMapping("/update/quantity")
    @ApiOperation("修改购物车数量")
    @PreAuthorize("hasRole('ROLE_user')")
    public JsonResult updateQuantity(@Validated CartUpdateDTO cartUpdateDTO){
        omsCartService.updateQuantity(cartUpdateDTO);
        return JsonResult.ok("修改完成");
    }





}
