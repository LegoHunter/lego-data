package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Carrier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class CarrierMapperTest extends MapperTestSupport {

    @Test
    void insertUpdateFindByCodeAndFindAll() {
        Carrier carrier = Carrier.builder()
                .carrierCode("UPS")
                .carrierName("UPS")
                .trackingUrlPattern("https://track.example/{tracking}")
                .build();

        carrierMapper.insertCarrier(carrier);
        carrier.setCarrierName("United Parcel Service");
        carrierMapper.updateCarrier(carrier);

        assertThat(carrierMapper.findCarrierByCode("UPS"))
                .hasValueSatisfying(found -> assertThat(found.getCarrierName()).isEqualTo("United Parcel Service"));
        assertThat(carrierMapper.findAll()).extracting(Carrier::getCarrierCode).containsExactly("UPS");
    }
}
