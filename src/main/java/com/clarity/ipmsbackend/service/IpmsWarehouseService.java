package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.warehouse.AddWarehouseRequest;
import com.clarity.ipmsbackend.model.dto.warehouse.UpdateWarehouseRequest;
import com.clarity.ipmsbackend.model.entity.IpmsWarehouse;
import com.clarity.ipmsbackend.model.vo.SafeCustomerVO;
import com.clarity.ipmsbackend.model.vo.SafeWarehouseVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_warehouse(仓库表)】的数据库操作Service
* @createDate 2023-02-27 19:51:46
*/
public interface IpmsWarehouseService extends IService<IpmsWarehouse> {

    /**
     * 仓库编号自动生成
     *
     * @return
     */
    String warehouseCodeAutoGenerate();

    /**
     * 增加仓库
     *
     * @param addWarehouseRequest
     * @return
     */
    int addWarehouse(AddWarehouseRequest addWarehouseRequest);

    /**
     * 根据 id 删除仓库
     *
     * @param id
     * @return
     */
    int deleteWarehouseById(long id);

    /**
     * 更新仓库
     *
     * @param updateWarehouseRequest
     * @return
     */
    int updateWarehouse(UpdateWarehouseRequest updateWarehouseRequest);

    /**
     * 分页查询仓库，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeWarehouseVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);
}
