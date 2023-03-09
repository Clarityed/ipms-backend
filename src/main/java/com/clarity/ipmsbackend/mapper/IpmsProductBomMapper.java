package com.clarity.ipmsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clarity.ipmsbackend.model.entity.IpmsProductBom;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Clarity
* @description 针对表【ipms_product_bom(商品物料清单（BOM） 关系表)】的数据库操作Mapper
* @createDate 2023-03-05 11:57:41
* @Entity generator.entity.IpmsProductBom
*/
public interface IpmsProductBomMapper extends BaseMapper<IpmsProductBom> {

    /**
     * 获取作为 2 级 BOM 的原材料商品关系
     *
     * @return
     */
    List<IpmsProductBom> getAsTwoLevelBomMaterial();

    /**
     * 获取作为 1 级 BOM 和 2 BOM 的商品关系
     *
     * @return
     */
    List<IpmsProductBom> getAsProductOfBom();

    /**
     * 获取 2 级 BOM 商品的关系
     *
     * @return
     */
    List<IpmsProductBom> getTwoLevelBomProduct();

    /**
     * 获取 BOM 子级相关信息
     *
     * @param bomCode
     * @return
     */
    List<IpmsProductBom> getBomSubComponentMessage(@Param("bomCode") String bomCode);
}




