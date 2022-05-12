package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.front.service.IFrontProductService;
import cn.tedu.mall.pojo.product.vo.*;
import cn.tedu.mall.product.service.front.IForFrontSkuService;
import cn.tedu.mall.product.service.front.IForFrontSpuService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrontProductServiceImpl implements IFrontProductService {

    @DubboReference
    private IForFrontSpuService dubboSpuService;
    @DubboReference
    private IForFrontSkuService dubboSkuService;

    @Override
    public JsonPage<SpuListItemVO> listSpuByCategoryId(Long categoryId, Integer page, Integer pageSize) {
        // 调用product模块编写好的业务逻辑层方法查询结果
        JsonPage<SpuListItemVO> spuListItemVOJsonPage=
                dubboSpuService.listSpuByCategoryId(categoryId,page,pageSize);
        // 千万别忘了返回结果
        return spuListItemVOJsonPage;
    }

    // 根据spuid 查询spu对象
    @Override
    public SpuStandardVO getFrontSpuById(Long id) {
        // 调用根据id查询spu的product方法
        SpuStandardVO spuStandardVO=dubboSpuService.getSpuById(id);
        // 别忘了返回spuStandardVO
        return spuStandardVO;
    }

    // 根据spuId查询 当前商品所有sku列表
    @Override
    public List<SkuStandardVO> getFrontSkusBySpuId(Long spuId) {
        List<SkuStandardVO> list=dubboSkuService.getSkusBySpuId(spuId);
        // 别忘了返回
        return list;
    }

    // 根据spuid查询 spu的详情
    @Override
    public SpuDetailStandardVO getSpuDetail(Long spuId) {
        SpuDetailStandardVO spuDetailStandardVO=dubboSpuService.getSpuDetailById(spuId);
        // 别忘了返回spuDetailStandardVO
        return spuDetailStandardVO;
    }

    @Override
    public List<AttributeStandardVO> getSpuAttributesBySpuId(Long spuId) {
        return null;
    }
}
