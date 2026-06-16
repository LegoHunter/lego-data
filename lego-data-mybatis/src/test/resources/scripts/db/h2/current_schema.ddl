DROP TABLE IF EXISTS marketplace_order_transaction_link;
DROP TABLE IF EXISTS marketplace_order_payload;
DROP TABLE IF EXISTS marketplace_order_item;
DROP TABLE IF EXISTS marketplace_order;
DROP TABLE IF EXISTS marketplace_order_sync_run;
DROP TABLE IF EXISTS ebay_listing_item_specific;
DROP TABLE IF EXISTS ebay_marketplace_listing;
DROP TABLE IF EXISTS bricklink_marketplace_listing;
DROP TABLE IF EXISTS marketplace_listing;
DROP TABLE IF EXISTS item_inventory_external_catalog_item;
DROP TABLE IF EXISTS external_catalog_item_category;
DROP TABLE IF EXISTS external_service_capability;
DROP TABLE IF EXISTS external_service_item;
DROP TABLE IF EXISTS bricklink_item_inventory;
DROP TABLE IF EXISTS external_item_inventory;
DROP TABLE IF EXISTS external_image_album_image;
DROP TABLE IF EXISTS external_image;
DROP TABLE IF EXISTS external_image_album;
DROP TABLE IF EXISTS item_inventory_photo;
DROP TABLE IF EXISTS transaction_item_cost;
DROP TABLE IF EXISTS transaction_cost;
DROP TABLE IF EXISTS transaction_item;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS party;
DROP TABLE IF EXISTS external_catalog_item;
DROP TABLE IF EXISTS external_category;
DROP TABLE IF EXISTS external_item;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS external_service;
DROP TABLE IF EXISTS external_service_type;
DROP TABLE IF EXISTS item_inventory;
DROP TABLE IF EXISTS item_inventory_sale_intent;
DROP TABLE IF EXISTS item_inventory_state;
DROP TABLE IF EXISTS inventory_index;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS carrier;
DROP TABLE IF EXISTS `condition`;
DROP TABLE IF EXISTS cost_type;
DROP TABLE IF EXISTS payment_platform;
DROP TABLE IF EXISTS transaction_platform;
DROP TABLE IF EXISTS transaction_type;

CREATE TABLE carrier (
    carrier_code VARCHAR(32) PRIMARY KEY,
    carrier_name VARCHAR(255),
    tracking_url_pattern VARCHAR(1024)
);

CREATE TABLE `condition` (
    condition_id INT PRIMARY KEY,
    condition_code VARCHAR(32) UNIQUE,
    condition_description VARCHAR(255),
    condition_text VARCHAR(1024)
);

CREATE TABLE cost_type (
    cost_type_code VARCHAR(32) PRIMARY KEY,
    cost_type_name VARCHAR(255),
    cost_type_description VARCHAR(1024)
);

CREATE TABLE item_inventory_state (
    inventory_state_code VARCHAR(30) PRIMARY KEY,
    inventory_state_name VARCHAR(100) NOT NULL,
    inventory_state_description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT
);

CREATE TABLE item_inventory_sale_intent (
    sale_intent_code VARCHAR(30) PRIMARY KEY,
    sale_intent_name VARCHAR(100) NOT NULL,
    sale_intent_description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT
);

CREATE TABLE transaction_type (
    transaction_type_code VARCHAR(32) PRIMARY KEY,
    transaction_type_description VARCHAR(1024),
    conversion_factor INT
);

CREATE TABLE transaction_platform (
    transaction_platform_id INT PRIMARY KEY,
    transaction_platform_name VARCHAR(255) UNIQUE
);

CREATE TABLE payment_platform (
    payment_platform_id INT PRIMARY KEY,
    payment_platform_name VARCHAR(255) UNIQUE,
    payment_platform_url VARCHAR(1024)
);

CREATE TABLE external_service_type (
    external_service_type_id INT PRIMARY KEY,
    external_service_type_name VARCHAR(255) UNIQUE,
    external_service_type_description VARCHAR(1024)
);

CREATE TABLE external_service (
    external_service_id INT PRIMARY KEY,
    service_code VARCHAR(64) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    service_url VARCHAR(1024),
    external_service_type_id INT NOT NULL
);

CREATE TABLE external_service_capability (
    external_service_id INT NOT NULL,
    capability_code VARCHAR(64) NOT NULL,
    PRIMARY KEY (external_service_id, capability_code)
);

