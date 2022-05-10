package cn.tedu.mall.product.service.front;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.pojo.product.vo.SpuDetailStandardVO;
import cn.tedu.mall.pojo.product.vo.SpuListItemVO;
import cn.tedu.mall.pojo.product.vo.SpuStandardVO;

public interface IForFrontSpuService {
    /**
     * 分页查询 分类对应spu列表
     * @param categoryId
     * @param page
     * @param pageSize
     * @return
     */
    JsonPage<SpuListItemVO> listSpuByCategoryId(Long categoryId, Integer page, Integer pageSize);

    /**
     * 利用id 查询spu数据
     * @param id
     * @return
     */
    SpuStandardVO getSpuById(Long id);

    /**
     * 利用spuId 查询spu 详情
     * @param spuId
     * @return
     */
    SpuDetailStandardVO getSpuDetailById(Long spuId);
}
