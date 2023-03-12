package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.constant.BomConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsBomMapper;
import com.clarity.ipmsbackend.model.dto.bom.AddBomRequest;
import com.clarity.ipmsbackend.model.dto.bom.UpdateBomRequest;
import com.clarity.ipmsbackend.model.dto.productbom.AddProductBomRequest;
import com.clarity.ipmsbackend.model.dto.productbom.UpdateProductBomRequest;
import com.clarity.ipmsbackend.model.entity.IpmsBom;
import com.clarity.ipmsbackend.model.entity.IpmsProduct;
import com.clarity.ipmsbackend.model.entity.IpmsProductBom;
import com.clarity.ipmsbackend.model.entity.IpmsUnit;
import com.clarity.ipmsbackend.model.vo.bom.SafeBomVO;
import com.clarity.ipmsbackend.model.vo.bom.SafeForwardQueryBomVO;
import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.model.vo.bom.SafeReverseQueryBomVO;
import com.clarity.ipmsbackend.service.*;
import com.clarity.ipmsbackend.utils.CodeAutoGenerator;
import com.clarity.ipmsbackend.utils.TimeFormatUtil;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Clarity
 * @description 针对表【ipms_bom】的数据库操作Service实现
 * @createDate 2023-03-05 11:54:06
 */

