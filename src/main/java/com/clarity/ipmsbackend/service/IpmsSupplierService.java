package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.supplier.AddSupplierRequest;
import com.clarity.ipmsbackend.model.dto.supplier.UpdateSupplierRequest;
import com.clarity.ipmsbackend.model.entity.IpmsSupplier;
import com.clarity.ipmsbackend.model.vo.SafeSupplierVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_supplier(供应商表)】的数据库操作Service
* @createDate 2023-02-27 10:02:53
*/
public interface IpmsSupplierService extends IService<IpmsSupplier> {

    /**
     * 供应商编号自动生成
     *
     * @return
     */
    String supplierCodeAutoGenerate();

    /**
     * 增加供应商
     *
     * @param addSupplierRequest
     * @return
     */
    int addSupplier(AddSupplierRequest addSupplierRequest);

    /**
     * 根据 id 删除供应商
     *
     * @param id
     * @return
     */
    int deleteSupplierById(long id);

    /**
     * 更新供应商
     *
     * @param updateSupplierRequest
     * @return
     */
    int updateSupplier(UpdateSupplierRequest updateSupplierRequest);

    /**
     * 分页查询供应商，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeSupplierVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);

    /**
     * 实现供应商金额的增加
     *
     * @param supplierId
     * @param enterprisePayBalance
     * @return
     */
    int addEnterprisePayBalance(long supplierId, double enterprisePayBalance);

    /**
     * 实现供应商金额的减少
     *
     * @param supplierId
     * @param enterprisePayBalance
     * @return
     */
    int reduceEnterprisePayBalance(long supplierId, double enterprisePayBalance);
}
