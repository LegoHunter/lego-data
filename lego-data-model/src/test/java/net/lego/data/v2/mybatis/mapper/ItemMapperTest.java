package net.lego.data.v2.mybatis.mapper;

import lombok.extern.slf4j.Slf4j;
import net.lego.data.config.MyBatisV2Configuration;
import net.lego.data.v2.dto.Item;
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


@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class})
@Slf4j
class ItemMapperTest {

    @Autowired
    ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/item_schema_v2.ddl", "/scripts/db/h2/truncate-table-item.sql"})
    void findAll() {
        itemMapper.findAll();
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/item_schema_v2.ddl", "/scripts/db/h2/truncate-table-item.sql"})
    void findByItemId() {
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/item_schema_v2.ddl", "/scripts/db/h2/truncate-table-item.sql"})
    void findByItemNumber() {
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/item_schema_v2.ddl", "/scripts/db/h2/truncate-table-item.sql"})
    void insert() {
        Item item = Item.builder()
                .itemName("Item 1")
                .isObsolete(false)
                .itemNumber("1234")
                .notes("These are notes")
                .build();
        itemMapper.insert(item);

        itemMapper.findAll()
                .forEach(itemDto -> {
                    log.info("{}", itemDto);
                });
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/item_schema_v2.ddl", "/scripts/db/h2/truncate-table-item.sql"})
    void migrate() {
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/item_schema_v2.ddl", "/scripts/db/h2/truncate-table-item.sql"})
    void update() {
        Item item = Item.builder()
                .itemName("Item 1")
                .isObsolete(false)
                .itemNumber("1234")
                .notes("These are notes")
                .build();
        itemMapper.insert(item);
        itemMapper.findAll()
                .forEach(itemDto -> {
                    log.info("{}", itemDto);
                });

        Item item2 = Item.builder()
                .itemName("Item 1")
                .isObsolete(true)
                .itemNumber("1234")
                .notes("These are notes that are updated")
                .build();
        itemMapper.update(item2);
        itemMapper.findAll()
                .forEach(itemDto -> {
                    log.info("{}", itemDto);
                });
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class DaoConfiguration {
    }
}