CREATE TABLE external_category (
    external_category_id INT AUTO_INCREMENT PRIMARY KEY,
    external_service_id INT NOT NULL,
    external_category_key VARCHAR(255) NOT NULL,
    category_name VARCHAR(255),
    parent_external_category_id INT,
    UNIQUE (external_service_id, external_category_key)
);

CREATE TABLE external_catalog_item (
    external_catalog_item_id INT AUTO_INCREMENT PRIMARY KEY,
    external_service_id INT NOT NULL,
    external_item_key VARCHAR(255) NOT NULL,
    external_unique_key VARCHAR(255),
    item_name VARCHAR(1024),
    item_type_code VARCHAR(64),
    item_url VARCHAR(1024),
    year_released INT,
    UNIQUE (external_service_id, external_item_key),
    UNIQUE (external_service_id, external_unique_key)
);

CREATE TABLE external_catalog_item_category (
    external_catalog_item_id INT NOT NULL,
    external_category_id INT NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (external_catalog_item_id, external_category_id)
);

CREATE TABLE item_inventory (
    item_inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(64) UNIQUE,
    box_number INT,
    purchase_price DECIMAL(9,2),
    description VARCHAR(2048),
    active BOOLEAN,
    for_sale BOOLEAN,
    new_or_used VARCHAR(32),
    completeness VARCHAR(64),
    item_condition_id INT,
    box_condition_id INT,
    instructions_condition_id INT,
    sealed BOOLEAN,
    built_once BOOLEAN
);

CREATE TABLE item_inventory_external_catalog_item (
    item_inventory_id INT NOT NULL,
    external_catalog_item_id INT NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (item_inventory_id, external_catalog_item_id)
);

CREATE TABLE marketplace_listing (
    marketplace_listing_id INT AUTO_INCREMENT PRIMARY KEY,
    item_inventory_id INT NOT NULL,
    listing_external_service_id INT NOT NULL,
    external_catalog_item_id INT,
    external_listing_id VARCHAR(255),
    external_listing_url VARCHAR(1024),
    listing_status_code VARCHAR(64),
    title VARCHAR(1024),
    description CLOB,
    private_notes CLOB,
    unit_price DECIMAL(12,2),
    currency_code VARCHAR(8),
    fixed_price BOOLEAN,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP,
    ended_at TIMESTAMP,
    last_synchronized_at TIMESTAMP,
    UNIQUE (listing_external_service_id, external_listing_id)
);

CREATE TABLE bricklink_marketplace_listing (
    marketplace_listing_id INT PRIMARY KEY,
    bricklink_inventory_id INT UNIQUE,
    bricklink_inventory_status_code VARCHAR(64),
    bricklink_date_created TIMESTAMP,
    color_id INT,
    color_name VARCHAR(255),
    bind_id INT,
    bulk INT,
    is_retain BOOLEAN,
    is_stock_room BOOLEAN,
    stock_room_id VARCHAR(64),
    sale_rate INT,
    tier_quantity1 INT,
    tier_price1 DECIMAL(12,2),
    tier_quantity2 INT,
    tier_price2 DECIMAL(12,2),
    tier_quantity3 INT,
    tier_price3 DECIMAL(12,2),
    my_weight DECIMAL(10,4),
    remarks CLOB,
    last_remote_quantity INT
);

CREATE TABLE ebay_marketplace_listing (
    marketplace_listing_id INT PRIMARY KEY,
    ebay_item_id VARCHAR(255) UNIQUE,
    ebay_category_id VARCHAR(255),
    condition_id VARCHAR(255),
    condition_descriptor_fields CLOB,
    listing_format VARCHAR(64),
    duration VARCHAR(64),
    shipping_policy_id VARCHAR(255),
    payment_policy_id VARCHAR(255),
    return_policy_id VARCHAR(255)
);

CREATE TABLE ebay_listing_item_specific (
    marketplace_listing_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    `value` VARCHAR(1024) NOT NULL,
    PRIMARY KEY (marketplace_listing_id, name, `value`)
);

