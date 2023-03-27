package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.bom.AddBomRequest;
import com.clarity.ipmsbackend.model.dto.bom.UpdateBomRequest;
import com.clarity.ipmsbackend.model.entity.IpmsBom;
import com.clarity.ipmsbackend.model.vo.bom.SafeBomVO;
import com.clarity.ipmsbackend.model.vo.bom.SafeForwardQueryBomVO;
import com.clarity.ipmsbackend.model.vo.bom.SafeReverseQueryBomVO;
import com.clarity.ipmsbackend.model.vo.productionbill.SafeProductionTaskOrderProductVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Clarity
* @description 针对表【ipms_bom】的数据库操作Service
* @createDate 2023-03-05 11:54:06
*/
public interface IpmsBomService extends IService<IpmsBom> {

    /**
     * BOM 编号自动生成
     *
     * @return
     */
    String bomCodeAutoGenerate();

    /**
     * 增加 BOM
     *
     * @param addBomRequest
     * @param request
     * @return
     */
    int addBom(AddBomRequest addBomRequest, HttpServletRequest request);

    /**
     * 根据 id 删除 BOM
     *
     * @param id
     * @return
     */
    int deleteBomById(long id);

    /**
     * 更新 BOM
     *
     * @param updateBomRequest
     * @param request
     * @return
     */
    int updateBom(UpdateBomRequest updateBomRequest, HttpServletRequest request);

    /**
     * 分页查询 BOM，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeBomVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);

    /**
     * 审核 BOM
     *
     * @param bomId
     * @return
     */
    int checkBom(long bomId, HttpServletRequest request);

    /**
     * 反审核 BOM
     *
     * @param bomId
     * @return
     */
    int reverseCheckBom(long bomId);

    /**
     * 根据 BOM 编码获取 BOM 层级信息
     *
     * @param bomCode
     * @return
     */
    List<SafeForwardQueryBomVO> getBomLevelMessageByBomCode(String bomCode);

    /**
     * 根据商品编码获取子件的父级 BOM 商品
     *
     * @param productCode
     * @return
     */
    List<SafeReverseQueryBomVO> getBomFatherProductOfSubComponentByProductCode(String productCode);

    /**
     * 查询可作为 BOM 商品列表
     *
     * @param fuzzyQueryRequest
     * @return
     */
    Page<SafeProductionTaskOrderProductVO> selectCanAsBomProductList(FuzzyQueryRequest fuzzyQueryRequest);
}
