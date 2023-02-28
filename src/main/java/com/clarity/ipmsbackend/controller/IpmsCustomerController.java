package com.clarity.ipmsbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.customer.AddCustomerRequest;
import com.clarity.ipmsbackend.model.dto.customer.UpdateCustomerRequest;
import com.clarity.ipmsbackend.model.vo.SafeCustomerVO;
import com.clarity.ipmsbackend.service.IpmsCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 客户控制器
 *
 * @author: clarity
 * @date: 2023年02月25日 17:13
 */

@Api(tags = "客户管理接口")
@RestController
@Slf4j
@RequestMapping("/customer")
public class IpmsCustomerController {

    @Resource
    private IpmsCustomerService ipmsCustomerService;

    /**
     * 客户编号自动生成
     *
     * @return
     */
    @GetMapping("/customerCodeAutoGenerate")
    @ApiOperation(value = "客户编号自动生成（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> customerCodeAutoGenerate() {
        String result = ipmsCustomerService.customerCodeAutoGenerate();
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }

    /**
     * 增加客户（管理员）
     *
     * @param addCustomerRequest
     * @return
     */
    @PostMapping("/addCustomer")
    @ApiOperation("增加客户（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> addCustomer(@RequestBody AddCustomerRequest addCustomerRequest) {
        if (addCustomerRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsCustomerService.addCustomer(addCustomerRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除客户（管理员）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteCustomerById/{id}")
    @ApiOperation(value = "根据 id 删除客户（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> deleteCustomerById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsCustomerService.deleteCustomerById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新客户（管理员）
     *
     * @param updateCustomerRequest
     * @return
     */
    @PutMapping("/updateCustomer")
    @ApiOperation("更新客户（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> updateCustomer(@RequestBody UpdateCustomerRequest updateCustomerRequest) {
        if (updateCustomerRequest == null || updateCustomerRequest.getCustomerId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 为空");
        }
        int result = ipmsCustomerService.updateCustomer(updateCustomerRequest);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询客户，且模糊查询（任意用户）
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQuery")
    @ApiOperation(value = "分页查询客户，且模糊查询（任意用户）")
    public BaseResponse<Page<SafeCustomerVO>> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeCustomerVO> safeCustomerVOPage = ipmsCustomerService.pagingFuzzyQuery(fuzzyQueryRequest, request);
        return ResultUtils.success(safeCustomerVOPage);
    }
}
