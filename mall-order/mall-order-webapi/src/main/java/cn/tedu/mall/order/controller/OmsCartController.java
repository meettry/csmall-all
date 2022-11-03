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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Meettry
 * @date 2022/11/2 10:29
 */
@RestController
@RequestMapping("/oms/cart")
@Api(tags = "购物车管理模块")
@Slf4j
public class OmsCartController {
    @Autowired
    private IOmsCartService omsCartService;

    @PostMapping("/add")
    @ApiOperation("新增购物车")
    @PreAuthorize("hasAuthority('ROLE_user')")
    public JsonResult addCart(@Validated CartAddDTO cartAddDTO){
        // @Validated是激活SpringValidation框架用的
        // 参数CartAddDTO中,其各个属性设置了验证规则,若有参数不符合规则,则会抛出BindException
        omsCartService.addCart(cartAddDTO);
        return JsonResult.ok("新增sku到购物车完成");
    }

    @GetMapping("/list")
    @ApiOperation("根据用户id分页查询购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码",name = "page",example = "1"),
            @ApiImplicitParam(value = "每页条数",name = "pageSize",example = "3")
    })
    @PreAuthorize("hasAuthority('ROLE_user')")
    public JsonResult<JsonPage<CartStandardVO>> listCartsByPage(
            @RequestParam(required = false,defaultValue = WebConsts.DEFAULT_PAGE)Integer page,
            @RequestParam(required = false,defaultValue = WebConsts.DEFAULT_PAGE_SIZE)Integer pageSize){
        // @RequestParam 此注解是SpringMvc的注解,可以给控制器中的注解赋默认值
        // 需要写的参数有required = false,defaultValue为默认值
        return JsonResult.ok(omsCartService.listCarts(page, pageSize));
    }

    @PostMapping("/delete")
    @ApiOperation("根据id数组删除购物车中的sku信息")
    @ApiImplicitParam(value = "包含要删除id的数组",name = "ids",
            required = true, dataType = "array")
    // 当@PreAuthorize注解后面要判断的权限内容以ROLE_开头时
    // 表示我们判断的内容是SpringSecurity框架约定的角色
    // 我们可以在@PreAuthorize注解()里使用hasRole来简化对角色的判断
    // hasRole('user')这样的判断会检查当前登录用户是否有ROLE_user这个角色
    // 也就是会自动在user前加ROLE_来判断
    // @PreAuthorize("hasAuthority('ROLE_user')")
    @PreAuthorize("hasRole('user')")
    public JsonResult removeCartsByIds(Long[] ids){
        omsCartService.removeCart(ids);
        return JsonResult.ok("删除完成");
    }

    @PostMapping("/delete/all")
    @ApiOperation("清空购物车")
    @PreAuthorize("hasRole('user')")
    public JsonResult removeAllCarts(){
        omsCartService.removeAllCarts();
        return JsonResult.ok("清空完成");
    }

    @PostMapping("udate/quantity")
    @ApiOperation("修改购物车中sku数量")
    @PreAuthorize("hasRole('user')")
    public JsonResult updateQuantity(@Validated CartUpdateDTO cartUpdateDTO){
        omsCartService.updateQuantity(cartUpdateDTO);
        return JsonResult.ok("修改完成");
    }

}

