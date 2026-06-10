package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketplaceOrder;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface MarketplaceOrderMapper {
    String ALL_COLUMNS = """
            marketplace_order_id,
            last_sync_run_id,
            marketplace_code,
            external_order_id,
            order_direction,
            external_status_code,
            ordered_at,
            status_changed_at,
            buyer_display_name,
            buyer_email,
            payment_status_code,
            payment_method,
            payment_currency_code,
            paid_at,
            shipping_method,
            shipping_method_id,
            shipping_address_present,
            tracking_present,
            subtotal_amount,
            shipping_amount,
            grand_total_amount,
            currency_code,
            display_currency_code,
            total_count,
            unique_count,
            total_weight,
            is_invoiced,
            is_filed,
            sent_drive_thru,
            require_insurance,
            payload_hash,
            last_seen_at,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_order")
    @ResultMap("marketplaceOrderResultMap")
    Set<MarketplaceOrder> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_order WHERE marketplace_order_id = #{marketplaceOrderId}")
    @ResultMap("marketplaceOrderResultMap")
    Optional<MarketplaceOrder> findByMarketplaceOrderId(Integer marketplaceOrderId);

    @Select("SELECT " + ALL_COLUMNS + """
            FROM marketplace_order
            WHERE marketplace_code = #{marketplaceCode}
              AND external_order_id = #{externalOrderId}
            """)
    @ResultMap("marketplaceOrderResultMap")
    Optional<MarketplaceOrder> findByMarketplaceCodeAndExternalOrderId(
            @Param("marketplaceCode") String marketplaceCode,
            @Param("externalOrderId") String externalOrderId);

    @Insert("""
            INSERT INTO marketplace_order (
                last_sync_run_id,
                marketplace_code,
                external_order_id,
                order_direction,
                external_status_code,
                ordered_at,
                status_changed_at,
                buyer_display_name,
                buyer_email,
                payment_status_code,
                payment_method,
                payment_currency_code,
                paid_at,
                shipping_method,
                shipping_method_id,
                shipping_address_present,
                tracking_present,
                subtotal_amount,
                shipping_amount,
                grand_total_amount,
                currency_code,
                display_currency_code,
                total_count,
                unique_count,
                total_weight,
                is_invoiced,
                is_filed,
                sent_drive_thru,
                require_insurance,
                payload_hash,
                last_seen_at,
                created_at,
                updated_at
            )
            VALUES (
                #{lastSyncRunId},
                #{marketplaceCode},
                #{externalOrderId},
                #{orderDirection},
                #{externalStatusCode},
                #{orderedAt},
                #{statusChangedAt},
                #{buyerDisplayName},
                #{buyerEmail},
                #{paymentStatusCode},
                #{paymentMethod},
                #{paymentCurrencyCode},
                #{paidAt},
                #{shippingMethod},
                #{shippingMethodId},
                #{shippingAddressPresent},
                #{trackingPresent},
                #{subtotalAmount},
                #{shippingAmount},
                #{grandTotalAmount},
                #{currencyCode},
                #{displayCurrencyCode},
                #{totalCount},
                #{uniqueCount},
                #{totalWeight},
                #{invoiced},
                #{filed},
                #{sentDriveThru},
                #{requireInsurance},
                #{payloadHash},
                #{lastSeenAt},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "marketplaceOrderId")
    void insert(MarketplaceOrder marketplaceOrder);

    @Update("""
            UPDATE marketplace_order
            SET
                last_sync_run_id = #{lastSyncRunId},
                marketplace_code = #{marketplaceCode},
                external_order_id = #{externalOrderId},
                order_direction = #{orderDirection},
                external_status_code = #{externalStatusCode},
                ordered_at = #{orderedAt},
                status_changed_at = #{statusChangedAt},
                buyer_display_name = #{buyerDisplayName},
                buyer_email = #{buyerEmail},
                payment_status_code = #{paymentStatusCode},
                payment_method = #{paymentMethod},
                payment_currency_code = #{paymentCurrencyCode},
                paid_at = #{paidAt},
                shipping_method = #{shippingMethod},
                shipping_method_id = #{shippingMethodId},
                shipping_address_present = #{shippingAddressPresent},
                tracking_present = #{trackingPresent},
                subtotal_amount = #{subtotalAmount},
                shipping_amount = #{shippingAmount},
                grand_total_amount = #{grandTotalAmount},
                currency_code = #{currencyCode},
                display_currency_code = #{displayCurrencyCode},
                total_count = #{totalCount},
                unique_count = #{uniqueCount},
                total_weight = #{totalWeight},
                is_invoiced = #{invoiced},
                is_filed = #{filed},
                sent_drive_thru = #{sentDriveThru},
                require_insurance = #{requireInsurance},
                payload_hash = #{payloadHash},
                last_seen_at = #{lastSeenAt},
                updated_at = CURRENT_TIMESTAMP
            WHERE marketplace_order_id = #{marketplaceOrderId}
            """)
    int update(MarketplaceOrder marketplaceOrder);

    @Delete("DELETE FROM marketplace_order WHERE marketplace_order_id = #{marketplaceOrderId}")
    int delete(Integer marketplaceOrderId);

    @Insert("""
            INSERT INTO marketplace_order (
                marketplace_order_id,
                last_sync_run_id,
                marketplace_code,
                external_order_id,
                order_direction,
                external_status_code,
                ordered_at,
                status_changed_at,
                buyer_display_name,
                buyer_email,
                payment_status_code,
                payment_method,
                payment_currency_code,
                paid_at,
                shipping_method,
                shipping_method_id,
                shipping_address_present,
                tracking_present,
                subtotal_amount,
                shipping_amount,
                grand_total_amount,
                currency_code,
                display_currency_code,
                total_count,
                unique_count,
                total_weight,
                is_invoiced,
                is_filed,
                sent_drive_thru,
                require_insurance,
                payload_hash,
                last_seen_at,
                created_at,
                updated_at
            )
            VALUES (
                #{marketplaceOrderId},
                #{lastSyncRunId},
                #{marketplaceCode},
                #{externalOrderId},
                #{orderDirection},
                #{externalStatusCode},
                #{orderedAt},
                #{statusChangedAt},
                #{buyerDisplayName},
                #{buyerEmail},
                #{paymentStatusCode},
                #{paymentMethod},
                #{paymentCurrencyCode},
                #{paidAt},
                #{shippingMethod},
                #{shippingMethodId},
                #{shippingAddressPresent},
                #{trackingPresent},
                #{subtotalAmount},
                #{shippingAmount},
                #{grandTotalAmount},
                #{currencyCode},
                #{displayCurrencyCode},
                #{totalCount},
                #{uniqueCount},
                #{totalWeight},
                #{invoiced},
                #{filed},
                #{sentDriveThru},
                #{requireInsurance},
                #{payloadHash},
                #{lastSeenAt},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                marketplace_order_id = LAST_INSERT_ID(marketplace_order_id),
                last_sync_run_id = VALUES(last_sync_run_id),
                order_direction = VALUES(order_direction),
                external_status_code = VALUES(external_status_code),
                ordered_at = VALUES(ordered_at),
                status_changed_at = VALUES(status_changed_at),
                buyer_display_name = VALUES(buyer_display_name),
                buyer_email = VALUES(buyer_email),
                payment_status_code = VALUES(payment_status_code),
                payment_method = VALUES(payment_method),
                payment_currency_code = VALUES(payment_currency_code),
                paid_at = VALUES(paid_at),
                shipping_method = VALUES(shipping_method),
                shipping_method_id = VALUES(shipping_method_id),
                shipping_address_present = VALUES(shipping_address_present),
                tracking_present = VALUES(tracking_present),
                subtotal_amount = VALUES(subtotal_amount),
                shipping_amount = VALUES(shipping_amount),
                grand_total_amount = VALUES(grand_total_amount),
                currency_code = VALUES(currency_code),
                display_currency_code = VALUES(display_currency_code),
                total_count = VALUES(total_count),
                unique_count = VALUES(unique_count),
                total_weight = VALUES(total_weight),
                is_invoiced = VALUES(is_invoiced),
                is_filed = VALUES(is_filed),
                sent_drive_thru = VALUES(sent_drive_thru),
                require_insurance = VALUES(require_insurance),
                payload_hash = VALUES(payload_hash),
                last_seen_at = VALUES(last_seen_at),
                updated_at = CURRENT_TIMESTAMP
            """)
    @Options(useGeneratedKeys = true, keyProperty = "marketplaceOrderId")
    void upsert(MarketplaceOrder marketplaceOrder);
}
