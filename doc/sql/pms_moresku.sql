/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 导出 mall_pms 的数据库结构
DROP DATABASE IF EXISTS `mall_pms`;
CREATE DATABASE IF NOT EXISTS `mall_pms`;
USE `mall_pms`;

-- 导出  表 mall_pms.pms_album 结构
DROP TABLE IF EXISTS `pms_album`;
CREATE TABLE IF NOT EXISTS `pms_album` (
                                           `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
                                           `name` varchar(50) DEFAULT NULL COMMENT '相册名称',
    `description` varchar(255) DEFAULT NULL COMMENT '相册简介',
    `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
    `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
    `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_album_name` (`name`)
    ) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4  COMMENT='相册';

-- 正在导出表  mall_pms.pms_album 的数据：~5 rows (大约)
/*!40000 ALTER TABLE `pms_album` DISABLE KEYS */;
INSERT INTO `pms_album` (`id`, `name`, `description`, `sort`, `gmt_create`, `gmt_modified`) VALUES
(1, 'iPhone 13', NULL, 0, NULL, NULL),
(2, 'Mi 11 Ultra', NULL, 0, NULL, NULL),
(3, '华为P50pro', NULL, 0, NULL, NULL),
(4, 'mac Book Air', NULL, 0, NULL, NULL),
(5, '华为 MateBook', NULL, 0, NULL, NULL);
/*!40000 ALTER TABLE `pms_album` ENABLE KEYS */;

-- 导出  表 mall_pms.pms_attribute 结构
DROP TABLE IF EXISTS `pms_attribute`;
CREATE TABLE IF NOT EXISTS `pms_attribute` (
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
    `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
    `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4  COMMENT='属性';

-- 正在导出表  mall_pms.pms_attribute 的数据：~6 rows (大约)
/*!40000 ALTER TABLE `pms_attribute` DISABLE KEYS */;
INSERT INTO `pms_attribute` (`id`, `template_id`, `name`, `description`, `type`, `input_type`, `value_list`, `unit`, `sort`, `is_allow_customize`, `gmt_create`, `gmt_modified`) VALUES
(1, 1, '屏幕尺寸', '智能手机屏幕尺寸', 0, 1, '6.1寸,6.3寸', '英寸', 0, 1, NULL, NULL),
(2, 2, '屏幕尺寸', '笔记本电脑屏幕尺寸', 0, 1, '14寸,15寸', '英寸', 0, 1, NULL, NULL),
(3, 1, '颜色', '智能手机颜色', 0, 1, '黑色,金色,白色', NULL, 0, 1, NULL, NULL),
(5, 1, '运行内存', '智能手机运行内存', 0, 1, '4GB,8GB,16GB', 'GB', 0, 1, NULL, NULL),
(6, 2, 'CPU型号', '笔记本CPU型号', 0, 1, 'i3,i5,i7,m1', NULL, 0, 1, NULL, NULL),
(10, 2, '操作系统', '电脑操作系统', 0, 1, '无,Windows 7,Windows 10,Ubuntu,Mac OS', NULL, 0, 0, NULL, NULL);
/*!40000 ALTER TABLE `pms_attribute` ENABLE KEYS */;

-- 导出  表 mall_pms.pms_attribute_template 结构
DROP TABLE IF EXISTS `pms_attribute_template`;
CREATE TABLE IF NOT EXISTS `pms_attribute_template` (
                                                        `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
                                                        `name` varchar(50) DEFAULT NULL COMMENT '属性模版名称',
    `pinyin` varchar(50) DEFAULT NULL COMMENT '属性模版名称的拼音',
    `keywords` varchar(255) DEFAULT NULL COMMENT '关键词列表，各关键词使用英文的逗号分隔',
    `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
    `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
    `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_attribute_template_name` (`name`)
    ) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4  COMMENT='属性模版';

-- 正在导出表  mall_pms.pms_attribute_template 的数据：~2 rows (大约)
/*!40000 ALTER TABLE `pms_attribute_template` DISABLE KEYS */;
INSERT INTO `pms_attribute_template` (`id`, `name`, `pinyin`, `keywords`, `sort`, `gmt_create`, `gmt_modified`) VALUES
(1, '智能手机', 'zhinengshouji', '手机', 0, NULL, NULL),
(2, '笔记本', 'bijiben', '笔记本', 0, NULL, NULL);
/*!40000 ALTER TABLE `pms_attribute_template` ENABLE KEYS */;

