package cn.tedu.mall.search.repository;

import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpuForElasticRepository extends
                        ElasticsearchRepository<SpuForElastic,Long> {

    // 自定义查询方法
    Iterable<SpuForElastic> querySpuForElasticsByTitleMatches(String title);

    @Query("{\n" +
            "    \"bool\": {\n" +
            "      \"should\": [\n" +
            "        { \"match\": { \"name\": \"?0\"}},\n" +
            "        { \"match\": { \"title\": \"?0\"}},\n" +
            "        { \"match\": { \"description\": \"?0\"}},\n" +
            "        { \"match\": { \"category_name\": \"?0\"}}\n" +
            "        ]\n" +
            "     }\n" +
            "}")




}
