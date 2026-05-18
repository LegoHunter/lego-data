package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Party;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class PartyMapperTest extends MapperTestSupport {

    @Test
    void insertMigrateUpdateDecrementFindByIdAndFindAll() {
        Party fromParty = party("From");
        partyMapper.insert(fromParty);
        Party toParty = party("To");
        toParty.setPartyId(10L);
        partyMapper.migrate(toParty);

        toParty.setPartyCity("Updated City");
        partyMapper.update(toParty);

        assertThat(partyMapper.findPartyById(toParty.getPartyId()))
                .hasValueSatisfying(found -> assertThat(found.getPartyCity()).isEqualTo("Updated City"));

        partyMapper.decrementPartyId(10L);

        assertThat(partyMapper.findPartyById(9L)).isPresent();
        assertThat(partyMapper.findAll()).hasSize(2);
    }
}
