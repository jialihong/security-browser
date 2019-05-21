package com.jiaxh.security.browser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Auther: jiaxh
 * @Date: 2019/5/20 12:04
 */
@SpringBootApplication(scanBasePackages="com.jiaxh")
@EnableSwagger2
@EnableConfigurationProperties
public class BrowserApplication {
    public static void main(String[] args) {

        SpringApplication.run(BrowserApplication.class, args);
    }
}
