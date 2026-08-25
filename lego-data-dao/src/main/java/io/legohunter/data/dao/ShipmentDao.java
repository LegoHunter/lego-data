package io.legohunter.data.dao;

import io.legohunter.data.dto.Shipment;
import io.legohunter.data.mybatis.mapper.ShipmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component @RequiredArgsConstructor
public class ShipmentDao {
    private final ShipmentMapper mapper;
    public void insert(Shipment shipment) { mapper.insert(shipment); }
    public Optional<Shipment> findByPlatformAndExternalShipmentId(String platformCode, String externalShipmentId) { return mapper.findByPlatformAndExternalShipmentId(platformCode, externalShipmentId); }
    public void linkTransactionItem(Long transactionItemId, Long shipmentId) { mapper.linkTransactionItem(transactionItemId, shipmentId); }
}
