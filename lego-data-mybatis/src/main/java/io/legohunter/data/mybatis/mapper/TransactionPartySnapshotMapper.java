package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.TransactionPartySnapshot;
import org.apache.ibatis.annotations.*;
import java.util.List;

public interface TransactionPartySnapshotMapper {
    String COLUMNS = "transaction_party_snapshot_id,transaction_id,party_id,party_role_code,display_name,address1,address2,city,state,postal_code,country_code,country,phone,email,captured_at";
    @Insert("INSERT INTO transaction_party_snapshot (transaction_id,party_id,party_role_code,display_name,address1,address2,city,state,postal_code,country_code,country,phone,email,captured_at) VALUES (#{transactionId},#{partyId},#{partyRoleCode},#{displayName},#{address1},#{address2},#{city},#{state},#{postalCode},#{countryCode},#{country},#{phone},#{email},#{capturedAt})")
    @Options(useGeneratedKeys=true,keyProperty="transactionPartySnapshotId") void insert(TransactionPartySnapshot snapshot);
    @Select("SELECT " + COLUMNS + " FROM transaction_party_snapshot WHERE transaction_id=#{transactionId} ORDER BY transaction_party_snapshot_id")
    @ResultMap("transactionPartySnapshotResultMap") List<TransactionPartySnapshot> findByTransactionId(Long transactionId);
}
