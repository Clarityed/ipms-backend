package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsDepartmentMapper;
import com.clarity.ipmsbackend.model.dto.department.AddDepartmentRequest;
import com.clarity.ipmsbackend.model.dto.department.UpdateDepartmentRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.model.vo.SafeDepartmentVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.*;
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
* @description 针对表【ipms_department】的数据库操作Service实现
* @createDate 2023-02-22 17:27:35
*/
@Service
@Slf4j
public class IpmsDepartmentServiceImpl extends ServiceImpl<IpmsDepartmentMapper, IpmsDepartment>
    implements IpmsDepartmentService{

    @Resource
    private IpmsUserService ipmsUserService;

    @Resource
    private IpmsDepartmentMapper ipmsDepartmentMapper;

    @Resource
    private IpmsEnterpriseService ipmsEnterpriseService;

    @Resource
    private IpmsEmployeeService ipmsEmployeeService;

    @Resource
    private IpmsPurchaseBillService ipmsPurchaseBillService;

    @Resource
    private IpmsSaleBillService ipmsSaleBillService;

    @Resource
    private IpmsProductionBillService ipmsProductionBillService;

    @Resource
    private IpmsInventoryBillService ipmsInventoryBillService;

    @Override
    public int addDepartment(AddDepartmentRequest addDepartmentRequest, HttpServletRequest request) {
        // 1. 校验不能为空的参数
        String departmentCode = addDepartmentRequest.getDepartmentCode();
        String departmentName = addDepartmentRequest.getDepartmentName();
        if (StringUtils.isAnyBlank(departmentCode, departmentName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "部门编码或者部门名称为空");
        }
        // 2. 部门编号不能重复
        QueryWrapper<IpmsDepartment> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper.eq("department_code", departmentCode);
        IpmsDepartment oldDepartment = ipmsDepartmentMapper.selectOne(departmentQueryWrapper);
        if (oldDepartment != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "部门编号重复");
        }
        // 3. 插入数据
        IpmsDepartment department = new IpmsDepartment();
        BeanUtils.copyProperties(addDepartmentRequest, department);
        //     所属企业系统内部设置，从管理员登录态里获取
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        department.setEnterpriseId(loginUser.getEnterpriseId());
        //     上级部门可以为空，如果不为空设置所属部门 id（前端需要一个接口可以查询部门）
        department.setCreateTime(new Date());
        department.setUpdateTime(new Date());
        return ipmsDepartmentMapper.insert(department);
    }

    @Override
    public int deleteDepartmentById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        // 如果部门下有成员无法删除该部门
        QueryWrapper<IpmsEmployee> employeeQueryWrapper = new QueryWrapper<>();
        employeeQueryWrapper.eq("department_id", id);
        List<IpmsEmployee> employeeList = ipmsEmployeeService.list(employeeQueryWrapper);
        if (employeeList.size() != 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该部门下还要成员");
        }
        QueryWrapper<IpmsInventoryBill> ipmsInventoryBillQueryWrapper = new QueryWrapper<>();
        ipmsInventoryBillQueryWrapper.eq("department_id", id);
        List<IpmsInventoryBill> validAsInventoryBill = ipmsInventoryBillService.list(ipmsInventoryBillQueryWrapper);
        if (validAsInventoryBill != null && validAsInventoryBill.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "部门已被使用");
        }
        QueryWrapper<IpmsSaleBill> ipmsSaleBillQueryWrapper = new QueryWrapper<>();
        ipmsSaleBillQueryWrapper.eq("department_id", id);
        List<IpmsSaleBill> validAsSaleBill = ipmsSaleBillService.list(ipmsSaleBillQueryWrapper);
        if (validAsSaleBill != null && validAsSaleBill.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "部门已被使用");
        }
        QueryWrapper<IpmsProductionBill> ipmsProductionBillQueryWrapper = new QueryWrapper<>();
        ipmsProductionBillQueryWrapper.eq("department_id", id);
        List<IpmsProductionBill> validAsProductionBill = ipmsProductionBillService.list(ipmsProductionBillQueryWrapper);
        if (validAsProductionBill != null && validAsProductionBill.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "部门已被使用");
        }
        QueryWrapper<IpmsPurchaseBill> ipmsPurchaseBillQueryWrapper = new QueryWrapper<>();
        ipmsPurchaseBillQueryWrapper.eq("department_id", id);
        List<IpmsPurchaseBill> validAsPurchaseBill = ipmsPurchaseBillService.list(ipmsPurchaseBillQueryWrapper);
        if (validAsPurchaseBill != null && validAsPurchaseBill.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "部门已被使用");
        }
        int result = ipmsDepartmentMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int updateDepartment(UpdateDepartmentRequest updateDepartmentRequest) {
        Long departmentId = updateDepartmentRequest.getDepartmentId();
        if (departmentId == null || departmentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 为空或者不合法");
        }
        // 判断部门是否存在
        IpmsDepartment department = ipmsDepartmentMapper.selectById(departmentId);
        if (department == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 部门编号必须相同无法修改，且验证了部门是否存在
        String departmentCode = updateDepartmentRequest.getDepartmentCode();
        if (departmentCode != null) {
            QueryWrapper<IpmsDepartment> departmentQueryWrapper = new QueryWrapper<>();
            departmentQueryWrapper.eq("department_id", departmentId);
            IpmsDepartment oldDepartment = ipmsDepartmentMapper.selectOne(departmentQueryWrapper);
            if (!departmentCode.equals(oldDepartment.getDepartmentCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "部门编号必须相同无法修改");
            }
        }
        // 更新数据
        IpmsDepartment newDepartment = new IpmsDepartment();
        BeanUtils.copyProperties(updateDepartmentRequest, newDepartment);
        newDepartment.setUpdateTime(new Date());
        int result = ipmsDepartmentMapper.updateById(newDepartment);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public Page<SafeDepartmentVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Page<IpmsDepartment> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsDepartment> departmentQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (StringUtils.isNotBlank(fuzzyText)) {
            departmentQueryWrapper.like("department_id", fuzzyText).or()
                    .like("department_code", fuzzyText).or()
                    .like("department_name", fuzzyText).or()
                    .like("department_super", fuzzyText).or()
                    .like("department_description", fuzzyText).or();
        }
        Page<IpmsDepartment> departmentPage = ipmsDepartmentMapper.selectPage(page, departmentQueryWrapper);
        List<SafeDepartmentVO> safeDepartmentVOList = departmentPage.getRecords().stream().map(ipmsDepartment -> {
            SafeDepartmentVO safeDepartmentVO = new SafeDepartmentVO();
            BeanUtils.copyProperties(ipmsDepartment, safeDepartmentVO);
            Long departmentSuper = ipmsDepartment.getDepartmentSuper();
            if (departmentSuper != null) {
                IpmsDepartment superDepartment = ipmsDepartmentMapper.selectById(departmentSuper);
                safeDepartmentVO.setDepartmentSuperName(superDepartment.getDepartmentName());
            } else {
                IpmsEnterprise enterprise = ipmsEnterpriseService.getEnterprise(request);
                safeDepartmentVO.setDepartmentSuperName(enterprise.getEnterpriseName());
            }
            return safeDepartmentVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeDepartmentVO> safeDepartmentVOPage = new PageDTO<>(departmentPage.getCurrent(), departmentPage.getSize(), departmentPage.getTotal());
        safeDepartmentVOPage.setRecords(safeDepartmentVOList);
        return safeDepartmentVOPage;
    }

    @Override
    public String departmentCodeAutoGenerate() {
        QueryWrapper<IpmsDepartment> ipmsDepartmentQueryWrapper = new QueryWrapper<>();
        List<IpmsDepartment> ipmsDepartmentList = ipmsDepartmentMapper.selectList(ipmsDepartmentQueryWrapper);
        String departmentCode;
        if (ipmsDepartmentList.size() == 0) {
            departmentCode = "BM00000";
        } else {
            IpmsDepartment lastDepartment = ipmsDepartmentList.get(ipmsDepartmentList.size() - 1);
            departmentCode = lastDepartment.getDepartmentCode();
        }
        String nextDepartmentCode = null;
        try {
            nextDepartmentCode = CodeAutoGenerator.generatorCode(departmentCode);
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextDepartmentCode;
    }
}




