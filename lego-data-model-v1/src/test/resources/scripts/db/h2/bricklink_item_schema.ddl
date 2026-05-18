CREATE TABLE if not exists bricklink_item (
  item_id int(11) NOT NULL,
  bl_item_number varchar(10) NOT NULL,
  bl_item_id int(11) NOT NULL,
  CONSTRAINT bricklink_item_pk PRIMARY KEY (item_id,bl_item_id),
  CONSTRAINT bricklink_item_item_id_bl_item_id_index UNIQUE (item_id,bl_item_id),
  CONSTRAINT bricklink_item_item_item_id_fk FOREIGN KEY (item_id) REFERENCES item (item_id)
);
