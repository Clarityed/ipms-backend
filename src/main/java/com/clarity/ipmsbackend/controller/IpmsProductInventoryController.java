package com.clarity.ipmsbackend.controller;

import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.service.IpmsProductInventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * 库存管理控制器
 *
 * @author: clarity
 * @date: 2023年03月20日 17:53
 */

@Api(tags = "库存管理接口")
@RestController
@Slf4j
@RequestMapping("/inventory")
public class IpmsProductInventoryController {

    @Resource
    private IpmsProductInventoryService ipmsProductInventoryService;

    /**
     * 根据商品 id 和仓库 id 获取对应商品可用库存量（仓位 id 可能存在，存在的话必须传递）
     *
     * @param productId
     * @param warehouseId
     * @param warehousePositionId
     * @param request
     * @return
     */
    @GetMapping("/getAvailableInventoryOfProduct")
    @ApiOperation(value = "根据商品 id 和仓库 id 获取对应商品可用库存量（仓位 id 可能存在，存在的话必须传递）")
    public BaseResponse<BigDecimal> getAvailableInventoryOfProduct(@RequestParam("productId") Long productId,
                                                               @RequestParam("warehouseId") Long warehouseId,
                                                               @RequestParam("warehousePositionId") Long warehousePositionId,
                                                               HttpServletRequest request) {
        if (productId == null || productId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品 id 为空或者不合法");
        }
        if (warehouseId == null || warehouseId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓库 id 为空或者不合法");
        }
        if (warehousePositionId != null) {
            if (warehousePositionId < 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仓位 id 不合法");
            }
        }
        BigDecimal result = ipmsProductInventoryService.getAvailableInventoryOfProduct(productId, warehouseId, warehousePositionId, request);
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询商品库存失败");
        }
        return ResultUtils.success(result);
    }
}
