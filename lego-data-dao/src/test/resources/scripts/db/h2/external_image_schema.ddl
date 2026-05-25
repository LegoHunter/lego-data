DROP TABLE IF EXISTS external_image_album_image;
DROP TABLE IF EXISTS external_image;
DROP TABLE IF EXISTS external_image_album;
DROP TABLE IF EXISTS item_inventory_photo;
DROP TABLE IF EXISTS item_inventory;
DROP TABLE IF EXISTS external_service;
DROP TABLE IF EXISTS external_service_type;

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
    uploaded_at TIMESTAMP
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
