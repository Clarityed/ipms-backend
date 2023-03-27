package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.ProductionBillQueryRequest;
import com.clarity.ipmsbackend.model.dto.productionbill.AddProductionBillRequest;
import com.clarity.ipmsbackend.model.dto.productionbill.UpdateProductionBillRequest;
import com.clarity.ipmsbackend.model.entity.IpmsProductionBill;
import com.clarity.ipmsbackend.model.vo.productionbill.SafeProductionBillVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_production_bill(生产单据)】的数据库操作Service
* @createDate 2023-03-23 14:42:11
*/
public interface IpmsProductionBillService extends IService<IpmsProductionBill> {

    /**
     * 生产单据编号自动生成
     *
     * @param productionBillType 生产单据类型
     * @return 对应单据的下一个编号
     */
    String productionBillCodeAutoGenerate(String productionBillType);

    /**
     * 增加生产单据
     *
     * @param addProductionBillRequest 增加生产单据请求封装对象
     * @param request HttpServletRequest
     * @return 1 - 成功， 0 - 失败
     */
    int addProductionBill(AddProductionBillRequest addProductionBillRequest, HttpServletRequest request);

    /**
     * 审核生产单据
     *
     * @param productionBillId 生产单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int checkProductionBill(long productionBillId, HttpServletRequest request);

    /**
     * 反审核生产单据
     *
     * @param productionBillId 生产单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int reverseCheckProductionBill(long productionBillId, HttpServletRequest request);

    /**
     * 根据 id 删除 生产单据
     *
     * @param id 单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int deleteProductionBillById(long id);

    /**
     * 修改生产单据
     *
     * @param updateProductionBillRequest 修改生产单据请求封装对象
     * @param request HttpServletRequest
     * @return 1 - 成功， 0 - 失败
     */
    int updateProductionBill(UpdateProductionBillRequest updateProductionBillRequest, HttpServletRequest request);

    /**
     * 查询生产单据源单
     *
     * @param productionBillQueryRequest 生产单据查询请求
     * @return 为选单源提供的列表
     */
    Page<SafeProductionBillVO> selectSourceProductionBill(ProductionBillQueryRequest productionBillQueryRequest);

    /**
     * 分页查询生产单据，且数据脱敏，且支持模糊查询
     *
     * @param productionBillQueryRequest 生产单据查询请求
     * @return 对于类型单据信息列表
     */
    Page<SafeProductionBillVO> pagingFuzzyQuery(ProductionBillQueryRequest productionBillQueryRequest);
}
