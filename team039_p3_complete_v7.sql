DROP TABLE IF EXISTS ASACS39.audit_log;
DROP TABLE IF EXISTS ASACS39.services_used;
DROP TABLE IF EXISTS ASACS39.room_waitlist;
DROP TABLE IF EXISTS ASACS39.bunk;
DROP TABLE IF EXISTS ASACS39.room;
DROP TABLE IF EXISTS ASACS39.request;
DROP TABLE IF EXISTS ASACS39.item;
DROP TABLE IF EXISTS ASACS39.foodbank;
DROP TABLE IF EXISTS ASACS39.food_pantry;
DROP TABLE IF EXISTS ASACS39.soup_kitchen;
DROP TABLE IF EXISTS ASACS39.shelter;
DROP TABLE IF EXISTS ASACS39.users;
DROP TABLE IF EXISTS ASACS39.site;
DROP TABLE IF EXISTS ASACS39.clients;

CREATE TABLE ASACS39.site (
  site_number int(5) NOT NULL AUTO_INCREMENT,
  site_name varchar(150) NOT NULL,
  primary_phone varchar(15) NOT NULL,
  street_address varchar(100) NOT NULL,
  city varchar(100) NOT NULL,
  state varchar(12) NOT NULL,
  zip_code varchar(10) NOT NULL,
  PRIMARY KEY (site_number),
  UNIQUE KEY full_address (street_address, city, state, zip_code)
);

CREATE TABLE ASACS39.users (
  user_name varchar(30) NOT NULL,
  user_password varchar(75) NOT NULL,
  email varchar(100) NOT NULL,
  first_name varchar(40) NOT NULL,
  last_name varchar(40) NOT NULL,
  user_type varchar(9) NOT NULL,
  site_managing int(5) NOT NULL,
  check( user_type in ('volunteer','employee')),
  PRIMARY KEY (user_name),
  UNIQUE KEY user_email_unique (email),
  KEY user_site_managed_fk_1 (site_managing),
  CONSTRAINT user_site_managed_fk_1 FOREIGN KEY (site_managing) REFERENCES ASACS39.site (site_number)
);

CREATE TABLE ASACS39.clients (
  client_id int(20) NOT NULL AUTO_INCREMENT,
  id_nbr varchar(40) NOT NULL,
  id_desc varchar(200) NOT NULL,
  first_name varchar(40) NOT NULL,
  last_name varchar(40) NOT NULL,
  phone_number varchar(15) DEFAULT NULL,
  PRIMARY KEY (client_id),
  UNIQUE KEY client_id_desc_unique (id_nbr,id_desc)
);

CREATE TABLE ASACS39.foodbank (
  parent_site int(5) NOT NULL,
  PRIMARY KEY (parent_site),
  KEY foodbank_fk_1 (parent_site),
  CONSTRAINT foodbank_fk_1 FOREIGN KEY (parent_site) REFERENCES ASACS39.site (site_number)
);

CREATE TABLE ASACS39.item (
  item_id int(20) NOT NULL AUTO_INCREMENT,
  units int(5) NOT NULL,
  exp_date date NOT NULL,
  item_name varchar(150) NOT NULL,
  parent_foodbank int(5) NOT NULL,
  storage_type varchar(12) NOT NULL,
  supply_type varchar(16) DEFAULT NULL,
  food_type varchar(25) DEFAULT NULL,
  is_archived tinyint(4) NOT NULL DEFAULT '0',
      check (storage_type in
    ('dry good','frozen','refrigerated')),
    check (supply_type in
    ('personal hygiene', 'clothing', 'shelter', 'other')),
    check (food_type in
    ('vegetables', 'nuts/grains/beans', 'meat/seafood',
    'dairy/eggs', 'sauce/condiment/seasoning', 'juice/drink')),
  PRIMARY KEY (item_id),
  KEY item_parent_fb_fk_1 (parent_foodbank),
  CONSTRAINT item_parent_fb_fk_1 FOREIGN KEY (parent_foodbank) REFERENCES ASACS39.foodbank (parent_site) ON DELETE CASCADE
);

CREATE TABLE ASACS39.request (
  request_id int(20) NOT NULL AUTO_INCREMENT,
  requesting_user varchar(30) NOT NULL,
  approving_user varchar(30) DEFAULT NULL,
  request_item_id int(20) NOT NULL,
  requested_units int(5) DEFAULT '1',
  fulfilled_units int(5) DEFAULT '0',
  request_status varchar(20) DEFAULT 'pending',
  PRIMARY KEY (request_id),
  KEY request_ru_fk_1 (requesting_user),
  KEY request_au_fk_1 (approving_user),
  KEY request_iid_fk_1 (request_item_id),
  CONSTRAINT request_au_fk_1 FOREIGN KEY (approving_user) REFERENCES ASACS39.users (user_name),
  CONSTRAINT request_iid_fk_1 FOREIGN KEY (request_item_id) REFERENCES ASACS39.item (item_id) ON DELETE CASCADE,
  CONSTRAINT request_ru_fk_1 FOREIGN KEY (requesting_user) REFERENCES ASACS39.users (user_name)
);

