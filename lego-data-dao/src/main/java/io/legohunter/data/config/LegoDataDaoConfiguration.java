package io.legohunter.data.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "io.legohunter.data.dao",
        "io.legohunter.data.validation"
})
@Slf4j
public class LegoDataDaoConfiguration {
    public LegoDataDaoConfiguration() {
        log.info("LegoDataDaoConfiguration initialized");
    }
}
