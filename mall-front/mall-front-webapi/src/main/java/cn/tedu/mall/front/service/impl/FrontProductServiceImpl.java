package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.front.service.IFrontProductService;
import cn.tedu.mall.pojo.product.vo.*;
import cn.tedu.mall.product.service.front.IForFrontAttributeService;
import cn.tedu.mall.product.service.front.IForFrontSkuService;
import cn.tedu.mall.product.service.front.IForFrontSpuService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrontProductServiceImpl implements IFrontProductService {

    @DubboReference
    private IForFrontSpuService dubboSpuService;
    // 声明消费Sku相关的业务逻辑
    @DubboReference
    private IForFrontSkuService dubboSkuService;
    // 声明消费商品参数选项(attribute)的业务逻辑
    @DubboReference
    private IForFrontAttributeService dubboAttributeService;


    @Override
    public JsonPage<SpuListItemVO> listSpuByCategoryId(Long categoryId, Integer page, Integer pageSize) {
        // IForFrontSpuService实现类中完成了分页步骤,所以我们直接调用即可
        JsonPage<SpuListItemVO> spuListItemVOJsonPage=
                dubboSpuService.listSpuByCategoryId(categoryId,page,pageSize);
        // 千万别忘了返回spuListItemVOJsonPage
        return spuListItemVOJsonPage;
    }

    // 根据spuId查询Spu详情
    @Override
    public SpuStandardVO getFrontSpuById(Long id) {
        // dubbo调用spu的方法即可
        SpuStandardVO spuStandardVO=dubboSpuService.getSpuById(id);
        return spuStandardVO;
    }

    // 根据spuId查询当前spu对应的所有sku商品列表
    @Override
    public List<SkuStandardVO> getFrontSkusBySpuId(Long spuId) {
        List<SkuStandardVO> list=dubboSkuService.getSkusBySpuId(spuId);
        return list;
    }

    // 根据spuId查询spuDetail详情
    @Override
    public SpuDetailStandardVO getSpuDetail(Long spuId) {
        SpuDetailStandardVO spuDetailStandardVO=
                dubboSpuService.getSpuDetailById(spuId);
        return spuDetailStandardVO;
    }

    @Override
    public List<AttributeStandardVO> getSpuAttributesBySpuId(Long spuId) {
        return null;
    }
}
