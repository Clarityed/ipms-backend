package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsSupplierMapper;
import com.clarity.ipmsbackend.model.dto.supplier.linkman.AddSupplierLinkmanRequest;
import com.clarity.ipmsbackend.model.dto.supplier.AddSupplierRequest;
import com.clarity.ipmsbackend.model.dto.supplier.linkman.UpdateSupplierLinkmanRequest;
import com.clarity.ipmsbackend.model.dto.supplier.UpdateSupplierRequest;
import com.clarity.ipmsbackend.model.entity.IpmsSupplier;
import com.clarity.ipmsbackend.model.entity.IpmsSupplierLinkman;
import com.clarity.ipmsbackend.model.vo.SafeSupplierLinkmanVO;
import com.clarity.ipmsbackend.model.vo.SafeSupplierVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.IpmsSupplierLinkmanService;
import com.clarity.ipmsbackend.service.IpmsSupplierService;
import com.clarity.ipmsbackend.service.IpmsUserService;
import com.clarity.ipmsbackend.utils.CodeAutoGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Clarity
* @description 针对表【ipms_supplier(供应商表)】的数据库操作Service实现
* @createDate 2023-02-27 10:02:53
*/
@Service
@Slf4j
public class IpmsSupplierServiceImpl extends ServiceImpl<IpmsSupplierMapper, IpmsSupplier>
    implements IpmsSupplierService{

    @Resource
    private IpmsSupplierMapper ipmsSupplierMapper;

    @Resource
    private IpmsSupplierLinkmanService ipmsSupplierLinkmanService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Override
    public String supplierCodeAutoGenerate() {
        QueryWrapper<IpmsSupplier> supplierQueryWrapper = new QueryWrapper<>();
        List<IpmsSupplier> supplierList = ipmsSupplierMapper.selectList(supplierQueryWrapper);
        String supplierCode;
        if (supplierList.size() == 0) {
            supplierCode = "GYS00000";
        } else {
            IpmsSupplier lastSupplier = supplierList.get(supplierList.size() - 1);
            supplierCode = lastSupplier.getSupplierCode();
        }
        String nextSupplierCode = null;
        try {
            nextSupplierCode = CodeAutoGenerator.generatorCode(supplierCode);
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextSupplierCode;
    }

    @Override
    @Transactional // 事务注解
    public int addSupplier(AddSupplierRequest addSupplierRequest) {
        // 1. 校验参数是否为空
        String supplierName = addSupplierRequest.getSupplierName();
        String supplierCode = addSupplierRequest.getSupplierCode();
        if (StringUtils.isAnyBlank(supplierName, supplierCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "供应商姓名或者供应商编号为空");
        }
        // 2. 至少要插入一条联系人信息（调用插入联系人方法）
        List<AddSupplierLinkmanRequest> addSupplierLinkmanRequestList = addSupplierRequest.getAddSupplierLinkmanRequestList();
        if (addSupplierLinkmanRequestList == null || addSupplierLinkmanRequestList.size() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "联系人不存在或者联系人为空");
        }
        // 3. 编号不能重复
        QueryWrapper<IpmsSupplier> supplierQueryWrapper = new QueryWrapper<>();
        supplierQueryWrapper.eq("supplier_code", supplierCode);
        IpmsSupplier ipmsSupplier = ipmsSupplierMapper.selectOne(supplierQueryWrapper);
        if (ipmsSupplier != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编码重复");
        }
        // 4. 插入数据到供应商表
        IpmsSupplier supplier = new IpmsSupplier();
        BeanUtils.copyProperties(addSupplierRequest, supplier);
        supplier.setCreateTime(new Date());
        supplier.setUpdateTime(new Date());
        int supplierAddResult = ipmsSupplierMapper.insert(supplier);
        if (supplierAddResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 5. 调用插入联系人方法将列表中的信息插入供应商联系人表
        int supplierLinkmanAddResult = 0;
        for (AddSupplierLinkmanRequest addSupplierLinkmanRequest : addSupplierLinkmanRequestList) {
            supplierLinkmanAddResult = ipmsSupplierLinkmanService.addSupplierLinkman(addSupplierLinkmanRequest, supplier.getSupplierId());
        }
        return supplierLinkmanAddResult;
    }

    @Override
    @Transactional // 开启事务
    public int deleteSupplierById(long id) {
        //  1. 校验 id
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不存在或者 id 不合法");
        }
        // todo 如果供应商有订单无法删除供应商，订单关联了供应商的信息，存储了供应商的 id，所以供应商不能删除，否则查询订单会报错
        // 2. 删除供应商信息
        int customerDeleteResult = ipmsSupplierMapper.deleteById(id);
        if (customerDeleteResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 3. 删除客户联系人信息
        return ipmsSupplierLinkmanService.deleteSupplierLinkmanById(id);
    }

    @Override
    @Transactional // 开启事务
    public int updateSupplier(UpdateSupplierRequest updateSupplierRequest) {
        // 1. 校验参数
        Long supplierId = updateSupplierRequest.getSupplierId();
        if (supplierId == null || supplierId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不存在或者 id 不合法");
        }
        List<UpdateSupplierLinkmanRequest> updateSupplierLinkmanRequestList = updateSupplierRequest.getUpdateSupplierLinkmanRequestList();
        if (updateSupplierLinkmanRequestList == null || updateSupplierLinkmanRequestList.size() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "联系人信息不存在或者为空");
        }
        // 2. 判断供应商是否存在
        IpmsSupplier supplier = ipmsSupplierMapper.selectById(supplierId);
        if (supplier == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 3. 如果供应商编码有传递那么供应商编号必须相同，无法修改，且验证了供应商是否存在
        String supplierCode = updateSupplierRequest.getSupplierCode();
        if (supplierCode != null) {
            QueryWrapper<IpmsSupplier> supplierQueryWrapper = new QueryWrapper<>();
            supplierQueryWrapper.eq("supplier_id", supplierId);
            IpmsSupplier oldSupplier = ipmsSupplierMapper.selectOne(supplierQueryWrapper);
            if (!supplierCode.equals(oldSupplier.getSupplierCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "供应商编号必须相同无法修改");
            }
        }
        // 4. 更新数据
        //     更新供应商信息
        IpmsSupplier newSupplier = new IpmsSupplier();
        BeanUtils.copyProperties(updateSupplierRequest, newSupplier);
        newSupplier.setUpdateTime(new Date());
        int updateSupplierResult = ipmsSupplierMapper.updateById(newSupplier);
        if (updateSupplierResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //     更新供应商联系人信息
        int result = 0;
        for (UpdateSupplierLinkmanRequest updateSupplierLinkmanRequest : updateSupplierLinkmanRequestList) {
            result = ipmsSupplierLinkmanService.updateSupplierLinkman(updateSupplierLinkmanRequest, supplierId);
        }
        return result;
    }

    @Override
    public Page<SafeSupplierVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Page<IpmsSupplier> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsSupplier> ipmsSupplierQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (StringUtils.isNotBlank(fuzzyText)) {
            ipmsSupplierQueryWrapper.like("supplier_id", fuzzyText).or()
                    .like("supplier_code", fuzzyText).or()
                    .like("supplier_name", fuzzyText).or()
                    .like("supplier_type", fuzzyText).or()
                    .like("enterprise_pay_balance", fuzzyText).or();
        }
        Page<IpmsSupplier> supplierPage = ipmsSupplierMapper.selectPage(page, ipmsSupplierQueryWrapper);
        List<SafeSupplierVO> safeSupplierVOList = supplierPage.getRecords().stream().map(ipmsSupplier -> {
            SafeSupplierVO safeSupplierVO = new SafeSupplierVO();
            BeanUtils.copyProperties(ipmsSupplier, safeSupplierVO);
            Long supplierId = ipmsSupplier.getSupplierId();
            if (supplierId != null && supplierId > 0) {
                QueryWrapper<IpmsSupplierLinkman> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("supplier_id", supplierId);
                List<IpmsSupplierLinkman> supplierLinkmanList = ipmsSupplierLinkmanService.list(queryWrapper);
                List<SafeSupplierLinkmanVO> safeSupplierLinkmanVOList = new ArrayList<>();
                for (IpmsSupplierLinkman supplierLinkman : supplierLinkmanList) {
                    SafeSupplierLinkmanVO safeSupplierLinkmanVO = new SafeSupplierLinkmanVO();
                    BeanUtils.copyProperties(supplierLinkman, safeSupplierLinkmanVO);
                    safeSupplierLinkmanVOList.add(safeSupplierLinkmanVO);
                }
                safeSupplierVO.setSafeSupplierLinkmanVOList(safeSupplierLinkmanVOList);
            }
            return safeSupplierVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeSupplierVO> safeSupplierVOPage = new PageDTO<>(supplierPage.getCurrent(), supplierPage.getSize(), supplierPage.getTotal());
        safeSupplierVOPage.setRecords(safeSupplierVOList);
        return safeSupplierVOPage;
    }
}




