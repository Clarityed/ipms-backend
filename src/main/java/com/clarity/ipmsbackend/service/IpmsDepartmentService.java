package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.department.AddDepartmentRequest;
import com.clarity.ipmsbackend.model.dto.department.UpdateDepartmentRequest;
import com.clarity.ipmsbackend.model.entity.IpmsDepartment;
import com.clarity.ipmsbackend.model.vo.SafeDepartmentVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_department】的数据库操作Service
* @createDate 2023-02-22 17:27:35
*/
public interface IpmsDepartmentService extends IService<IpmsDepartment> {

    /**
     * 增加部门
     *
     * @param addDepartmentRequest
     * @param request
     * @return
     */
    int addDepartment(AddDepartmentRequest addDepartmentRequest, HttpServletRequest request);

    /**
     * 根据 id 删除部门
     *
     * @param id
     * @return
     */
    int deleteDepartmentById(long id);

    /**
     * 更新部门
     *
     * @param updateDepartmentRequest
     * @return
     */
    int updateDepartment(UpdateDepartmentRequest updateDepartmentRequest);

    /**
     * 分页查询用户，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeDepartmentVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);

    /**
     * 部门编号自动生成
     *
     * @return
     */
    String departmentCodeAutoGenerate();
}
