-- MySQL dump 10.13  Distrib 5.5.49, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: use16_cep_data_db
-- ------------------------------------------------------
-- Server version	5.5.49-0ubuntu0.14.04.1

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
-- Table structure for table `Clouds`
--

DROP TABLE IF EXISTS `Clouds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Clouds` (
  `id` int(11) DEFAULT NULL,
  `TrumpText` text,
  `ClintonText` text,
  `CruzText` text,
  `BernieText` text,
  KEY `id` (`id`),
  KEY `id_2` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Clouds`
--

LOCK TABLES `Clouds` WRITE;
/*!40000 ALTER TABLE `Clouds` DISABLE KEYS */;
INSERT INTO `Clouds` VALUES (1,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `Clouds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CombinedWordCloud`
--

DROP TABLE IF EXISTS `CombinedWordCloud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CombinedWordCloud` (
  `id` int(11) DEFAULT NULL,
  `TRUMP` text,
  `CLINTON` text,
  `BERNIE` text,
  `CRUZ` text,
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CombinedWordCloud`
--

LOCK TABLES `CombinedWordCloud` WRITE;
/*!40000 ALTER TABLE `CombinedWordCloud` DISABLE KEYS */;
INSERT INTO `CombinedWordCloud` VALUES (1,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `CombinedWordCloud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CandidatePopular`
--

DROP TABLE IF EXISTS `CandidatePopular`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CandidatePopular` (
  `text` varchar(1000) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `Owner` varchar(100) DEFAULT NULL,
  `Ft` int(11) DEFAULT NULL,
  `Rt` int(11) DEFAULT NULL,
  `HTag` varchar(500) DEFAULT NULL,
  `Retwitter` varchar(200) DEFAULT NULL,
  `OwnerFull` varchar(200) DEFAULT NULL,
  `RetwitterFullName` varchar(200) DEFAULT NULL,
  `created_at` varchar(100) DEFAULT NULL,
  KEY `count` (`count`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CandidatePopular`
--

LOCK TABLES `CandidatePopular` WRITE;
/*!40000 ALTER TABLE `CandidatePopular` DISABLE KEYS */;
INSERT INTO `CandidatePopular` VALUES (NULL,1,NULL,0,0,NULL,NULL,NULL,NULL,NULL),(NULL,2,NULL,0,0,NULL,NULL,NULL,NULL,NULL),(NULL,3,NULL,0,0,NULL,NULL,NULL,NULL,NULL),(NULL,4,NULL,0,0,NULL,NULL,NULL,NULL,NULL),(NULL,5,NULL,0,0,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `CandidatePopular` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ElectionPopular`
--

DROP TABLE IF EXISTS `ElectionPopular`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ElectionPopular` (
  `text` varchar(1000) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `Owner` varchar(100) DEFAULT NULL,
  `Ft` int(11) DEFAULT NULL,
  `Rt` int(11) DEFAULT NULL,
  `HTag` varchar(500) DEFAULT NULL,
  `Retwitter` varchar(200) DEFAULT NULL,
  `OwnerFull` varchar(200) DEFAULT NULL,
  `RetwitterFullName` varchar(200) DEFAULT NULL,
  `created_at` varchar(100) DEFAULT NULL,
  KEY `count` (`count`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ElectionPopular`
--

LOCK TABLES `ElectionPopular` WRITE;
/*!40000 ALTER TABLE `ElectionPopular` DISABLE KEYS */;
INSERT INTO `ElectionPopular` VALUES (NULL,1,NULL,0,0,NULL,NULL,NULL,NULL,NULL),(NULL,2,NULL,0,0,NULL,NULL,NULL,NULL,NULL),(NULL,3,NULL,0,0,NULL,NULL,NULL,NULL,NULL),(NULL,4,NULL,0,0,NULL,NULL,NULL,NULL,NULL),(NULL,5,NULL,0,0,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `ElectionPopular` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PopularLinks`
--

DROP TABLE IF EXISTS `PopularLinks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PopularLinks` (
  `text` varchar(1000) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `Owner` varchar(100) DEFAULT NULL,
  `Ft` int(11) DEFAULT NULL,
  `Rt` int(11) DEFAULT NULL,
  `HTag` varchar(500) DEFAULT NULL,
  `Retwitter` varchar(200) DEFAULT NULL,
  `OwnerFull` varchar(200) DEFAULT NULL,
  `RetwitterFullName` varchar(200) DEFAULT NULL,
  `created_at` varchar(100) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  KEY `count` (`count`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PopularLinks`
--

LOCK TABLES `PopularLinks` WRITE;
/*!40000 ALTER TABLE `PopularLinks` DISABLE KEYS */;
INSERT INTO `PopularLinks` VALUES (NULL,1,NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL),(NULL,2,NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL),(NULL,3,NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL),(NULL,4,NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL),(NULL,5,NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `PopularLinks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SentimentRate`
--

DROP TABLE IF EXISTS `SentimentRate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SentimentRate` (
  `CDate` varchar(100) DEFAULT NULL,
  `TRUMP` double DEFAULT NULL,
  `CLINTON` double DEFAULT NULL,
  `BERNIE` double DEFAULT NULL,
  `CRUZ` double DEFAULT NULL  
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SentimentRate`
--

LOCK TABLES `SentimentRate` WRITE;
/*!40000 ALTER TABLE `SentimentRate` DISABLE KEYS */;
/*!40000 ALTER TABLE `SentimentRate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TweetLatest`
--

DROP TABLE IF EXISTS `TweetLatest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TweetLatest` (
  `text` varchar(1000) DEFAULT NULL,
  `Owner` varchar(100) DEFAULT NULL,
  `ID` varchar(500) NOT NULL,
  `OwnerFullName` varchar(100) DEFAULT NULL,
  `HTags` varchar(200) DEFAULT NULL,
  `created_at` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TweetLatest`
--

LOCK TABLES `TweetLatest` WRITE;
/*!40000 ALTER TABLE `TweetLatest` DISABLE KEYS */;
/*!40000 ALTER TABLE `TweetLatest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WorldCloudNew`
--

DROP TABLE IF EXISTS `WorldCloudNew`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WorldCloudNew` (
  `id` int(11) DEFAULT NULL,
  `TRUMP` text,
  `CLINTON` text,
  `BERNIE` text,
  `CRUZ` text,
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WorldCloudNew`
--

LOCK TABLES `WorldCloudNew` WRITE;
/*!40000 ALTER TABLE `WorldCloudNew` DISABLE KEYS */;
INSERT INTO `WorldCloudNew` VALUES (1,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `WorldCloudNew` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fbmenstions`
--

DROP TABLE IF EXISTS `fbmenstions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fbmenstions` (
  `ID` int(11) DEFAULT NULL,
  `CLINTON` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fbmenstions`
--

LOCK TABLES `fbmenstions` WRITE;
/*!40000 ALTER TABLE `fbmenstions` DISABLE KEYS */;
/*!40000 ALTER TABLE `fbmenstions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `googleLatestNews`
--

DROP TABLE IF EXISTS `googleLatestNews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `googleLatestNews` (
  `text` varchar(1000) DEFAULT NULL,
  `Pub_Date` varchar(1000) DEFAULT NULL,
  `Link` varchar(1000) DEFAULT NULL,
  `Discription` varchar(50000) DEFAULT NULL,
  `Owner` varchar(1000) DEFAULT NULL,
  `ID` int(11) NOT NULL,
  `DateTime` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `googleLatestNews`
--

LOCK TABLES `googleLatestNews` WRITE;
/*!40000 ALTER TABLE `googleLatestNews` DISABLE KEYS */;
/*!40000 ALTER TABLE `googleLatestNews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news` (
  `TRUMP` varchar(500) DEFAULT NULL,
  `CLINTON` varchar(500) DEFAULT NULL,
  `BERNIE` varchar(500) DEFAULT NULL,
  `CRUZ` varchar(500) DEFAULT NULL,
  `CDate` varchar(100) DEFAULT NULL,
  `ID` int(11) DEFAULT NULL,
  KEY `count` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-02  9:54:01
