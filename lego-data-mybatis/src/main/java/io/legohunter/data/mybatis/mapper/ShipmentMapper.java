package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Shipment;
import org.apache.ibatis.annotations.*;
import java.util.Optional;

public interface ShipmentMapper {
    String COLUMNS = "shipment_id,external_shipment_id,shipment_date,shipment_tracking_number,carrier_code,fulfillment_platform_code,service_code";
    @Insert("INSERT INTO shipment (external_shipment_id,shipment_date,shipment_tracking_number,carrier_code,fulfillment_platform_code,service_code) VALUES (#{externalShipmentId},#{shipmentDate,jdbcType=DATE},#{shipmentTrackingNumber},#{carrierCode},#{fulfillmentPlatformCode},#{serviceCode})")
    @Options(useGeneratedKeys=true,keyProperty="shipmentId") void insert(Shipment shipment);
    @Select("SELECT " + COLUMNS + " FROM shipment WHERE fulfillment_platform_code=#{fulfillmentPlatformCode} AND external_shipment_id=#{externalShipmentId}")
    @ResultMap("shipmentResultMap") Optional<Shipment> findByPlatformAndExternalShipmentId(@Param("fulfillmentPlatformCode") String fulfillmentPlatformCode, @Param("externalShipmentId") String externalShipmentId);
    @Insert("INSERT INTO transaction_item_shipment (item_transaction_id,shipment_id) VALUES (#{transactionItemId},#{shipmentId})") void linkTransactionItem(@Param("transactionItemId") Long transactionItemId, @Param("shipmentId") Long shipmentId);
}
