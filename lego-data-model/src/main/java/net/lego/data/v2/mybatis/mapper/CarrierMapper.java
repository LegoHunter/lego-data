package net.lego.data.v2.mybatis.mapper;

import net.lego.data.v2.dto.Carrier;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

public interface CarrierMapper {
    @Insert("""
            INSERT INTO carrier (carrier_code, carrier_name, tracking_url_pattern) \
            VALUES (#{carrierCode}, #{carrierName}, #{trackingUrlPattern})\
            """)
    void insertCarrier(Carrier carrier);

    @Update("""
            UPDATE carrier SET \
            carrier_name = #{carrierName}, \
            tracking_url_pattern = #{trackingUrlPattern} \
            WHERE carrier_code = #{carrierCode} \
            """)
    void updateCarrier(Carrier carrier);

    @Select("""
            SELECT carrier_code, \
                   carrier_name, \
                   tracking_url_pattern \
            FROM carrier \
            """)
    @ResultMap("carrierResultMap")
    List<Carrier> findAll();

    @Select("""
            SELECT carrier_code, \
                   carrier_name, \
                   tracking_url_pattern \
            FROM carrier \
            WHERE carrier_code = #{carrierCode}\
            """)
    @ResultMap("carrierResultMap")
    Optional<Carrier> findCarrierByCode(String carrierCode);
}
