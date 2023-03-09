package com.clarity.ipmsbackend;

import com.clarity.ipmsbackend.mapper.IpmsBomMapper;
import com.clarity.ipmsbackend.mapper.IpmsProductBomMapper;
import com.clarity.ipmsbackend.model.entity.IpmsProductBom;
import com.clarity.ipmsbackend.model.vo.bom.SafeForwardQueryBomVO;
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

    @Test
    void contextLoads() {
        // String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + "123456789").getBytes());
        // System.out.println("encryptPassword = " + encryptPassword);
        // System.out.println(IpmsBackendApplicationTests.literallyCode());
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
        List<SafeForwardQueryBomVO> bomFatherProduct = ipmsBomMapper.getBomFatherProduct("BOM00001");
        System.out.println(bomFatherProduct);
        List<IpmsProductBom> bomSubComponentMessage = ipmsProductBomMapper.getBomSubComponentMessage("BOM00001");
        System.out.println(bomSubComponentMessage);
        List<SafeForwardQueryBomVO> bomOneLevelProduct = ipmsBomMapper.getBomOneLevelProduct(32L);
        System.out.println(bomOneLevelProduct);
    }

    public static String literallyCode() {
        String codeStr = "BM01111";
        StringBuffer englishCode = new StringBuffer();
        StringBuffer arabCode = new StringBuffer();
        String tempCode = "";
        char[] codeChars = codeStr.toCharArray();
        for (int i = 0; i < codeChars.length; i++) {
            if (Character.isUpperCase(codeChars[i])) {
                englishCode.append(codeChars[i]);
            } else {
                if (codeChars[i] == '0') {
                    arabCode.append(codeChars[i]);
                } else {
                    tempCode = tempCode + codeChars[i];
                    if (i == codeChars.length - 1) {
                        int lastCodeNum = Integer.parseInt(tempCode);
                        lastCodeNum++;
                        tempCode = String.valueOf(lastCodeNum);
                        arabCode.append(tempCode);
                    }
                }
            }
        }
        System.out.println(tempCode);
        String result = englishCode + arabCode.toString();
        return result;
    }
}
