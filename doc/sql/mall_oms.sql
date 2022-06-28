CREATE DATABASE `mall_oms`;

USE `mall_oms`;


/*Table structure for table `oms_cart` */

DROP TABLE IF EXISTS `oms_cart`;

CREATE TABLE `oms_cart` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                            `user_id` bigint unsigned DEFAULT NULL COMMENT '用户id',
                            `sku_id` bigint unsigned DEFAULT NULL COMMENT 'SKU id',
                            `title` varchar(255) DEFAULT NULL COMMENT '商品SKU标题（冗余）',
                            `main_picture` varchar(255) DEFAULT NULL COMMENT '商品SKU图片URL（第1张）（冗余）',
                            `price` decimal(10,2) DEFAULT NULL COMMENT '商品SKU单价（加入时）',
                            `quantity` smallint unsigned DEFAULT NULL COMMENT '商品SKU购买数量',
                            `gmt_create` datetime DEFAULT NULL COMMENT '数据创建时间',
                            `gmt_modified` datetime DEFAULT NULL COMMENT '数据最后修改时间',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4  COMMENT='购物车数据表';

/*Data for the table `oms_cart` */

/*Table structure for table `oms_order` */

DROP TABLE IF EXISTS `oms_order`;

CREATE TABLE `oms_order` (
                             `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                             `sn` varchar(50) DEFAULT NULL COMMENT '订单编号',
                             `user_id` bigint unsigned DEFAULT NULL COMMENT '用户id',
                             `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人姓名（冗余，历史）',
                             `mobile_phone` varchar(50) DEFAULT NULL COMMENT '联系电话（冗余，历史）',
                             `telephone` varchar(50) DEFAULT NULL COMMENT '固定电话（冗余，历史）',
                             `province_code` varchar(50) DEFAULT NULL COMMENT '省-代号（冗余，历史）',
                             `province_name` varchar(50) DEFAULT NULL COMMENT '省-名称（冗余，历史）',
                             `city_code` varchar(50) DEFAULT NULL COMMENT '市-代号（冗余，历史）',
                             `city_name` varchar(50) DEFAULT NULL COMMENT '市-名称（冗余，历史）',
                             `district_code` varchar(50) DEFAULT NULL COMMENT '区-代号（冗余，历史）',
                             `district_name` varchar(50) DEFAULT NULL COMMENT '区-名称（冗余，历史）',
                             `street_code` varchar(50) DEFAULT NULL COMMENT '街道-代号（冗余，历史）',
                             `street_name` varchar(50) DEFAULT NULL COMMENT '街道-名称（冗余，历史）',
                             `detailed_address` varchar(255) DEFAULT NULL COMMENT '详细地址（冗余，历史）',
                             `tag` varchar(50) DEFAULT NULL COMMENT '标签（冗余，历史），例如：家、公司、学校',
                             `payment_type` tinyint unsigned DEFAULT NULL COMMENT '支付方式，0=银联，1=微信，2=支付宝',
                             `state` tinyint unsigned DEFAULT '0' COMMENT '状态，0=未支付，1=已关闭（超时未支付），2=已取消，3=已支付，4=已签收，5=已拒收，6=退款处理中，7=已退款',
                             `reward_point` int unsigned DEFAULT '0' COMMENT '积分',
                             `amount_of_original_price` decimal(10,2) DEFAULT NULL COMMENT '商品原总价',
                             `amount_of_freight` decimal(10,2) DEFAULT NULL COMMENT '运费总价',
                             `amount_of_discount` decimal(10,2) DEFAULT NULL COMMENT '优惠金额',
                             `amount_of_actual_pay` decimal(10,2) DEFAULT NULL COMMENT '实际支付',
                             `gmt_order` datetime DEFAULT NULL COMMENT '下单时间',
                             `gmt_pay` datetime DEFAULT NULL COMMENT '支付时间',
                             `gmt_create` datetime DEFAULT NULL COMMENT '数据创建时间',
                             `gmt_modified` datetime DEFAULT NULL COMMENT '数据最后修改时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `sn` (`sn`)
) ENGINE=InnoDB AUTO_INCREMENT=5012 DEFAULT CHARSET=utf8mb4  COMMENT='订单数据表';

/*Data for the table `oms_order` */

insert  into `oms_order`(`id`,`sn`,`user_id`,`contact_name`,`mobile_phone`,`telephone`,`province_code`,`province_name`,`city_code`,`city_name`,`district_code`,`district_name`,`street_code`,`street_name`,`detailed_address`,`tag`,`payment_type`,`state`,`reward_point`,`amount_of_original_price`,`amount_of_freight`,`amount_of_discount`,`amount_of_actual_pay`,`gmt_order`,`gmt_pay`,`gmt_create`,`gmt_modified`) values
(5011,'01bb082e-88d3-4571-a3b5-f5fb01fdb2eb',1,'刘先生','13800138001','010-88888888','110000','北京市','110100','市辖区','110103','海淀区','00005','中关村街道','中关村软件园28-3-405','公司',1,0,100,5000.00,100.00,0.00,0.00,NULL,NULL,'2022-03-16 11:42:11','2022-03-16 11:42:11');

