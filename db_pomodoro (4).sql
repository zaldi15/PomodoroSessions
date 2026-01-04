-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jan 04, 2026 at 05:12 PM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_pomodoro`
--

-- --------------------------------------------------------

--
-- Table structure for table `missions`
--

CREATE TABLE `missions` (
  `id_mission` int NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `target_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `missions`
--

INSERT INTO `missions` (`id_mission`, `title`, `description`, `target_date`) VALUES
(2, 'Susah', '----', '2026-01-02');

-- --------------------------------------------------------

--
-- Table structure for table `pomodoro_sessions`
--

CREATE TABLE `pomodoro_sessions` (
  `session_id` int NOT NULL,
  `user_id` int NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `focus_duration` int DEFAULT NULL,
  `break_duration` int DEFAULT NULL,
  `total_sessions` int DEFAULT NULL,
  `completed` tinyint(1) DEFAULT '0',
  `category` varchar(50) COLLATE utf8mb4_general_ci DEFAULT 'Umum'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pomodoro_sessions`
--

INSERT INTO `pomodoro_sessions` (`session_id`, `user_id`, `start_time`, `end_time`, `focus_duration`, `break_duration`, `total_sessions`, `completed`, `category`) VALUES
(105, 77, '2026-01-04 08:00:00', '2026-01-04 08:30:00', 36, 7, 2, 0, 'Academic'),
(106, 78, '2026-01-04 08:00:00', '2026-01-04 08:30:00', 30, 3, 1, 1, 'Academic'),
(107, 79, '2026-01-04 08:00:00', '2026-01-04 08:30:00', 46, 4, 5, 0, 'Academic'),
(108, 80, '2026-01-04 08:00:00', '2026-01-04 08:30:00', 15, 5, 1, 0, 'Academic'),
(109, 77, '2026-01-04 09:00:00', '2026-01-04 09:30:00', 25, 5, 1, 1, 'Development'),
(110, 78, '2026-01-04 09:00:00', '2026-01-04 09:30:00', 25, 5, 1, 1, 'Development'),
(111, 79, '2026-01-04 09:00:00', '2026-01-04 09:30:00', 25, 5, 1, 1, 'Development'),
(112, 80, '2026-01-04 09:00:00', '2026-01-04 09:30:00', 25, 5, 1, 1, 'Development'),
(113, 77, '2026-01-04 10:00:00', '2026-01-04 10:30:00', 100, 5, 10, 1, 'Project'),
(114, 78, '2026-01-04 10:00:00', '2026-01-04 10:30:00', 25, 5, 1, 1, 'Project'),
(115, 79, '2026-01-04 10:00:00', '2026-01-04 10:30:00', 25, 5, 1, 1, 'Project'),
(116, 80, '2026-01-04 10:00:00', '2026-01-04 10:30:00', 25, 5, 1, 1, 'Project'),
(117, 77, '2026-01-04 13:00:00', '2026-01-04 13:30:00', 25, 5, 1, 1, 'Development'),
(118, 78, '2026-01-04 13:00:00', '2026-01-04 13:30:00', 25, 5, 1, 1, 'Development'),
(119, 79, '2026-01-04 13:00:00', '2026-01-04 13:30:00', 25, 5, 1, 1, 'Development'),
(120, 80, '2026-01-04 13:00:00', '2026-01-04 13:30:00', 40, 5, 3, 1, 'Development'),
(121, 77, '2026-01-04 14:00:00', '2026-01-04 14:30:00', 25, 5, 1, 1, 'Academic'),
(122, 78, '2026-01-04 14:00:00', '2026-01-04 14:30:00', 25, 5, 1, 1, 'Academic'),
(123, 79, '2026-01-04 14:00:00', '2026-01-04 14:30:00', 25, 5, 1, 1, 'Academic'),
(124, 80, '2026-01-04 14:00:00', '2026-01-04 14:30:00', 25, 5, 1, 1, 'Academic'),
(136, 77, '2026-01-04 15:00:00', '2026-01-04 16:00:00', 50, 10, 3, 1, 'Development'),
(137, 78, '2026-01-04 15:00:00', '2026-01-04 16:00:00', 35, 10, 1, 0, 'Development'),
(138, 79, '2026-01-04 15:00:00', '2026-01-04 16:00:00', 50, 10, 1, 1, 'Development'),
(139, 80, '2026-01-04 15:00:00', '2026-01-04 16:00:00', 50, 10, 5, 1, 'Development'),
(143, 77, '2026-01-04 16:30:00', '2026-01-04 16:45:00', 60, 10, 3, 0, 'Academic'),
(144, 78, '2026-01-04 16:30:00', '2026-01-04 16:45:00', 15, 1, 1, 0, 'Academic'),
(145, 79, '2026-01-04 16:30:00', '2026-01-04 16:45:00', 15, 2, 1, 0, 'Academic'),
(146, 80, '2026-01-04 16:30:00', '2026-01-04 16:45:00', 41, 3, 1, 0, 'Academic'),
(150, 77, '2026-01-04 19:00:00', '2026-01-04 21:00:00', 100, 20, 4, 1, 'Project'),
(151, 78, '2026-01-04 19:00:00', '2026-01-04 21:00:00', 100, 20, 4, 1, 'Project'),
(152, 79, '2026-01-04 19:00:00', '2026-01-04 21:00:00', 100, 20, 4, 1, 'Project'),
(153, 80, '2026-01-04 19:00:00', '2026-01-04 21:00:00', 100, 20, 4, 1, 'Project'),
(157, 81, '2026-01-04 09:00:00', '2026-01-04 09:35:00', 30, 5, 2, 1, 'Academic'),
(158, 82, '2026-01-04 10:00:00', '2026-01-04 10:20:00', 20, 3, 1, 0, 'Development'),
(159, 83, '2026-01-04 11:00:00', '2026-01-04 12:10:00', 60, 10, 3, 1, 'Project'),
(160, 84, '2026-01-04 13:00:00', '2026-01-04 13:55:00', 45, 8, 4, 1, 'Development'),
(161, 85, '2026-01-04 14:00:00', '2026-01-04 15:15:00', 50, 15, 5, 1, 'Academic'),
(162, 86, '2026-01-04 16:00:00', '2026-01-04 18:30:00', 55, 12, 6, 1, 'Project'),
(163, 77, '2026-01-04 08:00:00', '2026-01-04 08:30:00', 25, 5, 1, 1, 'development'),
(164, 77, '2026-01-04 09:00:00', '2026-01-04 09:55:00', 40, 10, 2, 0, 'project'),
(165, 77, '2026-01-04 11:00:00', '2026-01-04 12:15:00', 50, 15, 3, 1, 'development'),
(166, 77, '2026-01-04 13:00:00', '2026-01-04 13:45:00', 30, 8, 4, 1, 'academic'),
(167, 77, '2026-01-04 15:00:00', '2026-01-04 16:10:00', 45, 7, 5, 0, 'development'),
(168, 78, '2026-01-04 08:00:00', '2026-01-04 09:30:00', 60, 20, 6, 1, 'academic'),
(169, 78, '2026-01-04 10:00:00', '2026-01-04 10:30:00', 20, 4, 7, 1, 'academic'),
(170, 78, '2026-01-04 11:00:00', '2026-01-04 11:55:00', 35, 12, 8, 0, 'project'),
(171, 78, '2026-01-04 13:00:00', '2026-01-04 14:20:00', 55, 18, 9, 1, 'academic'),
(172, 78, '2026-01-04 15:00:00', '2026-01-04 15:40:00', 28, 6, 10, 0, 'academic'),
(173, 79, '2026-01-04 08:00:00', '2026-01-04 08:50:00', 32, 9, 11, 1, 'project'),
(174, 79, '2026-01-04 09:30:00', '2026-01-04 10:10:00', 22, 3, 12, 1, 'project'),
(175, 79, '2026-01-04 11:00:00', '2026-01-04 12:10:00', 48, 11, 13, 0, 'development'),
(176, 79, '2026-01-04 13:00:00', '2026-01-04 15:15:00', 90, 30, 14, 1, 'project'),
(177, 79, '2026-01-04 16:00:00', '2026-01-04 16:30:00', 15, 2, 15, 0, 'academic'),
(178, 80, '2026-01-04 08:00:00', '2026-01-04 08:50:00', 26, 13, 16, 1, 'project'),
(179, 80, '2026-01-04 10:00:00', '2026-01-04 11:05:00', 38, 14, 17, 0, 'development'),
(180, 80, '2026-01-04 12:00:00', '2026-01-04 13:10:00', 42, 16, 18, 1, 'academic'),
(181, 80, '2026-01-04 14:00:00', '2026-01-04 15:20:00', 52, 17, 19, 0, 'project'),
(182, 80, '2026-01-04 16:00:00', '2026-01-04 17:30:00', 58, 19, 20, 1, 'development'),
(183, 81, '2026-01-04 07:00:00', '2026-01-04 08:00:00', 27, 21, 21, 1, 'development'),
(184, 81, '2026-01-04 09:00:00', '2026-01-04 10:05:00', 33, 22, 22, 0, 'development'),
(185, 81, '2026-01-04 11:00:00', '2026-01-04 12:15:00', 41, 23, 23, 1, 'development'),
(186, 81, '2026-01-04 14:00:00', '2026-01-04 15:25:00', 47, 24, 24, 0, 'project'),
(187, 81, '2026-01-04 16:00:00', '2026-01-04 17:35:00', 53, 25, 25, 1, 'project'),
(188, 82, '2026-01-04 08:00:00', '2026-01-04 09:35:00', 61, 31, 26, 1, 'development'),
(189, 82, '2026-01-04 10:00:00', '2026-01-04 11:36:00', 62, 32, 27, 0, 'development'),
(190, 82, '2026-01-04 12:00:00', '2026-01-04 13:37:00', 63, 33, 28, 1, 'project'),
(191, 82, '2026-01-04 14:00:00', '2026-01-04 15:38:00', 64, 34, 29, 0, 'development'),
(192, 82, '2026-01-04 16:00:00', '2026-01-04 17:39:00', 65, 35, 30, 1, 'development'),
(193, 83, '2026-01-04 08:00:00', '2026-01-04 09:40:00', 66, 36, 31, 1, 'academic'),
(194, 83, '2026-01-04 10:00:00', '2026-01-04 11:41:00', 67, 37, 32, 0, 'academic'),
(195, 83, '2026-01-04 12:00:00', '2026-01-04 13:42:00', 68, 38, 33, 1, 'academic'),
(196, 83, '2026-01-04 14:00:00', '2026-01-04 15:43:00', 69, 39, 34, 1, 'academic'),
(197, 83, '2026-01-04 16:00:00', '2026-01-04 17:44:00', 70, 40, 35, 0, 'academic'),
(198, 84, '2026-01-04 08:00:00', '2026-01-04 09:45:00', 71, 41, 36, 1, 'project'),
(199, 84, '2026-01-04 10:00:00', '2026-01-04 11:46:00', 72, 42, 37, 0, 'development'),
(200, 84, '2026-01-04 12:00:00', '2026-01-04 13:47:00', 73, 43, 38, 1, 'project'),
(201, 84, '2026-01-04 14:00:00', '2026-01-04 15:48:00', 74, 44, 39, 0, 'development'),
(202, 84, '2026-01-04 16:00:00', '2026-01-04 17:49:00', 75, 45, 40, 1, 'project'),
(203, 85, '2026-01-04 08:00:00', '2026-01-04 09:50:00', 76, 46, 41, 1, 'project'),
(204, 85, '2026-01-04 10:00:00', '2026-01-04 11:51:00', 77, 47, 42, 1, 'development'),
(205, 85, '2026-01-04 12:00:00', '2026-01-04 13:52:00', 78, 48, 43, 0, 'project'),
(206, 85, '2026-01-04 14:00:00', '2026-01-04 15:53:00', 79, 49, 44, 1, 'development'),
(207, 85, '2026-01-04 16:00:00', '2026-01-04 17:54:00', 80, 50, 45, 0, 'project'),
(208, 86, '2026-01-04 08:00:00', '2026-01-04 09:55:00', 81, 51, 46, 1, 'academic'),
(209, 86, '2026-01-04 10:00:00', '2026-01-04 11:56:00', 82, 52, 47, 1, 'academic'),
(210, 86, '2026-01-04 12:00:00', '2026-01-04 13:57:00', 83, 53, 48, 0, 'academic'),
(211, 86, '2026-01-04 14:00:00', '2026-01-04 15:58:00', 84, 54, 49, 1, 'academic'),
(212, 86, '2026-01-04 16:00:00', '2026-01-04 17:59:00', 85, 55, 50, 0, 'academic');

-- --------------------------------------------------------

--
-- Table structure for table `reminders`
--

CREATE TABLE `reminders` (
  `reminder_id` int NOT NULL,
  `task_id` int DEFAULT NULL,
  `user_id` int NOT NULL,
  `reminder_time` datetime NOT NULL,
  `message` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `sent` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `report`
--

CREATE TABLE `report` (
  `report_id` int NOT NULL,
  `user_id` int NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `completed_tasks` int DEFAULT '0',
  `average_focus_hours` decimal(10,2) DEFAULT '0.00',
  `generated_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `report`
--

INSERT INTO `report` (`report_id`, `user_id`, `start_date`, `end_date`, `completed_tasks`, `average_focus_hours`, `generated_at`) VALUES
(5, 56, '2026-01-01', '2026-01-31', 5, '0.02', '2026-01-04 14:07:21'),
(6, 56, '2026-01-01', '2026-01-31', 5, '0.02', '2026-01-04 14:08:43'),
(7, 56, '2026-01-01', '2026-01-31', 5, '0.04', '2026-01-04 14:10:44'),
(8, 56, '2026-01-01', '2026-01-31', 6, '0.04', '2026-01-04 14:11:31'),
(9, 56, '2026-01-01', '2026-01-31', 6, '0.04', '2026-01-04 14:11:59'),
(10, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:13:38'),
(11, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:15:16'),
(12, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:15:21'),
(13, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:15:49'),
(14, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:15:51'),
(15, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:24:18'),
(16, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:27:27'),
(17, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:27:57'),
(18, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:32:40'),
(19, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:42:56'),
(20, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:43:19'),
(21, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 14:45:26'),
(22, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 15:24:48'),
(23, 56, '2026-01-01', '2026-01-31', 7, '0.06', '2026-01-04 15:36:06'),
(24, 56, '2026-01-01', '2026-01-31', 8, '0.06', '2026-01-04 15:36:26'),
(25, 56, '2026-01-01', '2026-01-31', 6, '0.00', '2026-01-04 15:37:41'),
(26, 56, '2026-01-01', '2026-01-31', 7, '0.12', '2026-01-04 15:39:37'),
(27, 56, '2026-01-01', '2026-01-31', 7, '0.12', '2026-01-04 15:48:59'),
(28, 56, '2026-01-01', '2026-01-31', 8, '0.07', '2026-01-04 15:58:17'),
(29, 56, '2026-01-01', '2026-01-31', 8, '0.07', '2026-01-04 15:59:24'),
(30, 56, '2026-01-01', '2026-01-31', 8, '0.07', '2026-01-04 15:59:29'),
(31, 56, '2026-01-01', '2026-01-31', 8, '0.07', '2026-01-04 15:59:34'),
(32, 56, '2026-01-01', '2026-01-31', 8, '0.07', '2026-01-04 15:59:41'),
(33, 56, '2026-01-01', '2026-01-31', 8, '0.07', '2026-01-04 16:00:07'),
(34, 56, '2026-01-01', '2026-01-31', 8, '0.07', '2026-01-04 16:00:11'),
(35, 56, '2026-01-01', '2026-01-31', 8, '0.07', '2026-01-04 16:00:32'),
(36, 56, '2026-01-01', '2026-01-31', 6, '0.00', '2026-01-04 16:01:50'),
(37, 56, '2026-01-01', '2026-01-31', 6, '0.00', '2026-01-04 16:02:04'),
(38, 56, '2026-01-01', '2026-01-31', 6, '0.00', '2026-01-04 16:05:27'),
(39, 56, '2026-01-01', '2026-01-31', 8, '0.07', '2026-01-04 16:05:42'),
(40, 56, '2026-01-01', '2026-01-31', 8, '0.07', '2026-01-04 16:08:02'),
(41, 56, '2026-01-01', '2026-01-31', 8, '0.07', '2026-01-04 16:19:24'),
(42, 56, '2026-01-01', '2026-01-31', 6, '0.07', '2026-01-04 16:20:51'),
(43, 56, '2026-01-01', '2026-01-31', 6, '0.07', '2026-01-04 16:21:15'),
(44, 56, '2026-01-01', '2026-01-31', 6, '0.07', '2026-01-04 16:40:19'),
(45, 56, '2026-01-01', '2026-01-31', 6, '0.07', '2026-01-04 16:40:36'),
(46, 56, '2026-01-01', '2026-01-31', 6, '0.07', '2026-01-04 17:00:46'),
(47, 56, '2026-01-01', '2026-01-31', 6, '0.07', '2026-01-04 17:12:40'),
(48, 56, '2026-01-01', '2026-01-31', 6, '0.07', '2026-01-04 17:18:42'),
(49, 56, '2026-01-01', '2026-01-31', 6, '0.07', '2026-01-04 20:13:09'),
(50, 59, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(51, 57, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(52, 60, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(53, 61, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(54, 62, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(55, 63, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(56, 64, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(57, 65, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(58, 66, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(59, 67, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(60, 68, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(61, 69, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(62, 70, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(63, 71, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(64, 72, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(65, 73, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(66, 74, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(67, 58, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(68, 75, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(69, 76, '2026-01-01', '2026-01-31', 5, '10.40', '2026-01-04 20:26:53'),
(81, 77, '2026-01-01', '2026-01-31', 0, '0.00', '2026-01-04 20:33:49'),
(82, 79, '2026-01-01', '2026-01-31', 0, '0.00', '2026-01-04 20:36:11'),
(83, 78, '2026-01-01', '2026-01-31', 0, '0.00', '2026-01-04 20:37:41'),
(84, 78, '2026-01-01', '2026-01-31', 0, '0.00', '2026-01-04 20:37:52'),
(85, 77, '2026-01-01', '2026-01-31', 3, '1.25', '2026-01-04 21:01:59'),
(86, 77, '2026-01-01', '2026-01-31', 2, '1.25', '2026-01-04 21:02:27'),
(87, 77, '2026-01-01', '2026-01-31', 2, '1.25', '2026-01-04 21:02:59'),
(88, 77, '2026-01-01', '2026-01-31', 2, '1.25', '2026-01-04 21:03:16'),
(89, 77, '2026-01-01', '2026-01-31', 2, '1.25', '2026-01-04 21:19:42'),
(90, 79, '2026-01-01', '2026-01-31', 5, '3.71', '2026-01-04 21:53:36'),
(91, 78, '2026-01-01', '2026-01-31', 6, '2.28', '2026-01-04 21:57:55'),
(92, 79, '2026-01-01', '2026-01-31', 5, '3.71', '2026-01-04 22:06:30'),
(93, 79, '2026-01-01', '2026-01-31', 5, '3.71', '2026-01-04 22:07:09'),
(94, 79, '2026-01-01', '2026-01-31', 5, '3.71', '2026-01-04 22:12:52'),
(95, 77, '2026-01-01', '2026-01-31', 4, '1.53', '2026-01-04 23:12:28'),
(96, 77, '2026-01-01', '2026-01-31', 4, '1.53', '2026-01-04 23:19:50'),
(97, 79, '2026-01-01', '2026-01-31', 5, '3.71', '2026-01-04 23:24:53');

-- --------------------------------------------------------

--
-- Table structure for table `tasks`
--

CREATE TABLE `tasks` (
  `task_id` int NOT NULL,
  `user_id` int NOT NULL,
  `title` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `deadline` datetime DEFAULT NULL,
  `completed` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `category` varchar(50) COLLATE utf8mb4_general_ci DEFAULT 'Academic'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tasks`
--

INSERT INTO `tasks` (`task_id`, `user_id`, `title`, `description`, `deadline`, `completed`, `created_at`, `category`) VALUES
(1, 77, 'Setup Database', 'Install PostgreSQL dan konfigurasi user', '2026-01-10 00:00:00', 1, '2026-01-04 00:00:00', 'development'),
(2, 77, 'API Authentication', 'Implementasi JWT untuk login', '2026-01-12 00:00:00', 0, '2026-01-04 00:00:00', 'development'),
(3, 77, 'UI Header', 'Buat komponen navbar responsive', '2026-01-08 00:00:00', 1, '2026-01-04 00:00:00', 'development'),
(4, 77, 'Bug Fixing', 'Perbaiki error pada form registrasi', '2026-01-09 00:00:00', 0, '2026-01-04 00:00:00', 'development'),
(5, 77, 'Deployment', 'Push ke server staging', '2026-01-15 00:00:00', 0, '2026-01-04 00:00:00', 'project'),
(6, 78, 'Research Paper', 'Baca 5 jurnal tentang AI', '2026-01-11 00:00:00', 1, '2026-01-04 00:00:00', 'academic'),
(7, 78, 'Data Cleaning', 'Bersihkan dataset mentah', '2026-01-13 00:00:00', 0, '2026-01-04 00:00:00', 'academic'),
(8, 78, 'Math Assignment', 'Kerjakan bab kalkulus', '2026-01-07 00:00:00', 1, '2026-01-04 00:00:00', 'academic'),
(9, 78, 'Essay Draft', 'Buat outline esai sejarah', '2026-01-14 00:00:00', 0, '2026-01-04 00:00:00', 'academic'),
(10, 78, 'Group Meet', 'Diskusi kelompok via Zoom', '2026-01-06 00:00:00', 1, '2026-01-04 00:00:00', 'project'),
(11, 79, 'Wireframing', 'Desain low-fidelity mobile app', '2026-01-10 00:00:00', 0, '2026-01-04 00:00:00', 'project'),
(12, 79, 'Brand Logo', 'Buat 3 alternatif logo', '2026-01-12 00:00:00', 1, '2026-01-04 00:00:00', 'project'),
(13, 79, 'Color Palette', 'Tentukan sistem warna UI', '2026-01-09 00:00:00', 1, '2026-01-04 00:00:00', 'development'),
(14, 79, 'Client Pitch', 'Siapkan slide presentasi', '2026-01-16 00:00:00', 0, '2026-01-04 00:00:00', 'project'),
(15, 79, 'Icon Set', 'Buat custom icon untuk menu', '2026-01-11 00:00:00', 0, '2026-01-04 00:00:00', 'project'),
(16, 80, 'Market Analysis', 'Analisa kompetitor bisnis', '2026-01-20 00:00:00', 1, '2026-01-04 00:00:00', 'project'),
(17, 80, 'SEO Optimization', 'Update meta tags website', '2026-01-22 00:00:00', 0, '2026-01-04 00:00:00', 'development'),
(18, 80, 'Physics Lab', 'Tulis laporan praktikum', '2026-01-18 00:00:00', 1, '2026-01-04 00:00:00', 'academic'),
(19, 80, 'Financial Plan', 'Buat budget tahunan', '2026-01-25 00:00:00', 0, '2026-01-04 00:00:00', 'project'),
(20, 80, 'Reading List', 'Selesaikan buku Clean Code', '2026-01-30 00:00:00', 1, '2026-01-04 00:00:00', 'development'),
(21, 81, 'Bot Scripting', 'Automasi reply chat', '2026-01-05 00:00:00', 1, '2026-01-04 00:00:00', 'development'),
(22, 81, 'Server Monitoring', 'Set up alert grafana', '2026-01-06 00:00:00', 0, '2026-01-04 00:00:00', 'development'),
(23, 81, 'Unit Testing', 'Buat test case dashboard', '2026-01-07 00:00:00', 1, '2026-01-04 00:00:00', 'development'),
(24, 81, 'Documentation', 'Tulis README project', '2026-01-08 00:00:00', 0, '2026-01-04 00:00:00', 'project'),
(25, 81, 'Email Template', 'Desain template newsletter', '2026-01-09 00:00:00', 1, '2026-01-04 00:00:00', 'project'),
(298, 77, 'Deep Learning Research', 'Membaca paper transformer', '2026-01-10 00:00:00', 1, '2026-01-04 20:53:58', 'Academic'),
(299, 78, 'Deep Learning Research', 'Membaca paper transformer', '2026-01-10 23:59:59', 1, '2026-01-04 20:53:58', 'Academic'),
(300, 79, 'Deep Learning Research', 'Membaca paper transformer', '2026-01-10 23:59:59', 1, '2026-01-04 20:53:58', 'Academic'),
(301, 80, 'Deep Learning Research', 'Membaca paper transformer', '2026-01-10 23:59:59', 1, '2026-01-04 20:53:58', 'Academic'),
(302, 77, 'Backend API Refactor', 'Optimasi query database', '2026-01-12 00:00:00', 0, '2026-01-04 20:53:58', 'Development'),
(303, 78, 'Backend API Refactor', 'Optimasi query database', '2026-01-12 18:00:00', 1, '2026-01-04 20:53:58', 'Development'),
(304, 79, 'Backend API Refactor', 'Optimasi query database', '2026-01-12 18:00:00', 1, '2026-01-04 20:53:58', 'Development'),
(305, 80, 'Backend API Refactor', 'Optimasi query database', '2026-01-12 18:00:00', 1, '2026-01-04 20:53:58', 'Development'),
(306, 77, 'UI/UX Dashboard', 'Slicing design ke JavaFX', '2026-01-15 12:00:00', 0, '2026-01-04 20:53:58', 'Project'),
(307, 78, 'UI/UX Dashboard', 'Slicing design ke JavaFX', '2026-01-15 12:00:00', 0, '2026-01-04 20:53:58', 'Project'),
(308, 79, 'UI/UX Dashboard', 'Slicing design ke JavaFX', '2026-01-15 12:00:00', 0, '2026-01-04 20:53:58', 'Project'),
(309, 80, 'UI/UX Dashboard', 'Slicing design ke JavaFX', '2026-01-15 12:00:00', 0, '2026-01-04 20:53:58', 'Project'),
(310, 77, 'Database Migration', 'Setup relasi tabel baru', '2026-01-08 09:00:00', 1, '2026-01-04 20:53:58', 'Development'),
(311, 78, 'Database Migration', 'Setup relasi tabel baru', '2026-01-08 09:00:00', 1, '2026-01-04 20:53:58', 'Development'),
(312, 79, 'Database Migration', 'Setup relasi tabel baru', '2026-01-08 09:00:00', 1, '2026-01-04 20:53:58', 'Development'),
(313, 80, 'Database Migration', 'Setup relasi tabel baru', '2026-01-08 09:00:00', 1, '2026-01-04 20:53:58', 'Development'),
(314, 77, 'Mathematics Assignment', 'Problem set kalkulus 3', '2026-01-06 23:59:59', 0, '2026-01-04 20:53:58', 'Academic'),
(315, 78, 'Mathematics Assignment', 'Problem set kalkulus 3', '2026-01-06 23:59:59', 0, '2026-01-04 20:53:58', 'Academic'),
(316, 79, 'Mathematics Assignment', 'Problem set kalkulus 3', '2026-01-06 23:59:59', 0, '2026-01-04 20:53:58', 'Academic'),
(317, 80, 'Mathematics Assignment', 'Problem set kalkulus 3', '2026-01-06 23:59:59', 0, '2026-01-04 20:53:58', 'Academic'),
(329, 81, 'Research AI Trends', 'Reading 10 papers', '2026-01-15 00:00:00', 1, '2026-01-04 21:08:27', 'Academic'),
(330, 82, 'Fix Navigation Bug', 'Fixing sidebar lag', '2026-01-10 00:00:00', 0, '2026-01-04 21:08:27', 'Development'),
(331, 83, 'Product Launch Plan', 'Marketing strategy', '2026-01-20 00:00:00', 1, '2026-01-04 21:08:27', 'Project'),
(332, 84, 'Database Indexing', 'Speed up queries', '2026-01-11 00:00:00', 1, '2026-01-04 21:08:27', 'Development'),
(333, 85, 'Physics Lab Report', 'Calculating velocity', '2026-01-07 00:00:00', 0, '2026-01-04 21:08:27', 'Academic'),
(334, 86, 'UI Kit Construction', 'Create button components', '2026-01-18 00:00:00', 1, '2026-01-04 21:08:27', 'Project');

-- --------------------------------------------------------

--
-- Table structure for table `tracking_productivity`
--

CREATE TABLE `tracking_productivity` (
  `tracking_id` int NOT NULL,
  `user_id` int NOT NULL,
  `task_id` int NOT NULL,
  `total_sessions` int DEFAULT '0',
  `total_focus_hours` double DEFAULT '0',
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tracking_productivity`
--

INSERT INTO `tracking_productivity` (`tracking_id`, `user_id`, `task_id`, `total_sessions`, `total_focus_hours`, `last_updated`) VALUES
(130, 77, 298, 3, 1.25, '2026-01-04 14:03:13'),
(131, 78, 299, 3, 1.25, '2026-01-04 13:53:58'),
(132, 79, 300, 3, 1.25, '2026-01-04 13:53:58'),
(133, 80, 301, 3, 1.25, '2026-01-04 13:53:58'),
(134, 77, 302, 3, 1.25, '2026-01-04 13:53:58'),
(135, 78, 303, 3, 1.25, '2026-01-04 13:53:58'),
(136, 79, 304, 3, 1.25, '2026-01-04 13:53:58'),
(137, 80, 305, 3, 1.25, '2026-01-04 13:53:58'),
(138, 77, 310, 3, 1.25, '2026-01-04 13:53:58'),
(139, 78, 311, 3, 1.25, '2026-01-04 13:53:58'),
(140, 79, 312, 3, 1.25, '2026-01-04 13:53:58'),
(141, 80, 313, 3, 1.25, '2026-01-04 13:53:58'),
(142, 77, 1, 4, 1.6, '2026-01-04 03:00:00'),
(143, 77, 2, 2, 1.3, '2026-01-04 04:45:00'),
(144, 77, 3, 5, 2, '2026-01-04 08:00:00'),
(145, 77, 4, 3, 1.5, '2026-01-04 09:35:00'),
(146, 77, 5, 6, 3, '2026-01-04 13:30:00'),
(147, 78, 6, 3, 3, '2026-01-04 03:10:00'),
(148, 78, 7, 7, 2.3, '2026-01-04 05:00:00'),
(149, 78, 8, 4, 2.3, '2026-01-04 07:40:00'),
(150, 78, 9, 8, 7.3, '2026-01-04 10:00:00'),
(151, 78, 10, 10, 4.6, '2026-01-04 14:15:00'),
(152, 79, 11, 5, 2.6, '2026-01-04 02:45:00'),
(153, 79, 12, 12, 4.4, '2026-01-04 04:20:00'),
(154, 79, 13, 13, 10.4, '2026-01-04 06:30:00'),
(155, 79, 14, 1, 1.5, '2026-01-04 09:00:00'),
(156, 79, 15, 9, 2.2, '2026-01-04 10:45:00'),
(157, 80, 16, 16, 6.9, '2026-01-04 02:00:00'),
(158, 80, 17, 17, 10.7, '2026-01-04 04:00:00'),
(159, 80, 18, 2, 1.4, '2026-01-04 06:00:00'),
(160, 80, 19, 19, 16.4, '2026-01-04 09:00:00'),
(161, 80, 20, 20, 19.3, '2026-01-04 12:00:00'),
(162, 81, 21, 21, 9.4, '2026-01-04 01:00:00'),
(163, 81, 22, 22, 12.1, '2026-01-04 03:00:00'),
(164, 81, 23, 23, 15.7, '2026-01-04 05:00:00'),
(165, 81, 24, 24, 18.8, '2026-01-04 08:00:00'),
(166, 81, 25, 25, 22, '2026-01-04 10:00:00'),
(167, 82, 26, 26, 26.4, '2026-01-04 02:35:00'),
(168, 82, 27, 27, 27.9, '2026-01-04 04:36:00'),
(169, 82, 28, 28, 29.4, '2026-01-04 06:37:00'),
(170, 82, 29, 29, 30.9, '2026-01-04 08:38:00'),
(171, 82, 30, 30, 32.5, '2026-01-04 10:39:00'),
(172, 83, 31, 31, 34.1, '2026-01-04 02:40:00'),
(173, 83, 32, 32, 35.7, '2026-01-04 04:41:00'),
(174, 83, 33, 33, 37.4, '2026-01-04 06:42:00'),
(175, 83, 34, 34, 39.1, '2026-01-04 08:43:00'),
(176, 83, 35, 35, 40.8, '2026-01-04 10:44:00'),
(177, 84, 36, 36, 42.6, '2026-01-04 02:45:00'),
(178, 84, 37, 37, 44.4, '2026-01-04 04:46:00'),
(179, 84, 38, 38, 46.2, '2026-01-04 06:47:00'),
(180, 84, 39, 39, 48.1, '2026-01-04 08:48:00'),
(181, 84, 40, 40, 50, '2026-01-04 10:49:00'),
(182, 85, 41, 41, 51.9, '2026-01-04 02:50:00'),
(183, 85, 42, 42, 53.9, '2026-01-04 04:51:00'),
(184, 85, 43, 43, 55.9, '2026-01-04 06:52:00'),
(185, 85, 44, 44, 57.9, '2026-01-04 08:53:00'),
(186, 85, 45, 45, 60, '2026-01-04 10:54:00'),
(187, 86, 46, 46, 62.1, '2026-01-04 02:55:00'),
(188, 86, 47, 47, 64.2, '2026-01-04 04:56:00'),
(189, 86, 48, 48, 66.4, '2026-01-04 06:57:00'),
(190, 86, 49, 49, 68.6, '2026-01-04 08:58:00'),
(191, 86, 50, 50, 70.8, '2026-01-04 10:59:00');

-- --------------------------------------------------------

--
-- Table structure for table `tracking_productivity_backup`
--

CREATE TABLE `tracking_productivity_backup` (
  `tracking_id` int NOT NULL DEFAULT '0',
  `user_id` int NOT NULL,
  `task_id` int DEFAULT NULL,
  `total_sessions` int DEFAULT '0',
  `total_focus_hours` decimal(10,2) DEFAULT '0.00',
  `last_updated` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tracking_productivity_backup`
--

INSERT INTO `tracking_productivity_backup` (`tracking_id`, `user_id`, `task_id`, `total_sessions`, `total_focus_hours`, `last_updated`) VALUES
(337, 56, 169, 6, '0.12', '2026-01-04 15:39:33');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_login` datetime DEFAULT NULL,
  `role` varchar(50) COLLATE utf8mb4_general_ci DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `email`, `created_at`, `last_login`, `role`) VALUES
(77, 'Haidar', 'BqE9lIBZfyywYyJQjSF/uE515/9KmSQ8HHAeymrsKec=', '1@gmail.com', '2026-01-04 20:32:24', '2026-01-04 23:19:31', 'user'),
(78, 'Nafira', 'B3YcOHaTQ3xqmhqpqRRVqNV4KiJc6TYGUdNWOjnWp8M=', '2.com', '2026-01-04 20:32:48', '2026-01-04 22:55:36', 'user'),
(79, 'sekar', '0XRoW+Me6ouCEfiq1p2zFPlX1M2RYET7N//mHUI9lg4=', '3.com', '2026-01-04 20:33:05', '2026-01-04 23:38:45', 'user'),
(80, 'zaldi', 'DVhLaZCHyyGXZB/fmxFr4LAFMkbx1PWnqIaN9qovLJM=', '4.com', '2026-01-04 20:33:23', NULL, 'user'),
(81, 'bot1', 'WZRHGrsBESr8wYFZ9sx0tPURuZgG2lmzyvWpwXPKz8U=', '3', '2026-01-04 21:04:55', '2026-01-04 23:42:21', 'admin'),
(82, 'bot2', 'WZRHGrsBESr8wYFZ9sx0tPURuZgG2lmzyvWpwXPKz8U=', '2', '2026-01-04 21:05:14', NULL, 'user'),
(83, 'bot3', 'WZRHGrsBESr8wYFZ9sx0tPURuZgG2lmzyvWpwXPKz8U=', '5', '2026-01-04 21:05:25', '2026-01-04 21:13:23', 'user'),
(84, 'bot4', 'WZRHGrsBESr8wYFZ9sx0tPURuZgG2lmzyvWpwXPKz8U=', '1', '2026-01-04 21:05:37', NULL, 'user'),
(85, 'bot5', 'WZRHGrsBESr8wYFZ9sx0tPURuZgG2lmzyvWpwXPKz8U=', '8', '2026-01-04 21:05:50', NULL, 'user'),
(86, 'bot6', 'WZRHGrsBESr8wYFZ9sx0tPURuZgG2lmzyvWpwXPKz8U=', '6', '2026-01-04 21:06:04', NULL, 'user');

-- --------------------------------------------------------

--
-- Table structure for table `user_missions`
--

CREATE TABLE `user_missions` (
  `user_mission_id` int NOT NULL,
  `user_id` int NOT NULL,
  `id_mission` int DEFAULT NULL,
  `mission_name` varchar(255) NOT NULL,
  `description` text,
  `points_reward` int DEFAULT '0',
  `status` enum('pending','completed') DEFAULT 'pending',
  `completed_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `user_missions`
--

INSERT INTO `user_missions` (`user_mission_id`, `user_id`, `id_mission`, `mission_name`, `description`, `points_reward`, `status`, `completed_at`) VALUES
(1, 77, NULL, 'First Focus', 'Selesaikan sesi Pomodoro pertama Anda', 50, 'completed', NULL),
(2, 77, NULL, 'Consistency King', 'Selesaikan 5 task dalam satu hari', 100, 'pending', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `pomodoro_sessions`
--
ALTER TABLE `pomodoro_sessions`
  ADD PRIMARY KEY (`session_id`);

--
-- Indexes for table `report`
--
ALTER TABLE `report`
  ADD PRIMARY KEY (`report_id`);

--
-- Indexes for table `tasks`
--
ALTER TABLE `tasks`
  ADD PRIMARY KEY (`task_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `idx_tasks_category` (`category`),
  ADD KEY `idx_tasks_user_category` (`user_id`,`category`);

--
-- Indexes for table `tracking_productivity`
--
ALTER TABLE `tracking_productivity`
  ADD PRIMARY KEY (`tracking_id`),
  ADD UNIQUE KEY `unique_user_task` (`user_id`,`task_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_missions`
--
ALTER TABLE `user_missions`
  ADD PRIMARY KEY (`user_mission_id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `pomodoro_sessions`
--
ALTER TABLE `pomodoro_sessions`
  MODIFY `session_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=213;

--
-- AUTO_INCREMENT for table `report`
--
ALTER TABLE `report`
  MODIFY `report_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=98;

--
-- AUTO_INCREMENT for table `tasks`
--
ALTER TABLE `tasks`
  MODIFY `task_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=335;

--
-- AUTO_INCREMENT for table `tracking_productivity`
--
ALTER TABLE `tracking_productivity`
  MODIFY `tracking_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=192;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=87;

--
-- AUTO_INCREMENT for table `user_missions`
--
ALTER TABLE `user_missions`
  MODIFY `user_mission_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tasks`
--
ALTER TABLE `tasks`
  ADD CONSTRAINT `tasks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `tracking_productivity`
--
ALTER TABLE `tracking_productivity`
  ADD CONSTRAINT `tracking_productivity_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `user_missions`
--
ALTER TABLE `user_missions`
  ADD CONSTRAINT `user_missions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
