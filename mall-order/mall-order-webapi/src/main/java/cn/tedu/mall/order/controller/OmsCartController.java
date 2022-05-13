package cn.tedu.mall.order.controller;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.order.utils.WebConsts;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
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
@Api(tags="购物车功能")
public class OmsCartController {
    @Autowired
    private IOmsCartService cartService;
    @PostMapping("/add")
    @ApiOperation("当前登录用户新增sku到购物车")
    // 判断当前用户是否为ROLE_user(是否登录了)
    @PreAuthorize("hasRole('ROLE_user')")
    // @Validated启动控制器运行前对CartAddDTO对象的SpringValidation验证
    public JsonResult addCart(@Validated CartAddDTO cartAddDTO){
        cartService.addCart(cartAddDTO);
        return JsonResult.ok("添加到购物车完成");
    }
    // leaf   passport    order
    // sso:10002   order:10005
    // 先运行10002的knife4j 访问前台用户登录 登录成功复制jwt
    // 转到order10005,在全局设置中设置参数
    // name:   Authorization
    // value:  Beaer [复制的jwt]
    // 然后要刷新10005的knife4j页面
    // 就可以发送新增到购物车的请求了

    // 根据登录用户id查询购物信息的方法
    @GetMapping("/list")
    @ApiOperation("根据登录用户id查询购物信息的方法")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码",name="page",required = true,dataType = "int"),
            @ApiImplicitParam(value = "每页条数",name="pageSize",required = true,dataType = "int")
    })
    @PreAuthorize("hasRole('ROLE_user')")
    public JsonResult<JsonPage<CartStandardVO>> listCart(
        @RequestParam(required = false,defaultValue = WebConsts.DEFAULT_PAGE) Integer page,
        @RequestParam(required = false,defaultValue = WebConsts.DEFAULT_PAGE_SIZE) Integer pageSize
    ){
        JsonPage<CartStandardVO> jsonPage=cartService.listCarts(page,pageSize);
        return JsonResult.ok(jsonPage);
    }


    // 根据用户指定的购物车id删除购物车信息(支持批量)
    @PostMapping("/delete/")
    @ApiOperation("根据用户指定的购物车id删除购物车信息(支持批量)")
    @ApiImplicitParams(
            @ApiImplicitParam(value = "指定购物车id",name="ids",required = true,dataType = "array")
    )
    @PreAuthorize("hasRole('ROLE_user')")
    public JsonResult removeCartByIds(Long[] ids){
        cartService.removeCart(ids);
        return JsonResult.ok();
    }

    // 根据用户id清空该用户购物车列表
    @PostMapping("/delete/all")
    @ApiOperation("根据用户id清空该用户购物车列表")
    @PreAuthorize("hasRole('ROLE_user')")
    public JsonResult removeCartsByUserId(){
        cartService.removeAllCarts();
        return JsonResult.ok("您的购物车已经清空!");
    }






}







