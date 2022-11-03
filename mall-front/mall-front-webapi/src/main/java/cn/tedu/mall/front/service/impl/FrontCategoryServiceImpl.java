package cn.tedu.mall.front.service.impl;

import cn.tedu.mall.common.exception.CoolSharkServiceException;
import cn.tedu.mall.common.restful.ResponseCode;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Meettry
 * @date 2022/10/31 10:21
 */
@DubboService
@Slf4j
@Service
public class FrontCategoryServiceImpl implements IFrontCategoryService {

    // 装配Dubbo对象，完成Product模块查询全部分类对象集合的方法
    // front模块不连数据库，是消费者
    @DubboReference
    private IForFrontCategoryService dubboCategoryService;
    // 装配操作Redis对象
    @Autowired
    private RedisTemplate redisTemplate;

    //开发过程中，使用Redis的规范要求所有代码中使用Redis的Key，都要定义为常量来避免错误
    public static final String CATEGORY_TREE_KEY = "category_tree";

    @Override
    public FrontCategoryTreeVO categoryTree() {
        // 方法中先检查Redis中是否保存了三级分类树对象
        if (redisTemplate.hasKey(CATEGORY_TREE_KEY)) {
            //redis中如果已经保存了这个key，直接获取
            FrontCategoryTreeVO<FrontCategoryEntity> treeVO =
                    (FrontCategoryTreeVO<FrontCategoryEntity>)
                            redisTemplate.boundValueOps(CATEGORY_TREE_KEY).get();
            // 将从redis中获取的treeVO返回
            // redis中不存集合，只存对象
            return treeVO;
        }
        // redis中没有三级分类树信息，表示本次访问可能是首次访问
        // 就要进行连接数据库查询数据后，构建三级分类树结构，再保存到Redis的业务流程
        // dubbo调用穿所有分类对象的数据
        List<CategoryStandardVO> categoryStandardVOS = dubboCategoryService.getCategoryList();
        // CategoryStandardVO是没有children属性的,FrontCategoryEntity是有的。
        // 编写一个方法，将子分类镀锡保存到对应的父分类对象的childern
        // 大致思路就是将FrontCategoryTreeVO转换为FrontCategoryEntity
        // 转换和构建过程比较复杂，我们专门编写一个类完成
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO = initTree(categoryStandardVOS);
        // 将三级分类树保存在redis中
        // 学习时使用1分钟，实际开发一般较久，24h左右
        redisTemplate.boundValueOps(CATEGORY_TREE_KEY).set(treeVO,1, TimeUnit.MINUTES);
        // 返回 treeVO
        return treeVO;
    }

    /**
     * 创建三级分类树
     * @param categoryStandardVOs
     * @return 三级分类树数据
     */
    private FrontCategoryTreeVO<FrontCategoryEntity> initTree(List<CategoryStandardVO> categoryStandardVOs) {

        // 第一步：
        // 确保所有分类的父类别id
        // 以父分类id为 Key，以分类对象为value保存在一个Map中
        // 一个父分类可以包含东哥子分类对象，所以这个Map的Value是个List
        Map<Long, List<FrontCategoryEntity>> map = new HashMap<>();
        log.info("当前分类总数量，：{}", categoryStandardVOs.size());
        // 遍历数据库查出来的对象的所有分类对象集合
        for (CategoryStandardVO categoryStandardVO : categoryStandardVOs) {
            // 因为CategoryStandardVO没有children属性不能保存子分类对象
            // 所以要将CategoryStandardVO对象转换为能够保存children属性的FrontCategoryEntity
            FrontCategoryEntity frontCategoryEntity = new FrontCategoryEntity();
            // 同名属性赋值
            BeanUtils.copyProperties(categoryStandardVO, frontCategoryEntity);
            // 获取当前分类对象的父分类id，用作Map中的Key（固若父分类id为0，表示一级分类）
            // 将父分类id取出，以便后续使用
            Long parentId = frontCategoryEntity.getParentId();
            //判断这个父分类id是否已经在map中作为Key出现
            if (map.containsKey(parentId)) {
                //如果当前map已经存在这个key，直接将当前分类对象添加到Value的集合中
                map.get(parentId).add(frontCategoryEntity);
            } else {
                // 如果当前map中没有这个key，那么直接创建这个key-value
                // 要先实例化一个List对象，作为新key的value
                List<FrontCategoryEntity> value = new ArrayList<>();
                value.add(frontCategoryEntity);
                // 再将parentId和value添加到map中
                map.put(parentId, value);
            }
        }

        // 第二步
        // 将子分类对象关联到对应的父分类对象的children属性中
        // 先获得所有一级分类对象
        List<FrontCategoryEntity> firstLevels = map.get(0L);
        // 判断一级分类集合如果为null，直接抛出异常
        if (firstLevels == null || firstLevels.isEmpty()) {
            String message = "缺失一级分类对象！";
            log.warn(message);
            throw new CoolSharkServiceException(ResponseCode.INTERNAL_SERVER_ERROR, message);
        }
        // 遍历一级分类集合
        for (FrontCategoryEntity oneLevel : firstLevels) {
            // 获取当前一级分类对象的id
            Long secondLevelParentId = oneLevel.getId();
            // 根据上面一级分类的id,获得对应的二级分类的集合
            List<FrontCategoryEntity> secondLevels = map.get(secondLevelParentId);
            // 判断二级分类是否为空
            if (secondLevels == null || secondLevels.isEmpty()) {
                // 二级分类缺失不用抛出异常，报出警告即可继续执行
                log.warn("当前分类没有二级分类内容：{}", secondLevelParentId);
                continue;
            }
            // 确定二级分类对象后，遍历二级分类对象集合
            for (FrontCategoryEntity twoLevel : secondLevels) {
                // 获取当前二级分类对象的id
                Long thirdLevelParentId = twoLevel.getId();
                // 根据上面二级分类的id,获得对应的三级分类的集合
                List<FrontCategoryEntity> thirdLevels = map.get(thirdLevelParentId);
                // 判断三级分类是否为空
                if (thirdLevels == null || thirdLevels.isEmpty()) {
                    log.warn("当前二级分类没有三级分类内容：{}", thirdLevelParentId);
                    continue;
                }
                // 将三级分类对象集合，添加到当前二级分类对象的children属性中
                twoLevel.setChildrens(thirdLevels);
            }
            // 将二级分类对象集合(已经赋好值的对象集合)，添加到当前一级对象分类对象的children属性中
            oneLevel.setChildrens(secondLevels);
        }
        // 到此为止，所有的分类对象都应该正确保存到了自己对应的父分类的children属性中
        // 但是最后要将一级分类的集合，firstLevels赋值给FrontCategoryTreeVO<FrontCategoryEntity>
        // 所以要先实例化它，再给他赋值，返回
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO = new FrontCategoryTreeVO<>();
        treeVO.setCategories(firstLevels);
        return treeVO;
    }
}
