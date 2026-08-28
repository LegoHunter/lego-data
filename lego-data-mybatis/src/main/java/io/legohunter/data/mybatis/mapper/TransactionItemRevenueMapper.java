package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.TransactionItemRevenue;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;

public interface TransactionItemRevenueMapper {
    String COLUMNS = "transaction_item_revenue_id,transaction_item_id,currency_code,unit_amount,quantity,total_amount";
    @Select("SELECT " + COLUMNS + " FROM transaction_item_revenue")
    @ResultMap("transactionItemRevenueResultMap") List<TransactionItemRevenue> findAll();
    @Select("SELECT " + COLUMNS + " FROM transaction_item_revenue WHERE transaction_item_revenue_id=#{transactionItemRevenueId}")
    @ResultMap("transactionItemRevenueResultMap") Optional<TransactionItemRevenue> findByTransactionItemRevenueId(Long transactionItemRevenueId);
    @Insert("INSERT INTO transaction_item_revenue (transaction_item_id,currency_code,unit_amount,quantity,total_amount) VALUES (#{transactionItemId},#{currencyCode},#{unitAmount},#{quantity},#{totalAmount})")
    @Options(useGeneratedKeys=true,keyProperty="transactionItemRevenueId") void insert(TransactionItemRevenue revenue);
    @Insert("INSERT INTO transaction_item_revenue (transaction_item_id,currency_code,unit_amount,quantity,total_amount) VALUES (#{transactionItemId},#{currencyCode},#{unitAmount},#{quantity},#{totalAmount}) ON DUPLICATE KEY UPDATE currency_code=VALUES(currency_code),unit_amount=VALUES(unit_amount),quantity=VALUES(quantity),total_amount=VALUES(total_amount)")
    @Options(useGeneratedKeys=true,keyProperty="transactionItemRevenueId") void upsert(TransactionItemRevenue revenue);
    @Delete("DELETE FROM transaction_item_revenue WHERE transaction_item_revenue_id=#{transactionItemRevenueId}") int delete(Long transactionItemRevenueId);
    @Select("SELECT " + COLUMNS + " FROM transaction_item_revenue WHERE transaction_item_id=#{transactionItemId}")
    @ResultMap("transactionItemRevenueResultMap") Optional<TransactionItemRevenue> findByTransactionItemId(Long transactionItemId);
}
