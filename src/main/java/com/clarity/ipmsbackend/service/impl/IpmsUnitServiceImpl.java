package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsUnitMapper;
import com.clarity.ipmsbackend.model.dto.unit.AddUnitRequest;
import com.clarity.ipmsbackend.model.dto.unit.UpdateUnitRequest;
import com.clarity.ipmsbackend.model.entity.IpmsInventoryBill;
import com.clarity.ipmsbackend.model.entity.IpmsProduct;
import com.clarity.ipmsbackend.model.entity.IpmsUnit;
import com.clarity.ipmsbackend.model.vo.SafeUnitVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.IpmsProductService;
import com.clarity.ipmsbackend.service.IpmsUnitService;
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
 * @description 针对表【ipms_unit(计量单位表)】的数据库操作Service实现
 * @createDate 2023-02-28 16:01:24
 */
@Service
@Slf4j
public class IpmsUnitServiceImpl extends ServiceImpl<IpmsUnitMapper, IpmsUnit>
        implements IpmsUnitService {

    @Resource
    private IpmsUnitMapper ipmsUnitMapper;

    @Resource
    private IpmsUserService ipmsUserService;

    @Resource
    private IpmsProductService ipmsProductService;

    @Override
    public String unitCodeAutoGenerate() {
        QueryWrapper<IpmsUnit> ipmsUnitQueryWrapper = new QueryWrapper<>();
        List<IpmsUnit> ipmsUnitList = ipmsUnitMapper.selectList(ipmsUnitQueryWrapper);
        String unitCode;
        if (ipmsUnitList.size() == 0) {
            unitCode = "DW00000";
        } else {
            IpmsUnit lastUnit = ipmsUnitList.get(ipmsUnitList.size() - 1);
            unitCode = lastUnit.getUnitCode();
        }
        String nextUnitCode = null;
        try {
            nextUnitCode = CodeAutoGenerator.generatorCode(unitCode);
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextUnitCode;
    }

    @Override
    public int addUnit(AddUnitRequest addUnitRequest) {
        String unitName = addUnitRequest.getUnitName();
        String unitCode = addUnitRequest.getUnitCode();
        if (StringUtils.isAnyBlank(unitCode, unitName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计量单位编码为空或者名称为空");
        }
        QueryWrapper<IpmsUnit> unitQueryWrapper = new QueryWrapper<>();
        unitQueryWrapper.eq("unit_code", unitCode);
        IpmsUnit ipmsUnitWithValidCode = ipmsUnitMapper.selectOne(unitQueryWrapper);
        if (ipmsUnitWithValidCode != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编码重复");
        }
        // 单位名称不能重复，单位是唯一的
        unitQueryWrapper = new QueryWrapper<>();
        unitQueryWrapper.eq("unit_name", unitName);
        IpmsUnit ipmsUnitWithValidName = ipmsUnitMapper.selectOne(unitQueryWrapper);
        if (ipmsUnitWithValidName != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单位重复");
        }
        IpmsUnit unit = new IpmsUnit();
        BeanUtils.copyProperties(addUnitRequest, unit);
        unit.setCreateTime(new Date());
        unit.setUpdateTime(new Date());
        int result = ipmsUnitMapper.insert(unit);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int deleteUnitById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计量单位 id 不合法");
        }
        // 计量单位会被相关单据引用无法直接删除，必须删除引用它的单据然后才能删除，也有可能被商品引用
        QueryWrapper<IpmsProduct> ipmsProductQueryWrapper = new QueryWrapper<>();
        ipmsProductQueryWrapper.eq("customer_id", id);
        List<IpmsProduct> validAsProduct = ipmsProductService.list(ipmsProductQueryWrapper);
        if (validAsProduct != null && validAsProduct.size() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "计量单位已被使用");
        }
        int result = ipmsUnitMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int updateUnit(UpdateUnitRequest updateUnitRequest) {
        Long unitId = updateUnitRequest.getUnitId();
        if (unitId == null || unitId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计量单位为空或者 id 不合法");
        }
        IpmsUnit oldUnit = ipmsUnitMapper.selectById(unitId);
        if (oldUnit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String unitCode = updateUnitRequest.getUnitCode();
        if (unitCode != null) {
            if (!unitCode.equals(oldUnit.getUnitCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号不相同");
            }
        }
        String unitName = updateUnitRequest.getUnitName();
        if (unitName != null) {
            if (!unitName.equals(oldUnit.getUnitName())) {
                QueryWrapper<IpmsUnit> unitQueryWrapper = new QueryWrapper<>();
                unitQueryWrapper.eq("unit_name", unitName);
                IpmsUnit ipmsUnit = ipmsUnitMapper.selectOne(unitQueryWrapper);
                if (ipmsUnit != null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "计量单位名称重复");
                }
            }
        }
        IpmsUnit newUnit = new IpmsUnit();
        BeanUtils.copyProperties(updateUnitRequest, newUnit);
        newUnit.setUpdateTime(new Date());
        int result = ipmsUnitMapper.updateById(newUnit);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public Page<SafeUnitVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Page<IpmsUnit> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsUnit> unitQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (StringUtils.isNotBlank(fuzzyText)) {
            unitQueryWrapper.like("unit_code", fuzzyText).or()
                    .like("unit_name", fuzzyText).or();
        }
        Page<IpmsUnit> unitPage = ipmsUnitMapper.selectPage(page, unitQueryWrapper);
        List<SafeUnitVO> safeUnitVOList = unitPage.getRecords().stream().map(ipmsUnit -> {
            SafeUnitVO safeUnitVO = new SafeUnitVO();
            BeanUtils.copyProperties(ipmsUnit, safeUnitVO);
            return safeUnitVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeUnitVO> safeUnitVOPage = new PageDTO<>(unitPage.getCurrent(), unitPage.getSize(), unitPage.getTotal());
        safeUnitVOPage.setRecords(safeUnitVOList);
        return safeUnitVOPage;
    }
}




