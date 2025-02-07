package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.TransactionCost;
import net.lego.data.v2.dto.Transactions;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

public interface TransactionCostMapper {
    @Insert("""
            insert into transaction_cost (transaction_id, cost_type_code, amount, currency_code) 
            values (#{transactionId}, #{costTypeCode}, #{amount}, #{currencyCode})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "transactionId")
    void insert(TransactionCost transactionCost);

    @Insert("""
            insert into transaction_cost (transaction_cost_id, transaction_id, cost_type_code, amount, currency_code) 
            values (#{transactionCostId}, #{transactionId}, #{costTypeCode}, #{amount}, #{currencyCode})
            """)
    @Options(useGeneratedKeys = false, keyProperty = "partyId")
    void migrate(TransactionCost transactionCost);

    @Update("""
            update transaction_cost set 
                   transaction_id = #{transactionId},
                   cost_type_code = #{costTypeCode},
                   amount = #{amount},
                   currency_code = #{currencyCode}                       
            where transaction_cost_id = #{transactionCostId}
            """)
    void update(TransactionCost transactionCost);

    @Select("""
            select transaction_cost_id,
                   transaction_id,
                   cost_type_code,
                   amount,
                   currency_code
            from transaction_cost
            """)
    @ResultMap("transactionCostResultMap")
    List<TransactionCost> findAll();

    @Select("""
            select transaction_cost_id,
                   transaction_id,
                   cost_type_code,
                   amount,
                   currency_code
            from transaction_cost
            where transaction_cost_id = #{transactionCostId}
            """)
    @ResultMap("transactionCostResultMap")
    Optional<TransactionCost> findById(Long transactionCostId);

    @Select("""
            select transaction_cost_id,
                   transaction_id,
                   cost_type_code,
                   amount,
                   currency_code
            from transaction_cost
            where transaction_id = #{transactionId}
            and cost_type_code = #{costTypeCode}
            """)
    @ResultMap("transactionCostResultMap")
    Optional<TransactionCost> findByTransactionIdAndCostTypeCode(Long transactionId, String costTypeCode);
}
