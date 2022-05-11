package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.front.service.IFrontCategoryService;
import cn.tedu.mall.pojo.front.vo.FrontCategoryTreeVO;
import cn.tedu.mall.product.service.front.IForFrontCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
        // 如果有拿出来返回即可
        // 如果没有数据,dubbo调用查询所有分类对象的方法
        // 然后构建三级分类树返回

        return null;
    }
}
