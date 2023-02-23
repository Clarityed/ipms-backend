package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.department.UpdateDepartmentRequest;
import com.clarity.ipmsbackend.model.dto.employee.AddEmployeeRequest;
import com.clarity.ipmsbackend.model.dto.employee.UpdateEmployeeRequest;
import com.clarity.ipmsbackend.model.entity.IpmsEmployee;
import com.clarity.ipmsbackend.model.vo.SafeDepartmentVO;
import com.clarity.ipmsbackend.model.vo.SafeEmployeeVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_employee】的数据库操作Service
* @createDate 2023-02-23 15:30:41
*/
public interface IpmsEmployeeService extends IService<IpmsEmployee> {

    /**
     * 增加职员
     *
     * @param addEmployeeRequest
     * @return
     */
    int addEmployee(AddEmployeeRequest addEmployeeRequest);

    /**
     * 根据 id 删除职员
     *
     * @param id
     * @return
     */
    int deleteEmployeeById(long id);

    /**
     * 更新职员
     *
     * @param updateEmployeeRequest
     * @return
     */
    int updateEmployee(UpdateEmployeeRequest updateEmployeeRequest);

    /**
     * 分页查询用户，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeEmployeeVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);

    /**
     * 职员编号自动生成
     *
     * @return
     */
    String employeeCodeAutoGenerate();
}
