package com.clarity.ipmsbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.product.AddProductRequest;
import com.clarity.ipmsbackend.model.dto.product.UpdateProductRequest;
import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import com.clarity.ipmsbackend.service.IpmsProductBomService;
import com.clarity.ipmsbackend.service.IpmsProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 商品控制器
 *
 * @author: clarity
 * @date: 2023年03月04日 11:55
 */

@Api(tags = "商品管理接口")
@RestController
@Slf4j
@RequestMapping("/product")
public class IpmsProductController {

    @Resource
    private IpmsProductService ipmsProductService;

    @Resource
    private IpmsProductBomService ipmsProductBomService;

    /**
     * 商品编号自动生成
     *
     * @return
     */
    @GetMapping("/productCodeAutoGenerate")
    @ApiOperation(value = "商品编号自动生成（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> productCodeAutoGenerate() {
        String result = ipmsProductService.productCodeAutoGenerate();
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }

    /**
     * 增加商品（管理员）
     *
     * @param addProductRequest
     * @return
     */
    @PostMapping("/addProduct")
    @ApiOperation("增加商品（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> addProduct(@RequestBody AddProductRequest addProductRequest) {
        if (addProductRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsProductService.addProduct(addProductRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除商品（管理员）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteProductById/{id}")
    @ApiOperation(value = "根据 id 删除商品（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> deleteProductById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsProductService.deleteProductById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新商品（管理员）
     *
     * @param updateProductRequest
     * @return
     */
    @PutMapping("/updateProduct")
    @ApiOperation("更新商品（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> updateProduct(@RequestBody UpdateProductRequest updateProductRequest) {
        if (updateProductRequest == null || updateProductRequest.getProductId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 为空");
        }
        int result = ipmsProductService.updateProduct(updateProductRequest);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询商品，且模糊查询（任意用户）
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQuery")
    @ApiOperation(value = "分页查询商品，且模糊查询（任意用户）")
    public BaseResponse<Page<SafeProductVO>> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeProductVO> safeProductVOPage = ipmsProductService.pagingFuzzyQuery(fuzzyQueryRequest, request);
        return ResultUtils.success(safeProductVOPage);
    }

    /**
     * 分页查询商品，且数据脱敏，且支持模糊查询，并且提供给开发员创建 BOM 时，查询能够作为 BOM 的商品（开发员，开发主管）
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQueryCanAsProductOfBom")
    @ApiOperation(value = "分页查询商品，且模糊查询，提供给开发员创建 BOM 时，查询能够作为 BOM 的商品（开发员，开发主管）")
    @AuthCheck(anyRole = {UserConstant.DEVELOP_ROLE, UserConstant.DEVELOP_ROLE_SUPER})
    public BaseResponse<Page<SafeProductVO>> pagingFuzzyQueryCanAsProductOfBom(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeProductVO> safeProductVOPage = ipmsProductBomService.pagingFuzzyQueryCanAsProductOfBom(fuzzyQueryRequest, request);
        return ResultUtils.success(safeProductVOPage);
    }

    /**
     * 分页查询商品，且数据脱敏，且支持模糊查询，并且提供给开发员创建 BOM 时，查询能够作为 BOM 子件的商品（开发员，开发主管）
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQueryCanAsProductOfBomSubComponent")
    @ApiOperation(value = "分页查询商品，且数据脱敏，且支持模糊查询，并且提供给开发员创建 BOM 时，查询能够作为 BOM 子件的商品（开发员，开发主管）")
    @AuthCheck(anyRole = {UserConstant.DEVELOP_ROLE, UserConstant.DEVELOP_ROLE_SUPER})
    public BaseResponse<Page<SafeProductVO>> pagingFuzzyQueryCanAsProductOfBomSubComponent(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeProductVO> safeProductVOPage = ipmsProductBomService.pagingFuzzyQueryCanAsProductOfBomSubComponent(fuzzyQueryRequest, request);
        return ResultUtils.success(safeProductVOPage);
    }
}
