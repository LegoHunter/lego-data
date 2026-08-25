package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.TransactionItemRevenue;
import org.apache.ibatis.annotations.*;
import java.util.Optional;

public interface TransactionItemRevenueMapper {
    String COLUMNS = "transaction_item_revenue_id,transaction_item_id,currency_code,unit_amount,quantity,total_amount";
    @Insert("INSERT INTO transaction_item_revenue (transaction_item_id,currency_code,unit_amount,quantity,total_amount) VALUES (#{transactionItemId},#{currencyCode},#{unitAmount},#{quantity},#{totalAmount})")
    @Options(useGeneratedKeys=true,keyProperty="transactionItemRevenueId") void insert(TransactionItemRevenue revenue);
    @Select("SELECT " + COLUMNS + " FROM transaction_item_revenue WHERE transaction_item_id=#{transactionItemId}")
    @ResultMap("transactionItemRevenueResultMap") Optional<TransactionItemRevenue> findByTransactionItemId(Long transactionItemId);
}
