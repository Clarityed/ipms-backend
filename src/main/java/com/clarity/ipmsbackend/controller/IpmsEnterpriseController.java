package com.clarity.ipmsbackend.controller;

import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.enterprise.UpdateEnterpriseRequest;
import com.clarity.ipmsbackend.model.entity.IpmsEnterprise;
import com.clarity.ipmsbackend.model.vo.SafeEnterpriseVO;
import com.clarity.ipmsbackend.service.IpmsEnterpriseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 企业控制器
 *
 * @author: clarity
 * @date: 2023年02月20日 9:57
 */

@Api(tags = "企业管理接口")
@RestController
@Slf4j
@RequestMapping("/enterprise")
public class IpmsEnterpriseController {

    @Resource
    private IpmsEnterpriseService ipmsEnterpriseService;

    @ApiIgnore
    @GetMapping("/test/{id}")
    public BaseResponse<Integer> test(@PathVariable("id")int id) {
        if (id == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不能为 1");
        }
        return ResultUtils.success(id);
    }

    @GetMapping("/getEnterprise")
    @ApiOperation("查询企业（任意用户）")
    public BaseResponse<SafeEnterpriseVO> getEnterprise(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        IpmsEnterprise enterprise = ipmsEnterpriseService.getEnterprise(request);
        SafeEnterpriseVO safeEnterpriseVO = new SafeEnterpriseVO();
        BeanUtils.copyProperties(enterprise, safeEnterpriseVO);
        return ResultUtils.success(safeEnterpriseVO);
    }

    @PutMapping("/updateEnterprise")
    @ApiOperation("更新企业信息（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> updateEnterprise(@RequestBody UpdateEnterpriseRequest updateEnterpriseRequest) {
        if (updateEnterpriseRequest == null || updateEnterpriseRequest.getEnterpriseId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 为空");
        }
        int result = ipmsEnterpriseService.updateEnterprise(updateEnterpriseRequest);
        return ResultUtils.success(result);
    }
}
