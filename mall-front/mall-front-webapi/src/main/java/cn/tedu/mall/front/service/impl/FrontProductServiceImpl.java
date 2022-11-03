package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.front.service.IFrontProductService;
import cn.tedu.mall.pojo.product.vo.*;
import cn.tedu.mall.product.service.front.IForFrontAttributeService;
import cn.tedu.mall.product.service.front.IForFrontSkuService;
import cn.tedu.mall.product.service.front.IForFrontSpuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Meettry
 * @date 2022/11/1 10:23
 */
@Service
@Slf4j
public class FrontProductServiceImpl implements IFrontProductService {

    @DubboReference
    private IForFrontSpuService dubboSpuService;
    // 根据spuId查询sku信息用的对象
    @DubboReference
    private IForFrontSkuService dubboSkuService;
    // 根据spuId查询查询当前商品的所有属性/规格用的对象
    @DubboReference
    private IForFrontAttributeService dubboAttributeService;

    @Override
    public JsonPage<SpuListItemVO> listSpuByCategoryId(Long categoryId, Integer page, Integer pageSize) {
        // dubbo调用的方法是完成了分页查询的方法,这里只是调用然后返回即可
        return dubboSpuService.listSpuByCategoryId(categoryId, page, pageSize);
    }

    @Override
    public SpuStandardVO getFrontSpuById(Long id) {
        return dubboSpuService.getSpuById(id);
    }

    @Override
    public List<SkuStandardVO> getFrontSkusBySpuId(Long spuId) {
        return dubboSkuService.getSkusBySpuId(spuId);
    }

    @Override
    public SpuDetailStandardVO getSpuDetail(Long spuId) {
        return dubboSpuService.getSpuDetailById(spuId);
    }

    @Override
    public List<AttributeStandardVO> getSpuAttributesBySpuId(Long spuId) {
        return dubboAttributeService.getSpuAttributesBySpuId(spuId);
    }
}
