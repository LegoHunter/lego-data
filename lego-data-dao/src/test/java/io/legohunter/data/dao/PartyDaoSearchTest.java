package io.legohunter.data.dao;

import io.legohunter.data.dto.Party;
import io.legohunter.data.dto.PartyExternalIdentity;
import io.legohunter.data.mybatis.mapper.PartyExternalIdentityMapper;
import io.legohunter.data.mybatis.mapper.PartyMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PartyDaoSearchTest {
    private final PartyMapper mapper = mock(PartyMapper.class);
    private final PartyDao dao = new PartyDao(mapper);

    @Test
    void searchTrimsCriteriaDropsBlanksAndEscapesLikeWildcards() {
        Party party = Party.builder().partyId(7L).build();
        when(mapper.search("50\\%\\_off\\\\", null, 1, "user_1", 25)).thenReturn(List.of(party));

        assertThat(dao.search("  50%_off\\ ", "   ", 1, " user_1 ", 25)).containsExactly(party);
    }

    @Test
    void searchWithoutCriteriaPassesNulls() {
        dao.search(null, null, null, null, 10);

        verify(mapper).search(null, null, null, null, 10);
    }

    @Test
    void findByEmailSkipsTheQueryForABlankEmail() {
        assertThat(dao.findByEmail(" ")).isEmpty();
        assertThat(dao.findByEmail(null)).isEmpty();

        verifyNoInteractions(mapper);
    }

    @Test
    void findByEmailTrimsTheEmail() {
        dao.findByEmail(" a@example.com ");

        verify(mapper).findByEmail("a@example.com");
    }

    @Test
    void findByNameTreatsAMissingPartAsEmpty() {
        dao.findByName(" Brick Shop ", null);

        verify(mapper).findByName("Brick Shop", "");
    }

    @Test
    void externalIdentitiesAreReadByParty() {
        PartyExternalIdentityMapper identityMapper = mock(PartyExternalIdentityMapper.class);
        PartyExternalIdentity identity = PartyExternalIdentity.builder().partyId(7L).build();
        when(identityMapper.findByPartyId(7L)).thenReturn(List.of(identity));

        assertThat(new PartyExternalIdentityDao(identityMapper).findByPartyId(7L)).containsExactly(identity);
    }
}
