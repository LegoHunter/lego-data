create table if not exists item_inventory
(
    item_inventory_id             int auto_increment primary key,
    uuid                          varchar(64) unique,
    box_number                    int,
    purchase_price                decimal(9,2),
    description                   varchar(2048),
    active                        boolean,
    for_sale                      boolean,
    new_or_used                   varchar(32),
    completeness                  varchar(64),
    item_condition_id             int,
    box_condition_id              int,
    instructions_condition_id     int,
    sealed                        boolean,
    built_once                    boolean,
    inventory_state_code          varchar(30) default 'AVAILABLE' not null,
    inventory_state_changed_at    timestamp default CURRENT_TIMESTAMP not null,
    sale_intent_code              varchar(30) default 'UNDECIDED' not null,
    sale_intent_updated_at        timestamp default CURRENT_TIMESTAMP not null,
    sale_intent_note              varchar(500)
);

merge into item_inventory (
    item_inventory_id,
    uuid,
    box_number,
    purchase_price,
    description,
    active,
    for_sale,
    new_or_used,
    completeness,
    sealed,
    built_once
) key (item_inventory_id) values
    (1, 'item-inventory-photo-test-1', 1, 0.00, 'Item inventory photo test row 1', true, false, 'USED', 'COMPLETE', false, false),
    (2, 'item-inventory-photo-test-2', 1, 0.00, 'Item inventory photo test row 2', true, false, 'USED', 'COMPLETE', false, false);

create table if not exists item_inventory_photo
(
    item_inventory_photo_id int auto_increment primary key,
    item_inventory_id       int                                                                null,
    s3_bucket               varchar(255)                                                       null,
    s3_key                  varchar(255)                                                       null,
    md5                     char(32)                                                           null,
    metadata_hash           char(64)                                                           null,
    file_name               varchar(255)                                                       not null,
    file_size               bigint                                                             null,
    is_primary              tinyint                                  default 0                 null,
    caption                 varchar(1024)                                                      null,
    status                  enum ('UPLOADED', 'PROCESSED', 'FAILED') default 'UPLOADED'        not null,
    created_at              timestamp                                default CURRENT_TIMESTAMP not null,
    updated_at              timestamp                                default CURRENT_TIMESTAMP not null,
    uploaded_at             timestamp                                                          null,
    constraint uq_item_service_key
        unique (s3_key),
    constraint uq_md5_key
        unique (md5)
);

create index if not exists fk_item_inventory_photo_item_inventory
    on item_inventory_photo (item_inventory_id);

alter table item_inventory_photo add column if not exists metadata_hash char(64);

