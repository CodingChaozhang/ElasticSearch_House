DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT(11) NOT NULL IDENTITY PRIMARY KEY ,
  `name` VARCHAR(32) NOT NULL UNIQUE ,
  `password` VARCHAR(32) NOT NULL ,
  `email` VARCHAR(32) NOT NULL UNIQUE ,
  `phone_number` VARCHAR(15) NOT NULL UNIQUE ,
  `status` INT(2) NOT NULL ,
  `avatar` VARCHAR(255),
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `last_login_time` DATETIME NOT NULL ,
  `last_update_time` DATETIME NOT NULL DEFAULT NOW()
);

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` INT(11) NOT NULL IDENTITY PRIMARY KEY ,
  `user_id` INT(11) NOT NULL,
  `name` VARCHAR(32) NOT NULL,
  UNIQUE (`user_id`, `name`)
);

CREATE INDEX ON `role`(`user_id`);

DROP TABLE IF EXISTS `support_address`;
CREATE TABLE `support_address` (
  `id` int(11) NOT NULL IDENTITY,
  `belong_to` VARCHAR(32) NOT NULL,
  `en_name` varchar(32) NOT NULL,
  `cn_name` varchar(32) NOT NULL,
  `level` varchar(16) NOT NULL,
  `baidu_map_lng` DOUBLE NOT NULL ,
  `baidu_map_lat` DOUBLE NOT NULL ,
  PRIMARY KEY (`id`)
);

CREATE INDEX ON `support_address` (`belong_to`,`en_name`,`level`);

DROP TABLE IF EXISTS `house`;
CREATE TABLE `house` (
  `id` int(11)  NOT NULL IDENTITY COMMENT 'house唯一标识',
  `title` varchar(32) NOT NULL,
  `price` int(11) NOT NULL COMMENT '价格',
  `area` int(11) NOT NULL COMMENT '面积',
  `room` int(11) NOT NULL COMMENT '卧室数量',
  `floor` int(11) NOT NULL COMMENT '楼层',
  `total_floor` int(11) NOT NULL COMMENT '总楼层',
  `watch_times` int(11) DEFAULT '0' COMMENT '被看次数',
  `build_year` int(4) NOT NULL COMMENT '建立年限',
  `status` int(4) NOT NULL DEFAULT '0' COMMENT '房屋状态 0-未审核 1-审核通过 2-已出租 3-逻辑删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `city_en_name` varchar(32) NOT NULL COMMENT '城市标记缩写 如 北京bj',
  `region_en_name` varchar(32) NOT NULL COMMENT '区域标记缩写 如 朝阳区cyq',
  `cover` varchar(32) DEFAULT NULL COMMENT '封面',
  `direction` int(11) NOT NULL COMMENT '朝向',
  `distance_to_subway` int(11) NOT NULL DEFAULT '-1' COMMENT '距地铁距离 默认-1 附近无地铁',
  `parlour` int(11) NOT NULL DEFAULT '0' COMMENT '客厅数量',
  `district` varchar(32) NOT NULL COMMENT '所在小区',
  `admin_id` int(11) NOT NULL COMMENT '所属管理员id',
  `bathroom` int(11) NOT NULL DEFAULT '0' COMMENT '卫生间数量',
  `street` varchar(32) NOT NULL COMMENT '街道',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `house_detail`;
CREATE TABLE `house_detail` (
  `id` int(11) NOT NULL IDENTITY,
  `description` varchar(255) DEFAULT NULL COMMENT '详细描述',
  `layout_desc` varchar(255) DEFAULT NULL COMMENT '户型介绍',
  `traffic` varchar(255) DEFAULT NULL COMMENT '交通出行',
  `round_service` varchar(255) DEFAULT NULL COMMENT '周边配套',
  `rent_way` int(2) NOT NULL COMMENT '租赁方式',
  `address` varchar(32) NOT NULL COMMENT '详细地址 ',
  `subway_line_id` int(11) DEFAULT NULL COMMENT '附近地铁线id',
  `subway_line_name` varchar(32) DEFAULT NULL COMMENT '附近地铁线名称',
  `subway_station_id` int(11) DEFAULT NULL COMMENT '地铁站id',
  `subway_station_name` varchar(32) DEFAULT NULL COMMENT '地铁站名',
  `house_id` int(11) NOT NULL COMMENT '对应house的id',
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `house_tag`;
CREATE TABLE `house_tag` (
  `house_id` int(11) NOT NULL COMMENT '房屋id',
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
);