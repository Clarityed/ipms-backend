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
    }

}
