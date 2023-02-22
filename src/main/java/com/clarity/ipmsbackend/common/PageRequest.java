package com.clarity.ipmsbackend.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求体
 *
 * @author: clarity
 * @date: 2023年02月21日 18:20
 */

@ApiModel("通用分页请求体")
@Data
public class PageRequest implements Serializable {

    /**
     * 页面数据条数
     */
    @ApiModelProperty("页面数据条数")
    protected int pageSize = 10;

    /**
     * 当前第几页
     */
    @ApiModelProperty("当前第几页")
    protected int currentPage = 1;

    private static final long serialVersionUID = 1L;
}