-- 导出  表 mall_pms.pms_brand 结构
DROP TABLE IF EXISTS `pms_brand`;
CREATE TABLE IF NOT EXISTS `pms_brand` (
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
    `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
    `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_brand_name` (`name`)
    ) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4  COMMENT='品牌';

-- 正在导出表  mall_pms.pms_brand 的数据：~3 rows (大约)
/*!40000 ALTER TABLE `pms_brand` DISABLE KEYS */;
INSERT INTO `pms_brand` (`id`, `name`, `pinyin`, `logo`, `description`, `keywords`, `sort`, `sales`, `product_count`, `comment_count`, `positive_comment_count`, `enable`, `gmt_create`, `gmt_modified`) VALUES
(1, '华为', 'huawei', NULL, '华为专注网络设备三十年', '华为,huawei,mate,magicbook', 0, 0, 0, 0, 0, 1, NULL, NULL),
(2, '小米', 'xiaomi', NULL, '小米，为发烧而生', '小米,xiaomi,发烧', 0, 0, 0, 0, 0, 1, NULL, NULL),
(3, '苹果', 'pingguo', NULL, '苹果，全球知名品牌', '苹果,apple,pingguo,iphone,mac', 0, 0, 0, 0, 0, 1, NULL, NULL);
/*!40000 ALTER TABLE `pms_brand` ENABLE KEYS */;

-- 导出  表 mall_pms.pms_brand_category 结构
DROP TABLE IF EXISTS `pms_brand_category`;
CREATE TABLE IF NOT EXISTS `pms_brand_category` (
                                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
                                                    `brand_id` bigint unsigned DEFAULT NULL COMMENT '品牌id',
                                                    `category_id` bigint unsigned DEFAULT NULL COMMENT '类别id',
                                                    `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
                                                    `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
                                                    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4  COMMENT='品牌与类别关联';

-- 正在导出表  mall_pms.pms_brand_category 的数据：~8 rows (大约)
/*!40000 ALTER TABLE `pms_brand_category` DISABLE KEYS */;
INSERT INTO `pms_brand_category` (`id`, `brand_id`, `category_id`, `gmt_create`, `gmt_modified`) VALUES
(1, 1, 3, NULL, NULL),
(2, 2, 3, NULL, NULL),
(3, 3, 3, NULL, NULL),
(4, 1, 8, NULL, NULL),
(5, 2, 8, NULL, NULL),
(6, 3, 8, NULL, NULL),
(7, 1, 9, NULL, NULL),
(8, 3, 9, NULL, NULL);
/*!40000 ALTER TABLE `pms_brand_category` ENABLE KEYS */;

-- 导出  表 mall_pms.pms_category 结构
DROP TABLE IF EXISTS `pms_category`;
CREATE TABLE IF NOT EXISTS `pms_category` (
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
    `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
    `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_name` (`name`)
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4  COMMENT='类别';

-- 正在导出表  mall_pms.pms_category 的数据：~9 rows (大约)
/*!40000 ALTER TABLE `pms_category` DISABLE KEYS */;
INSERT INTO `pms_category` (`id`, `name`, `parent_id`, `depth`, `keywords`, `sort`, `icon`, `enable`, `is_parent`, `is_display`, `gmt_create`, `gmt_modified`) VALUES
(1, '手机 / 运营商 / 数码', 0, 1, NULL, 0, NULL, 1, 1, 1, NULL, NULL),
(2, '手机通讯', 1, 2, '手机,电话', 0, NULL, 1, 1, 1, NULL, NULL),
(3, '智能手机', 2, 3, NULL, 0, NULL, 1, 0, 1, NULL, NULL),
(4, '非智能手机', 2, 3, NULL, 0, NULL, 1, 0, 1, NULL, NULL),
(5, '电脑 / 办公', 0, 1, NULL, 0, NULL, 1, 1, 1, NULL, NULL),
(6, '电脑整机', 5, 2, '电脑,计算机,微机,服务器,工作站', 0, NULL, 1, 1, 1, NULL, NULL),
(7, '电脑配件', 5, 2, '配件,组装,CPU,内存,硬盘', 0, NULL, 1, 1, 1, NULL, NULL),
(8, '笔记本', 6, 3, '电脑,笔记本,微机,便携', 0, NULL, 1, 0, 1, NULL, NULL),
(9, '台式机', 6, 3, '台式机,一体机', 0, NULL, 1, 0, 1, NULL, NULL);
/*!40000 ALTER TABLE `pms_category` ENABLE KEYS */;

-- 导出  表 mall_pms.pms_category_attribute_template 结构
DROP TABLE IF EXISTS `pms_category_attribute_template`;
CREATE TABLE IF NOT EXISTS `pms_category_attribute_template` (
                                                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
                                                                 `category_id` bigint unsigned DEFAULT NULL COMMENT '类别id',
                                                                 `attribute_template_id` bigint unsigned DEFAULT NULL COMMENT '属性模版id',
                                                                 `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
                                                                 `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
                                                                 PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4  COMMENT='类别与属性模版关联';

-- 正在导出表  mall_pms.pms_category_attribute_template 的数据：~2 rows (大约)
/*!40000 ALTER TABLE `pms_category_attribute_template` DISABLE KEYS */;
INSERT INTO `pms_category_attribute_template` (`id`, `category_id`, `attribute_template_id`, `gmt_create`, `gmt_modified`) VALUES
(1, 3, 1, '2022-03-31 11:52:13', '2022-03-31 11:52:15'),
(2, 8, 2, '2022-03-31 11:52:13', '2022-03-31 11:52:15');
/*!40000 ALTER TABLE `pms_category_attribute_template` ENABLE KEYS */;

-- 导出  表 mall_pms.pms_picture 结构
DROP TABLE IF EXISTS `pms_picture`;
CREATE TABLE IF NOT EXISTS `pms_picture` (
                                             `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
                                             `album_id` bigint unsigned DEFAULT NULL COMMENT '相册id',
                                             `url` varchar(255) DEFAULT NULL COMMENT '图片url',
    `description` varchar(255) DEFAULT NULL COMMENT '图片简介',
    `width` smallint unsigned DEFAULT NULL COMMENT '图片宽度，单位：px',
    `height` smallint unsigned DEFAULT NULL COMMENT '图片高度，单位：px',
    `is_cover` tinyint unsigned DEFAULT '0' COMMENT '是否为封面图片，1=是，0=否',
    `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
    `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
    `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4  COMMENT='图片';

-- 正在导出表  mall_pms.pms_picture 的数据：~10 rows (大约)
/*!40000 ALTER TABLE `pms_picture` DISABLE KEYS */;
INSERT INTO `pms_picture` (`id`, `album_id`, `url`, `description`, `width`, `height`, `is_cover`, `sort`, `gmt_create`, `gmt_modified`) VALUES
(1, 1, 'http://localhost:9060/iphone13.PNG', NULL, 1024, 768, 0, 0, NULL, NULL),
(2, 1, 'http://localhost:9060/iphone13.PNG', NULL, 1024, 768, 0, 0, NULL, NULL),
(3, 1, 'http://localhost:9060/iphone13.PNG', NULL, 1024, 768, 0, 0, NULL, NULL),
(4, 1, 'http://localhost:9060/iphone13.PNG', NULL, 1024, 768, 0, 0, NULL, NULL),
(5, 1, 'http://localhost:9060/iphone13.PNG', NULL, 1024, 768, 0, 0, NULL, NULL),
(6, 2, '模拟数据：Mi 11 Ultra图片URL-1', NULL, 1024, 768, 0, 0, NULL, NULL),
(7, 2, '模拟数据：Mi 11 Ultra图片URL-2', NULL, 1024, 768, 0, 0, NULL, NULL),
(8, 2, '模拟数据：Mi 11 Ultra图片URL-3', NULL, 1024, 768, 0, 0, NULL, NULL),
(9, 2, '模拟数据：Mi 11 Ultra图片URL-4', NULL, 1024, 768, 0, 0, NULL, NULL),
(10, 2, '模拟数据：Mi 11 Ultra图片URL-5', NULL, 1024, 768, 0, 0, NULL, NULL);
/*!40000 ALTER TABLE `pms_picture` ENABLE KEYS */;

-- 导出  表 mall_pms.pms_sku 结构
DROP TABLE IF EXISTS `pms_sku`;
CREATE TABLE IF NOT EXISTS `pms_sku` (
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
    `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
    `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='SKU（Stock Keeping Unit）';

-- 正在导出表  mall_pms.pms_sku 的数据：~9 rows (大约)
/*!40000 ALTER TABLE `pms_sku` DISABLE KEYS */;
INSERT INTO `pms_sku` (`id`, `spu_id`, `title`, `bar_code`, `attribute_template_id`, `specifications`, `album_id`, `pictures`, `price`, `stock`, `stock_threshold`, `sales`, `comment_count`, `positive_comment_count`, `sort`, `gmt_create`, `gmt_modified`) VALUES
(1, 2, '2021年新款，小米11 Ultra黑色512G，16G超大内存120Hz高刷67w快充', NULL, 1, '{"attributes":[{"id":1,"name":"屏幕尺寸","value":"6.1寸"},{"id":3,"name":"颜色","value":"黑色"},{"id":5,"name":"运行内存","value":"16GB"}]}', NULL, 'http://localhost:9060/1.PNG', 6999.99, 887, 50, 0, 0, 0, 0, NULL, '2022-05-08 18:15:05'),
(2, 2, '2021年新款，小米11 Ultra白色512G，8G超大内存120Hz高刷67w快充', NULL, 1, '{"attributes":[{"id":1,"name":"屏幕尺寸","value":"6.1寸"},{"id":3,"name":"颜色","value":"白色"},{"id":5,"name":"运行内存","value":"8GB"}]}', NULL, 'http://localhost:9060/1.PNG', 6499.99, 745, 50, 0, 0, 0, 0, NULL, '2022-05-08 18:18:13'),
(3, 1, '苹果手机iPhone 13白色 8G 6.1寸', NULL, 1, '{"attributes":[{"id":1,"name":"屏幕尺寸","value":"6.1寸"},{"id":3,"name":"颜色","value":"白色"},{"id":5,"name":"运行内存","value":"8GB"}]}', NULL, 'http://localhost:9060/0.PNG', 6200.00, 667, 50, 0, 0, 0, 0, NULL, '2022-05-08 18:18:13'),
(4, 1, '苹果手机iPhone 13金色 16G 6.1寸', NULL, 1, '{"attributes":[{"id":1,"name":"屏幕尺寸","value":"6.1寸"},{"id":3,"name":"颜色","value":"金色"},{"id":5,"name":"运行内存","value":"16GB"}]}', NULL, 'http://localhost:9060/0.PNG', 7200.00, 416, 50, 0, 0, 0, 0, NULL, '2022-05-08 18:18:13'),
(5, 3, '华为P50pro黑色 16G 6.3寸', NULL, 1, '{"attributes":[{"id":1,"name":"屏幕尺寸","value":"6.3寸"},{"id":3,"name":"颜色","value":"黑色"},{"id":5,"name":"运行内存","value":"16GB"}]}', NULL, 'http://localhost:9060/2.PNG', 6300.00, 771, 50, 0, 0, 0, 0, NULL, '2022-05-08 18:18:13'),
(6, 3, '华为P50pro白色 16G 6.3寸', NULL, 1, '{"attributes":[{"id":1,"name":"屏幕尺寸","value":"6.3寸"},{"id":3,"name":"颜色","value":"白色"},{"id":5,"name":"运行内存","value":"16GB"}]}', NULL, 'http://localhost:9060/2.PNG', 6300.00, 613, 50, 0, 0, 0, 0, NULL, '2022-05-08 18:18:13'),
(7, 4, 'mac Book Air 14寸 M1 Mac OS', NULL, 2, '{"attributes":[{"id":2,"name":"屏幕尺寸","value":"14寸"},{"id":6,"name":"CPU型号","value":"m1"},{"id":10,"name":"操作系统","value":"Mac OS"}]}', NULL, 'http://localhost:9060/3.PNG', 7680.00, 679, 50, 0, 0, 0, 0, NULL, '2022-05-08 18:18:13'),
(8, 5, '华为MateBook 14寸 i5 Windows 10', NULL, 2, '{"attributes":[{"id":2,"name":"屏幕尺寸","value":"14寸"},{"id":6,"name":"CPU型号","value":"i5"},{"id":10,"name":"操作系统","value":"Windows 10"}]}', NULL, 'http://localhost:9060/4.PNG', 5300.00, 801, 50, 0, 0, 0, 0, NULL, '2022-05-08 18:18:13'),
(9, 5, '华为MateBook 14寸 i7 Windows 10', NULL, 2, '{"attributes":[{"id":2,"name":"屏幕尺寸","value":"14寸"},{"id":6,"name":"CPU型号","value":"i7"},{"id":10,"name":"操作系统","value":"Windows 10"}]}', NULL, 'http://localhost:9060/4.PNG', 5800.00, 777, 50, 0, 0, 0, 0, NULL, '2022-05-08 18:18:13');
/*!40000 ALTER TABLE `pms_sku` ENABLE KEYS */;

-- 导出  表 mall_pms.pms_sku_specification 结构
DROP TABLE IF EXISTS `pms_sku_specification`;
CREATE TABLE IF NOT EXISTS `pms_sku_specification` (
                                                       `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
                                                       `sku_id` bigint unsigned DEFAULT NULL COMMENT 'SKU id',
                                                       `attribute_id` bigint unsigned DEFAULT NULL COMMENT '属性id',
                                                       `attribute_name` varchar(50) DEFAULT NULL COMMENT '属性名称',
    `attribute_value` varchar(50) DEFAULT NULL COMMENT '属性值',
    `unit` varchar(10) DEFAULT NULL COMMENT '自动补充的计量单位',
    `sort` tinyint unsigned DEFAULT '0' COMMENT '自定义排序序号',
    `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
    `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4  COMMENT='SKU数据';

-- 正在导出表  mall_pms.pms_sku_specification 的数据：~27 rows (大约)
/*!40000 ALTER TABLE `pms_sku_specification` DISABLE KEYS */;
INSERT INTO `pms_sku_specification` (`id`, `sku_id`, `attribute_id`, `attribute_name`, `attribute_value`, `unit`, `sort`, `gmt_create`, `gmt_modified`) VALUES
(1, 1, 1, '屏幕尺寸', '6.1', '寸', 0, NULL, NULL),
(2, 1, 3, '颜色', '黑色', NULL, 0, NULL, NULL),
(3, 1, 5, '运行内存', '16', 'GB', 0, NULL, NULL),
(4, 2, 1, '屏幕尺寸', '6.1', '寸', 0, NULL, NULL),
(5, 2, 3, '颜色', '白色', NULL, 0, NULL, NULL),
(6, 2, 5, '运行内存', '8', 'GB', 0, NULL, NULL),
(7, 3, 1, '屏幕尺寸', '6.1', '寸', 0, NULL, NULL),
(8, 3, 3, '颜色', '白色', NULL, 0, NULL, NULL),
(9, 3, 5, '运行内存', '8', 'GB', 0, NULL, NULL),
(10, 4, 1, '屏幕尺寸', '6.1', '寸', 0, NULL, NULL),
(11, 4, 3, '颜色', '金色', NULL, 0, NULL, NULL),
(12, 4, 5, '运行内存', '16', 'GB', 0, NULL, NULL),
(13, 5, 1, '屏幕尺寸', '6.3', '寸', 0, NULL, NULL),
(14, 5, 3, '颜色', '黑色', NULL, 0, NULL, NULL),
(15, 5, 5, '运行内存', '16', 'GB', 0, NULL, NULL),
(16, 6, 1, '屏幕尺寸', '6.3', '寸', 0, NULL, NULL),
(17, 6, 3, '颜色', '白色', NULL, 0, NULL, NULL),
(18, 6, 5, '运行内存', '16', 'GB', 0, NULL, NULL),
(19, 7, 2, '屏幕尺寸', '14', '寸', 0, NULL, NULL),
(20, 7, 6, 'CPU型号', 'm1', NULL, 0, NULL, NULL),
(21, 7, 10, '操作系统', 'Mac OS', NULL, 0, NULL, NULL),
(22, 8, 2, '屏幕尺寸', '14', '寸', 0, NULL, NULL),
(23, 8, 6, 'CPU型号', 'i5', NULL, 0, NULL, NULL),
(24, 8, 10, '操作系统', 'Windows 10', NULL, 0, NULL, NULL),
(25, 9, 2, '屏幕尺寸', '14', '寸', 0, NULL, NULL),
(26, 9, 6, 'CPU型号', 'i7', NULL, 0, NULL, NULL),
(27, 9, 10, '操作系统', 'Windows 10', NULL, 0, NULL, NULL);
/*!40000 ALTER TABLE `pms_sku_specification` ENABLE KEYS */;

-- 导出  表 mall_pms.pms_spu 结构
DROP TABLE IF EXISTS `pms_spu`;
CREATE TABLE IF NOT EXISTS `pms_spu` (
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
    `gmt_check` timestamp NULL DEFAULT NULL COMMENT '审核通过时间（冗余）',
    `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
    `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='SPU（Standard Product Unit）';

-- 正在导出表  mall_pms.pms_spu 的数据：~5 rows (大约)
/*!40000 ALTER TABLE `pms_spu` DISABLE KEYS */;
INSERT INTO `pms_spu` (`id`, `name`, `type_number`, `title`, `description`, `list_price`, `stock`, `stock_threshold`, `unit`, `brand_id`, `brand_name`, `category_id`, `category_name`, `attribute_template_id`, `album_id`, `pictures`, `keywords`, `tags`, `sales`, `comment_count`, `positive_comment_count`, `sort`, `is_deleted`, `is_published`, `is_new_arrival`, `is_recommend`, `is_checked`, `check_user`, `gmt_check`, `gmt_create`, `gmt_modified`) VALUES
(1, 'iPhone 13', 'A2404', '苹果手机iPhone 13（A2404）', '2021年新款，全网首发', 5690.00, 5000, 20, '部', 3, '苹果', 3, '智能手机', NULL, NULL, 'http://localhost:9060/0.PNG,http://localhost:9060/1.PNG', 'ip13,iPhone13,苹果13', '20w快充,NFC,无线充电', 0, 0, 0, 0, 0, 1, 0, 0, 1, NULL, NULL, '2022-03-31 11:16:33', '2022-03-31 15:22:33'),
(2, '小米11 Ultra', 'M112021', '小米11 Ultra（M112021）', '2021年最新旗舰机', 5299.00, 8000, 20, '部', 2, '小米', 3, '智能手机', NULL, NULL, 'http://localhost:9060/1.PNG', 'mi11,xiaomi11,ultra', '67w快充,1亿像素,5000毫安电池', 0, 0, 0, 0, 0, 1, 0, 0, 1, NULL, NULL, '2022-03-31 11:16:37', '2022-03-31 15:22:36'),
(3, '华为P50pro', 'P50pro', '华为P50pro', '2021年最新旗舰机', 5588.00, 8000, 20, '部', 1, '华为', 3, '智能手机', NULL, NULL, 'http://localhost:9060/2.PNG', 'p50,p50pro', '...', 0, 0, 0, 0, 0, 1, 0, 0, 1, NULL, NULL, '2022-03-31 11:16:37', '2022-03-31 15:22:36'),
(4, 'mac Book Air', 'mba', '苹果笔记本电脑', '轻薄笔记本', 7680.00, 8000, 20, '台', 3, '苹果', 8, '笔记本', NULL, NULL, 'http://localhost:9060/3.PNG', 'macbook,air', '...', 0, 0, 0, 0, 0, 1, 0, 0, 1, NULL, NULL, '2022-03-31 11:16:37', '2022-03-31 15:22:36'),
(5, '华为 MateBook', 'MateBook', '华为笔记本电脑', '强大功能的笔记本', 4680.00, 8000, 20, '台', 1, '华为', 8, '笔记本', NULL, NULL, 'http://localhost:9060/4.PNG', 'huaweibook', '...', 0, 0, 0, 0, 0, 1, 0, 0, 1, NULL, NULL, '2022-03-31 11:16:37', '2022-03-31 15:22:36');
/*!40000 ALTER TABLE `pms_spu` ENABLE KEYS */;

-- 导出  表 mall_pms.pms_spu_detail 结构
DROP TABLE IF EXISTS `pms_spu_detail`;
CREATE TABLE IF NOT EXISTS `pms_spu_detail` (
                                                `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '记录id',
                                                `spu_id` bigint unsigned DEFAULT NULL COMMENT 'SPU id',
                                                `detail` text COMMENT 'SPU详情，应该使用HTML富文本，通常内容是若干张图片',
                                                `gmt_create` timestamp NULL DEFAULT NULL COMMENT '数据创建时间',
                                                `gmt_modified` timestamp NULL DEFAULT NULL COMMENT '数据最后修改时间',
                                                PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4  COMMENT='SPU详情';

-- 正在导出表  mall_pms.pms_spu_detail 的数据：~5 rows (大约)
/*!40000 ALTER TABLE `pms_spu_detail` DISABLE KEYS */;
INSERT INTO `pms_spu_detail` (`id`, `spu_id`, `detail`, `gmt_create`, `gmt_modified`) VALUES
(1, 1, '<div>iPhone 13的详情HTML</div>', NULL, NULL),
(2, 2, '<div>小米11 Ultra的详情HTML</div>', NULL, NULL),
(3, 3, '<div>华为P50pro的详情HTML</div>', NULL, NULL),
(4, 4, '<div>苹果Mac Book Air的详情HTML</div>', NULL, NULL),
(5, 5, '<div>华为 MateBook的详情HTML</div>', NULL, NULL);
/*!40000 ALTER TABLE `pms_spu_detail` ENABLE KEYS */;

-- 导出  表 mall_pms.undo_log 结构
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE IF NOT EXISTS `undo_log` (
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
    ) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb3;

-- 正在导出表  mall_pms.undo_log 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `undo_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `undo_log` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
