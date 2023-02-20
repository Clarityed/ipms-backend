package com.clarity.ipmsbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.clarity.ipmsbackend.mapper")
public class IpmsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IpmsBackendApplication.class, args);
    }

}
