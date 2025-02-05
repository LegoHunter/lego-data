package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.Party;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
}


