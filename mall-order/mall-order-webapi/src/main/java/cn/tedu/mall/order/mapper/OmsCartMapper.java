package cn.tedu.mall.order.mapper;

import cn.tedu.mall.pojo.order.model.OmsCart;
import cn.tedu.mall.pojo.order.vo.CartStandardVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OmsCartMapper {

    // 1.判断当前登录用户的购物车中是否包含指定sku的方法
    OmsCart selectExistCart(@Param("userId")Long userId,@Param("skuId")Long skuId);

    // 2.保存到购物车的方法
    void saveCart(OmsCart omsCart);

    // 3.修改指定购物车信息数量的方法
    void updateQuantityById(OmsCart omsCart);


    // 根据用户id查询购物车列表
    List<CartStandardVO> selectCartsByUserId(Long userId);

    // 按id删除购物车商品(支持删除多个)
    int deleteCartsByIds(Long[] ids);

    // 按用户id清空该用户所有购物车内容
    int deleteCartsByUserId(Long userId);

    // 根据用户id和skuid删除购物中信息
    void deleteCartByUserIdAndSkuId(OmsCart omsCart);


}
