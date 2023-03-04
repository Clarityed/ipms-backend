package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.constant.ProductConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.product.AddProductRequest;
import com.clarity.ipmsbackend.model.dto.product.UpdateProductRequest;
import com.clarity.ipmsbackend.model.entity.*;
import com.clarity.ipmsbackend.mapper.IpmsProductMapper;
import com.clarity.ipmsbackend.model.entity.IpmsProduct;
import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import com.clarity.ipmsbackend.model.vo.SafeProductVO;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Clarity
 * @description 针对表【ipms_product(商品表)】的数据库操作Service实现
 * @createDate 2023-03-04 11:28:57
 */

@Service
@Slf4j
public class IpmsProductServiceImpl extends ServiceImpl<IpmsProductMapper, IpmsProduct>
        implements IpmsProductService {

    @Resource
    private IpmsProductMapper ipmsProductMapper;

    @Resource
    private IpmsUnitService ipmsUnitService;

    @Resource
    private IpmsUserService ipmsUserService;

    @Override
    public String productCodeAutoGenerate() {
        QueryWrapper<IpmsProduct> ipmsProductQueryWrapper = new QueryWrapper<>();
        List<IpmsProduct> ipmsProductList = ipmsProductMapper.selectList(ipmsProductQueryWrapper);
        String productCode;
        if (ipmsProductList.size() == 0) {
            productCode = "SP00000";
        } else {
            IpmsProduct lastProduct = ipmsProductList.get(ipmsProductList.size() - 1);
            productCode = lastProduct.getProductCode();
        }
        String nextProductCode = null;
        try {
            nextProductCode = CodeAutoGenerator.literallyCode(productCode);
        } catch (Exception e) {
            log.info("编码自动生成器异常");
        }
        return nextProductCode;
    }

    @Override
    public int addProduct(AddProductRequest addProductRequest) {
        // 1. 商品编码和商品名称不能为空
        String productCode = addProductRequest.getProductCode();
        String productName = addProductRequest.getProductName();
        if (StringUtils.isAnyBlank(productCode, productName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品编码或者名称为空");
        }
        // 2. 商品单位 id 不能为空，且要合法
        Long unitId = addProductRequest.getUnitId();
        if (unitId == null || unitId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单位 id 为空或者不合法");
        }
        // 3. 参考成本和采购价不能为负数
        BigDecimal productReferenceCost = addProductRequest.getProductReferenceCost();
        BigDecimal productPurchasePrice = addProductRequest.getProductPurchasePrice();
        if (productReferenceCost != null) {
            if (productReferenceCost.doubleValue() < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "参考成本小于 0");
            }
        }
        if (productPurchasePrice != null) {
            if (productPurchasePrice.doubleValue() < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购价小于 0");
            }
        }
        // 4. 判断是否开启保质期管理，如果开启则必须输入保质期单位时间
        Integer isShelfLifeManagement = addProductRequest.getIsShelfLifeManagement();
        String productShelfLifeUnit = addProductRequest.getProductShelfLifeUnit();
        Integer productShelfLife = addProductRequest.getProductShelfLife();
        if (isShelfLifeManagement != null) {
            if (ProductConstant.OPEN_SHELF_LIFE_MANAGEMENT == isShelfLifeManagement) {
                if (productShelfLifeUnit == null || productShelfLife == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "保质期单位和时间为空");
                }
                if (productShelfLife <= 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "保质期至少要大于 0");
                }
            }
        }
        // 5. 判断单位是否存在
        IpmsUnit productUnit = ipmsUnitService.getById(unitId);
        if (productUnit == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "单位不存在");
        }
        // 6. 商品编码不能重复
        QueryWrapper<IpmsProduct> productQueryWrapper = new QueryWrapper<>();
        productQueryWrapper.eq("product_code", productCode);
        IpmsProduct ipmsProduct = ipmsProductMapper.selectOne(productQueryWrapper);
        if (ipmsProduct != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品编码重复");
        }
        // 7. 插入数据
        IpmsProduct product = new IpmsProduct();
        BeanUtils.copyProperties(addProductRequest, product);
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        int result = ipmsProductMapper.insert(product);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int deleteProductById(long id) {
        // 1. 商品 id 要合法
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 id 不合法");
        }
        // 2. todo 判断商品有没有被 BOM 单使用，被使用无法删除，与指相似的有销售订单，等等其他相关订单
        // 3. 删除商品
        int result = ipmsProductMapper.deleteById(id);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public int updateProduct(UpdateProductRequest updateProductRequest) {
        // 1. 商品 id 不能为空，且要合法
        Long productId = updateProductRequest.getProductId();
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 id 为空或者不合法");
        }
        // 2. 参考成本和采购价不能为负数
        BigDecimal productReferenceCost = updateProductRequest.getProductReferenceCost();
        BigDecimal productPurchasePrice = updateProductRequest.getProductPurchasePrice();
        if (productReferenceCost != null) {
            if (productReferenceCost.doubleValue() < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "参考成本小于 0");
            }
        }
        if (productPurchasePrice != null) {
            if (productPurchasePrice.doubleValue() < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "采购价小于 0");
            }
        }
        // 3. 判断是否开启保质期管理，如果开启则必须输入保质期单位时间，保质期不能小于 1 天
        Integer isShelfLifeManagement = updateProductRequest.getIsShelfLifeManagement();
        String productShelfLifeUnit = updateProductRequest.getProductShelfLifeUnit();
        Integer productShelfLife = updateProductRequest.getProductShelfLife();
        if (isShelfLifeManagement != null) {
            if (ProductConstant.OPEN_SHELF_LIFE_MANAGEMENT == isShelfLifeManagement) {
                if (productShelfLifeUnit == null || productShelfLife == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "保质期单位和时间为空");
                }
                if (productShelfLife <= 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "保质期至少要大于 0");
                }
            }
        }
        // 4. 判断要修改的商品是否存在
        IpmsProduct oldProduct = ipmsProductMapper.selectById(productId);
        if (oldProduct == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 5. 商品编号不能修改（暂定，后面可能要修改）
        String productCode = updateProductRequest.getProductCode();
        if (productCode != null) {
            if (!productCode.equals(oldProduct.getProductCode())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品编号不能修改");
            }
        }
        // 6. 判断要修改的计量单位是否存在
        Long unitId = updateProductRequest.getUnitId();
        if (unitId != null) {
            IpmsUnit productUnit = ipmsUnitService.getById(unitId);
            if (productUnit == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "单位 id 不存在");
            }
        }
        // 7. 修改商品信息
        IpmsProduct newProduct = new IpmsProduct();
        BeanUtils.copyProperties(updateProductRequest, newProduct);
        newProduct.setUpdateTime(new Date());
        int result = ipmsProductMapper.updateById(newProduct);
        if (result != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }

    @Override
    public Page<SafeProductVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
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
        if (StringUtils.isNotBlank(fuzzyText)) {
            productQueryWrapper.like("product_code", fuzzyText).or()
                    .like("product_name", fuzzyText).or()
                    .like("product_type", fuzzyText).or()
                    .like("product_specification", fuzzyText).or();
        }
        Page<IpmsProduct> productPage = ipmsProductMapper.selectPage(page, productQueryWrapper);
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
}




