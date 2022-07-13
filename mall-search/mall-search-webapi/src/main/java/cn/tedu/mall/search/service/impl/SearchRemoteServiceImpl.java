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
    public JsonPage<SpuForElastic> search(String keyword, Integer page, Integer pageSize) {
        Page<SpuEntity> spuEntities=spuForElasticRepository
                .querySearchByText(keyword, PageRequest.of(page-1,pageSize));
        JsonPage<SpuEntity> jsonPage=new JsonPage<>();

        return null;
    }

    @Override
    public void loadSpuByPage() {

    }
}
