create table if not exists item_inventory_photo
(
    item_inventory_photo_id int auto_increment primary key,
    item_inventory_id       int                                                                null,
    s3_bucket               varchar(255)                                                       null,
    s3_key                  varchar(255)                                                       null,
    md5                     char(32)                                                           null,
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

