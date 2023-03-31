package com.clarity.ipmsbackend;

import com.clarity.ipmsbackend.mapper.IpmsBomMapper;
import com.clarity.ipmsbackend.mapper.IpmsProductBomMapper;
import com.clarity.ipmsbackend.mapper.IpmsProductInventoryMapper;
import com.clarity.ipmsbackend.model.vo.inventory.ProductInventoryQueryVO;
import com.clarity.ipmsbackend.model.vo.inventory.SafeProductInventoryQueryVO;
import com.clarity.ipmsbackend.service.IpmsProductInventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class IpmsBackendApplicationTests {

    @Resource
    private IpmsBomMapper ipmsBomMapper;

    @Resource
    private IpmsProductBomMapper ipmsProductBomMapper;

    @Resource
    private IpmsProductInventoryService ipmsProductInventoryService;

    @Resource
    private IpmsProductInventoryMapper ipmsProductInventoryMapper;

    @Test
    void contextLoads() {
        List<ProductInventoryQueryVO> productInventoryQueryVOList = ipmsProductInventoryMapper.selectProductInventory("");
        System.out.println(productInventoryQueryVOList);
//         String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + "123456789").getBytes());
//         System.out.println("encryptPassword = " + encryptPassword);
//         System.out.println(IpmsBackendApplicationTests.literallyCode("SCTL000201"));
//        List<Long> longList = new ArrayList<>();
//        longList.add(1L);
//        longList.add(1L);
//        longList.add(1L);
//        longList.add(1L);
//        longList.add(1L);
//        longList.add(1L);
//        List<Long> distinctLongList = longList.stream().distinct().collect(Collectors.toList());
//        System.out.println(longList);
//        System.out.println(distinctLongList);
//        List<SafeForwardQueryBomVO> bomFatherProduct = ipmsBomMapper.getBomFatherProduct("BOM00001");
//        System.out.println(bomFatherProduct);
//        List<IpmsProductBom> bomSubComponentMessage = ipmsProductBomMapper.getBomSubComponentMessage("BOM00001");
//        System.out.println(bomSubComponentMessage);
//        List<SafeForwardQueryBomVO> bomOneLevelProduct = ipmsBomMapper.getBomOneLevelProduct(32L);
//        System.out.println(bomOneLevelProduct);
//        System.out.println(IpmsBackendApplicationTests.literallyCode("SCDD", "20230312", 1));
//        QueryWrapper<IpmsProductInventory> productInventoryQueryWrapper = new QueryWrapper<>();
//        productInventoryQueryWrapper.eq("warehouse_position_id", 1L);
//        productInventoryQueryWrapper.eq("warehouse_id", 1L);
//        productInventoryQueryWrapper.eq("product_id", 1L);
//        IpmsProductInventory one = ipmsProductInventoryService.getOne(productInventoryQueryWrapper);
//        System.out.println(one);
    }

    public static String literallyCode(String prefix, String date, int number) {
        String numberStr = String.format("%05d", number);
        return prefix + "-" + date + "-" + numberStr;
    }

    public static String literallyCode(String codeStr) {
        StringBuilder englishCode = new StringBuilder();
        StringBuilder arabCode = new StringBuilder();
        StringBuilder tempCode = new StringBuilder();
        StringBuilder allZeroStr = new StringBuilder();
        char[] codeChars = codeStr.toCharArray();
        for (int i = 0; i < codeChars.length; i++) {
            if (Character.isUpperCase(codeChars[i])) {
                englishCode.append(codeChars[i]);
            } else {
                if (codeChars[i] == '0') {
                    arabCode.append(codeChars[i]);
                    allZeroStr.append(codeChars[i]);
                } else {
                    tempCode.append(codeChars[i]);
                    if (i == codeChars.length - 1) {
                        int lastCodeNum = Integer.parseInt(tempCode.toString());
                        lastCodeNum++;
                        tempCode = new StringBuilder(String.valueOf(lastCodeNum));
                        arabCode.append(tempCode);
                    }
                }
            }
        }
        // 尾部数据全零判断
        if (allZeroStr.toString().equals(arabCode.toString())) {
            arabCode.delete(0,1);
            arabCode.append(1);
        }
        return englishCode + arabCode.toString();
    }
}
