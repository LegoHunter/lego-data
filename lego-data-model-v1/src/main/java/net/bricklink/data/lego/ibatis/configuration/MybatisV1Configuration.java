package net.bricklink.data.lego.ibatis.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ConditionalOnMissingBean(type = "liquibase")
@MapperScan(basePackages = {"net.bricklink.data.lego.ibatis.mapper", "net.lego.data.v1.mybatis.mapper"})
@EnableTransactionManagement
public class MybatisV1Configuration {
}
