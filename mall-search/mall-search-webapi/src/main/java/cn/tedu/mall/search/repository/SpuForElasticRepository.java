package cn.tedu.mall.search.repository;

import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpuForElasticRepository
        extends ElasticsearchRepository<SpuForElastic,Long> {

    // 查询spu关键字段中包含"手机"的方法
    @Query("{\n" +
            "    \"bool\": {\n" +
            "      \"should\": [\n" +
            "        { \"match\": { \"name\": \"手机\"}},\n" +
            "        { \"match\": { \"title\": \"手机\"}},\n" +
            "        { \"match\": { \"description\": \"手机\"}},\n" +
            "        { \"match\": { \"category_name\": \"手机\"}}\n" +
            "        ]\n" +
            "     }\n" +
            "}")
    Iterable<SpuForElastic> querySearch();

}
