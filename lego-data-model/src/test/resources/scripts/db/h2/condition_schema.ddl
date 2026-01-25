CREATE TABLE if not exists `condition` (
  condition_id int(11) DEFAULT NULL,
  condition_code varchar(5) NOT NULL,
  condition_description varchar(50) NOT NULL,
  condition_text tinytext,
  constraint condition_id primary key (condition_id),
  CONSTRAINT condition_code_index UNIQUE (condition_code)
);
truncate table `condition`;

INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES (1, 'M', 'Mint', null);
INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES (2, 'E', 'Excellent', null);
INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES (3, 'VG', 'Very Good', null);
INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES (4, 'G', 'Good', null);
INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES (5, 'P', 'Poor', null);
INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES (6, 'NA', 'Not Applicable', null);
INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES (7, 'F', 'Fair', null);
INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES (8, 'MS', 'Missing', null);
INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES (9, 'CC', 'Color Copy', null);
INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES (10, 'BW', 'Black & White Copy', null);
INSERT INTO `condition` (condition_id, condition_code, condition_description, condition_text) VALUES (11, 'SL', 'Sealed', 'Mint in sealed box');
commit;