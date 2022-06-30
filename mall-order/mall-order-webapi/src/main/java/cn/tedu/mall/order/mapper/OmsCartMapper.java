package cn.tedu.mall.order.mapper;

import cn.tedu.mall.pojo.order.model.OmsCart;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OmsCartMapper {
    // 判断当前用户的购物车列表中是否包含指定sku商品的方法
    OmsCart selectExistsCart(@Param("userId") Long userId,@Param("skuId") Long skuId);

    // 新增商品到购物车表中
    void saveCart(OmsCart omsCart);

    // 修改指定购物车商品的数量的方法
    void updateQuantityById(OmsCart omsCart);
}
