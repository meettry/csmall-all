/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 8.0.26 : Database - mall_pms
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`mall_pms` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `mall_pms`;
/*Table structure for table `pms_album` */

DROP TABLE IF EXISTS `pms_album`;

CREATE TABLE `pms_album` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `name` varchar(50) DEFAULT NULL COMMENT '相册名称',
  `description` varchar(255) DEFAULT NULL COMMENT '相册简介',
  `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_album_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='相册';

/*Data for the table `pms_album` */

insert  into `pms_album`(`id`,`name`,`description`,`sort`,`gmt_create`,`gmt_modified`) values 
(1,'iPhone 13',NULL,0,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(2,'Mi 11 Ultra',NULL,0,'2022-04-06 14:13:31','2022-04-06 14:13:31');

/*Table structure for table `pms_attribute` */

DROP TABLE IF EXISTS `pms_attribute`;

CREATE TABLE `pms_attribute` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `template_id` bigint unsigned DEFAULT NULL COMMENT '所属属性模版id',
  `name` varchar(50) DEFAULT NULL COMMENT '属性名称',
  `description` varchar(255) DEFAULT NULL COMMENT '简介（某些属性名称可能相同，通过简介补充描述）',
  `type` tinyint unsigned DEFAULT '0' COMMENT '属性类型，1=销售属性，0=非销售属性',
  `input_type` tinyint unsigned DEFAULT '0' COMMENT '输入类型，0=手动录入，1=单选，2=多选，3=单选（下拉列表），4=多选（下拉列表）',
  `value_list` varchar(255) DEFAULT NULL COMMENT '备选值列表',
  `unit` varchar(50) DEFAULT NULL COMMENT '计量单位',
  `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
  `is_allow_customize` tinyint unsigned DEFAULT '0' COMMENT '是否允许自定义，1=允许，0=禁止',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='属性';

/*Data for the table `pms_attribute` */

