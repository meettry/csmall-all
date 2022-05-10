/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 5.7.33-log : Database - mall_ums
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`mall_ums` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `mall_ums`;
/*Table structure for table `ums_change_password_log` */

DROP TABLE IF EXISTS `ums_change_password_log`;

CREATE TABLE `ums_change_password_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '用户id',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名（冗余）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称（冗余）',
  `new_password` char(64) DEFAULT NULL COMMENT '新密码（密文）',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '浏览器内核',
  `gmt_change_password` datetime DEFAULT NULL COMMENT '修改密码时间',
  `gmt_create` datetime DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='用户修改密码日志表';

/*Data for the table `ums_change_password_log` */

insert  into `ums_change_password_log`(`id`,`user_id`,`username`,`nickname`,`new_password`,`ip`,`user_agent`,`gmt_change_password`,`gmt_create`,`gmt_modified`) values 
(1,1,NULL,NULL,'1234','127.0.0.1','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15','2022-02-20 14:24:37',NULL,NULL),
(2,1,NULL,NULL,'4567','127.0.0.1','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15','2022-02-21 02:24:37',NULL,NULL),
(3,3,NULL,NULL,'8888','127.0.0.1','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15','2022-02-21 14:24:37',NULL,NULL);

/*Table structure for table `ums_delivery_address` */

DROP TABLE IF EXISTS `ums_delivery_address`;

CREATE TABLE `ums_delivery_address` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '用户id',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人姓名',
  `mobile_phone` varchar(50) DEFAULT NULL COMMENT '联系电话',
  `telephone` varchar(50) DEFAULT NULL COMMENT '固定电话',
  `province_code` varchar(50) DEFAULT NULL COMMENT '省-代号',
  `province_name` varchar(50) DEFAULT NULL COMMENT '省-名称',
  `city_code` varchar(50) DEFAULT NULL COMMENT '市-代号',
  `city_name` varchar(50) DEFAULT NULL COMMENT '市-名称',
  `district_code` varchar(50) DEFAULT NULL COMMENT '区-代号',
  `district_name` varchar(50) DEFAULT NULL COMMENT '区-名称',
  `street_code` varchar(50) DEFAULT NULL COMMENT '街道-代号',
  `street_name` varchar(50) DEFAULT NULL COMMENT '街道-名称',
  `detailed_address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `tag` varchar(50) DEFAULT NULL COMMENT '标签，例如：家、公司、学校',
  `is_default` tinyint(3) unsigned DEFAULT '0' COMMENT '是否为默认地址，1=默认，0=非默认',
  `gmt_create` datetime DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

/*Data for the table `ums_delivery_address` */

insert  into `ums_delivery_address`(`id`,`user_id`,`contact_name`,`mobile_phone`,`telephone`,`province_code`,`province_name`,`city_code`,`city_name`,`district_code`,`district_name`,`street_code`,`street_name`,`detailed_address`,`tag`,`is_default`,`gmt_create`,`gmt_modified`) values 
(1,1,'刘先生','13800138001','010-88888888','110000','北京市','110100','市辖区','110103','海淀区','00005','中关村街道','中关村软件园28-3-405','公司',0,NULL,NULL);

/*Table structure for table `ums_login_log` */

DROP TABLE IF EXISTS `ums_login_log`;

CREATE TABLE `ums_login_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '用户id',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名（冗余）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称（冗余）',
  `ip` varchar(50) DEFAULT NULL COMMENT '登录IP地址',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '浏览器内核',
  `gmt_login` datetime DEFAULT NULL COMMENT '登录时间',
  `gmt_create` datetime DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='用户登录日志表';

/*Data for the table `ums_login_log` */

insert  into `ums_login_log`(`id`,`user_id`,`username`,`nickname`,`ip`,`user_agent`,`gmt_login`,`gmt_create`,`gmt_modified`) values 
(1,1,'root','root','127.0.0.1','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15','2022-02-20 14:24:37',NULL,NULL),
(2,2,'root','root','127.0.0.1','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15','2022-02-21 02:24:37',NULL,NULL),
(3,3,'root','root','127.0.0.1','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15','2022-02-21 14:24:37',NULL,NULL);

/*Table structure for table `ums_reward_point_log` */

DROP TABLE IF EXISTS `ums_reward_point_log`;

CREATE TABLE `ums_reward_point_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '用户id',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名（冗余）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称（冗余）',
  `change_value` int(11) DEFAULT NULL COMMENT '变动值',
  `reason` varchar(50) DEFAULT NULL COMMENT '变动原因',
  `gmt_change` datetime DEFAULT NULL COMMENT '变动时间',
  `gmt_create` datetime DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='用户积分日志表';

