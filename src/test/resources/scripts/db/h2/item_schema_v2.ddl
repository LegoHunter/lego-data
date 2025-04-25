CREATE TABLE IF NOT EXISTS item (
    item_id INT NOT NULL AUTO_INCREMENT,
    item_number VARCHAR(20) NULL DEFAULT NULL,
    item_name VARCHAR(100) NOT NULL,
    notes TINYTEXT NULL DEFAULT NULL,
    year_released INT NULL DEFAULT NULL,
    is_obsolete TINYINT NULL DEFAULT NULL,
    constraint item_id primary key (item_id)
);