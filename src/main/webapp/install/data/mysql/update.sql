ALTER TABLE `shopjqb`.`xx_admin` add COLUMN `commercial_id`  bigint(20) default null;
ALTER TABLE `shopjqb`.`xx_product` add COLUMN `commercial_id`  bigint(20) not null default 0;
ALTER TABLE `shopjqb`.`xx_order` add COLUMN commercial_id  bigint(20) not null default 0,add COLUMN customer_order_id  bigint(20) not null default 0;
ALTER TABLE `shopjqb`.`xx_order` add COLUMN exchange_for_order bigint default null,add COLUMN order_type integer default 0;
alter table shopjqb.xx_order add index FK25E6B94FD3A8BE7F (commercial_id);
create table shopjqb.xx_customer_order (id bigint not null auto_increment, create_date datetime not null, modify_date datetime not null, address varchar(255) not null, amount_paid decimal(21,6) not null, area_name varchar(255) not null, consignee varchar(255) not null, coupon_discount decimal(21,6) not null, expire datetime, fee decimal(21,6) not null, freight decimal(21,6) not null, invoice_title varchar(255), is_allocated_stock bit not null, is_invoice bit not null, lock_expire datetime, memo varchar(255), offset_amount decimal(21,6) not null, order_status integer not null, payment_method_name varchar(255) not null, payment_status integer not null, phone varchar(255) not null, point bigint not null, promotion varchar(255), promotion_discount decimal(21,6) not null, shipping_method_name varchar(255) not null, shipping_status integer not null, sn varchar(100) not null unique, tax decimal(21,6) not null, zip_code varchar(255) not null, area bigint, member bigint not null, operator bigint, payment_method bigint, shipping_method bigint, primary key (id));
create table shopjqb.xx_commercial (id bigint not null auto_increment, create_date datetime , modify_date datetime , orders integer,area bigint(20),  code varchar(255), name varchar(255) ,introduction longtext, url varchar(255),full_name varchar(255) default null,account varchar(50) default null,deposit_bank varchar(255) default null, primary key (id));
create table shopjqb.xx_customer_order_log (id bigint not null auto_increment, create_date datetime not null, modify_date datetime not null, content varchar(255), operator varchar(255), type integer not null, customer_order bigint not null, primary key (id));
create table xx_settle_accounts (id bigint not null auto_increment, create_date datetime not null, modify_date datetime not null, account varchar(255), amount decimal(21,6) not null, bank varchar(255), expire datetime, fee decimal(21,6) not null, memo varchar(255),method integer not null, operator varchar(255), payer varchar(255), payment_date datetime, payment_method varchar(255), sn varchar(100) not null unique, status integer not null, type integer not null,admin bigint not null,commercial_id bigint default null,  primary key (id))

ALTER TABLE `shopjqb`.`xx_order` add COLUMN completed_date datetime default null;

ALTER TABLE `shopjqb`.`xx_ad` add COLUMN  promotion_ids varchar(255) default null;

ALTER TABLE `shopjqb`.`xx_order_item` add COLUMN cost decimal(21,6) default 0;
ALTER TABLE `shopjqb`.`xx_returns_item` add COLUMN cost decimal(21,6) default 0,add COLUMN price decimal(21,6) default 0;

ALTER TABLE `shopjqb`.`xx_product` add COLUMN sequence_num integer default 0;

ALTER TABLE `shopjqb`.`xx_area` add COLUMN  is_marketable  bit default 0;

ALTER TABLE `shopjqb`.`xx_product` add COLUMN  is_pay_shipping bit,add COLUMN is_restriction bit default 0,
 add COLUMN restriction_num integer default 0,add COLUMN shipping_price decimal(21,6);

 CREATE TABLE shopjqb.`xx_commercial_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 CREATE TABLE shopjqb.`xx_customer_order_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;