-- 修复 banners 表字段，确保与实体类 Banner.java 一致
-- 1. 修复 banners 表字段，解决 'created_at' 无默认值导致的保存失败
ALTER TABLE `banners` 
MODIFY COLUMN `is_active` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否启用',
MODIFY COLUMN `click_count` int DEFAULT 0 COMMENT '点击次数',
MODIFY COLUMN `sort` int DEFAULT 0 COMMENT '排序权重',
MODIFY COLUMN `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
MODIFY COLUMN `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
MODIFY COLUMN `type` varchar(20) DEFAULT 'BANNER' COMMENT '类型';

-- 2. 补充业务逻辑字段（如果缺失）
ALTER TABLE `banners`
ADD COLUMN IF NOT EXISTS `link_type` varchar(20) DEFAULT 'NONE' COMMENT '跳转类型: PRODUCT, URL, NONE',
ADD COLUMN IF NOT EXISTS `link_value` varchar(255) DEFAULT NULL COMMENT '跳转目标值',
ADD COLUMN IF NOT EXISTS `position` varchar(20) DEFAULT 'HOME' COMMENT '显示位置: HOME',
ADD COLUMN IF NOT EXISTS `start_time` datetime(6) DEFAULT NULL COMMENT '开始时间',
ADD COLUMN IF NOT EXISTS `end_time` datetime(6) DEFAULT NULL COMMENT '结束时间';

-- 3. 修复其他可能存在的重复创建表问题（幂等性处理）
-- 针对反馈中提到的 Table already exists 错误，确保所有迁移脚本都是幂等的

-- 用户收藏表
CREATE TABLE IF NOT EXISTS `user_favorite_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_user_product_favorite_new` (`user_id`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单评价表
CREATE TABLE IF NOT EXISTS `order_review` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `store_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `product_rating` INT NOT NULL DEFAULT 5,
    `delivery_rating` INT NOT NULL DEFAULT 5,
    `content` TEXT,
    `images` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单申诉表
CREATE TABLE IF NOT EXISTS `order_appeal` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `order_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `reason` VARCHAR(100) NOT NULL,
    `description` TEXT NOT NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `reply` TEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统公告表
CREATE TABLE IF NOT EXISTS `sys_announcements` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NOT NULL,
    `type` VARCHAR(20) DEFAULT 'SYSTEM',
    `is_active` BIT(1) DEFAULT b'1',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;