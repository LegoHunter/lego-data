package io.legohunter.data.mybatis.mapper;

import lombok.extern.slf4j.Slf4j;
import io.legohunter.data.config.MyBatisV2Configuration;
import io.legohunter.data.dto.ItemInventory;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({MyBatisV2Configuration.class})
@Slf4j
class ItemInventoryMapperTest {

    @Autowired
    ItemInventoryMapper itemInventoryMapper;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory.sql"
    })
    void findAll() {

        ItemInventory itemInventory1 = buildItemInventory();
        ItemInventory itemInventory2 = buildItemInventory();

        itemInventoryMapper.insert(itemInventory1);
        itemInventoryMapper.insert(itemInventory2);

        assertThat(itemInventoryMapper.findAll())
                .hasSize(2)
                .extracting(ItemInventory::getUuid)
                .containsExactlyInAnyOrder(
                        itemInventory1.getUuid(),
                        itemInventory2.getUuid()
                );
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory.sql"
    })
    void findByItemInventoryId() {

        ItemInventory itemInventory = buildItemInventory();

        itemInventoryMapper.insert(itemInventory);

        Optional<ItemInventory> result =
                itemInventoryMapper.findByItemInventoryId(
                        itemInventory.getItemInventoryId()
                );

        assertThat(result)
                .isPresent()
                .get()
                .extracting(
                        ItemInventory::getUuid,
                        ItemInventory::getDescription
                )
                .containsExactly(
                        itemInventory.getUuid(),
                        itemInventory.getDescription()
                );
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory.sql"
    })
    void findByItemInventoryId_returnsEmpty_whenNotFound() {

        Optional<ItemInventory> result =
                itemInventoryMapper.findByItemInventoryId(999);

        assertThat(result).isEmpty();
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory.sql"
    })
    void findByUuid() {

        ItemInventory itemInventory = buildItemInventory();

        itemInventoryMapper.insert(itemInventory);

        Optional<ItemInventory> result =
                itemInventoryMapper.findByUuid(itemInventory.getUuid());

        assertThat(result)
                .isPresent()
                .get()
                .extracting(
                        ItemInventory::getUuid,
                        ItemInventory::getBoxNumber,
                        ItemInventory::getDescription
                )
                .containsExactly(
                        itemInventory.getUuid(),
                        itemInventory.getBoxNumber(),
                        itemInventory.getDescription()
                );
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory.sql"
    })
    void findByUuid_returnsEmpty_whenNotFound() {

        Optional<ItemInventory> result =
                itemInventoryMapper.findByUuid(UUID.randomUUID().toString());

        assertThat(result).isEmpty();
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory.sql"
    })
    void insert() {

        ItemInventory itemInventory = buildItemInventory();

        itemInventoryMapper.insert(itemInventory);

        assertThat(itemInventory.getItemInventoryId()).isNotNull();

        Optional<ItemInventory> result =
                itemInventoryMapper.findByItemInventoryId(
                        itemInventory.getItemInventoryId()
                );

        assertThat(result)
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getUuid())
                            .isEqualTo(itemInventory.getUuid());

                    assertThat(found.getDescription())
                            .isEqualTo(itemInventory.getDescription());

                    assertThat(found.getActive()).isTrue();
                    assertThat(found.getForSale()).isFalse();
                    assertThat(found.getSealed()).isTrue();
                    assertThat(found.getBuiltOnce()).isFalse();
                });
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory.sql"
    })
    void update() {

        ItemInventory itemInventory = buildItemInventory();

        itemInventoryMapper.insert(itemInventory);

        itemInventory.setDescription("Updated Description");
        itemInventory.setForSale(true);
        itemInventory.setBuiltOnce(true);
        itemInventory.setSealed(false);

        int updated = itemInventoryMapper.update(itemInventory);

        assertThat(updated).isEqualTo(1);

        Optional<ItemInventory> result =
                itemInventoryMapper.findByItemInventoryId(
                        itemInventory.getItemInventoryId()
                );

        assertThat(result)
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getDescription())
                            .isEqualTo("Updated Description");

                    assertThat(found.getForSale()).isTrue();

                    assertThat(found.getBuiltOnce()).isTrue();

                    assertThat(found.getSealed()).isFalse();
                });
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory.sql"
    })
    void update_returnsZero_whenItemDoesNotExist() {

        ItemInventory itemInventory = buildItemInventory();
        itemInventory.setItemInventoryId(999);

        int updated = itemInventoryMapper.update(itemInventory);

        assertThat(updated).isZero();
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory.sql"
    })
    void upsert_inserts_whenUuidDoesNotExist() {

        ItemInventory itemInventory = buildItemInventory();

        itemInventoryMapper.upsert(itemInventory);

        assertThat(itemInventory.getItemInventoryId()).isNotNull();

        Optional<ItemInventory> result =
                itemInventoryMapper.findByUuid(itemInventory.getUuid());

        assertThat(result)
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getDescription())
                            .isEqualTo(itemInventory.getDescription());

                    assertThat(found.getBoxNumber())
                            .isEqualTo(itemInventory.getBoxNumber());
                });
    }

    @Test
    @Sql(scripts = {
            "/scripts/db/h2/item_inventory_schema.ddl",
            "/scripts/db/h2/truncate-table-item-inventory.sql"
    })
    void upsert_updates_whenUuidExists() {

        ItemInventory itemInventory = buildItemInventory();

        itemInventoryMapper.insert(itemInventory);

        itemInventory.setDescription("UPSERT UPDATED");
        itemInventory.setForSale(true);
        itemInventory.setBuiltOnce(true);

        itemInventoryMapper.upsert(itemInventory);

        Optional<ItemInventory> result =
                itemInventoryMapper.findByUuid(itemInventory.getUuid());

        assertThat(result)
                .isPresent()
                .get()
                .satisfies(found -> {

                    assertThat(found.getDescription())
                            .isEqualTo("UPSERT UPDATED");

                    assertThat(found.getForSale()).isTrue();

                    assertThat(found.getBuiltOnce()).isTrue();
                });
    }

    private ItemInventory buildItemInventory() {

        ItemInventory itemInventory = new ItemInventory();
                itemInventory.setUuid(UUID.randomUUID().toString());
                itemInventory.setBoxNumber(17);
                itemInventory.setDescription("Test Description");
                itemInventory.setActive(true);
                itemInventory.setForSale(false);
                itemInventory.setNewOrUsed("USED");
                itemInventory.setCompleteness("COMPLETE");
                itemInventory.setItemConditionId(1);
                itemInventory.setBoxConditionId(2);
                itemInventory.setInstructionsConditionId(3);
                itemInventory.setSealed(true);
                itemInventory.setBuiltOnce(false);
        return itemInventory;
    }

    @EnableAutoConfiguration
    @Configuration
    @PropertySource("application.properties")
    static class DaoConfiguration {
    }
}