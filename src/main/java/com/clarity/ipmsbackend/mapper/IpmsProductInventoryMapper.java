package com.clarity.ipmsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clarity.ipmsbackend.model.entity.IpmsProductInventory;
import com.clarity.ipmsbackend.model.vo.inventory.ProductInventoryQueryVO;
import com.clarity.ipmsbackend.model.vo.inventory.SafeProductInventoryQueryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Clarity
* @description 针对表【ipms_product_inventory(商品库存表)】的数据库操作Mapper
* @createDate 2023-03-13 15:58:53
* @Entity generator.entity.IpmsProductInventory
*/
public interface IpmsProductInventoryMapper extends BaseMapper<IpmsProductInventory> {

    /**
     * 查询商品库存，不支持分页，但是支持更多的字段模糊查询
     *
     * @param fuzzyText
     * @return
     */
    List<ProductInventoryQueryVO> selectProductInventory(@Param("fuzzyText") String fuzzyText);
}




