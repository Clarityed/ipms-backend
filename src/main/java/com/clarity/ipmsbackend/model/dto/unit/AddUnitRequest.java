package com.clarity.ipmsbackend.model.dto.unit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 增加计量单位请求封装类
 *
 * @author: clarity
 * @date: 2023年02月28日 16:08
 */

@ApiModel("增加计量单位请求封装类")
@Data
public class AddUnitRequest implements Serializable {

    /**
     * 计量单位编号
     */
    @ApiModelProperty("计量单位编号")
    private String unitCode;

    /**
     * 计量单位名称
     */
    @ApiModelProperty("计量单位名称")
    private String unitName;

    private static final long serialVersionUID = 1L;
}
