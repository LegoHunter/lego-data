package net.bricklink.data.lego.dao;

import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.configuration.BricklinkRestConfiguration;
import com.bricklink.api.rest.configuration.BricklinkRestProperties;
import com.bricklink.api.rest.model.v1.BricklinkResource;
import com.bricklink.api.rest.model.v1.Category;
import com.bricklink.api.rest.model.v1.Item;
import lombok.extern.slf4j.Slf4j;
import net.bricklink.data.lego.dto.BricklinkInventory;
import net.bricklink.data.lego.ibatis.configuration.MybatisV1Configuration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@SpringBootTest(classes = {BricklinkIntegrationTest.MyTestConfiguration.class, BricklinkRestConfiguration.class, BricklinkRestProperties.class, BricklinkInventoryDao.class, MybatisV1Configuration.class})
@EnableAutoConfiguration
@ActiveProfiles("unit-test")
@Slf4j
class BricklinkIntegrationTest {
    @Autowired
    private BricklinkRestClient bricklinkRestClient;

    @Autowired
    private BricklinkInventoryDao bricklinkInventoryDao;

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
    void getAllCategories() {
        List<BricklinkInventory> itemsForSale = bricklinkInventoryDao.getAllForSale();
        itemsForSale.stream().forEach((bi -> {
            BricklinkResource<Item> item = bricklinkRestClient.getCatalogItem("SET", bi.getBlItemNo());
            Integer categoryId = item.getData().getCategory_id();
            log.info("[{}]-[{}]", categoryId, item);
        }));
        BricklinkResource<List<Category>> categories = bricklinkRestClient.getCategories();
        categories.getData()
                  .stream()
                  .filter(c -> c.getCategory_name().toLowerCase().contains("train"))
                  .forEach(c -> {
                      log.info("[{}]", c);
                  });

        BricklinkResource<Category> category = bricklinkRestClient.getCategory(122L);
        categories.getData()
                  .stream()
                  .forEach(c -> {
                      log.info("[{}]", c);
                  });
    }

    @Configuration
    static class MyTestConfiguration {

    }

    // 122, 124
}
