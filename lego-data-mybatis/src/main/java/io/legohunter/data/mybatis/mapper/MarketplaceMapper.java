package io.legohunter.data.mybatis.mapper;

import io.legohunter.data.dto.Marketplace;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/** Marketplaces in the shared pricing schema (HOMELAB-31). Read-only: the deployer loads the rows. */
public interface MarketplaceMapper {
    String ALL_COLUMNS = """
            marketplace_code,
            display_name,
            base_url,
            default_currency_code,
            crawl_enabled,
            created_at
            """;

    @Select("SELECT " + ALL_COLUMNS + " FROM ${marketplacePricingSchema}.marketplace ORDER BY marketplace_code")
    @ResultMap("marketplaceResultMap")
    List<Marketplace> findAll();

    @Select("SELECT " + ALL_COLUMNS + " FROM ${marketplacePricingSchema}.marketplace WHERE marketplace_code = #{marketplaceCode}")
    @ResultMap("marketplaceResultMap")
    Optional<Marketplace> findByMarketplaceCode(String marketplaceCode);
}
