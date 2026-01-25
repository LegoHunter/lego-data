package net.bricklink.data.lego.dao;

import lombok.extern.slf4j.Slf4j;
import net.bricklink.data.lego.dto.BricklinkInventory;
import net.bricklink.data.lego.ibatis.configuration.MybatisV1Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({BricklinkInventoryDao.class, MybatisV1Configuration.class})
@Slf4j
class BricklinkInventoryTest {

    @Autowired
    BricklinkInventoryDao bricklinkInventoryDao;

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class DaoConfiguration {
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/condition_schema.ddl",
            "/scripts/db/h2/item_schema.ddl",
            "/scripts/db/h2/bricklink_item_schema.ddl",
            "/scripts/db/h2/bricklink_inventory_schema.ddl",
            "/scripts/db/h2/truncate-tables.sql",
            "/scripts/db/h2/item_data-01.sql",
            "/scripts/db/h2/bricklink_item_data-01.sql",
            "/scripts/db/h2/bricklink_inventory_data-01.sql"})
    void updateFromImageKeywords() {
        List<BricklinkInventory> bricklinkInventoryList = bricklinkInventoryDao.getAll();
        assertThat(bricklinkInventoryList).hasSize(1);
        bricklinkInventoryList.forEach(bi -> log.info("[{}]", bi));
        assertThat(bricklinkInventoryList.get(0).getBuiltOnce()).isFalse();
        assertThat(bricklinkInventoryList.get(0).getBoxConditionId()).isNull();
        assertThat(bricklinkInventoryList.get(0).getInstructionsConditionCode()).isNull();

        // Test update of all three updateable fields from image keywords
        BricklinkInventory bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setUuid(bricklinkInventoryList.get(0).getUuid());
        bricklinkInventory.setBlItemNo(bricklinkInventoryList.get(0).getBlItemNo());
        bricklinkInventory.setBuiltOnce(true);
        bricklinkInventory.setBoxConditionCode("M");
        bricklinkInventory.setInstructionsConditionCode("g");
        bricklinkInventoryDao.updateFromImageKeywords(bricklinkInventory);

        bricklinkInventoryList = bricklinkInventoryDao.getAll();
        assertThat(bricklinkInventoryList).hasSize(1);
        bricklinkInventoryList.forEach(bi -> log.info("[{}]", bi));
        assertThat(bricklinkInventoryList.get(0).getBuiltOnce()).isTrue();
        assertThat(bricklinkInventoryList.get(0).getBoxConditionId()).isEqualTo(1);
        assertThat(bricklinkInventoryList.get(0).getInstructionsConditionId()).isEqualTo(4);

        // Test update of each individual updateable field from image keywords
        // built_once
        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setUuid(bricklinkInventoryList.get(0).getUuid());
        bricklinkInventory.setBlItemNo(bricklinkInventoryList.get(0).getBlItemNo());
        bricklinkInventory.setBuiltOnce(false);
        bricklinkInventoryDao.updateFromImageKeywords(bricklinkInventory);

        bricklinkInventoryList = bricklinkInventoryDao.getAll();
        assertThat(bricklinkInventoryList).hasSize(1);
        bricklinkInventoryList.forEach(bi -> log.info("[{}]", bi));
        assertThat(bricklinkInventoryList.get(0).getBuiltOnce()).isFalse();
        assertThat(bricklinkInventoryList.get(0).getBoxConditionId()).isEqualTo(1);
        assertThat(bricklinkInventoryList.get(0).getInstructionsConditionId()).isEqualTo(4);

        // box_condition_id
        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setUuid(bricklinkInventoryList.get(0).getUuid());
        bricklinkInventory.setBlItemNo(bricklinkInventoryList.get(0).getBlItemNo());
        bricklinkInventory.setBoxConditionCode("vg");
        bricklinkInventoryDao.updateFromImageKeywords(bricklinkInventory);

        bricklinkInventoryList = bricklinkInventoryDao.getAll();
        assertThat(bricklinkInventoryList).hasSize(1);
        bricklinkInventoryList.forEach(bi -> log.info("[{}]", bi));
        assertThat(bricklinkInventoryList.get(0).getBuiltOnce()).isFalse();
        assertThat(bricklinkInventoryList.get(0).getBoxConditionId()).isEqualTo(3);
        assertThat(bricklinkInventoryList.get(0).getInstructionsConditionId()).isEqualTo(4);

        // instructions_condition_id
        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setUuid(bricklinkInventoryList.get(0).getUuid());
        bricklinkInventory.setBlItemNo(bricklinkInventoryList.get(0).getBlItemNo());
        bricklinkInventory.setInstructionsConditionCode("Ms");
        bricklinkInventoryDao.updateFromImageKeywords(bricklinkInventory);

        bricklinkInventoryList = bricklinkInventoryDao.getAll();
        assertThat(bricklinkInventoryList).hasSize(1);
        bricklinkInventoryList.forEach(bi -> log.info("[{}]", bi));
        assertThat(bricklinkInventoryList.get(0).getBuiltOnce()).isFalse();
        assertThat(bricklinkInventoryList.get(0).getBoxConditionId()).isEqualTo(3);
        assertThat(bricklinkInventoryList.get(0).getInstructionsConditionId()).isEqualTo(8);

        // test setting only some of the columns that can be updated/
        // box_condition_id and built_once
        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setUuid(bricklinkInventoryList.get(0).getUuid());
        bricklinkInventory.setBlItemNo(bricklinkInventoryList.get(0).getBlItemNo());
        bricklinkInventory.setBuiltOnce(true);
        bricklinkInventory.setBoxConditionCode("F");
        bricklinkInventoryDao.updateFromImageKeywords(bricklinkInventory);

        bricklinkInventoryList = bricklinkInventoryDao.getAll();
        assertThat(bricklinkInventoryList).hasSize(1);
        bricklinkInventoryList.forEach(bi -> log.info("[{}]", bi));
        assertThat(bricklinkInventoryList.get(0).getBuiltOnce()).isTrue();
        assertThat(bricklinkInventoryList.get(0).getBoxConditionId()).isEqualTo(7);
        assertThat(bricklinkInventoryList.get(0).getInstructionsConditionId()).isEqualTo(8);
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/condition_schema.ddl",
            "/scripts/db/h2/item_schema.ddl",
            "/scripts/db/h2/bricklink_item_schema.ddl",
            "/scripts/db/h2/bricklink_inventory_schema.ddl",
            "/scripts/db/h2/bricklink_sale_item_schema.ddl",
            "/scripts/db/h2/truncate-tables.sql",
            "/scripts/db/h2/item_data-02.sql",
            "/scripts/db/h2/bricklink_item_data-02.sql",
            "/scripts/db/h2/bricklink_inventory_data-02.sql"})
    void getAllForSale() {
        List<BricklinkInventory> bricklinkInventoryList = bricklinkInventoryDao.getAllForSale();
        bricklinkInventoryList.stream().forEach(bi -> {
            log.info("[{}]", bi);
        });
    }

    @Test
    void canBeAvailableForSale() {
        BricklinkInventory bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setForSale(true);
        bricklinkInventory.setInventoryId(1L);
        bricklinkInventory.setInstructionsConditionId(1);
        bricklinkInventory.setBoxConditionId(1);
        bricklinkInventory.setUnitPrice(1.00);
        bricklinkInventory.setOrderId(null);
        assertThat(bricklinkInventory.canBeAvailableForSale()).isTrue();

        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setForSale(true);
        bricklinkInventory.setInventoryId(1L);
        bricklinkInventory.setInstructionsConditionId(1);
        bricklinkInventory.setBoxConditionId(1);
        bricklinkInventory.setUnitPrice(0.00);
        bricklinkInventory.setOrderId(null);
        assertThat(bricklinkInventory.canBeAvailableForSale()).isFalse();

        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setForSale(true);
        bricklinkInventory.setInventoryId(1L);
        bricklinkInventory.setInstructionsConditionId(1);
        bricklinkInventory.setBoxConditionId(1);
        bricklinkInventory.setUnitPrice(null);
        bricklinkInventory.setOrderId(null);
        assertThat(bricklinkInventory.canBeAvailableForSale()).isFalse();

        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setForSale(false);
        bricklinkInventory.setInventoryId(1L);
        bricklinkInventory.setInstructionsConditionId(null);
        bricklinkInventory.setBoxConditionId(null);
        bricklinkInventory.setOrderId(null);
        assertThat(bricklinkInventory.canBeAvailableForSale()).isFalse();

        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setForSale(true);
        bricklinkInventory.setInventoryId(null);
        bricklinkInventory.setInstructionsConditionId(null);
        bricklinkInventory.setBoxConditionId(null);
        bricklinkInventory.setOrderId(null);
        assertThat(bricklinkInventory.canBeAvailableForSale()).isFalse();

        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setForSale(false);
        bricklinkInventory.setInventoryId(null);
        bricklinkInventory.setInstructionsConditionId(null);
        bricklinkInventory.setBoxConditionId(null);
        bricklinkInventory.setOrderId(null);
        assertThat(bricklinkInventory.canBeAvailableForSale()).isFalse();

        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setForSale(true);
        bricklinkInventory.setInventoryId(1L);
        bricklinkInventory.setInstructionsConditionId(1);
        bricklinkInventory.setBoxConditionId(null);
        bricklinkInventory.setOrderId(null);
        assertThat(bricklinkInventory.canBeAvailableForSale()).isFalse();

        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setForSale(true);
        bricklinkInventory.setInventoryId(1L);
        bricklinkInventory.setInstructionsConditionId(null);
        bricklinkInventory.setBoxConditionId(1);
        bricklinkInventory.setOrderId(null);
        assertThat(bricklinkInventory.canBeAvailableForSale()).isFalse();

        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setForSale(true);
        bricklinkInventory.setInventoryId(1L);
        bricklinkInventory.setInstructionsConditionId(1);
        bricklinkInventory.setBoxConditionId(1);
        bricklinkInventory.setUnitPrice(1.00);
        bricklinkInventory.setOrderId("12345678");
        assertThat(bricklinkInventory.canBeAvailableForSale()).isFalse();

        bricklinkInventory = new BricklinkInventory();
        bricklinkInventory.setForSale(true);
        bricklinkInventory.setInventoryId(1L);
        bricklinkInventory.setInstructionsConditionId(1);
        bricklinkInventory.setBoxConditionId(1);
        bricklinkInventory.setUnitPrice(1.00);
        bricklinkInventory.setOrderId(null);
        assertThat(bricklinkInventory.canBeAvailableForSale()).isTrue();
    }
}
