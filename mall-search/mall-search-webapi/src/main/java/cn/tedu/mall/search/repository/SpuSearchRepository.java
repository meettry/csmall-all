package cn.tedu.mall.search.repository;

import cn.tedu.mall.pojo.search.entity.SpuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpuSearchRepository extends ElasticsearchRepository<SpuEntity,Long> {
    // 虚拟机的系统中设置了一个search_text的字段
    // 这个字段包含了所有可能需要查询的字段的值的和
    // 简单来说就是包含了查询本地ES中的name\title\description\categoryName等
    // 实际上虚拟机的ES也只对search_text字段进行分词
    // 所以执行搜索只需搜索search_text字段
    @Query("{\"match\":{\"search_text\":{\"query\":\"?0\"}}}")
    Page<SpuEntity> querySearchByText(String keyword, Pageable pageable);



}






