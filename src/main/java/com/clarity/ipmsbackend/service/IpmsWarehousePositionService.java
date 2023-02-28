package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.warehouse.position.AddWarehousePositionRequest;
import com.clarity.ipmsbackend.model.dto.warehouse.position.UpdateWarehousePositionRequest;
import com.clarity.ipmsbackend.model.entity.IpmsWarehousePosition;
import com.clarity.ipmsbackend.model.vo.SafeWarehousePositionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_warehouse_position(仓位表)】的数据库操作Service
* @createDate 2023-02-28 11:07:12
*/
public interface IpmsWarehousePositionService extends IService<IpmsWarehousePosition> {

    /**
     * 仓位编号自动生成
     *
     * @return
     */
    String warehousePositionCodeAutoGenerate();


    /**
     * 增加仓位
     *
     * @param addWarehousePositionRequest
     * @return
     */
    int addWarehousePosition(AddWarehousePositionRequest addWarehousePositionRequest);

    /**
     * 根据 id 删除仓位
     *
     * @param id
     * @return
     */
    int deleteWarehousePositionById(long id);

    /**
     * 更新仓位
     *
     * @param updateWarehousePositionRequest
     * @return
     */
    int updateWarehousePosition(UpdateWarehousePositionRequest updateWarehousePositionRequest);

    /**
     * 分页查询仓位，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeWarehousePositionVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);
}
