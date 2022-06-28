package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.front.service.IFrontCategoryService;
import cn.tedu.mall.pojo.front.entity.FrontCategoryEntity;
import cn.tedu.mall.pojo.front.vo.FrontCategoryTreeVO;
import cn.tedu.mall.pojo.product.vo.CategoryStandardVO;
import cn.tedu.mall.product.service.front.IForFrontCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
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

    // 项目中涉及Redis的信息读取,定义这个常量,降低拼写错误风险
    public static final String CATEGORY_TREE_KEY="category_tree";
    // 利用Dubbo获得可以连接数据库获得所有分类信息的业务逻辑层方法
    @DubboReference
    private IForFrontCategoryService dubboCategoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public FrontCategoryTreeVO categoryTree() {
        // 凡是有Redis读取检查的都是这个模式
        if(redisTemplate.hasKey(CATEGORY_TREE_KEY)){
            // 如果Redis中包含分类树信息,直接从Redis中获取返回即可
            FrontCategoryTreeVO<FrontCategoryEntity> treeVo=
                    (FrontCategoryTreeVO<FrontCategoryEntity>)
                            redisTemplate.boundValueOps(CATEGORY_TREE_KEY).get();
            return treeVo;
        }
        // 如果Redis中没有数据,证明本次运行需要从数据库查询所有分类,拼接从三级分类树并返回
        // 首先一定是先要从数据库中查询所有分类对象
        List<CategoryStandardVO> categoryStandardVOs=
                                    dubboCategoryService.getCategoryList();
        // 将所有分类对象关联成三级分类树返回
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO=initTree(categoryStandardVOs);
        // 将确定好的三级分类树保存到Redis
        redisTemplate.boundValueOps(CATEGORY_TREE_KEY)
                        .set(treeVO, 24 , TimeUnit.HOURS);
        // 千万别忘了返回!!!
        return treeVO;
    }

    private FrontCategoryTreeVO<FrontCategoryEntity> initTree(List<CategoryStandardVO> categoryStandardVOs) {
        // 第一部分,确定所有分类对象的父分类
        // 声明一个Map,这个map的Key是父分类的Id,这个Map的Value是当前父分类的所有子分类对象
        Map<Long,List<FrontCategoryEntity>> map=new HashMap<>();
        // 日志输出分类对象个数
        log.info("当前分类对象总数:{}",categoryStandardVOs.size());
        // 下面编写for循环,遍历categoryStandardVOs集合,将其中的所有元素保存到父分类id值对应的Map中
        // 根分类parentId是0


        // 第二部分,将每个分类对象关联到正确父分类对象中

    }

}
