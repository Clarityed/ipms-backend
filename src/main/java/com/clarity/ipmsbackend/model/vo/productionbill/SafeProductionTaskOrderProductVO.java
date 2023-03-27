package com.clarity.ipmsbackend.model.vo.productionbill;

import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 安全生产任务单商品响应封装类
 *
 * @author: clarity
 * @date: 2023年03月27日 9:35
 */

@ApiModel("安全生产任务单商品响应封装类")
@Data
public class SafeProductionTaskOrderProductVO {

    /**
     * 商品
     */
    private SafeProductVO safeProductVO;

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
     * 安全生产任务单子件商品响应封装类列表
     */
    @ApiModelProperty("安全生产任务单子件商品响应封装类列表")
    private List<SafeProductionTaskOrderSubcomponentProductVO> safeProductionTaskOrderSubcomponentProductVOList;
}
