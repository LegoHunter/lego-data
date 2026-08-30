package io.legohunter.data.dao;

import io.legohunter.data.dto.Shipment;
import io.legohunter.data.mybatis.mapper.ShipmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component @RequiredArgsConstructor
public class ShipmentDao {
    private final ShipmentMapper mapper;
    public List<Shipment> findAll() { return mapper.findAll(); }
    public Optional<Shipment> findByShipmentId(Long shipmentId) { return mapper.findByShipmentId(shipmentId); }
    public Shipment insert(Shipment shipment) { mapper.insert(shipment); return shipment; }
    public Shipment upsert(Shipment shipment) { mapper.upsert(shipment); return shipment; }
    public int delete(Long shipmentId) { return mapper.delete(shipmentId); }
    public Optional<Shipment> findByPlatformAndExternalShipmentId(String platformCode, String externalShipmentId) { return mapper.findByPlatformAndExternalShipmentId(platformCode, externalShipmentId); }
    public void linkTransactionItem(Long transactionItemId, Long shipmentId) { mapper.linkTransactionItem(transactionItemId, shipmentId); }
    public List<Long> findTransactionItemIdsByShipmentId(Long shipmentId) { return mapper.findTransactionItemIdsByShipmentId(shipmentId); }
}
