package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.model.dto.employee.AddEmployeeRequest;
import com.clarity.ipmsbackend.model.entity.IpmsEmployee;

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
}
