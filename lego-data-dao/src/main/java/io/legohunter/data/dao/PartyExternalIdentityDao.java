package io.legohunter.data.dao;

import io.legohunter.data.dto.PartyExternalIdentity;
import io.legohunter.data.mybatis.mapper.PartyExternalIdentityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component @RequiredArgsConstructor
public class PartyExternalIdentityDao {
    private final PartyExternalIdentityMapper mapper;
    public List<PartyExternalIdentity> findAll() { return mapper.findAll(); }
    public Optional<PartyExternalIdentity> findByPartyExternalIdentityId(Long partyExternalIdentityId) { return mapper.findByPartyExternalIdentityId(partyExternalIdentityId); }
    public Optional<PartyExternalIdentity> findByPlatformAndExternalPartyId(Integer platformId, String externalPartyId) { return mapper.findByPlatformAndExternalPartyId(platformId, externalPartyId); }
    public PartyExternalIdentity insert(PartyExternalIdentity identity) { mapper.insert(identity); return identity; }
    public PartyExternalIdentity upsert(PartyExternalIdentity identity) { mapper.upsert(identity); return identity; }
    public int delete(Long partyExternalIdentityId) { return mapper.delete(partyExternalIdentityId); }
}
