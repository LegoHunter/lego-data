package io.legohunter.data.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan(basePackages = {"io.legohunter.data.mybatis.mapper"})
@EnableTransactionManagement
@Slf4j
public class MyBatisV2Configuration {
    public MyBatisV2Configuration() {
        log.info("MyBatisV2Configuration initialized");
    }
}