/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : houseRental

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 14/04/2025 17:28:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for booking
-- ----------------------------
DROP TABLE IF EXISTS `booking`;
CREATE TABLE `booking` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `booking_time` datetime NOT NULL COMMENT '预约看房时间',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `remark` text COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-待确认，1-已确认，2-已取消，3-已完成',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  KEY `idx_house` (`house_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预约表';

-- ----------------------------
-- Records of booking
-- ----------------------------
BEGIN;
INSERT INTO `booking` (`id`, `house_id`, `user_id`, `booking_time`, `contact_name`, `contact_phone`, `remark`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (1, 1, 2, '2024-01-05 10:00:00', '张三', '13800138001', '希望能在周末看房', 2, '2025-01-03 14:24:42', '2025-01-03 14:24:42', 0);
INSERT INTO `booking` (`id`, `house_id`, `user_id`, `booking_time`, `contact_name`, `contact_phone`, `remark`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (2, 2, 2, '2024-01-06 14:30:00', '张三', '13800138001', '最好能提前联系', 2, '2025-01-03 14:24:42', '2025-01-03 14:24:42', 0);
INSERT INTO `booking` (`id`, `house_id`, `user_id`, `booking_time`, `contact_name`, `contact_phone`, `remark`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (3, 1, 3, '2024-01-07 15:00:00', '李四', '13800138002', '想详细了解房屋情况', 2, '2025-01-03 14:24:42', '2025-01-03 14:24:42', 0);
INSERT INTO `booking` (`id`, `house_id`, `user_id`, `booking_time`, `contact_name`, `contact_phone`, `remark`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (4, 3, 2, '2024-01-08 11:00:00', '张三', '13800138001', '关注采光问题', 3, '2025-01-03 14:24:42', '2025-01-03 14:24:42', 0);
INSERT INTO `booking` (`id`, `house_id`, `user_id`, `booking_time`, `contact_name`, `contact_phone`, `remark`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (5, 3, 3, '2025-01-22 16:00:00', 'user', '18787878787', '123123123', 4, '2025-01-23 13:55:16', '2025-01-23 13:55:16', 0);
INSERT INTO `booking` (`id`, `house_id`, `user_id`, `booking_time`, `contact_name`, `contact_phone`, `remark`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (6, 2, 3, '2025-01-23 16:00:00', 'user', '18877630940', '999999999', 4, '2025-01-23 16:33:49', '2025-01-23 16:33:49', 0);
INSERT INTO `booking` (`id`, `house_id`, `user_id`, `booking_time`, `contact_name`, `contact_phone`, `remark`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (7, 5, 3, '2025-01-24 03:30:00', 'iiiii', '18908990988', 'a\'w\'da\'w\'da\'d', 2, '2025-01-23 16:49:57', '2025-01-23 16:49:57', 0);
INSERT INTO `booking` (`id`, `house_id`, `user_id`, `booking_time`, `contact_name`, `contact_phone`, `remark`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (8, 1, 1, '2025-02-19 09:04:00', '13123', '18889900987', '123123123', 4, '2025-02-18 15:07:09', '2025-02-18 15:07:09', 0);
INSERT INTO `booking` (`id`, `house_id`, `user_id`, `booking_time`, `contact_name`, `contact_phone`, `remark`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (9, 4, 3, '2025-02-18 07:09:08', '123123', '18767677898', '123123', 3, '2025-02-18 15:08:33', '2025-04-02 09:27:19', 0);
COMMIT;

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_house` (`user_id`,`house_id`,`deleted`),
  KEY `idx_user` (`user_id`),
  KEY `idx_house` (`house_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收藏表';

-- ----------------------------
-- Records of favorite
-- ----------------------------
BEGIN;
INSERT INTO `favorite` (`id`, `user_id`, `house_id`, `created_time`, `updated_time`, `deleted`) VALUES (18, 3, 2, '2025-01-03 16:12:50', '2025-01-06 15:05:33', 1);
INSERT INTO `favorite` (`id`, `user_id`, `house_id`, `created_time`, `updated_time`, `deleted`) VALUES (19, 3, 3, '2025-01-03 16:37:54', '2025-01-06 16:18:50', 0);
INSERT INTO `favorite` (`id`, `user_id`, `house_id`, `created_time`, `updated_time`, `deleted`) VALUES (20, 3, 1, '2025-01-03 16:38:02', '2025-01-06 15:05:46', 1);
INSERT INTO `favorite` (`id`, `user_id`, `house_id`, `created_time`, `updated_time`, `deleted`) VALUES (24, 3, 2, '2025-01-06 17:12:06', '2025-01-06 17:12:06', 0);
INSERT INTO `favorite` (`id`, `user_id`, `house_id`, `created_time`, `updated_time`, `deleted`) VALUES (25, 1, 4, '2025-01-23 11:13:43', '2025-01-23 11:13:43', 0);
INSERT INTO `favorite` (`id`, `user_id`, `house_id`, `created_time`, `updated_time`, `deleted`) VALUES (26, 1, 1, '2025-02-18 15:06:41', '2025-02-18 15:06:41', 0);
COMMIT;

-- ----------------------------
-- Table structure for house
-- ----------------------------
DROP TABLE IF EXISTS `house`;
CREATE TABLE `house` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `description` text COMMENT '描述',
  `area` decimal(10,2) NOT NULL COMMENT '面积（平方米）',
  `price` decimal(10,2) NOT NULL COMMENT '租金（元/月）',
  `deposit` decimal(10,2) DEFAULT NULL COMMENT '押金（元）',
  `room_count` tinyint NOT NULL COMMENT '房间数',
  `hall_count` tinyint NOT NULL COMMENT '厅室数',
  `bathroom_count` tinyint NOT NULL COMMENT '卫生间数',
  `floor` int NOT NULL COMMENT '所在楼层',
  `total_floor` int NOT NULL COMMENT '总楼层',
  `has_elevator` tinyint NOT NULL DEFAULT '0' COMMENT '是否有电梯：0-否，1-是',
  `orientation` varchar(20) NOT NULL COMMENT '朝向',
  `decoration` varchar(20) NOT NULL COMMENT '装修情况',
  `facilities` varchar(255) DEFAULT NULL COMMENT '配套设施，JSON格式',
  `images` text COMMENT '图片URL，JSON格式',
  `province` varchar(50) NOT NULL COMMENT '省份',
  `city` varchar(50) NOT NULL COMMENT '城市',
  `district` varchar(50) NOT NULL COMMENT '区县',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `owner_id` bigint NOT NULL COMMENT '房东ID',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-上架，2-已出租',
  `lease_status` tinyint NOT NULL DEFAULT '0' COMMENT '租赁状态：0-未租出，1-已租出',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_owner` (`owner_id`),
  KEY `idx_status` (`status`),
  KEY `idx_location` (`province`,`city`,`district`),
  KEY `idx_house_location` (`latitude`,`longitude`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房源表';

-- ----------------------------
-- Records of house
-- ----------------------------
BEGIN;
INSERT INTO `house` (`id`, `title`, `description`, `area`, `price`, `deposit`, `room_count`, `hall_count`, `bathroom_count`, `floor`, `total_floor`, `has_elevator`, `orientation`, `decoration`, `facilities`, `images`, `province`, `city`, `district`, `address`, `latitude`, `longitude`, `owner_id`, `contact_name`, `contact_phone`, `status`, `lease_status`, `created_time`, `updated_time`, `deleted`, `update_time`) VALUES (1, '整租·南山区精装两房·近地铁', '房源位于南山区，临近地铁1号线，周边配套设施齐全，交通便利。房间采光好，布局合理，配套设施齐全。', 75.50, 4500.00, 4500.00, 2, 1, 1, 12, 30, 1, '朝南', '精装', '[\"wifi\",\"tv\",\"ac\",\"washer\",\"fridge\"]', '/images/bec0109b-c8fe-4e88-9b12-a51d6027f995.jpeg', '广东省', '深圳市', '南山区', '南山大道123号', 22.535023, 113.932466, 1, '张先生', '13800138001', 1, 0, '2025-01-11 16:04:23', '2025-02-25 11:08:26', 0, '2025-02-25 11:08:26');
INSERT INTO `house` (`id`, `title`, `description`, `area`, `price`, `deposit`, `room_count`, `hall_count`, `bathroom_count`, `floor`, `total_floor`, `has_elevator`, `orientation`, `decoration`, `facilities`, `images`, `province`, `city`, `district`, `address`, `latitude`, `longitude`, `owner_id`, `contact_name`, `contact_phone`, `status`, `lease_status`, `created_time`, `updated_time`, `deleted`, `update_time`) VALUES (2, '整租·福田区豪华三房·江景房', '房源位于福田区，临江而建，视野开阔，可观江景。精装修，家具家电齐全，拎包入住。', 120.00, 7000.00, 7000.00, 3, 2, 2, 25, 35, 1, '东南', '豪装', '[\"wifi\",\"tv\",\"ac\",\"washer\",\"fridge\",\"microwave\",\"desk\",\"wardrobe\"]', '/images/97947d0c-0089-4e69-bd09-1b2b560fa211.jpeg,/images/724316c4-d119-4665-8a98-aea82966fc81.jpeg', '广东省', '深圳市', '福田区', '福田路456号', 22.543097, 114.057868, 1, '李先生', '13800138002', 1, 0, '2025-01-10 16:09:47', '2025-02-25 11:08:26', 0, '2025-02-25 11:08:26');
INSERT INTO `house` (`id`, `title`, `description`, `area`, `price`, `deposit`, `room_count`, `hall_count`, `bathroom_count`, `floor`, `total_floor`, `has_elevator`, `orientation`, `decoration`, `facilities`, `images`, `province`, `city`, `district`, `address`, `latitude`, `longitude`, `owner_id`, `contact_name`, `contact_phone`, `status`, `lease_status`, `created_time`, `updated_time`, `deleted`, `update_time`) VALUES (3, '整租·罗湖区温馨一房·近地铁', '房源位于罗湖区，临近地铁2号线，周边商圈成熟，生活便利。房间温馨舒适，适合单身人士居住。', 45.00, 3000.00, 3000.00, 1, 1, 1, 8, 20, 1, '朝南', '简装', '[\"wifi\",\"tv\",\"ac\",\"washer\"]', '/images/c0d17144-bc4f-43de-a21c-46f994f4ba48.jpeg,/images/da7d4ac0-6400-4dd2-8855-7d6560b594e1.jpeg,/images/570c296e-6d01-424e-b3a8-0db303ff2c24.jpeg', '广东省', '深圳市', '罗湖区', '罗湖路789号', 22.555341, 114.137674, 1, '王先生', '13800138003', 1, 0, '2025-01-11 16:09:47', '2025-02-25 11:08:26', 0, '2025-02-25 11:08:26');
INSERT INTO `house` (`id`, `title`, `description`, `area`, `price`, `deposit`, `room_count`, `hall_count`, `bathroom_count`, `floor`, `total_floor`, `has_elevator`, `orientation`, `decoration`, `facilities`, `images`, `province`, `city`, `district`, `address`, `latitude`, `longitude`, `owner_id`, `contact_name`, `contact_phone`, `status`, `lease_status`, `created_time`, `updated_time`, `deleted`, `update_time`) VALUES (4, '测试房源111111', 'qweqe', 30.00, 700.00, 900.00, 1, 2, 1, 3, 6, 0, '朝南', '简装', '[\"washer\",\"ac\",\"desk\",\"wardrobe\",\"wifi\"]', '/images/6a23c7b3-2f5f-4bf8-8f0f-ba0dd3bdccaa.jpeg', '江苏省', '南京市', '溧水区', '南京工业大学浦江学院', 31.653061, 119.028319, 1, '123123123', '19988774679', 1, 0, '2025-01-23 11:06:40', '2025-02-25 11:08:26', 0, '2025-02-25 11:08:26');
INSERT INTO `house` (`id`, `title`, `description`, `area`, `price`, `deposit`, `room_count`, `hall_count`, `bathroom_count`, `floor`, `total_floor`, `has_elevator`, `orientation`, `decoration`, `facilities`, `images`, `province`, `city`, `district`, `address`, `latitude`, `longitude`, `owner_id`, `contact_name`, `contact_phone`, `status`, `lease_status`, `created_time`, `updated_time`, `deleted`, `update_time`) VALUES (5, 'ceshi2222', 'tttttttt', 40.00, 1300.00, 1200.00, 1, 1, 1, 10, 19, 0, '朝东', '简装', '[\"desk\",\"wardrobe\",\"wifi\",\"tv\",\"ac\",\"washer\",\"fridge\",\"microwave\"]', '/images/cc314c17-8bec-4cfb-9654-59a657f818cf.jpeg', '北京市', '市辖区', '东城区', '1231231312311', 39.928353, 116.416357, 1, 'adong', '18598887478', 1, 0, '2025-01-23 11:17:11', '2025-02-25 11:08:26', 0, '2025-02-25 11:08:26');
INSERT INTO `house` (`id`, `title`, `description`, `area`, `price`, `deposit`, `room_count`, `hall_count`, `bathroom_count`, `floor`, `total_floor`, `has_elevator`, `orientation`, `decoration`, `facilities`, `images`, `province`, `city`, `district`, `address`, `latitude`, `longitude`, `owner_id`, `contact_name`, `contact_phone`, `status`, `lease_status`, `created_time`, `updated_time`, `deleted`, `update_time`) VALUES (6, '231231', '123123', 1.00, 1.00, 1.00, 1, 1, 2, 1, 1, 1, '西南', '毛坯', 'wifi,washer,aircon,heater', '/images/a42d9c58-8ac1-4f67-8bfe-cf604450319a.jpeg', '天津市', '市辖区', '和平区', '12313', 39.117196, 117.195907, 1, '123123', '13897898765', 1, 0, '2025-02-18 15:05:50', '2025-02-25 11:08:26', 0, '2025-02-25 11:08:26');
INSERT INTO `house` (`id`, `title`, `description`, `area`, `price`, `deposit`, `room_count`, `hall_count`, `bathroom_count`, `floor`, `total_floor`, `has_elevator`, `orientation`, `decoration`, `facilities`, `images`, `province`, `city`, `district`, `address`, `latitude`, `longitude`, `owner_id`, `contact_name`, `contact_phone`, `status`, `lease_status`, `created_time`, `updated_time`, `deleted`, `update_time`) VALUES (7, '123123', '1231313', 1.00, 1.00, 1.00, 1, 1, 1, 1, 1, 0, '东南', '简装', 'washer,wifi,tv', '/images/ea44b7bf-7e15-441a-82c7-3953926a64d4.jpeg', '江苏省', '南京市', '雨花台区', '凤翔新城2期', 31.975325, 118.766827, 1, '123123', '18866573094', 1, 0, '2025-02-25 11:46:23', '2025-02-25 11:47:52', 0, NULL);
INSERT INTO `house` (`id`, `title`, `description`, `area`, `price`, `deposit`, `room_count`, `hall_count`, `bathroom_count`, `floor`, `total_floor`, `has_elevator`, `orientation`, `decoration`, `facilities`, `images`, `province`, `city`, `district`, `address`, `latitude`, `longitude`, `owner_id`, `contact_name`, `contact_phone`, `status`, `lease_status`, `created_time`, `updated_time`, `deleted`, `update_time`) VALUES (8, '12123123', '12312312313113', 1.00, 100.00, 100.00, 1, 1, 1, 1, 1, 1, '西南', '简装', '[\"tv\",\"ac\",\"washer\",\"fridge\"]', '/images/0eace26c-38d9-4305-a0fc-3f447b6dbe1d.jpeg', '江苏省', '南京市', '雨花台区', '凤翔新城1期', 31.976070, 118.764241, 1, '11323131311', '19988776655', 2, 1, '2025-02-27 16:22:24', '2025-04-14 14:20:47', 0, NULL);
COMMIT;

-- ----------------------------
-- Table structure for lease_contract
-- ----------------------------
DROP TABLE IF EXISTS `lease_contract`;
CREATE TABLE `lease_contract` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `house_id` bigint NOT NULL COMMENT '房源ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `owner_id` bigint NOT NULL COMMENT '房东ID',
  `contract_number` varchar(50) NOT NULL COMMENT '合同编号',
  `start_date` date NOT NULL COMMENT '租期开始日期',
  `end_date` date NOT NULL COMMENT '租期结束日期',
  `monthly_rent` decimal(10,2) NOT NULL COMMENT '月租金',
  `deposit` decimal(10,2) NOT NULL COMMENT '押金',
  `payment_method` varchar(20) NOT NULL COMMENT '支付方式：月付/季付/半年付/年付',
  `contract_file` varchar(255) DEFAULT NULL COMMENT '合同文件路径',
  `sign_date` datetime DEFAULT NULL COMMENT '签约日期',
  `tenant_sign` tinyint NOT NULL DEFAULT '0' COMMENT '租户是否签署：0-未签署，1-已签署',
  `owner_sign` tinyint NOT NULL DEFAULT '0' COMMENT '房东是否签署：0-未签署，1-已签署',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-草稿，1-待租户签署，2-待房东签署，3-已生效，4-已终止，5-已到期',
  `remark` text COMMENT '备注',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contract_number` (`contract_number`),
  KEY `idx_house` (`house_id`),
  KEY `idx_tenant` (`tenant_id`),
  KEY `idx_owner` (`owner_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='租赁合同表';

-- ----------------------------
-- Records of lease_contract
-- ----------------------------
BEGIN;
INSERT INTO `lease_contract` (`id`, `house_id`, `tenant_id`, `owner_id`, `contract_number`, `start_date`, `end_date`, `monthly_rent`, `deposit`, `payment_method`, `contract_file`, `sign_date`, `tenant_sign`, `owner_sign`, `status`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (1, 8, 4, 1, 'LC-20250414-7259', '2025-04-14', '2026-04-14', 100.00, 100.00, '月付', NULL, '2025-04-14 14:20:47', 1, 1, 3, '', '2025-04-14 11:48:25', '2025-04-14 11:48:25', 0);
COMMIT;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `type` tinyint NOT NULL COMMENT '类型：1-系统消息，2-预约消息',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '消息状态：0-未读，1-已读',
  `read_status` tinyint NOT NULL DEFAULT '0' COMMENT '阅读状态：0-未读，1-已读',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_read` (`read_status`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息表';

-- ----------------------------
-- Records of message
-- ----------------------------
BEGIN;
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (1, 2, '预约确认通知', '您预约查看的房源已确认，请准时到达。', 2, 0, 0, '2025-01-03 14:25:53', '2025-01-03 14:25:53', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (2, 2, '系统通知', '欢迎使用房屋租赁平台！', 1, 0, 1, '2025-01-03 14:25:53', '2025-01-03 14:25:53', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (3, 2, '预约取消通知', '很抱歉，您预约的房源因特殊原因已被取消。', 2, 0, 0, '2025-01-03 14:25:53', '2025-01-03 14:25:53', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (4, 3, '系统通知', '欢迎使用房屋租赁平台！', 1, 1, 0, '2025-01-03 14:25:53', '2025-01-06 16:31:08', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (5, 3, '预约提醒', '您有一个预约即将开始，请准时到达。', 2, 1, 0, '2025-01-03 14:25:53', '2025-01-06 16:31:16', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (6, 3, '个人信息更新通知', '您的个人信息已更新：邮箱已更新；', 1, 1, 0, '2025-01-06 16:51:18', '2025-01-06 16:51:29', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (7, 3, '个人信息更新通知', '您的个人信息已更新：昵称已更新；', 1, 1, 0, '2025-01-06 17:11:54', '2025-01-06 17:11:58', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (8, 3, '收藏提醒', '您已成功收藏房源：整租·福田区豪华三房·江景房', 2, 1, 0, '2025-01-06 17:12:06', '2025-01-23 13:49:25', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (9, 3, '房源状态变更提醒', '您收藏的房源：整租·福田区豪华三房·江景房 房源已下线', 3, 1, 0, '2025-01-16 09:41:38', '2025-01-23 10:52:31', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (10, 3, '房源状态变更提醒', '您收藏的房源：整租·福田区豪华三房·江景房 房源已上线', 3, 1, 0, '2025-01-16 09:41:39', '2025-01-23 10:52:31', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (11, 3, '房源状态变更提醒', '您收藏的房源：整租·罗湖区温馨一房·近地铁 房源已下线', 3, 1, 0, '2025-01-16 09:41:52', '2025-01-23 10:52:31', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (12, 3, '房源状态变更提醒', '您收藏的房源：整租·罗湖区温馨一房·近地铁 房源已上线', 3, 1, 0, '2025-01-16 09:41:52', '2025-01-23 10:52:31', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (13, 2, '账号删除通知', '您的账号已被管理员删除。', 1, 0, 0, '2025-01-16 11:08:04', '2025-01-16 11:08:04', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (14, 2, '账号状态变更通知', '您的账号已禁用', 1, 0, 0, '2025-01-16 14:01:58', '2025-01-16 14:01:58', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (15, 2, '账号状态变更通知', '您的账号已启用', 1, 0, 0, '2025-01-16 14:02:00', '2025-01-16 14:02:00', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (16, 3, '账号状态变更通知', '您的账号已禁用', 1, 1, 0, '2025-01-16 14:02:13', '2025-01-23 10:52:28', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (17, 3, '账号状态变更通知', '您的账号已启用', 1, 1, 0, '2025-01-16 14:02:48', '2025-01-23 13:49:38', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (18, 3, '账号状态变更通知', '您的账号已禁用', 1, 1, 0, '2025-01-16 14:16:53', '2025-01-23 13:49:36', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (19, 3, '账号状态变更通知', '您的账号已启用', 1, 1, 0, '2025-01-16 14:16:54', '2025-01-23 10:52:31', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (20, 4, '欢迎加入', '欢迎使用房屋租赁平台！', 1, 1, 0, '2025-01-16 14:24:53', '2025-04-14 13:36:25', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (21, 4, '账号信息变更通知', '您的账号信息已被管理员更新，请注意查看。', 1, 1, 0, '2025-01-16 14:31:04', '2025-04-14 13:36:25', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (22, 4, '账号删除通知', '您的账号已被管理员删除。', 1, 1, 0, '2025-01-16 14:31:12', '2025-04-14 13:36:25', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (23, 4, '账号删除通知', '您的账号已被管理员删除。', 1, 1, 0, '2025-01-23 10:26:50', '2025-04-14 13:36:25', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (24, 4, '账号状态变更通知', '您的账号已禁用', 1, 1, 0, '2025-01-23 10:30:17', '2025-04-14 13:36:25', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (25, 4, '账号状态变更通知', '您的账号已启用', 1, 1, 0, '2025-01-23 10:30:20', '2025-04-14 13:36:25', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (26, 4, '账号删除通知', '您的账号已被管理员删除。', 1, 1, 0, '2025-01-23 10:30:22', '2025-04-14 13:36:25', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (27, 4, '账号状态变更通知', '您的账号已禁用', 1, 1, 0, '2025-01-23 10:30:25', '2025-04-14 13:36:25', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (28, 4, '账号状态变更通知', '您的账号已启用', 1, 1, 0, '2025-01-23 10:30:26', '2025-04-14 13:36:25', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (29, 3, '账号状态变更通知', '您的账号已禁用', 1, 1, 0, '2025-01-23 10:30:28', '2025-01-23 10:52:31', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (30, 4, '账号删除通知', '您的账号已被管理员删除。', 1, 1, 0, '2025-01-23 10:38:25', '2025-04-14 13:36:25', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (31, 3, '账号状态变更通知', '您的账号已启用', 1, 1, 0, '2025-01-23 10:38:32', '2025-01-23 10:52:31', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (32, 1, '账号状态变更通知', '您的账号已禁用', 1, 1, 0, '2025-01-23 10:38:38', '2025-01-23 11:17:28', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (33, 1, '账号状态变更通知', '您的账号已启用', 1, 1, 0, '2025-01-23 10:38:39', '2025-01-23 11:24:16', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (34, 3, '账号状态变更通知', '您的账号已禁用', 1, 1, 0, '2025-01-23 10:40:21', '2025-01-23 10:52:31', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (35, 3, '账号状态变更通知', '您的账号已启用', 1, 1, 0, '2025-01-23 10:47:52', '2025-01-23 10:52:31', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (36, 2, '账号状态变更通知', '您的账号已禁用', 1, 0, 0, '2025-01-23 11:00:08', '2025-01-23 11:00:08', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (37, 3, '账号状态变更通知', '您的账号已禁用', 1, 1, 0, '2025-01-23 11:00:08', '2025-01-23 16:27:54', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (38, 2, '账号状态变更通知', '您的账号已启用', 1, 0, 0, '2025-01-23 11:00:14', '2025-01-23 11:00:14', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (39, 3, '账号状态变更通知', '您的账号已启用', 1, 1, 0, '2025-01-23 11:00:14', '2025-01-23 16:27:54', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (40, 10, '欢迎加入', '欢迎使用房屋租赁平台！', 1, 0, 0, '2025-01-23 11:00:54', '2025-01-23 11:00:54', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (41, 11, '欢迎加入', '欢迎使用房屋租赁平台！', 1, 0, 0, '2025-01-23 11:01:27', '2025-01-23 11:01:27', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (42, 11, '账号删除通知', '您的账号已被管理员删除。', 1, 0, 0, '2025-01-23 11:02:32', '2025-01-23 11:02:32', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (43, 10, '账号删除通知', '您的账号已被管理员删除。', 1, 0, 0, '2025-01-23 11:02:32', '2025-01-23 11:02:32', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (44, 2, '账号信息变更通知', '您的账号信息已被管理员更新，请注意查看。', 1, 0, 0, '2025-01-23 11:03:04', '2025-01-23 11:03:04', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (45, 3, '房源状态变更提醒', '您收藏的房源：整租·罗湖区温馨一房·近地铁 房源已下线', 3, 1, 0, '2025-01-23 11:08:53', '2025-01-23 16:27:54', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (46, 3, '房源状态变更提醒', '您收藏的房源：整租·罗湖区温馨一房·近地铁 房源已上线', 3, 1, 0, '2025-01-23 11:08:56', '2025-01-23 16:27:54', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (47, 1, '收藏提醒', '您已成功收藏房源：测试房源111111', 3, 1, 0, '2025-01-23 11:13:43', '2025-01-23 11:17:28', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (48, 12, '欢迎加入', '欢迎使用房屋租赁平台！', 1, 0, 0, '2025-01-23 11:15:09', '2025-01-23 11:15:09', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (49, 1, '新的预约请求', '您的房源\"整租·罗湖区温馨一房·近地铁\"收到了新的预约请求，请及时处理。', 1, 1, 0, '2025-01-23 13:55:16', '2025-01-23 16:53:17', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (50, 1, '预约已取消', '用户已取消房源\"整租·福田区豪华三房·江景房\"的预约。', 1, 1, 0, '2025-01-23 14:04:20', '2025-01-23 16:53:15', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (51, 1, '预约已取消', '用户已取消房源\"整租·南山区精装两房·近地铁\"的预约。', 1, 1, 0, '2025-01-23 14:04:21', '2025-01-23 16:53:13', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (52, 3, '预约已被拒绝', '房东已拒绝您对房源\"整租·罗湖区温馨一房·近地铁\"的预约。', 1, 1, 0, '2025-01-23 16:25:55', '2025-01-23 16:45:18', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (53, 1, '新的预约请求', '您的房源\"整租·福田区豪华三房·江景房\"收到了新的预约请求，请及时处理。', 1, 1, 0, '2025-01-23 16:33:49', '2025-01-23 16:53:11', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (54, 3, '预约已被拒绝', '房东已拒绝您对房源\"整租·福田区豪华三房·江景房\"的预约。', 1, 1, 0, '2025-01-23 16:34:25', '2025-01-23 16:45:17', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (55, 1, '新的预约请求', '您的房源\"ceshi2222\"收到了新的预约请求，请及时处理。', 1, 1, 0, '2025-01-23 16:49:57', '2025-01-23 16:53:08', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (56, 1, '预约已取消', '用户已取消房源\"ceshi2222\"的预约。', 2, 1, 0, '2025-01-23 16:52:56', '2025-01-23 16:53:20', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (57, 1, '收藏提醒', '您已成功收藏房源：整租·南山区精装两房·近地铁', 3, 1, 0, '2025-02-18 15:06:41', '2025-02-18 15:07:25', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (58, 1, '新的预约请求', '您的房源\"整租·南山区精装两房·近地铁\"收到了新的预约请求，请及时处理。', 2, 1, 0, '2025-02-18 15:07:09', '2025-02-18 15:07:24', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (59, 1, '新的预约请求', '您的房源\"测试房源111111\"收到了新的预约请求，请及时处理。', 2, 1, 0, '2025-02-18 15:08:33', '2025-02-25 09:45:51', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (60, 1, '预约已被拒绝', '房东已拒绝您对房源\"整租·南山区精装两房·近地铁\"的预约。', 2, 1, 0, '2025-02-18 15:09:07', '2025-02-25 09:45:49', 1);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (61, 3, '预约状态更新', '您预约的房源\"测试房源111111\"状态已更新为：已确认', 2, 0, 0, '2025-02-18 15:09:11', '2025-02-18 15:09:11', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (62, 3, '预约状态更新', '您预约的房源\"测试房源111111\"状态已更新为：已完成', 2, 0, 0, '2025-04-02 09:27:19', '2025-04-02 09:27:19', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (63, 4, '租赁合同待签署', '您有一份租赁合同等待签署，请尽快处理。', 4, 1, 0, '2025-04-14 11:48:26', '2025-04-14 13:51:00', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (64, 1, '租赁合同创建通知', '您的房源已创建租赁合同，等待租户签署。', 4, 1, 0, '2025-04-14 11:48:26', '2025-04-14 14:59:01', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (65, 1, '租赁合同待签署', '租户已签署合同，请您及时确认并签署。', 4, 1, 0, '2025-04-14 14:00:14', '2025-04-14 14:58:59', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (66, 4, '租赁合同已生效', '您的租赁合同已正式生效，租期从 2025-04-14 到 2026-04-14', 4, 1, 0, '2025-04-14 14:20:47', '2025-04-14 15:02:20', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (67, 4, '租金账单已生成', '您的租赁合同已生成新的租金账单，请查看并及时支付。', 4, 1, 0, '2025-04-14 14:30:37', '2025-04-14 15:02:20', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (68, 4, '租金支付成功', '您的租金支付已成功，租期：2026-04-14 至 2026-04-14，金额：3.33元。', 4, 1, 0, '2025-04-14 14:31:52', '2025-04-14 15:02:20', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (69, 1, '租金收款通知', '您已收到一笔租金，租期：2026-04-14 至 2026-04-14，金额：3.33元。', 4, 1, 0, '2025-04-14 14:31:52', '2025-04-14 14:58:56', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (70, 4, '租金支付成功', '您的租金支付已成功，租期：2026-03-14 至 2026-04-13，金额：103.33元。', 4, 1, 0, '2025-04-14 14:54:37', '2025-04-14 15:02:10', 0);
INSERT INTO `message` (`id`, `user_id`, `title`, `content`, `type`, `status`, `read_status`, `created_time`, `updated_time`, `deleted`) VALUES (71, 1, '租金收款通知', '您已收到一笔租金，租期：2026-03-14 至 2026-04-13，金额：103.33元。', 4, 1, 0, '2025-04-14 14:54:37', '2025-04-14 15:04:38', 0);
COMMIT;

-- ----------------------------
-- Table structure for rent_payment
-- ----------------------------
DROP TABLE IF EXISTS `rent_payment`;
CREATE TABLE `rent_payment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `contract_id` bigint NOT NULL COMMENT '合同ID',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_date` date NOT NULL COMMENT '支付日期',
  `payment_method` varchar(20) NOT NULL COMMENT '支付方式：支付宝/微信/银行转账/现金',
  `payment_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态：0-未支付，1-已支付，2-已逾期',
  `transaction_no` varchar(100) DEFAULT NULL COMMENT '交易流水号',
  `period_start` date NOT NULL COMMENT '租期开始日期',
  `period_end` date NOT NULL COMMENT '租期结束日期',
  `remark` text COMMENT '备注',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  KEY `idx_contract` (`contract_id`),
  KEY `idx_status` (`payment_status`),
  KEY `idx_period` (`period_start`,`period_end`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='租金支付记录表';

-- ----------------------------
-- Records of rent_payment
-- ----------------------------
BEGIN;
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (1, 1, 100.00, '1970-01-01', '未支付', 0, NULL, '2025-04-14', '2025-05-13', NULL, '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (2, 1, 103.33, '1970-01-01', '未支付', 0, NULL, '2025-05-14', '2025-06-13', NULL, '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (3, 1, 100.00, '1970-01-01', '未支付', 0, NULL, '2025-06-14', '2025-07-13', NULL, '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (4, 1, 103.33, '1970-01-01', '未支付', 0, NULL, '2025-07-14', '2025-08-13', NULL, '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (5, 1, 103.33, '1970-01-01', '未支付', 0, NULL, '2025-08-14', '2025-09-13', NULL, '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (6, 1, 100.00, '1970-01-01', '未支付', 0, NULL, '2025-09-14', '2025-10-13', NULL, '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (7, 1, 103.33, '1970-01-01', '未支付', 0, NULL, '2025-10-14', '2025-11-13', NULL, '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (8, 1, 100.00, '1970-01-01', '未支付', 0, NULL, '2025-11-14', '2025-12-13', NULL, '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (9, 1, 103.33, '1970-01-01', '未支付', 0, NULL, '2025-12-14', '2026-01-13', NULL, '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (10, 1, 103.33, '1970-01-01', '未支付', 0, NULL, '2026-01-14', '2026-02-13', NULL, '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (11, 1, 93.33, '1970-01-01', '未支付', 0, NULL, '2026-02-14', '2026-03-13', NULL, '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (12, 1, 103.33, '2025-04-14', '微信扫码', 1, 'HR250414193356', '2026-03-14', '2026-04-13', '通过扫码完成支付', '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
INSERT INTO `rent_payment` (`id`, `contract_id`, `amount`, `payment_date`, `payment_method`, `payment_status`, `transaction_no`, `period_start`, `period_end`, `remark`, `created_time`, `updated_time`, `deleted`) VALUES (13, 1, 3.33, '2025-04-14', '支付宝', 1, '12313', '2026-04-14', '2026-04-14', '', '2025-04-14 14:30:36', '2025-04-14 14:30:36', 0);
COMMIT;

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(50) NOT NULL COMMENT '配置键',
  `config_value` text NOT NULL COMMENT '配置值',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置表';

-- ----------------------------
-- Records of system_config
-- ----------------------------
BEGIN;
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (1, 'website_name', '房屋租赁平台', '网站名称', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (2, 'website_title', '房屋租赁平台 - 专业的租房平台', '网站标题', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (3, 'website_description', '专业的房屋租赁平台，提供海量房源信息', '网站描述', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (4, 'website_keywords', '租房,房屋租赁,租房平台', '网站关键词', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (5, 'website_logo', '', '网站Logo', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (6, 'website_icp', '', 'ICP备案号', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (7, 'website_phone', '', '客服电话', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (8, 'website_email', '', '客服邮箱', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (9, 'upload_type', 'local', '上传方式：local-本地存储，oss-阿里云OSS，cos-腾讯云COS', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (10, 'upload_path', '/uploads', '上传目录', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (11, 'upload_url', '', '访问域名', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (12, 'email_host', '', 'SMTP服务器', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (13, 'email_port', '', 'SMTP端口', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (14, 'email_username', '', '发件人邮箱', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (15, 'email_password', '', '邮箱密码', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (16, 'email_from', '', '发件人', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (17, 'email_ssl', '0', '是否启用SSL：0-否，1-是', '2024-12-31 10:06:37', '2024-12-31 10:06:37', 0);
INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `description`, `created_time`, `updated_time`, `deleted`) VALUES (18, 'message_type_contract', '4', '消息类型：合同消息', '2025-04-14 10:34:55', '2025-04-14 10:34:55', 0);
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role` varchar(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin-管理员，user-普通用户，landlord-房东',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (1, 'admin', '96e79218965eb72c92a549dd5a330112', '管理员', '/uploads/images/eb053c96-b2a2-49ef-a9bd-5073a90aceb8.jpeg', '18000000000', '123123@qq.com', 'admin', 1, '2025-01-02 16:09:47', '2025-01-23 10:38:39', 0);
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (2, 'landlord', 'e10adc3949ba59abbe56e057f20f883e', '房东', '', '18899990098', 'landlord@213.com', 'landlord', 1, '2025-01-02 16:09:47', '2025-01-23 11:03:03', 0);
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (3, 'user', 'e10adc3949ba59abbe56e057f20f883e', '测试用户01', '/uploads/images/30f20d05-f124-4723-a931-66f7dfb71d6e.jpeg', '18899990098', '133121@qq.com', 'user', 1, '2025-01-02 16:09:47', '2025-01-23 11:00:14', 0);
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (4, 'test', 'e10adc3949ba59abbe56e057f20f883e', NULL, '/uploads/images/70180f14-47d4-420a-a5a6-c23f34d90240.jpeg', '18887766772', '111111@qq.com', 'user', 1, '2025-01-16 14:24:53', '2025-02-18 14:04:09', 0);
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (10, 'ttttttttttt', 'e10adc3949ba59abbe56e057f20f883e', NULL, '', '18812331231', 'tttttttt@123.com', 'landlord', 1, '2025-01-23 11:00:54', '2025-02-18 14:18:11', 0);
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (11, 'yyyyyyyyyyyy', '6590f73ecdf351c38de00befd2ecf17b', NULL, '', '18877766908', 'yyyyyyyyy@222.com', 'landlord', 1, '2025-01-23 11:01:27', '2025-01-23 11:02:31', 1);
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `role`, `status`, `created_time`, `updated_time`, `deleted`) VALUES (12, 'testuser', 'e10adc3949ba59abbe56e057f20f883e', NULL, '/images/634c8508-0102-41df-b485-d626d9be292e.jpeg', '18899765678', 'testuser@123.com', 'user', 1, '2025-01-23 11:15:09', '2025-01-23 11:15:09', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