insert  into `pms_attribute`(`id`,`template_id`,`name`,`description`,`type`,`input_type`,`value_list`,`unit`,`sort`,`is_allow_customize`,`gmt_create`,`gmt_modified`) values 
(1,1,'屏幕尺寸','智能手机屏幕尺寸',0,1,'6.1,6.3','英寸',0,1,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(2,4,'屏幕尺寸','笔记本电脑屏幕尺寸',0,1,'14,15','英寸',0,1,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(3,1,'颜色','智能手机颜色',1,1,'黑色,金色,白色',NULL,0,1,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(4,1,'颜色','衬衣颜色',1,1,'白色,蓝色,灰色,黑色',NULL,0,1,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(5,1,'运行内存','智能手机运行内存',1,1,'4,8,16','GB',0,1,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(6,1,'CPU型号','智能手机CPU型号',0,1,'骁龙870,骁龙880',NULL,0,1,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(7,1,'机身毛重','智能手机机身毛重',0,0,NULL,'g',0,0,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(8,1,'机身存储','智能手机机身存储',0,1,'64,128,256,512','GB',0,0,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(9,1,'操作系统','智能手机操作系统',0,1,'Android,iOS',NULL,0,0,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(10,4,'操作系统','电脑操作系统',0,1,'无,Windows 7,Windows 10,Ubuntu,Mac OS',NULL,0,0,'2022-04-06 14:13:31','2022-04-06 14:13:31');

/*Table structure for table `pms_attribute_template` */

DROP TABLE IF EXISTS `pms_attribute_template`;

CREATE TABLE `pms_attribute_template` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `name` varchar(50) DEFAULT NULL COMMENT '属性模版名称',
  `pinyin` varchar(50) DEFAULT NULL COMMENT '属性模版名称的拼音',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键词列表，各关键词使用英文的逗号分隔',
  `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_attribute_template_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='属性模版';

/*Data for the table `pms_attribute_template` */

insert  into `pms_attribute_template`(`id`,`name`,`pinyin`,`keywords`,`sort`,`gmt_create`,`gmt_modified`) values 
(1,'智能手机','zhinengshouji','手机',0,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(2,'服装-上身','fuzhuang','服装,上衣',0,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(3,'服装-裤子','fuzhuang','服装,裤',0,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(4,'笔记本电脑','bijibendiannao','电脑,笔记本',0,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(5,'台式电脑','taishidiannao','电脑,台式电脑,台式机',0,'2022-04-06 14:13:31','2022-04-06 14:13:31');

/*Table structure for table `pms_brand` */

DROP TABLE IF EXISTS `pms_brand`;

CREATE TABLE `pms_brand` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `name` varchar(50) DEFAULT NULL COMMENT '品牌名称',
  `pinyin` varchar(50) DEFAULT NULL COMMENT '品牌名称的拼音',
  `logo` varchar(255) DEFAULT NULL COMMENT '品牌logo的URL',
  `description` varchar(255) DEFAULT NULL COMMENT '品牌简介',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键词列表，各关键词使用英文的逗号分隔',
  `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
  `sales` int unsigned DEFAULT '0' COMMENT '销量（冗余）',
  `product_count` int unsigned DEFAULT '0' COMMENT '商品种类数量总和（冗余）',
  `comment_count` int unsigned DEFAULT '0' COMMENT '买家评论数量总和（冗余）',
  `positive_comment_count` int unsigned DEFAULT '0' COMMENT '买家好评数量总和（冗余）',
  `enable` tinyint unsigned DEFAULT '0' COMMENT '是否启用，1=启用，0=未启用',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_brand_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='品牌';

/*Data for the table `pms_brand` */

insert  into `pms_brand`(`id`,`name`,`pinyin`,`logo`,`description`,`keywords`,`sort`,`sales`,`product_count`,`comment_count`,`positive_comment_count`,`enable`,`gmt_create`,`gmt_modified`) values 
(1,'华为','huawei',NULL,'华为专注网络设备三十年','华为,huawei,mate,magicbook',0,0,0,0,0,1,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(2,'小米','xiaomi',NULL,'小米，为发烧而生','小米,xiaomi,发烧',0,0,0,0,0,1,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(3,'苹果','pingguo',NULL,'苹果，全球知名品牌','苹果,apple,pingguo,iphone,mac',0,0,0,0,0,1,'2022-04-06 14:13:31','2022-04-06 14:13:31');

/*Table structure for table `pms_brand_category` */

DROP TABLE IF EXISTS `pms_brand_category`;

CREATE TABLE `pms_brand_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `brand_id` bigint unsigned DEFAULT NULL COMMENT '品牌id',
  `category_id` bigint unsigned DEFAULT NULL COMMENT '类别id',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='品牌与类别关联';

/*Data for the table `pms_brand_category` */

insert  into `pms_brand_category`(`id`,`brand_id`,`category_id`,`gmt_create`,`gmt_modified`) values 
(1,1,3,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(2,2,3,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(3,3,3,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(4,1,8,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(5,2,8,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(6,3,8,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(7,1,9,'2022-04-06 14:13:31','2022-04-06 14:13:31'),
(8,3,9,'2022-04-06 14:13:31','2022-04-06 14:13:31');

/*Table structure for table `pms_category` */

DROP TABLE IF EXISTS `pms_category`;

CREATE TABLE `pms_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `name` varchar(50) DEFAULT NULL COMMENT '类别名称',
  `parent_id` bigint unsigned DEFAULT '0' COMMENT '父级类别id，如果无父级，则为0',
  `depth` tinyint unsigned DEFAULT '1' COMMENT '深度，最顶级类别的深度为1，次级为2，以此类推',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键词列表，各关键词使用英文的逗号分隔',
  `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标图片的URL',
  `enable` tinyint unsigned DEFAULT '0' COMMENT '是否启用，1=启用，0=未启用',
  `is_parent` tinyint unsigned DEFAULT '0' COMMENT '是否为父级（是否包含子级），1=是父级，0=不是父级',
  `is_display` tinyint unsigned DEFAULT '0' COMMENT '是否显示在导航栏中，1=启用，0=未启用',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='类别';

/*Data for the table `pms_category` */

insert  into `pms_category`(`id`,`name`,`parent_id`,`depth`,`keywords`,`sort`,`icon`,`enable`,`is_parent`,`is_display`,`gmt_create`,`gmt_modified`) values 
(1,'手机 / 运营商 / 数码',0,1,NULL,0,'http://csmall.org//brand/7/2/0/8/0/c/c/1/57d44546-e22a-476c-b191-52d19d113343.jpg',1,1,1,'2022-04-06 14:16:24','2022-04-06 14:16:32'),
(2,'手机通讯',1,2,'手机,电话',0,'http://csmall.org//brand/7/2/0/8/0/c/c/1/57d44546-e22a-476c-b191-52d19d113343.jpg',1,1,1,'2022-04-06 14:16:24','2022-04-06 14:16:32'),
(3,'智能手机',2,3,NULL,0,'http://csmall.org//brand/7/2/0/8/0/c/c/1/57d44546-e22a-476c-b191-52d19d113343.jpg',1,0,1,'2022-04-06 14:16:24','2022-04-06 14:16:32'),
(4,'非智能手机',2,3,NULL,0,'http://csmall.org//brand/7/2/0/8/0/c/c/1/57d44546-e22a-476c-b191-52d19d113343.jpg',1,0,1,'2022-04-06 14:16:24','2022-04-06 14:16:32'),
(5,'电脑 / 办公',0,1,NULL,0,'http://csmall.org//brand/7/2/0/8/0/c/c/1/57d44546-e22a-476c-b191-52d19d113343.jpg',1,1,1,'2022-04-06 14:16:24','2022-04-06 14:16:32'),
(6,'电脑整机',5,2,'电脑,计算机,微机,服务器,工作站',0,'http://csmall.org//brand/7/2/0/8/0/c/c/1/57d44546-e22a-476c-b191-52d19d113343.jpg',1,1,1,'2022-04-06 14:16:24','2022-04-06 14:16:32'),
(7,'电脑配件',5,2,'配件,组装,CPU,内存,硬盘',0,'http://csmall.org//brand/7/2/0/8/0/c/c/1/57d44546-e22a-476c-b191-52d19d113343.jpg',1,1,1,'2022-04-06 14:16:24','2022-04-06 14:16:32'),
(8,'笔记本',6,3,'电脑,笔记本,微机,便携',0,'http://csmall.org//brand/7/2/0/8/0/c/c/1/57d44546-e22a-476c-b191-52d19d113343.jpg',1,0,1,'2022-04-06 14:16:24','2022-04-06 14:16:32'),
(9,'台式机 / 一体机',6,3,'台式机,一体机',0,'http://csmall.org//brand/7/2/0/8/0/c/c/1/57d44546-e22a-476c-b191-52d19d113343.jpg',1,0,1,'2022-04-06 14:16:24','2022-04-06 14:16:32');

/*Table structure for table `pms_category_attribute_template` */

DROP TABLE IF EXISTS `pms_category_attribute_template`;

CREATE TABLE `pms_category_attribute_template` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `category_id` bigint unsigned DEFAULT NULL COMMENT '类别id',
  `attribute_template_id` bigint unsigned DEFAULT NULL COMMENT '属性模版id',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='类别与属性模版关联';

/*Data for the table `pms_category_attribute_template` */

insert  into `pms_category_attribute_template`(`id`,`category_id`,`attribute_template_id`,`gmt_create`,`gmt_modified`) values 
(1,3,1,'2022-04-06 14:13:31','2022-04-06 14:13:31');

/*Table structure for table `pms_picture` */

DROP TABLE IF EXISTS `pms_picture`;

CREATE TABLE `pms_picture` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `album_id` bigint unsigned DEFAULT NULL COMMENT '相册id',
  `url` varchar(255) DEFAULT NULL COMMENT '图片url',
  `description` varchar(255) DEFAULT NULL COMMENT '图片简介',
  `width` smallint unsigned DEFAULT NULL COMMENT '图片宽度，单位：px',
  `height` smallint unsigned DEFAULT NULL COMMENT '图片高度，单位：px',
  `is_cover` tinyint unsigned DEFAULT '0' COMMENT '是否为封面图片，1=是，0=否',
  `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图片';

/*Data for the table `pms_picture` */

insert  into `pms_picture`(`id`,`album_id`,`url`,`description`,`width`,`height`,`is_cover`,`sort`,`gmt_create`,`gmt_modified`) values 
(1,1,'模拟数据：iPhone 13图片URL-1',NULL,1024,768,0,0,'2022-04-06 14:17:31','2022-04-06 14:17:10'),
(2,1,'模拟数据：iPhone 13图片URL-2',NULL,1024,768,0,0,'2022-04-06 14:17:31','2022-04-06 14:17:10'),
(3,1,'模拟数据：iPhone 13图片URL-3',NULL,1024,768,0,0,'2022-04-06 14:17:31','2022-04-06 14:17:10'),
(4,1,'模拟数据：iPhone 13图片URL-4',NULL,1024,768,0,0,'2022-04-06 14:17:31','2022-04-06 14:17:10'),
(5,1,'模拟数据：iPhone 13图片URL-5',NULL,1024,768,0,0,'2022-04-06 14:17:31','2022-04-06 14:17:10'),
(6,2,'模拟数据：Mi 11 Ultra图片URL-1',NULL,1024,768,0,0,'2022-04-06 14:17:31','2022-04-06 14:17:10'),
(7,2,'模拟数据：Mi 11 Ultra图片URL-2',NULL,1024,768,0,0,'2022-04-06 14:17:31','2022-04-06 14:17:10'),
(8,2,'模拟数据：Mi 11 Ultra图片URL-3',NULL,1024,768,0,0,'2022-04-06 14:17:31','2022-04-06 14:17:10'),
(9,2,'模拟数据：Mi 11 Ultra图片URL-4',NULL,1024,768,0,0,'2022-04-06 14:17:31','2022-04-06 14:17:10'),
(10,2,'模拟数据：Mi 11 Ultra图片URL-5',NULL,1024,768,0,0,'2022-04-06 14:17:31','2022-04-06 14:17:10');

/*Table structure for table `pms_sku` */

DROP TABLE IF EXISTS `pms_sku`;

CREATE TABLE `pms_sku` (
  `id` bigint unsigned NOT NULL COMMENT '记录id',
  `spu_id` bigint unsigned DEFAULT NULL COMMENT 'SPU id',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `bar_code` varchar(255) DEFAULT NULL COMMENT '条型码',
  `attribute_template_id` bigint unsigned DEFAULT NULL COMMENT '属性模版id',
  `specifications` varchar(2500) DEFAULT NULL COMMENT '全部属性，使用JSON格式表示（冗余）',
  `album_id` bigint unsigned DEFAULT NULL COMMENT '相册id',
  `pictures` varchar(500) DEFAULT NULL COMMENT '组图URLs，使用JSON格式表示',
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `stock` int unsigned DEFAULT '0' COMMENT '当前库存',
  `stock_threshold` int unsigned DEFAULT '0' COMMENT '库存预警阈值',
  `sales` int unsigned DEFAULT '0' COMMENT '销量（冗余）',
  `comment_count` int unsigned DEFAULT '0' COMMENT '买家评论数量总和（冗余）',
  `positive_comment_count` int unsigned DEFAULT '0' COMMENT '买家好评数量总和（冗余）',
  `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SKU（Stock Keeping Unit）';

/*Data for the table `pms_sku` */

insert  into `pms_sku`(`id`,`spu_id`,`title`,`bar_code`,`attribute_template_id`,`specifications`,`album_id`,`pictures`,`price`,`stock`,`stock_threshold`,`sales`,`comment_count`,`positive_comment_count`,`sort`,`gmt_create`,`gmt_modified`) values 
(1,2,'2021年新款，小米11 Ultra黑色512G，16G超大内存120Hz高刷67w快充',NULL,1,'{\"attributes\":[{\"id\":1,\"name\":\"屏幕尺寸\",\"value\":\"6.1寸\"},{\"id\":3,\"name\":\"颜色\",\"value\":\"黑色\"},{\"id\":5,\"name\":\"运行内存\",\"value\":\"16GB\"}]}',NULL,NULL,6999.99,3000,50,0,0,0,0,'2022-04-06 14:17:52','2022-04-06 14:17:52'),
(2,2,'2021年新款，小米11 Ultra白色512G，8G超大内存120Hz高刷67w快充',NULL,1,'{\"attributes\":[{\"id\":1,\"name\":\"屏幕尺寸\",\"value\":\"6.1寸\"},{\"id\":3,\"name\":\"颜色\",\"value\":\"白色\"},{\"id\":5,\"name\":\"运行内存\",\"value\":\"8GB\"}]}',NULL,NULL,6499.99,3000,50,0,0,0,0,'2022-04-06 14:17:52','2022-04-06 14:17:52');

/*Table structure for table `pms_sku_specification` */

DROP TABLE IF EXISTS `pms_sku_specification`;

CREATE TABLE `pms_sku_specification` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `sku_id` bigint unsigned DEFAULT NULL COMMENT 'SKU id',
  `attribute_id` bigint unsigned DEFAULT NULL COMMENT '属性id',
  `attribute_name` varchar(50) DEFAULT NULL COMMENT '属性名称',
  `attribute_value` varchar(50) DEFAULT NULL COMMENT '属性值',
  `unit` varchar(10) DEFAULT NULL COMMENT '自动补充的计量单位',
  `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SKU数据';

/*Data for the table `pms_sku_specification` */

insert  into `pms_sku_specification`(`id`,`sku_id`,`attribute_id`,`attribute_name`,`attribute_value`,`unit`,`sort`,`gmt_create`,`gmt_modified`) values 
(1,1,1,'屏幕尺寸','6.1','寸',0,'2022-04-06 14:18:06','2022-04-06 14:18:06'),
(2,1,3,'颜色','黑色',NULL,0,'2022-04-06 14:18:06','2022-04-06 14:18:06'),
(3,1,5,'运行内存','16','GB',0,'2022-04-06 14:18:06','2022-04-06 14:18:06'),
(4,2,1,'屏幕尺寸','6.1','寸',0,'2022-04-06 14:18:06','2022-04-06 14:18:06'),
(5,2,3,'颜色','白色',NULL,0,'2022-04-06 14:18:06','2022-04-06 14:18:06'),
(6,2,5,'运行内存','8','GB',0,'2022-04-06 14:18:06','2022-04-06 14:18:06');

/*Table structure for table `pms_spu` */

DROP TABLE IF EXISTS `pms_spu`;

CREATE TABLE `pms_spu` (
  `id` bigint unsigned NOT NULL COMMENT '记录id',
  `name` varchar(50) DEFAULT NULL COMMENT 'SPU名称',
  `type_number` varchar(50) DEFAULT NULL COMMENT 'SPU编号',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `description` varchar(255) DEFAULT NULL COMMENT '简介',
  `list_price` decimal(10,2) DEFAULT NULL COMMENT '价格（显示在列表中）',
  `stock` int unsigned DEFAULT '0' COMMENT '当前库存（冗余）',
  `stock_threshold` int unsigned DEFAULT '0' COMMENT '库存预警阈值（冗余）',
  `unit` varchar(50) DEFAULT NULL COMMENT '计件单位',
  `brand_id` bigint unsigned DEFAULT NULL COMMENT '品牌id',
  `brand_name` varchar(50) DEFAULT NULL COMMENT '品牌名称（冗余）',
  `category_id` bigint unsigned DEFAULT NULL COMMENT '类别id',
  `category_name` varchar(50) DEFAULT NULL COMMENT '类别名称（冗余）',
  `attribute_template_id` bigint unsigned DEFAULT NULL COMMENT '属性模版id',
  `album_id` bigint unsigned DEFAULT NULL COMMENT '相册id',
  `pictures` varchar(500) DEFAULT NULL COMMENT '组图URLs，使用JSON数组表示',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键词列表，各关键词使用英文的逗号分隔',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签列表，各标签使用英文的逗号分隔，原则上最多3个',
  `sales` int unsigned DEFAULT '0' COMMENT '销量（冗余）',
  `comment_count` int unsigned DEFAULT '0' COMMENT '买家评论数量总和（冗余）',
  `positive_comment_count` int unsigned DEFAULT '0' COMMENT '买家好评数量总和（冗余）',
  `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
  `is_deleted` tinyint unsigned DEFAULT '0' COMMENT '是否标记为删除，1=已删除，0=未删除',
  `is_published` tinyint unsigned DEFAULT '0' COMMENT '是否上架（发布），1=已上架，0=未上架（下架）',
  `is_new_arrival` tinyint unsigned DEFAULT '0' COMMENT '是否新品，1=新品，0=非新品',
  `is_recommend` tinyint unsigned DEFAULT '0' COMMENT '是否推荐，1=推荐，0=不推荐',
  `is_checked` tinyint unsigned DEFAULT '0' COMMENT '是否已审核，1=已审核，0=未审核',
  `check_user` varchar(50) DEFAULT NULL COMMENT '审核人（冗余）',
  `gmt_check` datetime NULL DEFAULT NULL COMMENT '审核通过时间（冗余）',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SPU（Standard Product Unit）';

/*Data for the table `pms_spu` */

insert  into `pms_spu`(`id`,`name`,`type_number`,`title`,`description`,`list_price`,`stock`,`stock_threshold`,`unit`,`brand_id`,`brand_name`,`category_id`,`category_name`,`attribute_template_id`,`album_id`,`pictures`,`keywords`,`tags`,`sales`,`comment_count`,`positive_comment_count`,`sort`,`is_deleted`,`is_published`,`is_new_arrival`,`is_recommend`,`is_checked`,`check_user`,`gmt_check`,`gmt_create`,`gmt_modified`) values 
(1,'iPhone 13','A2404','苹果手机iPhone 13（A2404）','2021年新款，全网首发',5199.99,5000,20,'部',3,'苹果',3,'智能手机',NULL,NULL,NULL,'ip13,iPhone13,苹果13','20w快充,NFC,无线充电',0,0,0,0,0,1,0,0,1,NULL,NULL,'2022-04-06 14:18:16','2022-04-06 14:18:16'),
(2,'小米11 Ultra','M112021','小米11 Ultra（M112021）','2021年最新旗舰机',5899.99,8000,20,'部',2,'小米',3,'智能手机',NULL,NULL,NULL,'mi11,xiaomi11,ultra','67w快充,1亿像素,5000毫安电池',0,0,0,0,0,1,0,0,1,NULL,NULL,'2022-04-06 14:18:16','2022-04-06 14:18:16');

/*Table structure for table `pms_spu_detail` */

DROP TABLE IF EXISTS `pms_spu_detail`;

CREATE TABLE `pms_spu_detail` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `spu_id` bigint unsigned DEFAULT NULL COMMENT 'SPU id',
  `detail` text COMMENT 'SPU详情，应该使用HTML富文本，通常内容是若干张图片',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '数据创建时间',
  `gmt_modified` datetime NULL DEFAULT NULL COMMENT '数据最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SPU详情';

/*Data for the table `pms_spu_detail` */

insert  into `pms_spu_detail`(`id`,`spu_id`,`detail`,`gmt_create`,`gmt_modified`) values 
(1,1,'<div>iPhone 13的详情HTML</div>','2022-04-06 14:18:25','2022-04-06 14:18:25'),
(2,2,'<div>小米11 Ultra的详情HTML</div>','2022-04-06 14:18:25','2022-04-06 14:18:25');

/*Table structure for table `undo_log` */

DROP TABLE IF EXISTS `undo_log`;

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
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8mb3;

/*Data for the table `undo_log` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
