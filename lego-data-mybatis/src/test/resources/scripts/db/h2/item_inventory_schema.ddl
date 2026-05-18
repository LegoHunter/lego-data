DROP TABLE IF EXISTS item_inventory;

CREATE TABLE item_inventory (
    item_inventory_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL UNIQUE,
    box_number VARCHAR(255),
    description VARCHAR(2000),
    active BOOLEAN NOT NULL,
    for_sale BOOLEAN NOT NULL,
    new_or_used VARCHAR(50),
    completeness VARCHAR(50),
    item_condition_id BIGINT,
    box_condition_id BIGINT,
    instructions_condition_id BIGINT,
    sealed BOOLEAN NOT NULL,
    built_once BOOLEAN NOT NULL
);