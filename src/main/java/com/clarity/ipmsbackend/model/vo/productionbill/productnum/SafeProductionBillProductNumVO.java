package com.clarity.ipmsbackend.model.vo.productionbill.productnum;

import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 安全生产单据商品响应封装类
 *
 * @author: scott
 * @date: 2023年03月23日 15:08
 */

@ApiModel("安全生产单据商品响应封装类")
@Data
public class SafeProductionBillProductNumVO implements Serializable {

    /**
     * 生产单据商品 id
     */
    @ApiModelProperty("生产单据商品 id")
    private Long productionBillProductId;

    /**
     * 商品
     */
    @ApiModelProperty("物料商品")
    private SafeProductVO safeProductVO;

    /**
     * 仓库 id
     */
    @ApiModelProperty("仓库 id")
    private Long warehouseId;

    /**
     * 仓库名称
     */
    @ApiModelProperty("仓库名称")
    private String warehouseName;

    /**
     * 仓位 id
     */
    @ApiModelProperty("仓位 id")
    private Long warehousePositionId;

    /**
     * 仓位名称
     */
    @ApiModelProperty("仓位 id")
    private String warehousePositionName;

    /**
     * 可用库存
     */
    @ApiModelProperty("可用库存")
    private BigDecimal availableInventory;

    /**
     * 商品材料分子（在生产任务单中表示材料分子，必须填写，如果存在BOM单，在BOM单中获取，其他类型的单据都不用）
     */
    @ApiModelProperty("商品材料分子，只在生产任务单中使用")
    private BigDecimal productMaterialMole;

    /**
     * 需要执行的商品数量（表示商品数量）
     */
    @ApiModelProperty("商品数量")
    private BigDecimal needExecutionProductNum;

    /**
     * 剩余可被作为源单的商品数量
     */
    @ApiModelProperty("在生产任务单中表示需要投放的物料数量，在生产领料单中表示需要领料的物料数量，生产退料单（不显示），在生产入库单中表示需要入库的商品数量，生产退库单没有意义不展示")
    private BigDecimal surplusNeedExecutionProductNum;

    /**
     * 商品发料方式（默认直接发料）
     */
    @ApiModelProperty("商品发料方式（默认直接发料）")
    private String productIssuanceMethod;

    /**
     * 商品备注
     */
    @ApiModelProperty("物料备注")
    private String productRemark;

    private static final long serialVersionUID = 1L;
}
