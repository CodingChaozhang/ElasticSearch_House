/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50721
Source Host           : localhost:3306
Source Database       : house

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2018-06-22 21:11:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `role`
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL COMMENT '用户id',
  `name` varchar(32) NOT NULL COMMENT '用户角色名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_and_name` (`user_id`,`name`) USING BTREE,
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='用户角色表';

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '1', 'USER');
INSERT INTO `role` VALUES ('2', '2', 'ADMIN');
INSERT INTO `role` VALUES ('3', '3', 'USER');
INSERT INTO `role` VALUES ('4', '4', 'USER');
INSERT INTO `role` VALUES ('5', '5', 'USER');
INSERT INTO `role` VALUES ('6', '6', 'USER');
INSERT INTO `role` VALUES ('7', '7', 'USER');
INSERT INTO `role` VALUES ('8', '8', 'USER');
