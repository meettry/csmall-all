package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.front.service.IFrontCategoryService;
import cn.tedu.mall.pojo.front.entity.FrontCategoryEntity;
import cn.tedu.mall.pojo.front.vo.FrontCategoryTreeVO;
import cn.tedu.mall.pojo.product.vo.CategoryStandardVO;
import cn.tedu.mall.product.service.front.IForFrontCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@DubboService
@Service
@Slf4j
public class FrontCategoryServiceImpl implements IFrontCategoryService {

    // 设置Redis中保存三级分类的常量字符串
    public static final String CATEGORY_TREE_KEY="category_tree";
    // 利用Dubbo注解消费 product模块中提供的方法
    @DubboReference
    private IForFrontCategoryService forFrontCategoryService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public FrontCategoryTreeVO categoryTree() {
        // 先检查redis中是否包含key为CATEGORY_TREE_KEY的数据
        if(redisTemplate.hasKey(CATEGORY_TREE_KEY)){
            // 如果有拿出来返回即可
            FrontCategoryTreeVO<FrontCategoryEntity> treeVO=
            (FrontCategoryTreeVO<FrontCategoryEntity>)redisTemplate
                                            .boundValueOps(CATEGORY_TREE_KEY).get();
            return treeVO;
        }
        // 如果没有数据,dubbo调用查询所有分类对象的方法
        List<CategoryStandardVO> categoryStandardVOs=
                forFrontCategoryService.getCategoryList();
        // 然后构建三级分类树返回
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO=initTree(categoryStandardVOs);
        // 成功获得所有分类树信息后,将这个对象保存到Redis以便后面的访问直接从Redis中获取
        redisTemplate.boundValueOps(CATEGORY_TREE_KEY).set(treeVO,24, TimeUnit.HOURS);
        // 千万别忘了返回!!!!
        return treeVO;
    }

    private FrontCategoryTreeVO<FrontCategoryEntity> initTree(List<CategoryStandardVO> categoryStandardVOs) {
        // 定义一个Map,这个map使用当前一级分类的id做key,使用当前一级分类下所有子分类做Value
        Map<Long,List<FrontCategoryEntity>> map=new HashMap<>();
        // 日志输出当前所有分类总数
        log.info("当前总分类个数:{}",categoryStandardVOs.size());
        // 下面这个循环的目标是将所有一级分类从所有分类对象中提取出来,保存到map中
        // 提取方式是检查parentId值为0
        for(CategoryStandardVO categoryStandardVO: categoryStandardVOs){
            // 因为CategoryStandardVO类型只包含基本的分类对象数据
            // 要想出现级别包含关系,需要一个能够包含childrens属性的实体类
            FrontCategoryEntity frontCategoryEntity=new FrontCategoryEntity();
            // 利用工具类将CategoryStandardVO对象的同名属性赋值给FrontCategoryEntity对象
            BeanUtils.copyProperties(categoryStandardVO,frontCategoryEntity);
            // 提取出当前正则遍历的分类对象的父级id(parent_id)
            Long parentId=frontCategoryEntity.getParentId();
            // 判断这个父级id是不是0
            if(parentId.equals(0)){
                // 如果是0,表示当前对象是一级分类,添加到上面定义的map中
                //map.put(frontCategoryEntity.getId(),)
            }
        }
        return null;
    }
}
