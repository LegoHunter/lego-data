package io.legohunter.lego.data.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "lego")
public class DatabaseProperties {
    private Map<String, Database> databases = new HashMap<>();

    @lombok.Data
    public static class Database {
        private String url;
        private String username;
        private String password;
        private String driverClassName;
    }

    @lombok.Data
    public static class Data {
        private boolean enabled;
    }
}
