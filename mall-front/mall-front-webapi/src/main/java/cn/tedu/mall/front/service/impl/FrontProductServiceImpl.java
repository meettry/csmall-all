package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.front.service.IFrontProductService;
import cn.tedu.mall.pojo.product.vo.*;
import cn.tedu.mall.product.service.front.IForFrontSpuService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrontProductServiceImpl implements IFrontProductService {

    @DubboReference
    private IForFrontSpuService dubboSpuService;


    @Override
    public JsonPage<SpuListItemVO> listSpuByCategoryId(Long categoryId, Integer page, Integer pageSize) {
        // 调用product模块编写好的业务逻辑层方法查询结果
        JsonPage<SpuListItemVO> spuListItemVOJsonPage=
                dubboSpuService.listSpuByCategoryId(categoryId,page,pageSize);
        // 千万别忘了返回结果
        return spuListItemVOJsonPage;
    }

    @Override
    public SpuStandardVO getFrontSpuById(Long id) {
        return null;
    }

    @Override
    public List<SkuStandardVO> getFrontSkusBySpuId(Long spuId) {
        return null;
    }

    @Override
    public SpuDetailStandardVO getSpuDetail(Long spuId) {
        return null;
    }

    @Override
    public List<AttributeStandardVO> getSpuAttributesBySpuId(Long spuId) {
        return null;
    }
}
