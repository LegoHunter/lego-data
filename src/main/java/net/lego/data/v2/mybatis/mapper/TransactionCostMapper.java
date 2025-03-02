package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.TransactionCost;
import net.lego.data.v2.dto.Transactions;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

public interface TransactionCostMapper {

    @Insert("""
            insert into transaction_cost (cost_category_code, cost_reference_id, cost_type_code, currency_code, amount, notes) 
            values (#{costCategoryCode}, #{costReferenceId}, #{costTypeCode}, #{currencyCode}, #{amount}, #{notes})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "transactionCostId")
    void insert(TransactionCost transactionCost);

    @Insert("""
            insert into transaction_cost (transaction_cost_id, cost_category_code, cost_reference_id, cost_type_code, currency_code, amount, notes) 
            values (#{transactionCostId}, #{costCategoryCode}, #{costReferenceId}, #{costTypeCode}, #{currencyCode}, #{amount}, #{notes})
            """)
    void migrate(TransactionCost transactionCost);

    @Update("""
            update transaction_cost set
                   cost_category_code = #{costCategoryCode},
                   cost_reference_id =  #{costReferenceId},
                   cost_type_code =  #{costTypeCode},
                   currency_code =  #{currencyCode},
                   amount =  #{amount},
                   notes = #{notes}
            where transaction_cost_id = #{transactionCostId}
            """)
    void update(TransactionCost transactionCost);

    default void deleteTransactionCosts(Long transactionId) {
        deleteTransactionCostsForCostReferenceId(transactionId, "TRANSACTION");
    }

    default void deleteTransactionItemCosts(Long transactionItemId) {
        deleteTransactionCostsForCostReferenceId(transactionItemId, "TRANSACTION_ITEM");
    }

    @Delete("""
            delete from transaction_cost 
            where cost_reference_id = #{costReferenceId}
            and cost_category_code = #{costCategoryCode}
            """)
    void deleteTransactionCostsForCostReferenceId(Long costReferenceId, String costCategoryCode);

    @Delete("""
            delete from transaction_cost where transaction_cost_id = #{transactionCostId} 
            """)
    void delete(Long transactionCostId);

    @Select("""
            select transaction_cost_id, cost_category_code, cost_reference_id, cost_type_code, currency_code, amount, notes
            from transaction_cost
            """)
    @ResultMap("transactionCostResultMap")
    List<TransactionCost> findAll();

    @Select("""
            select transaction_cost_id, cost_category_code, cost_reference_id, cost_type_code, currency_code, amount, notes
            from transaction_cost
            where cost_reference_id = #{costReferenceId}
            and cost_category_code = #{costCategoryCode}
            """)
    @ResultMap("transactionCostResultMap")
    List<TransactionCost> findByCostCategoryCodeAndCostReferenceId(String costCategoryCode, Long costReferenceId);

    default List<TransactionCost> findByTransactionCosts(Long transactionId) {
        return findByCostCategoryCodeAndCostReferenceId("TRANSACTIONS", transactionId);
    }

    @Select("""
            select transaction_cost_id, cost_category_code, cost_reference_id, cost_type_code, currency_code, amount, notes
            from transaction_cost
            where transaction_cost_id = #{transactionCostId}
            """)
    @ResultMap("transactionCostResultMap")
    Optional<TransactionCost> findById(Long transactionCostId);

    default Optional<TransactionCost> findByTransactionIdAndCostTypeCode(Long transactionId, String costTypeCode) {
        return findByCostCategoryCodeAndCostReferenceIdAndCostTypeCode("TRANSACTION", transactionId, costTypeCode);
    }

    default Optional<TransactionCost> findByTransactionItemIdAndCostTypeCode(Long transactionItemId, String costTypeCode) {
        return findByCostCategoryCodeAndCostReferenceIdAndCostTypeCode("TRANSACTION_ITEM", transactionItemId, costTypeCode);
    }

    @Select("""
            select transaction_cost_id, cost_category_code, cost_reference_id, cost_type_code, currency_code, amount, notes
            from transaction_cost
            where cost_reference_id = #{costReferenceId}
            and cost_category_code = #{costCategoryCode}
            and cost_type_code = #{costTypeCode}
            """)
    @ResultMap("transactionCostResultMap")
    Optional<TransactionCost> findByCostCategoryCodeAndCostReferenceIdAndCostTypeCode(String costCategoryCode, Long costReferenceId, String costTypeCode);
}
