package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Carrier;
import io.legohunter.data.dto.Condition;
import io.legohunter.data.dto.CostType;
import io.legohunter.data.dto.ExternalCategory;
import io.legohunter.data.dto.ExternalService;
import io.legohunter.data.dto.ExternalServiceCapability;
import io.legohunter.data.dto.ExternalServiceType;
import io.legohunter.data.dto.PaymentPlatform;
import io.legohunter.data.dto.TransactionPlatform;
import io.legohunter.data.dto.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfiguration.class)
@Sql(scripts = {
        "/scripts/db/h2/current_schema.ddl",
        "/scripts/db/h2/current_lookup_data.sql"
})
class CurrentLookupDataTest extends MapperTestSupport {

    @Test
    void loadsSandboxLookupReferenceData() {
        assertThat(carrierMapper.findAll()).extracting(Carrier::getCarrierCode)
                .containsExactlyInAnyOrder("DHL", "FEDEX", "UPS", "USPS");
        assertThat(conditionMapper.findAll()).extracting(Condition::getConditionCode)
                .containsExactlyInAnyOrder("M", "E", "VG", "G", "P", "NA", "F", "MS", "CC", "BW", "SL");
        assertThat(costTypeMapper.findAll()).extracting(CostType::getCostTypeCode)
                .containsExactlyInAnyOrder("DISCOUNT", "FEE", "INSURANCE", "PRICE", "SHIPPING", "TAX");
        assertThat(transactionTypeMapper.findAll()).extracting(TransactionType::getTransactionTypeCode)
                .containsExactlyInAnyOrder("A", "B", "F", "G", "P", "RM", "S", "TG", "TR", "YS");
        assertThat(transactionPlatformMapper.findAll()).extracting(TransactionPlatform::getTransactionPlatformName)
                .containsExactlyInAnyOrder("Bricklink", "eBay", "Private", "CataWiki", "Lauritz");
        assertThat(paymentPlatformMapper.findAll()).extracting(PaymentPlatform::getPaymentPlatformName)
                .containsExactlyInAnyOrder("PayPal", "Credit Card");
        assertThat(externalServiceTypeMapper.findAll()).extracting(ExternalServiceType::getExternalServiceTypeName)
                .containsExactlyInAnyOrder("LEGO", "MARKETPLACE", "AUCTION", "THIRDPARTY", "IMAGE_HOSTING");
        assertThat(externalServiceMapper.findAll()).extracting(ExternalService::getServiceCode)
                .containsExactlyInAnyOrder("LEGO", "BRICKLINK", "EBAY", "CATAWIKI", "LAURITZ", "QXL", "PNW_STEAM_SHOP", "BRICK_MODEL_RAILROADER", "REBRICKABLE", "FLICKR");
        assertThat(externalServiceCapabilityMapper.findAll()).extracting(ExternalServiceCapability::getCapabilityCode)
                .contains("CATALOG", "MARKETPLACE_LISTING", "ORDER_SYNC", "PRICE_GUIDE", "IMAGE_HOSTING");
        assertThat(externalCategoryMapper.findAll()).extracting(ExternalCategory::getCategoryName)
                .containsExactlyInAnyOrder("Brick", "Star Wars", "The Hobbit and The Lord of the Rings", "Technic", "Star Wars");
    }
}
