package net.lego.data.v2.dao;

import lombok.RequiredArgsConstructor;
import net.lego.data.v2.dto.Party;
import net.lego.data.v2.mybatis.mapper.PartyMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PartyDao {
    private final PartyMapper partyMapper;

    public void insert(final Party party) {
        partyMapper.insert(party);
    }

    public void migrate(final Party party) {
        partyMapper.migrate(party);
    }

    public void setAutoIncrementMode() {
        partyMapper.setAutoIncrementMode();
    }

    public void update(final Party party) {
        partyMapper.update(party);
    }

    public List<Party> findAll() {
        return partyMapper.findAll();
    }

    public Optional<Party> findPartyById(Long partyId) {
        return partyMapper.findPartyById(partyId);
    }

    public void decrementPartyId(Long partyId) {
        partyMapper.decrementPartyId(partyId);
    }
}
