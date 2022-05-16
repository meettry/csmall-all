package cn.tedu.mall.order.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.pojo.domain.CsmallAuthenticationInfo;
import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.order.mapper.OmsCartMapper;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
import cn.tedu.mall.pojo.order.dto.CartUpdateDTO;
import cn.tedu.mall.pojo.order.dto.OrderAddDTO;
import cn.tedu.mall.pojo.order.model.OmsCart;
import cn.tedu.mall.pojo.order.vo.CartStandardVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OmsCartServiceImpl implements IOmsCartService {

    @Autowired
    private OmsCartMapper omsCartMapper;

    @Override
    public void addCart(CartAddDTO cartDTO) {
        // 获取当前登录用户的userId
        Long userId=getUserId();
        // 判断这个userId的用户是否已经将指定的sku添加到购物车
        OmsCart omsCartExist= omsCartMapper.selectExistCart(userId,cartDTO.getSkuId());
        if(omsCartExist!=null) {
            // 如果sku已经存在于购物车中,就修改这个sku在购物车中的数量
            omsCartExist.setQuantity(omsCartExist.getQuantity()+cartDTO.getQuantity());
            // 执行数据库修改
            omsCartMapper.updateQuantityById(omsCartExist);
        }else {
            // 如果sky没有存在于购物车中,就新增sku信息到购物车
            OmsCart omsCartNew=new OmsCart();
            // 将cartDTO对象中的同名属性赋值给 新创建的OmsCart对象
            BeanUtils.copyProperties(cartDTO,omsCartNew);
            // OmsCart对象中赋值当前登录用户id
            omsCartNew.setUserId(userId);
            // 新增新增
            omsCartMapper.saveCart(omsCartNew);

        }
    }

    // 分页显示当前登录用户的购物车信息
    @Override
    public JsonPage<CartStandardVO> listCarts(Integer page, Integer pageSize) {
        // 拿到userId
        Long userId=getUserId();
        // 查询前设置好你要查询的页面和每页的条数
        PageHelper.startPage(page,pageSize);
        // 会执行分页查询,获得分页的list列表
        List<CartStandardVO> list=omsCartMapper.selectCartsByUserId(userId);
        // 实例化PageInfo并转换为JsonPage返回给控制器
        return JsonPage.restPage(new PageInfo<>(list));
    }

    @Override
    public void removeCart(Long[] ids) {
        // rows是删除掉的行数
        int rows=omsCartMapper.deleteCartsByIds(ids);
        if(rows==0){
            throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,"您的购物车没有对应商品");
        }
    }

    @Override
    public void removeAllCarts() {
        // 获得用户id
        Long userId=getUserId();
        int rows=omsCartMapper.deleteCartsByUserId(userId);
        if(rows==0){
            throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,"您的购物车中没有数据");
        }
    }

    // 生成订单时,先删除购物车中的方法
    @Override
    public void removeUserCarts(OmsCart omsCart) {
        // 根据omsCart中的userId和skuId删除购物车信息
        omsCartMapper.deleteCartByUserIdAndSkuId(omsCart);
    }

    // 修改购物车商品数量
    @Override
    public void updateQuantity(CartUpdateDTO cartUpdateDTO) {
        OmsCart omsCart=new OmsCart();
        // 将cartUpdateDTO对象的同名属性赋值给omsCart
        BeanUtils.copyProperties(cartUpdateDTO,omsCart);
        omsCartMapper.updateQuantityById(omsCart);
    }

    // 业务逻辑层中可能有多个方法需要获得当前用户信息
    // 我们可以定义一个方法实现从SpringSecurity中获得用户信息
    public CsmallAuthenticationInfo getUserInfo(){
        // 从SpringSecurity框架的容器中,获得当前用户的authenticationToken对象
        UsernamePasswordAuthenticationToken authenticationToken=
                (UsernamePasswordAuthenticationToken)SecurityContextHolder
                                        .getContext().getAuthentication();
        // 判断获取的对象是不是null
        if(authenticationToken!=null){
            // 如果不是空,就是登录成功了,从authenticationToken对象中获得当前登录用户
            CsmallAuthenticationInfo csmallAuthenticationInfo=
                    (CsmallAuthenticationInfo)authenticationToken.getCredentials();
            return csmallAuthenticationInfo;
        }
        throw new CoolSharkServiceException(ResponseCode.UNAUTHORIZED,"没有登录信息");
    }
    // 单纯获得当前登录用户id的方法
    public Long getUserId(){
        return getUserInfo().getId();
    }






}
