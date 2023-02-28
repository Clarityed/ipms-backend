package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.constant.WarehouseConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsWarehouseMapper;
import com.clarity.ipmsbackend.model.dto.warehouse.AddWarehouseRequest;
import com.clarity.ipmsbackend.model.dto.warehouse.UpdateWarehouseRequest;
import com.clarity.ipmsbackend.model.entity.IpmsWarehouse;
import com.clarity.ipmsbackend.model.entity.IpmsEmployee;
import com.clarity.ipmsbackend.model.entity.IpmsEnterprise;
import com.clarity.ipmsbackend.model.entity.IpmsWarehouse;
import com.clarity.ipmsbackend.model.vo.SafeWarehouseVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.model.vo.SafeWarehouseVO;
import com.clarity.ipmsbackend.service.IpmsEmployeeService;
import com.clarity.ipmsbackend.service.IpmsUserService;
import com.clarity.ipmsbackend.service.IpmsWarehouseService;
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
 * @description 针对表【ipms_warehouse(仓库表)】的数据库操作Service实现
 * @createDate 2023-02-27 19:51:46
 */
@Service
@Slf4j
public class IpmsWarehouseServiceImpl extends ServiceImpl<IpmsWarehouseMapper, IpmsWarehouse>
        implements IpmsWarehouseService {

    @Resource
    private IpmsWarehouseMapper ipmsWarehouseMapper;

    @Resource
    private IpmsEmployeeService ipmsEmployeeService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Override
    public String warehouseCodeAutoGenerate() {
        QueryWrapper<IpmsWarehouse> ipmsWarehouseQueryWrapper = new QueryWrapper<>();
        List<IpmsWarehouse> ipmsWarehouseList = ipmsWarehouseMapper.selectList(ipmsWarehouseQueryWrapper);
        String warehouseCode;
        if (ipmsWarehouseList.size() == 0) {
            warehouseCode = "CK00000";
        } else {
            IpmsWarehouse lastWarehouse = ipmsWarehouseList.get(ipmsWarehouseList.size() - 1);
            warehouseCode = lastWarehouse.getWarehouseCode();
        }
        String nextWarehouseCode = null;
        try {
            nextWarehouseCode = CodeAutoGenerator.literallyCode(warehouseCode);
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextWarehouseCode;
    }

    @Override
    public int addWarehouse(AddWarehouseRequest addWarehouseRequest) {
        // 1. 校验参数
        String warehouseCode = addWarehouseRequest.getWarehouseCode();
        String warehouseName = addWarehouseRequest.getWarehouseName();
        if (StringUtils.isAnyBlank(warehouseCode, warehouseName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库编号或者名称为空");
        }
        // 2. 编码不能重复
        QueryWrapper<IpmsWarehouse> warehouseQueryWrapper = new QueryWrapper<>();
        warehouseQueryWrapper.eq("warehouse_code", warehouseCode);
        IpmsWarehouse ipmsWarehouse = ipmsWarehouseMapper.selectOne(warehouseQueryWrapper);
        if (ipmsWarehouse != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库编号重复");
        }
        // 3. 如果仓管 id 不为空，判断是否存在该职员
        Long warehouseKeeperId = addWarehouseRequest.getWarehouseKeeperId();
        if (warehouseKeeperId != null) {
            if (warehouseKeeperId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓管 id 不合法");
            }
            IpmsEmployee warehouseKeeper = ipmsEmployeeService.getById(warehouseKeeperId);
            if (warehouseKeeper == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该仓管不存在");
            }
        }
        // 4. 最后插入数据
        IpmsWarehouse warehouse = new IpmsWarehouse();
        BeanUtils.copyProperties(addWarehouseRequest, warehouse);
        warehouse.setCreateTime(new Date());
        warehouse.setUpdateTime(new Date());
        int result = ipmsWarehouseMapper.insert(warehouse);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int deleteWarehouseById(long id) {
        // 1. 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        // 2. 判断该参考是否开启了仓位功能
        IpmsWarehouse warehouse = ipmsWarehouseMapper.selectById(id);
        if (WarehouseConstant.OPEN_WAREHOUSE_POSITION_MANAGEMENT == warehouse.getIsWarehousePositionManagement()) {
            //   - todo 如果开启了则查询有没有仓位
            //   - 如果开启了但是没用仓位那样也有机会删除，但是要看下面的校验
        }
        // 3. todo 有没有作为哪些单据的仓库，如果有无法删除
        // 4. 删除仓库
        int result = ipmsWarehouseMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int updateWarehouse(UpdateWarehouseRequest updateWarehouseRequest) {
        // 1. 校验参数
        Long warehouseId = updateWarehouseRequest.getWarehouseId();
        if (warehouseId == null || warehouseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库 id 不存在或者为空");
        }
        // 2. 判断更新仓库是否存在
        IpmsWarehouse oldWarehouse = ipmsWarehouseMapper.selectById(warehouseId);
        if (oldWarehouse == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 3. 编码必须相同
        String warehouseCode = updateWarehouseRequest.getWarehouseCode();
        if (warehouseCode != null) {
            IpmsWarehouse warehouse = ipmsWarehouseMapper.selectById(warehouseId);
            if (!warehouseCode.equals(warehouse.getWarehouseCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库编码不一致");
            }
        }
        // 4. 员工 id 必须存在
        Long warehouseKeeperId = updateWarehouseRequest.getWarehouseKeeperId();
        if (warehouseKeeperId != null) {
            if (warehouseKeeperId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓管 id 不合法");
            }
            IpmsEmployee warehouseKeeper = ipmsEmployeeService.getById(warehouseKeeperId);
            if (warehouseKeeper == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该仓管不存在");
            }
        }
        // 5. 最后更新员工信息
        IpmsWarehouse newWarehouse = new IpmsWarehouse();
        BeanUtils.copyProperties(updateWarehouseRequest, newWarehouse);
        newWarehouse.setUpdateTime(new Date());
        int result = ipmsWarehouseMapper.updateById(newWarehouse);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public Page<SafeWarehouseVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Page<IpmsWarehouse> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsWarehouse> warehouseQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (StringUtils.isNotBlank(fuzzyText)) {
            warehouseQueryWrapper.like("warehouse_code", fuzzyText).or()
                    .like("warehouse_name", fuzzyText).or();
        }
        Page<IpmsWarehouse> warehousePage = ipmsWarehouseMapper.selectPage(page, warehouseQueryWrapper);
        List<SafeWarehouseVO> safeWarehouseVOList = warehousePage.getRecords().stream().map(ipmsWarehouse -> {
            SafeWarehouseVO safeWarehouseVO = new SafeWarehouseVO();
            BeanUtils.copyProperties(ipmsWarehouse, safeWarehouseVO);
            Long warehouseKeeperId = ipmsWarehouse.getWarehouseKeeperId();
            if (warehouseKeeperId != null && warehouseKeeperId > 0) {
                IpmsEmployee warehouseKeeper = ipmsEmployeeService.getById(warehouseKeeperId);
                safeWarehouseVO.setWarehouseKeeperName(warehouseKeeper.getEmployeeName());
                safeWarehouseVO.setWarehouseKeeperPhone(warehouseKeeper.getEmployeePhoneNum());
            }
            return safeWarehouseVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeWarehouseVO> safeWarehouseVOPage = new PageDTO<>(warehousePage.getCurrent(), warehousePage.getSize(), warehousePage.getTotal());
        safeWarehouseVOPage.setRecords(safeWarehouseVOList);
        return safeWarehouseVOPage;
    }
}




