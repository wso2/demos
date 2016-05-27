-- MySQL dump 10.13  Distrib 5.5.49, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: bk
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
-- Table structure for table `GoogleLatestNews`
--

DROP TABLE IF EXISTS `GoogleLatestNews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GoogleLatestNews` (
  `CDateTime` varchar(1000) DEFAULT NULL,
  `text` varchar(1000) DEFAULT NULL,
  `Pub_Date` varchar(1000) DEFAULT NULL,
  `Link` varchar(1000) DEFAULT NULL,
  `Discription` varchar(50000) DEFAULT NULL,
  `Owner` varchar(1000) DEFAULT NULL,
  `ID` int(11) NOT NULL,
  `DateTime` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GoogleLatestNews`
--

LOCK TABLES `GoogleLatestNews` WRITE;
/*!40000 ALTER TABLE `GoogleLatestNews` DISABLE KEYS */;
/*!40000 ALTER TABLE `GoogleLatestNews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `GoogleToolTipNews`
--

DROP TABLE IF EXISTS `GoogleToolTipNews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GoogleToolTipNews` (
  `DateTime` varchar(500) DEFAULT NULL,
  `TRUMP` varchar(500) DEFAULT NULL,
  `CLINTON` varchar(500) DEFAULT NULL,
  `BERNIE` varchar(500) DEFAULT NULL,
  `CRUZ` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GoogleToolTipNews`
--

LOCK TABLES `GoogleToolTipNews` WRITE;
/*!40000 ALTER TABLE `GoogleToolTipNews` DISABLE KEYS */;
/*!40000 ALTER TABLE `GoogleToolTipNews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PopularLinks`
--

DROP TABLE IF EXISTS `PopularLinks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PopularLinks` (
  `DateTime` varchar(1000) DEFAULT NULL,
  `text` varchar(1000) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `Ft` int(11) DEFAULT NULL,
  `Rt` int(11) DEFAULT NULL,
  `HTag` varchar(500) DEFAULT NULL,
  `Retwitter` varchar(200) DEFAULT NULL,
  `OwnerFull` varchar(200) DEFAULT NULL,
  `RetwitterFullName` varchar(200) DEFAULT NULL,
  `created_at` varchar(100) DEFAULT NULL,
  `url` varchar(5000) DEFAULT NULL,
  `imgUrl` varchar(10000) DEFAULT 'images/logo.png',
  `Owner` varchar(10000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PopularLinks`
--

LOCK TABLES `PopularLinks` WRITE;
/*!40000 ALTER TABLE `PopularLinks` DISABLE KEYS */;
/*!40000 ALTER TABLE `PopularLinks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SentimentRate`
--

DROP TABLE IF EXISTS `SentimentRate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SentimentRate` (
  `DateTime` varchar(100) DEFAULT NULL,
  `CDate` varchar(100) DEFAULT NULL,
  `TRUMP` double DEFAULT NULL,
  `CLINTON` double DEFAULT NULL,
  `BERNIE` double DEFAULT NULL,
  `CRUZ` double DEFAULT NULL,
  `RUBIE` double DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `ID` varchar(200) DEFAULT NULL
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
-- Table structure for table `TweetPopularCandidate`
--

DROP TABLE IF EXISTS `TweetPopularCandidate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TweetPopularCandidate` (
  `DateTime` varchar(1000) DEFAULT NULL,
  `text` varchar(1000) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `Owner` varchar(100) DEFAULT NULL,
  `Ft` int(11) DEFAULT NULL,
  `Rt` int(11) DEFAULT NULL,
  `HTag` varchar(500) DEFAULT NULL,
  `Retwitter` varchar(200) DEFAULT NULL,
  `OwnerFull` varchar(200) DEFAULT NULL,
  `RetwitterFullName` varchar(200) DEFAULT NULL,
  `created_at` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TweetPopularCandidate`
--

LOCK TABLES `TweetPopularCandidate` WRITE;
/*!40000 ALTER TABLE `TweetPopularCandidate` DISABLE KEYS */;
/*!40000 ALTER TABLE `TweetPopularCandidate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TweetPopularElection`
--

DROP TABLE IF EXISTS `TweetPopularElection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TweetPopularElection` (
  `DateTime` varchar(1000) DEFAULT NULL,
  `text` varchar(1000) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `Owner` varchar(100) DEFAULT NULL,
  `Ft` int(11) DEFAULT NULL,
  `Rt` int(11) DEFAULT NULL,
  `HTag` varchar(500) DEFAULT NULL,
  `Retwitter` varchar(200) DEFAULT NULL,
  `OwnerFull` varchar(200) DEFAULT NULL,
  `RetwitterFullName` varchar(200) DEFAULT NULL,
  `created_at` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TweetPopularElection`
--

LOCK TABLES `TweetPopularElection` WRITE;
/*!40000 ALTER TABLE `TweetPopularElection` DISABLE KEYS */;
/*!40000 ALTER TABLE `TweetPopularElection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `WordCloud`
--

DROP TABLE IF EXISTS `WordCloud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WordCloud` (
  `DateTime` varchar(1000) DEFAULT NULL,
  `Election` longtext,
  `TRUMP` longtext,
  `CLINTON` longtext,
  `BERNIE` longtext,
  `CRUZ` longtext,
  `CDate` varchar(100) DEFAULT NULL,
  `ID` int(11) DEFAULT NULL,
  `count` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `WordCloud`
--

LOCK TABLES `WordCloud` WRITE;
/*!40000 ALTER TABLE `WordCloud` DISABLE KEYS */;
/*!40000 ALTER TABLE `WordCloud` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-02 11:40:26
