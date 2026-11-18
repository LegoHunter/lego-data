package io.legohunter.data.dao;

import lombok.RequiredArgsConstructor;
import io.legohunter.data.dto.Party;
import io.legohunter.data.mybatis.mapper.PartyMapper;
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

    /**
     * Finds parties by any mix of: part of the name, part of the email, and an identity on a transaction
     * platform. A null or blank criterion is not applied; with none, the first {@code limit} parties are
     * returned. Matching ignores case.
     */
    public List<Party> search(String name, String email, Integer transactionPlatformId, String externalPartyId, int limit) {
        return partyMapper.search(
                likeFragment(name),
                likeFragment(email),
                transactionPlatformId,
                trimToNull(externalPartyId),
                limit
        );
    }

    /** Parties whose email is exactly this one, ignoring case. Empty for a null or blank email. */
    public List<Party> findByEmail(String email) {
        String trimmed = trimToNull(email);
        return trimmed == null ? List.of() : partyMapper.findByEmail(trimmed);
    }

    /** Parties with exactly this first and last name, ignoring case; a missing part matches a missing part. */
    public List<Party> findByName(String firstName, String lastName) {
        return partyMapper.findByName(trimToEmpty(firstName), trimToEmpty(lastName));
    }

    private static String likeFragment(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        return trimmed.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
