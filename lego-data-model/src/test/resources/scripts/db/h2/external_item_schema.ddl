DROP TABLE IF EXISTS external_item ;
DROP TABLE IF EXISTS category ;
DROP TABLE IF EXISTS external_service;
DROP TABLE IF EXISTS external_service_type ;

-- -----------------------------------------------------
-- Table external_service_type
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS external_service_type (
       external_service_type_id INT NOT NULL,
       external_service_type_name VARCHAR(20) NOT NULL,
    external_service_type_description VARCHAR(100) NULL DEFAULT NULL,
    PRIMARY KEY (external_service_type_id));

-- -----------------------------------------------------
-- Table external_service
-- -----------------------------------------------------

CREATE TABLE external_service
(
    external_service_id      INT         NOT NULL,
    external_service_name    VARCHAR(50) NOT NULL,
    external_service_url     VARCHAR(500) NULL DEFAULT NULL,
    external_service_type_id INT         NOT NULL,
    PRIMARY KEY (external_service_id),
    UNIQUE external_service_name_UNIQUE (external_service_name ASC),
    CONSTRAINT fk_external_service_external_service_type1
    FOREIGN KEY (external_service_type_id)
    REFERENCES external_service_type (external_service_type_id)
);

-- -----------------------------------------------------
-- Table category
-- -----------------------------------------------------

CREATE TABLE category (
      external_service_id INT NOT NULL,
      external_category_id INT NOT NULL,
      category_name VARCHAR(50) NOT NULL,
    parent_id INT NULL,
    PRIMARY KEY (external_service_id, external_category_id),
    CONSTRAINT fk_category_external_service1
    FOREIGN KEY (external_service_id)
    REFERENCES external_service (external_service_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table external_item
-- -----------------------------------------------------

CREATE TABLE external_item (
   external_item_id INT NOT NULL AUTO_INCREMENT,
   external_service_id INT NOT NULL,
   external_category_id INT NOT NULL,
   external_number VARCHAR(25) NOT NULL,
    external_unique_id INT NULL,
    external_name VARCHAR(250) NOT NULL,
    external_item_type VARCHAR(20) NOT NULL,
    external_url VARCHAR(300) NULL DEFAULT NULL,
    external_year_released INT NULL,
    PRIMARY KEY (external_item_id),
    CONSTRAINT fk_external_item_category1
    FOREIGN KEY (external_service_id , external_category_id)
    REFERENCES category (external_service_id , external_category_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);