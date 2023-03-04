package com.clarity.ipmsbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clarity.ipmsbackend.common.FuzzyQueryRequest;
import com.clarity.ipmsbackend.model.dto.product.AddProductRequest;
import com.clarity.ipmsbackend.model.dto.product.UpdateProductRequest;
import com.clarity.ipmsbackend.model.dto.unit.UpdateUnitRequest;
import com.clarity.ipmsbackend.model.entity.IpmsProduct;
import com.clarity.ipmsbackend.model.vo.SafeProductVO;
import com.clarity.ipmsbackend.model.vo.SafeUnitVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Clarity
* @description 针对表【ipms_product(商品表)】的数据库操作Service
* @createDate 2023-03-04 11:28:57
*/
public interface IpmsProductService extends IService<IpmsProduct> {

    /**
     * 商品编号自动生成
     *
     * @return
     */
    String productCodeAutoGenerate();

    /**
     * 增加商品
     *
     * @param addProductRequest
     * @return
     */
    int addProduct(AddProductRequest addProductRequest);

    /**
     * 根据 id 删除商品
     *
     * @param id
     * @return
     */
    int deleteProductById(long id);

    /**
     * 更新商品
     *
     * @param updateProductRequest
     * @return
     */
    int updateProduct(UpdateProductRequest updateProductRequest);

    /**
     * 分页查询商品，且数据脱敏，且支持模糊查询
     *
     * @param fuzzyQueryRequest
     * @param request
     * @return
     */
    Page<SafeProductVO> pagingFuzzyQuery(FuzzyQueryRequest fuzzyQueryRequest, HttpServletRequest request);
}