@Service
@Slf4j
public class IpmsBomServiceImpl extends ServiceImpl<IpmsBomMapper, IpmsBom>
        implements IpmsBomService {

    @Resource
    private IpmsBomMapper ipmsBomMapper;

    @Resource
    private IpmsProductBomService ipmsProductBomService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Resource
    private IpmsProductService ipmsProductService;

    @Resource
    private IpmsUnitService ipmsUnitService;

    @Override
    public String bomCodeAutoGenerate() {
        QueryWrapper<IpmsBom> ipmsBomQueryWrapper = new QueryWrapper<>();
        List<IpmsBom> ipmsBomList = ipmsBomMapper.selectList(ipmsBomQueryWrapper);
        String bomCode;
        if (ipmsBomList.size() == 0) {
            bomCode = "BOM00000";
        } else {
            IpmsBom lastBom = ipmsBomList.get(ipmsBomList.size() - 1);
            bomCode = lastBom.getBomCode();
        }
        String nextBomCode = null;
        try {
            nextBomCode = CodeAutoGenerator.generatorCode(bomCode);
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextBomCode;
    }

    @Override
    @Transactional
    public int addBom(AddBomRequest addBomRequest, HttpServletRequest request) {
        // 1. 参数校验
        String bomCode = addBomRequest.getBomCode();
        if (bomCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "BOM 编号为空");
        }
        List<AddProductBomRequest> addProductBomRequestList = addBomRequest.getAddProductBomRequestList();
        if (addProductBomRequestList == null || addProductBomRequestList.size() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "至少要有一个子件");
        }
        // 2. 编码不能重复
        QueryWrapper<IpmsBom> bomQueryWrapper = new QueryWrapper<>();
        bomQueryWrapper.eq("bom_code", bomCode);
        IpmsBom ipmsBom = ipmsBomMapper.selectOne(bomQueryWrapper);
        if (ipmsBom != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "BOM 编号重复");
        }
        // 3. 插入数据
        IpmsBom bom = new IpmsBom();
        BeanUtils.copyProperties(addBomRequest, bom);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        bom.setFounder(loginUser.getUserName());
        bom.setCreateTime(new Date());
        bom.setUpdateTime(new Date());
        int result = ipmsBomMapper.insert(bom);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 4. 插入关联信息
        int bomLevel = 1; // 默认是 1 级 bom 单
        for (AddProductBomRequest addProductBomRequest : addProductBomRequestList) {
            Map<String, Long> map = ipmsProductBomService.addProductBom(addProductBomRequest, bom.getBomId());
            int level = Math.toIntExact(map.get("bomLevel"));
            if (level == 2) {
                bomLevel = level;
            }
        }
        // 4. BOM 单级别设置
        IpmsBom setLevelBom = new IpmsBom();
        setLevelBom.setBomId(bom.getBomId());
        setLevelBom.setBomLevel(bomLevel);
        int setLevelResult = ipmsBomMapper.updateById(setLevelBom);
        if (setLevelResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 5. 修改父件 BOM 单等级
        for (AddProductBomRequest addProductBomRequest : addProductBomRequestList) {
            QueryWrapper<IpmsProductBom> productBomQueryWrapper = new QueryWrapper<>();
            productBomQueryWrapper.eq("subcomponent_product_id", addProductBomRequest.getSubcomponentProductId());
            List<IpmsProductBom> productBomList = ipmsProductBomService.list();
            if (productBomList != null && productBomList.size() > 0) {
                for (IpmsProductBom productBom : productBomList) {
                    if (productBom.getSubcomponentBomId() != null) {
                        IpmsBom fatherLevelBom = new IpmsBom();
                        fatherLevelBom.setBomId(productBom.getBomId());
                        fatherLevelBom.setBomLevel(2);
                        ipmsBomMapper.updateById(fatherLevelBom);
                    }
                }
            }
        }
        return result;
    }

    @Override
    @Transactional
    public int deleteBomById(long id) {
        // 1. 校验参数
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        // 2. 判断是否可以删除，审核后的 BOM 无法删除。
        IpmsBom bom = ipmsBomMapper.selectById(id);
        if (bom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (BomConstant.CHECKED_STATE == bom.getCheckState()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法删除，单据已审核");
        }
        // todo 3. 判断 BOM 是否被使用，被使用的 BOM 无法删除。
        // 4. 删除 BOM，关联删除商品 BOM 关系信息
        int bomDeleteResult = ipmsBomMapper.deleteById(id);
        if (bomDeleteResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ipmsProductBomService.deleteProductBomById(id);
    }

    @Override
    @Transactional
    public int updateBom(UpdateBomRequest updateBomRequest, HttpServletRequest request) {
        // 1. 参数校验
        Long bomId = updateBomRequest.getBomId();
        if (bomId == null || bomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "BOM id 为空或者不合法");
        }
        List<UpdateProductBomRequest> updateProductBomRequestList = updateBomRequest.getUpdateProductBomRequestList();
        if (updateProductBomRequestList == null || updateProductBomRequestList.size() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "子件信息为空或者至少需要一个子件信息");
        }
        // 2. 判断修改的 bom 是否存在
        IpmsBom oldBom = ipmsBomMapper.selectById(bomId);
        if (oldBom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断 BOM 是否已经审核，已审核单据无法修改
        Integer checkState = oldBom.getCheckState();
        if (BomConstant.CHECKED_STATE == checkState) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "BOM 已审核");
        }
        // 3. 编号无法修改
        String bomCode = updateBomRequest.getBomCode();
        if (bomCode != null) {
            if (!bomCode.equals(oldBom.getBomCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号无法修改");
            }
        }
        // 4. 核心流程：对于子件信息表格（商品 BOM 关系表）的修改
        // 4.1 首先根据 BOM id，查询这个 BOM 单下的所有子件，直接删除不在前端传递过来的商品 BOM 关系 id中的数据
        // 定义等级修改参数变量
        int bomLevel = 1; // 默认是 1 级 bom 单
        List<Long> updateAndInsertProductBomIdList = new ArrayList<>(); // 前端传递要修改的数据，包括新增的数据
        for (UpdateProductBomRequest updateProductBomRequest : updateProductBomRequestList) {
            Long productBomId = updateProductBomRequest.getProductBomId();
            if (productBomId != null && productBomId > 0) {
                if (ipmsProductBomService.getById(productBomId) != null) {
                    updateAndInsertProductBomIdList.add(productBomId);
                    // 4.2 根据前端传递的商品 BOM 关系 id，查询商品 BOM 关系表的数据，如果存在，那么修改这些数据
                    // 这一次修改返回的等级
                    int currentLevel = ipmsProductBomService.updateProductBom(updateProductBomRequest);
                    if (currentLevel == 2) {
                        bomLevel = currentLevel;
                    }
                } else {
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
                }
            } else {
                // 4.3 如果不存在（前端增加数据 id 设置成 0，或者空），那么增加这些数据。
                AddProductBomRequest addProductBomRequest = new AddProductBomRequest();
                BeanUtils.copyProperties(updateProductBomRequest, addProductBomRequest);
                Map<String, Long> map = ipmsProductBomService.addProductBom(addProductBomRequest, bomId);
                updateAndInsertProductBomIdList.add(map.get("productBomId"));
                // 这一次增加返回的等级
                int currentLevel = Math.toIntExact(map.get("bomLevel"));
                if (currentLevel == 2) {
                    bomLevel = currentLevel;
                }
            }
        }
        if (updateAndInsertProductBomIdList.size() > 0) {
            QueryWrapper<IpmsProductBom> productBomQueryWrapper = new QueryWrapper<>();
            productBomQueryWrapper.notIn("product_bom_id", updateAndInsertProductBomIdList);
            productBomQueryWrapper.eq("bom_id", bomId);
            ipmsProductBomService.remove(productBomQueryWrapper);
        }
        IpmsBom newBom = new IpmsBom();
        BeanUtils.copyProperties(updateBomRequest, newBom);
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        newBom.setModifier(loginUser.getUserName());
        newBom.setUpdateTime(new Date());
        newBom.setBomLevel(bomLevel);
        int result = ipmsBomMapper.updateById(newBom);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public Page<SafeBomVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Page<IpmsBom> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsBom> bomQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (StringUtils.isNotBlank(fuzzyText)) {
            bomQueryWrapper.like("bom_code", fuzzyText).or()
                    .like("bom_level", fuzzyText).or();
        }
        Page<IpmsBom> bomPage = ipmsBomMapper.selectPage(page, bomQueryWrapper);
        List<SafeBomVO> safeBomVOList = bomPage.getRecords().stream().map(ipmsBom -> {
            SafeBomVO safeBomVO = new SafeBomVO();
            BeanUtils.copyProperties(ipmsBom, safeBomVO);
            Long bomId = ipmsBom.getBomId();
            if (bomId != null && bomId > 0) {
                QueryWrapper<IpmsProductBom> productBomQueryWrapper = new QueryWrapper<>();
                productBomQueryWrapper.eq("bom_id", bomId);
                List<IpmsProductBom> productBomList = ipmsProductBomService.list(productBomQueryWrapper);
                // 只要存在那么肯定至少有一条数据
                if (productBomList != null && productBomList.size() > 0) {
                    IpmsProductBom productBom = productBomList.get(0);
                    Long productId = productBom.getProductId();
                    IpmsProduct fatherProduct = ipmsProductService.getById(productId);
                    Long unitId = fatherProduct.getUnitId();
                    SafeProductVO safeProductVO = new SafeProductVO();
                    BeanUtils.copyProperties(fatherProduct, safeProductVO);
                    if (unitId != null && unitId > 0) {
                        IpmsUnit unit = ipmsUnitService.getById(unitId);
                        safeProductVO.setUnitName(unit.getUnitName());
                    }
                    safeBomVO.setSafeProductVO(safeProductVO);
                    List<SafeProductVO> safeProductVOList = new ArrayList<>();
                    for (IpmsProductBom ipmsProductBom : productBomList) {
                        safeProductVO = new SafeProductVO();
                        Long subcomponentProductId = ipmsProductBom.getSubcomponentProductId();
                        IpmsProduct subComponentProduct = ipmsProductService.getById(subcomponentProductId);
                        Long subComponentProductUnitId = subComponentProduct.getUnitId();
                        if (subComponentProductUnitId != null && subComponentProductUnitId > 0) {
                            IpmsUnit subComponentProductUnit = ipmsUnitService.getById(subComponentProductUnitId);
                            safeProductVO.setUnitName(subComponentProductUnit.getUnitName());
                        }
                        BeanUtils.copyProperties(subComponentProduct, safeProductVO);
                        safeProductVOList.add(safeProductVO);
                    }
                    safeBomVO.setSafeProductVOList(safeProductVOList);
                }
                safeBomVO.setCheckTime(TimeFormatUtil.dateFormatting(ipmsBom.getCheckTime()));
                safeBomVO.setUpdateTime(TimeFormatUtil.dateFormatting(ipmsBom.getUpdateTime()));
                safeBomVO.setCreateTime(TimeFormatUtil.dateFormatting(ipmsBom.getCreateTime()));
            }
            return safeBomVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeBomVO> safeBomVOPage = new PageDTO<>(bomPage.getCurrent(), bomPage.getSize(), bomPage.getTotal());
        safeBomVOPage.setRecords(safeBomVOList);
        return safeBomVOPage;
    }

    @Override
    public int checkBom(long bomId, HttpServletRequest request) {
        // 1. 参数是否为空，或者是否合法
        if (bomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "BOM id 不合法");
        }
        // 2. 未审核的 BOM id 是否存在
        IpmsBom bom = ipmsBomMapper.selectById(bomId);
        if (bom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 3. 如果 BOM 已经审核提示已经审核
        if (BomConstant.CHECKED_STATE == bom.getCheckState()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经审核过了");
        }
        // 4. 更新 BOM 为已审核
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        bom.setCheckState(BomConstant.CHECKED_STATE);
        bom.setChecker(loginUser.getUserName());
        bom.setCheckTime(new Date());
        int result = ipmsBomMapper.updateById(bom);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int reverseCheckBom(long bomId) {
        // 1. 参数是否为空，或者是否合法
        if (bomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "BOM id 不合法");
        }
        // 2. 已审核的 BOM id 是否存在
        IpmsBom bom = ipmsBomMapper.selectById(bomId);
        if (bom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 3. 如果 BOM 未审核提示还未审核
        if (BomConstant.UNCHECKED_STATE == bom.getCheckState()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "还没有审核呢");
        }
        // 4. 更新 BOM 为未审核
        bom.setCheckState(BomConstant.UNCHECKED_STATE);
        bom.setChecker(null);
        bom.setCheckTime(null);
        int result = ipmsBomMapper.updateById(bom);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public List<SafeForwardQueryBomVO> getBomLevelMessageByBomCode(String bomCode) {
        if (bomCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "BOM 编号为空");
        }
        QueryWrapper<IpmsBom> bomQueryWrapper = new QueryWrapper<>();
        bomQueryWrapper.eq("bom_code", bomCode);
        IpmsBom bom = ipmsBomMapper.selectOne(bomQueryWrapper);
        if (bom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该 BOM 单不存在");
        }
        List<SafeForwardQueryBomVO> bomProductList = ipmsBomMapper.getBomFatherProduct(bomCode);
        // 设置父级商品为 1
        SafeForwardQueryBomVO fatherProductMessage = bomProductList.get(0);
        fatherProductMessage.setLevel(0);
        fatherProductMessage.setSubcomponentMaterialNum(1);
        // 获取子级商品 id、商品用量、商品子级 BOM id
        List<IpmsProductBom> bomSubComponentMessageList = ipmsProductBomService.getBomSubComponentMessage(bomCode);
        for (IpmsProductBom productBom : bomSubComponentMessageList) {
            Long subcomponentBomId = productBom.getSubcomponentBomId();
            Long subcomponentProductId = productBom.getSubcomponentProductId();
            Integer subcomponentMaterialNum = productBom.getSubcomponentMaterialNum();
            if (subcomponentBomId != null && subcomponentBomId > 0) {
                List<SafeForwardQueryBomVO> bomOneLevelProduct = ipmsBomMapper.getBomOneLevelProduct(subcomponentBomId);
                SafeForwardQueryBomVO safeForwardQueryBomVO = bomOneLevelProduct.get(0);
                safeForwardQueryBomVO.setLevel(1);
                safeForwardQueryBomVO.setSubcomponentMaterialNum(subcomponentMaterialNum);
                bomProductList.addAll(bomOneLevelProduct);
                String subComponentBomCode = safeForwardQueryBomVO.getBomCode();
                List<IpmsProductBom> bomTwoLevelSubComponentMessage = ipmsProductBomService.getBomSubComponentMessage(subComponentBomCode);
                for (IpmsProductBom twoLevelProductBom : bomTwoLevelSubComponentMessage) {
                    IpmsProduct twoSubComponentProduct = ipmsProductService.getById(twoLevelProductBom.getSubcomponentProductId());
                    SafeForwardQueryBomVO twoSubComponentBomVO = new SafeForwardQueryBomVO();
                    BeanUtils.copyProperties(twoSubComponentProduct, twoSubComponentBomVO);
                    Long unitId = twoSubComponentProduct.getUnitId();
                    IpmsUnit unit = ipmsUnitService.getById(unitId);
                    twoSubComponentBomVO.setSubcomponentMaterialNum(twoLevelProductBom.getSubcomponentMaterialNum());
                    twoSubComponentBomVO.setLevel(2);
                    twoSubComponentBomVO.setUnitName(unit.getUnitName());
                    bomProductList.add(twoSubComponentBomVO);
                }
            } else {
                // 否则通过 id 去查询商品信息
                IpmsProduct oneLevelProduct = ipmsProductService.getById(subcomponentProductId);
                Long unitId = oneLevelProduct.getUnitId();
                IpmsUnit unit = ipmsUnitService.getById(unitId);
                SafeForwardQueryBomVO safeForwardQueryBomVO = new SafeForwardQueryBomVO();
                BeanUtils.copyProperties(oneLevelProduct, safeForwardQueryBomVO);
                safeForwardQueryBomVO.setLevel(1);
                safeForwardQueryBomVO.setUnitName(unit.getUnitName());
                safeForwardQueryBomVO.setSubcomponentMaterialNum(subcomponentMaterialNum);
                bomProductList.add(safeForwardQueryBomVO);
            }
        }
        return bomProductList;
    }

    @Override
    public List<SafeReverseQueryBomVO> getBomFatherProductOfSubComponentByProductCode(String productCode) {
        if (productCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        QueryWrapper<IpmsProduct> productQueryWrapper = new QueryWrapper<>();
        productQueryWrapper.eq("product_code", productCode);
        IpmsProduct product = ipmsProductService.getOne(productQueryWrapper);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 根据商品编号查询到 BOM 商品关系表，关联该 bomId 和 BOM 表的 id，
        Long productId = product.getProductId();
        QueryWrapper<IpmsProductBom> productBomQueryWrapper = new QueryWrapper<>();
        productBomQueryWrapper.eq("product_id", productId);
        List<IpmsProductBom> productBomList = ipmsProductBomService.list(productBomQueryWrapper);
        // 收集父级商品信息
        List<SafeReverseQueryBomVO> safeReverseQueryBomVOList = new ArrayList<>();
        if (productBomList != null && productBomList.size() > 0) {
            IpmsProductBom productBom = productBomList.get(0);
            Long bomId = productBom.getBomId();
            IpmsBom bom = ipmsBomMapper.selectById(bomId);
            // 如果查出是 2 级 BOM 那么肯定没有上级了。
            if (2 == bom.getBomLevel()) {
                return safeReverseQueryBomVOList;
            }
            // 1 级 BOM 单商品的上级，把子件 id 遍历获取集合，这个集合就是 1 级 BOM 单商品的上级 id；
            if (1 == bom.getBomLevel()) {
                productBomQueryWrapper = new QueryWrapper<>();
                productBomQueryWrapper.eq("subcomponent_product_id", productId);
                List<IpmsProductBom> subcomponentProductBomList = ipmsProductBomService.list(productBomQueryWrapper);
                for (IpmsProductBom subcomponentProductBom : subcomponentProductBomList) {
                    Long fatherProductId = subcomponentProductBom.getProductId();
                    Long fatherBomId = subcomponentProductBom.getBomId();
                    IpmsProduct fatherProduct = ipmsProductService.getById(fatherProductId);
                    SafeReverseQueryBomVO safeReverseQueryBomVO = new SafeReverseQueryBomVO();
                    BeanUtils.copyProperties(fatherProduct, safeReverseQueryBomVO);
                    safeReverseQueryBomVO.setLevel(1);
                    IpmsBom fatherBom = ipmsBomMapper.selectById(fatherBomId);
                    safeReverseQueryBomVO.setBomCode(fatherBom.getBomCode());
                    safeReverseQueryBomVO.setCheckState(fatherBom.getCheckState());
                    safeReverseQueryBomVO.setBomRemark(fatherBom.getBomRemark());
                    safeReverseQueryBomVOList.add(safeReverseQueryBomVO);
                }
            }
        } else {
            // 否则就是原材料
            productBomQueryWrapper = new QueryWrapper<>();
            productBomQueryWrapper.eq("subcomponent_product_id", productId);
            List<IpmsProductBom> productBoms = ipmsProductBomService.list(productBomQueryWrapper);
            // 原材料没有作为任何子件，那就是没有上级
            if (productBoms == null || productBoms.size() <= 0) {
                return safeReverseQueryBomVOList;
            } else {
                for (IpmsProductBom productBom : productBoms) {
                    Long oneFatherProductId = productBom.getProductId();
                    Long oneFatherBomId = productBom.getBomId();
                    IpmsProduct oneFatherProduct = ipmsProductService.getById(oneFatherProductId);
                    SafeReverseQueryBomVO safeReverseQueryBomVO = new SafeReverseQueryBomVO();
                    BeanUtils.copyProperties(oneFatherProduct, safeReverseQueryBomVO);
                    safeReverseQueryBomVO.setLevel(1);
                    IpmsBom oneFatherBom = ipmsBomMapper.selectById(oneFatherBomId);
                    safeReverseQueryBomVO.setBomCode(oneFatherBom.getBomCode());
                    safeReverseQueryBomVO.setCheckState(oneFatherBom.getCheckState());
                    safeReverseQueryBomVO.setBomRemark(oneFatherBom.getBomRemark());
                    safeReverseQueryBomVOList.add(safeReverseQueryBomVO);
                    productBomQueryWrapper = new QueryWrapper<>();
                    productBomQueryWrapper.eq("subcomponent_bom_id", oneFatherBomId);
                    List<IpmsProductBom> twoFatherProductBom = ipmsProductBomService.list(productBomQueryWrapper);
                    if (twoFatherProductBom != null && twoFatherProductBom.size() > 0) {
                        for (IpmsProductBom ipmsProductBom : twoFatherProductBom) {
                            Long twoFatherProductId = ipmsProductBom.getProductId();
                            Long twoFatherBomId = ipmsProductBom.getBomId();
                            IpmsProduct twoFatherProduct = ipmsProductService.getById(twoFatherProductId);
                            safeReverseQueryBomVO = new SafeReverseQueryBomVO();
                            BeanUtils.copyProperties(twoFatherProduct, safeReverseQueryBomVO);
                            safeReverseQueryBomVO.setLevel(2);
                            IpmsBom twoFatherBom = ipmsBomMapper.selectById(twoFatherBomId);
                            safeReverseQueryBomVO.setBomCode(twoFatherBom.getBomCode());
                            safeReverseQueryBomVO.setCheckState(twoFatherBom.getCheckState());
                            safeReverseQueryBomVO.setBomRemark(twoFatherBom.getBomRemark());
                            safeReverseQueryBomVOList.add(safeReverseQueryBomVO);
                        }
                    }
                }
            }
        }
        return safeReverseQueryBomVOList;
    }
}




