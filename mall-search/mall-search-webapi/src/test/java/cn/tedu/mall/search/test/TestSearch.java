package cn.tedu.mall.search.test;

import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import cn.tedu.mall.search.repository.SpuForElasticRepository;
import cn.tedu.mall.search.service.ISearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.SchemaOutputResolver;

@SpringBootTest
public class TestSearch {

    @Autowired
    private ISearchService searchService;

    @Test
    void loadData(){
        searchService.loadSpuByPage();
        System.out.println("ok");
    }

    @Autowired
    private SpuForElasticRepository elasticRepository;

   /* @Test
    void getAll(){
        Iterable<SpuForElastic> es=elasticRepository.findAll();
        es.forEach(e-> System.out.println(e));

    }

    @Test
    void getSpu(){
        Iterable<SpuForElastic> it=elasticRepository
                        .querySpuForElasticsByTitleMatches("手机");
        it.forEach(e-> System.out.println(e));
    }

    @Test
    void getKeyword(){
        Iterable<SpuForElastic> it=elasticRepository.querySearch("手机");
        it.forEach(e-> System.out.println(e));
    }

*/


}
