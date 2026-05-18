package io.legohunter.data.mybatis.mapper;

import lombok.extern.slf4j.Slf4j;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.ExternalItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static io.legohunter.data.dto.ExternalService.ExternalServiceType.BRICKLINK;


@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class})
@Slf4j
class ExternalItemMapperTest {

    @Autowired
    ExternalItemMapper externalItemMapper;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/external_item_schema.ddl", "/scripts/db/h2/truncate-table-external-item.sql", "/scripts/db/h2/external_item_data.sql"})
    void findAll() {
        Set<ExternalItem> externalItems = externalItemMapper.findAllByExternalService(BRICKLINK.name());
        log.info("Found {} external items :: {}", externalItems.size(), externalItems);
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/external_item_schema.ddl", "/scripts/db/h2/truncate-table-external-item.sql"})
    void findByItemId() {
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/external_item_schema.ddl", "/scripts/db/h2/truncate-table-external-item.sql"})
    void findByItemNumber() {
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/external_item_schema.ddl", "/scripts/db/h2/truncate-table-external-item.sql"})
    void insert() {
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/external_item_schema.ddl", "/scripts/db/h2/truncate-table-external-item.sql"})
    void migrate() {
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/external_item_schema.ddl", "/scripts/db/h2/truncate-table-external-item.sql"})
    void update() {
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class DaoConfiguration {
    }
}