package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.Carrier;
import net.lego.data.v2.mybatis.mapper.CarrierMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CarrierDao {
    private final CarrierMapper carrierMapper;

    public List<Carrier> findAll() {
        return carrierMapper.findAll();
    }

    public Optional<Carrier> findCarrierByCode(final String carrierCode) {
        return carrierMapper.findCarrierByCode(carrierCode);
    }

    public void insert(Carrier carrier) {
        carrierMapper.insertCarrier(carrier);
    }

    public void update(Carrier carrier) {
        carrierMapper.updateCarrier(carrier);
    }
}
