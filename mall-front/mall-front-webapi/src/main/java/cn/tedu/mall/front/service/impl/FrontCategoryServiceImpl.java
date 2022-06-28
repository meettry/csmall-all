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
        for(CategoryStandardVO categoryStandardVO : categoryStandardVOs){
            // 因为CategoryStandardVO类型中没有children属性保存子分类对象
            // 所以我们要使用FrontCategoryEntity来保存同名属性
            FrontCategoryEntity frontCategoryEntity=new FrontCategoryEntity();
            // 利用BeanUtils将categoryStandardVO同名属性赋值给frontCategoryEntity
            BeanUtils.copyProperties(categoryStandardVO,frontCategoryEntity);
            // 提取当前分类对象的父级id(parentId)
            Long parentId=frontCategoryEntity.getParentId();
            // 判断当前的父级Id是否在Map中已经存在
            if(!map.containsKey(parentId)){
                // 如果parentId是第一次出现,就要向map中添加一个元素
                List<FrontCategoryEntity> value=new ArrayList<>();
                value.add(frontCategoryEntity);
                map.put(parentId,value);
            }else{
                // 如果当前以parentId值作为key的map元素已经存在,
                // 我们就向当前map元素的List集合中添加当前分类对象即可
                map.get(parentId).add(frontCategoryEntity);
            }
        }
        log.info("当前map中包含父级id的个数为:{}",map.size());
        // 第二部分,将每个分类对象关联到正确父分类对象中
        // 我们已经获得了每个父分类包含了内些子分类的数据
        // 下面就可以从根分类开始,通过循环遍历将每个分类对象包含的子分类添加到children属性中
        // 因为根分类id为0,所以先key为0的获取
        List<FrontCategoryEntity> firstLevels=map.get(0L);
        //判断根分类是否为null
        if(firstLevels==null){
            throw new CoolSharkServiceException(ResponseCode.BAD_REQUEST,"当前项目没有根分类");
        }
        // 首先遍历我们从Map中获取的所有根分类
        for(FrontCategoryEntity oneLevel: firstLevels){
            // 获得当前根分类对象的id
            Long secondLevelParentId=oneLevel.getId();
            // map中获取当前分类对象的所有子分类的集合
            List<FrontCategoryEntity> secondLevels=map.get(secondLevelParentId);
            // 判断二级分类是否为null
            if(secondLevels==null){
                log.warn("当前分类缺少二级分类内容:{}",secondLevelParentId);
                // 为了防止空集合遍历发生异常,我们直接跳过本次循环,运行下次循环
                continue;
            }
            // 遍历当前根分类的所有二级分类
            for(FrontCategoryEntity twoLevel : secondLevels){
                // 获得当前二级分类对象id,作为三级分类的父id保存
                Long thirdLevelParentId = twoLevel.getId();
                // 获得当前二级分类的所有子元素
                List<FrontCategoryEntity> thirdLevels=map.get(thirdLevelParentId);
                if(thirdLevels==null){
                    log.warn("当前分类缺少三级分类内容:{}",thirdLevelParentId);
                    continue;
                }
                // 将三级分类对象集合赋值给二级分类对象的children属性
                twoLevel.setChildrens(thirdLevels);
            }
            // 将二级分类对象集合赋值给一级分类对象的children属性
            oneLevel.setChildrens(secondLevels);
        }
        // 将转换完成的所有一级分类对象,按方法要求返回
        FrontCategoryTreeVO<FrontCategoryEntity> treeVO=new FrontCategoryTreeVO<>();
        // 向对象中属性赋值
        treeVO.setCategories(firstLevels);
        // 别忘了修改返回treeVO
        return treeVO;

    }

}
