package cn.tedu.mall.seckill.service.impl;

import cn.tedu.mall.common.restful.JsonPage;
import cn.tedu.mall.pojo.product.vo.SpuStandardVO;
import cn.tedu.mall.pojo.seckill.model.SeckillSpu;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuDetailSimpleVO;
import cn.tedu.mall.pojo.seckill.vo.SeckillSpuVO;
import cn.tedu.mall.product.service.seckill.IForSeckillSkuService;
import cn.tedu.mall.product.service.seckill.IForSeckillSpuService;
import cn.tedu.mall.seckill.mapper.SeckillSpuMapper;
import cn.tedu.mall.seckill.service.ISeckillSpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SeckillSpuServiceImpl implements ISeckillSpuService {

    @Autowired
    private SeckillSpuMapper seckillSpuMapper;
    // 利用product模块根据spuid查询spu信息的方法,保证返回的spu列表包含详细信息
    @DubboReference
    private IForSeckillSpuService dubboSeckillSpuService;

    @Override
    public JsonPage<SeckillSpuVO> listSeckillSpus(Integer page, Integer pageSize) {
        // 查询分页结果,使用PageHelper设置分页查询内容
        PageHelper.startPage(page,pageSize);
        List<SeckillSpu> seckillSpus=seckillSpuMapper.selectSeckillSpus();
        // 下面进行数据封装
        // 准备一个包含更详细信息返回值类型的泛型集合
        List<SeckillSpuVO> seckillSpuVOs=new ArrayList<>();
        for(SeckillSpu seckillSpu: seckillSpus){
            // 根据spuid查询到spu详情
            Long spuId=seckillSpu.getSpuId();
            // 根据spuId 查询Spu详情(Dubbo调用)
            SpuStandardVO spuStandardVO=
                dubboSeckillSpuService.getSpuById(spuId);
            // 实例化包含spu详情和秒杀详情的对象
            SeckillSpuVO seckillSpuVO=new SeckillSpuVO();
            // 将spu详情赋值给SeckillSpuVO对象
            BeanUtils.copyProperties(spuStandardVO,seckillSpuVO);
            // 取出秒杀价
            BigDecimal seckillListPrice=seckillSpu.getListPrice();
            // 向seckillSpuVO中赋值秒杀相关的属性,秒杀价,秒杀开始时间,秒杀结束时间
            seckillSpuVO.setSeckillListPrice(seckillListPrice);
            seckillSpuVO.setStartTime(seckillSpu.getStartTime());
            seckillSpuVO.setEndTime(seckillSpu.getEndTime());
            // 将既有秒杀信息,又有spu详情的seckillSpuVO添加到集合中
            seckillSpuVOs.add(seckillSpuVO);
        }
        // 将分页查询结果PageInfo转换为JsonPage返回
        return JsonPage.restPage(new PageInfo<>(seckillSpuVOs));
    }

    @Override
    public SeckillSpuVO getSeckillSpu(Long spuId) {
        return null;
    }

    @Override
    public SeckillSpuDetailSimpleVO getSeckillSpuDetail(Long spuId) {
        return null;
    }
}