/*Table structure for table `oms_order_item` */

DROP TABLE IF EXISTS `oms_order_item`;

CREATE TABLE `oms_order_item` (
                                  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                  `order_id` bigint unsigned DEFAULT NULL COMMENT '订单id',
                                  `sku_id` bigint unsigned DEFAULT NULL COMMENT 'SKU id',
                                  `title` varchar(255) DEFAULT NULL COMMENT '商品SKU标题（冗余，历史）',
                                  `bar_code` varchar(255) DEFAULT NULL COMMENT '商品SKU商品条型码（冗余）',
                                  `data` varchar(2500) DEFAULT NULL COMMENT '商品SKU全部属性，使用JSON格式表示（冗余）',
                                  `main_picture` varchar(255) DEFAULT NULL COMMENT '商品SKU图片URL（第1张）（冗余）',
                                  `price` decimal(10,2) DEFAULT NULL COMMENT '商品SKU单价（冗余，历史）',
                                  `quantity` smallint unsigned DEFAULT NULL COMMENT '商品SKU购买数量',
                                  `gmt_create` datetime DEFAULT NULL COMMENT '数据创建时间',
                                  `gmt_modified` datetime DEFAULT NULL COMMENT '数据最后修改时间',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5011 DEFAULT CHARSET=utf8mb4  COMMENT='订单商品数据表';

/*Data for the table `oms_order_item` */

insert  into `oms_order_item`(`id`,`order_id`,`sku_id`,`title`,`bar_code`,`data`,`main_picture`,`price`,`quantity`,`gmt_create`,`gmt_modified`) values
(5006,5007,1,'2021年新款，小米11 Ultra黑色512G，16G超大内存120Hz高刷67w快充','0','[{\"id\":1,\"name\":\"屏幕尺寸\",\"value\":\"6.1寸\"},{\"id\":3,\"name\":\"颜色\",\"value\":\"黑色\"},{\"id\":5,\"name\":\"运行内存\",\"value\":\"16GB\"}]','picture1',6999.99,10,NULL,NULL),
(5007,5008,1,'2021年新款，小米11 Ultra黑色512G，16G超大内存120Hz高刷67w快充','0','[{\"id\":1,\"name\":\"屏幕尺寸\",\"value\":\"6.1寸\"},{\"id\":3,\"name\":\"颜色\",\"value\":\"黑色\"},{\"id\":5,\"name\":\"运行内存\",\"value\":\"16GB\"}]','picture1',6999.99,10,NULL,NULL),
(5008,5009,1,'2021年新款，小米11 Ultra黑色512G，16G超大内存120Hz高刷67w快充','0','[{\"id\":1,\"name\":\"屏幕尺寸\",\"value\":\"6.1寸\"},{\"id\":3,\"name\":\"颜色\",\"value\":\"黑色\"},{\"id\":5,\"name\":\"运行内存\",\"value\":\"16GB\"}]','picture1',6999.99,10,NULL,NULL),
(5009,5010,1,'2021年新款，小米11 Ultra黑色512G，16G超大内存120Hz高刷67w快充','0','[{\"id\":1,\"name\":\"屏幕尺寸\",\"value\":\"6.1寸\"},{\"id\":3,\"name\":\"颜色\",\"value\":\"黑色\"},{\"id\":5,\"name\":\"运行内存\",\"value\":\"16GB\"}]','picture1',6999.99,10,NULL,NULL),
(5010,5011,1,'2021年新款，小米11 Ultra黑色512G，16G超大内存120Hz高刷67w快充','0','[{\"id\":1,\"name\":\"屏幕尺寸\",\"value\":\"6.1寸\"},{\"id\":3,\"name\":\"颜色\",\"value\":\"黑色\"},{\"id\":5,\"name\":\"运行内存\",\"value\":\"16GB\"}]','picture1',6999.99,10,'2022-03-16 11:42:11','2022-03-16 11:42:11');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
CREATE TABLE `undo_log` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `branch_id` bigint NOT NULL,
                            `xid` varchar(100) NOT NULL,
                            `context` varchar(128) NOT NULL,
                            `rollback_info` longblob NOT NULL,
                            `log_status` int NOT NULL,
                            `log_created` datetime NOT NULL,
                            `log_modified` datetime NOT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb3