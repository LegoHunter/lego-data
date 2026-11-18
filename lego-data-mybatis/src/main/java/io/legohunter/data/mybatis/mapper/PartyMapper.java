package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Party;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

public interface PartyMapper {
    @Insert("""
            INSERT INTO party (party_first_name, party_middle_initial, party_last_name, party_address1, party_address2, party_city, party_state, party_postal_code, party_country_code, party_country, party_phone, party_email, party_type, party_activation_date) \
            VALUES (#{partyFirstName}, #{partyMiddleInitial}, #{partyLastName}, #{partyAddress1}, #{partyAddress2}, #{partyCity}, #{partyState}, #{partyPostalCode}, #{partyCountryCode}, #{partyCountry}, #{partyPhone}, #{partyEmail}, #{partyType}, #{partyActivationDate}) \
            """)
    @Options(useGeneratedKeys = true, keyProperty = "partyId")
    void insert(Party party);

    @Insert("""
            INSERT INTO party (party_id, party_first_name, party_middle_initial, party_last_name, party_address1, party_address2, party_city, party_state, party_postal_code, party_country_code, party_country, party_phone, party_email, party_type, party_activation_date) \
            VALUES (#{partyId}, #{partyFirstName}, #{partyMiddleInitial}, #{partyLastName}, #{partyAddress1}, #{partyAddress2}, #{partyCity}, #{partyState}, #{partyPostalCode}, #{partyCountryCode}, #{partyCountry}, #{partyPhone}, #{partyEmail}, #{partyType}, #{partyActivationDate}) \
            """)
    void migrate(Party party);

    @Update("""
            SET SESSION sql_mode='NO_AUTO_VALUE_ON_ZERO';
            """)
    void setAutoIncrementMode();

    @Update("""
            UPDATE party SET \
               party_id = #{partyId}, \
               party_first_name = #{partyFirstName}, \
               party_middle_initial = #{partyMiddleInitial}, \
               party_last_name = #{partyLastName}, \
               party_address1 =  #{partyAddress1}, \
               party_address2 = #{partyAddress2}, \
               party_city = #{partyCity}, \
               party_state = #{partyState}, \
               party_postal_code = #{partyPostalCode}, \
               party_country_code = #{partyCountryCode}, \
               party_country = #{partyCountry}, \
               party_phone = #{partyPhone}, \
               party_email = #{partyEmail}, \
               party_type = #{partyType}, \
               party_activation_date = #{partyActivationDate} \
            WHERE party_id = #{partyId}\
            """)
    void update(Party party);

    @Update("""
            UPDATE party SET \
               party_id = party_id - 1 \
            WHERE party_id = #{partyId}\
            """)
    void decrementPartyId(Long partyId);

    @Select("""
            SELECT party_id, \
                   party_first_name, \
                   party_middle_initial, \
                   party_last_name, \
                   party_address1, \
                   party_address2, \
                   party_city, \
                   party_state, \
                   party_postal_code, \
                   party_country_code, \
                   party_country, \
                   party_phone, \
                   party_email, \
                   party_type, \
                   party_activation_date \
            FROM party \
            """)
    @ResultMap("partyResultMap")
    List<Party> findAll();

    @Select("""
            SELECT party_id, \
                   party_first_name, \
                   party_middle_initial, \
                   party_last_name, \
                   party_address1, \
                   party_address2, \
                   party_city, \
                   party_state, \
                   party_postal_code, \
                   party_country_code, \
                   party_country, \
                   party_phone, \
                   party_email, \
                   party_type, \
                   party_activation_date \
            FROM party \
            WHERE party_id = #{partyId}\
            """)
    @ResultMap("partyResultMap")
    Optional<Party> findPartyById(Long partyId);

    /**
     * Finds parties by any mix of: part of the name, part of the email, and an identity on a transaction
     * platform (for example a BrickLink user name). A null criterion is not applied. Matching ignores case.
     * {@code name} and {@code email} are used inside a LIKE pattern, so the caller escapes LIKE wildcards.
     */
    @Select("""
            <script>
            SELECT DISTINCT p.party_id, p.party_first_name, p.party_middle_initial, p.party_last_name, p.party_address1, p.party_address2, p.party_city, p.party_state, p.party_postal_code, p.party_country_code, p.party_country, p.party_phone, p.party_email, p.party_type, p.party_activation_date
            FROM party p
            <if test="externalPartyId != null or transactionPlatformId != null">
            INNER JOIN party_external_identity pei ON pei.party_id = p.party_id
              <if test="externalPartyId != null">AND LOWER(pei.external_party_id) = LOWER(#{externalPartyId})</if>
              <if test="transactionPlatformId != null">AND pei.transaction_platform_id = #{transactionPlatformId}</if>
            </if>
            <where>
              <if test="name != null">
                LOWER(CONCAT(COALESCE(p.party_first_name, ''), ' ', COALESCE(p.party_last_name, ''))) LIKE CONCAT('%', LOWER(#{name}), '%')
              </if>
              <if test="email != null">
                AND LOWER(p.party_email) LIKE CONCAT('%', LOWER(#{email}), '%')
              </if>
            </where>
            ORDER BY p.party_id
            LIMIT #{limit}
            </script>
            """)
    @ResultMap("partyResultMap")
    List<Party> search(
            @Param("name") String name,
            @Param("email") String email,
            @Param("transactionPlatformId") Integer transactionPlatformId,
            @Param("externalPartyId") String externalPartyId,
            @Param("limit") int limit
    );

    /** Parties whose email is exactly this one, ignoring case. */
    @Select("""
            SELECT p.party_id, p.party_first_name, p.party_middle_initial, p.party_last_name, p.party_address1, p.party_address2, p.party_city, p.party_state, p.party_postal_code, p.party_country_code, p.party_country, p.party_phone, p.party_email, p.party_type, p.party_activation_date
            FROM party p
            WHERE LOWER(p.party_email) = LOWER(#{email})
            ORDER BY p.party_id
            """)
    @ResultMap("partyResultMap")
    List<Party> findByEmail(@Param("email") String email);

    /**
     * Parties with exactly this first and last name, ignoring case and outer spaces. A missing name part in
     * the table counts as empty, so pass an empty string (not null) for a part the party does not have.
     */
    @Select("""
            SELECT p.party_id, p.party_first_name, p.party_middle_initial, p.party_last_name, p.party_address1, p.party_address2, p.party_city, p.party_state, p.party_postal_code, p.party_country_code, p.party_country, p.party_phone, p.party_email, p.party_type, p.party_activation_date
            FROM party p
            WHERE LOWER(TRIM(COALESCE(p.party_first_name, ''))) = LOWER(#{firstName})
              AND LOWER(TRIM(COALESCE(p.party_last_name, ''))) = LOWER(#{lastName})
            ORDER BY p.party_id
            """)
    @ResultMap("partyResultMap")
    List<Party> findByName(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
