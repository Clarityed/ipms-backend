package com.clarity.ipmsbackend.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Knife4j 接口文档配置
 *
 * @author: clarity
 * @date: 2023年02月20日 10:17
 */

@Configuration
@EnableSwagger2
@EnableKnife4j
@Profile("dev")
public class Knife4jConfig {

    @Bean
    public Docket dockerBean() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.clarity.ipmsbackend.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .description("智能生成制造后端接口文档")
                .contact(new Contact("Clarity", "https://github.com/Clarityed", "2270893459@qq.com"))
                .version("v1.0.0")
                .title("ipms-backend")
                .build();
    }
//    @Bean
//    public Docket dockerBean() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(new ApiInfoBuilder()
//                        .title("ipms-backend")
//                        .description("智能生成制造后端接口文档")
//                        .version("1.0")
//                        .build())
//                .select()
//                // 指定 Controller 扫描包路径
//                .apis(RequestHandlerSelectors.basePackage("com.clarity.ipmsbackend.controller"))
//                .paths(PathSelectors.any())
//                .build();
//    }
}
