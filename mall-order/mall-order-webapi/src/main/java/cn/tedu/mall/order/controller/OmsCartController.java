package cn.tedu.mall.order.controller;

import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
