package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Transactions;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

public interface TransactionsMapper {
    @Insert("""
            insert into transactions (transaction_date, notes, from_party_id, to_party_id, transaction_platform_id, transaction_order_id) 
            values (#{transactionDate,jdbcType=DATE}, #{notes}, #{fromPartyId}, #{toPartyId}, #{transactionPlatformId}, #{transactionOrderId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "transactionId")
    void insert(Transactions transactions);

    @Insert("""
            insert into transactions (transaction_id, transaction_date, notes, from_party_id, to_party_id, transaction_platform_id, transaction_order_id) 
            values (#{transactionId}, #{transactionDate,jdbcType=DATE}, #{notes}, #{fromPartyId}, #{toPartyId}, #{transactionPlatformId}, #{transactionOrderId})
            """)
    @Options(useGeneratedKeys = false, keyProperty = "partyId")
    void migrate(Transactions transactions);

    @Update("""
            update transactions set 
            transaction_date = #{transactionDate,jdbcType=DATE},
            notes = #{notes}, 
            from_party_id = #{fromPartyId}, 
            to_party_id = #{toPartyId}, 
            transaction_platform_id = #{transactionPlatformId}, 
            transaction_order_id = #{transactionOrderId} 
            where transaction_id = #{transactionId}
            """)
    void update(Transactions transactions);

    @Delete("""
            delete from transactions where transaction_id = #{transactionId}
            """)
    void delete(Transactions transactions);

    @Select("""
            select transaction_id,
                   transaction_date,
                   notes,
                   from_party_id,
                   to_party_id,
                   transaction_platform_id,
                   transaction_order_id
            from transactions
            """)
    @ResultMap("transactionResultMap")
    List<Transactions> findAll();

    @Select("""
            select transaction_id,
                   transaction_date,
                   notes,
                   from_party_id,
                   to_party_id,
                   transaction_platform_id,
                   transaction_order_id
            from transactions
            where transaction_id = #{transactionId}
            """)
    @ResultMap("transactionResultMap")
    Optional<Transactions> findById(Long transactionId);

    @Select("""
            select transaction_id,
                   transaction_date,
                   notes,
                   from_party_id,
                   to_party_id,
                   transaction_platform_id,
                   transaction_order_id
            from transactions
            where transaction_platform_id = #{transactionPlatformId}
              and transaction_order_id = #{transactionOrderId}
            """)
    @ResultMap("transactionResultMap")
    Optional<Transactions> findByTransactionPlatformIdAndTransactionOrderId(
            @Param("transactionPlatformId") Integer transactionPlatformId,
            @Param("transactionOrderId") String transactionOrderId
    );
}
