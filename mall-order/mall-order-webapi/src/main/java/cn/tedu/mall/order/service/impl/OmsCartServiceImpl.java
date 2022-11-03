package cn.tedu.mall.order.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.common.restful.ResponseCode;
import cn.tedu.mall.common.security.UserInfo;
import cn.tedu.mall.order.mapper.OmsCartMapper;
import cn.tedu.mall.order.service.IOmsCartService;
import cn.tedu.mall.pojo.order.dto.CartAddDTO;
import cn.tedu.mall.pojo.order.dto.CartUpdateDTO;
import cn.tedu.mall.pojo.order.model.OmsCart;
import cn.tedu.mall.pojo.order.vo.CartStandardVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Meettry
 * @date 2022/11/2 9:29
 */
@Service
@Slf4j
public class OmsCartServiceImpl implements IOmsCartService {
    @Autowired
    private OmsCartMapper omsCartMapper;

    @Override
    public void addCart(CartAddDTO cartDTO) {
        // 要查询购物车中是否有指定商品之前,必须确定用户的Id
        Long userId = UserInfo.getUserId();
        Long skuId = cartDTO.getSkuId();
        // 根据用户id和商品skuId,查询商品信息
        OmsCart omsCart = omsCartMapper.selectExistsCart(userId, skuId);
        // 判断该商品是否存在
        if (omsCart == null) {
            // 表示该商品不在购物车中,所以执行新增操作
            OmsCart newCart = new OmsCart();
            // 将参数CartAddDTO对象的同名属性赋值给newCart
            BeanUtils.copyProperties(cartDTO,newCart);
            // 将CartAddDTO中没有的属性赋值给newCart
            newCart.setUserId(userId);
            // 执行新增操作
            omsCartMapper.saveCart(newCart);
        } else {
            // 表示该商品不在购物车中,所以执行修改数量操作
            omsCart.setQuantity(omsCart.getQuantity()+cartDTO.getQuantity());
            // 确定了数量,直接调用修改购物车数量方法即可
            omsCartMapper.updateQuantityById(omsCart);
        }


    }

    @Override
    public JsonPage<CartStandardVO> listCarts(Integer page, Integer pageSize) {
        // 从SpringSecurity上下文中获得用户id
        Long userId = UserInfo.getUserId();
        // 要想执行分页查询,先设置分页条件
        PageHelper.startPage(page,pageSize);
        // 设置完分页条件执行的查询.会自动在Sql语句后添加limit关键字
        List<CartStandardVO> list = omsCartMapper.selectCartByUserId(userId);
        // list是分页数据,实例化PageInfo对象将分页数据传入,转成JsonPage返回
        return JsonPage.restPage(new PageInfo<>(list));
    }

    @Override
    public void removeCart(Long[] ids) {
        // 调用mapper中批量删除的方法即可
        int rows = omsCartMapper.deleteCartsByIds(ids);
        if(rows==0){
            throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,"您要删除的商品不存在!");
        }
    }

    @Override
    public void removeAllCarts() {
        Long userId = UserInfo.getUserId();
        int rows = omsCartMapper.deleteCartsByUserId(userId);
        if(rows==0){
            throw new CoolSharkServiceException(ResponseCode.NOT_FOUND,"您的购物车为空!");
        }

    }

    @Override
    public void removeUserCarts(OmsCart omsCart) {
        // 生成订单时需要使用的业务,删除失败也不影响订单的生成
        // 因此直接调用即可
        omsCartMapper.deleteCartByUserIdAndSkuId(omsCart);
    }

    @Override
    public void updateQuantity(CartUpdateDTO cartUpdateDTO) {
        // 因为执行修改mapper方法要求的参数是OmsCart
        // 所以要先实例化OmsCart类型对象
        OmsCart omsCart = new OmsCart();
        // 然后将参数cartUpdateDTO的同名属性赋值到omsCart中
        BeanUtils.copyProperties(cartUpdateDTO,omsCart);
        omsCartMapper.updateQuantityById(omsCart);
    }

}
