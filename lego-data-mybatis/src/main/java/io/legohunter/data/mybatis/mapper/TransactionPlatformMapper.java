package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.TransactionPlatform;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

public interface TransactionPlatformMapper {
    @Insert("""
            INSERT INTO transaction_platform (transaction_platform_id, transaction_platform_name) \
            VALUES (#{transactionPlatformId}, #{transactionPlatformName})\
            """)
    void insert(TransactionPlatform transactionPlatform);

    @Update("""
            UPDATE transaction_platform SET transaction_platform_name = #{transactionPlatformName} \
            WHERE transaction_platform_id = #{transactionPlatformId}\
            """)
    void update(TransactionPlatform transactionPlatform);

    @Select("""
            SELECT transaction_platform_id, \
                   transaction_platform_name \
            FROM transaction_platform \
            """)
    @ResultMap("transactionPlatformResultMap")
    List<TransactionPlatform> findAll();

    @Select("""
            SELECT transaction_platform_id, \
                   transaction_platform_name \
            FROM transaction_platform \
            WHERE transaction_platform_id = #{transactionPlatformId}\
            """)
    @ResultMap("transactionPlatformResultMap")
    Optional<TransactionPlatform> findTransactionPlatformById(Integer transactionPlatformId);

    @Select("""
            SELECT transaction_platform_id, \
                   transaction_platform_name \
            FROM transaction_platform \
            WHERE transaction_platform_name = #{transactionPlatformName}\
            """)
    @ResultMap("transactionPlatformResultMap")
    Optional<TransactionPlatform> findTransactionPlatformByName(String transactionPlatformName);
}
