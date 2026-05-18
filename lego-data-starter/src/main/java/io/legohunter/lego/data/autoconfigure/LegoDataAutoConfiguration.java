package io.legohunter.lego.data.autoconfigure;

import com.zaxxer.hikari.HikariDataSource;
import io.legohunter.lego.data.builder.DataSourceBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bricklink.data.lego.ibatis.configuration.MybatisV1Configuration;
import io.legohunter.data.config.LegoDataDaoConfiguration;
import io.legohunter.data.config.MyBatisV2Configuration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Auto-configuration for Lego Data.
 * <p>
 * Activated when:
 * - DataSource and HikariDataSource classes are on the classpath
 * - Property 'lego.data.enabled' is present and set to true
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({DataSource.class, HikariDataSource.class})
@Configuration(proxyBeanMethods = false)
@Import({
        MybatisV1Configuration.class,
        MyBatisV2Configuration.class,
        LegoDataDaoConfiguration.class
})
@ConditionalOnProperty(
        name = "lego.data.enabled",
        havingValue = "true"

)
@RequiredArgsConstructor
public class LegoDataAutoConfiguration {

    private final Environment environment;
    private final ConfigurableApplicationContext context;

    @Bean
    @ConfigurationProperties("lego.databases")
    public Map<String, Object> databasesMap() {
        return new HashMap<>();
    }

    @Bean
    public DataSource dataSource(Map<String, Object> databasesMap) {

        String databaseKeyName = environment.getProperty("spring.datasource.database-key-name");
        if (databaseKeyName == null) {
            throw new IllegalStateException("spring.datasource.database-key-name property not set");
        }

        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder();
        return dataSourceBuilder.dataSource(databaseKeyName, databasesMap, "spring.datasource.hikari", environment);
    }
}
