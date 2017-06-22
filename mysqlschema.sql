-- MySQL dump 10.13  Distrib 5.6.36, for macos10.12 (x86_64)
--
-- Host: localhost    Database: price_monitor
-- ------------------------------------------------------
-- Server version	5.6.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) DEFAULT NULL,
  `product_list_url` varchar(255) DEFAULT NULL,
  `create_time` bigint(20) NOT NULL,
  `update_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category_priority`
--

DROP TABLE IF EXISTS `category_priority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category_priority` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL,
  `create_time` bigint(20) NOT NULL,
  `priority` int(11) NOT NULL,
  `update_time` bigint(20) NOT NULL,
  `user_count` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_category_priority` (`category_id`,`priority`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL,
  `create_time` bigint(20) NOT NULL,
  `detail_url` varchar(255) DEFAULT NULL,
  `discount_percent` double NOT NULL,
  `old_price` double NOT NULL,
  `price` double NOT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  `thumnail` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `update_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93049 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `notification_type` int(11) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `update_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_category`
--

DROP TABLE IF EXISTS `user_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `create_time` bigint(20) NOT NULL,
  `update_time` bigint(20) NOT NULL,
  `min_discount_percent` double NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_user_id_category_id` (`user_id`,`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_count_threshold`
--

DROP TABLE IF EXISTS `user_count_threshold`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_count_threshold` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` bigint(20) NOT NULL,
  `high_priority_user_count` bigint(20) NOT NULL,
  `update_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-06-21 21:49:57
