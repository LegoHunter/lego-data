package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.config.MyBatisV2Configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableAutoConfiguration
@Import(MyBatisV2Configuration.class)
@PropertySource("application.properties")
class MapperTestConfiguration {
}
