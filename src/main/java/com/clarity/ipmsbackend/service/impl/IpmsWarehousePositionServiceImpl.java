package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.constant.WarehouseConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsWarehousePositionMapper;
import com.clarity.ipmsbackend.model.dto.warehouse.position.AddWarehousePositionRequest;
import com.clarity.ipmsbackend.model.dto.warehouse.position.UpdateWarehousePositionRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.model.vo.SafeWarehousePositionVO;
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
* @description 针对表【ipms_warehouse_position(仓位表)】的数据库操作Service实现
* @createDate 2023-02-28 11:07:12
*/
@Service
@Slf4j
public class IpmsWarehousePositionServiceImpl extends ServiceImpl<IpmsWarehousePositionMapper, IpmsWarehousePosition>
    implements IpmsWarehousePositionService{

    @Resource
    private IpmsWarehousePositionMapper ipmsWarehousePositionMapper;

    @Resource
    private IpmsWarehouseService ipmsWarehouseService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Resource
    private IpmsProductionBillProductNumService ipmsProductionBillProductNumService;

    @Resource
    private IpmsSaleBillProductNumService ipmsSaleBillProductNumService;

    @Resource
    private IpmsPurchaseBillProductNumService ipmsPurchaseBillProductNumService;

    @Resource
    private IpmsInventoryBillProductNumService ipmsInventoryBillProductNumService;

    @Resource
    private IpmsProductionBillService ipmsProductionBillService;

    @Override
    public String warehousePositionCodeAutoGenerate() {
        QueryWrapper<IpmsWarehousePosition> ipmsWarehousePositionQueryWrapper = new QueryWrapper<>();
        List<IpmsWarehousePosition> ipmsWarehousePositionList = ipmsWarehousePositionMapper.selectList(ipmsWarehousePositionQueryWrapper);
        String warehousePositionCode;
        if (ipmsWarehousePositionList.size() == 0) {
            warehousePositionCode = "CW00000";
        } else {
            IpmsWarehousePosition lastWarehousePosition = ipmsWarehousePositionList.get(ipmsWarehousePositionList.size() - 1);
            warehousePositionCode = lastWarehousePosition.getWarehousePositionCode();
        }
        String nextWarehousePositionCode = null;
        try {
            nextWarehousePositionCode = CodeAutoGenerator.generatorCode(warehousePositionCode);
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextWarehousePositionCode;
    }

    @Override
    public int addWarehousePosition(AddWarehousePositionRequest addWarehousePositionRequest) {
        // 1. 校验参数是否为空
        String warehousePositionCode = addWarehousePositionRequest.getWarehousePositionCode();
        String warehousePositionName = addWarehousePositionRequest.getWarehousePositionName();
        if (StringUtils.isAnyBlank(warehousePositionCode, warehousePositionName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位编码或者名称为空");
        }
        Long warehouseId = addWarehousePositionRequest.getWarehouseId();
        if (warehouseId == null || warehouseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库 id 为空或者不合法");
        }
        // 2. 编号是否重复
        QueryWrapper<IpmsWarehousePosition> warehousePositionQueryWrapper = new QueryWrapper<>();
        warehousePositionQueryWrapper.eq("warehouse_position_code", warehousePositionCode);
        IpmsWarehousePosition ipmsWarehousePosition = ipmsWarehousePositionMapper.selectOne(warehousePositionQueryWrapper);
        if (ipmsWarehousePosition != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位编号重复");
        }
        // 3. 判断仓库是否存在
        IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
        if (warehouse == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "数据库没有该仓库");
        }
        // 4. 校验所选择的仓库是否开启仓位管理
        if (WarehouseConstant.OPEN_WAREHOUSE_POSITION_MANAGEMENT != warehouse.getIsWarehousePositionManagement()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该仓库没用开启仓位管理");
        }
        // 5. 最后查入数据
        IpmsWarehousePosition warehousePosition = new IpmsWarehousePosition();
        BeanUtils.copyProperties(addWarehousePositionRequest, warehousePosition);
        warehousePosition.setCreateTime(new Date());
        warehousePosition.setUpdateTime(new Date());
        int result = ipmsWarehousePositionMapper.insert(warehousePosition);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int deleteWarehousePositionById(long id) {
        // 1. 校验 id 合法性
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位 id 不合法");
        }
        // 2. 判断仓位是否被相关订单使用
        QueryWrapper<IpmsInventoryBillProductNum> ipmsInventoryBillProductNumQueryWrapper = new QueryWrapper<>();
        ipmsInventoryBillProductNumQueryWrapper.eq("warehouse_position_id", id);
        List<IpmsInventoryBillProductNum> validAsInventoryBillProductNum = ipmsInventoryBillProductNumService.list(ipmsInventoryBillProductNumQueryWrapper);
        if (validAsInventoryBillProductNum != null && validAsInventoryBillProductNum.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "仓位已被使用");
        }
        QueryWrapper<IpmsSaleBillProductNum> ipmsSaleBillProductNumQueryWrapper = new QueryWrapper<>();
        ipmsSaleBillProductNumQueryWrapper.eq("warehouse_position_id", id);
        List<IpmsSaleBillProductNum> validAsSaleBillProductNum = ipmsSaleBillProductNumService.list(ipmsSaleBillProductNumQueryWrapper);
        if (validAsSaleBillProductNum != null && validAsSaleBillProductNum.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "仓位已被使用");
        }
        QueryWrapper<IpmsProductionBillProductNum> ipmsProductionBillProductNumQueryWrapper = new QueryWrapper<>();
        ipmsProductionBillProductNumQueryWrapper.eq("warehouse_position_id", id);
        List<IpmsProductionBillProductNum> validAsProductionBillProductNum = ipmsProductionBillProductNumService.list(ipmsProductionBillProductNumQueryWrapper);
        if (validAsProductionBillProductNum != null && validAsProductionBillProductNum.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "仓位已被使用");
        }
        QueryWrapper<IpmsPurchaseBillProductNum> ipmsPurchaseBillProductNumQueryWrapper = new QueryWrapper<>();
        ipmsPurchaseBillProductNumQueryWrapper.eq("warehouse_position_id", id);
        List<IpmsPurchaseBillProductNum> validAsPurchaseBillProductNum = ipmsPurchaseBillProductNumService.list(ipmsPurchaseBillProductNumQueryWrapper);
        if (validAsPurchaseBillProductNum != null && validAsPurchaseBillProductNum.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "仓位已被使用");
        }
        QueryWrapper<IpmsProductionBill> ipmsProductionBillQueryWrapper = new QueryWrapper<>();
        ipmsProductionBillQueryWrapper.eq("warehouse_position_id", id);
        List<IpmsProductionBill> validAsProductionBill = ipmsProductionBillService.list(ipmsProductionBillQueryWrapper);
        if (validAsProductionBill != null && validAsProductionBill.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "仓位已被使用");
        }
        // todo 可能 BOM 商品也要判断有没有仓位
        // 3. 删除仓位
        int result = ipmsWarehousePositionMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int updateWarehousePosition(UpdateWarehousePositionRequest updateWarehousePositionRequest) {
        // 1. 校验参数
        Long warehousePositionId = updateWarehousePositionRequest.getWarehousePositionId();
        if (warehousePositionId == null || warehousePositionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位 id 为空或者不合法");
        }
        // 2. 判断该仓位是否存在
        IpmsWarehousePosition oldWarehousePosition = ipmsWarehousePositionMapper.selectById(warehousePositionId);
        if (oldWarehousePosition == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 3. 如果编号不为空，编号必须相同（相当于不能修改编号）
        String warehousePositionCode = updateWarehousePositionRequest.getWarehousePositionCode();
        if (warehousePositionCode != null) {
            QueryWrapper<IpmsWarehousePosition> warehousePositionQueryWrapper = new QueryWrapper<>();
            warehousePositionQueryWrapper.eq("warehouse_position_id", warehousePositionId);
            IpmsWarehousePosition sourceWarehousePosition = ipmsWarehousePositionMapper.selectOne(warehousePositionQueryWrapper);
            if (!warehousePositionCode.equals(sourceWarehousePosition.getWarehousePositionCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库编码不相同");
            }
        }
        // 4. 如果仓库 id 不为空，判断仓库是否存在
        Long warehouseId = updateWarehousePositionRequest.getWarehouseId();
        if (warehouseId != null) {
            if (warehouseId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位 id 不合法");
            }
            IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
            if (warehouse == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库不存在");
            }
        }
        // 5. 最后更新仓位信息
        IpmsWarehousePosition newWarehousePosition = new IpmsWarehousePosition();
        BeanUtils.copyProperties(updateWarehousePositionRequest, newWarehousePosition);
        newWarehousePosition.setUpdateTime(new Date());
        int result = ipmsWarehousePositionMapper.updateById(newWarehousePosition);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public Page<SafeWarehousePositionVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Page<IpmsWarehousePosition> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsWarehousePosition> ipmsWarehousePositionQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (StringUtils.isNotBlank(fuzzyText)) {
            ipmsWarehousePositionQueryWrapper.like("warehouse_position_code", fuzzyText).or()
                    .like("warehouse_position_name", fuzzyText).or();
        }
        Page<IpmsWarehousePosition> warehousePositionPage = ipmsWarehousePositionMapper.selectPage(page, ipmsWarehousePositionQueryWrapper);
        List<SafeWarehousePositionVO> safeWarehousePositionVOList = warehousePositionPage.getRecords().stream().map(ipmsWarehousePosition -> {
            SafeWarehousePositionVO safeWarehousePositionVO = new SafeWarehousePositionVO();
            BeanUtils.copyProperties(ipmsWarehousePosition, safeWarehousePositionVO);
            Long warehouseId = ipmsWarehousePosition.getWarehouseId();
            if (warehouseId != null && warehouseId > 0) {
                IpmsWarehouse warehouse = ipmsWarehouseService.getById(warehouseId);
                if (warehouse != null) {
                    safeWarehousePositionVO.setWarehouseName(warehouse.getWarehouseName());
                }
            }
            return safeWarehousePositionVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeWarehousePositionVO> safeWarehousePositionVOPage = new PageDTO<>(warehousePositionPage.getCurrent(), warehousePositionPage.getSize(), warehousePositionPage.getTotal());
        safeWarehousePositionVOPage.setRecords(safeWarehousePositionVOList);
        return safeWarehousePositionVOPage;
    }
}




