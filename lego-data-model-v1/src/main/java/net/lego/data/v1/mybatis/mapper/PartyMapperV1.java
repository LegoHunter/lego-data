package net.lego.data.v1.mybatis.mapper;

import net.lego.data.v1.dto.Party;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

public interface PartyMapperV1 {
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
                   party_password, \
                   party_activation_date, \
                   party_active_indicator \
            FROM party \
            """)
    @ResultMap("partyResultMapV1")
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
                   party_password, \
                   party_activation_date, \
                   party_active_indicator \
            FROM party \
            WHERE party_id = #{partyId}\
            """)
    @ResultMap("partyResultMapV1")
    Optional<Party> findPartyById(Long partyId);
}
