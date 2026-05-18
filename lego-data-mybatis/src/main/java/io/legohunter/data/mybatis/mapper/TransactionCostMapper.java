package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.TransactionCost;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.*;


public interface TransactionCostMapper {

    @Insert("""
            insert into transaction_cost (transaction_id, cost_type_code, currency_code, amount, notes) 
            values (#{transactionId}, #{costTypeCode}, #{currencyCode}, #{amount}, #{notes})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "transactionCostId")
    void insert(TransactionCost transactionCost);

    @Insert("""
            insert into transaction_cost (transaction_cost_id, transaction_id, cost_type_code, currency_code, amount, notes) 
            values (#{transactionCostId}, #{transactionId}, #{costTypeCode}, #{currencyCode}, #{amount}, #{notes})
            """)
    void migrate(TransactionCost transactionCost);

    @Update("""
            update transaction_cost set
                   cost_type_code =  #{costTypeCode},
                   currency_code =  #{currencyCode},
                   amount =  #{amount},
                   notes = #{notes}
            where transaction_cost_id = #{transactionCostId}
            """)
    void update(TransactionCost transactionCost);

    @Delete("""
            delete from transaction_cost 
            where transaction_id = #{transactionId}
            """)
    void deleteTransactionCosts(Long transactionId);

    @Delete("""
            delete from transaction_cost
            where transaction_cost_id = #{transactionCostId}
            """)
    void delete(Long transactionCostId);

    @Select("""
            select transaction_cost_id, transaction_id, cost_type_code, currency_code, amount, notes
            from transaction_cost
            """)
    @ResultMap("transactionCostResultMap")
    List<TransactionCost> findAll();

    @Select("""
            select transaction_cost_id, transaction_id, cost_type_code, currency_code, amount, notes
            from transaction_cost
            where transaction_id = #{transactionId}
            and cost_type_code = #{costTypeCode}
            """)
    @ResultMap("transactionCostResultMap")
    List<TransactionCost> findByTransactionIdAndCostTypeCode(Long transactionId, String costTypeCode);

    @Select("""
            select transaction_cost_id, transaction_id, cost_type_code, currency_code, amount, notes
            from transaction_cost
            where transaction_cost_id = #{transactionCostId}
            """)
    @ResultMap("transactionCostResultMap")
    Optional<TransactionCost> findById(Long transactionCostId);
}