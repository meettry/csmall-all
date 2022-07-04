package cn.tedu.mall.search.repository;

import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SpuForElasticRepository extends
                        ElasticsearchRepository<SpuForElastic,Long> {
}
