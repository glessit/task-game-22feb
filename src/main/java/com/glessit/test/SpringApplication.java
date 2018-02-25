package com.glessit.test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.glessit.test"})
@EnableScheduling
public class SpringApplication {
    public static void main(String[] args) throws Exception {
        org.springframework.boot.SpringApplication.run(SpringApplication.class);
    }
}
