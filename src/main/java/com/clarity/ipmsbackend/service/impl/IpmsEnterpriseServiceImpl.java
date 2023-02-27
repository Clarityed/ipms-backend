package com.clarity.ipmsbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clarity.ipmsbackend.common.ErrorCode;
import com.clarity.ipmsbackend.exception.BusinessException;
import com.clarity.ipmsbackend.mapper.IpmsEnterpriseMapper;
import com.clarity.ipmsbackend.model.dto.enterprise.UpdateEnterpriseRequest;
import com.clarity.ipmsbackend.model.entity.IpmsEnterprise;
import com.clarity.ipmsbackend.model.vo.SafeUserVO;
import com.clarity.ipmsbackend.service.IpmsEnterpriseService;
import com.clarity.ipmsbackend.service.IpmsUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
* @author Clarity
* @description 针对表【ipms_enterprise】的数据库操作Service实现
* @createDate 2023-02-20 09:47:33
*/
@Service
public class IpmsEnterpriseServiceImpl extends ServiceImpl<IpmsEnterpriseMapper, IpmsEnterprise>
    implements IpmsEnterpriseService{

    @Resource
    private IpmsEnterpriseMapper ipmsEnterpriseMapper;

    @Resource
    private IpmsUserService ipmsUserService;

    @Override
    public IpmsEnterprise getEnterprise(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SafeUserVO loginUser = ipmsUserService.getLoginUser(request);
        long enterpriseId = loginUser.getEnterpriseId();
        IpmsEnterprise ipmsEnterprise = ipmsEnterpriseMapper.selectById(enterpriseId);
        if (ipmsEnterprise == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ipmsEnterprise;
    }

    @Override
    public int updateEnterprise(UpdateEnterpriseRequest updateEnterpriseRequest) {
        long enterpriseId = updateEnterpriseRequest.getEnterpriseId();
        if (enterpriseId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不合法");
        }
        // 判断企业是否存在
        IpmsEnterprise ipmsEnterprise = ipmsEnterpriseMapper.selectById(enterpriseId);
        if (ipmsEnterprise == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 修改信息
        IpmsEnterprise enterprise = new IpmsEnterprise();
        BeanUtils.copyProperties(updateEnterpriseRequest, enterprise);
        enterprise.setUpdateTime(new Date());
        return ipmsEnterpriseMapper.updateById(enterprise);
    }
}




