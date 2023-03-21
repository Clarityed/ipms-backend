package com.clarity.ipmsbackend.controller;

import com.clarity.ipmsbackend.service.IpmsSaleBillService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 销售单据控制器
 *
 * @author: scott
 * @date: 2023年03月21日 10:42
 */

@Api(tags = "销售单据管理接口")
@RestController
@Slf4j
@RequestMapping("/saleBill")
public class IpmsSaleBillController {

    @Resource
    private IpmsSaleBillService ipmsSaleBillService;
}
