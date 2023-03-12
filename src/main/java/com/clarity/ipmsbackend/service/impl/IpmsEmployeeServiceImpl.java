package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsEmployeeMapper;
import com.clarity.ipmsbackend.model.dto.employee.AddEmployeeRequest;
import com.clarity.ipmsbackend.model.dto.employee.UpdateEmployeeRequest;
import com.clarity.ipmsbackend.model.entity.IpmsDepartment;
import com.clarity.ipmsbackend.model.entity.IpmsEmployee;
import com.clarity.ipmsbackend.model.vo.SafeEmployeeVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.IpmsDepartmentService;
import com.clarity.ipmsbackend.service.IpmsEmployeeService;
import com.clarity.ipmsbackend.service.IpmsUserService;
import com.clarity.ipmsbackend.utils.CodeAutoGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Clarity
* @description 针对表【ipms_employee】的数据库操作Service实现
* @createDate 2023-02-23 15:30:41
*/
@Service
@Slf4j
public class IpmsEmployeeServiceImpl extends ServiceImpl<IpmsEmployeeMapper, IpmsEmployee>
    implements IpmsEmployeeService{

    @Resource
    private IpmsEmployeeMapper ipmsEmployeeMapper;

    @Resource
    private IpmsDepartmentService ipmsDepartmentService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Override
    public int addEmployee(AddEmployeeRequest addEmployeeRequest) {
        // 校验不能为空的参数
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

    @Override
    public int deleteEmployeeById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        // todo 如果员工和订单有关联，无法删除员工，还有和仓库有关系
        int result = ipmsEmployeeMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int updateEmployee(UpdateEmployeeRequest updateEmployeeRequest) {
        Long employeeId = updateEmployeeRequest.getEmployeeId();
        if (employeeId == null || employeeId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 为空或者不合法");
        }
        // 判断职员是否存在
        IpmsEmployee employee = ipmsEmployeeMapper.selectById(employeeId);
        if (employee == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 职员编号必须相同无法修改，且验证了职员是否存在
        String employeeCode = updateEmployeeRequest.getEmployeeCode();
        if (employeeCode != null) {
            QueryWrapper<IpmsEmployee> employeeQueryWrapper = new QueryWrapper<>();
            employeeQueryWrapper.eq("employee_id", employeeId);
            IpmsEmployee oldEmployee = ipmsEmployeeMapper.selectOne(employeeQueryWrapper);
            if (!employeeCode.equals(oldEmployee.getEmployeeCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "职员编号必须相同无法修改");
            }
        }
        // 判断部门是否存在
        Long departmentId = updateEmployeeRequest.getDepartmentId();
        if (departmentId != null) {
            if (departmentId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "部门 id 不合法");
            }
            IpmsDepartment department = ipmsDepartmentService.getById(updateEmployeeRequest.getDepartmentId());
            if (department == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "部门不存在");
            }
        }
        // 更新数据
        IpmsEmployee newEmployee = new IpmsEmployee();
        BeanUtils.copyProperties(updateEmployeeRequest, newEmployee);
        newEmployee.setUpdateTime(new Date());
        int result = ipmsEmployeeMapper.updateById(newEmployee);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public Page<SafeEmployeeVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Page<IpmsEmployee> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsEmployee> employeeQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (StringUtils.isNotBlank(fuzzyText)) {
            employeeQueryWrapper.like("employee_id", fuzzyText).or()
                    .like("employee_code", fuzzyText).or()
                    .like("employee_name", fuzzyText).or()
                    .like("employee_phone_num", fuzzyText).or()
                    .like("employee_gender", fuzzyText).or()
                    .like("employee_email", fuzzyText).or()
                    .like("employee_wechat_num", fuzzyText).or()
                    .like("employee_opening_bank", fuzzyText).or()
                    .like("employee_bank_account", fuzzyText).or()
                    .like("employee_id_card_num", fuzzyText).or()
                    .like("employee_id_card_type", fuzzyText).or()
                    .like("employee_is_master", fuzzyText).or();
        }
        Page<IpmsEmployee> employeePage = ipmsEmployeeMapper.selectPage(page, employeeQueryWrapper);
        List<SafeEmployeeVO> safeEmployeeVOList = employeePage.getRecords().stream().map(ipmsEmployee -> {
            SafeEmployeeVO safeEmployeeVO = new SafeEmployeeVO();
            BeanUtils.copyProperties(ipmsEmployee, safeEmployeeVO);
            Long departmentId = ipmsEmployee.getDepartmentId();
            if (departmentId != null) {
                IpmsDepartment department = ipmsDepartmentService.getById(departmentId);
                safeEmployeeVO.setDepartmentName(department.getDepartmentName());
            }
            return safeEmployeeVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeEmployeeVO> safeEmployeeVOPage = new PageDTO<>(employeePage.getCurrent(), employeePage.getSize(), employeePage.getTotal());
        safeEmployeeVOPage.setRecords(safeEmployeeVOList);
        return safeEmployeeVOPage;
    }

    @Override
    public String employeeCodeAutoGenerate() {
        QueryWrapper<IpmsEmployee> ipmsEmployeeQueryWrapper = new QueryWrapper<>();
        List<IpmsEmployee> ipmsEmployeeList = ipmsEmployeeMapper.selectList(ipmsEmployeeQueryWrapper);
        String employeeCode;
        if (ipmsEmployeeList.size() == 0) {
            employeeCode = "ZY00000";
        } else {
            IpmsEmployee lastEmployee = ipmsEmployeeList.get(ipmsEmployeeList.size() - 1);
            employeeCode = lastEmployee.getEmployeeCode();
        }
        String nextEmployeeCode = null;
        try {
            nextEmployeeCode = CodeAutoGenerator.generatorCode(employeeCode);
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextEmployeeCode;
    }
}




