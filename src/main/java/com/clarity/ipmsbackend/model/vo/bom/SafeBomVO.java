package com.clarity.ipmsbackend.model.vo.bom;

import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 安全 BOM 响应封装类
 *
 * @author: clarity
 * @date: 2023年03月08日 16:11
 */

@ApiModel("安全 BOM 响应封装类")
@Data
public class SafeBomVO implements Serializable {

    /**
     * BOM id
     */
    @ApiModelProperty("BOM id")
    private Long bomId;

    /**
     * BOM 等级
     */
    @ApiModelProperty("BOM 等级")
    private Integer bomLevel;

    /**
     * BOM 编号
     */
    @ApiModelProperty("BOM 编号")
    private String bomCode;

    /**
     * BOM 商品父级
     */
    @ApiModelProperty("BOM 商品父级")
    private SafeProductVO safeProductVO;

    /**
     * BOM 备注
     */
    @ApiModelProperty("BOM 备注")
    private String bomRemark;

    /**
     * BOM 分类 id
     */
    @ApiModelProperty("BOM 分类 id")
    private Long bomClassId;

    /**
     * BOM 商品子件
     */
    @ApiModelProperty("BOM 商品子件")
    private List<SafeProductVO> safeProductVOList;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String founder;

    /**
     * 修改者
     */
    @ApiModelProperty("修改者")
    private String modifier;

    /**
     * 审核状态（默认为 0，0 - 未审核，1 - 已审核）
     */
    @ApiModelProperty("审核状态（默认为 0，0 - 未审核，1 - 已审核）")
    private Integer checkState;

    /**
     * 审核人
     */
    @ApiModelProperty("审核人")
    private String checker;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private String updateTime;

    /**
     * 审核时间
     */
    @ApiModelProperty("审核时间")
    private String checkTime;

    private static final long serialVersionUID = 1L;
}
