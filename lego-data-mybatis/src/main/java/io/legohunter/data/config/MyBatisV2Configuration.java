package io.legohunter.data.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.regex.Pattern;

@Configuration
@MapperScan(basePackages = {"io.legohunter.data.mybatis.mapper"})
@EnableTransactionManagement
@Slf4j
public class MyBatisV2Configuration {

    /**
     * MyBatis configuration variable holding the shared pricing schema name. The marketplace pricing
     * mappers qualify every table with {@code ${marketplacePricingSchema}} (HOMELAB-31), so one
     * environment connection reads the shared schema on the same MySQL server.
     */
    public static final String MARKETPLACE_PRICING_SCHEMA_VARIABLE = "marketplacePricingSchema";

    public static final String DEFAULT_MARKETPLACE_PRICING_SCHEMA = "marketplace_pricing";

    private static final Pattern SCHEMA_NAME = Pattern.compile("[A-Za-z0-9_]{1,64}");

    public MyBatisV2Configuration() {
        log.info("MyBatisV2Configuration initialized");
    }

    /**
     * Sets {@value #MARKETPLACE_PRICING_SCHEMA_VARIABLE} from {@code lego.marketplace-pricing.schema}
     * (default {@value #DEFAULT_MARKETPLACE_PRICING_SCHEMA}). The value is pasted into SQL text, so
     * only a plain schema name is accepted. A value already set through
     * {@code mybatis.configuration-properties} is kept.
     */
    @Bean
    public ConfigurationCustomizer marketplacePricingSchemaCustomizer(
            @Value("${lego.marketplace-pricing.schema:" + DEFAULT_MARKETPLACE_PRICING_SCHEMA + "}") String schema) {
        String validated = validateSchemaName(schema);
        return configuration -> {
            if (configuration.getVariables().getProperty(MARKETPLACE_PRICING_SCHEMA_VARIABLE) == null) {
                configuration.getVariables().setProperty(MARKETPLACE_PRICING_SCHEMA_VARIABLE, validated);
            }
            log.info("Marketplace pricing schema [{}]",
                    configuration.getVariables().getProperty(MARKETPLACE_PRICING_SCHEMA_VARIABLE));
        };
    }

    static String validateSchemaName(String schema) {
        if (schema == null || !SCHEMA_NAME.matcher(schema.trim()).matches()) {
            throw new IllegalArgumentException("lego.marketplace-pricing.schema must be a plain schema name, not ["
                    + schema + "]");
        }
        return schema.trim();
    }
}
