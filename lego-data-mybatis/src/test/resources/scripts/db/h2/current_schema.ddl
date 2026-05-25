DROP TABLE IF EXISTS bricklink_item_inventory;
DROP TABLE IF EXISTS external_item_inventory;
DROP TABLE IF EXISTS external_service_item;
DROP TABLE IF EXISTS external_image_album_image;
DROP TABLE IF EXISTS external_image;
DROP TABLE IF EXISTS external_image_album;
DROP TABLE IF EXISTS item_inventory_photo;
DROP TABLE IF EXISTS transaction_item_cost;
DROP TABLE IF EXISTS transaction_cost;
DROP TABLE IF EXISTS transaction_item;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS party;
DROP TABLE IF EXISTS external_item;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS external_service;
DROP TABLE IF EXISTS external_service_type;
DROP TABLE IF EXISTS item_inventory;
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
    external_service_name VARCHAR(255) UNIQUE,
    external_service_url VARCHAR(1024),
    external_service_type_id INT
);

CREATE TABLE category (
    external_service_id INT NOT NULL,
    external_category_id INT NOT NULL,
    category_name VARCHAR(255),
    parent_id INT,
    PRIMARY KEY (external_service_id, external_category_id)
);

CREATE TABLE external_item (
    external_item_id INT AUTO_INCREMENT PRIMARY KEY,
    external_number VARCHAR(255),
    external_unique_id BIGINT,
    external_name VARCHAR(1024),
    external_item_type VARCHAR(64),
    external_url VARCHAR(1024),
    external_category_id INT,
    external_year_released INT,
    external_service_id INT,
    UNIQUE (external_service_id, external_number)
);

CREATE TABLE item_inventory (
    item_inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(64) UNIQUE,
    box_number INT,
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

CREATE TABLE external_service_item (
    external_item_id INT NOT NULL,
    item_inventory_id INT NOT NULL,
    PRIMARY KEY (external_item_id, item_inventory_id)
);

CREATE TABLE external_item_inventory (
    external_item_id INT NOT NULL,
    item_inventory_id INT NOT NULL,
    fixed_price BOOLEAN,
    order_id INT,
    extended_description VARCHAR(4096),
    extra_description VARCHAR(4096),
    internal_comments VARCHAR(4096),
    update_timestamp TIMESTAMP,
    last_synchronized_timestamp TIMESTAMP,
    PRIMARY KEY (external_item_id, item_inventory_id)
);

CREATE TABLE bricklink_item_inventory (
    bricklink_item_inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    external_item_id INT,
    item_inventory_id INT,
    inventory_id BIGINT,
    item_type VARCHAR(64),
    color_id INT,
    color_name VARCHAR(255),
    quantity INT,
    unit_price DOUBLE,
    bind_id INT,
    description VARCHAR(4096),
    remarks VARCHAR(4096),
    bulk INT,
    is_retain BOOLEAN,
    is_stock_room BOOLEAN,
    stock_room_id VARCHAR(64),
    date_created TIMESTAMP,
    my_cost DOUBLE,
    sale_rate INT,
    tier_quantity1 INT,
    tier_quantity2 INT,
    tier_quantity3 INT,
    tier_price1 DOUBLE,
    tier_price2 DOUBLE,
    tier_price3 DOUBLE,
    my_weight DOUBLE
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

ALTER TABLE category
    ADD CONSTRAINT fk_category_external_service1
    FOREIGN KEY (external_service_id) REFERENCES external_service (external_service_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_item
    ADD CONSTRAINT fk_external_item_category1
    FOREIGN KEY (external_service_id, external_category_id) REFERENCES category (external_service_id, external_category_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE item_inventory
    ADD CONSTRAINT fk_item_inventory_condition1
    FOREIGN KEY (item_condition_id) REFERENCES `condition` (condition_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE item_inventory
    ADD CONSTRAINT fk_item_inventory_condition2
    FOREIGN KEY (instructions_condition_id) REFERENCES `condition` (condition_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_service_item
    ADD CONSTRAINT fk_external_service_item_external_item1
    FOREIGN KEY (external_item_id) REFERENCES external_item (external_item_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_service_item
    ADD CONSTRAINT fk_external_service_item_item_inventory1
    FOREIGN KEY (item_inventory_id) REFERENCES item_inventory (item_inventory_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_item_inventory
    ADD CONSTRAINT fk_external_item_has_item_inventory_external_item1
    FOREIGN KEY (external_item_id) REFERENCES external_item (external_item_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE external_item_inventory
    ADD CONSTRAINT fk_external_item_has_item_inventory_item_inventory1
    FOREIGN KEY (item_inventory_id) REFERENCES item_inventory (item_inventory_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE bricklink_item_inventory
    ADD CONSTRAINT fk_bricklink_item_inventory_external_item_inventory1
    FOREIGN KEY (external_item_id, item_inventory_id) REFERENCES external_item_inventory (external_item_id, item_inventory_id)
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
