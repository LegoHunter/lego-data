package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.TransactionType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

public interface TransactionTypeMapper {
    @Insert("""
            INSERT INTO transaction_type (transaction_type_code, transaction_type_description, conversion_factor) \
            VALUES (#{transactionTypeCode}, #{transactionTypeDescription}, #{conversionFactor})\
            """)
    void insert(TransactionType transactionType);

    @Update("""
            UPDATE transaction_type SET transaction_type_code = #{transactionTypeCode}, transaction_type_description = #{transactionTypeDescription}, conversion_factor = #{conversionFactor} \
            WHERE transaction_type_code = #{transactionTypeCode}\
            """)
    void update(TransactionType transactionType);

    @Select("""
            SELECT transaction_type_code, \
                   transaction_type_description, \
                   conversion_factor \
            FROM transaction_type \
            """)
    @ResultMap("transactionTypeResultMap")
    List<TransactionType> findAll();

    @Select("""
            SELECT transaction_type_code, \
                   transaction_type_description, \
                   conversion_factor \
            FROM transaction_type \
            WHERE transaction_type_code = #{transactionTypeCode}\
            """)
    @ResultMap("transactionTypeResultMap")
    Optional<TransactionType> findTransactionTypeByCode(String transactionTypeCode);
}
