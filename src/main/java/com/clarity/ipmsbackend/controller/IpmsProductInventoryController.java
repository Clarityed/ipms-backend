package com.clarity.ipmsbackend.controller;

import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.vo.inventory.SafeProductInventoryQueryVO;
import com.clarity.ipmsbackend.service.IpmsProductInventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

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

    /**
     * 查询商品库存，不支持分页，但是支持更多的字段模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/selectProductInventory")
    @ApiOperation(value = "查询商品库存，不支持分页，但是支持更多的字段模糊查询（fuzzyText 为空，查商品库存表全部数据）")
    public BaseResponse<List<SafeProductInventoryQueryVO>> selectProductInventory(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (request == null || fuzzyQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<SafeProductInventoryQueryVO> safeProductInventoryQueryVOS = ipmsProductInventoryService.selectProductInventory(fuzzyQueryRequest, request);
        return ResultUtils.success(safeProductInventoryQueryVOS);
    }
}
