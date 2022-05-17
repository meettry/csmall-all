package cn.tedu.mall.order.controller;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.JsonResult;
import cn.tedu.mall.order.service.IOmsOrderService;
import cn.tedu.mall.pojo.order.dto.OrderAddDTO;
import cn.tedu.mall.pojo.order.dto.OrderListTimeDTO;
import cn.tedu.mall.pojo.order.vo.OrderAddVO;
import cn.tedu.mall.pojo.order.vo.OrderListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oms/order")
@Api(tags="订单功能")
public class OmsOrderController {

    @Autowired
    private IOmsOrderService orderService;

    @PostMapping("/add")
    @ApiOperation("生成订单的方法")
    @PreAuthorize("hasRole('ROLE_user')")
    public JsonResult<OrderAddVO> addOrder(@Validated OrderAddDTO orderAddDTO){
        OrderAddVO orderAddVO=orderService.addOrder(orderAddDTO);
        return JsonResult.ok(orderAddVO);
    }

    // 当前登录用户根据指定时间分页查询订单
    @GetMapping("/list")
    @ApiOperation("当前登录用户根据指定时间分页查询订单")
    @PreAuthorize("hasRole('ROLE_user')")
    public JsonResult<JsonPage<OrderListVO>> listUserOrders(OrderListTimeDTO orderListTimeDTO){

        JsonPage<OrderListVO> orderListVOs=orderService.listOrdersBetweenTimes(orderListTimeDTO);
        return JsonResult.ok(orderListVOs);
    }



}






