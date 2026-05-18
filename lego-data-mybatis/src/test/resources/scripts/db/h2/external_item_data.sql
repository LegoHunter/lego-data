insert into external_service_type (external_service_type_id, external_service_type_name) values (2, 'MARKETPLACE');

insert into external_service (external_service_id, external_service_name, external_service_type_id) values (2, 'BRICKLINK', 2);

insert into category (external_service_id, external_category_id, category_name, parent_id) values (2, 1, 'Category 1', null);
insert into category (external_service_id, external_category_id, category_name, parent_id) values (2, 2, 'Category 2', null);
insert into category (external_service_id, external_category_id, category_name, parent_id) values (2, 3, 'Category 3', null);
insert into category (external_service_id, external_category_id, category_name, parent_id) values (2, 4, 'Category 4', null);

insert into external_item (external_service_id, external_category_id, external_number, external_unique_id, external_name, external_item_type, external_url, external_year_released) values (2, 1, '1234-1', null, 'Ext Name 1', 'SET', null, 2001);
insert into external_item (external_service_id, external_category_id, external_number, external_unique_id, external_name, external_item_type, external_url, external_year_released) values (2, 2, '2345-1', null, 'Ext Name 2', 'SET', null, 2002);