package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.unit.AddUnitRequest;
import com.clarity.ipmsbackend.model.dto.unit.UpdateUnitRequest;
import com.clarity.ipmsbackend.model.entity.IpmsUnit;
import com.clarity.ipmsbackend.model.vo.SafeUnitVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_unit(计量单位表)】的数据库操作Service
* @createDate 2023-02-28 16:01:24
*/
public interface IpmsUnitService extends IService<IpmsUnit> {

    /**
     * 计量单位编号自动生成
     *
     * @return
     */
    String unitCodeAutoGenerate();


    /**
     * 增加计量单位
     *
     * @param addUnitRequest
     * @return
     */
    int addUnit(AddUnitRequest addUnitRequest);

    /**
     * 根据 id 删除计量单位
     *
     * @param id
     * @return
     */
    int deleteUnitById(long id);

    /**
     * 更新计量单位
     *
     * @param updateUnitRequest
     * @return
     */
    int updateUnit(UpdateUnitRequest updateUnitRequest);

    /**
     * 分页查询计量单位，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeUnitVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);
}
