package cn.tedu.mall.search.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.pojo.product.model.Spu;
import cn.tedu.mall.pojo.search.entity.SpuEntity;
import cn.tedu.mall.pojo.search.entity.SpuForElastic;
import cn.tedu.mall.product.service.front.IForFrontSpuService;
import cn.tedu.mall.search.repository.SpuForElasticRepository;
import cn.tedu.mall.search.service.ISearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SearchLocalServiceImpl implements ISearchService {
    @Autowired
    private SpuForElasticRepository spuRepository;
    @DubboReference
    private IForFrontSpuService forFrontSpuService;
    // 利用Dubbo向product模块查询所有spu并新增到ES的方法
    @Override
    public void loadSpuByPage() {
        // 使用先循环,再判断的循环结构do-while
        int i=1;
        int pages=0;
        do{
            // Dubbo调用当前页所有spu信息
            JsonPage<Spu> spus=forFrontSpuService.getSpuByPage(i,2);
            // 实例化一个SpuForElastic集合,以备添加元素,要依靠它向ES中新增信息
            List<SpuForElastic> esSpus= new ArrayList<>();
            // 变量查询出的所有spu,转换成SpuForElastic添加到上面的集合中
            for (Spu spu: spus.getList()){
                SpuForElastic esSpu=new SpuForElastic();
                BeanUtils.copyProperties(spu,esSpu);
                esSpus.add(esSpu);
            }
            // 执行新增
            spuRepository.saveAll(esSpus);
            i++;
            pages=spus.getTotalPage();
        }while (i<=pages);
    }


    @Override
    public JsonPage<SpuEntity> search(String keyword, Integer page, Integer pageSize) {
        return null;
    }


}







