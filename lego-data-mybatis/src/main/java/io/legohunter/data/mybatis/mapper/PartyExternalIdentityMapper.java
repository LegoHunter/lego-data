package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.PartyExternalIdentity;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;

public interface PartyExternalIdentityMapper {
    String COLUMNS = "party_external_identity_id, party_id, transaction_platform_id, external_party_id, created_at";
    @Select("SELECT " + COLUMNS + " FROM party_external_identity")
    @ResultMap("partyExternalIdentityResultMap") List<PartyExternalIdentity> findAll();
    @Select("SELECT " + COLUMNS + " FROM party_external_identity WHERE party_external_identity_id=#{partyExternalIdentityId}")
    @ResultMap("partyExternalIdentityResultMap") Optional<PartyExternalIdentity> findByPartyExternalIdentityId(Long partyExternalIdentityId);
    @Select("SELECT " + COLUMNS + " FROM party_external_identity WHERE transaction_platform_id=#{transactionPlatformId} AND external_party_id=#{externalPartyId}")
    @ResultMap("partyExternalIdentityResultMap") Optional<PartyExternalIdentity> findByPlatformAndExternalPartyId(@Param("transactionPlatformId") Integer transactionPlatformId, @Param("externalPartyId") String externalPartyId);
    @Insert("INSERT INTO party_external_identity (party_id,transaction_platform_id,external_party_id) VALUES (#{partyId},#{transactionPlatformId},#{externalPartyId})")
    @Options(useGeneratedKeys=true,keyProperty="partyExternalIdentityId") void insert(PartyExternalIdentity identity);
    @Insert("INSERT INTO party_external_identity (party_id,transaction_platform_id,external_party_id) VALUES (#{partyId},#{transactionPlatformId},#{externalPartyId}) ON DUPLICATE KEY UPDATE party_id=VALUES(party_id)")
    @Options(useGeneratedKeys=true,keyProperty="partyExternalIdentityId") void upsert(PartyExternalIdentity identity);
    @Delete("DELETE FROM party_external_identity WHERE party_external_identity_id=#{partyExternalIdentityId}") int delete(Long partyExternalIdentityId);
}
