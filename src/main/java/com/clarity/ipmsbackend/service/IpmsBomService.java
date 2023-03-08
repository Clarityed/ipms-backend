package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.bom.AddBomRequest;
import com.clarity.ipmsbackend.model.dto.bom.UpdateBomRequest;
import com.clarity.ipmsbackend.model.entity.IpmsBom;
import com.clarity.ipmsbackend.model.vo.SafeBomVO;

import javax.servlet.http.HttpServletRequest;

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
}
