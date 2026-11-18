package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Party;
import io.legohunter.data.dto.PartyExternalIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@MapperIntegrationTest
class PartyMapperTest extends MapperTestSupport {
    @Autowired PartyExternalIdentityMapper partyExternalIdentityMapper;

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

    @Test
    void searchMatchesNameEmailAndExternalIdentityIgnoringCase() {
        Party alice = party("Alice");
        partyMapper.insert(alice);
        Party bob = party("Bob");
        bob.setPartyLastName(null);
        partyMapper.insert(bob);
        Party shop = party("Brick Shop");
        shop.setPartyLastName(null);
        shop.setPartyEmail(null);
        partyMapper.insert(shop);
        partyExternalIdentityMapper.insert(PartyExternalIdentity.builder()
                .partyId(shop.getPartyId()).transactionPlatformId(1).externalPartyId("BrickShopUser").build());
        partyExternalIdentityMapper.insert(PartyExternalIdentity.builder()
                .partyId(shop.getPartyId()).transactionPlatformId(2).externalPartyId("brickshop-ebay").build());

        assertThat(partyMapper.search("ALICE test", null, null, null, 10))
                .extracting(Party::getPartyId).containsExactly(alice.getPartyId());
        assertThat(partyMapper.search("b", null, null, null, 10))
                .extracting(Party::getPartyId).containsExactly(bob.getPartyId(), shop.getPartyId());
        assertThat(partyMapper.search(null, "BOB@EXAMPLE", null, null, 10))
                .extracting(Party::getPartyId).containsExactly(bob.getPartyId());
        assertThat(partyMapper.search(null, null, 1, "brickshopuser", 10))
                .extracting(Party::getPartyId).containsExactly(shop.getPartyId());
        assertThat(partyMapper.search(null, null, 2, "brickshopuser", 10)).isEmpty();
        assertThat(partyMapper.search(null, null, null, "brickshop-ebay", 10))
                .extracting(Party::getPartyId).containsExactly(shop.getPartyId());
        assertThat(partyMapper.search("shop", null, 2, null, 10))
                .extracting(Party::getPartyId).containsExactly(shop.getPartyId());
        assertThat(partyMapper.search("alice", null, 1, null, 10)).isEmpty();
        assertThat(partyMapper.search(null, null, null, null, 2))
                .extracting(Party::getPartyId).containsExactly(alice.getPartyId(), bob.getPartyId());
        assertThat(partyMapper.search("\\%", null, null, null, 10)).isEmpty();
    }

    @Test
    void findByEmailAndFindByNameMatchExactlyIgnoringCase() {
        Party alice = party("Alice");
        partyMapper.insert(alice);
        Party shop = party("Brick Shop");
        shop.setPartyLastName(null);
        partyMapper.insert(shop);

        assertThat(partyMapper.findByEmail("ALICE@example.com"))
                .extracting(Party::getPartyId).containsExactly(alice.getPartyId());
        assertThat(partyMapper.findByEmail("alice@example")).isEmpty();
        assertThat(partyMapper.findByName("alice", "TESTER"))
                .extracting(Party::getPartyId).containsExactly(alice.getPartyId());
        assertThat(partyMapper.findByName("alice", "")).isEmpty();
        assertThat(partyMapper.findByName("brick shop", ""))
                .extracting(Party::getPartyId).containsExactly(shop.getPartyId());
    }

    @Test
    void externalIdentitiesAreFoundByParty() {
        Party shop = party("Brick Shop");
        partyMapper.insert(shop);
        partyExternalIdentityMapper.insert(PartyExternalIdentity.builder()
                .partyId(shop.getPartyId()).transactionPlatformId(1).externalPartyId("BrickShopUser").build());

        assertThat(partyExternalIdentityMapper.findByPartyId(shop.getPartyId()))
                .extracting(PartyExternalIdentity::getExternalPartyId).containsExactly("BrickShopUser");
        assertThat(partyExternalIdentityMapper.findByPartyId(-1L)).isEmpty();
    }
}
