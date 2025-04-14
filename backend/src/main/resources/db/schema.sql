-- MySQL dump 10.13  Distrib 8.0.40, for macos14 (arm64)
--
-- Host: localhost    Database: houseRental
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `booking`
--

DROP TABLE IF EXISTS `booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预约表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking`
--

/*!40000 ALTER TABLE `booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (18,3,2,'2025-01-03 16:12:50','2025-01-06 15:05:33',1),(19,3,3,'2025-01-03 16:37:54','2025-01-06 16:18:50',0),(20,3,1,'2025-01-03 16:38:02','2025-01-06 15:05:46',1),(24,3,2,'2025-01-06 17:12:06','2025-01-06 17:12:06',0);
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `house`
--

DROP TABLE IF EXISTS `house`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
                         `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
                         PRIMARY KEY (`id`),
                         KEY `idx_owner` (`owner_id`),
                         KEY `idx_status` (`status`),
                         KEY `idx_location` (`province`,`city`,`district`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房源表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `house`
--

/*!40000 ALTER TABLE `house` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,2,'预约确认通知','您预约查看的房源已确认，请准时到达。',2,0,0,'2025-01-03 14:25:53','2025-01-03 14:25:53',0),(2,2,'系统通知','欢迎使用房屋租赁平台！',1,0,1,'2025-01-03 14:25:53','2025-01-03 14:25:53',0),(3,2,'预约取消通知','很抱歉，您预约的房源因特殊原因已被取消。',2,0,0,'2025-01-03 14:25:53','2025-01-03 14:25:53',0),(4,3,'系统通知','欢迎使用房屋租赁平台！',1,1,0,'2025-01-03 14:25:53','2025-01-06 16:31:08',0),(5,3,'预约提醒','您有一个预约即将开始，请准时到达。',2,1,0,'2025-01-03 14:25:53','2025-01-06 16:31:16',0),(6,3,'个人信息更新通知','您的个人信息已更新：邮箱已更新；',1,1,0,'2025-01-06 16:51:18','2025-01-06 16:51:29',0),(7,3,'个人信息更新通知','您的个人信息已更新：昵称已更新；',1,1,0,'2025-01-06 17:11:54','2025-01-06 17:11:58',0),(8,3,'收藏提醒','您已成功收藏房源：整租·福田区豪华三房·江景房',2,1,0,'2025-01-06 17:12:06','2025-01-06 17:12:56',0),(9,3,'房源状态变更提醒','您收藏的房源：整租·福田区豪华三房·江景房 房源已下线',3,0,0,'2025-01-16 09:41:38','2025-01-16 09:41:38',0),(10,3,'房源状态变更提醒','您收藏的房源：整租·福田区豪华三房·江景房 房源已上线',3,0,0,'2025-01-16 09:41:39','2025-01-16 09:41:39',0),(11,3,'房源状态变更提醒','您收藏的房源：整租·罗湖区温馨一房·近地铁 房源已下线',3,0,0,'2025-01-16 09:41:52','2025-01-16 09:41:52',0),(12,3,'房源状态变更提醒','您收藏的房源：整租·罗湖区温馨一房·近地铁 房源已上线',3,0,0,'2025-01-16 09:41:52','2025-01-16 09:41:52',0),(13,2,'账号删除通知','您的账号已被管理员删除。',1,0,0,'2025-01-16 11:08:04','2025-01-16 11:08:04',0);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_config`
--

DROP TABLE IF EXISTS `system_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_config`
--

/*!40000 ALTER TABLE `system_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'houseRental'
--
