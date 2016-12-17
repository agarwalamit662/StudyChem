-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 24, 2014 at 10:13 AM
-- Server version: 5.6.12-log
-- PHP Version: 5.4.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `studychem`
--
CREATE DATABASE IF NOT EXISTS `studychem` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `studychem`;

-- --------------------------------------------------------

--
-- Table structure for table `addassignment`
--

CREATE TABLE IF NOT EXISTS `addassignment` (
  `teacher` varchar(25) NOT NULL,
  `assgname` varchar(25) NOT NULL,
  PRIMARY KEY (`assgname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `addassignment`
--

INSERT INTO `addassignment` (`teacher`, `assgname`) VALUES
('tarangrockr@yahoo.com', '2'),
('tarangrockr@yahoo.com', 'Assg1'),
('tarangrockr@yahoo.com', 'CS'),
('tarangrockr@yahoo.com', 'kkfnsdk'),
('tarangrockr@yahoo.com', 'ksihfksksff'),
('tarangrockr@yahoo.com', 'newassg'),
('tarangrockr@yahoo.com', 'Notenough');

-- --------------------------------------------------------

--
-- Table structure for table `assignment`
--

CREATE TABLE IF NOT EXISTS `assignment` (
  `assgname` varchar(25) NOT NULL,
  `assmsg` varchar(25) NOT NULL,
  `startdate` date NOT NULL,
  `lastdate` date NOT NULL,
  `class` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `class9`
--

CREATE TABLE IF NOT EXISTS `class9` (
  `email` varchar(25) NOT NULL,
  `periodic` int(11) NOT NULL,
  `chemical` int(11) NOT NULL,
  `mixture` int(11) NOT NULL,
  `melting` int(11) NOT NULL,
  `solution` int(11) NOT NULL,
  `boiling` int(11) NOT NULL,
  `exothermic` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `class9`
--

INSERT INTO `class9` (`email`, `periodic`, `chemical`, `mixture`, `melting`, `solution`, `boiling`, `exothermic`) VALUES
('tarangrockr@yahoo.com', 0, 0, 0, 0, 0, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `news`
--

CREATE TABLE IF NOT EXISTS `news` (
  `teacher` varchar(100) NOT NULL,
  `heading` text NOT NULL,
  `content` text NOT NULL,
  `date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `news`
--

INSERT INTO `news` (`teacher`, `heading`, `content`, `date`) VALUES
('tarangrockr@yahoo.com', 'Assignments!', 'Dear Students,\nPlease Submit your assignments by the end of this week!', '2014-03-28 01:17:26'),
('tarangrockr@yahoo.com', 'Quiz', 'Be ready for the surprise quiz', '2014-03-28 01:19:43'),
('tarangrockr@yahoo.com', 'Test', 'Welcome to StudyChem', '2014-03-28 12:23:09'),
('tarangrockr@yahoo.com', 'Hello', 'Test', '2014-03-28 14:56:41'),
('tarangrockr@yahoo.com', '', '', '2014-04-04 08:26:11'),
('tarangrockr@yahoo.com', 'siofjsf', 'jsfksfhhs', '2014-04-04 11:38:41'),
('tarangrockr@yahoo.com', '', '', '2014-04-10 21:16:16'),
('tarangrockr@yahoo.com', 'jsusukd', 'kfsfksffoikhskf\r\nsifkhsfk', '2014-04-12 10:21:37');

-- --------------------------------------------------------

--
-- Table structure for table `notification`
--

CREATE TABLE IF NOT EXISTS `notification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `teacher` varchar(100) NOT NULL,
  `student` varchar(100) NOT NULL,
  `topic` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `problem`
--

CREATE TABLE IF NOT EXISTS `problem` (
  `quizid` varchar(25) NOT NULL,
  `ques_num` int(11) NOT NULL,
  `ques` text NOT NULL,
  `opta` varchar(100) NOT NULL,
  `optb` varchar(100) NOT NULL,
  `optc` varchar(100) NOT NULL,
  `optd` varchar(100) NOT NULL,
  `correctans` varchar(25) NOT NULL,
  `marks` double NOT NULL,
  `teacher` varchar(25) NOT NULL,
  PRIMARY KEY (`quizid`,`ques_num`,`teacher`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `problem`
--

INSERT INTO `problem` (`quizid`, `ques_num`, `ques`, `opta`, `optb`, `optc`, `optd`, `correctans`, `marks`, `teacher`) VALUES
('11', 1, '1+1', '2', '4', '6', '7', 'a', 10, 'tarangrockr@yahoo.com'),
('11', 2, '2+2', '3', '4', '5', '7', 'b', 5, 'tarangrockr@yahoo.com'),
('12', 1, '5+3', '8', '5', '6', '7', 'a', 10, 'tarangrockr@yahoo.com'),
('12', 2, '4*1', '4', '5', '6', '7', 'a', 5, 'tarangrockr@yahoo.com');

-- --------------------------------------------------------

--
-- Table structure for table `quizscore`
--

CREATE TABLE IF NOT EXISTS `quizscore` (
  `student` varchar(100) NOT NULL,
  `quizid` int(11) NOT NULL,
  `score` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `quiz_paper`
--

CREATE TABLE IF NOT EXISTS `quiz_paper` (
  `student` varchar(100) NOT NULL,
  `quizid` int(11) NOT NULL,
  `quesid` int(11) NOT NULL,
  `obtained` int(11) NOT NULL,
  `wr` int(11) NOT NULL,
  PRIMARY KEY (`student`,`quizid`,`quesid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `quiz_paper`
--

INSERT INTO `quiz_paper` (`student`, `quizid`, `quesid`, `obtained`, `wr`) VALUES
('201101089@daiict.ac.in', 11, 1, 10, 1),
('201101089@daiict.ac.in', 11, 2, 5, 1),
('201101089@daiict.ac.in', 12, 1, 10, 1),
('201101089@daiict.ac.in', 12, 2, 5, 1),
('amit@gmail.com', 11, 1, 10, 1),
('amit@gmail.com', 11, 2, 5, 1),
('ekta@gmail.com', 11, 1, 10, 1);

-- --------------------------------------------------------

--
-- Table structure for table `quiz_table`
--

CREATE TABLE IF NOT EXISTS `quiz_table` (
  `teacher` varchar(25) NOT NULL,
  `quizid` int(25) NOT NULL AUTO_INCREMENT,
  `quizname` varchar(25) NOT NULL,
  `quizdes` varchar(200) NOT NULL,
  `num_qsn` int(11) NOT NULL,
  `duration` int(11) NOT NULL,
  `startdate` date NOT NULL,
  `enddate` date NOT NULL,
  `code` varchar(25) NOT NULL,
  `quiznumber` int(25) NOT NULL,
  PRIMARY KEY (`quizid`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=13 ;

--
-- Dumping data for table `quiz_table`
--

INSERT INTO `quiz_table` (`teacher`, `quizid`, `quizname`, `quizdes`, `num_qsn`, `duration`, `startdate`, `enddate`, `code`, `quiznumber`) VALUES
('tarangrockr@yahoo.com', 11, '1st Quiz', 'First ', 0, 50, '2014-04-11', '2014-04-14', '', 1),
('tarangrockr@yahoo.com', 12, 'som', 'kjdnbfkjsfnk', 0, 50, '2014-04-12', '2014-04-15', '', 2);

-- --------------------------------------------------------

--
-- Table structure for table `results`
--

CREATE TABLE IF NOT EXISTS `results` (
  `student` varchar(25) NOT NULL,
  `quizid` varchar(25) NOT NULL,
  `marks` double NOT NULL,
  PRIMARY KEY (`student`,`quizid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `results`
--

INSERT INTO `results` (`student`, `quizid`, `marks`) VALUES
('201101089@daiict.ac.in', '11', 15),
('201101089@daiict.ac.in', '12', 15),
('amit@gmail.com', '11', 15);

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE IF NOT EXISTS `student` (
  `name` text NOT NULL,
  `email` varchar(100) NOT NULL,
  `class` text NOT NULL,
  `password` text NOT NULL,
  `roleid` int(2) NOT NULL,
  `isActivated` int(2) NOT NULL,
  `registrationKey` int(100) NOT NULL,
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`name`, `email`, `class`, `password`, `roleid`, `isActivated`, `registrationKey`) VALUES
('shamp', '201101089@daiict.ac.in', '9', 'hello', 1, 1, 248651860),

('babu', 'agarwalamit662@gmail.com', '9', 'amit1234', 2, 1, 353),
('Amit', 'amit@gmail.com', '9', '12345', 1, 1, 0),
('ekta', 'ekta@gmail.com', '9', '123456', 1, 1, 123456558),
('Gaurav patel', 'gau@gmail.com', '9', '123456', 1, 0, 0),
('Tarang Patel', 'patel_tarang@daiict.ac.in', '9', 'tarang', 1, 1, 756484200),
('Tarang Patel', 'tarangrockr@gmail.com', '10', '123456', 1, 1, 15),
('Tarang Patel', 'tarangrockr@yahoo.com', '9', 'abcdef', 2, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `studentquiz`
--

CREATE TABLE IF NOT EXISTS `studentquiz` (
  `student` varchar(25) NOT NULL,
  `quizid` varchar(25) NOT NULL,
  `quesid` varchar(25) NOT NULL,
  `marked` varchar(25) NOT NULL,
  `status` int(11) NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`student`,`quizid`,`quesid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `studentquiz`
--

INSERT INTO `studentquiz` (`student`, `quizid`, `quesid`, `marked`, `status`, `date`) VALUES
('201101089@daiict.ac.in', '11', '1', 'a', 0, '0000-00-00'),
('201101089@daiict.ac.in', '11', '2', 'b', 0, '0000-00-00'),
('201101089@daiict.ac.in', '12', '1', 'a', 0, '0000-00-00'),
('201101089@daiict.ac.in', '12', '2', 'a', 0, '0000-00-00'),
('amit@gmail.com', '11', '1', 'a', 0, '0000-00-00'),
('amit@gmail.com', '11', '2', 'b', 0, '0000-00-00'),
('ekta@gmail.com', '11', '1', 'a', 0, '0000-00-00');

-- --------------------------------------------------------

--
-- Table structure for table `stuhasattend`
--

CREATE TABLE IF NOT EXISTS `stuhasattend` (
  `student` varchar(25) NOT NULL,
  `date1` date NOT NULL,
  `value` text NOT NULL,
  `teacher` varchar(25) NOT NULL,
  PRIMARY KEY (`student`,`date1`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `stuhasattend`
--

INSERT INTO `stuhasattend` (`student`, `date1`, `value`, `teacher`) VALUES
('201101089@daiict.ac.in', '0000-00-00', '1', 'tarangrockr@yahoo.com'),
('201101089@daiict.ac.in', '2014-04-01', '1', 'tarangrockr@yahoo.com'),
('201101089@daiict.ac.in', '2014-04-02', '0', 'tarangrockr@yahoo.com'),
('201101089@daiict.ac.in', '2014-04-04', '1', 'tarangrockr@yahoo.com'),
('201101089@daiict.ac.in', '2014-04-05', '0', 'tarangrockr@yahoo.com'),
('201101089@daiict.ac.in', '2014-04-10', '1', 'tarangrockr@yahoo.com'),
('201101089@daiict.ac.in', '2014-04-12', '1', 'tarangrockr@yahoo.com'),
('201101089@daiict.ac.in', '2014-04-17', '0', 'tarangrockr@yahoo.com'),
('201101089@daiict.ac.in', '2014-04-18', '1', 'tarangrockr@yahoo.com'),
('amit@gmail.com', '0000-00-00', '1', 'tarangrockr@yahoo.com'),
('amit@gmail.com', '2014-04-01', '0', 'tarangrockr@yahoo.com'),
('amit@gmail.com', '2014-04-02', '0', 'tarangrockr@yahoo.com'),
('amit@gmail.com', '2014-04-10', '0', 'tarangrockr@yahoo.com'),
('amit@gmail.com', '2014-04-12', '0', 'tarangrockr@yahoo.com'),
('amit@gmail.com', '2014-04-18', '1', 'tarangrockr@yahoo.com'),
('amit@gmail.com', '2014-04-19', '0', 'tarangrockr@yahoo.com'),
('gau@gmail.com', '0000-00-00', '1', 'tarangrockr@yahoo.com'),
('gau@gmail.com', '2014-04-01', '1', 'tarangrockr@yahoo.com'),
('gau@gmail.com', '2014-04-02', '0', 'tarangrockr@yahoo.com'),
('gau@gmail.com', '2014-04-10', '0', 'tarangrockr@yahoo.com'),
('gau@gmail.com', '2014-04-11', '1', 'tarangrockr@yahoo.com'),
('gau@gmail.com', '2014-04-12', '1', 'tarangrockr@yahoo.com'),
('gau@gmail.com', '2014-04-18', '0', 'tarangrockr@yahoo.com'),
('gau@gmail.com', '2014-05-15', '0', 'tarangrockr@yahoo.com');

-- --------------------------------------------------------

--
-- Table structure for table `submitnot`
--

CREATE TABLE IF NOT EXISTS `submitnot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `teacher` varchar(100) NOT NULL,
  `student` varchar(100) NOT NULL,
  `assgname` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `teacherclass`
--

CREATE TABLE IF NOT EXISTS `submission` (
  `teacher` varchar(25) NOT NULL,
  `student` varchar(25) NOT NULL,
  `assgname` varchar(100) NOT NULL,
  `subdate` date NOT NULL,
  `path` text NOT NULL,
  PRIMARY KEY (`teacher`,`student`,`assgname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `submission`
--

INSERT INTO `submission` (`teacher`, `student`, `assgname`, `subdate`, `path`) VALUES
('tarangrockr@yahoo.com', '201101089@daiict.ac.in', 'dscs', '2014-04-04', 'C:/wamp/www/StudyChem/submission/IMG_01571.JPG'),
('tarangrockr@yahoo.com', '201101089@daiict.ac.in', 'efeff', '2014-04-06', 'C:/wamp/www/StudyChem/submission/2012-11-17_11.26_.22_.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `teacherclass`
--



CREATE TABLE IF NOT EXISTS `teacherclass` (
  `teacher` varchar(25) NOT NULL,
  `student` varchar(25) NOT NULL,
  PRIMARY KEY (`teacher`,`student`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `teacherclass`
--

INSERT INTO `teacherclass` (`teacher`, `student`) VALUES
('tarangrockr@yahoo.com', '201101089@daiict.ac.in'),
('tarangrockr@yahoo.com', 'amit@gmail.com'),
('tarangrockr@yahoo.com', 'ekta@gmail.com'),
('tarangrockr@yahoo.com', 'gau@gmail.com');

-- --------------------------------------------------------

--
-- Table structure for table `timer`
--

CREATE TABLE IF NOT EXISTS `timer` (
  `student` varchar(25) NOT NULL,
  `quizid` varchar(25) NOT NULL,
  `time_left` int(11) NOT NULL,
  PRIMARY KEY (`student`,`quizid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `timer`
--

INSERT INTO `timer` (`student`, `quizid`, `time_left`) VALUES
('201101089@daiict.ac.in', '11', 0),
('201101089@daiict.ac.in', '12', 0),
('amit@gmail.com', '11', 0),
('amit@gmail.com', '12', 50),
('ekta@gmail.com', '12', 50),
('gau@gmail.com', '11', 50),
('gau@gmail.com', '12', 50);

-- --------------------------------------------------------

--
-- Table structure for table `topic`
--

CREATE TABLE IF NOT EXISTS `topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `teacher` varchar(100) NOT NULL,
  `topic` text NOT NULL,
  `theory` text NOT NULL,
  `show` int(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=19 ;

--
-- Dumping data for table `topic`
--

INSERT INTO `topic` (`id`, `teacher`, `topic`, `theory`, `show`) VALUES
(1, 'tarangrockr@yahoo.com', 'Checking', '  \r\n      \r\n      \r\n    <b>Hello How <font size="6">asdfsasd</font></b>    <div><b><font size="6">asdADAd</font></b></div><div><font size="6"><b>Hey!&nbsp;</b></font></div><div><font size="6"><b>Get the hell outta here!</b></font></div><div><font size="6"><b>Fuck off!l;sdmslfssfl;msf;fsvms;lcxc</b></font></div>        ', 1),
(4, 'tarangrockr@yahoo.com', 'Hello', '  \r\n      \r\n      \r\n      \r\n      \r\n      \r\n      \r\n    asdSD<div><br></div><div><a href="https://www.youtube.com/watch?v=aB0JQRmLN3c" title="sdfs" target="_blank">https://www.youtube.com/watch?v=aB0JQRmLN3c</a></div><div><br><div>asdsadf</div><div>asdasd</div><div><br></div><div>aSDaSD</div><div>asd</div><div>asdas</div><div>d</div><div>ASD</div><div>ASD</div><div>AsDAd</div><div><br></div>                </div>            ', 1),
(5, 'tarangrockr@yahoo.com', 'Melting Point', '<h3 style="text-align: center;"><u>Melting Point</u></h3><div><u><br></u></div><div><ol><li>hello</li><li>asdas</li><li>asdasd</li><li>asdad</li><li>asdasdada</li><li>asdsdsd</li><li>asfsdfasdf</li><li>sdfsdfA</li><li><br></li></ol></div>  \n    ', 0),
(8, 'tarangrockr@yahoo.com', 'kihfseidfhwfoi', 'kfshfkisfhsifolsbksnbkxbvkx', 0),
(10, 'tarangrockr@yahoo.com', 'amit hello', 'kjsbskfjsbck', 0),
(15, 'tarangrockr@yahoo.com', 'meeting', 'Today''S&nbsp;<div><br></div><div><br></div>', 0),
(16, 'tarangrockr@yahoo.com', 'Sahil', 'Sahil is good<div><b>mhmbgjkbkjbkjuubku</b></div>', 0),
(17, 'tarangrockr@yahoo.com', 'vipul', 'jnjksnfksfnskd', 1),
(18, 'tarangrockr@yahoo.com', 'kokil', '<img src="http://i.imgur.com/1BumVlJ.jpg" width="523">  \r\n    <div><br></div><div><br></div><div>Kokil see this</div>', 1);

-- --------------------------------------------------------

--
-- Table structure for table `topicupload`
--

CREATE TABLE IF NOT EXISTS `topicupload` (
  `id1` int(11) NOT NULL AUTO_INCREMENT,
  `id` int(11) NOT NULL,
  `teacher` varchar(100) NOT NULL,
  `student` varchar(100) NOT NULL,
  PRIMARY KEY (`id1`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=31 ;

--
-- Dumping data for table `topicupload`
--

INSERT INTO `topicupload` (`id1`, `id`, `teacher`, `student`) VALUES
(20, 4, 'tarangrockr@yahoo.com', 'amit@gmail.com'),
(21, 4, 'tarangrockr@yahoo.com', 'gau@gmail.com'),
(23, 1, 'tarangrockr@yahoo.com', 'amit@gmail.com'),
(24, 1, 'tarangrockr@yahoo.com', 'gau@gmail.com'),
(26, 18, 'tarangrockr@yahoo.com', 'amit@gmail.com'),
(27, 18, 'tarangrockr@yahoo.com', 'gau@gmail.com'),
(29, 17, 'tarangrockr@yahoo.com', 'amit@gmail.com'),
(30, 17, 'tarangrockr@yahoo.com', 'gau@gmail.com');

-- --------------------------------------------------------

--
-- Table structure for table `upload`
--

CREATE TABLE IF NOT EXISTS `upload` (
  `assgname` varchar(25) NOT NULL,
  `assmsg` varchar(25) NOT NULL,
  `startdate` date NOT NULL,
  `lastdate` date NOT NULL,
  `path` text NOT NULL,
  `teacher` varchar(100) NOT NULL,
  UNIQUE KEY `assgname` (`assgname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `upload`
--

INSERT INTO `upload` (`assgname`, `assmsg`, `startdate`, `lastdate`, `path`, `teacher`) VALUES
('2', 'oifs', '2014-04-11', '2014-04-20', 'C:/wamp/www/StudyChem/uploads/gantt_chart.pdf', 'tarangrockr@yahoo.com'),
('Assg1', 'ifdsob', '2014-04-08', '2014-04-10', 'C:/wamp/www/StudyChem/uploads/201101104_Rajesh_Kumar_Gaur_Resume.pdf', 'tarangrockr@yahoo.com'),
('CS', 'oisflsdld', '2014-04-11', '2014-04-12', 'C:/wamp/www/StudyChem/uploads/gantt_chart1.pdf', 'tarangrockr@yahoo.com'),
('kkfnsdk', 'ksnksnsksdnk', '2014-04-09', '2014-04-12', 'C:/wamp/www/StudyChem/uploads/gantt_chart3.pdf', 'tarangrockr@yahoo.com'),
('ksihfksksff', 'ihsidhifhaoidh', '2014-04-11', '2014-04-12', 'C:/wamp/www/StudyChem/uploads/201101104_Rajesh_Kumar_Gaur_Resume2.pdf', 'tarangrockr@yahoo.com'),
('newassg', 'oijhoijoi', '2014-04-11', '2014-04-12', 'C:/wamp/www/StudyChem/uploads/clientserver1.pdf', 'tarangrockr@yahoo.com'),
('Notenough ', 'slfjsflsd', '2014-04-09', '2014-04-18', 'C:/wamp/www/StudyChem/uploads/gantt_chart2.pdf', 'tarangrockr@yahoo.com');

-- --------------------------------------------------------

--
-- Table structure for table `uploadnot`
--

CREATE TABLE IF NOT EXISTS `uploadnot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `teacher` varchar(200) NOT NULL,
  `student` varchar(200) NOT NULL,
  `assgname` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=13 ;

--
-- Dumping data for table `uploadnot`
--

INSERT INTO `uploadnot` (`id`, `teacher`, `student`, `assgname`) VALUES
(8, 'tarangrockr@yahoo.com', 'amit@gmail.com', 'Not enough '),
(9, 'tarangrockr@yahoo.com', 'gau@gmail.com', 'Not enough '),
(11, 'tarangrockr@yahoo.com', 'amit@gmail.com', 'kkfnsdk'),
(12, 'tarangrockr@yahoo.com', 'gau@gmail.com', 'kkfnsdk');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