CREATE TABLE marketplace_order_sync_run (
    marketplace_order_sync_run_id INT AUTO_INCREMENT PRIMARY KEY,
    marketplace_code VARCHAR(30) NOT NULL,
    sync_job_name VARCHAR(100),
    sync_direction VARCHAR(20) NOT NULL,
    sync_status_code VARCHAR(30) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    orders_discovered INT NOT NULL DEFAULT 0,
    orders_fetched INT NOT NULL DEFAULT 0,
    orders_failed INT NOT NULL DEFAULT 0,
    error_message CLOB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE marketplace_order (
    marketplace_order_id INT AUTO_INCREMENT PRIMARY KEY,
    last_sync_run_id INT,
    marketplace_code VARCHAR(30) NOT NULL,
    external_order_id VARCHAR(100) NOT NULL,
    order_direction VARCHAR(20) NOT NULL,
    external_status_code VARCHAR(50) NOT NULL,
    ordered_at TIMESTAMP,
    status_changed_at TIMESTAMP,
    buyer_display_name VARCHAR(150),
    buyer_email VARCHAR(255),
    payment_status_code VARCHAR(50),
    payment_method VARCHAR(100),
    payment_currency_code CHAR(3),
    paid_at TIMESTAMP,
    shipping_method VARCHAR(150),
    shipping_method_id VARCHAR(100),
    shipping_address_present BOOLEAN DEFAULT FALSE,
    tracking_present BOOLEAN DEFAULT FALSE,
    subtotal_amount DECIMAL(10,2),
    shipping_amount DECIMAL(10,2),
    grand_total_amount DECIMAL(10,2),
    currency_code CHAR(3),
    display_currency_code CHAR(3),
    total_count INT,
    unique_count INT,
    total_weight DECIMAL(10,3),
    is_invoiced BOOLEAN,
    is_filed BOOLEAN,
    sent_drive_thru BOOLEAN,
    require_insurance BOOLEAN,
    payload_hash VARCHAR(64),
    last_seen_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_marketplace_order_source_order UNIQUE (marketplace_code, external_order_id)
);

CREATE TABLE marketplace_order_item (
    marketplace_order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    marketplace_order_id INT NOT NULL,
    marketplace_listing_id INT,
    item_inventory_id INT,
    external_order_item_id VARCHAR(100),
    external_inventory_id VARCHAR(100),
    external_item_no VARCHAR(100),
    external_item_type VARCHAR(30),
    color_id INT,
    color_name VARCHAR(100),
    quantity INT NOT NULL DEFAULT 0,
    condition_code VARCHAR(10),
    completeness_code VARCHAR(10),
    unit_price DECIMAL(10,2),
    final_unit_price DECIMAL(10,2),
    currency_code CHAR(3),
    item_weight DECIMAL(10,3),
    remarks CLOB,
    description CLOB,
    payload_hash VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE marketplace_order_payload (
    marketplace_order_payload_id INT AUTO_INCREMENT PRIMARY KEY,
    marketplace_order_id INT NOT NULL,
    marketplace_order_sync_run_id INT,
    payload_type_code VARCHAR(50) NOT NULL,
    payload_hash VARCHAR(64),
    payload_json CLOB,
    captured_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE marketplace_order_transaction_link (
    marketplace_order_transaction_link_id INT AUTO_INCREMENT PRIMARY KEY,
    marketplace_order_id INT NOT NULL,
    transaction_id BIGINT NOT NULL,
    marketplace_order_item_id INT,
    transaction_item_id BIGINT,
    link_type_code VARCHAR(30) NOT NULL,
    link_status_code VARCHAR(30) NOT NULL,
    linked_at TIMESTAMP NOT NULL,
    unlinked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventory_index (
    box_id INT NOT NULL,
    box_index INT NOT NULL,
    item_number VARCHAR(255),
    box_name VARCHAR(255),
    box_number VARCHAR(255),
    sealed VARCHAR(32),
    quantity INT,
    description VARCHAR(2048),
    active BOOLEAN,
    moved_to_box_id INT,
    PRIMARY KEY (box_id, box_index)
);

CREATE TABLE item (
    item_number VARCHAR(255) PRIMARY KEY
);

CREATE TABLE item_inventory_photo (
    item_inventory_photo_id INT AUTO_INCREMENT PRIMARY KEY,
    item_inventory_id INT,
    s3_bucket VARCHAR(255),
    s3_key VARCHAR(1024),
    md5 VARCHAR(64) UNIQUE,
    metadata_hash VARCHAR(64),
    file_name VARCHAR(255),
    file_size BIGINT,
    is_primary BOOLEAN,
    caption VARCHAR(2048),
    status VARCHAR(32),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    uploaded_at TIMESTAMP,
    CONSTRAINT uq_item_service_key UNIQUE (s3_key)
);

CREATE TABLE external_image_album (
    external_image_album_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_service_id INT NOT NULL,
    item_inventory_id INT NOT NULL,
    external_album_id VARCHAR(255),
    title VARCHAR(500) NOT NULL,
    album_url VARCHAR(1024),
    short_url VARCHAR(1024),
    sync_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    error_message CLOB,
    last_synced_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (external_service_id, item_inventory_id),
    UNIQUE (external_service_id, external_album_id)
);

CREATE TABLE external_image (
    external_image_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_service_id INT NOT NULL,
    item_inventory_photo_id INT NOT NULL,
    external_service_image_id VARCHAR(255),
    title VARCHAR(500) NOT NULL,
    image_url VARCHAR(1024),
    md5_at_upload CHAR(32) NOT NULL,
    metadata_hash_at_sync CHAR(64),
    sync_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    error_message CLOB,
    uploaded_at TIMESTAMP,
    last_synced_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (external_service_id, item_inventory_photo_id),
    UNIQUE (external_service_id, external_service_image_id)
);

CREATE TABLE external_image_album_image (
    external_image_album_id BIGINT NOT NULL,
    external_image_id BIGINT NOT NULL,
    sort_order INT,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (external_image_album_id, external_image_id)
);

CREATE INDEX idx_item_inventory_photo_status_inventory
    ON item_inventory_photo (status, item_inventory_id);
CREATE INDEX idx_external_image_service_photo_status_metadata
    ON external_image (external_service_id, item_inventory_photo_id, sync_status, metadata_hash_at_sync);
CREATE INDEX idx_external_image_album_service_inventory_status
    ON external_image_album (external_service_id, item_inventory_id, sync_status);
CREATE INDEX idx_marketplace_listing_inventory
    ON marketplace_listing (item_inventory_id);
CREATE INDEX idx_marketplace_listing_catalog_item
    ON marketplace_listing (external_catalog_item_id);
CREATE INDEX idx_marketplace_order_sync_run_marketplace_status
    ON marketplace_order_sync_run (marketplace_code, sync_status_code);
CREATE INDEX idx_marketplace_order_sync_run_started_at
    ON marketplace_order_sync_run (started_at);
CREATE INDEX idx_marketplace_order_status
    ON marketplace_order (marketplace_code, external_status_code);
CREATE INDEX idx_marketplace_order_last_sync_run
    ON marketplace_order (last_sync_run_id);
CREATE INDEX idx_marketplace_order_item_order
    ON marketplace_order_item (marketplace_order_id);
CREATE INDEX idx_marketplace_order_item_listing
    ON marketplace_order_item (marketplace_listing_id);
CREATE INDEX idx_marketplace_order_item_inventory
    ON marketplace_order_item (item_inventory_id);
CREATE INDEX idx_marketplace_order_item_external_inventory
    ON marketplace_order_item (external_inventory_id);
CREATE INDEX idx_marketplace_order_payload_order
    ON marketplace_order_payload (marketplace_order_id);
CREATE INDEX idx_marketplace_order_payload_sync_run
    ON marketplace_order_payload (marketplace_order_sync_run_id);
CREATE INDEX idx_marketplace_order_payload_type_hash
    ON marketplace_order_payload (payload_type_code, payload_hash);
CREATE INDEX idx_marketplace_order_transaction_link_order
    ON marketplace_order_transaction_link (marketplace_order_id);
CREATE INDEX idx_marketplace_order_transaction_link_transaction
    ON marketplace_order_transaction_link (transaction_id);
CREATE INDEX idx_marketplace_order_transaction_link_order_item
    ON marketplace_order_transaction_link (marketplace_order_item_id);
CREATE INDEX idx_marketplace_order_transaction_link_transaction_item
    ON marketplace_order_transaction_link (transaction_item_id);
CREATE INDEX idx_marketplace_order_transaction_link_type_status
    ON marketplace_order_transaction_link (link_type_code, link_status_code);

CREATE TABLE party (
    party_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    party_first_name VARCHAR(255),
    party_middle_initial VARCHAR(8),
    party_last_name VARCHAR(255),
    party_address1 VARCHAR(255),
    party_address2 VARCHAR(255),
    party_city VARCHAR(255),
    party_state VARCHAR(64),
    party_postal_code VARCHAR(64),
    party_country_code VARCHAR(8),
    party_country VARCHAR(255),
    party_phone VARCHAR(64),
    party_email VARCHAR(255),
    party_type VARCHAR(64),
    party_activation_date TIMESTAMP
);

CREATE TABLE transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_date TIMESTAMP,
    notes VARCHAR(2048),
    from_party_id BIGINT,
    to_party_id BIGINT,
    transaction_platform_id INT,
    transaction_order_id VARCHAR(255)
);

CREATE TABLE transaction_item (
    transaction_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id BIGINT,
    transaction_type_code VARCHAR(32),
    notes VARCHAR(2048),
    item_inventory_id INT
);

CREATE TABLE transaction_cost (
    transaction_cost_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id BIGINT,
    cost_type_code VARCHAR(32),
    currency_code VARCHAR(8),
    amount DOUBLE,
    notes VARCHAR(2048)
);

CREATE TABLE transaction_item_cost (
    transaction_item_cost_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_item_id BIGINT,
    cost_type_code VARCHAR(32),
    currency_code VARCHAR(8),
    amount DOUBLE,
    notes VARCHAR(2048)
);

ALTER TABLE external_service
    ADD CONSTRAINT fk_external_service_external_service_type1
    FOREIGN KEY (external_service_type_id) REFERENCES external_service_type (external_service_type_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_service_capability
    ADD CONSTRAINT fk_external_service_capability_external_service1
    FOREIGN KEY (external_service_id) REFERENCES external_service (external_service_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_category
    ADD CONSTRAINT fk_external_category_external_service1
    FOREIGN KEY (external_service_id) REFERENCES external_service (external_service_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_category
    ADD CONSTRAINT fk_external_category_parent1
    FOREIGN KEY (parent_external_category_id) REFERENCES external_category (external_category_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_catalog_item
    ADD CONSTRAINT fk_external_catalog_item_external_service1
    FOREIGN KEY (external_service_id) REFERENCES external_service (external_service_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_catalog_item_category
    ADD CONSTRAINT fk_external_catalog_item_category_item1
    FOREIGN KEY (external_catalog_item_id) REFERENCES external_catalog_item (external_catalog_item_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_catalog_item_category
    ADD CONSTRAINT fk_external_catalog_item_category_category1
    FOREIGN KEY (external_category_id) REFERENCES external_category (external_category_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE item_inventory
    ADD CONSTRAINT fk_item_inventory_condition1
    FOREIGN KEY (item_condition_id) REFERENCES `condition` (condition_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE item_inventory
    ADD CONSTRAINT fk_item_inventory_condition2
    FOREIGN KEY (instructions_condition_id) REFERENCES `condition` (condition_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE item_inventory
    ADD CONSTRAINT fk_item_inventory_condition3
    FOREIGN KEY (box_condition_id) REFERENCES `condition` (condition_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE item_inventory_external_catalog_item
    ADD CONSTRAINT fk_item_inventory_external_catalog_item_inventory1
    FOREIGN KEY (item_inventory_id) REFERENCES item_inventory (item_inventory_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE item_inventory_external_catalog_item
    ADD CONSTRAINT fk_item_inventory_external_catalog_item_catalog_item1
    FOREIGN KEY (external_catalog_item_id) REFERENCES external_catalog_item (external_catalog_item_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_listing
    ADD CONSTRAINT fk_marketplace_listing_item_inventory1
    FOREIGN KEY (item_inventory_id) REFERENCES item_inventory (item_inventory_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_listing
    ADD CONSTRAINT fk_marketplace_listing_external_service1
    FOREIGN KEY (listing_external_service_id) REFERENCES external_service (external_service_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_listing
    ADD CONSTRAINT fk_marketplace_listing_external_catalog_item1
    FOREIGN KEY (external_catalog_item_id) REFERENCES external_catalog_item (external_catalog_item_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE bricklink_marketplace_listing
    ADD CONSTRAINT fk_bricklink_marketplace_listing_marketplace_listing1
    FOREIGN KEY (marketplace_listing_id) REFERENCES marketplace_listing (marketplace_listing_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ebay_marketplace_listing
    ADD CONSTRAINT fk_ebay_marketplace_listing_marketplace_listing1
    FOREIGN KEY (marketplace_listing_id) REFERENCES marketplace_listing (marketplace_listing_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ebay_listing_item_specific
    ADD CONSTRAINT fk_ebay_listing_item_specific_marketplace_listing1
    FOREIGN KEY (marketplace_listing_id) REFERENCES marketplace_listing (marketplace_listing_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_order
    ADD CONSTRAINT fk_marketplace_order_sync_run1
    FOREIGN KEY (last_sync_run_id) REFERENCES marketplace_order_sync_run (marketplace_order_sync_run_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_order_item
    ADD CONSTRAINT fk_marketplace_order_item_order1
    FOREIGN KEY (marketplace_order_id) REFERENCES marketplace_order (marketplace_order_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_order_item
    ADD CONSTRAINT fk_marketplace_order_item_marketplace_listing1
    FOREIGN KEY (marketplace_listing_id) REFERENCES marketplace_listing (marketplace_listing_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_order_item
    ADD CONSTRAINT fk_marketplace_order_item_item_inventory1
    FOREIGN KEY (item_inventory_id) REFERENCES item_inventory (item_inventory_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_order_payload
    ADD CONSTRAINT fk_marketplace_order_payload_order1
    FOREIGN KEY (marketplace_order_id) REFERENCES marketplace_order (marketplace_order_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_order_payload
    ADD CONSTRAINT fk_marketplace_order_payload_sync_run1
    FOREIGN KEY (marketplace_order_sync_run_id) REFERENCES marketplace_order_sync_run (marketplace_order_sync_run_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE item_inventory_photo
    ADD CONSTRAINT fk_item_inventory_photo_item_inventory1
    FOREIGN KEY (item_inventory_id) REFERENCES item_inventory (item_inventory_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_image_album
    ADD CONSTRAINT fk_external_image_album_external_service1
    FOREIGN KEY (external_service_id) REFERENCES external_service (external_service_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_image_album
    ADD CONSTRAINT fk_external_image_album_item_inventory1
    FOREIGN KEY (item_inventory_id) REFERENCES item_inventory (item_inventory_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_image
    ADD CONSTRAINT fk_external_image_external_service1
    FOREIGN KEY (external_service_id) REFERENCES external_service (external_service_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_image
    ADD CONSTRAINT fk_external_image_item_inventory_photo1
    FOREIGN KEY (item_inventory_photo_id) REFERENCES item_inventory_photo (item_inventory_photo_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_image_album_image
    ADD CONSTRAINT fk_external_image_album_image_album1
    FOREIGN KEY (external_image_album_id) REFERENCES external_image_album (external_image_album_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_image_album_image
    ADD CONSTRAINT fk_external_image_album_image_image1
    FOREIGN KEY (external_image_id) REFERENCES external_image (external_image_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_party1
    FOREIGN KEY (from_party_id) REFERENCES party (party_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_party2
    FOREIGN KEY (to_party_id) REFERENCES party (party_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_transaction_platform1
    FOREIGN KEY (transaction_platform_id) REFERENCES transaction_platform (transaction_platform_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE transaction_item
    ADD CONSTRAINT fk_transaction_item_transactions1
    FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE transaction_item
    ADD CONSTRAINT fk_transaction_item_transaction_type1
    FOREIGN KEY (transaction_type_code) REFERENCES transaction_type (transaction_type_code)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE transaction_item
    ADD CONSTRAINT fk_transaction_item_item_inventory1
    FOREIGN KEY (item_inventory_id) REFERENCES item_inventory (item_inventory_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_order_transaction_link
    ADD CONSTRAINT fk_marketplace_order_transaction_link_order1
    FOREIGN KEY (marketplace_order_id) REFERENCES marketplace_order (marketplace_order_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_order_transaction_link
    ADD CONSTRAINT fk_marketplace_order_transaction_link_transaction1
    FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_order_transaction_link
    ADD CONSTRAINT fk_marketplace_order_transaction_link_order_item1
    FOREIGN KEY (marketplace_order_item_id) REFERENCES marketplace_order_item (marketplace_order_item_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE marketplace_order_transaction_link
    ADD CONSTRAINT fk_marketplace_order_transaction_link_transaction_item1
    FOREIGN KEY (transaction_item_id) REFERENCES transaction_item (transaction_item_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE transaction_cost
    ADD CONSTRAINT fk_transaction_cost_transactions1
    FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE transaction_cost
    ADD CONSTRAINT fk_transaction_cost_cost_type10
    FOREIGN KEY (cost_type_code) REFERENCES cost_type (cost_type_code)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE transaction_item_cost
    ADD CONSTRAINT fk_transaction_item_cost_transaction_item1
    FOREIGN KEY (transaction_item_id) REFERENCES transaction_item (transaction_item_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE transaction_item_cost
    ADD CONSTRAINT fk_transaction_cost_cost_type1
    FOREIGN KEY (cost_type_code) REFERENCES cost_type (cost_type_code)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

