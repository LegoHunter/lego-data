package net.bricklink.data.lego.dao;

import lombok.extern.slf4j.Slf4j;
import net.bricklink.data.lego.dto.BricklinkSaleItem;
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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({BricklinkSaleItemDao.class, MybatisV1Configuration.class})
@Slf4j
class BricklinkSaleItemTest {
    @Autowired
    private BricklinkSaleItemDao bricklinkSaleItemDao;

    @Test
    @Sql(scripts = {"/scripts/db/h2/bricklink_sale_item_schema.ddl",
            "/scripts/db/h2/truncate-tables.sql",
            "/scripts/db/h2/bricklink_sale_item_data-01.sql"})
    void updateBricklinkSaleItemSold() {
        List<Integer> currentlyForSaleInventoryIds = new ArrayList<>();
        currentlyForSaleInventoryIds.add(101);
        currentlyForSaleInventoryIds.add(103);
        currentlyForSaleInventoryIds.add(104);
        bricklinkSaleItemDao.updateBricklinkSaleItemSold(5082L, "U", currentlyForSaleInventoryIds);
        List<BricklinkSaleItem> saleItems = bricklinkSaleItemDao.getAll();
        assertThat(saleItems).hasSize(5);
        saleItems.forEach(s -> {
            if (s.getInventoryId().equals(101)) {
                assertThat(s.getStatus()).isEqualTo("C");
            }
            if (s.getInventoryId().equals(102)) {
                assertThat(s.getStatus()).isEqualTo("S");
            }
            if (s.getInventoryId().equals(103)) {
                assertThat(s.getStatus()).isEqualTo("C");
            }
            if (s.getInventoryId().equals(104)) {
                assertThat(s.getStatus()).isEqualTo("C");
            }
            if (s.getInventoryId().equals(105)) {
                assertThat(s.getStatus()).isEqualTo("S");
            }
        });
    }

    @Test
    @Sql(scripts = {"/scripts/db/h2/condition_schema.ddl",
            "/scripts/db/h2/item_schema.ddl",
            "/scripts/db/h2/bricklink_item_schema.ddl",
            "/scripts/db/h2/bricklink_inventory_schema.ddl",
            "/scripts/db/h2/bricklink_sale_item_schema.ddl",
            "/scripts/db/h2/truncate-tables.sql",
            "/scripts/db/h2/item_data-03.sql",
            "/scripts/db/h2/bricklink_item_data-03.sql",
            "/scripts/db/h2/bricklink_inventory_data-03.sql",
            "/scripts/db/h2/bricklink_sale_item_data-02.sql"})
    void getPricesForItem_excludesPersonalInventoryItems() {
        List<BricklinkSaleItem> saleItems = bricklinkSaleItemDao.getBrinklinkSaleItems(5186L, "U", "C");
        assertThat(saleItems.size()).isEqualTo(26);
        saleItems.forEach(s -> {
            log.info("[{}]", s);
            assertThat(s.getInventoryId()).isNotEqualTo(125587580);
        });
        Double[] prices = saleItems.stream().map(BricklinkSaleItem::getUnitPrice).toArray(Double[]::new);
        log.info("prices [{}]", prices.length);
        for (Double d : prices) {
            log.info("price [{}]", d);
        }
    }


    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class DaoConfiguration {
    }
}
