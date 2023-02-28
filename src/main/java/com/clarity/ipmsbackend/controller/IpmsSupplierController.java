package com.clarity.ipmsbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.supplier.AddSupplierRequest;
import com.clarity.ipmsbackend.model.dto.supplier.UpdateSupplierRequest;
import com.clarity.ipmsbackend.model.vo.SafeSupplierVO;
import com.clarity.ipmsbackend.service.IpmsSupplierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 供应商控制器
 *
 * @author: clarity
 * @date: 2023年02月27日 11:55
 */

@Api(tags = "供应商管理接口")
@RestController
@Slf4j
@RequestMapping("/supplier")
public class IpmsSupplierController {

    @Resource
    private IpmsSupplierService ipmsSupplierService;

    /**
     * 供应商编号自动生成
     *
     * @return
     */
    @GetMapping("/supplierCodeAutoGenerate")
    @ApiOperation(value = "供应商编号自动生成（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> supplierCodeAutoGenerate() {
        String result = ipmsSupplierService.supplierCodeAutoGenerate();
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }

    /**
     * 增加供应商（管理员）
     *
     * @param addSupplierRequest
     * @return
     */
    @PostMapping("/addSupplier")
    @ApiOperation("增加供应商（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> addSupplier(@RequestBody AddSupplierRequest addSupplierRequest) {
        if (addSupplierRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsSupplierService.addSupplier(addSupplierRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除供应商（管理员）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteSupplierById/{id}")
    @ApiOperation(value = "根据 id 删除供应商（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> deleteSupplierById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsSupplierService.deleteSupplierById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新供应商（管理员）
     *
     * @param updateSupplierRequest
     * @return
     */
    @PutMapping("/updateSupplier")
    @ApiOperation("更新供应商（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> updateSupplier(@RequestBody UpdateSupplierRequest updateSupplierRequest) {
        if (updateSupplierRequest == null || updateSupplierRequest.getSupplierId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 为空");
        }
        int result = ipmsSupplierService.updateSupplier(updateSupplierRequest);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询供应商，且模糊查询（任意用户）
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQuery")
    @ApiOperation(value = "分页查询供应商，且模糊查询（任意用户）")
    public BaseResponse<Page<SafeSupplierVO>> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeSupplierVO> safeSupplierVOPage = ipmsSupplierService.pagingFuzzyQuery(fuzzyQueryRequest, request);
        return ResultUtils.success(safeSupplierVOPage);
    }
}
