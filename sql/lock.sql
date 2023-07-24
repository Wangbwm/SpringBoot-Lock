/*
 Navicat Premium Data Transfer

 Source Server         : MySQL_192.168.198.100
 Source Server Type    : MySQL
 Source Server Version : 50742
 Source Host           : 192.168.198.100:3306
 Source Schema         : lock

 Target Server Type    : MySQL
 Target Server Version : 50742
 File Encoding         : 65001

 Date: 24/07/2023 17:37:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for distributed_locks
-- ----------------------------
DROP TABLE IF EXISTS `distributed_locks`;
CREATE TABLE `distributed_locks`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lock_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `version` int(11) NULL DEFAULT NULL,
  `locked` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of distributed_locks
-- ----------------------------
INSERT INTO `distributed_locks` VALUES (1, 'test', 1, 0);

SET FOREIGN_KEY_CHECKS = 1;
