package cn.tedu.mall.search.test;

import cn.tedu.mall.pojo.search.entity.SpuEntity;
import cn.tedu.mall.search.MallSearchWebApiApplication;
import cn.tedu.mall.search.repository.SpuSearchRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MallSearchWebApiApplication.class})
public class MallSearchTestApplication {
    @Autowired
    private SpuSearchRepository spuRepository;
    @Test
    public void addSpu(){
        SpuEntity spuEntity=new SpuEntity();
        spuEntity.setId(1l);
        spuEntity.setName("小米13");
        spuRepository.save(spuEntity);
    }
}
