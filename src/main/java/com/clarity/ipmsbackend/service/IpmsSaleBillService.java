package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.SaleBillQueryRequest;
import com.clarity.ipmsbackend.model.dto.salebill.AddSaleBillRequest;
import com.clarity.ipmsbackend.model.dto.salebill.UpdateSaleBillRequest;
import com.clarity.ipmsbackend.model.entity.IpmsSaleBill;
import com.clarity.ipmsbackend.model.vo.salebill.SafeSaleBillVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_sale_bill(销售单据)】的数据库操作Service
* @createDate 2023-03-21 10:28:09
*/
public interface IpmsSaleBillService extends IService<IpmsSaleBill> {

    /**
     * 销售单据编号自动生成
     *
     * @param saleBillType 销售单据类型
     * @return 对应单据的下一个编号
     */
    String saleBillCodeAutoGenerate(String saleBillType);

    /**
     * 增加销售单据
     *
     * @param addSaleBillRequest 增加销售单据请求封装对象
     * @param request HttpServletRequest
     * @return 1 - 成功， 0 - 失败
     */
    int addSaleBill(AddSaleBillRequest addSaleBillRequest, HttpServletRequest request);

    /**
     * 审核销售单据
     *
     * @param saleBillId 销售单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int checkSaleBill(long saleBillId, HttpServletRequest request);

    /**
     * 反审核销售单据
     *
     * @param saleBillId 销售单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int reverseCheckSaleBill(long saleBillId, HttpServletRequest request);

    /**
     * 根据 id 删除 销售单据
     *
     * @param id 单据 id
     * @return 1 - 成功， 0 - 失败
     */
    int deleteSaleBillById(long id);

    /**
     * 修改销售单据
     *
     * @param updateSaleBillRequest 修改销售单据请求封装对象
     * @param request HttpServletRequest
     * @return 1 - 成功， 0 - 失败
     */
    int updateSaleBill(UpdateSaleBillRequest updateSaleBillRequest, HttpServletRequest request);

    /**
     * 销售单据查询（可作为选单源功能，销售单据列表查询功能，并且也支持模糊查询）
     *
     * @param saleBillQueryRequest 销售单据查询请求
     * @return 为选单源提供的列表
     */
    Page<SafeSaleBillVO> selectSaleBill(SaleBillQueryRequest saleBillQueryRequest);
}
