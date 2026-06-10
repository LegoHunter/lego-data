package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.MarketplaceOrderTransactionLink;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Optional;
import java.util.Set;

public interface MarketplaceOrderTransactionLinkMapper {
    String ALL_COLUMNS = """
            marketplace_order_transaction_link_id,
            marketplace_order_id,
            transaction_id,
            marketplace_order_item_id,
            transaction_item_id,
            link_type_code,
            link_status_code,
            linked_at,
            unlinked_at,
            created_at,
            updated_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_order_transaction_link")
    @ResultMap("marketplaceOrderTransactionLinkResultMap")
    Set<MarketplaceOrderTransactionLink> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM marketplace_order_transaction_link WHERE marketplace_order_transaction_link_id = #{marketplaceOrderTransactionLinkId}")
    @ResultMap("marketplaceOrderTransactionLinkResultMap")
    Optional<MarketplaceOrderTransactionLink> findByMarketplaceOrderTransactionLinkId(Integer marketplaceOrderTransactionLinkId);

    @Insert("""
            INSERT INTO marketplace_order_transaction_link (
                marketplace_order_id,
                transaction_id,
                marketplace_order_item_id,
                transaction_item_id,
                link_type_code,
                link_status_code,
                linked_at,
                unlinked_at,
                created_at,
                updated_at
            )
            VALUES (
                #{marketplaceOrderId},
                #{transactionId},
                #{marketplaceOrderItemId},
                #{transactionItemId},
                #{linkTypeCode},
                #{linkStatusCode},
                #{linkedAt},
                #{unlinkedAt},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "marketplaceOrderTransactionLinkId")
    void insert(MarketplaceOrderTransactionLink marketplaceOrderTransactionLink);

    @Update("""
            UPDATE marketplace_order_transaction_link
            SET
                marketplace_order_id = #{marketplaceOrderId},
                transaction_id = #{transactionId},
                marketplace_order_item_id = #{marketplaceOrderItemId},
                transaction_item_id = #{transactionItemId},
                link_type_code = #{linkTypeCode},
                link_status_code = #{linkStatusCode},
                linked_at = #{linkedAt},
                unlinked_at = #{unlinkedAt},
                updated_at = CURRENT_TIMESTAMP
            WHERE marketplace_order_transaction_link_id = #{marketplaceOrderTransactionLinkId}
            """)
    int update(MarketplaceOrderTransactionLink marketplaceOrderTransactionLink);

    @Delete("DELETE FROM marketplace_order_transaction_link WHERE marketplace_order_transaction_link_id = #{marketplaceOrderTransactionLinkId}")
    int delete(Integer marketplaceOrderTransactionLinkId);

    @Insert("""
            INSERT INTO marketplace_order_transaction_link (
                marketplace_order_transaction_link_id,
                marketplace_order_id,
                transaction_id,
                marketplace_order_item_id,
                transaction_item_id,
                link_type_code,
                link_status_code,
                linked_at,
                unlinked_at,
                created_at,
                updated_at
            )
            VALUES (
                #{marketplaceOrderTransactionLinkId},
                #{marketplaceOrderId},
                #{transactionId},
                #{marketplaceOrderItemId},
                #{transactionItemId},
                #{linkTypeCode},
                #{linkStatusCode},
                #{linkedAt},
                #{unlinkedAt},
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON DUPLICATE KEY UPDATE
                marketplace_order_id = VALUES(marketplace_order_id),
                transaction_id = VALUES(transaction_id),
                marketplace_order_item_id = VALUES(marketplace_order_item_id),
                transaction_item_id = VALUES(transaction_item_id),
                link_type_code = VALUES(link_type_code),
                link_status_code = VALUES(link_status_code),
                linked_at = VALUES(linked_at),
                unlinked_at = VALUES(unlinked_at),
                updated_at = CURRENT_TIMESTAMP
            """)
    @Options(useGeneratedKeys = true, keyProperty = "marketplaceOrderTransactionLinkId")
    void upsert(MarketplaceOrderTransactionLink marketplaceOrderTransactionLink);
}
