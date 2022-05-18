package cn.tedu.mall.search.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.pojo.search.entity.SpuEntity;
import cn.tedu.mall.search.repository.SpuSearchRepository;
import cn.tedu.mall.search.service.ISearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SearchServiceImpl implements ISearchService {

    @Autowired
    private SpuSearchRepository spuSearchRepository;

    @Override
    public JsonPage<SpuEntity> search(String keyword, Integer page, Integer pageSize) {
        if(page==null){
            page=1;
        }
        if(pageSize==null){
            pageSize=5;
        }
        // Page类型是SpringData框架支持的分页类型,和PageHelper框架的PageInfo类似,包含分页信息和查询结果
        Page<SpuEntity> spuEntities=spuSearchRepository
                .querySearchByText(keyword, PageRequest.of(page-1,pageSize));
        // JsonPage类型不包含将Page类型转换为JsonPage的方法,需要我们敲代码转换
        JsonPage jsonPage=new JsonPage();
        jsonPage.setPage(page);
        jsonPage.setPageSize(pageSize);
        // 赋值总页数
        jsonPage.setTotalPage(spuEntities.getTotalPages());
        // 赋值总条数
        jsonPage.setTotal(spuEntities.getTotalElements());
        // 赋值数据
        jsonPage.setList(spuEntities.getContent());
        // 别忘了返回jsonPage!!!!!
        return jsonPage;
    }

    @Override
    public void loadSpuByPage() {

    }
}
