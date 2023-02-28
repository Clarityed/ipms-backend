package com.clarity.ipmsbackend.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 安全计量单位响应封装类
 *
 * @author: clarity
 * @date: 2023年02月28日 16:12
 */

@ApiModel("安全计量单位响应封装类")
@Data
public class SafeUnitVO implements Serializable {

    /**
     * 计量单位 id
     */
    @ApiModelProperty("计量单位 id")
    private Long unitId;

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
