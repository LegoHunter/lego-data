DELETE FROM item_inventory;
ALTER TABLE item_inventory ALTER COLUMN item_inventory_id RESTART WITH 1;
