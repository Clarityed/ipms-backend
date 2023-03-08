package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.productbom.AddProductBomRequest;
import com.clarity.ipmsbackend.model.dto.productbom.UpdateProductBomRequest;
import com.clarity.ipmsbackend.model.entity.IpmsProductBom;
import com.clarity.ipmsbackend.model.vo.SafeProductVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
* @author Clarity
* @description 针对表【ipms_product_bom(商品物料清单（BOM） 关系表)】的数据库操作Service
* @createDate 2023-03-05 11:57:41
*/
public interface IpmsProductBomService extends IService<IpmsProductBom> {

    /**
     * 增加商品 BOM 关系信息
     *
     * @param addProductBomRequest
     * @param bomId
     * @return BOM 等级（1 - 表示等级 1，2 - 表示等级 2）
     */
    Map<String, Long> addProductBom(AddProductBomRequest addProductBomRequest, long bomId);

    /**
     * 根据 id 删除商品 BOM 关系信息
     *
     * @param id
     * @return
     */
    int deleteProductBomById(long id);

    /**
     * 更新商品 BOM 关系信息
     *
     * @param updateProductBomRequest
     * @return
     */
    int updateProductBom(UpdateProductBomRequest updateProductBomRequest);

    /**
     * 获取作为 2 级 BOM 的原材料商品 id
     *
     * @return
     */
    List<Long> getAsTwoLevelBomMaterialId();

    /**
     * 获取作为 1 级 BOM 和 2 BOM 的商品 id
     *
     * @return
     */
    List<Long> getAsProductOfBomId();

    /**
     * 分页查询商品，且数据脱敏，且支持模糊查询，并且提供给开发员创建 BOM 时，查询能够作为 BOM 的商品
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeProductVO> pagingFuzzyQueryCanAsProductOfBom(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);

    /**
     * 获取 2 级 BOM 商品的 id
     *
     * @return
     */
    List<Long> getTwoLevelBomProductId();

    /**
     * 分页查询商品，且数据脱敏，且支持模糊查询，并且提供给开发员创建 BOM 时，查询能够作为 BOM 子件的商品
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeProductVO> pagingFuzzyQueryCanAsProductOfBomSubComponent(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);
}
