package com.clarity.ipmsbackend.controller;

import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.service.IpmsEnterpriseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * 企业控制器
 *
 * @author: clarity
 * @date: 2023年02月20日 9:57
 */

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
}
