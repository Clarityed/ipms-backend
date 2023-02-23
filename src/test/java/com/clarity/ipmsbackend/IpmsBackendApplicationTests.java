package com.clarity.ipmsbackend;

import com.clarity.ipmsbackend.constant.UserConstant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
class IpmsBackendApplicationTests {

    @Test
    void contextLoads() {
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + "123456789").getBytes());
        System.out.println("encryptPassword = " + encryptPassword);
        System.out.println(IpmsBackendApplicationTests.literallyCode());
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
