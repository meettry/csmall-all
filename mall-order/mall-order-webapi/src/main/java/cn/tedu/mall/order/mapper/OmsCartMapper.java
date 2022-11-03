package cn.tedu.mall.order.mapper;

import cn.tedu.mall.pojo.order.model.OmsCart;
import cn.tedu.mall.pojo.order.vo.CartStandardVO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Meettry
 * @date 2022/11/1 17:26
 */
@Repository
public interface OmsCartMapper {
    // 判断当前用户购物车中的是否已经包含指定的商品
    OmsCart selectExistsCart(@Param("userId") Long userId,@Param("skuId") Long skuId);

    // 新增sku信息到购物车
    int saveCart(OmsCart omsCart);

    // 修改购物车中sku的数量
    int updateQuantityById(OmsCart omsCart);

    // 根据用户id查询购物车中sku信息
    List<CartStandardVO> selectCartByUserId(Long userId);

    // 根据用户选中的id删除购物车中的商品(批量删除)
    int deleteCartsByIds(Long[] ids);

    // 清空用户的购物车
    int deleteCartsByUserId(Long userId);

    // 根据用户id和SkuId删除商品
    int deleteCartByUserIdAndSkuId(OmsCart omsCart);
}
