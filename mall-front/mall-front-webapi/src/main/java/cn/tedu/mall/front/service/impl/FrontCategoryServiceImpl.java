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
        // 定义一个Map,这个map使用父分类的id做key,使用当前父分类下所有子分类做Value
        Map<Long,List<FrontCategoryEntity>> map=new HashMap<>();
        // 日志输出当前所有分类总数
        log.info("当前总分类个数:{}",categoryStandardVOs.size());
        // 下面这个循环的目标是将所有父分类从所有分类对象中提取出来,并包含它的子分类集合,保存到map中
        // 提取方式是检查parentId值为0
        for(CategoryStandardVO categoryStandardVO: categoryStandardVOs){
            // 因为CategoryStandardVO类型只包含基本的分类对象数据
            // 要想出现级别包含关系,需要一个能够包含childrens属性的实体类
            FrontCategoryEntity frontCategoryEntity=new FrontCategoryEntity();
            // 利用工具类将CategoryStandardVO对象的同名属性赋值给FrontCategoryEntity对象
            BeanUtils.copyProperties(categoryStandardVO,frontCategoryEntity);
            // 提取出当前正则遍历的分类对象的父级id(parent_id)
            Long parentId=frontCategoryEntity.getParentId();
            // 判断这个父级在map中是否已经存在
            if(!map.containsKey(parentId)){
                // 如果是不存在,表示当前对象是父级分类,先实例化一个List,再保存在Map中
                List<FrontCategoryEntity> value=new ArrayList<>();
                value.add(frontCategoryEntity);
                map.put(parentId,value);
            }else{
                // 如果不是存在.证明不是父级分类,不是父级分类就一定可以绑定到另一个父级分类中
                map.get(parentId).add(frontCategoryEntity);
            }

        }
        log.info("当前map数据包含父级id的个数:{}",map.size());
        // 上面的循环完成后我们只能获得所有父级分类包含哪些子分类
        // 但是他们并不能确实哪个是一级哪个是二级
        // 下面要将map中所有的父级分类的关系保存在正确的节点位置
        // 数据库设计了根节点也就是一级分类的父分类id为0,所以先获得父分类为0的所有一级分类
        List<FrontCategoryEntity> firstLevels=map.get(0L);
        if(firstLevels==null){
            throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,"您访问的分类树没有父级");
        }
        // 使用循环嵌套,完成二级和三级分类的构建
        // 外层循环完成二级分类赋值给一级分类的操作,内层循环完成三级分类赋值给二级分类的操作
        for(FrontCategoryEntity oneLevel:firstLevels){
            // 遍历所有一级分类,获得一级分类id,也就是二级分类的父id
            Long secondLevelParentId=oneLevel.getId();
            // 获得内存循环遍历的list,也就是当前二级分类包含的所有三级分类
            List<FrontCategoryEntity> secondLevels=map.get(secondLevelParentId);
            // 如果当前二级分类为空,抛出异常
            if(secondLevels==null){
                log.warn("当前二级分类没有内容:{}",secondLevelParentId);
                continue;
            }
            for(FrontCategoryEntity twoLevel:secondLevels){
                // 获得三级分类id,准备将三级分类添加到二级分类中
                Long thirdLevelParentId=twoLevel.getId();
                List<FrontCategoryEntity> thirdLevels=map.get(thirdLevelParentId);
                // 判断当前二级分类是否包含三级分类
                if(thirdLevels==null){
                    log.warn("当前三级分类没有内容:{}",thirdLevelParentId);
                    continue;
                }
                //没有进if表示当前三级分类有明确指定二级分类
                twoLevel.setChildrens(thirdLevels);
            }
            // 将非空的二级分类(因为经过了上面的if非空判断),增加到一级分类中
            oneLevel.setChildrens(secondLevels);
        }
        FrontCategoryTreeVO treeVO =new FrontCategoryTreeVO();
        treeVO.setCategories(firstLevels);
        // 别忘了返回treeVO
        return treeVO;
    }
}
