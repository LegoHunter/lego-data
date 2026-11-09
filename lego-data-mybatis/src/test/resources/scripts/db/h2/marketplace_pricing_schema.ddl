-- H2 version of the shared marketplace_pricing schema (HOMELAB-31).
-- Source of truth: lego-database-deployer db/marketplace-pricing/files/1.0.0-baseline.yaml and
-- 1.1.0-reference-data.yaml. Same tables, columns, nullability, defaults, keys and foreign keys.
-- MySQL TEXT is CLOB and DATETIME is TIMESTAMP here. crawl_target.last_market_snapshot_id has no
-- foreign key in MySQL either.
-- The mappers qualify these tables with ${marketplacePricingSchema}, which defaults to marketplace_pricing.

DROP SCHEMA IF EXISTS marketplace_pricing CASCADE;
CREATE SCHEMA marketplace_pricing;

CREATE TABLE marketplace_pricing.marketplace (
    marketplace_code VARCHAR(32) NOT NULL PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    base_url VARCHAR(500),
    default_currency_code VARCHAR(8) NOT NULL,
    crawl_enabled TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE marketplace_pricing.canonical_item (
    canonical_item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_type_code VARCHAR(8) NOT NULL,
    item_number VARCHAR(100) NOT NULL,
    item_name VARCHAR(250),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_canonical_item_type_number UNIQUE (item_type_code, item_number)
);

CREATE TABLE marketplace_pricing.marketplace_item (
    marketplace_item_id INT AUTO_INCREMENT PRIMARY KEY,
    marketplace_code VARCHAR(32) NOT NULL,
    canonical_item_id INT NOT NULL,
    marketplace_item_key VARCHAR(255),
    marketplace_unique_key VARCHAR(255),
    search_terms VARCHAR(500),
    match_method_code VARCHAR(32) NOT NULL,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_marketplace_item_marketplace_item UNIQUE (marketplace_code, canonical_item_id),
    CONSTRAINT fk_marketplace_item_marketplace1 FOREIGN KEY (marketplace_code)
        REFERENCES marketplace_pricing.marketplace (marketplace_code),
    CONSTRAINT fk_marketplace_item_canonical_item1 FOREIGN KEY (canonical_item_id)
        REFERENCES marketplace_pricing.canonical_item (canonical_item_id)
);

CREATE TABLE marketplace_pricing.want_list_item (
    environment_code VARCHAR(16) NOT NULL,
    canonical_item_id INT NOT NULL,
    condition_code VARCHAR(8) NOT NULL,
    completeness_code VARCHAR(8) NOT NULL,
    first_wanted_at TIMESTAMP NOT NULL,
    last_confirmed_at TIMESTAMP NOT NULL,
    PRIMARY KEY (environment_code, canonical_item_id, condition_code, completeness_code),
    CONSTRAINT fk_want_list_item_canonical_item1 FOREIGN KEY (canonical_item_id)
        REFERENCES marketplace_pricing.canonical_item (canonical_item_id)
);

CREATE TABLE marketplace_pricing.crawl_target (
    crawl_target_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    marketplace_code VARCHAR(32) NOT NULL,
    canonical_item_id INT NOT NULL,
    query_condition_code VARCHAR(8) NOT NULL,
    target_status_code VARCHAR(32) NOT NULL DEFAULT 'IDLE',
    next_due_at TIMESTAMP NOT NULL,
    claimed_at TIMESTAMP,
    claim_expires_at TIMESTAMP,
    claimed_by VARCHAR(128),
    last_attempt_at TIMESTAMP,
    last_success_at TIMESTAMP,
    last_market_snapshot_id BIGINT,
    consecutive_failure_count INT NOT NULL DEFAULT 0,
    last_error_message CLOB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_crawl_target_marketplace_item_condition UNIQUE (marketplace_code, canonical_item_id, query_condition_code),
    CONSTRAINT fk_crawl_target_marketplace1 FOREIGN KEY (marketplace_code)
        REFERENCES marketplace_pricing.marketplace (marketplace_code),
    CONSTRAINT fk_crawl_target_canonical_item1 FOREIGN KEY (canonical_item_id)
        REFERENCES marketplace_pricing.canonical_item (canonical_item_id)
);
CREATE INDEX marketplace_pricing.idx_crawl_target_status_due
    ON marketplace_pricing.crawl_target (target_status_code, next_due_at);

CREATE TABLE marketplace_pricing.market_snapshot (
    market_snapshot_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    crawl_target_id BIGINT,
    marketplace_code VARCHAR(32) NOT NULL,
    canonical_item_id INT NOT NULL,
    query_condition_code VARCHAR(8) NOT NULL,
    marketplace_item_key VARCHAR(255),
    marketplace_unique_key VARCHAR(255),
    request_url VARCHAR(1024),
    request_parameters CLOB,
    raw_payload_hash VARCHAR(64),
    listing_count INT NOT NULL,
    captured_at TIMESTAMP NOT NULL,
    source_environment_code VARCHAR(16),
    source_pricing_snapshot_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_market_snapshot_source UNIQUE (source_environment_code, source_pricing_snapshot_id),
    CONSTRAINT fk_market_snapshot_crawl_target1 FOREIGN KEY (crawl_target_id)
        REFERENCES marketplace_pricing.crawl_target (crawl_target_id),
    CONSTRAINT fk_market_snapshot_marketplace1 FOREIGN KEY (marketplace_code)
        REFERENCES marketplace_pricing.marketplace (marketplace_code),
    CONSTRAINT fk_market_snapshot_canonical_item1 FOREIGN KEY (canonical_item_id)
        REFERENCES marketplace_pricing.canonical_item (canonical_item_id)
);
CREATE INDEX marketplace_pricing.idx_market_snapshot_item_condition_captured
    ON marketplace_pricing.market_snapshot (canonical_item_id, query_condition_code, captured_at);

CREATE TABLE marketplace_pricing.market_listing (
    market_listing_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    market_snapshot_id BIGINT NOT NULL,
    external_listing_id VARCHAR(255),
    seller_key VARCHAR(255),
    seller_name VARCHAR(255),
    seller_country_code VARCHAR(8),
    seller_feedback_score INT,
    raw_condition_code VARCHAR(64),
    raw_completeness_code VARCHAR(64),
    condition_code VARCHAR(8),
    completeness_code VARCHAR(8),
    match_confidence DECIMAL(5,4),
    listing_format_code VARCHAR(32) NOT NULL DEFAULT 'FIXED_PRICE',
    quantity_available INT,
    item_price DECIMAL(12,2) NOT NULL,
    shipping_price DECIMAL(12,2),
    total_price DECIMAL(12,2),
    currency_code VARCHAR(8) NOT NULL,
    native_price DECIMAL(12,2),
    native_currency_code VARCHAR(8),
    description CLOB,
    source_listing_payload CLOB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_market_listing_market_snapshot1 FOREIGN KEY (market_snapshot_id)
        REFERENCES marketplace_pricing.market_snapshot (market_snapshot_id)
);

CREATE TABLE marketplace_pricing.crawl_attempt (
    crawl_attempt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    crawl_target_id BIGINT NOT NULL,
    started_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP,
    outcome_code VARCHAR(32) NOT NULL,
    error_message CLOB,
    market_snapshot_id BIGINT,
    CONSTRAINT fk_crawl_attempt_crawl_target1 FOREIGN KEY (crawl_target_id)
        REFERENCES marketplace_pricing.crawl_target (crawl_target_id),
    CONSTRAINT fk_crawl_attempt_market_snapshot1 FOREIGN KEY (market_snapshot_id)
        REFERENCES marketplace_pricing.market_snapshot (market_snapshot_id)
);
CREATE INDEX marketplace_pricing.idx_crawl_attempt_target_started
    ON marketplace_pricing.crawl_attempt (crawl_target_id, started_at);

-- Reference data (1.1.0-reference-data).
INSERT INTO marketplace_pricing.marketplace (marketplace_code, display_name, base_url, default_currency_code, crawl_enabled)
VALUES ('BRICKLINK', 'BrickLink', 'https://www.bricklink.com', 'USD', 1),
       ('EBAY', 'eBay', 'https://www.ebay.com', 'USD', 0);
