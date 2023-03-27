package com.clarity.ipmsbackend.model.vo.productionbill;

import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 安全生产任务单子件商品响应封装类（提供查询父级商品关联查询子件）
 *
 * @author: clarity
 * @date: 2023年03月27日 10:12
 */

@ApiModel("安全生产任务单子件商品响应封装类（提供查询父级商品关联查询子件）")
@Data
public class SafeProductionTaskOrderSubcomponentProductVO {

    /**
     * 物料商品
     */
    @ApiModelProperty("物料商品")
    private SafeProductVO safeProductVO;

    /**
     * 物料商品用量
     */
    @ApiModelProperty("物料用量")
    private Integer productMaterialNum;
}