CREATE TABLE ASACS39.food_pantry (
  parent_site int(5) NOT NULL,
  description varchar(200) DEFAULT NULL,
  hours varchar(200) NOT NULL,
  pantry_condition varchar(200) DEFAULT NULL,
  PRIMARY KEY (parent_site),
  KEY food_pantry_fk_1 (parent_site),
  CONSTRAINT food_pantry_fk_1 FOREIGN KEY (parent_site) REFERENCES ASACS39.site (site_number)
);

CREATE TABLE ASACS39.soup_kitchen (
  parent_site int(5) NOT NULL,
  description varchar(200) DEFAULT NULL,
  hours varchar(200) NOT NULL,
  kitchen_condition varchar(200) DEFAULT NULL,
  seating_capacity int(5) NOT NULL,
  PRIMARY KEY (parent_site),
  KEY soup_kitchen_fk_1 (parent_site),
  CONSTRAINT soup_kitchen_fk_1 FOREIGN KEY (parent_site) REFERENCES ASACS39.site (site_number)
);

CREATE TABLE ASACS39.shelter (
  parent_site int(5) NOT NULL,
  description varchar(200) DEFAULT NULL,
  hours varchar(200) NOT NULL,
  shelter_condition varchar(200) DEFAULT NULL,
  PRIMARY KEY (parent_site),
  KEY shelter_fk_1 (parent_site),
  CONSTRAINT shelter_fk_1 FOREIGN KEY (parent_site) REFERENCES ASACS39.site (site_number)
);

CREATE TABLE ASACS39.room (
  room_id int(20) NOT NULL AUTO_INCREMENT,
  parent_shelter int(5) NOT NULL,
  occupying_client int(20) DEFAULT NULL,
  occupied tinyint(1) DEFAULT '0',
  PRIMARY KEY (room_id),
  KEY room_ps_fk_1 (parent_shelter),
  KEY room_oc_fk_1 (occupying_client),
  CONSTRAINT room_oc_fk_1 FOREIGN KEY (occupying_client) REFERENCES ASACS39.clients (client_id),
  CONSTRAINT room_ps_fk_1 FOREIGN KEY (parent_shelter) REFERENCES ASACS39.shelter (parent_site) ON DELETE CASCADE
);

CREATE TABLE ASACS39.bunk (
  bunk_id int(20) NOT NULL AUTO_INCREMENT,
  parent_shelter int(5) NOT NULL,
  occupying_client int(20) DEFAULT NULL,
  occupied tinyint(1) DEFAULT '0',
  bunk_type varchar(12) DEFAULT 'mixed',
  check(bunk_type in ('male only', 'female only', 'mixed ') ),
  PRIMARY KEY (bunk_id),
  KEY bunk_ps_fk_1 (parent_shelter),
  KEY bunk_oc_fk_1 (occupying_client),
  CONSTRAINT bunk_oc_fk_1 FOREIGN KEY (occupying_client) REFERENCES ASACS39.clients (client_id),
  CONSTRAINT bunk_ps_fk_1 FOREIGN KEY (parent_shelter) REFERENCES ASACS39.shelter (parent_site) ON DELETE CASCADE
);

CREATE TABLE ASACS39.room_waitlist (
  parent_shelter int(5) NOT NULL,
  client_id int(20) NOT NULL,
  wait_position int(3) NOT NULL,
  PRIMARY KEY (parent_shelter, client_id),
  KEY room_waitlist_oc_fk_1 (client_id),
  KEY room_waitlist_ps_fk_1 (parent_shelter),
  CONSTRAINT room_waitlist_oc_fk_1 FOREIGN KEY (client_id) REFERENCES ASACS39.clients (client_id),
  CONSTRAINT room_waitlist_ps_fk_1 FOREIGN KEY (parent_shelter) REFERENCES ASACS39.shelter (parent_site) ON DELETE CASCADE
);

CREATE TABLE ASACS39.services_used (
  tracking_client int(20) NOT NULL,
  description varchar(200) NOT NULL,
  date_time datetime NOT NULL,
  site_number int(5) NOT NULL,
  PRIMARY KEY (tracking_client,date_time),
  KEY service_used_client_fk_1 (tracking_client),
  KEY service_used_site_fk_1 (site_number),
  CONSTRAINT service_used_client_fk_1 FOREIGN KEY (tracking_client) REFERENCES ASACS39.clients (client_id),
  CONSTRAINT service_used_site_fk_1 FOREIGN KEY (site_number) REFERENCES ASACS39.site (site_number)
);

CREATE TABLE ASACS39.audit_log (
  tracking_client int(20) NOT NULL,
  change_description varchar(200) NOT NULL,
  date_time datetime NOT NULL,
  PRIMARY KEY (tracking_client, date_time),
  KEY audit_log_client_fk_1 (tracking_client),
  CONSTRAINT audit_log_client_fk_1 FOREIGN KEY (tracking_client) REFERENCES ASACS39.clients (client_id)
);

INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site1', '770-555-1234', '443 No Street', 'Atlanta', 'GA', '30303');
INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site2', '770-555-1255', '8522 Spring Road', 'Atlanta', 'GA', '30303');
INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site3', '770-555-7345', '845 Maple Circle', 'Atlanta', 'GA', '30307');
INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site4', '678-555-8643', '1882 Lakeview Drive', 'Atlanta', 'GA', '30316');
INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site5', '678-555-4421', '423 Flower Court', 'Atlanta', 'GA', '30312');
INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site6', '678-555-3333', '5 Circle Drive', 'Sandy Springs', 'GA', '30319');
INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site7', '678-555-1111', '423 Lantern Lane', 'Atlanta', 'GA', '30320');
INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site8', '678-555-2222', '876 Roman Street', 'Decatur', 'GA', '30319');
INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site9', '678-555-8888', '25 Maiden Lane', 'Decatur', 'GA', '30318');
INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site10', '678-555-6666', '531 Magnolia Court', 'Atlanta', 'GA', '30317');
INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site11', '678-555-5555', '998 Smith Street', 'Atlanta', 'GA', '30318');
INSERT INTO ASACS39.site (site_name, primary_phone, street_address, city, state, zip_code) VALUES ('site12', '678-555-4444', '67 Lafayette Court', 'Atlanta', 'GA', '30317');

INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol1', 'gatech123', 'liz@team39asacs.com', 'Site1', 'Volunteer1', 'volunteer', 1);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol2', 'gatech123', 'jack@team39asacs.com', 'Site2', 'Volunteer2', 'volunteer', 2);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol3', 'gatech123', 'Tracy@team39asacs.com', 'Site3', 'Volunteer3', 'volunteer', 3);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol4', 'gatech123', 'jenna@team39asacs.com', 'Site4', 'Volunteer4', 'volunteer', 4);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol5', 'gatech123', 'kenneth@team39asacs.com', 'Site5', 'Volunteer5', 'volunteer', 5);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol6', 'gatech123', 'pete@team39asacs.com', 'Site6', 'Volunteer6', 'volunteer', 6);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol7', 'gatech123', 'frank@team39asacs.com', 'Site7', 'Volunteer7', 'volunteer', 7);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol8', 'gatech123', 'cerie@team39asacs.com', 'Site8', 'Volunteer8', 'volunteer', 8);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol9', 'gatech123', 'toofer@team39asacs.com', 'Site9', 'Volunteer9', 'volunteer', 9);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol10', 'gatech123', 'grizz@team39asacs.com', 'Site10', 'Volunteer10', 'volunteer', 10);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol11', 'gatech123', 'ryan@team39asacs.com', 'Site11', 'Volunteer11', 'volunteer', 11);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('vol12', 'gatech123', 'rosa@team39asacs.com', 'Site12', 'Volunteer12', 'volunteer', 12);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp1', 'gatech123', 'sam@team39asacs.com', 'Site1', 'Employee1', 'employee', 1);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp2', 'gatech123', 'barbie@team39asacs.com', 'Site2', 'Employee2', 'employee', 2);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp3', 'gatech123', 'carlota@team39asacs.com', 'Site3', 'Employee3', 'employee', 3);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp4', 'gatech123', 'alberta@team39asacs.com', 'Site4', 'Employee4', 'employee', 4);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp5', 'gatech123', 'stanley@team39asacs.com', 'Site5', 'Employee5', 'employee', 5);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp6', 'gatech123', 'dee@team39asacs.com', 'Site6', 'Employee6', 'employee', 6);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp7', 'gatech123', 'charlie@team39asacs.com', 'Site7', 'Employee7', 'employee', 7);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp8', 'gatech123', 'john@team39asacs.com', 'Site8', 'Employee8', 'employee', 8);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp9', 'gatech123', 'davidc@team39asacs.com', 'Site9', 'Employee9', 'employee', 9);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp10', 'gatech123', 'laurar@team39asacs.com', 'Site10', 'Employee10', 'employee', 10);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp11', 'gatech123', 'titap@team39asacs.com', 'Site11', 'Employee11', 'employee', 11);
INSERT INTO ASACS39.users (user_name, user_password, email, first_name, last_name, user_type, site_managing) VALUES ('emp12', 'gatech123', 'phoebe@team39asacs.com', 'Site12', 'Employee12', 'employee', 12);

INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('653347', 'AL Birth Certificate', 'Joe', 'Client1', '404-555-2298');
INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('653345789', 'GA Drivers License', 'Jane', 'Client2', '404-555-2254');
INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('01275322', 'MI Birth Certificate', 'Joe', 'Client3', '432-555-7765');
INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('77643222', 'NY Drivers License', 'Jane', 'Client4', '887-555-9932');
INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('1764frgs3262', 'OH Birth Certificate', 'Joe', 'Client5', '332-555-5532');
INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('74343239', 'CA Drivers License', 'Jane', 'Client6', '221-555-9932');
INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('4764fru3243', 'AZ Birth Certificate', 'Joe', 'Client7', '667-555-8876');
INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('77643222', 'AK Drivers License', 'Jane', 'Client8', '876-555-4472');
INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('486gtb43287', 'WA Birth Certificate', 'Joe', 'Client9', '334-555-3398');
INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('66643286', 'ND Drivers License', 'Jane', 'Client10', '900-555-0027');
INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('964wsd3265', 'SD Birth Certificate', 'Joe', 'Client11', '233-555-4476');
INSERT INTO ASACS39.clients (id_nbr, id_desc, first_name, last_name, phone_number) VALUES ('23643258', 'GA Drivers License', 'Jane', 'Client12', '677-555-2298');

INSERT INTO ASACS39.foodbank (parent_site) VALUES (1);
INSERT INTO ASACS39.foodbank (parent_site) VALUES (2);
INSERT INTO ASACS39.foodbank (parent_site) VALUES (3);

INSERT INTO ASACS39.food_pantry (parent_site, description, hours, pantry_condition) VALUES (1, 'pantry1', 'Sunday 2-6pm, Monday 8-7pm', 'picture id required');
INSERT INTO ASACS39.food_pantry (parent_site, description, hours, pantry_condition) VALUES (3, 'pantry3', 'Saturday 1-6pm, Monday 10-7pm', 'Picture id/drivers license required, proof of income, SSN');

INSERT INTO ASACS39.soup_kitchen (parent_site, description, hours, kitchen_condition, seating_capacity) VALUES (2, 'soup2', 'Saturday 12-6pm, Monday 11-7pm, Tuesday 12-6pm', 'picture id required', '50');
INSERT INTO ASACS39.soup_kitchen (parent_site, description, hours, kitchen_condition, seating_capacity) VALUES (3, 'soup3', 'Monday 11-6pm, Sunday 11-4pm, Wednesday 11-6pm', 'proof of income', '40');

INSERT INTO ASACS39.shelter (parent_site, description, hours, shelter_condition) VALUES (2, 'shelter2', 'Monday-Saturday 6pm-5am', 'picture id required, proof of income');
INSERT INTO ASACS39.shelter (parent_site, description, hours, shelter_condition) VALUES (3, 'shelter3', 'Tuesday-Sunday 6pm-5am', 'proof of income');

INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'male only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'male only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'male only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'male only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'female only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'female only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'female only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'female only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'mixed');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'mixed');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'mixed');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (2,null,0,'mixed');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'male only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'male only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'male only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'male only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'female only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'female only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'female only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'female only');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'mixed');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'mixed');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'mixed');
INSERT INTO ASACS39.bunk (parent_shelter, occupying_client, occupied, bunk_type) VALUES (3,null,0,'mixed');

INSERT INTO ASACS39.room (parent_shelter, occupying_client, occupied) VALUES (3,null,0);
INSERT INTO ASACS39.room (parent_shelter, occupying_client, occupied) VALUES (3,null,0);

INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (20, CURDATE() + INTERVAL 7 DAY, 'Romaine lettuce', 1, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 7 DAY, 'Collards', 1, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 7 DAY, 'Kale', 1, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (20, CURDATE() + INTERVAL 7 DAY, 'Garden cress', 1, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 7 DAY, 'Dandelion', 1, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 7 DAY, 'Pokeweed', 1, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (20, CURDATE() + INTERVAL 7 DAY, 'Swiss chard', 1, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 7 DAY, 'Iceberg lettuce', 1, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 7 DAY, 'Water spinach', 1, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (20, CURDATE() + INTERVAL 7 DAY, 'Borage', 1, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Pecans', 1, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 180 DAY, 'Pine nuts', 1, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 180 DAY, 'Pistachios', 1, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Almonds', 1, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 180 DAY, 'Walnuts', 1, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 180 DAY, 'Cashews', 1, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Brazil nuts', 1, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 180 DAY, 'Hazelnuts', 1, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 180 DAY, 'Macadamia nuts', 1, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Chestnuts', 1, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 365 DAY, 'Ketchup', 1, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Salt', 1, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'Mayo', 1, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 365 DAY, 'Sriracha', 1, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Soy sauce', 1, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'BBQ sauce', 1, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 365 DAY, 'Oregano', 1, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Paprika', 1, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'Pepper', 1, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 365 DAY, 'Garlic powder', 1, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Coca-Cola', 1, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'Sprite', 1, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 365 DAY, 'Sweet tea', 1, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Unsweet tea', 1, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'Kool-Aid orange', 1, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 365 DAY, 'Pepsi', 1, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Kool-Aid green', 1, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'Kool-Aid red', 1, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 365 DAY, 'Kool-Aid blue', 1, 'dry good', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Fanta orange', 1, 'dry good', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (20, CURDATE() + INTERVAL 180 DAY, 'Ground beef', 1, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Ground lamb', 1, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 180 DAY, 'Lamb leg', 1, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 180 DAY, 'Pork shoulder', 1, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (20, CURDATE() + INTERVAL 180 DAY, 'Ground pork', 1, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Beef patties', 1, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 180 DAY, 'Beef meatballs', 1, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 180 DAY, 'Pork chops', 1, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (20, CURDATE() + INTERVAL 180 DAY, 'Pork tenderloin', 1, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Beef brisket', 1, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 14 DAY, 'Feta cheese', 1, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 14 DAY, 'Monterrey Jack cheese', 1, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 14 DAY, 'Mozzarella cheese', 1, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() + INTERVAL 14 DAY, 'Cheddar cheese', 1, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (70, CURDATE() + INTERVAL 14 DAY, 'American cheese', 1, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (20, CURDATE() + INTERVAL 14 DAY, 'Manchego cheese', 1, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 14 DAY, 'Cream cheese', 1, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 14 DAY, 'Ricotta cheese', 1, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 14 DAY, 'Cotija cheese', 1, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() + INTERVAL 14 DAY, 'Brie cheese', 1, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Toothpaste', 1, 'dry good', 'personal hygiene', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() + INTERVAL 365 DAY, 'Toothbrush', 1, 'dry good', 'personal hygiene', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (70, CURDATE() + INTERVAL 365 DAY, 'Deodorant', 1, 'dry good', 'personal hygiene', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (80, CURDATE() + INTERVAL 365 DAY, 'Shampoo', 1, 'dry good', 'personal hygiene', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Body soap', 1, 'dry good', 'personal hygiene', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, '9999-01-01', 'Pants', 1, 'dry good', 'clothing', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, '9999-01-01', 'Shirt', 1, 'dry good', 'clothing', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, '9999-01-01', 'Socks', 1, 'dry good', 'clothing', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, '9999-01-01', 'Sweater', 1, 'dry good', 'clothing', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (70, '9999-01-01', 'Underwear', 1, 'dry good', 'clothing', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 7 DAY, 'Carrot', 2, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 7 DAY, 'Turnip', 2, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 7 DAY, 'Parsnip', 2, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() + INTERVAL 7 DAY, 'Rutabaga', 2, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 7 DAY, 'Radish', 2, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 7 DAY, 'Celery', 2, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 7 DAY, 'Daikon', 2, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() + INTERVAL 7 DAY, 'Scallion', 2, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 7 DAY, 'Wasabi', 2, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 7 DAY, 'Taro', 2, 'refrigerated', null, 'vegetables');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Wheat', 2, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Oat', 2, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Barley', 2, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Rye', 2, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Triticale', 2, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Corn', 2, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Rice', 2, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Winter Wheat', 2, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Wild Rice', 2, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 180 DAY, 'Sorghum', 2, 'dry good', null, 'nuts/grains/beans');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (20, CURDATE() + INTERVAL 365 DAY, 'Ketchup', 2, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'Salt', 2, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 365 DAY, 'Mayo', 2, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Sriracha', 2, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (20, CURDATE() + INTERVAL 365 DAY, 'Soy sauce', 2, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'BBQ sauce', 2, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 365 DAY, 'Oregano', 2, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Paprika', 2, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (20, CURDATE() + INTERVAL 365 DAY, 'Pepper', 2, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'Garlic powder', 2, 'dry good', null, 'sauce/condiment/seasoning');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 30 DAY, 'Orange juice', 2, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 30 DAY, 'Tomato juice', 2, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 30 DAY, 'Carrot juice', 2, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() + INTERVAL 30 DAY, 'Grape juice', 2, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 30 DAY, 'Grapefruit juice', 2, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 30 DAY, 'Cranberry juice', 2, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 30 DAY, 'Cherry juice', 2, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() + INTERVAL 30 DAY, 'Lemon juice', 2, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 30 DAY, 'Lime juice', 2, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 30 DAY, 'Peach juice', 2, 'refrigerated', null, 'juice/drink');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'Salmon', 2, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 365 DAY, 'Catfish', 2, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Tilapia', 2, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() + INTERVAL 365 DAY, 'Mussels', 2, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'Shrimp', 2, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Tuna', 2, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 365 DAY, 'Halibut', 2, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() + INTERVAL 365 DAY, 'Trout', 2, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 365 DAY, 'Cod', 2, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 365 DAY, 'Flounder', 2, 'frozen', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 60 DAY, 'White Eggs', 2, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 60 DAY, 'Brown Eggs', 2, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 60 DAY, 'Quail Eggs', 2, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() + INTERVAL 60 DAY, 'Duck Eggs', 2, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 60 DAY, 'Goose Eggs', 2, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 60 DAY, 'Turkey Eggs', 2, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() + INTERVAL 60 DAY, 'Ostrich Eggs', 2, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() + INTERVAL 60 DAY, 'Liquid Eggs', 2, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() + INTERVAL 60 DAY, 'Egg Whites only', 2, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() + INTERVAL 60 DAY, 'Pheasant Eggs', 2, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, '9999-01-01', 'Sleeping bag', 2, 'dry good', 'shelter', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, '9999-01-01', 'Tent', 2, 'dry good', 'shelter', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, '9999-01-01', 'Blanket', 2, 'dry good', 'shelter', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, '9999-01-01', 'Winter Jacket', 2, 'dry good', 'shelter', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, '9999-01-01', 'Raincoat', 2, 'dry good', 'shelter', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, '9999-01-01', 'Paper plates', 2, 'dry good', 'other', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, '9999-01-01', 'Toilet paper', 2, 'dry good', 'other', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, '9999-01-01', 'Paper towels', 2, 'dry good', 'other', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, '9999-01-01', 'Batteries', 2, 'dry good', 'other', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, '9999-01-01', 'Trash bags', 2, 'dry good', 'other', null);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() - INTERVAL 10 DAY, 'Chicken wings', 3, 'refrigerated', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() - INTERVAL 10 DAY, 'Chicken legs', 3, 'refrigerated', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() - INTERVAL 10 DAY, 'Chicken breasts', 3, 'refrigerated', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() - INTERVAL 10 DAY, 'Chicken nuggets', 3, 'refrigerated', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() - INTERVAL 10 DAY, 'Chicken kidneys', 3, 'refrigerated', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() - INTERVAL 10 DAY, 'Chicken tenders', 3, 'refrigerated', null, 'meat/seafood');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (30, CURDATE() - INTERVAL 10 DAY, 'Milk', 3, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (40, CURDATE() - INTERVAL 10 DAY, 'Chocolate Milk', 3, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (50, CURDATE() - INTERVAL 10 DAY, 'Skim Milk', 3, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (60, CURDATE() - INTERVAL 10 DAY, '2% Milk', 3, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (70, CURDATE() - INTERVAL 10 DAY, 'Lactose-free Milk', 3, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type) VALUES (70, CURDATE() - INTERVAL 10 DAY, 'Goat Milk', 3, 'refrigerated', null, 'dairy/eggs');
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type, is_archived) VALUES (0, CURDATE() + INTERVAL 30 DAY, 'Turnip', 2, 'refrigerated', null, 'vegetables', 1);
INSERT INTO ASACS39.item (units, exp_date, item_name, parent_foodbank, storage_type, supply_type, food_type, is_archived) VALUES (0, CURDATE() + INTERVAL 60 DAY, 'Mayo', 2, 'dry good', null, 'sauce/condiment/seasoning', 1);

INSERT INTO ASACS39.room_waitlist (parent_shelter, client_id, wait_position) VALUES (3, 9, 1);
INSERT INTO ASACS39.room_waitlist (parent_shelter, client_id, wait_position) VALUES (3, 10, 2);
INSERT INTO ASACS39.room_waitlist (parent_shelter, client_id, wait_position) VALUES (3, 11, 3);
INSERT INTO ASACS39.room_waitlist (parent_shelter, client_id, wait_position) VALUES (3, 12, 4);

INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 80, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 85, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 115, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 124, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 86, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 122, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 107, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 108, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 119, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 94, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 115, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 124, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 86, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 122, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 107, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 108, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 119, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 94, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 85, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 80, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 149, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 151, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 142, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', null, 150, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 151, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 150, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 149, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', null, 142, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 50, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 46, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 10, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 60, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 54, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 5, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 41, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 18, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 2, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 7, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 10, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 60, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 54, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 5, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 41, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 18, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 2, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 7, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 50, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 46, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 149, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 147, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 151, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 143, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 147, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 149, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 143, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 151, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 65, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 63, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 66, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 64, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 67, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 61, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', null, 70, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 67, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 61, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 66, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 64, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 70, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 65, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', null, 63, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 51, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 54, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 16, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 5, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 40, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 30, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 41, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 32, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 27, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 15, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 30, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 41, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 16, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 5, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 40, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 32, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 27, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 15, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 51, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 54, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 101, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 91, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 118, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 123, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 75, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 103, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 115, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 83, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 113, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 78, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 101, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 91, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 118, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 123, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 75, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 103, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 115, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 83, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 113, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 78, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 70, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 62, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 65, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 63, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 66, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 69, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 64, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 65, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 69, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 64, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 70, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 62, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 63, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 66, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 139, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 133, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 136, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 138, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 134, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 140, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', null, 135, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 140, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 135, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 139, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 134, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 133, 1, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 136, 2, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 138, 3, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', null, 134, 4, 0,'pending');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', 'emp2', 107, 3, 3,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', 'emp2', 110, 4, 4,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', 'emp2', 111, 5, 5,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp1', 'emp2', 112, 2, 2,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', 'vol2', 101, 1, 1,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', 'vol2', 102, 2, 2,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', 'vol2', 113, 4, 4,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol1', 'vol2', 114, 3, 3,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', 'emp1', 42, 5, 5,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', 'emp1', 43, 3, 3,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', 'emp1', 59, 4, 4,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp2', 'emp1', 60, 2, 2,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', 'vol1', 44, 1, 1,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', 'vol1', 45, 2, 2,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', 'vol1', 55, 4, 4,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol2', 'vol1', 56, 3, 3,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', 'emp1', 70, 5, 5,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', 'emp1', 69, 2, 2,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', 'emp1', 63, 3, 3,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('emp3', 'emp1', 65, 3, 3,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', 'vol1', 61, 1, 1,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', 'vol1', 62, 2, 2,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', 'vol1', 66, 3, 3,'full');
INSERT INTO ASACS39.request (requesting_user, approving_user, request_item_id, requested_units, fulfilled_units, request_status) VALUES ('vol3', 'vol1', 67, 4, 4,'full');

INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (1, 'client1 check in into pantry in site 1', CURDATE() - INTERVAL 365 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (1, 'client1 checked in into pantry in site 3', CURDATE() - INTERVAL 60 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (1, 'client1 check in into pantry in site 1', CURDATE() - INTERVAL 7 DAY, 2);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (2, 'client2 checked in into pantry in site 1', CURDATE() - INTERVAL 200 DAY, 2);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (2, 'client2 check in into pantry in site 3', CURDATE() - INTERVAL 50 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (2, 'client2 check in into pantry in site 1', CURDATE() - INTERVAL 9 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (3, 'client3 check in into pantry in site 1', CURDATE() - INTERVAL 365 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (3, 'client3 checked in into pantry in site 3', CURDATE() - INTERVAL 60 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (3, 'client3 check in into pantry in site 3', CURDATE() - INTERVAL 7 DAY, 2);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (4, 'client4 checked in into pantry in site 3', CURDATE() - INTERVAL 180 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (4, 'client4 check in into pantry in site 1', CURDATE() - INTERVAL 90 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (4, 'client4 check in into pantry in site 3', CURDATE() - INTERVAL 5 DAY, 2);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (5, 'client5 check in into kitchen in site 2', CURDATE() - INTERVAL 100 DAY, 2);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (5, 'client5 check in into kitchen in site 2', CURDATE() - INTERVAL 80 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (5, 'client5 checked in into kitchen in site 3', CURDATE() - INTERVAL 10 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (6, 'client6 checked in into kitchen in site 3', CURDATE() - INTERVAL 360 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (6, 'client6 check in into kitchen in site 2', CURDATE() - INTERVAL 200 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (6, 'client6 check in into kitchen in site 2', CURDATE() - INTERVAL 12 DAY, 2);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (7, 'client7 check in into kitchen in site 2', CURDATE() - INTERVAL 300 DAY, 2);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (7, 'client7 check in into kitchen in site 3', CURDATE() - INTERVAL 100 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (7, 'client7 checked in into kitchen in site 3', CURDATE() - INTERVAL 10 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (8, 'client8 checked in into kitchen in site 3', CURDATE() - INTERVAL 210 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (8, 'client8 check in into kitchen in site 2', CURDATE() - INTERVAL 90 DAY, 2);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (8, 'client8 check in into kitchen in site 2', CURDATE() - INTERVAL 29 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (9, 'client9 checked in into room in site 3', CURDATE() - INTERVAL 245 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (9, 'client9 check in into bunk in site 2', CURDATE() - INTERVAL 150 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (9, 'client9 check in into bunk in site 2', CURDATE() - INTERVAL 8 DAY, 2);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (10, 'client10 checked in into room in site 3', CURDATE() - INTERVAL 150 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (10, 'client10 check in into bunk in site 2', CURDATE() - INTERVAL 120 DAY, 2);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (10, 'client10 check in into bunk in site 3', CURDATE() - INTERVAL 4 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (11, 'client11 check in into bunk in site 2', CURDATE() - INTERVAL 15 DAY, 2);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (11, 'client11 check in into bunk in site 2', CURDATE() - INTERVAL 8 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (11, 'client11 checked in into room in site 3', CURDATE() - INTERVAL 3 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (12, 'client12 checked in into room in site 3', CURDATE() - INTERVAL 78 DAY, 3);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (12, 'client12 check in into bunk in site 3', CURDATE() - INTERVAL 54 DAY, 1);
INSERT INTO ASACS39.services_used (tracking_client, description, date_time, site_number) VALUES (12, 'client12 check in into bunk in site 2', CURDATE() - INTERVAL 3 DAY, 2);

INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (1, 'User vol1 updated client record: 6653347 AL Birth Certificate Joe Client1 665-555-2298', CURDATE() - INTERVAL 200 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (1, 'User emp2 updated client record: 565436 AL Birth Certificate Joe Client1 404-555-2298', CURDATE() - INTERVAL 43 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (1, 'User vol3 updated client record: 6653347 AL Birth Certificate Joe Client1 404-555-2298', CURDATE() - INTERVAL 3 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (2, 'User emp4 updated client record: 6653345789 GA Drivers License Jane Client2 897-555-2254', CURDATE() - INTERVAL 310 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (2, 'User vol5 updated client record: 87456456 GA Drivers License Jane Client2 404-555-2254', CURDATE() - INTERVAL 166 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (2, 'User emp6 updated client record: 6653345789 GA Drivers License Jane Client2 404-555-2254', CURDATE() - INTERVAL 5 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (3, 'User vol7 updated client record: 601275322 MI Birth Certificate Joe Client3 886-555-7765', CURDATE() - INTERVAL 287 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (3, 'User emp1 updated client record: 76456334534 MI Birth Certificate Joe Client3 432-555-7765', CURDATE() - INTERVAL 33 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (3, 'User vol2 updated client record: 601275322 MI Birth Certificate Joe Client3 432-555-7765', CURDATE() - INTERVAL 7 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (4, 'User emp3 updated client record: 677643222 NY Drivers License Jane Client4 643-555-9932', CURDATE() - INTERVAL 374 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (4, 'User vol4 updated client record: 1231213 NY Drivers License Jane Client4 887-555-9932', CURDATE() - INTERVAL 70 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (4, 'User emp5 updated client record: 677643222 NY Drivers License Jane Client4 887-555-9932', CURDATE() - INTERVAL 6 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (5, 'User vol6 updated client record: 61764frgs3262 OH Birth Certificate Joe Client5 674-555-5532', CURDATE() - INTERVAL 232 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (5, 'User emp7 updated client record: 789865746 OH Birth Certificate Joe Client5 332-555-5532', CURDATE() - INTERVAL 43 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (5, 'User vol1 updated client record: 61764frgs3262 OH Birth Certificate Joe Client5 332-555-5532', CURDATE() - INTERVAL 8 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (6, 'User emp2 updated client record: 674343239 CA Drivers License Jane Client6 009-555-9932', CURDATE() - INTERVAL 311 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (6, 'User vol3 updated client record: 43352345 CA Drivers License Jane Client6 221-555-9932', CURDATE() - INTERVAL 73 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (6, 'User emp4 updated client record: 674343239 CA Drivers License Jane Client6 221-555-9932', CURDATE() - INTERVAL 32 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (7, 'User vol5 updated client record: 64764fru3243 AZ Birth Certificate Joe Client7 321-555-8876', CURDATE() - INTERVAL 318 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (7, 'User emp6 updated client record: 896756456 AZ Birth Certificate Joe Client7 667-555-8876', CURDATE() - INTERVAL 120 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (7, 'User vol7 updated client record: 64764fru3243 AZ Birth Certificate Joe Client7 667-555-8876', CURDATE() - INTERVAL 4 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (8, 'User emp1 updated client record: 677643222 AK Drivers License Jane Client8 665-555-4472', CURDATE() - INTERVAL 344 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (8, 'User vol2 updated client record: 543534524 AK Drivers License Jane Client8 876-555-4472', CURDATE() - INTERVAL 133 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (8, 'User emp3 updated client record: 677643222 AK Drivers License Jane Client8 876-555-4472', CURDATE() - INTERVAL 99 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (9, 'User vol4 updated client record: 6486gtb43287 WA Birth Certificate Joe Client9 409-555-3398', CURDATE() - INTERVAL 322 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (9, 'User emp5 updated client record: 876756 WA Birth Certificate Joe Client9 334-555-3398', CURDATE() - INTERVAL 87 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (9, 'User vol6 updated client record: 6486gtb43287 WA Birth Certificate Joe Client9 334-555-3398', CURDATE() - INTERVAL 7 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (10, 'User emp1 updated client record: 666643286 ND Drivers License Jane Client10 875-555-0027', CURDATE() - INTERVAL 342 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (10, 'User vol2 updated client record: 1234323 ND Drivers License Jane Client10 900-555-0027', CURDATE() - INTERVAL 56 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (10, 'User emp3 updated client record: 666643286 ND Drivers License Jane Client10 900-555-0027', CURDATE() - INTERVAL 7 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (11, 'User vol4 updated client record: 6964wsd3265 SD Birth Certificate Joe Client11 435-555-4476', CURDATE() - INTERVAL 337 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (11, 'User emp5 updated client record: 0894567347 SD Birth Certificate Joe Client11 233-555-4476', CURDATE() - INTERVAL 104 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (11, 'User vol6 updated client record: 6964wsd3265 SD Birth Certificate Joe Client11 233-555-4476', CURDATE() - INTERVAL 8 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (12, 'User emp7 updated client record: 623643258 GA Drivers License Jane Client12 567-555-2298', CURDATE() - INTERVAL 299 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (12, 'User vol1 updated client record: 75245325654 GA Drivers License Jane Client12 677-555-2298', CURDATE() - INTERVAL 65 DAY);
INSERT INTO ASACS39.audit_log (tracking_client, change_description, date_time) VALUES (12, 'User emp2 updated client record: 623643258 GA Drivers License Jane Client12 677-555-2298', CURDATE() - INTERVAL 3 DAY);