package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.TransactionItem;
import io.legohunter.data.dto.Transactions;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

public interface TransactionItemMapper {
    @Insert("""
            insert into transaction_item (transaction_id, transaction_type_code, notes, item_inventory_id)
            values (#{transactionId}, #{transactionTypeCode}, #{notes}, #{itemInventoryId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "transactionItemId")
    void insert(TransactionItem transactionItem);

    @Insert("""
            insert into transaction_item (transaction_item_id, transaction_id, transaction_type_code, notes, item_inventory_id)
            values (#{transactionItemId}, #{transactionId}, #{transactionTypeCode}, #{notes}, #{itemInventoryId})
            """)
    void migrate(TransactionItem transactionItem);

    @Update("""
            update transaction_item set 
            notes = #{notes},
            transaction_type_code = #{transactionTypeCode}
            where transaction_item_id = #{transactionItemId}
            """)
    void update(TransactionItem transactionItem);

    @Delete("""
            delete from transaction_item where transaction_item_id = #{transactionItemId}
            """)
    void delete(TransactionItem transactionItem);

    @Select("""
            """)
    @ResultMap("transactionItemResultMap")
    List<Transactions> findAll();

    @Select("""
            """)
    @ResultMap("transactionItemResultMap")
    Optional<TransactionItem> findById(Long transactionItemId);
}