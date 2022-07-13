package cn.tedu.mall.search.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.pojo.product.model.Spu;
import cn.tedu.mall.pojo.product.vo.SpuStandardVO;
import cn.tedu.mall.pojo.search.entity.SpuEntity;
import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import cn.tedu.mall.search.repository.SpuForElasticRepository;
import cn.tedu.mall.search.service.ISearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

// 实现查询远程服务器ES的业务逻辑层实现类
@Service
@Slf4j
public class SearchRemoteServiceImpl implements ISearchService {

    //注入查询ES的Repository
    @Autowired
    private SpuForElasticRepository spuForElasticRepository;
    @Override
    public JsonPage<SpuEntity> search(String keyword, Integer page, Integer pageSize) {
        Page<SpuEntity> spuEntities=spuForElasticRepository
                .querySearchByText(keyword, PageRequest.of(page-1,pageSize));
        JsonPage<SpuEntity> jsonPage=new JsonPage<>();
        //赋值分页参数
        jsonPage.setPage(page);
        jsonPage.setPageSize(pageSize);
        // 总页数
        jsonPage.setTotalPage(spuEntities.getTotalPages());
        // 总条数
        jsonPage.setTotal(spuEntities.getTotalElements());
        // 赋值数据
        jsonPage.setList(spuEntities.getContent());
        // 别忘了返回jsonPage
        return jsonPage;
    }

    @Override
    public void loadSpuByPage() {

    }
}
