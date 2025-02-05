package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.Transactions;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

public interface TransactionsMapper {
    @Insert("""
            insert into transactions (transaction_date, notes, from_party_id, to_party_id, transaction_platform_code) 
            values (#{transactionDate}, #{notes}, #{fromPartyId}, #{toPartyId}, #{transactionPlatformCode})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "transactionId")
    void insert(Transactions transactions);

    @Insert("""
            insert into transactions (transaction_id, transaction_date, notes, from_party_id, to_party_id, transaction_platform_code) 
            values (#{transactionId}, #{transactionDate}, #{notes}, #{fromPartyId}, #{toPartyId}, #{transactionPlatformCode})
            """)
    @Options(useGeneratedKeys = false, keyProperty = "partyId")
    void migrate(Transactions transactions);

    @Update("""
            update transactions set 
            transaction_date = #{transactionDateTime}, 
            notes = #{notes}, 
            from_party_id = #{fromPartyId}, 
            to_party_id = #{toPartyId}, 
            transaction_platform_code = #{transactionPlatformCode} 
            where transaction_id = #{transactionId}
            """)
    void update(Transactions transactions);

    @Select("""
            select transaction_id,
                   transaction_date,
                   notes,
                   from_party_id,
                   to_party_id,
                   transaction_platform_id
            from transactions
            where transaction_id = #{transactionId}
            """)
    @ResultMap("transactionResultMap")
    List<Transactions> findAll();

    @Select("""
            select transaction_id,
                   transaction_date,
                   notes,
                   from_party_id,
                   to_party_id,
                   transaction_platform_id
            from transactions
            """)
    @ResultMap("transactionResultMap")
    Optional<Transactions> findById(Long transactionId);
}
