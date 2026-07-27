package io.legohunter.data.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CostTypeTest {

    @Test
    void equalityUsesCostTypeCodeOnly() {
        CostType price = CostType.builder()
                .costTypeCode("PRICE")
                .costTypeName("Price")
                .costTypeDescription("Original item price")
                .build();
        CostType renamedPrice = CostType.builder()
                .costTypeCode("PRICE")
                .costTypeName("Updated Price")
                .costTypeDescription("Updated description")
                .build();
        CostType shipping = CostType.builder()
                .costTypeCode("SHIPPING")
                .costTypeName("Shipping")
                .costTypeDescription("Shipping fee")
                .build();

        assertThat(price)
                .isEqualTo(renamedPrice)
                .isNotEqualTo(shipping);
        assertThat(price.hashCode()).isEqualTo(renamedPrice.hashCode());
    }
}
