CREATE TABLE if not exists inventory_index (
  box_id int(11) NOT NULL DEFAULT '0',
  box_index int(11) NOT NULL DEFAULT '0',
  item_number varchar(15) NOT NULL,
  box_name varchar(20) DEFAULT NULL,
  box_number varchar(10) NOT NULL,
  sealed varchar(5) DEFAULT NULL,
  quantity int(11) DEFAULT NULL,
  description text,
  PRIMARY KEY (box_id,box_index),
  KEY inventory_index__index_1 (box_id,box_index)
);