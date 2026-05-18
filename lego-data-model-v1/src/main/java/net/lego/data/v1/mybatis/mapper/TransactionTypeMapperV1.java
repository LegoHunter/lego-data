package net.lego.data.v1.mybatis.mapper;

import net.lego.data.v1.dto.TransactionType;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

public interface TransactionTypeMapperV1 {
    @Select("""
            SELECT transaction_type_code, \
                   transaction_type_description, \
                   conversion_factor \
            FROM transaction_type\
            """)
    @ResultMap("transactionTypeResultMapV1")
    List<TransactionType> findAll();

    @Select("""
            SELECT transaction_type_code, \
                   transaction_type_description, \
                   conversion_factor \
            FROM transaction_type \
            WHERE transaction_type_code = #{transactionTypeCode}\
            """)
    @ResultMap("transactionTypeResultMapV1")
    Optional<TransactionType> findTransactionTypeByCode(String transactionTypeCode);
}
