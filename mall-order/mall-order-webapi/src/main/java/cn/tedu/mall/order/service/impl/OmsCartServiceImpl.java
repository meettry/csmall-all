package cn.tedu.mall.order.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.pojo.domain.CsmallAuthenticationInfo;
import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.order.mapper.OmsCartMapper;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
import cn.tedu.mall.pojo.order.dto.CartUpdateDTO;
import cn.tedu.mall.pojo.order.model.OmsCart;
import cn.tedu.mall.pojo.order.vo.CartStandardVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
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
        // 查询这个userId的用户是否已经将指定的sku添加到购物车
        OmsCart omsCart=omsCartMapper.selectExistsCart(userId,cartDTO.getSkuId());
        // 判断查询结果是否为null
        if(omsCart!=null){
            // 不等于null,表示当前用户这个sku已经添加在购物车列表中
            // 我们需要做的就是修改它的数量,根据cartDTO对象的quantity属性值添加
            omsCart.setQuantity(omsCart.getQuantity()+cartDTO.getQuantity());
            // 调用持久层方法修改数量
            omsCartMapper.updateQuantityById(omsCart);
        }else{
            // 如果omsCart是null 会运行else代码块
            // 去完成购物车对象的新增,先实例化OmsCart对象
            OmsCart newOmsCart=new OmsCart();
            // 将参数cartDTO的同名属性赋值给newOmsCart
            BeanUtils.copyProperties(cartDTO,newOmsCart);
            // cartDTO对象中没有userId属性,需要单独赋值
            newOmsCart.setUserId(userId);
            // 执行新增
            omsCartMapper.saveCart(newOmsCart);
        }
    }

    // 根据用户id分页查询当前用户的购物车列表
    @Override
    public JsonPage<CartStandardVO> listCarts(Integer page, Integer pageSize) {
        // 获得用户id
        Long userId=getUserId();
        // 执行查询前设置分页条件
        PageHelper.startPage(page,pageSize);
        // 执行分页查询
        List<CartStandardVO> list=omsCartMapper.selectCartsByUserId(userId);
        // 实例化PageInfo对象获得分页信息后将它转换为JsonPage返回
        return JsonPage.restPage(new PageInfo<>(list));
    }

    // 按ids数组中的id值删除cart表中信息
    @Override
    public void removeCart(Long[] ids) {
        // 删除是包含返回值的
        int rows=omsCartMapper.deleteCartsByIds(ids);
        if(rows==0){
            throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,
                    "购物车中没有您要删除的商品");
        }
    }

    @Override
    public void removeAllCarts() {

    }

    @Override
    public void removeUserCarts(OmsCart omsCart) {

    }

    @Override
    public void updateQuantity(CartUpdateDTO cartUpdateDTO) {

    }

    // 业务逻辑层获得用户信息的方法,因为多个方法需要获得用户信息,所以单独编写一个方法
    // 这个方法的实现是SpringSecurity提供的登录用户的容器
    // 方法的目标是获得SpringSecurity用户容器,从容器中获得用户信息
    public CsmallAuthenticationInfo getUserInfo(){
        // 获得SpringSecurity容器对象
        UsernamePasswordAuthenticationToken authenticationToken=
                (UsernamePasswordAuthenticationToken)SecurityContextHolder.
                        getContext().getAuthentication();
        // 判断获取的容器信息是否为空
        if(authenticationToken!=null){
            // 如果容器中有内容,证明当前容器中有登录用户信息
            // 我们获取这个用户信息并返回
            CsmallAuthenticationInfo csmallAuthenticationInfo=
                    (CsmallAuthenticationInfo)authenticationToken.getCredentials();
            return csmallAuthenticationInfo;
        }
        throw new CoolSharkServiceException(ResponseCode.UNAUTHORIZED,"没有登录信息");
    }
    // 业务逻辑层中大多数方法都是获得用户id,所以编写一个返回用户id的方法
    public Long getUserId(){
        return getUserInfo().getId();
    }



}
