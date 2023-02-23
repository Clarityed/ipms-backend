package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.employee.AddEmployeeRequest;
import com.clarity.ipmsbackend.model.entity.IpmsDepartment;
import com.clarity.ipmsbackend.model.entity.IpmsEmployee;
import com.clarity.ipmsbackend.mapper.IpmsEmployeeMapper;
import com.clarity.ipmsbackend.service.IpmsDepartmentService;
import com.clarity.ipmsbackend.service.IpmsEmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
* @author Clarity
* @description 针对表【ipms_employee】的数据库操作Service实现
* @createDate 2023-02-23 15:30:41
*/
@Service
public class IpmsEmployeeServiceImpl extends ServiceImpl<IpmsEmployeeMapper, IpmsEmployee>
    implements IpmsEmployeeService{

    @Resource
    private IpmsEmployeeMapper ipmsEmployeeMapper;

    @Resource
    private IpmsDepartmentService ipmsDepartmentService;

    @Override
    public int addEmployee(AddEmployeeRequest addEmployeeRequest) {
        String employeeCode = addEmployeeRequest.getEmployeeCode();
        String employeeName = addEmployeeRequest.getEmployeeName();
        String employeePhoneNum = addEmployeeRequest.getEmployeePhoneNum();
        if (StringUtils.isAnyBlank(employeeCode, employeeName, employeePhoneNum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "职员编号或者名称或者手机号为空");
        }
        Long departmentId = addEmployeeRequest.getDepartmentId();
        if (departmentId == null || departmentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "职员 id 为空或者不合法");
        }
        // 判断部门是否存在
        IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
        if (department == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "部门不存在");
        }
        // 职员编码不能重复
        QueryWrapper<IpmsEmployee> employeeQueryWrapper = new QueryWrapper<>();
        employeeQueryWrapper.eq("employee_code", employeeCode);
        IpmsEmployee oldEmployee = ipmsEmployeeMapper.selectOne(employeeQueryWrapper);
        if (oldEmployee != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "职员编码重复");
        }
        // 插入数据
        IpmsEmployee employee = new IpmsEmployee();
        BeanUtils.copyProperties(addEmployeeRequest, employee);
        employee.setCreateTime(new Date());
        employee.setUpdateTime(new Date());
        int result = ipmsEmployeeMapper.insert(employee);
        if (result == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }
}




