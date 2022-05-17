package cn.tedu.mall.search.test;


import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import cn.tedu.mall.search.MallSearchWebApiApplication;
import cn.tedu.mall.search.repository.SpuForElasticRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
public class MallSearchTestApplication {

    @Autowired
    private SpuForElasticRepository elasticRepository;

    @Test
    public void search(){
        Iterable<SpuForElastic> iterable=elasticRepository
                .querySearch("手机");
        iterable.forEach(spu -> System.out.println(spu));
    }


}
