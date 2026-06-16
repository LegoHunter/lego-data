package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.ItemInventorySaleIntent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class ItemInventorySaleIntentMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindByCodeFindAllUpsertAndDelete() {
        ItemInventorySaleIntent saleIntent = ItemInventorySaleIntent.builder()
                .saleIntentCode("SELLABLE")
                .saleIntentName("Sellable")
                .saleIntentDescription("Eligible for marketplace listing")
                .active(true)
                .sortOrder(10)
                .build();

        itemInventorySaleIntentMapper.insert(saleIntent);
        saleIntent.setSaleIntentDescription("Eligible for automated marketplace listing");
        itemInventorySaleIntentMapper.update(saleIntent);

        assertThat(itemInventorySaleIntentMapper.findBySaleIntentCode("SELLABLE"))
                .hasValueSatisfying(found -> assertThat(found.getSaleIntentDescription()).isEqualTo("Eligible for automated marketplace listing"));
        assertThat(itemInventorySaleIntentMapper.findAll()).extracting(ItemInventorySaleIntent::getSaleIntentCode).containsExactly("SELLABLE");

        saleIntent.setSaleIntentName("List for sale");
        itemInventorySaleIntentMapper.upsert(saleIntent);
        assertThat(itemInventorySaleIntentMapper.findBySaleIntentCode("SELLABLE"))
                .hasValueSatisfying(found -> assertThat(found.getSaleIntentName()).isEqualTo("List for sale"));
        assertThat(itemInventorySaleIntentMapper.delete("SELLABLE")).isOne();
    }
}
