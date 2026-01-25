package net.lego.data.v1.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v1.dto.Party;
import net.lego.data.v1.mybatis.mapper.PartyMapperV1;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("partyDaoV1")
@RequiredArgsConstructor
public class PartyDao {
    private final PartyMapperV1 partyMapperV1;

    public List<Party> findAll() {
        return partyMapperV1.findAll();
    }

    Optional<Party> findPartyById(Long partyId) {
        return partyMapperV1.findPartyById(partyId);
    }
}
