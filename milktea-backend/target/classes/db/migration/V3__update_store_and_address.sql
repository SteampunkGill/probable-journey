-- 确保 stores 表包含省市县和经纬度字段（initial.sql 已包含，此处为增量同步确保）
ALTER TABLE `stores` MODIFY COLUMN `province` VARCHAR(50) DEFAULT NULL COMMENT '省份';
ALTER TABLE `stores` MODIFY COLUMN `city` VARCHAR(50) DEFAULT NULL COMMENT '城市';
ALTER TABLE `stores` MODIFY COLUMN `district` VARCHAR(50) DEFAULT NULL COMMENT '区县';
ALTER TABLE `stores` MODIFY COLUMN `latitude` DECIMAL(10, 8) DEFAULT 0 COMMENT '纬度';
ALTER TABLE `stores` MODIFY COLUMN `longitude` DECIMAL(11, 8) DEFAULT 0 COMMENT '经度';

-- 确保 user_addresses 表包含省市县和经纬度字段
ALTER TABLE `user_addresses` MODIFY COLUMN `province` VARCHAR(50) NOT NULL COMMENT '省份';
ALTER TABLE `user_addresses` MODIFY COLUMN `city` VARCHAR(50) NOT NULL COMMENT '城市';
ALTER TABLE `user_addresses` MODIFY COLUMN `district` VARCHAR(50) NOT NULL COMMENT '区县';
ALTER TABLE `user_addresses` MODIFY COLUMN `latitude` DECIMAL(10, 7) DEFAULT NULL COMMENT '纬度';
ALTER TABLE `user_addresses` MODIFY COLUMN `longitude` DECIMAL(10, 7) DEFAULT NULL COMMENT '经度';