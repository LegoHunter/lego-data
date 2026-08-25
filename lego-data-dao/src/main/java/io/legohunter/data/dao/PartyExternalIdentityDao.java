package io.legohunter.data.dao;

import io.legohunter.data.dto.PartyExternalIdentity;
import io.legohunter.data.mybatis.mapper.PartyExternalIdentityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component @RequiredArgsConstructor
public class PartyExternalIdentityDao {
    private final PartyExternalIdentityMapper mapper;
    public Optional<PartyExternalIdentity> findByPlatformAndExternalPartyId(Integer platformId, String externalPartyId) { return mapper.findByPlatformAndExternalPartyId(platformId, externalPartyId); }
    public void insert(PartyExternalIdentity identity) { mapper.insert(identity); }
}
