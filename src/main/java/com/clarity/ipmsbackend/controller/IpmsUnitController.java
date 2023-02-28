package com.clarity.ipmsbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clarity.ipmsbackend.annotation.AuthCheck;
import com.clarity.ipmsbackend.common.BaseResponse;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.common.ResultUtils;
import com.clarity.ipmsbackend.constant.UserConstant;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.model.dto.unit.AddUnitRequest;
import com.clarity.ipmsbackend.model.dto.unit.UpdateUnitRequest;
import com.clarity.ipmsbackend.model.vo.SafeUnitVO;
import com.clarity.ipmsbackend.service.IpmsUnitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 计量单位控制器
 *
 * @author: clarity
 * @date: 2023年02月28日 16:05
 */

@Api(tags = "计量单位管理接口")
@RestController
@Slf4j
@RequestMapping("/unit")
public class IpmsUnitController {

    @Resource
    private IpmsUnitService ipmsUnitService;

    /**
     * 计量单位编号自动生成
     *
     * @return
     */
    @GetMapping("/unitCodeAutoGenerate")
    @ApiOperation(value = "计量单位编号自动生成（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> unitCodeAutoGenerate() {
        String result = ipmsUnitService.unitCodeAutoGenerate();
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编码自动生成器异常");
        }
        return ResultUtils.success(result);
    }

    /**
     * 增加计量单位（管理员）
     *
     * @param addUnitRequest
     * @return
     */
    @PostMapping("/addUnit")
    @ApiOperation("增加计量单位（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> addUnit(@RequestBody AddUnitRequest addUnitRequest) {
        if (addUnitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = ipmsUnitService.addUnit(addUnitRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除计量单位（管理员）
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteUnitById/{id}")
    @ApiOperation(value = "根据 id 删除计量单位（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> deleteUnitById(@PathVariable("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        int result = ipmsUnitService.deleteUnitById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新计量单位（管理员）
     *
     * @param updateUnitRequest
     * @return
     */
    @PutMapping("/updateUnit")
    @ApiOperation("更新计量单位（管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> updateUnit(@RequestBody UpdateUnitRequest updateUnitRequest) {
        if (updateUnitRequest == null || updateUnitRequest.getUnitId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空或者 id 为空");
        }
        int result = ipmsUnitService.updateUnit(updateUnitRequest);
        return ResultUtils.success(result);
    }

    /**
     * 分页查询计量单位，且模糊查询（任意用户）
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/pagingFuzzyQuery")
    @ApiOperation(value = "分页查询计量单位，且模糊查询（任意用户）")
    public BaseResponse<Page<SafeUnitVO>> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request) {
        if (fuzzyQueryRequest == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<SafeUnitVO> safeUnitVOPage = ipmsUnitService.pagingFuzzyQuery(fuzzyQueryRequest, request);
        return ResultUtils.success(safeUnitVOPage);
    }
}
