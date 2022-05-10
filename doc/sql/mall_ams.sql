/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 5.7.33-log : Database - mall_ams
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`mall_ams` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `mall_ams`;
/*Table structure for table `ams_admin` */

DROP TABLE IF EXISTS `ams_admin`;

CREATE TABLE `ams_admin` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` char(64) DEFAULT NULL COMMENT '密码（密文）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(50) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(50) DEFAULT NULL COMMENT '电子邮箱',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `is_enable` tinyint(3) unsigned DEFAULT '0' COMMENT '是否启用，1=启用，0=未启用',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP地址（冗余）',
  `login_count` int(10) unsigned DEFAULT '0' COMMENT '累计登录次数（冗余）',
  `gmt_last_login` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后登录时间（冗余）',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

/*Data for the table `ams_admin` */

insert  into `ams_admin`(`id`,`username`,`password`,`nickname`,`avatar`,`phone`,`email`,`description`,`is_enable`,`last_login_ip`,`login_count`,`gmt_last_login`,`gmt_create`,`gmt_modified`) values
(1,'root','$2a$10$O5KwU//RWm8EdRQkL3L/wOVUPMTdgc.yHZsLzwc2nVpdICenLIGZK','root',NULL,'18510273000','root@tedu.cn','最高管理员',1,NULL,0,'2021-12-02 17:59:27','2021-12-02 17:59:27','2021-12-02 17:59:27'),
(2,'super_admin','$2a$10$O5KwU//RWm8EdRQkL3L/wOVUPMTdgc.yHZsLzwc2nVpdICenLIGZK','administrator',NULL,'18510273001','admin@tedu.cn','超级管理员',1,NULL,0,'2021-12-02 17:59:27','2021-12-02 17:59:27','2021-12-02 17:59:27'),
(3,'nobody','$2a$10$O5KwU//RWm8EdRQkL3L/wOVUPMTdgc.yHZsLzwc2nVpdICenLIGZK','无名',NULL,'18510273002','liucs@tedu.cn',NULL,0,NULL,0,'2021-12-02 17:59:27','2021-12-02 17:59:27','2021-12-02 17:59:27'),
(8,'dfsgdgrehberwhgre','$2a$10$HRba5IwiRa9CgUNXIt3FWupPda5Jk7WqLGYwXtyTkuHLEnU744Yw2','','dsafsa','12353452341','','',0,NULL,0,'2021-12-20 16:23:04','2021-12-20 16:23:04','2021-12-20 16:23:04');

/*Table structure for table `ams_admin_role` */

DROP TABLE IF EXISTS `ams_admin_role`;

CREATE TABLE `ams_admin_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) unsigned DEFAULT NULL COMMENT '管理员id',
  `role_id` bigint(20) unsigned DEFAULT NULL COMMENT '角色id',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色关联表';

/*Data for the table `ams_admin_role` */

insert  into `ams_admin_role`(`id`,`admin_id`,`role_id`,`gmt_create`,`gmt_modified`) values
(4,2,2,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(5,2,3,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(6,2,4,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(7,3,3,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(8,1,1,'2021-12-20 17:07:30','2021-12-20 17:07:30'),
(9,1,2,'2021-12-20 17:07:30','2021-12-20 17:07:30'),
(10,1,3,'2021-12-20 17:07:30','2021-12-20 17:07:30');

/*Table structure for table `ams_login_log` */

DROP TABLE IF EXISTS `ams_login_log`;

CREATE TABLE `ams_login_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) unsigned DEFAULT NULL COMMENT '管理员id',
  `username` varchar(50) DEFAULT NULL COMMENT '管理员用户名（冗余）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '管理员昵称（冗余）',
  `ip` varchar(50) DEFAULT NULL COMMENT '登录IP地址',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '浏览器内核',
  `gmt_login` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='管理员登录日志表';

/*Data for the table `ams_login_log` */

insert  into `ams_login_log`(`id`,`admin_id`,`username`,`nickname`,`ip`,`user_agent`,`gmt_login`,`gmt_create`,`gmt_modified`) values
(1,1,'root','root','127.0.0.1','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15','2021-12-01 17:59:27','2021-12-02 17:59:27','2021-12-02 17:59:27'),
(2,2,'root','root','127.0.0.1','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15','2021-12-02 05:59:27','2021-12-02 17:59:27','2021-12-02 17:59:27'),
(3,3,'root','root','127.0.0.1','Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15','2021-12-02 17:59:27','2021-12-02 17:59:27','2021-12-02 17:59:27');

/*Table structure for table `ams_permission` */

DROP TABLE IF EXISTS `ams_permission`;

CREATE TABLE `ams_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `value` varchar(255) DEFAULT NULL COMMENT '值',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `sort` tinyint(3) unsigned DEFAULT '0' COMMENT '自定义排序序号',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

/*Data for the table `ams_permission` */

insert  into `ams_permission`(`id`,`name`,`value`,`description`,`sort`,`gmt_create`,`gmt_modified`) values
(1,'商品-商品管理-读取','/pms/product/read','读取商品数据，含列表、详情、查询等',0,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(2,'商品-商品管理-编辑','/pms/product/update','修改商品数据',0,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(3,'商品-商品管理-删除','/pms/product/delete','删除商品数据',0,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(4,'后台管理-管理员-读取','/ams/admin/read','读取管理员数据，含列表、详情、查询等',0,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(5,'后台管理-管理员-编辑','/ams/admin/update','编辑管理员数据',0,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(6,'后台管理-管理员-删除','/ams/admin/delete','删除管理员数据',0,'2021-12-02 17:59:27','2021-12-02 17:59:27');

/*Table structure for table `ams_role` */

DROP TABLE IF EXISTS `ams_role`;

CREATE TABLE `ams_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `sort` tinyint(3) unsigned DEFAULT '0' COMMENT '自定义排序序号',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

/*Data for the table `ams_role` */

insert  into `ams_role`(`id`,`name`,`description`,`sort`,`gmt_create`,`gmt_modified`) values
(1,'超级管理员',NULL,0,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(2,'系统管理员',NULL,0,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(3,'商品管理员',NULL,0,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(4,'订单管理员',NULL,0,'2021-12-02 17:59:27','2021-12-02 17:59:27');

/*Table structure for table `ams_role_permission` */

DROP TABLE IF EXISTS `ams_role_permission`;

CREATE TABLE `ams_role_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) unsigned DEFAULT NULL COMMENT '角色id',
  `permission_id` bigint(20) unsigned DEFAULT NULL COMMENT '权限id',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COMMENT='ams_role_permission';

/*Data for the table `ams_role_permission` */

insert  into `ams_role_permission`(`id`,`role_id`,`permission_id`,`gmt_create`,`gmt_modified`) values
(1,1,1,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(2,1,2,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(3,1,3,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(4,1,4,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(5,1,5,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(6,1,6,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(7,2,1,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(8,2,2,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(9,2,3,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(10,2,4,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(11,2,5,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(12,2,6,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(13,3,1,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(14,3,2,'2021-12-02 17:59:27','2021-12-02 17:59:27'),
(15,3,3,'2021-12-02 17:59:27','2021-12-02 17:59:27');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
