package cn.tedu.mall.search.repository;

import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpuForElasticRepository extends
                        ElasticsearchRepository<SpuForElastic,Long> {

    // 自定义查询方法
    Iterable<SpuForElastic> querySpuForElasticsByTitleMatches(String title);


}
