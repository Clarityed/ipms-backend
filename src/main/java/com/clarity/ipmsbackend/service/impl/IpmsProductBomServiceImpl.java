package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.constant.ProductConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsProductBomMapper;
import com.clarity.ipmsbackend.model.dto.productbom.AddProductBomRequest;
import com.clarity.ipmsbackend.model.dto.productbom.UpdateProductBomRequest;
import com.clarity.ipmsbackend.model.entity.IpmsProduct;
import com.clarity.ipmsbackend.model.entity.IpmsProductBom;
import com.clarity.ipmsbackend.model.entity.IpmsWarehouse;
import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author Clarity
* @description 针对表【ipms_product_bom(商品物料清单（BOM） 关系表)】的数据库操作Service实现
* @createDate 2023-03-05 11:57:41
*/
@Service
public class IpmsProductBomServiceImpl extends ServiceImpl<IpmsProductBomMapper, IpmsProductBom>
    implements IpmsProductBomService{

    @Resource
    private IpmsProductService ipmsProductService;

    @Resource
    private IpmsProductBomMapper ipmsProductBomMapper;

    @Resource
    private IpmsWarehouseService ipmsWarehouseService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Resource
    private IpmsUnitService ipmsUnitService;

    @Override
    public Map<String, Long> addProductBom(AddProductBomRequest addProductBomRequest, long bomId) {
        // 1. 参数校验
        Long productId = addProductBomRequest.getProductId();
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 id 为空或者不合法");
        }
        Long subcomponentProductId = addProductBomRequest.getSubcomponentProductId();
        if (subcomponentProductId == null || subcomponentProductId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "子件商品 id 为空或者不合法");
        }
        // 组件商品和子件商品不能相同
        if (productId.equals(subcomponentProductId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法把自身当作自己的子件");
        }
        Integer subcomponentMaterialNum = addProductBomRequest.getSubcomponentMaterialNum();
        if (subcomponentMaterialNum == null || subcomponentMaterialNum <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "子件材料用量为空或者小于等于 0");
        }
        Integer subcomponentLossRate = addProductBomRequest.getSubcomponentLossRate();
        if (subcomponentLossRate != null) {
            if (subcomponentLossRate < 0 || subcomponentLossRate > 100) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "损耗率只能在 0 - 100 之间");
            }
        }
        // 作为 2 级的商品无法创建 BOM 单。
        QueryWrapper<IpmsProductBom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subcomponent_product_id", productId);
        List<IpmsProductBom> productBomList = ipmsProductBomMapper.selectList(queryWrapper);
        if (productBomList != null && productBomList.size() > 0) {
            for (IpmsProductBom productBom : productBomList) {
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("subcomponent_bom_id", productBom.getBomId());
                List<IpmsProductBom> validTwoLevelProductBomList = ipmsProductBomMapper.selectList(queryWrapper);
                if (validTwoLevelProductBomList != null && validTwoLevelProductBomList.size() > 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "BOM 的 2 级原材料无法作为 BOM ");
                }
                // 作为 1 级的 BOM 必须是原材料才能创建，
                if (productBom.getSubcomponentBomId() == null) {
                    queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("subcomponent_product_id", subcomponentProductId);
                    List<IpmsProductBom> ipmsProductBomList = ipmsProductBomMapper.selectList(queryWrapper);
                    if (ipmsProductBomList != null && ipmsProductBomList.size() > 0 && ipmsProductBomList.get(0).getSubcomponentBomId() != null) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "作为 BOM 的 1 级原材料才能创建 BOM，而且只能创建 1 级的");
                    }
                }
            }
        }
        // 2.1 判断组件商品是否存在
        IpmsProduct componentProduct = ipmsProductService.getById(productId);
        if (componentProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品组件不存在");
        }
        // 2.2 判断该商品是否可为组件
        if (componentProduct.getIsComponent() != ProductConstant.OPEN_COMPONENT_FUNCTION) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品组件" + componentProduct.getProductName() + "没有开启可为组件功能");
        }
        // 3.1 判断子件商品是否存在
        IpmsProduct subcomponentProduct = ipmsProductService.getById(subcomponentProductId);
        if (subcomponentProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品子件不存在");
        }
        // 3.2 判断该商品是否可为子件
        if (subcomponentProduct.getIsSubcomponent() != ProductConstant.OPEN_SUBCOMPONENT_FUNCTION) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品子件" + subcomponentProduct.getProductName() + "没有开启可为子件功能");
        }
        // 如果仓库不为空，判断仓库是否存在
        Long subcomponentIssuingWarehouseId = addProductBomRequest.getSubcomponentIssuingWarehouseId();
        if (subcomponentIssuingWarehouseId != null && subcomponentIssuingWarehouseId > 0) {
            IpmsWarehouse warehouse = ipmsWarehouseService.getById(subcomponentIssuingWarehouseId);
            if (warehouse == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, subcomponentProduct.getProductName() + "的仓库不存在");
            }
        }
        // 4. 利用字段子件商品 id 去根据字段商品 id 查询数据（子件中有 1 级子件的添加）
        //    如果存在，那么该子件商品也是 BOM 单，那么就是 2 级 BOM
        QueryWrapper<IpmsProductBom> productBomQueryWrapper = new QueryWrapper<>();
        productBomQueryWrapper.eq("product_id", subcomponentProductId);
        List<IpmsProductBom> ipmsProductBomList = ipmsProductBomMapper.selectList(productBomQueryWrapper);
        // 5. 插入数据
        int result = 1;
        IpmsProductBom productBom = new IpmsProductBom();
        BeanUtils.copyProperties(addProductBomRequest, productBom);
        productBom.setBomId(bomId);
        if (ipmsProductBomList != null && ipmsProductBomList.size() > 0) {
            for (IpmsProductBom ipmsProductBom : ipmsProductBomList) {
                productBom.setSubcomponentBomId(ipmsProductBom.getBomId());
            }
            result = 2;
        }
        // 在商品已经是某个 BOM 单的子件，将它作为 BOM 单添加
        productBomQueryWrapper = new QueryWrapper<>();
        productBomQueryWrapper.eq("subcomponent_product_id", productId);
        List<IpmsProductBom> productBoms = ipmsProductBomMapper.selectList(productBomQueryWrapper);
        for (IpmsProductBom bom : productBoms) {
            bom.setSubcomponentBomId(bomId);
            ipmsProductBomMapper.updateById(bom);
        }
        productBom.setCreateTime(new Date());
        productBom.setUpdateTime(new Date());
        int insertResult = ipmsProductBomMapper.insert(productBom);
        if (insertResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        Map<String, Long> map = new HashMap<>();
        map.put("bomLevel", (long) result);
        map.put("productBomId", productBom.getProductBomId());
        return map;
    }

    @Override
    public int deleteProductBomById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsProductBomMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int updateProductBom(UpdateProductBomRequest updateProductBomRequest) {
        // 1. 参数校验
        Long productBomId = updateProductBomRequest.getProductBomId();
        if (productBomId == null || productBomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 BOM 关系 id 为空或者不合法");
        }
        Long productId = updateProductBomRequest.getProductId();
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 id 为空或者不合法");
        }
        Long subcomponentProductId = updateProductBomRequest.getSubcomponentProductId();
        if (subcomponentProductId == null || subcomponentProductId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "子件商品 id 为空或者不合法");
        }
        // 组件商品和子件商品不能相同
        if (productId.equals(subcomponentProductId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法把自身当作自己的子件");
        }
        Integer subcomponentMaterialNum = updateProductBomRequest.getSubcomponentMaterialNum();
        if (subcomponentMaterialNum != null) {
            if (subcomponentMaterialNum <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "子件材料用量小于等于 0");
            }
        }
        Integer subcomponentLossRate = updateProductBomRequest.getSubcomponentLossRate();
        if (subcomponentLossRate != null) {
            if (subcomponentLossRate < 0 || subcomponentLossRate > 100) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "损耗率只能在 0 - 100 之间");
            }
        }
        // 2.1 判断组件商品是否存在
        IpmsProduct componentProduct = ipmsProductService.getById(productId);
        if (componentProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品组件不存在");
        }
        // 2.2 判断该商品是否可为组件
        if (componentProduct.getIsComponent() != ProductConstant.OPEN_COMPONENT_FUNCTION) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品组件" + componentProduct.getProductName() + "没有开启可为组件功能");
        }
        // 3.1 判断子件商品是否存在
        IpmsProduct subcomponentProduct = ipmsProductService.getById(subcomponentProductId);
        if (subcomponentProduct == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品子件不存在");
        }
        // 3.2 判断该商品是否可为子件
        if (subcomponentProduct.getIsSubcomponent() != ProductConstant.OPEN_SUBCOMPONENT_FUNCTION) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品子件" + subcomponentProduct.getProductName() + "没有开启可为子件功能");
        }
        // 如果仓库不为空，判断仓库是否存在
        Long subcomponentIssuingWarehouseId = updateProductBomRequest.getSubcomponentIssuingWarehouseId();
        if (subcomponentIssuingWarehouseId != null && subcomponentIssuingWarehouseId > 0) {
            IpmsWarehouse warehouse = ipmsWarehouseService.getById(subcomponentIssuingWarehouseId);
            if (warehouse == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, subcomponentProduct.getProductName() + "的仓库不存在");
            }
        }
        // 4. 利用字段子件商品 id 去根据字段商品 id 查询数据
        //    如果存在，那么该子件商品也是 BOM 单，那么就是 2 级 BOM
        QueryWrapper<IpmsProductBom> productBomQueryWrapper = new QueryWrapper<>();
        productBomQueryWrapper.eq("product_id", subcomponentProductId);
        List<IpmsProductBom> ipmsProductBomList = ipmsProductBomMapper.selectList(productBomQueryWrapper);
        // 5. 修改数据
        int result = 1;
        IpmsProductBom productBom = new IpmsProductBom();
        BeanUtils.copyProperties(updateProductBomRequest, productBom);
        if (ipmsProductBomList != null&& ipmsProductBomList.size() > 0) {
            productBom.setSubcomponentBomId(ipmsProductBomList.get(0).getBomId());
            result = 2;
        }
        productBom.setUpdateTime(new Date());
        int updateResult = ipmsProductBomMapper.updateById(productBom);
        if (updateResult != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public List<Long> getAsTwoLevelBomMaterialId() {
        List<IpmsProductBom> asTwoLevelBomMaterialList = ipmsProductBomMapper.getAsTwoLevelBomMaterial();
        List<Long> asTwoLevelBomMaterialIdList = new ArrayList<>();
        if (asTwoLevelBomMaterialList != null && asTwoLevelBomMaterialList.size() > 0) {
            for (IpmsProductBom asTwoLevelBomMaterial : asTwoLevelBomMaterialList) {
                asTwoLevelBomMaterialIdList.add(asTwoLevelBomMaterial.getSubcomponentProductId());
            }
        }
        return asTwoLevelBomMaterialIdList;
    }

    @Override
    public List<Long> getAsProductOfBomId() {
        List<IpmsProductBom> asProductOfBomList = ipmsProductBomMapper.getAsProductOfBom();
        List<Long> asProductIdOfBomList = new ArrayList<>();
        if (asProductOfBomList != null && asProductOfBomList.size() > 0) {
            for (IpmsProductBom asProductOfBom : asProductOfBomList) {
                asProductIdOfBomList.add(asProductOfBom.getProductId());
            }
        }
        return asProductIdOfBomList;
    }

    @Override
    public Page<SafeProductVO> pagingFuzzyQueryCanAsProductOfBom(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        List<Long> asTwoLevelBomMaterialIdList = this.getAsTwoLevelBomMaterialId();
        List<Long> asProductOfBomIdList = this.getAsProductOfBomId();
        List<Long> canAsProductIdOfBomList = new ArrayList<>();
        if (asTwoLevelBomMaterialIdList.size() > 0) {
            canAsProductIdOfBomList.addAll(asTwoLevelBomMaterialIdList);
        }
        if (asProductOfBomIdList.size() > 0) {
            canAsProductIdOfBomList.addAll(asProductOfBomIdList);
        }
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Page<IpmsProduct> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsProduct> productQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (canAsProductIdOfBomList.size() > 0) {
            if (StringUtils.isNotBlank(fuzzyText)) {
                productQueryWrapper.like("product_code", fuzzyText).and(productId -> productId.notIn("product_id", canAsProductIdOfBomList)).or()
                        .like("product_name", fuzzyText).and(productId -> productId.notIn("product_id", canAsProductIdOfBomList)).or()
                        .like("product_type", fuzzyText).and(productId -> productId.notIn("product_id", canAsProductIdOfBomList)).or()
                        .like("product_specification", fuzzyText).and(productId -> productId.notIn("product_id", canAsProductIdOfBomList));
            } else {
                productQueryWrapper.notIn("product_id", canAsProductIdOfBomList);
            }
        } else {
            productQueryWrapper.like("product_code", fuzzyText).or()
                    .like("product_name", fuzzyText).or()
                    .like("product_type", fuzzyText).or()
                    .like("product_specification", fuzzyText);
        }
        Page<IpmsProduct> productPage = ipmsProductService.page(page, productQueryWrapper);
        List<SafeProductVO> safeProductVOList = productPage.getRecords().stream().map(ipmsProduct -> {
            SafeProductVO safeProductVO = new SafeProductVO();
            BeanUtils.copyProperties(ipmsProduct, safeProductVO);
            Long unitId = ipmsProduct.getUnitId();
            if (unitId != null && unitId > 0) {
                safeProductVO.setUnitName(ipmsUnitService.getById(unitId).getUnitName());
            }
            return safeProductVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeProductVO> safeProductVOPage = new PageDTO<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        safeProductVOPage.setRecords(safeProductVOList);
        return safeProductVOPage;
    }

    @Override
    public List<Long> getTwoLevelBomProductId() {
        List<IpmsProductBom> twoLevelBomProductList = ipmsProductBomMapper.getTwoLevelBomProduct();
        List<Long> twoLevelBomProductIdList = new ArrayList<>();
        if (twoLevelBomProductList != null && twoLevelBomProductList.size() > 0) {
            for (IpmsProductBom twoLevelBomProduct : twoLevelBomProductList) {
                twoLevelBomProductIdList.add(twoLevelBomProduct.getProductId());
            }
        }
        return twoLevelBomProductIdList;
    }

    @Override
    public Page<SafeProductVO> pagingFuzzyQueryCanAsProductOfBomSubComponent(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        List<Long> twoLevelBomProductIdList = this.getTwoLevelBomProductId();
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Page<IpmsProduct> page = new Page<>(fuzzyQueryRequest.getCurrentPage(), fuzzyQueryRequest.getPageSize());
        QueryWrapper<IpmsProduct> productQueryWrapper = new QueryWrapper<>();
        String fuzzyText = fuzzyQueryRequest.getFuzzyText();
        if (twoLevelBomProductIdList.size() > 0) {
            if (StringUtils.isNotBlank(fuzzyText)) {
                productQueryWrapper.like("product_code", fuzzyText).and(productId -> productId.notIn("product_id", twoLevelBomProductIdList)).or()
                        .like("product_name", fuzzyText).and(productId -> productId.notIn("product_id", twoLevelBomProductIdList)).or()
                        .like("product_type", fuzzyText).and(productId -> productId.notIn("product_id", twoLevelBomProductIdList)).or()
                        .like("product_specification", fuzzyText).and(productId -> productId.notIn("product_id", twoLevelBomProductIdList));
            } else {
                productQueryWrapper.notIn("product_id", twoLevelBomProductIdList);
            }
        } else {
            productQueryWrapper.like("product_code", fuzzyText).or()
                    .like("product_name", fuzzyText).or()
                    .like("product_type", fuzzyText).or()
                    .like("product_specification", fuzzyText);
        }
        Page<IpmsProduct> productPage = ipmsProductService.page(page, productQueryWrapper);
        List<SafeProductVO> safeProductVOList = productPage.getRecords().stream().map(ipmsProduct -> {
            SafeProductVO safeProductVO = new SafeProductVO();
            BeanUtils.copyProperties(ipmsProduct, safeProductVO);
            Long unitId = ipmsProduct.getUnitId();
            if (unitId != null && unitId > 0) {
                safeProductVO.setUnitName(ipmsUnitService.getById(unitId).getUnitName());
            }
            return safeProductVO;
        }).collect(Collectors.toList());
        // 关键步骤
        Page<SafeProductVO> safeProductVOPage = new PageDTO<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        safeProductVOPage.setRecords(safeProductVOList);
        return safeProductVOPage;
    }

    @Override
    public List<IpmsProductBom> getBomSubComponentMessage(String bomCode) {
        if (bomCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "BOM 编号为空");
        }
        List<IpmsProductBom> bomSubComponentMessageList = ipmsProductBomMapper.getBomSubComponentMessage(bomCode);
        if (bomSubComponentMessageList == null || bomSubComponentMessageList.size() <= 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return bomSubComponentMessageList;
    }
}




