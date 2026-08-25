package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PartyExternalIdentity;
import org.apache.ibatis.annotations.*;
import java.util.Optional;

public interface PartyExternalIdentityMapper {
    String COLUMNS = "party_external_identity_id, party_id, transaction_platform_id, external_party_id, created_at";
    @Select("SELECT " + COLUMNS + " FROM party_external_identity WHERE transaction_platform_id=#{transactionPlatformId} AND external_party_id=#{externalPartyId}")
    @ResultMap("partyExternalIdentityResultMap") Optional<PartyExternalIdentity> findByPlatformAndExternalPartyId(@Param("transactionPlatformId") Integer transactionPlatformId, @Param("externalPartyId") String externalPartyId);
    @Insert("INSERT INTO party_external_identity (party_id,transaction_platform_id,external_party_id) VALUES (#{partyId},#{transactionPlatformId},#{externalPartyId})")
    @Options(useGeneratedKeys=true,keyProperty="partyExternalIdentityId") void insert(PartyExternalIdentity identity);
}
