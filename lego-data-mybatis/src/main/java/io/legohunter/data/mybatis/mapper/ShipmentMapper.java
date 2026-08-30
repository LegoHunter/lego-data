package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Shipment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

public interface ShipmentMapper {
    String COLUMNS = """
            shipment_id,
            external_shipment_id,
            shipment_date,
            shipment_tracking_number,
            carrier_code,
            fulfillment_platform_code,
            service_code
            """;

    @Select("SELECT " + COLUMNS + " FROM shipment")
    @ResultMap("shipmentResultMap")
    List<Shipment> findAll();

    @Select("SELECT " + COLUMNS + " FROM shipment WHERE shipment_id=#{shipmentId}")
    @ResultMap("shipmentResultMap")
    Optional<Shipment> findByShipmentId(Long shipmentId);

    @Insert("""
            INSERT INTO shipment (
                external_shipment_id, shipment_date, shipment_tracking_number, carrier_code,
                fulfillment_platform_code, service_code
            ) VALUES (
                #{externalShipmentId}, #{shipmentDate,jdbcType=DATE}, #{shipmentTrackingNumber}, #{carrierCode},
                #{fulfillmentPlatformCode}, #{serviceCode}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "shipmentId")
    void insert(Shipment shipment);

    @Insert("""
            INSERT INTO shipment (
                external_shipment_id, shipment_date, shipment_tracking_number, carrier_code,
                fulfillment_platform_code, service_code
            ) VALUES (
                #{externalShipmentId}, #{shipmentDate,jdbcType=DATE}, #{shipmentTrackingNumber}, #{carrierCode},
                #{fulfillmentPlatformCode}, #{serviceCode}
            ) ON DUPLICATE KEY UPDATE
                shipment_date = VALUES(shipment_date),
                shipment_tracking_number = VALUES(shipment_tracking_number),
                carrier_code = VALUES(carrier_code),
                service_code = VALUES(service_code)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "shipmentId")
    void upsert(Shipment shipment);

    @Delete("DELETE FROM shipment WHERE shipment_id = #{shipmentId}")
    int delete(Long shipmentId);

    @Select("SELECT " + COLUMNS + " FROM shipment WHERE fulfillment_platform_code=#{fulfillmentPlatformCode} AND external_shipment_id=#{externalShipmentId}")
    @ResultMap("shipmentResultMap")
    Optional<Shipment> findByPlatformAndExternalShipmentId(
            @Param("fulfillmentPlatformCode") String fulfillmentPlatformCode,
            @Param("externalShipmentId") String externalShipmentId
    );

    @Insert("""
            INSERT INTO transaction_item_shipment (item_transaction_id, shipment_id)
            SELECT #{transactionItemId}, #{shipmentId}
            WHERE NOT EXISTS (
                SELECT 1
                FROM transaction_item_shipment
                WHERE item_transaction_id = #{transactionItemId}
                  AND shipment_id = #{shipmentId}
            )
            """)
    void linkTransactionItem(
            @Param("transactionItemId") Long transactionItemId,
            @Param("shipmentId") Long shipmentId
    );

    @Select("""
            SELECT item_transaction_id
            FROM transaction_item_shipment
            WHERE shipment_id = #{shipmentId}
            ORDER BY item_transaction_id
            """)
    List<Long> findTransactionItemIdsByShipmentId(@Param("shipmentId") Long shipmentId);
}
