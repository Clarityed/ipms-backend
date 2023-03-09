package com.clarity.ipmsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clarity.ipmsbackend.model.entity.IpmsBom;
import com.clarity.ipmsbackend.model.vo.bom.SafeForwardQueryBomVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Clarity
* @description 针对表【ipms_bom】的数据库操作Mapper
* @createDate 2023-03-05 11:54:06
* @Entity generator.entity.IpmsBom
*/
public interface IpmsBomMapper extends BaseMapper<IpmsBom> {

    /**
     * 获取 BOM 单父级商品信息及对应的单位信息
     *
     * @return
     */
    List<SafeForwardQueryBomVO> getBomFatherProduct(@Param("bomCode") String bomCode);

    /**
     * 获取 BOM 单 1 级商品信息及对应的单位信息
     *
     * @param bomId
     * @return
     */
    List<SafeForwardQueryBomVO> getBomOneLevelProduct(@Param("bomId") long bomId);
}




