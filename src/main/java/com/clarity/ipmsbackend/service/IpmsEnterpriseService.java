package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.model.dto.enterprise.UpdateEnterpriseRequest;
import com.clarity.ipmsbackend.model.entity.IpmsEnterprise;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_enterprise】的数据库操作Service
* @createDate 2023-02-20 09:47:33
*/
public interface IpmsEnterpriseService extends IService<IpmsEnterprise> {

    /**
     * 获取企业（所属企业信息）
     *
     * @param request
     * @return
     */
    IpmsEnterprise getEnterprise(HttpServletRequest request);

    /**
     * 修改企业信息（管理员）
     *
     * @param updateEnterpriseRequest
     * @return
     */
    int updateEnterprise(UpdateEnterpriseRequest updateEnterpriseRequest);
}
