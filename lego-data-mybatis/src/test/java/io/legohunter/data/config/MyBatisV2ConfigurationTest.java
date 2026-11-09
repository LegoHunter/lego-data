package io.legohunter.data.config;

import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MyBatisV2ConfigurationTest {

    private final MyBatisV2Configuration myBatisV2Configuration = new MyBatisV2Configuration();

    @Test
    void setsTheMarketplacePricingSchemaVariable() {
        Configuration configuration = new Configuration();

        myBatisV2Configuration.marketplacePricingSchemaCustomizer(" marketplace_pricing_scratch ").customize(configuration);

        assertThat(configuration.getVariables().getProperty(MyBatisV2Configuration.MARKETPLACE_PRICING_SCHEMA_VARIABLE))
                .isEqualTo("marketplace_pricing_scratch");
    }

    @Test
    void keepsAValueSetThroughConfigurationProperties() {
        Configuration configuration = new Configuration();
        configuration.getVariables().setProperty(MyBatisV2Configuration.MARKETPLACE_PRICING_SCHEMA_VARIABLE, "already_set");

        myBatisV2Configuration.marketplacePricingSchemaCustomizer("marketplace_pricing").customize(configuration);

        assertThat(configuration.getVariables().getProperty(MyBatisV2Configuration.MARKETPLACE_PRICING_SCHEMA_VARIABLE))
                .isEqualTo("already_set");
    }

    @Test
    void refusesAnythingButAPlainSchemaName() {
        assertThatThrownBy(() -> myBatisV2Configuration.marketplacePricingSchemaCustomizer("x; DROP TABLE y"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> MyBatisV2Configuration.validateSchemaName("`marketplace_pricing`"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> MyBatisV2Configuration.validateSchemaName(null)).isInstanceOf(IllegalArgumentException.class);
        assertThat(MyBatisV2Configuration.validateSchemaName("marketplace_pricing")).isEqualTo("marketplace_pricing");
    }
}