/*Data for the table `ums_reward_point_log` */

insert  into `ums_reward_point_log`(`id`,`user_id`,`username`,`nickname`,`change_value`,`reason`,`gmt_change`,`gmt_create`,`gmt_modified`) values 
(1,1,NULL,NULL,5,'登录','2022-02-18 14:24:37',NULL,NULL),
(2,1,NULL,NULL,5,'登录','2022-02-20 14:24:37',NULL,NULL),
(3,1,NULL,NULL,5,'登录','2022-02-21 13:24:37',NULL,NULL),
(4,1,NULL,NULL,100,'购买商品-D202111051542371','2022-02-21 14:09:37',NULL,NULL),
(5,1,NULL,NULL,-30,'兑换商品-D202111051557296','2022-02-21 14:24:37',NULL,NULL);

/*Table structure for table `ums_user` */

DROP TABLE IF EXISTS `ums_user`;

CREATE TABLE `ums_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` char(64) DEFAULT NULL COMMENT '密码（冗余，密文）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(50) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(50) DEFAULT NULL COMMENT '电子邮箱',
  `is_enable` tinyint(3) unsigned DEFAULT '0' COMMENT '是否启用，1=启用，0=未启用',
  `reward_point` int(10) unsigned DEFAULT '0' COMMENT '积分（冗余）',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP地址（冗余）',
  `login_count` int(10) unsigned DEFAULT '0' COMMENT '累计登录次数（冗余）',
  `gmt_last_login` datetime DEFAULT NULL COMMENT '最后登录时间（冗余）',
  `gmt_create` datetime DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='用户基本（常用）信息表';

/*Data for the table `ums_user` */

insert  into `ums_user`(`id`,`username`,`password`,`nickname`,`avatar`,`phone`,`email`,`is_enable`,`reward_point`,`last_login_ip`,`login_count`,`gmt_last_login`,`gmt_create`,`gmt_modified`) values 
(1,'jackson','$2a$10$O5KwU//RWm8EdRQkL3L/wOVUPMTdgc.yHZsLzwc2nVpdICenLIGZK','root',NULL,NULL,'root@tedu.cn',1,0,NULL,0,NULL,NULL,NULL),
(2,'marry','$2a$10$O5KwU//RWm8EdRQkL3L/wOVUPMTdgc.yHZsLzwc2nVpdICenLIGZK','administrator',NULL,NULL,'admin@tedu.cn',1,0,NULL,0,NULL,NULL,NULL),
(3,'liucs','$2a$10$O5KwU//RWm8EdRQkL3L/wOVUPMTdgc.yHZsLzwc2nVpdICenLIGZK','苍老师',NULL,NULL,'liucs@tedu.cn',1,0,NULL,0,NULL,NULL,NULL);

/*Table structure for table `ums_user_detail` */

DROP TABLE IF EXISTS `ums_user_detail`;

CREATE TABLE `ums_user_detail` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned DEFAULT NULL COMMENT '用户id',
  `day_of_birth` date DEFAULT NULL COMMENT '生日',
  `country` varchar(50) DEFAULT NULL COMMENT '国家',
  `province` varchar(50) DEFAULT NULL COMMENT '省',
  `city` varchar(50) DEFAULT NULL COMMENT '市',
  `district` varchar(50) DEFAULT NULL COMMENT '区',
  `education` varchar(50) DEFAULT NULL COMMENT '学历',
  `industry` varchar(50) DEFAULT NULL COMMENT '行业',
  `career` varchar(50) DEFAULT NULL COMMENT '职业',
  `gmt_create` datetime DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='用户详细（不常用）信息表';

/*Data for the table `ums_user_detail` */

insert  into `ums_user_detail`(`id`,`user_id`,`day_of_birth`,`country`,`province`,`city`,`district`,`education`,`industry`,`career`,`gmt_create`,`gmt_modified`) values 
(1,1,'1990-05-06','中国','湖北','武汉','汉口','本科','教育','一线员工',NULL,NULL),
(2,2,'1989-09-10','中国','广东省','深圳市','南山区','本科','计算机互联网','软件工程师',NULL,NULL),
(3,3,'1984-03-02','中国','北京','市辖区','海淀区','专科','国企','中层管理',NULL,NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
