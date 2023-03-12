package com.clarity.ipmsbackend;

import com.clarity.ipmsbackend.mapper.IpmsBomMapper;
import com.clarity.ipmsbackend.mapper.IpmsProductBomMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
        System.out.println(IpmsBackendApplicationTests.literallyCode("SCDD", "20230312", 1));
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
