package cn.tedu.mall.search.service;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.pojo.search.entity.SpuEntity;


public interface ISearchService {
    JsonPage<SpuEntity> search(String keyword, Integer page, Integer pageSize);
}
