/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 5.7.33-log : Database - mall_seckill
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`mall_seckill` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `mall_seckill`;
/*Table structure for table `seckill_sku` */

DROP TABLE IF EXISTS `seckill_sku`;

CREATE TABLE `seckill_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '表主键',
  `sku_id` bigint(20) DEFAULT NULL COMMENT '秒杀商品sku关联id',
  `spu_id` bigint(20) DEFAULT NULL COMMENT '秒杀商品spu关联id',
  `seckill_stock` int(11) DEFAULT NULL COMMENT '秒杀商品库存数',
  `seckill_price` decimal(10,0) DEFAULT NULL COMMENT '秒杀价钱',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `seckill_limit` int(11) DEFAULT '0' COMMENT '秒杀商品数量限制,最多一个用户可以秒杀的商品个数,如果是0表示没有限制',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

/*Data for the table `seckill_sku` */

insert  into `seckill_sku`(`id`,`sku_id`,`spu_id`,`seckill_stock`,`seckill_price`,`gmt_create`,`gmt_modified`,`seckill_limit`) values 
(1,1,2,0,1000,'2022-02-22 16:43:09','2022-02-28 16:17:44',1),
(2,2,2,9,800,'2022-02-22 16:43:09','2022-02-28 16:28:42',1);

/*Table structure for table `seckill_spu` */

DROP TABLE IF EXISTS `seckill_spu`;

CREATE TABLE `seckill_spu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `spu_id` bigint(20) DEFAULT NULL,
  `list_price` decimal(11,0) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

/*Data for the table `seckill_spu` */

insert  into `seckill_spu`(`id`,`spu_id`,`list_price`,`start_time`,`end_time`,`gmt_create`,`gmt_modified`) values 
(1,2,2000,'2022-02-23 16:51:40','2022-03-01 16:51:42','2022-02-23 16:51:45','2022-02-23 16:51:48');

/*Table structure for table `success` */

DROP TABLE IF EXISTS `success`;

CREATE TABLE `success` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `seckill_id` bigint(11) DEFAULT NULL COMMENT '关联秒杀id',
  `user_id` bigint(11) DEFAULT NULL COMMENT '用户id',
  `user_phone` varchar(50) DEFAULT NULL COMMENT '手机号码(冗余)',
  `sku_id` bigint(11) DEFAULT NULL COMMENT 'SKU id',
  `title` varchar(255) DEFAULT NULL COMMENT '商品SKU标题（冗余）',
  `main_picture` varchar(255) DEFAULT NULL COMMENT '商品SKU图片URL（第1张）',
  `seckill_price` decimal(10,2) DEFAULT NULL COMMENT '秒杀商品单价(加入时)',
  `quantity` smallint(3) DEFAULT NULL COMMENT '秒杀商品数量',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据最后修改时间',
  `bar_code` varchar(255) DEFAULT NULL,
  `data` varchar(2500) DEFAULT NULL,
  `order_sn` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='秒杀成功表格';

/*Data for the table `success` */

insert  into `success`(`id`,`seckill_id`,`user_id`,`user_phone`,`sku_id`,`title`,`main_picture`,`seckill_price`,`quantity`,`gmt_create`,`gmt_modified`,`bar_code`,`data`,`order_sn`) values 
(2,NULL,1,NULL,1,'2021年新款，小米11 Ultra黑色512G，16G超大内存120Hz高刷67w快充','picture1',6999.99,10,'2022-02-28 16:17:44','2022-02-28 16:17:44','0','{\"attributes\":[{\"id\":1,\"name\":\"屏幕尺寸\",\"value\":\"6.1寸\"},{\"id\":3,\"name\":\"颜色\",\"value\":\"黑色\"},{\"id\":5,\"name\":\"运行内存\",\"value\":\"16GB\"}]}',NULL),
(3,NULL,1,NULL,2,'2021年新款，小米11 Ultra黑色512G，16G超大内存120Hz高刷67w快充','picture1',6999.99,1,'2022-02-28 16:28:42','2022-02-28 16:28:42','0','{\"attributes\":[{\"id\":1,\"name\":\"屏幕尺寸\",\"value\":\"6.1寸\"},{\"id\":3,\"name\":\"颜色\",\"value\":\"黑色\"},{\"id\":5,\"name\":\"运行内存\",\"value\":\"16GB\"}]}',NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
