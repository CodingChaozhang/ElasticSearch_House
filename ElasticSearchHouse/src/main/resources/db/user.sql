/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50721
Source Host           : localhost:3306
Source Database       : house

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2018-06-22 21:11:28
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户唯一id',
  `name` varchar(32) DEFAULT NULL COMMENT '用户名',
  `email` varchar(32) DEFAULT NULL COMMENT '电子邮箱',
  `phone_number` varchar(15) NOT NULL COMMENT '电话号码',
  `password` varchar(32) DEFAULT NULL COMMENT '密码',
  `status` int(2) unsigned NOT NULL DEFAULT '0' COMMENT '用户状态 0-正常 1-封禁',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户账号创建时间',
  `last_login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上次登录时间',
  `last_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '上次更新记录时间',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_on_phone` (`phone_number`) USING BTREE COMMENT '用户手机号',
  UNIQUE KEY `index_on_username` (`name`) USING BTREE COMMENT '用户名索引',
  UNIQUE KEY `index_on_email` (`email`) USING BTREE COMMENT '电子邮箱索引'
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='用户基本信息表';

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'waliwali', 'wali@wali.com', '15111111111', '6fd1aad88b038aeecd9adeccc92b0bd1', '1', '2017-08-25 15:18:20', '2017-08-25 12:00:00', '2017-11-26 10:29:02', 'http://pav17qjlw.bkt.clouddn.com/99ff568bd61c744bf31185aeddf13580.png');
INSERT INTO `user` VALUES ('2', 'admin', 'admin@imooc.com', '1388888888', '55b3d0936a3fb63168d57a6bda0ddbbf', '1', '2017-08-27 09:07:05', '2017-08-27 09:07:07', '2017-10-21 15:03:57', 'http://pav17qjlw.bkt.clouddn.com/99ff568bd61c744bf31185aeddf13580.png');
INSERT INTO `user` VALUES ('5', '138****8888', null, '13888888888', null, '0', '2017-11-25 17:56:45', '2017-11-25 17:56:45', '2017-11-25 17:56:45', null);
INSERT INTO `user` VALUES ('8', '151****9677', null, '15110059677', null, '0', '2017-11-25 18:58:18', '2017-11-25 18:58:18', '2017-11-25 18:58:18', null);
