package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.TransactionItemCost;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

public interface TransactionItemCostMapper {

    @Insert("""
            insert into transaction_item_cost (transaction_item_id, cost_type_code, currency_code, amount, notes) 
            values (#{transactionItemId}, #{costTypeCode}, #{currencyCode}, #{amount}, #{notes})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "transactionItemCostId")
    void insert(TransactionItemCost transactionItemCost);

    @Insert("""
            insert into transaction_item_cost (transaction_item_cost_id, transaction_item_id, cost_type_code, currency_code, amount, notes) 
            values (#{transactionItemCostId}, #{transactionItemId}, #{costTypeCode}, #{currencyCode}, #{amount}, #{notes})
            """)
    void migrate(TransactionItemCost transactionItemCost);

    @Update("""
            update transaction_item_cost set
                   cost_type_code =  #{costTypeCode},
                   currency_code =  #{currencyCode},
                   amount =  #{amount},
                   notes = #{notes}
            where transaction_item_cost_id = #{transactionItemCostId}
            """)
    void update(TransactionItemCost transactionItemCost);

    @Delete("""
            delete from transaction_item_cost 
            where transaction_item_id = #{transactionItemId}
            """)
    void deleteTransactionCosts(Long transactionItemId);

    @Delete("""
            delete from transaction_item_cost
            where transaction_item_cost_id = #{transactionItemCostId}
            """)
    void delete(Long transactionItemCostId);

    @Select("""
            select transaction_item_cost_id, transaction_item_id, cost_type_code, currency_code, amount, notes
            from transaction_item_cost
            """)
    @ResultMap("transactionItemCostResultMap")
    List<TransactionItemCost> findAll();

    @Select("""
            select transaction_item_cost_id, transaction_item_id, cost_type_code, currency_code, amount, notes
            from transaction_item_cost
            where transaction_item_id = #{transactionItemId}
            and cost_type_code = #{costTypeCode}
            """)
    @ResultMap("transactionItemCostResultMap")
    List<TransactionItemCost> findByTransactionItemIdAndCostTypeCode(Long transactionItemId, String costTypeCode);

    @Select("""
            select transaction_item_cost_id, transaction_item_id, cost_type_code, currency_code, amount, notes
            from transaction_item_cost
            where transaction_item_cost_id = #{transactionItemCostId}
            """)
    @ResultMap("transactionItemCostResultMap")
    Optional<TransactionItemCost> findById(Long transactionItemCostId);
}
