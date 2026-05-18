create table if not exists bricklink_sale_item
(
  bl_sale_item_id int auto_increment
    primary key,
  inventory_id int not null,
  bl_item_id int not null,
  description varchar(2048) null,
  new_or_used varchar(1) not null,
  completeness varchar(1) not null,
  quantity smallint(6) not null,
  has_extended_description tinyint(1) not null,
  unit_price decimal(7,2) not null,
  date_created datetime default CURRENT_TIMESTAMP not null,
  status varchar(1) default 'C' not null,
  constraint bl_inventory_id_index_1
  unique (inventory_id),
  constraint inventory_id
  unique (inventory_id)
)
;

create index if not exists bricklink_item_fk_1
  on bricklink_sale_item (bl_item_id)
;

