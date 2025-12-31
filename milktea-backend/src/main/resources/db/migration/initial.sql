-- 自动生成的数据库导出脚本
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for about_us
-- ----------------------------
DROP TABLE IF EXISTS `about_us`;
CREATE TABLE `about_us` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(20) DEFAULT NULL,
  `content` text,
  `latitude` double DEFAULT NULL,
  `logo_url` varchar(255) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for allergens
-- ----------------------------
DROP TABLE IF EXISTS `allergens`;
CREATE TABLE `allergens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(50) NOT NULL,
  `icon_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1tipxa3jfm7x5gcbqlmn6h7on` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for banners
-- ----------------------------
DROP TABLE IF EXISTS `banners`;
CREATE TABLE `banners` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `background_color` varchar(20) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `image_url` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否启用',
  `sort_order` int DEFAULT NULL,
  `subtitle` varchar(100) DEFAULT NULL,
  `target_id` varchar(50) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `type` varchar(20) DEFAULT 'BANNER' COMMENT '类型',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  `url` varchar(255) DEFAULT NULL,
  `link_type` varchar(20) DEFAULT NULL,
  `link_value` varchar(255) DEFAULT NULL,
  `position` varchar(20) DEFAULT NULL,
  `sort` int DEFAULT '0' COMMENT '排序权重',
  `click_count` int DEFAULT '0' COMMENT '点击次数',
  `end_time` datetime(6) DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of banners
-- ----------------------------
INSERT INTO `banners` (`id`, `background_color`, `created_at`, `image_url`, `is_active`, `sort_order`, `subtitle`, `target_id`, `title`, `type`, `updated_at`, `url`, `link_type`, `link_value`, `position`, `sort`, `click_count`, `end_time`, `start_time`) VALUES (4, NULL, '0000-00-00 00:00:00.000000', 'http://localhost:8081/uploads/1767103782514_77844436.jpg', 'b''\x01''', NULL, NULL, NULL, NULL, '', '2025-12-30 22:09:43.390619', NULL, 'PRODUCT', '1', 'HOME', 1, 0, NULL, NULL);
INSERT INTO `banners` (`id`, `background_color`, `created_at`, `image_url`, `is_active`, `sort_order`, `subtitle`, `target_id`, `title`, `type`, `updated_at`, `url`, `link_type`, `link_value`, `position`, `sort`, `click_count`, `end_time`, `start_time`) VALUES (5, NULL, '0000-00-00 00:00:00.000000', 'http://localhost:8081/uploads/1767103789345_a8e0d375.jpg', 'b''\x01''', NULL, NULL, NULL, NULL, '', '2025-12-30 22:09:49.932402', NULL, 'NONE', 'https://example.com/promotion', 'HOME', 2, 0, NULL, NULL);
INSERT INTO `banners` (`id`, `background_color`, `created_at`, `image_url`, `is_active`, `sort_order`, `subtitle`, `target_id`, `title`, `type`, `updated_at`, `url`, `link_type`, `link_value`, `position`, `sort`, `click_count`, `end_time`, `start_time`) VALUES (6, NULL, '0000-00-00 00:00:00.000000', 'http://localhost:8081/uploads/1767103795440_bd1ec20a.webp', 'b''\x01''', NULL, NULL, NULL, NULL, '', '2025-12-30 22:09:55.883014', NULL, 'NONE', NULL, 'HOME', 3, 0, NULL, NULL);
INSERT INTO `banners` (`id`, `background_color`, `created_at`, `image_url`, `is_active`, `sort_order`, `subtitle`, `target_id`, `title`, `type`, `updated_at`, `url`, `link_type`, `link_value`, `position`, `sort`, `click_count`, `end_time`, `start_time`) VALUES (7, NULL, '2025-12-28 16:43:22.372064', 'http://localhost:8081/uploads/1767103802174_8790e49f.jpg', 'b''\x01''', NULL, NULL, NULL, NULL, 'BANNER', '2025-12-30 22:10:03.042981', NULL, 'NONE', '', 'HOME', 0, 0, NULL, NULL);

-- ----------------------------
-- Table structure for cart_item_customizations
-- ----------------------------
DROP TABLE IF EXISTS `cart_item_customizations`;
CREATE TABLE `cart_item_customizations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `customization_type_name` varchar(50) NOT NULL,
  `option_label` varchar(50) NOT NULL,
  `option_value` varchar(50) NOT NULL,
  `price_adjustment_at_add` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL,
  `cart_item_id` bigint NOT NULL,
  `option_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr6pnoso87b0qdm4ajaf29mgjd` (`cart_item_id`),
  KEY `FKnxofeju7c4edwdi0dcgsqhl39` (`option_id`),
  CONSTRAINT `FKnxofeju7c4edwdi0dcgsqhl39` FOREIGN KEY (`option_id`) REFERENCES `product_options` (`id`),
  CONSTRAINT `FKr6pnoso87b0qdm4ajaf29mgjd` FOREIGN KEY (`cart_item_id`) REFERENCES `cart_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for cart_items
-- ----------------------------
DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `invalid_reason` varchar(255) DEFAULT NULL,
  `is_selected` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否选中',
  `is_valid` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否有效',
  `original_price_at_add` decimal(38,2) DEFAULT NULL,
  `price_at_add` decimal(10,2) NOT NULL DEFAULT '0.00',
  `quantity` int NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `store_id` bigint DEFAULT NULL COMMENT '门店ID',
  `sweetness` varchar(255) DEFAULT NULL,
  `temperature` varchar(255) DEFAULT NULL,
  `spec_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1vhvont0fdtramle6nmghntj7` (`user_id`,`product_id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  KEY `FKaydhc1g0rvhgg36t1irinj6nt` (`spec_id`),
  KEY `FK5hdx4t879kwd8w87xkhs9c6dq` (`store_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK5hdx4t879kwd8w87xkhs9c6dq` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`),
  CONSTRAINT `FK709eickf3kc0dujx3ub9i7btf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKaydhc1g0rvhgg36t1irinj6nt` FOREIGN KEY (`spec_id`) REFERENCES `product_specs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of cart_items
-- ----------------------------
INSERT INTO `cart_items` (`id`, `created_at`, `invalid_reason`, `is_selected`, `is_valid`, `original_price_at_add`, `price_at_add`, `quantity`, `updated_at`, `product_id`, `user_id`, `store_id`, `sweetness`, `temperature`, `spec_id`) VALUES (50, '2025-12-31 07:48:33.045570', NULL, 'b''\x01''', 'b''\x01''', NULL, '15.00', 2, '2025-12-31 07:48:46.588256', 6, 1, 2, NULL, NULL, NULL);
INSERT INTO `cart_items` (`id`, `created_at`, `invalid_reason`, `is_selected`, `is_valid`, `original_price_at_add`, `price_at_add`, `quantity`, `updated_at`, `product_id`, `user_id`, `store_id`, `sweetness`, `temperature`, `spec_id`) VALUES (51, '2025-12-31 07:48:49.556813', NULL, 'b''\x01''', 'b''\x01''', NULL, '18.00', 1, '2025-12-31 07:48:49.557815', 10, 1, 2, NULL, NULL, NULL);
INSERT INTO `cart_items` (`id`, `created_at`, `invalid_reason`, `is_selected`, `is_valid`, `original_price_at_add`, `price_at_add`, `quantity`, `updated_at`, `product_id`, `user_id`, `store_id`, `sweetness`, `temperature`, `spec_id`) VALUES (52, '2025-12-31 07:48:53.320900', NULL, 'b''\x01''', 'b''\x01''', NULL, '28.00', 1, '2025-12-31 07:48:53.320900', 14, 1, 2, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `icon_url` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `name` varchar(50) NOT NULL,
  `sort_order` int DEFAULT '0',
  `updated_at` datetime(6) NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsaok720gsu4u2wrgbk10b5n8d` (`parent_id`),
  CONSTRAINT `FKsaok720gsu4u2wrgbk10b5n8d` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (1, '2025-12-26 20:04:01', NULL, '/images/categories/hot.png', NULL, 0, '人气Top', 1, '2025-12-26 20:04:01', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (2, '2025-12-26 20:04:01', NULL, '/images/categories/milk-tea.png', NULL, 0, '经典奶茶', 2, '2025-12-26 20:04:01', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (3, '2025-12-26 20:04:01', NULL, '/images/categories/fruit-tea.png', NULL, 0, '清爽果茶', 3, '2025-12-26 20:04:01', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (4, '2025-12-26 20:04:01', NULL, '/images/categories/coffee.png', NULL, 0, '现磨咖啡', 4, '2025-12-26 20:04:01', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (5, '2025-12-26 20:04:01', NULL, '/images/categories/snack.png', NULL, 0, '精选小食', 5, '2025-12-26 20:04:01', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (6, '2025-12-26 20:04:01', NULL, '/images/categories/hot-drink.png', NULL, 0, '热饮系列', 6, '2025-12-26 20:04:01', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (7, '2025-12-26 20:04:01', NULL, '/images/categories/low-sugar.png', NULL, 0, '低糖系列', 7, '2025-12-26 20:04:01', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (8, '2025-12-26 20:04:01', NULL, '/images/categories/seasonal.png', NULL, 0, '季节限定', 8, '2025-12-26 20:04:01', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (9, '2025-12-26 20:04:01', NULL, '/images/categories/ice-cream.png', NULL, 0, '冰淇淋', 9, '2025-12-26 20:04:01', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (10, '2025-12-26 20:20:57', NULL, '/images/categories/hot.png', NULL, 0, '人气Top', 1, '2025-12-26 20:20:57', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (11, '2025-12-26 20:20:57', NULL, '/images/categories/milk-tea.png', NULL, 0, '经典奶茶', 2, '2025-12-26 20:20:57', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (12, '2025-12-26 20:20:57', NULL, '/images/categories/fruit-tea.png', NULL, 0, '清爽果茶', 3, '2025-12-26 20:20:57', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (13, '2025-12-26 20:20:57', NULL, '/images/categories/coffee.png', NULL, 0, '现磨咖啡', 4, '2025-12-26 20:20:57', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (14, '2025-12-26 20:20:57', NULL, '/images/categories/snack.png', NULL, 0, '精选小食', 5, '2025-12-26 20:20:57', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (15, '2025-12-26 20:20:57', NULL, '/images/categories/hot-drink.png', NULL, 0, '热饮系列', 6, '2025-12-26 20:20:57', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (16, '2025-12-26 20:20:57', NULL, '/images/categories/low-sugar.png', NULL, 0, '低糖系列', 7, '2025-12-26 20:20:57', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (17, '2025-12-26 20:20:57', NULL, '/images/categories/seasonal.png', NULL, 0, '季节限定', 8, '2025-12-26 20:20:57', NULL);
INSERT INTO `categories` (`id`, `created_at`, `description`, `icon_url`, `image_url`, `is_active`, `name`, `sort_order`, `updated_at`, `parent_id`) VALUES (18, '2025-12-26 20:20:57', NULL, '/images/categories/ice-cream.png', NULL, 0, '冰淇淋', 9, '2025-12-26 20:20:57', NULL);

-- ----------------------------
-- Table structure for complaints
-- ----------------------------
DROP TABLE IF EXISTS `complaints`;
CREATE TABLE `complaints` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `compensation` decimal(10,2) DEFAULT NULL,
  `compensation_coupon_id` bigint DEFAULT NULL,
  `compensation_type` varchar(20) DEFAULT NULL,
  `content` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `images_json` json DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `reply` text,
  `status` varchar(20) NOT NULL,
  `type` varchar(50) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `solution` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr32fabkp363nyst1byc5txf9u` (`order_id`),
  KEY `FK83j5gqkd7ku4vc908g4rtmglr` (`user_id`),
  CONSTRAINT `FK83j5gqkd7ku4vc908g4rtmglr` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKr32fabkp363nyst1byc5txf9u` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for coupon_templates
-- ----------------------------
DROP TABLE IF EXISTS `coupon_templates`;
CREATE TABLE `coupon_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `acquire_limit` int NOT NULL DEFAULT '1' COMMENT '每人限领数量',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `description` varchar(255) DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `is_active` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否激活',
  `min_amount` decimal(10,2) NOT NULL,
  `name` varchar(100) NOT NULL,
  `received_count` int DEFAULT '0',
  `remaining_quantity` int NOT NULL DEFAULT '0' COMMENT '剩余数量',
  `start_date` date DEFAULT NULL,
  `target_ids` json DEFAULT NULL,
  `total_count` int DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  `usage_scope` varchar(20) NOT NULL DEFAULT 'ALL' COMMENT '使用范围',
  `days_valid` int DEFAULT NULL,
  `validity_type` enum('FIXED_DAYS','FIXED_PERIOD') NOT NULL,
  `value` decimal(10,2) NOT NULL,
  `version` int DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `issued_quantity` int DEFAULT NULL,
  `rule_json` json DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `validity_days` int DEFAULT NULL,
  `total_quantity` int DEFAULT NULL,
  `usage_limit_per_user` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of coupon_templates
-- ----------------------------
INSERT INTO `coupon_templates` (`id`, `acquire_limit`, `created_at`, `description`, `end_date`, `is_active`, `min_amount`, `name`, `received_count`, `remaining_quantity`, `start_date`, `target_ids`, `total_count`, `type`, `updated_at`, `usage_scope`, `days_valid`, `validity_type`, `value`, `version`, `end_time`, `issued_quantity`, `rule_json`, `start_time`, `validity_days`, `total_quantity`, `usage_limit_per_user`) VALUES (1, 1, '2025-12-28 21:05:32.523907', NULL, NULL, 'b''\x01''', '50.00', '优惠券1', 0, 200, NULL, NULL, NULL, 'REDUCTION', '2025-12-31 09:39:37.624338', 'ALL', NULL, 'FIXED_DAYS', '25.00', NULL, NULL, 5, NULL, NULL, 7, 200, 1);

-- ----------------------------
-- Table structure for daily_statistics
-- ----------------------------
DROP TABLE IF EXISTS `daily_statistics`;
CREATE TABLE `daily_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) DEFAULT NULL,
  `new_user_count` int DEFAULT NULL,
  `order_count` int DEFAULT NULL,
  `sales_amount` decimal(15,2) DEFAULT NULL,
  `stat_date` date NOT NULL,
  `store_id` int NOT NULL,
  `total_user_count` int DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  `avg_order_value` decimal(10,2) DEFAULT NULL,
  `new_users` int DEFAULT NULL,
  `total_orders` int DEFAULT NULL,
  `total_points_earned` int DEFAULT NULL,
  `total_points_spent` int DEFAULT NULL,
  `total_sales` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for files
-- ----------------------------
DROP TABLE IF EXISTS `files`;
CREATE TABLE `files` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(50) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `height` int DEFAULT NULL,
  `mime_type` varchar(50) DEFAULT NULL,
  `path` varchar(255) NOT NULL,
  `size` int DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `url` varchar(255) NOT NULL,
  `width` int DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdgr5hx49828s5vhjo1s8q3wdp` (`user_id`),
  CONSTRAINT `FKdgr5hx49828s5vhjo1s8q3wdp` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for gift_cards
-- ----------------------------
DROP TABLE IF EXISTS `gift_cards`;
CREATE TABLE `gift_cards` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` decimal(10,2) NOT NULL,
  `card_code` varchar(128) NOT NULL,
  `card_no` varchar(32) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `expiry_time` datetime(6) DEFAULT NULL,
  `face_value` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dbmjqa52c6jffsqjjbdy4mera` (`card_no`),
  KEY `FKbjcq1j7vh35bq94vjoxkbk1a3` (`user_id`),
  CONSTRAINT `FKbjcq1j7vh35bq94vjoxkbk1a3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of gift_cards
-- ----------------------------
INSERT INTO `gift_cards` (`id`, `balance`, `card_code`, `card_no`, `created_at`, `expiry_time`, `face_value`, `status`, `updated_at`, `user_id`) VALUES (1, '100.00', '3C181002', 'GC1766825484812141', '2025-12-27 16:51:24.832052', '2026-12-27 16:51:24.821463', '100.00', 'UNUSED', '2025-12-27 16:51:24.832052', NULL);
INSERT INTO `gift_cards` (`id`, `balance`, `card_code`, `card_no`, `created_at`, `expiry_time`, `face_value`, `status`, `updated_at`, `user_id`) VALUES (2, '100.00', '15D2C461', 'GC1766848420705261', '2025-12-27 23:13:40.731831', '2026-12-27 23:13:40.713030', '100.00', 'UNUSED', '2025-12-27 23:13:40.731831', NULL);
INSERT INTO `gift_cards` (`id`, `balance`, `card_code`, `card_no`, `created_at`, `expiry_time`, `face_value`, `status`, `updated_at`, `user_id`) VALUES (3, '100.00', 'F05A2B6B', 'GC1766857741884548', '2025-12-28 01:49:01.929975', '2026-12-28 01:49:01.914067', '100.00', 'ACTIVE', '2025-12-28 01:49:01.929975', 11);
INSERT INTO `gift_cards` (`id`, `balance`, `card_code`, `card_no`, `created_at`, `expiry_time`, `face_value`, `status`, `updated_at`, `user_id`) VALUES (4, '100.00', '26EC61A4', 'GC1766911483303215', '2025-12-28 16:44:43.315713', '2026-12-28 16:44:43.309893', '100.00', 'ACTIVE', '2025-12-28 16:44:43.315713', 1);
INSERT INTO `gift_cards` (`id`, `balance`, `card_code`, `card_no`, `created_at`, `expiry_time`, `face_value`, `status`, `updated_at`, `user_id`) VALUES (5, '200.00', '84976712', 'GC1766930805101768', '2025-12-28 22:06:45.120625', '2026-12-28 22:06:45.111885', '200.00', 'ACTIVE', '2025-12-28 22:06:45.120625', 11);
INSERT INTO `gift_cards` (`id`, `balance`, `card_code`, `card_no`, `created_at`, `expiry_time`, `face_value`, `status`, `updated_at`, `user_id`) VALUES (6, '50.00', '2D6B65A1', 'GC1767143880222870', '2025-12-31 09:18:00.236243', '2026-12-31 09:18:00.227240', '50.00', 'ACTIVE', '2025-12-31 09:18:00.236243', 11);
INSERT INTO `gift_cards` (`id`, `balance`, `card_code`, `card_no`, `created_at`, `expiry_time`, `face_value`, `status`, `updated_at`, `user_id`) VALUES (7, '50.00', 'F055B04B', 'GC1767144513167748', '2025-12-31 09:28:33.167628', '2026-12-31 09:28:33.167628', '50.00', 'ACTIVE', '2025-12-31 09:28:33.167628', 11);

-- ----------------------------
-- Table structure for ingredients
-- ----------------------------
DROP TABLE IF EXISTS `ingredients`;
CREATE TABLE `ingredients` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(50) NOT NULL,
  `alert_threshold` decimal(10,2) DEFAULT NULL,
  `cost_per_unit` decimal(10,2) DEFAULT '0.00',
  `stock` decimal(10,2) DEFAULT NULL,
  `unit` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_j6tsl15xx76y4kv41yxr4uxab` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of ingredients
-- ----------------------------
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (1, '0000-00-00 00:00:00.000000', '珍珠', '500.00', '0.05', '5004.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (2, '0000-00-00 00:00:00.000000', '波霸', '300.00', '0.06', '3000.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (3, '0000-00-00 00:00:00.000000', '椰果', '200.00', '0.04', '2000.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (4, '0000-00-00 00:00:00.000000', '布丁', '150.00', '0.08', '1500.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (5, '0000-00-00 00:00:00.000000', '仙草', '100.00', '0.03', '1000.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (6, '0000-00-00 00:00:00.000000', '红豆', '250.00', '0.07', '2500.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (7, '0000-00-00 00:00:00.000000', '燕麦', '180.00', '0.05', '1800.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (8, '0000-00-00 00:00:00.000000', '芋圆', '120.00', '1.09', '1200.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (9, '0000-00-00 00:00:00.000000', '奶精粉', '1000.00', '0.02', '10000.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (10, '0000-00-00 00:00:00.000000', '红茶茶叶', '800.00', '0.10', '8000.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (11, '0000-00-00 00:00:00.000000', '绿茶茶叶', '750.00', '0.12', '7500.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (12, '0000-00-00 00:00:00.000000', '乌龙茶叶', '600.00', '0.15', '6000.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (13, '0000-00-00 00:00:00.000000', '白砂糖', '1500.00', '0.01', '15000.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (14, '0000-00-00 00:00:00.000000', '果糖', '2000.00', '0.02', '20000.00', 'ml');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (15, '0000-00-00 00:00:00.000000', '牛奶', '5000.00', '0.03', '50000.00', 'ml');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (16, '0000-00-00 00:00:00.000000', '淡奶油', '1000.00', '0.08', '10000.00', 'ml');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (17, '0000-00-00 00:00:00.000000', '芝士奶盖粉', '500.00', '0.12', '5000.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (18, '0000-00-00 00:00:00.000000', '芒果果酱', '300.00', '0.15', '3000.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (19, '0000-00-00 00:00:00.000000', '草莓果酱', '280.00', '0.14', '2800.00', 'g');
INSERT INTO `ingredients` (`id`, `created_at`, `name`, `alert_threshold`, `cost_per_unit`, `stock`, `unit`) VALUES (20, '0000-00-00 00:00:00.000000', '葡萄果酱', '250.00', '0.16', '2500.00', 'g');

-- ----------------------------
-- Table structure for member_levels
-- ----------------------------
DROP TABLE IF EXISTS `member_levels`;
CREATE TABLE `member_levels` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `min_growth_value` int NOT NULL DEFAULT '0' COMMENT '所需最小成长值',
  `name` varchar(50) NOT NULL,
  `privileges_json` json DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `discount_rate` decimal(3,2) DEFAULT '1.00' COMMENT '会员折扣率',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ldagxsoc9y8nagb3dgjvma2ur` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of member_levels
-- ----------------------------
INSERT INTO `member_levels` (`id`, `created_at`, `description`, `min_growth_value`, `name`, `privileges_json`, `updated_at`, `discount_rate`) VALUES (1, '2025-12-30 22:51:52', '注册即可加入', 0, '普通会员', NULL, '2025-12-30 22:51:52', '1.00');
INSERT INTO `member_levels` (`id`, `created_at`, `description`, `min_growth_value`, `name`, `privileges_json`, `updated_at`, `discount_rate`) VALUES (2, '2025-12-30 22:51:52', '消费满1000元升级', 1000, '黄金会员', NULL, '2025-12-30 22:51:52', '0.95');
INSERT INTO `member_levels` (`id`, `created_at`, `description`, `min_growth_value`, `name`, `privileges_json`, `updated_at`, `discount_rate`) VALUES (3, '2025-12-30 22:51:52', '消费满5000元升级', 5000, '钻石会员', NULL, '2025-12-30 22:51:52', '0.88');

-- ----------------------------
-- Table structure for notifications
-- ----------------------------
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `data_json` json DEFAULT NULL,
  `is_read` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已读',
  `read_at` datetime(6) DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `type` varchar(20) DEFAULT 'SYSTEM' COMMENT '通知类型',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID（推送任务可为空）',
  `sent_time` datetime(6) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `target_type` varchar(20) DEFAULT NULL,
  `target_value` varchar(100) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `link_url` varchar(255) DEFAULT NULL,
  `push_type` varchar(20) DEFAULT NULL,
  `scheduled_time` datetime(6) DEFAULT NULL,
  `trigger_condition` varchar(255) DEFAULT NULL,
  `trigger_type` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of notifications
-- ----------------------------
INSERT INTO `notifications` (`id`, `content`, `created_at`, `data_json`, `is_read`, `read_at`, `title`, `type`, `user_id`, `sent_time`, `status`, `target_type`, `target_value`, `image_url`, `link_url`, `push_type`, `scheduled_time`, `trigger_condition`, `trigger_type`) VALUES (1, '活动11415', '2025-12-28 14:20:32.396505', NULL, 'b''\x00''', NULL, '活动', NULL, NULL, '2025-12-28 14:20:32.303165', 'SENT', 'ALL', NULL, NULL, NULL, 'MARKETING', NULL, NULL, 'IMMEDIATE');

-- ----------------------------
-- Table structure for order_appeal
-- ----------------------------
DROP TABLE IF EXISTS `order_appeal`;
CREATE TABLE `order_appeal` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `reason` varchar(100) NOT NULL COMMENT '申诉原因',
  `description` text NOT NULL COMMENT '详细描述',
  `amount` decimal(10,2) NOT NULL COMMENT '申诉退款金额',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待处理, APPROVED-已通过, REJECTED-已拒绝',
  `reply` text COMMENT '后台回复',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单申诉表';

-- ----------------------------
-- Table structure for order_appeals
-- ----------------------------
DROP TABLE IF EXISTS `order_appeals`;
CREATE TABLE `order_appeals` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_remark` text,
  `amount` decimal(10,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `reason` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `order_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpg1qx0dymdqlujlls73ohvrwn` (`order_id`),
  KEY `FKpxemrslf71bkyf1hhbnvluqk8` (`user_id`),
  CONSTRAINT `FKpg1qx0dymdqlujlls73ohvrwn` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKpxemrslf71bkyf1hhbnvluqk8` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for order_item_customizations
-- ----------------------------
DROP TABLE IF EXISTS `order_item_customizations`;
CREATE TABLE `order_item_customizations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `customization_type_name` varchar(50) NOT NULL,
  `option_label` varchar(50) NOT NULL,
  `option_value` varchar(50) NOT NULL,
  `price_adjustment_at_order` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL,
  `order_item_id` bigint NOT NULL,
  `option_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9icomx5dcq92o8u99l1te7bql` (`order_item_id`),
  KEY `FKcpgx2pfynhs4jkr4s9bw4r1bu` (`option_id`),
  CONSTRAINT `FK9icomx5dcq92o8u99l1te7bql` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`id`),
  CONSTRAINT `FKcpgx2pfynhs4jkr4s9bw4r1bu` FOREIGN KEY (`option_id`) REFERENCES `product_options` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `original_price_at_order` decimal(10,2) DEFAULT NULL,
  `price_at_order` decimal(10,2) DEFAULT '0.00' COMMENT '下单时价格',
  `product_image_url` varchar(255) DEFAULT NULL,
  `product_name` varchar(100) NOT NULL,
  `quantity` int NOT NULL,
  `subtotal` decimal(10,2) DEFAULT '0.00' COMMENT '小计',
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `product_image` varchar(200) DEFAULT NULL,
  `remark` varchar(100) DEFAULT NULL,
  `spec_json` json DEFAULT NULL,
  `sweetness` varchar(20) DEFAULT NULL,
  `temperature` varchar(20) DEFAULT NULL,
  `total_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `fk_order_items_product_id_restrict` (`product_id`),
  CONSTRAINT `fk_order_items_product_id_restrict` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (11, '2025-12-28 01:29:18.888611', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 10, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (12, '2025-12-28 01:29:18.897825', NULL, '0.00', NULL, '杨枝甘露', 1, '0.00', 10, 8, '22.00', 'http://localhost:8081/uploads/1766754468302_ea193fa2.jpg', NULL, NULL, NULL, NULL, '22.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (13, '2025-12-28 01:29:25.537417', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 11, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (14, '2025-12-28 01:29:25.545655', NULL, '0.00', NULL, '杨枝甘露', 1, '0.00', 11, 8, '22.00', 'http://localhost:8081/uploads/1766754468302_ea193fa2.jpg', NULL, NULL, NULL, NULL, '22.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (15, '2025-12-28 01:29:51.853371', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 12, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (16, '2025-12-28 01:29:51.862304', NULL, '0.00', NULL, '杨枝甘露', 1, '0.00', 12, 8, '22.00', 'http://localhost:8081/uploads/1766754468302_ea193fa2.jpg', NULL, NULL, NULL, NULL, '22.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (17, '2025-12-28 01:33:24.333096', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 13, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (18, '2025-12-28 01:33:24.342880', NULL, '0.00', NULL, '杨枝甘露', 1, '0.00', 13, 8, '22.00', 'http://localhost:8081/uploads/1766754468302_ea193fa2.jpg', NULL, NULL, NULL, NULL, '22.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (19, '2025-12-28 01:34:09.811829', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 14, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (20, '2025-12-28 01:34:09.846086', NULL, '0.00', NULL, '杨枝甘露', 1, '0.00', 14, 8, '22.00', 'http://localhost:8081/uploads/1766754468302_ea193fa2.jpg', NULL, NULL, NULL, NULL, '22.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (21, '2025-12-28 01:47:15.708336', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 15, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (22, '2025-12-28 01:47:15.717380', NULL, '0.00', NULL, '杨枝甘露', 1, '0.00', 15, 8, '22.00', 'http://localhost:8081/uploads/1766754468302_ea193fa2.jpg', NULL, NULL, NULL, NULL, '22.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (23, '2025-12-28 17:16:24.229954', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 16, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (24, '2025-12-28 17:22:11.608531', NULL, '0.00', NULL, '珍珠奶茶', 3, '0.00', 17, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '45.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (25, '2025-12-28 17:22:11.612546', NULL, '0.00', NULL, '杨枝甘露', 1, '0.00', 17, 8, '22.00', 'http://localhost:8081/uploads/1766754468302_ea193fa2.jpg', NULL, NULL, NULL, NULL, '22.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (26, '2025-12-28 17:22:11.614419', NULL, '0.00', NULL, '多肉葡萄', 1, '0.00', 17, 9, '28.00', 'http://localhost:8081/uploads/1766754474756_76b4b99f.jpg', NULL, NULL, NULL, NULL, '28.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (27, '2025-12-28 17:32:19.426874', NULL, '0.00', NULL, '珍珠奶茶', 3, '0.00', 18, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '45.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (28, '2025-12-28 17:32:19.431092', NULL, '0.00', NULL, '杨枝甘露', 1, '0.00', 18, 8, '22.00', 'http://localhost:8081/uploads/1766754468302_ea193fa2.jpg', NULL, NULL, NULL, NULL, '22.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (29, '2025-12-28 17:32:19.434960', NULL, '0.00', NULL, '多肉葡萄', 1, '0.00', 18, 9, '28.00', 'http://localhost:8081/uploads/1766754474756_76b4b99f.jpg', NULL, NULL, NULL, NULL, '28.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (30, '2025-12-28 17:44:16.233239', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 19, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (31, '2025-12-28 17:50:52.032617', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 20, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (32, '2025-12-28 18:07:05.061091', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 21, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (33, '2025-12-28 18:21:07.756522', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 22, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (34, '2025-12-28 19:26:05.214264', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 23, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (35, '2025-12-28 21:49:08.719158', NULL, '0.00', NULL, '杨枝甘露', 1, '0.00', 24, 8, '22.00', 'http://localhost:8081/uploads/1766754468302_ea193fa2.jpg', NULL, NULL, NULL, NULL, '22.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (36, '2025-12-28 21:49:08.723761', NULL, '0.00', NULL, '多肉葡萄', 1, '0.00', 24, 9, '28.00', 'http://localhost:8081/uploads/1766754474756_76b4b99f.jpg', NULL, NULL, NULL, NULL, '28.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (37, '2025-12-28 21:50:38.562871', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 25, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (38, '2025-12-28 21:50:38.565502', NULL, '0.00', NULL, '杨枝甘露', 1, '0.00', 25, 8, '22.00', 'http://localhost:8081/uploads/1766754468302_ea193fa2.jpg', NULL, NULL, NULL, NULL, '22.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (39, '2025-12-28 21:50:38.567802', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 25, 11, '15.00', 'http://localhost:8081/uploads/1766830521730_cb2921c7.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (40, '2025-12-28 22:06:12.749963', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 26, 6, '15.00', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', NULL, NULL, NULL, NULL, '15.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (41, '2025-12-28 22:06:12.751733', NULL, '0.00', NULL, '多肉葡萄', 1, '0.00', 26, 9, '28.00', 'http://localhost:8081/uploads/1766754474756_76b4b99f.jpg', NULL, NULL, NULL, NULL, '28.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (42, '2025-12-28 22:06:12.752584', NULL, '0.00', NULL, '波霸奶茶', 1, '0.00', 26, 12, '16.00', 'http://localhost:8081/uploads/1766830527195_860483d7.jpg', NULL, NULL, NULL, NULL, '16.00');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (43, '2025-12-31 07:54:40.932310', NULL, '0.00', NULL, '珍珠奶茶', 2, '0.00', 27, 6, '14.17', '/uploads/1767108287276_07f7530d.png', NULL, NULL, NULL, NULL, '28.34');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (44, '2025-12-31 07:54:40.935744', NULL, '0.00', NULL, '波霸奶茶', 1, '0.00', 27, 7, '15.17', '/uploads/1767108294471_9a945db6.jpg', NULL, NULL, NULL, NULL, '15.17');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (45, '2025-12-31 07:54:40.937071', NULL, '0.00', NULL, '生椰拿铁', 1, '0.00', 27, 10, '17.17', '/uploads/1767108322608_e8d3e5c0.jpg', NULL, NULL, NULL, NULL, '17.17');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (46, '2025-12-31 07:54:40.938403', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 27, 11, '14.17', '/uploads/1767108329337_03ba19ca.webp', NULL, NULL, NULL, NULL, '14.17');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (47, '2025-12-31 07:54:40.939323', NULL, '0.00', NULL, '波霸奶茶', 1, '0.00', 27, 12, '15.17', '/uploads/1767108339594_31b5539e.jpg', NULL, NULL, NULL, NULL, '15.17');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (48, '2025-12-31 09:32:26.676810', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 28, 6, '13.33', '/uploads/1767108287276_07f7530d.png', NULL, NULL, NULL, NULL, '13.33');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (49, '2025-12-31 09:32:26.678949', NULL, '0.00', NULL, '波霸奶茶', 1, '0.00', 28, 7, '14.33', '/uploads/1767108294471_9a945db6.jpg', NULL, NULL, NULL, NULL, '14.33');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (50, '2025-12-31 09:32:26.680098', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 28, 11, '13.33', '/uploads/1767108329337_03ba19ca.webp', NULL, NULL, NULL, NULL, '13.33');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (51, '2025-12-31 09:34:41.773876', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 29, 6, '13.33', '/uploads/1767108287276_07f7530d.png', NULL, NULL, NULL, NULL, '13.33');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (52, '2025-12-31 09:34:41.775101', NULL, '0.00', NULL, '波霸奶茶', 1, '0.00', 29, 7, '14.33', '/uploads/1767108294471_9a945db6.jpg', NULL, NULL, NULL, NULL, '14.33');
INSERT INTO `order_items` (`id`, `created_at`, `original_price_at_order`, `price_at_order`, `product_image_url`, `product_name`, `quantity`, `subtotal`, `order_id`, `product_id`, `price`, `product_image`, `remark`, `spec_json`, `sweetness`, `temperature`, `total_price`) VALUES (53, '2025-12-31 09:34:41.776021', NULL, '0.00', NULL, '珍珠奶茶', 1, '0.00', 29, 11, '13.33', '/uploads/1767108329337_03ba19ca.webp', NULL, NULL, NULL, NULL, '13.33');

-- ----------------------------
-- Table structure for order_refund_images
-- ----------------------------
DROP TABLE IF EXISTS `order_refund_images`;
CREATE TABLE `order_refund_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `refund_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtp1pufg85a5clnusv2ltydxn8` (`refund_id`),
  CONSTRAINT `FKtp1pufg85a5clnusv2ltydxn8` FOREIGN KEY (`refund_id`) REFERENCES `order_refunds` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for order_refunds
-- ----------------------------
DROP TABLE IF EXISTS `order_refunds`;
CREATE TABLE `order_refunds` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `reason` varchar(100) NOT NULL,
  `refund_amount` decimal(10,2) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `order_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `order_no` varchar(32) NOT NULL,
  `reply` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmc53i8eq1lukxtbdulobibrek` (`order_id`),
  KEY `FKb9u2c1e8uhvpqn0koly6hlq8t` (`user_id`),
  CONSTRAINT `FKb9u2c1e8uhvpqn0koly6hlq8t` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKmc53i8eq1lukxtbdulobibrek` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for order_review
-- ----------------------------
DROP TABLE IF EXISTS `order_review`;
CREATE TABLE `order_review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `store_id` bigint NOT NULL COMMENT '门店ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_rating` int NOT NULL DEFAULT '5' COMMENT '商品评分(1-5)',
  `delivery_rating` int NOT NULL DEFAULT '5' COMMENT '配送评分(1-5)',
  `content` text COMMENT '评价内容',
  `images` text COMMENT '评价图片(JSON数组)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单评价表';

-- ----------------------------
-- Table structure for order_review_appends
-- ----------------------------
DROP TABLE IF EXISTS `order_review_appends`;
CREATE TABLE `order_review_appends` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `review_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9rg9nkraeoq87d2hhes0to52p` (`review_id`),
  CONSTRAINT `FK9rg9nkraeoq87d2hhes0to52p` FOREIGN KEY (`review_id`) REFERENCES `order_reviews` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for order_review_images
-- ----------------------------
DROP TABLE IF EXISTS `order_review_images`;
CREATE TABLE `order_review_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `review_id` bigint NOT NULL,
  `sort_order` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKibiwstkf6m9h025itpairjsa9` (`review_id`),
  CONSTRAINT `FKibiwstkf6m9h025itpairjsa9` FOREIGN KEY (`review_id`) REFERENCES `order_reviews` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for order_review_likes
-- ----------------------------
DROP TABLE IF EXISTS `order_review_likes`;
CREATE TABLE `order_review_likes` (
  `created_at` datetime(6) DEFAULT NULL,
  `review_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`review_id`,`user_id`),
  KEY `FKjwn3csdfuiu1wi3vf19yu5pij` (`user_id`),
  CONSTRAINT `FK98p7uhat6vbn1seewheffohtl` FOREIGN KEY (`review_id`) REFERENCES `order_reviews` (`id`),
  CONSTRAINT `FKjwn3csdfuiu1wi3vf19yu5pij` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for order_review_replies
-- ----------------------------
DROP TABLE IF EXISTS `order_review_replies`;
CREATE TABLE `order_review_replies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_official` bit(1) DEFAULT NULL,
  `review_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKauj972o6cpfi8tq0lv02opuff` (`review_id`),
  KEY `FK9dy2vfs95vsek0qxdbccdeqyr` (`user_id`),
  CONSTRAINT `FK9dy2vfs95vsek0qxdbccdeqyr` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKauj972o6cpfi8tq0lv02opuff` FOREIGN KEY (`review_id`) REFERENCES `order_reviews` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for order_review_tags
-- ----------------------------
DROP TABLE IF EXISTS `order_review_tags`;
CREATE TABLE `order_review_tags` (
  `review_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`review_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for order_reviews
-- ----------------------------
DROP TABLE IF EXISTS `order_reviews`;
CREATE TABLE `order_reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_anonymous` bit(1) NOT NULL,
  `rating` smallint NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `order_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `images_json` json DEFAULT NULL,
  `like_count` int DEFAULT NULL,
  `reply_count` int DEFAULT NULL,
  `score` int NOT NULL,
  `product_id` bigint DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  `delivery_score` int DEFAULT NULL,
  `product_score` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK47luv1v5wdmtm40rpbqrr2x65` (`order_id`),
  KEY `FKdlovycfbwhcwuht06h17igxk3` (`user_id`),
  KEY `FKdf6yyaj1ccykodu0lprh5hvvy` (`product_id`),
  KEY `FK_review_store` (`store_id`),
  CONSTRAINT `FK_review_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`),
  CONSTRAINT `FKcxerv1xcu85jo5dib1nloatqw` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`),
  CONSTRAINT `FKdf6yyaj1ccykodu0lprh5hvvy` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKdlovycfbwhcwuht06h17igxk3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKejq2ik6sp8jo71cfdf1v0w5vh` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for order_status_timelines
-- ----------------------------
DROP TABLE IF EXISTS `order_status_timelines`;
CREATE TABLE `order_status_timelines` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `is_current` bit(1) DEFAULT b'1' COMMENT '是否当前状态',
  `status` varchar(20) NOT NULL,
  `status_text` varchar(50) DEFAULT '' COMMENT '状态文本',
  `time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '状态时间',
  `order_id` bigint NOT NULL,
  `remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKchg3d94pdow7x5lf11u2a51sd` (`order_id`),
  CONSTRAINT `FKchg3d94pdow7x5lf11u2a51sd` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of order_status_timelines
-- ----------------------------
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (1, '2025-12-28 01:29:18.900617', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 01:29:18.903248', 10, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (2, '2025-12-28 01:29:25.549334', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 01:29:25.554248', 11, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (3, '2025-12-28 01:29:51.866766', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 01:29:51.871544', 12, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (4, '2025-12-28 01:33:24.345679', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 01:33:24.350107', 13, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (5, '2025-12-28 01:34:09.886166', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 01:34:09.894115', 14, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (6, '2025-12-28 01:47:15.741591', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 01:47:15.745817', 15, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (7, '2025-12-28 17:16:24.234163', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 17:16:24.235670', 16, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (8, '2025-12-28 17:22:11.622579', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 17:22:11.623692', 17, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (9, '2025-12-28 17:32:19.467204', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 17:32:19.469982', 18, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (10, '2025-12-28 17:44:16.238702', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 17:44:16.240398', 19, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (11, '2025-12-28 17:44:18.828010', 'b''\x01''', 'PAID', '', '2025-12-28 17:44:18.829107', 19, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (12, '2025-12-28 17:44:42.836394', 'b''\x01''', 'MAKING', '', '2025-12-28 17:44:42.837191', 19, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (13, '2025-12-28 17:44:49.857419', 'b''\x01''', 'DELIVERING', '', '2025-12-28 17:44:49.859422', 19, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (14, '2025-12-28 17:50:52.034987', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 17:50:52.036014', 20, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (15, '2025-12-28 17:50:55.577054', 'b''\x01''', 'PAID', '', '2025-12-28 17:50:55.578256', 19, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (16, '2025-12-28 17:51:14.594333', 'b''\x01''', 'MAKING', '', '2025-12-28 17:51:14.595597', 19, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (17, '2025-12-28 17:51:28.618629', 'b''\x01''', 'DELIVERING', '', '2025-12-28 17:51:28.619741', 19, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (18, '2025-12-28 18:07:05.065164', 'b''\x01''', 'PENDING_PAYMENT', '', '2025-12-28 18:07:05.066917', 21, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (19, '2025-12-28 18:07:07.455355', 'b''\x01''', 'PAID', '', '2025-12-28 18:07:07.456327', 19, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (20, '2025-12-28 18:07:23.469933', 'b''\x01''', 'MAKING', '', '2025-12-28 18:07:23.470973', 19, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (21, '2025-12-28 18:07:28.499274', 'b''\x01''', 'DELIVERING', '', '2025-12-28 18:07:28.500399', 19, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (22, '2025-12-28 18:08:28.519351', 'b''\x01''', 'DELIVERED', '', '2025-12-28 18:08:28.520879', 19, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (23, '2025-12-28 18:21:07.764032', 'b''\x01''', 'PENDING_PAYMENT', NULL, '2025-12-28 18:21:07.757624', 22, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (24, '2025-12-28 18:21:09.911526', 'b''\x01''', 'PAID', NULL, '2025-12-28 18:21:09.911526', 22, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (25, '2025-12-28 18:21:36.915406', 'b''\x01''', 'MAKING', NULL, '2025-12-28 18:21:36.915407', 22, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (26, '2025-12-28 18:21:48.933907', 'b''\x01''', 'DELIVERING', NULL, '2025-12-28 18:21:48.933908', 22, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (27, '2025-12-28 18:22:48.952025', 'b''\x01''', 'DELIVERED', NULL, '2025-12-28 18:22:48.952026', 22, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (28, '2025-12-28 19:26:05.231535', 'b''\x01''', 'PENDING_PAYMENT', NULL, '2025-12-28 19:26:05.218065', 23, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (29, '2025-12-28 19:26:07.264201', 'b''\x01''', 'PAID', NULL, '2025-12-28 19:26:07.264201', 23, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (30, '2025-12-28 19:26:24.290934', 'b''\x01''', 'MAKING', NULL, '2025-12-28 19:26:24.290935', 23, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (31, '2025-12-28 19:26:32.330576', 'b''\x01''', 'DELIVERING', NULL, '2025-12-28 19:26:32.329569', 23, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (32, '2025-12-28 19:27:32.381916', 'b''\x01''', 'DELIVERED', NULL, '2025-12-28 19:27:32.381916', 23, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (33, '2025-12-28 21:37:40.243334', 'b''\x01''', 'PAID', NULL, '2025-12-28 21:37:40.240267', 21, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (34, '2025-12-28 21:38:00.255484', 'b''\x01''', 'MAKING', NULL, '2025-12-28 21:38:00.255485', 21, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (35, '2025-12-28 21:38:12.272130', 'b''\x01''', 'DELIVERING', NULL, '2025-12-28 21:38:12.272131', 21, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (36, '2025-12-28 21:39:12.300311', 'b''\x01''', 'DELIVERED', NULL, '2025-12-28 21:39:12.300311', 21, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (37, '2025-12-28 21:49:08.765563', 'b''\x01''', 'PENDING_PAYMENT', NULL, '2025-12-28 21:49:08.725377', 24, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (38, '2025-12-28 21:49:13.549445', 'b''\x01''', 'PAID', NULL, '2025-12-28 21:49:13.549445', 21, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (39, '2025-12-28 21:49:32.572683', 'b''\x01''', 'MAKING', NULL, '2025-12-28 21:49:32.571973', 21, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (40, '2025-12-28 21:49:39.609661', 'b''\x01''', 'DELIVERING', NULL, '2025-12-28 21:49:39.609121', 21, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (41, '2025-12-28 21:50:38.570293', 'b''\x01''', 'PENDING_PAYMENT', NULL, '2025-12-28 21:50:38.569292', 25, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (42, '2025-12-28 21:50:39.646305', 'b''\x01''', 'DELIVERED', NULL, '2025-12-28 21:50:39.645294', 21, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (43, '2025-12-28 21:50:42.053718', 'b''\x01''', 'PAID', NULL, '2025-12-28 21:50:42.053718', 25, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (44, '2025-12-28 21:51:08.068743', 'b''\x01''', 'MAKING', NULL, '2025-12-28 21:51:08.068170', 25, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (45, '2025-12-28 21:51:14.099179', 'b''\x01''', 'DELIVERING', NULL, '2025-12-28 21:51:14.099179', 25, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (46, '2025-12-28 21:52:14.129289', 'b''\x01''', 'DELIVERED', NULL, '2025-12-28 21:52:14.129290', 25, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (47, '2025-12-28 22:06:12.754002', 'b''\x01''', 'PENDING_PAYMENT', NULL, '2025-12-28 22:06:12.753468', 26, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (48, '2025-12-28 22:06:14.681930', 'b''\x01''', 'PAID', NULL, '2025-12-28 22:06:14.680526', 26, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (49, '2025-12-28 22:06:43.692618', 'b''\x01''', 'MAKING', NULL, '2025-12-28 22:06:43.692619', 26, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (50, '2025-12-28 22:06:57.726348', 'b''\x01''', 'DELIVERING', NULL, '2025-12-28 22:06:57.726349', 26, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (51, '2025-12-28 22:07:12.450992', 'b''\x01''', 'PAID', NULL, '2025-12-28 22:07:12.450992', 26, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (52, '2025-12-28 22:07:42.468216', 'b''\x01''', 'MAKING', NULL, '2025-12-28 22:07:42.467203', 26, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (53, '2025-12-28 22:07:47.501831', 'b''\x01''', 'DELIVERING', NULL, '2025-12-28 22:07:47.501832', 26, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (54, '2025-12-28 22:07:57.760180', 'b''\x01''', 'DELIVERED', NULL, '2025-12-28 22:07:57.760180', 26, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (55, '2025-12-28 22:08:47.520618', 'b''\x01''', 'DELIVERED', NULL, '2025-12-28 22:08:47.520112', 26, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (56, '2025-12-30 23:22:23.139374', 'b''\x01''', 'PAID', NULL, '2025-12-30 23:22:23.132354', 24, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (57, '2025-12-30 23:22:34.135586', 'b''\x01''', 'MAKING', NULL, '2025-12-30 23:22:34.135586', 24, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (58, '2025-12-30 23:22:48.162681', 'b''\x01''', 'DELIVERING', NULL, '2025-12-30 23:22:48.162681', 24, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (59, '2025-12-30 23:23:48.192893', 'b''\x01''', 'DELIVERED', NULL, '2025-12-30 23:23:48.192894', 24, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (60, '2025-12-31 01:17:27.641031', 'b''\x01''', 'PAID', NULL, '2025-12-31 01:17:27.617996', 20, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (61, '2025-12-31 01:17:44.746833', 'b''\x01''', 'PAID', NULL, '2025-12-31 01:17:44.745827', 20, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (62, '2025-12-31 01:17:45.616123', 'b''\x01''', 'MAKING', NULL, '2025-12-31 01:17:45.615126', 20, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (63, '2025-12-31 01:17:58.674587', 'b''\x01''', 'DELIVERING', NULL, '2025-12-31 01:17:58.673599', 20, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (64, '2025-12-31 01:18:30.659874', 'b''\x01''', 'PAID', NULL, '2025-12-31 01:18:30.658870', 10, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (65, '2025-12-31 01:18:38.140043', 'b''\x01''', 'PAID', NULL, '2025-12-31 01:18:38.140043', 10, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (66, '2025-12-31 01:18:48.680993', 'b''\x01''', 'MAKING', NULL, '2025-12-31 01:18:48.680994', 10, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (67, '2025-12-31 01:18:58.767197', 'b''\x01''', 'DELIVERED', NULL, '2025-12-31 01:18:58.766197', 20, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (68, '2025-12-31 01:19:07.728624', 'b''\x01''', 'DELIVERING', NULL, '2025-12-31 01:19:07.728624', 10, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (69, '2025-12-31 01:19:10.590995', 'b''\x01''', 'PAID', NULL, '2025-12-31 01:19:10.590995', 10, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (70, '2025-12-31 01:19:21.702919', 'b''\x01''', 'MAKING', NULL, '2025-12-31 01:19:21.702920', 10, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (71, '2025-12-31 01:19:35.798611', 'b''\x01''', 'DELIVERING', NULL, '2025-12-31 01:19:35.798611', 10, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (72, '2025-12-31 01:20:07.803055', 'b''\x01''', 'DELIVERED', NULL, '2025-12-31 01:20:07.802055', 10, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (73, '2025-12-31 01:20:35.846536', 'b''\x01''', 'DELIVERED', NULL, '2025-12-31 01:20:35.845488', 10, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (74, '2025-12-31 01:39:11.022482', 'b''\x01''', 'CANCELLED', NULL, '2025-12-31 01:39:11.014367', 16, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (75, '2025-12-31 01:39:13.897546', 'b''\x01''', 'CANCELLED', NULL, '2025-12-31 01:39:13.896546', 15, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (76, '2025-12-31 01:39:16.531718', 'b''\x01''', 'CANCELLED', NULL, '2025-12-31 01:39:16.531719', 14, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (77, '2025-12-31 01:39:18.784206', 'b''\x01''', 'CANCELLED', NULL, '2025-12-31 01:39:18.784207', 13, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (78, '2025-12-31 01:39:22.053516', 'b''\x01''', 'CANCELLED', NULL, '2025-12-31 01:39:22.053517', 11, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (79, '2025-12-31 01:39:26.672984', 'b''\x01''', 'CANCELLED', NULL, '2025-12-31 01:39:26.671999', 12, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (80, '2025-12-31 07:54:40.944795', 'b''\x01''', 'PENDING_PAYMENT', NULL, '2025-12-31 07:54:40.939813', 27, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (81, '2025-12-31 07:54:45.731922', 'b''\x01''', 'PAID', NULL, '2025-12-31 07:54:45.731923', 27, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (82, '2025-12-31 07:54:59.755890', 'b''\x01''', 'MAKING', NULL, '2025-12-31 07:54:59.755891', 27, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (83, '2025-12-31 07:55:11.784480', 'b''\x01''', 'DELIVERING', NULL, '2025-12-31 07:55:11.784480', 27, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (84, '2025-12-31 07:56:11.815024', 'b''\x01''', 'DELIVERED', NULL, '2025-12-31 07:56:11.815025', 27, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (85, '2025-12-31 09:32:26.683253', 'b''\x01''', 'PENDING_PAYMENT', NULL, '2025-12-31 09:32:26.680685', 28, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (86, '2025-12-31 09:32:31.303233', 'b''\x01''', 'PAID', NULL, '2025-12-31 09:32:31.301955', 28, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (87, '2025-12-31 09:32:58.341419', 'b''\x01''', 'MAKING', NULL, '2025-12-31 09:32:58.340635', 28, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (88, '2025-12-31 09:33:18.371896', 'b''\x01''', 'DELIVERING', NULL, '2025-12-31 09:33:18.371896', 28, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (89, '2025-12-31 09:34:18.393412', 'b''\x01''', 'DELIVERED', NULL, '2025-12-31 09:34:18.393412', 28, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (90, '2025-12-31 09:34:41.776666', 'b''\x01''', 'PENDING_PAYMENT', NULL, '2025-12-31 09:34:41.775233', 29, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (91, '2025-12-31 09:34:45.020180', 'b''\x01''', 'PAID', NULL, '2025-12-31 09:34:45.020181', 28, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (92, '2025-12-31 09:35:07.029449', 'b''\x01''', 'MAKING', NULL, '2025-12-31 09:35:07.028387', 28, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (93, '2025-12-31 09:35:27.057496', 'b''\x01''', 'DELIVERING', NULL, '2025-12-31 09:35:27.057496', 28, NULL);
INSERT INTO `order_status_timelines` (`id`, `created_at`, `is_current`, `status`, `status_text`, `time`, `order_id`, `remark`) VALUES (94, '2025-12-31 09:36:27.097451', 'b''\x01''', 'DELIVERED', NULL, '2025-12-31 09:36:27.097452', 28, NULL);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `acceptor_id` bigint DEFAULT NULL,
  `acceptor_name` varchar(50) DEFAULT NULL,
  `actual_ready_time` datetime(6) DEFAULT NULL,
  `balance_discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '余额抵扣金额',
  `balance_used` decimal(10,2) DEFAULT '0.00' COMMENT '余额抵扣金额',
  `cancel_deadline` datetime(6) DEFAULT NULL,
  `counter_number` varchar(20) DEFAULT NULL,
  `coupon_id` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `delivery_fee` decimal(10,2) NOT NULL,
  `delivery_time_expected` datetime(6) DEFAULT NULL,
  `discount_amount` decimal(10,2) NOT NULL,
  `estimated_arrival_time` datetime(6) DEFAULT NULL,
  `estimated_ready_time` datetime(6) DEFAULT NULL,
  `invoice_tax_number` varchar(50) DEFAULT NULL,
  `invoice_title` varchar(100) DEFAULT NULL,
  `invoice_type` varchar(20) DEFAULT NULL,
  `is_rated` bit(1) DEFAULT b'0' COMMENT '是否已评价',
  `order_no` varchar(50) NOT NULL,
  `package_fee` decimal(10,2) DEFAULT '0.00' COMMENT '包装费',
  `pay_amount` decimal(10,2) DEFAULT '0.00' COMMENT '实付金额',
  `pickup_code` varchar(20) DEFAULT NULL,
  `pickup_time_actual` datetime(6) DEFAULT NULL,
  `points_discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '积分抵扣金额',
  `points_used` int DEFAULT '0' COMMENT '使用的积分',
  `product_amount` decimal(10,2) DEFAULT '0.00' COMMENT '商品总额',
  `queue_position` int DEFAULT NULL,
  `rate_deadline` datetime(6) DEFAULT NULL,
  `refund_deadline` datetime(6) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `rider_latitude` decimal(10,7) DEFAULT NULL,
  `rider_longitude` decimal(10,7) DEFAULT NULL,
  `rider_name` varchar(50) DEFAULT NULL,
  `rider_phone` varchar(20) DEFAULT NULL,
  `status` varchar(20) NOT NULL COMMENT '订单状态: PENDING_PAYMENT-待支付, PAID-已支付/待接单, MAKING-制作中, READY-待取餐, DELIVERING-配送中, DELIVERED-已送达, COMPLETED-已完成, FINISHED-已结束, REFUNDING-退款中, REFUNDED-已退款, CANCELLED-已取消, REVIEWED-已评价',
  `status_text` varchar(50) DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `type` varchar(20) DEFAULT 'PICKUP' COMMENT '订单类型',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delivery_address_id` bigint DEFAULT NULL,
  `pickup_store_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `actual_amount` decimal(10,2) DEFAULT '0.00' COMMENT '实际支付金额',
  `address_json` json DEFAULT NULL,
  `delivery_type` varchar(20) NOT NULL,
  `is_commented` bit(1) DEFAULT NULL,
  `last_remind_time` datetime(6) DEFAULT NULL,
  `order_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
  `pay_method` varchar(20) DEFAULT NULL,
  `pay_time` datetime(6) DEFAULT NULL,
  `queue_number` int DEFAULT NULL,
  `remind_count` int DEFAULT NULL,
  `transaction_id` varchar(64) DEFAULT NULL,
  `store_id` bigint NOT NULL,
  `refund_reason` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g8pohnngqi5x1nask7nff2u7w` (`order_no`),
  KEY `FKdetdfuwt99im2ecifbqm94isd` (`delivery_address_id`),
  KEY `FK1mkbbvgn9c5h1kflgdsmb4wlh` (`pickup_store_id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  KEY `FKnqkwhwveegs6ne9ra90y1gq0e` (`store_id`),
  CONSTRAINT `FK1mkbbvgn9c5h1kflgdsmb4wlh` FOREIGN KEY (`pickup_store_id`) REFERENCES `stores` (`id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKdetdfuwt99im2ecifbqm94isd` FOREIGN KEY (`delivery_address_id`) REFERENCES `user_addresses` (`id`),
  CONSTRAINT `FKnqkwhwveegs6ne9ra90y1gq0e` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (1, NULL, NULL, '2025-12-28 18:49:29.981620', '0.00', '0.00', NULL, NULL, NULL, '2025-12-26 20:16:33', '0.00', NULL, '5.00', NULL, '2025-12-26 20:31:33', NULL, NULL, NULL, 'b''\x00''', 'T202412260001', '0.00', '40.00', 'A123', NULL, '0.00', 0, '0.00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'MAKING', NULL, '45.00', '', '2025-12-31 09:38:32', NULL, NULL, 1, '0.00', '{"name": "张三", "phone": "13800138001", "address": "北京市朝阳区建国路88号"}', 'PICKUP', NULL, NULL, '2025-12-28 00:52:54', 'WECHAT', '2025-12-26 20:16:33', NULL, NULL, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (10, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 01:29:19', '0.00', NULL, '0.00', NULL, '2025-12-31 01:34:10.575247', NULL, NULL, NULL, 'b''\x00''', 'T202512280129188DC1', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '37.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '37.00', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 01:29:19', 'ALIPAY', '2025-12-31 01:19:10.575247', 74, 0, NULL, 2, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (11, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 01:29:26', '0.00', NULL, '0.00', NULL, NULL, NULL, NULL, NULL, 'b''\x00''', 'T20251228012925F733', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '37.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '37.00', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 01:29:25', NULL, NULL, NULL, 0, NULL, 2, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (12, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 01:29:52', '0.00', NULL, '0.00', NULL, NULL, NULL, NULL, NULL, 'b''\x00''', 'T20251228012951FA54', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '37.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '37.00', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 01:29:52', NULL, NULL, NULL, 0, NULL, 2, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (13, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 01:33:24', '0.00', NULL, '0.00', NULL, NULL, NULL, NULL, NULL, 'b''\x00''', 'T20251228013324FCC1', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '37.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '37.00', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 01:33:24', NULL, NULL, NULL, 0, NULL, 2, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (14, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 01:34:10', '0.00', NULL, '0.00', NULL, NULL, NULL, NULL, NULL, 'b''\x00''', 'T202512280134091C49', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '37.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '37.00', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 01:34:09', NULL, NULL, NULL, 0, NULL, 2, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (15, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 01:47:16', '0.00', NULL, '0.00', NULL, NULL, NULL, NULL, NULL, 'b''\x00''', 'T202512280147155E75', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '37.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '37.00', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 01:47:15', NULL, NULL, NULL, 0, NULL, 2, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (16, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 17:16:24', '5.00', NULL, '0.00', NULL, NULL, NULL, NULL, NULL, 'b''\x00''', 'T20251228171624E646', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '15.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '15.00', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 17:16:24', NULL, NULL, NULL, 0, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (17, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 17:22:12', '0.00', NULL, '4.75', NULL, NULL, NULL, NULL, NULL, 'b''\x00''', 'T202512281722116C5B', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '95.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 1, '90.25', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 17:22:12', NULL, NULL, NULL, 0, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (18, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 17:32:19', '0.00', NULL, '4.75', NULL, NULL, NULL, NULL, NULL, 'b''\x00''', 'T20251228173219DED8', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '95.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 1, '90.25', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 17:32:19', NULL, NULL, NULL, 0, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (19, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 17:44:16', '5.00', NULL, '0.00', NULL, '2025-12-28 18:22:07.440311', NULL, NULL, NULL, 'b''\x00''', 'T202512281744165C55', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '15.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '15.00', NULL, 'DELIVERY', 'b''\x01''', NULL, '2025-12-28 17:44:16', 'WECHAT', '2025-12-28 18:07:07.440311', 70, 0, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (20, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 17:50:52', '5.00', NULL, '0.00', NULL, '2025-12-31 01:32:27.487919', NULL, NULL, NULL, 'b''\x00''', 'T20251228175051836C', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '15.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '15.00', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 17:50:52', 'ALIPAY', '2025-12-31 01:17:27.486918', 90, 0, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (21, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 18:07:05', '5.00', NULL, '0.00', NULL, '2025-12-28 22:04:13.525138', NULL, NULL, NULL, 'b''\x00''', 'T20251228180705953C', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '15.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '15.00', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 18:07:05', 'ALIPAY', '2025-12-28 21:49:13.525138', 36, 0, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (22, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 18:21:08', '5.00', NULL, '0.00', NULL, '2025-12-28 18:36:09.894915', NULL, NULL, NULL, 'b''\x00''', 'T20251228182107D23C', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '15.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '15.00', NULL, 'DELIVERY', 'b''\x01''', NULL, '2025-12-28 18:21:08', 'ALIPAY', '2025-12-28 18:21:09.894915', 27, 0, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (23, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 19:26:05', '5.00', NULL, '0.00', NULL, '2025-12-28 19:41:07.246704', NULL, NULL, NULL, 'b''\x00''', 'T202512281926055D71', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '111
', NULL, NULL, NULL, NULL, 'MAKING', NULL, '15.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '15.00', NULL, 'DELIVERY', 'b''\x01''', NULL, '2025-12-28 19:26:05', 'ALIPAY', '2025-12-28 19:26:07.246165', 25, 0, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (24, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 21:49:09', '0.00', NULL, '15.00', NULL, '2025-12-30 23:37:23.109355', NULL, NULL, NULL, 'b''\x00''', 'T202512282149087482', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '50.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '35.00', NULL, 'DELIVERY', 'b''\x00''', '2025-12-30 23:22:33.035307', '2025-12-28 21:49:09', 'ALIPAY', '2025-12-30 23:22:23.109355', 49, 1, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (25, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 21:50:39', '0.00', NULL, '18.75', NULL, '2025-12-28 22:05:42.046506', NULL, NULL, NULL, 'b''\x00''', 'T20251228215038D1E3', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '52.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '33.25', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 21:50:39', 'WECHAT', '2025-12-28 21:50:42.046506', 33, 0, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (26, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-28 22:06:13', '0.00', NULL, '18.28', NULL, '2025-12-28 22:22:12.443078', NULL, NULL, NULL, 'b''\x00''', 'T202512282206125355', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '59.00', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '40.72', NULL, 'DELIVERY', 'b''\x00''', NULL, '2025-12-28 22:06:13', 'ALIPAY', '2025-12-28 22:07:12.443078', 11, 0, NULL, 2, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (27, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-31 07:54:41', '0.00', NULL, '15.00', NULL, '2025-12-31 08:09:45.715063', NULL, NULL, NULL, 'b''\x00''', 'T20251231075440D95F', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 'MAKING', NULL, '90.02', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '75.02', '{"id": 1, "tag": "公司", "city": "广州市", "name": "张三", "phone": "13812345678", "detail": "珠江新城花城大道88号", "district": "天河区", "province": "广东省", "isDefault": true}', 'DELIVERY', 'b''\x00''', '2025-12-31 07:55:01.242167', '2025-12-31 07:54:41', 'ALIPAY', '2025-12-31 07:54:45.715063', 76, 1, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (28, NULL, NULL, NULL, '0.00', '0.00', NULL, NULL, NULL, '2025-12-31 09:32:27', '0.00', NULL, '22.71', NULL, '2025-12-31 09:49:45.009892', NULL, NULL, NULL, 'b''\x00''', 'T20251231093226E82B', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '备注', NULL, NULL, NULL, NULL, 'MAKING', NULL, '40.99', 'PICKUP', '2025-12-31 09:38:32', NULL, NULL, 11, '18.28', '{"id": 1, "tag": "公司", "city": "广州市", "name": "张三", "phone": "13812345678", "detail": "珠江新城花城大道88号", "district": "天河区", "province": "广东省", "isDefault": true}', 'DELIVERY', 'b''\x00''', '2025-12-31 09:35:09.066414', '2025-12-31 09:32:27', 'WECHAT', '2025-12-31 09:34:45.009892', 29, 1, NULL, 1, NULL);
INSERT INTO `orders` (`id`, `acceptor_id`, `acceptor_name`, `actual_ready_time`, `balance_discount_amount`, `balance_used`, `cancel_deadline`, `counter_number`, `coupon_id`, `created_at`, `delivery_fee`, `delivery_time_expected`, `discount_amount`, `estimated_arrival_time`, `estimated_ready_time`, `invoice_tax_number`, `invoice_title`, `invoice_type`, `is_rated`, `order_no`, `package_fee`, `pay_amount`, `pickup_code`, `pickup_time_actual`, `points_discount_amount`, `points_used`, `product_amount`, `queue_position`, `rate_deadline`, `refund_deadline`, `remark`, `rider_latitude`, `rider_longitude`, `rider_name`, `rider_phone`, `status`, `status_text`, `total_amount`, `type`, `updated_at`, `delivery_address_id`, `pickup_store_id`, `user_id`, `actual_amount`, `address_json`, `delivery_type`, `is_commented`, `last_remind_time`, `order_time`, `pay_method`, `pay_time`, `queue_number`, `remind_count`, `transaction_id`, `store_id`, `refund_reason`) VALUES (29, NULL, NULL, '2025-12-31 09:38:35.448857', '0.00', '0.00', NULL, NULL, NULL, '2025-12-31 09:34:42', '0.00', NULL, '15.00', NULL, NULL, NULL, NULL, NULL, 'b''\x00''', 'T202512310934414610', '0.00', '0.00', NULL, NULL, '0.00', 0, '0.00', NULL, NULL, NULL, '备注', NULL, NULL, NULL, NULL, 'DELIVERED', NULL, '40.99', 'PICKUP', '2025-12-31 09:38:36', NULL, NULL, 11, '25.99', '{"id": 1, "tag": "公司", "city": "广州市", "name": "张三", "phone": "13812345678", "detail": "珠江新城花城大道88号", "district": "天河区", "province": "广东省", "isDefault": true}', 'DELIVERY', 'b''\x00''', NULL, '2025-12-31 09:34:42', NULL, NULL, NULL, 0, NULL, 1, NULL);

-- ----------------------------
-- Table structure for page_configs
-- ----------------------------
DROP TABLE IF EXISTS `page_configs`;
CREATE TABLE `page_configs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_json` json NOT NULL,
  `module` varchar(50) NOT NULL,
  `page` varchar(50) NOT NULL,
  `sort_order` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for payments
-- ----------------------------
DROP TABLE IF EXISTS `payments`;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `channel` varchar(20) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `expire_time` datetime(6) DEFAULT NULL,
  `is_sandbox` bit(1) NOT NULL,
  `pay_amount` decimal(10,2) NOT NULL,
  `pay_id` varchar(100) DEFAULT NULL,
  `pay_status` varchar(20) NOT NULL,
  `pay_time` datetime(6) DEFAULT NULL,
  `pay_type` varchar(20) NOT NULL,
  `payment_url` varchar(255) DEFAULT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK81gagumt0r8y3rmudcgpbk42l` (`order_id`),
  CONSTRAINT `FK81gagumt0r8y3rmudcgpbk42l` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for point_exchange_items
-- ----------------------------
DROP TABLE IF EXISTS `point_exchange_items`;
CREATE TABLE `point_exchange_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `name` varchar(100) NOT NULL,
  `points_cost` int NOT NULL,
  `stock` int DEFAULT '0' COMMENT '库存',
  `target_id` bigint DEFAULT NULL,
  `target_type` varchar(20) NOT NULL,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `point_cost` int DEFAULT '0' COMMENT '积分成本',
  `status` varchar(20) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of point_exchange_items
-- ----------------------------
INSERT INTO `point_exchange_items` (`id`, `created_at`, `description`, `image_url`, `is_active`, `name`, `points_cost`, `stock`, `target_id`, `target_type`, `updated_at`, `point_cost`, `status`, `category`) VALUES (1, '2025-12-28 00:13:39', '好喝的奶茶，仅需100积分', '/uploads/default.jpg', 1, '积分兑换奶茶', 100, 99, NULL, 'PRODUCT', '2025-12-28 00:13:39', 100, 'ACTIVE', 'DRINK');
INSERT INTO `point_exchange_items` (`id`, `created_at`, `description`, `image_url`, `is_active`, `name`, `points_cost`, `stock`, `target_id`, `target_type`, `updated_at`, `point_cost`, `status`, `category`) VALUES (2, '2025-12-28 00:13:39', '香浓咖啡，仅需150积分', '/uploads/default.jpg', 1, '积分兑换咖啡', 150, 99, NULL, 'PRODUCT', '2025-12-28 00:13:39', 150, 'ACTIVE', 'DRINK');
INSERT INTO `point_exchange_items` (`id`, `created_at`, `description`, `image_url`, `is_active`, `name`, `points_cost`, `stock`, `target_id`, `target_type`, `updated_at`, `point_cost`, `status`, `category`) VALUES (3, '2025-12-28 00:59:14', '可兑换中杯珍珠奶茶一杯', 'http://localhost:8081/uploads/1766720332092_0bd7261b.png', 1, '经典珍珠奶茶兑换券', 0, 100, NULL, '', '2025-12-28 00:59:14', 1500, 'AVAILABLE', NULL);
INSERT INTO `point_exchange_items` (`id`, `created_at`, `description`, `image_url`, `is_active`, `name`, `points_cost`, `stock`, `target_id`, `target_type`, `updated_at`, `point_cost`, `status`, `category`) VALUES (4, '2025-12-28 00:59:14', '可兑换中杯杨枝甘露一杯', 'http://localhost:8081/uploads/1766720347887_6959c38b.png', 1, '杨枝甘露兑换券', 0, 50, NULL, '', '2025-12-28 00:59:14', 2200, 'AVAILABLE', NULL);
INSERT INTO `point_exchange_items` (`id`, `created_at`, `description`, `image_url`, `is_active`, `name`, `points_cost`, `stock`, `target_id`, `target_type`, `updated_at`, `point_cost`, `status`, `category`) VALUES (5, '2025-12-28 00:59:14', '简约时尚，环保耐用', 'http://localhost:8081/uploads/1766720377864_5dd85f12.png', 1, '奶茶店定制帆布袋', 0, 30, NULL, '', '2025-12-28 00:59:14', 3000, 'AVAILABLE', NULL);
INSERT INTO `point_exchange_items` (`id`, `created_at`, `description`, `image_url`, `is_active`, `name`, `points_cost`, `stock`, `target_id`, `target_type`, `updated_at`, `point_cost`, `status`, `category`) VALUES (6, '2025-12-28 00:59:14', '满20元可用', 'http://localhost:8081/uploads/1766720398648_959f4ae2.png', 1, '10元全场通用券', 0, 500, NULL, '', '2025-12-28 00:59:14', 1000, 'AVAILABLE', NULL);

-- ----------------------------
-- Table structure for point_exchange_record_addresses
-- ----------------------------
DROP TABLE IF EXISTS `point_exchange_record_addresses`;
CREATE TABLE `point_exchange_record_addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(50) DEFAULT NULL,
  `detail_address` varchar(200) NOT NULL,
  `district` varchar(50) DEFAULT NULL,
  `phone` varchar(20) NOT NULL,
  `postal_code` varchar(10) DEFAULT NULL,
  `province` varchar(50) DEFAULT NULL,
  `recipient_name` varchar(50) NOT NULL,
  `record_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_7w1j8rh5pwx93wy0h65qx72r8` (`record_id`),
  CONSTRAINT `FKp6v4qscphjkdhy2psk15g40d8` FOREIGN KEY (`record_id`) REFERENCES `point_exchange_records` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for point_exchange_record_items
-- ----------------------------
DROP TABLE IF EXISTS `point_exchange_record_items`;
CREATE TABLE `point_exchange_record_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `points_per_item` int NOT NULL,
  `quantity` int NOT NULL,
  `total_points` int NOT NULL,
  `item_id` bigint NOT NULL,
  `record_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqaabygb6vkvg4spunc7nxc1ud` (`item_id`),
  KEY `FKml8jq3bsa1k6kk79yw0v4qg2k` (`record_id`),
  CONSTRAINT `FKml8jq3bsa1k6kk79yw0v4qg2k` FOREIGN KEY (`record_id`) REFERENCES `point_exchange_records` (`id`),
  CONSTRAINT `FKqaabygb6vkvg4spunc7nxc1ud` FOREIGN KEY (`item_id`) REFERENCES `point_exchange_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for point_exchange_record_status_timelines
-- ----------------------------
DROP TABLE IF EXISTS `point_exchange_record_status_timelines`;
CREATE TABLE `point_exchange_record_status_timelines` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `record_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKetu91nceqxftjnr0tacbvivxx` (`record_id`),
  CONSTRAINT `FKetu91nceqxftjnr0tacbvivxx` FOREIGN KEY (`record_id`) REFERENCES `point_exchange_records` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for point_exchange_records
-- ----------------------------
DROP TABLE IF EXISTS `point_exchange_records`;
CREATE TABLE `point_exchange_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `order_no` varchar(50) NOT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `shipping_fee` decimal(10,2) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `total_items` int NOT NULL,
  `total_points` int NOT NULL,
  `tracking_number` varchar(100) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo0ax8jadtmcmqo11s34d2cdm4` (`user_id`),
  CONSTRAINT `FKo0ax8jadtmcmqo11s34d2cdm4` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for point_transactions
-- ----------------------------
DROP TABLE IF EXISTS `point_transactions`;
CREATE TABLE `point_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance_after_transaction` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `points_change` int DEFAULT NULL,
  `related_id` bigint DEFAULT NULL,
  `related_type` varchar(20) DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `user_id` bigint NOT NULL,
  `amount` int NOT NULL,
  `balance_after` int DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcqbvxpskb8p7mko50hokltu5b` (`user_id`),
  CONSTRAINT `FKcqbvxpskb8p7mko50hokltu5b` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of point_transactions
-- ----------------------------
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (1, 10, '2025-12-26 00:29:21.146033', '每日签到奖励', NULL, NULL, NULL, 'SIGN_IN', 1, 10, NULL, '每日签到奖励');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (2, 5010, '2025-12-28 10:13:16.588048', NULL, NULL, NULL, NULL, 'EARN', 1, 5000, NULL, '');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (3, 15, '2025-12-28 17:44:18.823004', NULL, NULL, NULL, NULL, 'EARN', 11, 15, NULL, '订单消费赠送: T202512281744165C55');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (4, 30, '2025-12-28 17:50:55.573048', NULL, NULL, NULL, NULL, 'EARN', 11, 15, NULL, '订单消费赠送: T202512281744165C55');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (5, 45, '2025-12-28 18:07:07.452353', NULL, NULL, NULL, NULL, 'EARN', 11, 15, NULL, '订单消费赠送: T202512281744165C55');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (6, 60, '2025-12-28 18:21:09.908008', NULL, NULL, NULL, NULL, 'EARN', 11, 15, NULL, '订单消费赠送: T20251228182107D23C');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (7, 75, '2025-12-28 19:26:07.258677', NULL, NULL, NULL, NULL, 'EARN', 11, 15, NULL, '订单消费赠送: T202512281926055D71');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (8, 85, '2025-12-28 21:34:17.021218', '每日签到奖励', NULL, NULL, NULL, 'SIGN_IN', 11, 10, NULL, '每日签到奖励');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (9, 100, '2025-12-28 21:37:40.233992', NULL, NULL, NULL, NULL, 'EARN', 11, 15, NULL, '订单消费赠送: T20251228180705953C');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (10, 115, '2025-12-28 21:49:13.542809', NULL, NULL, NULL, NULL, 'EARN', 11, 15, NULL, '订单消费赠送: T20251228180705953C');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (11, 0, '2025-12-28 21:50:38.550979', NULL, NULL, NULL, NULL, 'SPEND', 11, -115, NULL, '订单抵现: T20251228215038D1E3');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (12, 33, '2025-12-28 21:50:42.050525', NULL, NULL, NULL, NULL, 'EARN', 11, 33, NULL, '订单消费赠送: T20251228215038D1E3');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (13, 5510, '2025-12-28 21:55:05.425250', NULL, NULL, NULL, NULL, 'EARN', 1, 500, NULL, '');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (14, 0, '2025-12-28 22:06:12.733635', NULL, NULL, NULL, NULL, 'SPEND', 11, -33, NULL, '订单抵现: T202512282206125355');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (15, 40, '2025-12-28 22:06:14.678159', NULL, NULL, NULL, NULL, 'EARN', 11, 40, NULL, '订单消费赠送: T202512282206125355');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (16, 80, '2025-12-28 22:07:12.446413', NULL, NULL, NULL, NULL, 'EARN', 11, 40, NULL, '订单消费赠送: T202512282206125355');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (17, 6010, '2025-12-28 22:09:03.953865', NULL, NULL, NULL, NULL, 'EARN', 1, 500, NULL, '');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (18, 115, '2025-12-30 23:22:23.119786', NULL, NULL, NULL, NULL, 'EARN', 11, 35, NULL, '订单消费赠送: T202512282149087482');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (19, 130, '2025-12-31 01:17:27.549621', NULL, NULL, NULL, NULL, 'EARN', 11, 15, NULL, '订单消费赠送: T20251228175051836C');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (20, 167, '2025-12-31 01:18:30.624846', NULL, NULL, NULL, NULL, 'EARN', 11, 37, NULL, '订单消费赠送: T202512280129188DC1');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (21, 204, '2025-12-31 01:19:10.586028', NULL, NULL, NULL, NULL, 'EARN', 11, 37, NULL, '订单消费赠送: T202512280129188DC1');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (22, 279, '2025-12-31 07:54:45.728914', NULL, NULL, NULL, NULL, 'EARN', 11, 75, NULL, '订单消费赠送: T20251231075440D95F');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (23, 0, '2025-12-31 09:32:26.634432', NULL, NULL, NULL, NULL, 'SPEND', 11, -279, NULL, '订单抵现: T20251231093226E82B');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (24, 18, '2025-12-31 09:32:31.300954', NULL, NULL, NULL, NULL, 'EARN', 11, 18, NULL, '订单消费赠送: T20251231093226E82B');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (25, 36, '2025-12-31 09:34:45.013543', NULL, NULL, NULL, NULL, 'EARN', 11, 18, NULL, '订单消费赠送: T20251231093226E82B');
INSERT INTO `point_transactions` (`id`, `balance_after_transaction`, `created_at`, `description`, `points_change`, `related_id`, `related_type`, `type`, `user_id`, `amount`, `balance_after`, `remark`) VALUES (26, 536, '2025-12-31 09:39:06.468173', NULL, NULL, NULL, NULL, 'EARN', 11, 500, NULL, '');

-- ----------------------------
-- Table structure for print_printers
-- ----------------------------
DROP TABLE IF EXISTS `print_printers`;
CREATE TABLE `print_printers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `printer_key` varchar(64) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `sn` varchar(64) NOT NULL,
  `status` int DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `store_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5yjds5yl88ix92vwna68wq4m4` (`store_id`),
  CONSTRAINT `FK5yjds5yl88ix92vwna68wq4m4` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for print_templates
-- ----------------------------
DROP TABLE IF EXISTS `print_templates`;
CREATE TABLE `print_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `is_default` bit(1) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_allergens
-- ----------------------------
DROP TABLE IF EXISTS `product_allergens`;
CREATE TABLE `product_allergens` (
  `allergen_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`allergen_id`,`product_id`),
  KEY `FKps3ek2a1xvrk0h8dqaqn29hb1` (`product_id`),
  CONSTRAINT `FK95v23b3wm2nnbjks4up3gwvkg` FOREIGN KEY (`allergen_id`) REFERENCES `allergens` (`id`),
  CONSTRAINT `FKps3ek2a1xvrk0h8dqaqn29hb1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_allergens_map
-- ----------------------------
DROP TABLE IF EXISTS `product_allergens_map`;
CREATE TABLE `product_allergens_map` (
  `allergen_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`allergen_id`,`product_id`),
  KEY `FKsuw88cchfxh48qfelmr1gdiur` (`product_id`),
  CONSTRAINT `FKsuw88cchfxh48qfelmr1gdiur` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKt15xps1xpc9qvj57lutkag2nj` FOREIGN KEY (`allergen_id`) REFERENCES `allergens` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_customization_options
-- ----------------------------
DROP TABLE IF EXISTS `product_customization_options`;
CREATE TABLE `product_customization_options` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `icon_url` varchar(255) DEFAULT NULL,
  `is_default` bit(1) NOT NULL,
  `label` varchar(50) NOT NULL,
  `price_adjustment` decimal(10,2) NOT NULL,
  `sort_order` int DEFAULT NULL,
  `stock` int DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `value` varchar(50) NOT NULL,
  `customization_type_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK69r85lfhsqqkrvbr0pr99dl2d` (`customization_type_id`),
  CONSTRAINT `FK69r85lfhsqqkrvbr0pr99dl2d` FOREIGN KEY (`customization_type_id`) REFERENCES `product_customization_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_customization_types
-- ----------------------------
DROP TABLE IF EXISTS `product_customization_types`;
CREATE TABLE `product_customization_types` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `is_enabled` bit(1) NOT NULL,
  `is_required` bit(1) NOT NULL,
  `label` varchar(50) DEFAULT NULL,
  `max_quantity` int DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  `type_name` varchar(50) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt44rqbcge62xdoacoe2xlr64g` (`product_id`),
  CONSTRAINT `FKt44rqbcge62xdoacoe2xlr64g` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_images
-- ----------------------------
DROP TABLE IF EXISTS `product_images`;
CREATE TABLE `product_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `sort_order` int DEFAULT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqnq71xsohugpqwf3c9gxmsuy` (`product_id`),
  CONSTRAINT `FKqnq71xsohugpqwf3c9gxmsuy` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_ingredients_map
-- ----------------------------
DROP TABLE IF EXISTS `product_ingredients_map`;
CREATE TABLE `product_ingredients_map` (
  `ingredient_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`ingredient_id`,`product_id`),
  KEY `FKr65gr2gla0dp8v4b61pekoqwa` (`product_id`),
  CONSTRAINT `FKr65gr2gla0dp8v4b61pekoqwa` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKt8r944lgsosl1lj524ptjivb3` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredients` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_nutritions
-- ----------------------------
DROP TABLE IF EXISTS `product_nutritions`;
CREATE TABLE `product_nutritions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(50) NOT NULL,
  `unit` varchar(20) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `value` varchar(50) NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKch9obbsvx5n3t0jhr2sudlfbp` (`product_id`),
  CONSTRAINT `FKch9obbsvx5n3t0jhr2sudlfbp` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_options
-- ----------------------------
DROP TABLE IF EXISTS `product_options`;
CREATE TABLE `product_options` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `stock` int DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8vv4f8fru80wxocwgxwsrow61` (`product_id`),
  CONSTRAINT `FK8vv4f8fru80wxocwgxwsrow61` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_recipes
-- ----------------------------
DROP TABLE IF EXISTS `product_recipes`;
CREATE TABLE `product_recipes` (
  `quantity` decimal(10,2) NOT NULL,
  `ingredient_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`ingredient_id`,`product_id`),
  KEY `FKdhkjblef758govm787foorpxw` (`product_id`),
  CONSTRAINT `FK1mj09yf5hkjh6tjpw33rudgsr` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredients` (`id`),
  CONSTRAINT `FKdhkjblef758govm787foorpxw` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of product_recipes
-- ----------------------------
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('50.00', 1, 1);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('60.00', 2, 2);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('5.00', 4, 7);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('40.00', 5, 3);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('5.00', 8, 6);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('20.00', 9, 1);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('25.00', 9, 2);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('10.00', 9, 7);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('5.00', 10, 1);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('6.00', 10, 2);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('7.00', 11, 6);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('15.00', 13, 1);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('20.00', 13, 2);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('30.00', 14, 3);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('100.00', 15, 3);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('100.00', 15, 6);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('200.00', 15, 7);
INSERT INTO `product_recipes` (`quantity`, `ingredient_id`, `product_id`) VALUES ('80.00', 18, 3);

-- ----------------------------
-- Table structure for product_related_map
-- ----------------------------
DROP TABLE IF EXISTS `product_related_map`;
CREATE TABLE `product_related_map` (
  `main_product_id` bigint NOT NULL,
  `related_product_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `relation_type` varchar(20) DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`main_product_id`,`related_product_id`),
  KEY `FKp1n29xlhww9eqjl92vj1yv0kh` (`related_product_id`),
  CONSTRAINT `FKaniph9xcr0mrpfssfwdtdov35` FOREIGN KEY (`main_product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKp1n29xlhww9eqjl92vj1yv0kh` FOREIGN KEY (`related_product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_spec_combination_inventories
-- ----------------------------
DROP TABLE IF EXISTS `product_spec_combination_inventories`;
CREATE TABLE `product_spec_combination_inventories` (
  `created_at` datetime(6) DEFAULT NULL,
  `low_stock_threshold` int DEFAULT NULL,
  `stock` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `combination_id` bigint NOT NULL,
  PRIMARY KEY (`combination_id`),
  CONSTRAINT `FKbn0qdd8thcaks15m4fxvl7opw` FOREIGN KEY (`combination_id`) REFERENCES `product_spec_combinations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_spec_combination_items
-- ----------------------------
DROP TABLE IF EXISTS `product_spec_combination_items`;
CREATE TABLE `product_spec_combination_items` (
  `combination_id` bigint NOT NULL,
  `spec_item_id` bigint NOT NULL,
  PRIMARY KEY (`combination_id`,`spec_item_id`),
  KEY `FKt6wfo9ybuxn8hsl2wtm5plcpd` (`spec_item_id`),
  CONSTRAINT `FK6unw4g53jb6bjtpvt37bmjtgp` FOREIGN KEY (`combination_id`) REFERENCES `product_spec_combinations` (`id`),
  CONSTRAINT `FKt6wfo9ybuxn8hsl2wtm5plcpd` FOREIGN KEY (`spec_item_id`) REFERENCES `product_spec_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_spec_combination_prices
-- ----------------------------
DROP TABLE IF EXISTS `product_spec_combination_prices`;
CREATE TABLE `product_spec_combination_prices` (
  `specItemId` bigint NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `combination_id` bigint NOT NULL,
  PRIMARY KEY (`combination_id`,`specItemId`),
  CONSTRAINT `FKs6emtmchwxv1uvh0bu6c8a2rq` FOREIGN KEY (`combination_id`) REFERENCES `product_spec_combinations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_spec_combinations
-- ----------------------------
DROP TABLE IF EXISTS `product_spec_combinations`;
CREATE TABLE `product_spec_combinations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `is_default` bit(1) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6nkfr7fec12sogt0kqy1pd76y` (`product_id`),
  CONSTRAINT `FK6nkfr7fec12sogt0kqy1pd76y` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_spec_inventories
-- ----------------------------
DROP TABLE IF EXISTS `product_spec_inventories`;
CREATE TABLE `product_spec_inventories` (
  `created_at` datetime(6) DEFAULT NULL,
  `low_stock_threshold` int DEFAULT NULL,
  `stock` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `spec_item_id` bigint NOT NULL,
  PRIMARY KEY (`product_id`,`spec_item_id`),
  KEY `FKodmao76f3etw2jxf273g3h1vp` (`spec_item_id`),
  CONSTRAINT `FKodmao76f3etw2jxf273g3h1vp` FOREIGN KEY (`spec_item_id`) REFERENCES `product_spec_items` (`id`),
  CONSTRAINT `FKowcuif4rg0yyxebiuabeea5yf` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_spec_items
-- ----------------------------
DROP TABLE IF EXISTS `product_spec_items`;
CREATE TABLE `product_spec_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `icon_url` varchar(255) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `sort_order` int DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `spec_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs7vmjeul5na5b0xi53csg7f7e` (`spec_id`),
  CONSTRAINT `FKs7vmjeul5na5b0xi53csg7f7e` FOREIGN KEY (`spec_id`) REFERENCES `product_specs` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_spec_prices
-- ----------------------------
DROP TABLE IF EXISTS `product_spec_prices`;
CREATE TABLE `product_spec_prices` (
  `price_adjustment` decimal(10,2) NOT NULL,
  `product_id` bigint NOT NULL,
  `spec_item_id` bigint NOT NULL,
  PRIMARY KEY (`product_id`,`spec_item_id`),
  KEY `FKl569reeft2r4npx423cf89pj0` (`spec_item_id`),
  CONSTRAINT `FK3sn420dee36ypq8o7x8qxc1gf` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKl569reeft2r4npx423cf89pj0` FOREIGN KEY (`spec_item_id`) REFERENCES `product_spec_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for product_specs
-- ----------------------------
DROP TABLE IF EXISTS `product_specs`;
CREATE TABLE `product_specs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_required` bit(1) DEFAULT NULL,
  `max_select` int DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  `sort_order` int DEFAULT NULL,
  `type` varchar(20) NOT NULL COMMENT 'SWEETNESS, TEMPERATURE',
  `updated_at` datetime(6) DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `is_default` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FKpg8s24i6nwo81ab7awlr3tglk` (`product_id`),
  CONSTRAINT `FKpg8s24i6nwo81ab7awlr3tglk` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `calories` varchar(20) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `default_sweetness` varchar(20) DEFAULT NULL,
  `default_temperature` varchar(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `detail_html` varchar(255) DEFAULT NULL,
  `favorite_count` int NOT NULL,
  `is_active` bit(1) NOT NULL,
  `is_hot` bit(1) NOT NULL,
  `is_new` bit(1) NOT NULL,
  `is_recommend` bit(1) NOT NULL,
  `main_image_url` varchar(255) DEFAULT NULL,
  `monthly_sales` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `original_price` decimal(10,2) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `rating` decimal(2,1) DEFAULT '5.0' COMMENT '商品评分-设置默认值以兼容新增逻辑',
  `rating_count` int NOT NULL,
  `sales` int NOT NULL,
  `shelf_life` varchar(50) DEFAULT NULL,
  `stock` int NOT NULL,
  `storage_method` varchar(100) DEFAULT NULL,
  `subtitle` varchar(255) DEFAULT NULL,
  `sugar_content` varchar(20) DEFAULT NULL,
  `support_sweetness` json DEFAULT NULL,
  `support_temperature` json DEFAULT NULL,
  `tags` json DEFAULT NULL,
  `unit` varchar(20) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `category_id` bigint DEFAULT NULL,
  `alert_threshold` int DEFAULT NULL,
  `cost_price` decimal(10,2) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_member_exclusive` bit(1) DEFAULT NULL,
  `member_price` decimal(10,2) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `is_recommended` bit(1) DEFAULT NULL,
  `sku` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fhmd06dsmj6k0n90swsh8ie9g` (`sku`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (6, NULL, '2025-12-26 20:04:01', NULL, NULL, '经典台式风味，Q弹珍珠', NULL, 0, 'b''\x01''', 'b''\x01''', 'b''\x01''', 'b''\x01''', '/uploads/1767108287276_07f7530d.png', 0, '珍珠奶茶', NULL, '15.00', '0.0', 0, 1200, NULL, 10000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.520469', 3, 5, '9.29', 'http://localhost:8081/uploads/1766754451983_0376fb33.jpg', 'b''\x00''', '13.50', 1, 'b''\x01''', NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (7, NULL, '2025-12-26 20:04:01', NULL, NULL, '超大颗波霸，口感丰富', NULL, 0, 'b''\x01''', 'b''\x01''', 'b''\x01''', 'b''\x01''', '/uploads/1767108294471_9a945db6.jpg', 0, '波霸奶茶', NULL, '16.00', '0.0', 0, 800, NULL, 10000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.527574', 2, 10, '6.60', 'http://localhost:8081/uploads/1766754463318_bd6f4f3d.jpg', 'b''\x00''', '14.40', 1, 'b''\x01''', NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (8, NULL, '2025-12-26 20:04:01', NULL, NULL, '新鲜芒果配西柚，酸甜适口', NULL, 0, 'b''\x01''', 'b''\x01''', 'b''\x01''', 'b''\x01''', '/uploads/1767108304939_dc831110.jpg', 0, '杨枝甘露', NULL, '22.00', '0.0', 0, 2500, NULL, 10000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.520469', 3, 11, '8.80', 'http://localhost:8081/uploads/1766754468302_ea193fa2.jpg', 'b''\x00''', '19.80', 1, 'b''\x01''', NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (9, NULL, '2025-12-26 20:04:01', NULL, NULL, '手剥新鲜葡萄，搭配芝士奶盖', NULL, 0, 'b''\x01''', 'b''\x01''', 'b''\x01''', 'b''\x01''', '/uploads/1767108317696_9d395ece.jpg', 0, '多肉葡萄', NULL, '28.00', '0.0', 0, 3000, NULL, 5000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.523300', 3, 5, '11.20', 'http://localhost:8081/uploads/1766754474756_76b4b99f.jpg', 'b''\x01''', '25.00', 1, 'b''\x01''', NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (10, NULL, '2025-12-26 20:04:01', NULL, NULL, '清甜生椰水遇上浓缩咖啡', NULL, 0, 'b''\x01''', 'b''\x01''', 'b''\x01''', 'b''\x01''', '/uploads/1767108322608_e8d3e5c0.jpg', 0, '生椰拿铁', NULL, '18.00', '0.0', 0, 1500, NULL, 50000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.522465', 4, 5, '7.20', 'http://localhost:8081/uploads/1766754480741_abf0f154.jpg', 'b''\x00''', '16.20', 1, 'b''\x01''', NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (11, NULL, '2025-12-26 20:16:03', NULL, NULL, '经典台式风味，Q弹珍珠', NULL, 0, 'b''\x01''', 'b''\x00''', 'b''\x00''', 'b''\x00''', '/uploads/1767108329337_03ba19ca.webp', 0, '珍珠奶茶', NULL, '15.00', '0.0', 0, 1200, NULL, 1000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.520469', 2, 10, '6.00', 'http://localhost:8081/uploads/1766830521730_cb2921c7.jpg', 'b''\x00''', '13.50', 1, NULL, NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (12, NULL, '2025-12-26 20:16:03', NULL, NULL, '超大颗波霸，口感丰富', NULL, 0, 'b''\x01''', 'b''\x00''', 'b''\x00''', 'b''\x00''', '/uploads/1767108339594_31b5539e.jpg', 0, '波霸奶茶', NULL, '16.00', '0.0', 0, 800, NULL, 1000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.574760', 2, 10, '6.40', 'http://localhost:8081/uploads/1766830527195_860483d7.jpg', 'b''\x00''', '14.40', 1, NULL, NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (13, NULL, '2025-12-26 20:16:03', NULL, NULL, '新鲜芒果配西柚，酸甜适口', NULL, 0, 'b''\x01''', 'b''\x00''', 'b''\x00''', 'b''\x00''', '/uploads/1767108345920_a4100dd1.jpg', 0, '杨枝甘露', NULL, '22.00', '0.0', 0, 2500, NULL, 1000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.578774', 3, 10, '8.80', 'http://localhost:8081/uploads/1766830549759_fa929e2e.jpg', 'b''\x00''', '19.80', 1, NULL, NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (14, NULL, '2025-12-26 20:16:03', NULL, NULL, '手剥新鲜葡萄，搭配芝士奶盖', NULL, 0, 'b''\x01''', 'b''\x00''', 'b''\x00''', 'b''\x00''', '/uploads/1767108357600_df166349.jpg', 0, '多肉葡萄', NULL, '28.00', '0.0', 0, 3000, NULL, 1000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.578774', 3, 10, '11.20', 'http://localhost:8081/uploads/1766830559942_a5baa340.jpg', 'b''\x01''', '25.00', 1, NULL, NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (15, NULL, '2025-12-26 20:16:03', NULL, NULL, '清甜生椰水遇上浓缩咖啡', NULL, 0, 'b''\x01''', 'b''\x00''', 'b''\x00''', 'b''\x00''', '/uploads/1767108351246_de7800e0.jpg', 0, '生椰拿铁', NULL, '18.00', '0.0', 0, 1500, NULL, 1000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.578774', 4, 1, '7.20', 'http://localhost:8081/uploads/1766830554350_ce675561.jpg', 'b''\x00''', '16.20', 1, NULL, NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (16, NULL, '2025-12-26 20:20:57', NULL, NULL, '经典台式风味，Q弹珍珠', NULL, 0, 'b''\x01''', 'b''\x00''', 'b''\x00''', 'b''\x00''', '/uploads/1767108364253_29e82b57.jpg', 0, '珍珠奶茶2', NULL, '15.00', '0.0', 0, 1200, NULL, 1000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.578774', 11, 10, '6.00', 'http://localhost:8081/uploads/1766830566090_6430ccf3.jpg', 'b''\x00''', '13.50', 1, NULL, NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (17, NULL, '2025-12-26 20:20:57', NULL, NULL, '超大颗波霸，口感丰富', NULL, 0, 'b''\x01''', 'b''\x00''', 'b''\x00''', 'b''\x00''', '/uploads/1767108377014_1c08ecd2.jpg', 0, '波霸奶茶', NULL, '16.00', '0.0', 0, 800, NULL, 1000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.578774', 6, 10, '6.40', 'http://localhost:8081/uploads/1766830571227_57ad73bf.jpg', 'b''\x00''', '14.40', 1, NULL, NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (18, NULL, '2025-12-26 20:20:57', NULL, NULL, '新鲜芒果配西柚，酸甜适口', NULL, 0, 'b''\x01''', 'b''\x00''', 'b''\x00''', 'b''\x00''', '/uploads/1767108396948_1cd1eedd.jpg', 0, '杨枝甘露', NULL, '22.00', '0.0', 0, 2500, NULL, 10000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.619759', 3, 10, '8.80', 'http://localhost:8081/uploads/1766830575245_56513c44.jpg', 'b''\x00''', '19.80', 1, NULL, NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (19, NULL, '2025-12-26 20:20:57', NULL, NULL, '手剥新鲜葡萄，搭配芝士奶盖', NULL, 0, 'b''\x01''', 'b''\x00''', 'b''\x00''', 'b''\x00''', '/uploads/1767108404605_c0afe9b2.jpg', 0, '多肉葡萄', NULL, '28.00', '0.0', 0, 3000, NULL, 1000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.619759', 3, 10, '11.20', 'http://localhost:8081/uploads/1766830579574_7b43314e.jpg', 'b''\x01''', '25.00', 1, NULL, NULL);
INSERT INTO `products` (`id`, `calories`, `created_at`, `default_sweetness`, `default_temperature`, `description`, `detail_html`, `favorite_count`, `is_active`, `is_hot`, `is_new`, `is_recommend`, `main_image_url`, `monthly_sales`, `name`, `original_price`, `price`, `rating`, `rating_count`, `sales`, `shelf_life`, `stock`, `storage_method`, `subtitle`, `sugar_content`, `support_sweetness`, `support_temperature`, `tags`, `unit`, `updated_at`, `category_id`, `alert_threshold`, `cost_price`, `image_url`, `is_member_exclusive`, `member_price`, `status`, `is_recommended`, `sku`) VALUES (20, NULL, '2025-12-26 20:20:57', NULL, NULL, '清甜生椰水遇上浓缩咖啡', NULL, 0, 'b''\x01''', 'b''\x00''', 'b''\x00''', 'b''\x00''', '/uploads/1767108411404_6c7fa64f.jpg', 0, '生椰拿铁', NULL, '18.00', '0.0', 0, 1500, NULL, 5000, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2025-12-31 09:37:15.632091', 4, 5, '7.20', 'http://localhost:8081/uploads/1766830587128_6c483bfd.jpg', 'b''\x00''', '16.20', 1, NULL, NULL);

-- ----------------------------
-- Table structure for promotions
-- ----------------------------
DROP TABLE IF EXISTS `promotions`;
CREATE TABLE `promotions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `button_text` varchar(50) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `end_time` datetime(6) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `start_time` datetime(6) NOT NULL,
  `subtitle` varchar(255) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL COMMENT '活动标题',
  `type` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `value` decimal(10,2) DEFAULT NULL,
  `name` varchar(100) NOT NULL COMMENT '活动名称',
  `rules_json` json NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of promotions
-- ----------------------------
INSERT INTO `promotions` (`id`, `button_text`, `created_at`, `end_time`, `image_url`, `is_active`, `start_time`, `subtitle`, `title`, `type`, `updated_at`, `value`, `name`, `rules_json`) VALUES (1, NULL, '2025-12-28 20:55:37.245522', '2026-01-09 20:52:00', NULL, 'b''\x01''', '2025-12-07 20:52:00', NULL, NULL, 'FULL_REDUCE', '2025-12-28 21:07:52.754014', NULL, '很好的活动', '{"reduce": 15, "threshold": 30}');
INSERT INTO `promotions` (`id`, `button_text`, `created_at`, `end_time`, `image_url`, `is_active`, `start_time`, `subtitle`, `title`, `type`, `updated_at`, `value`, `name`, `rules_json`) VALUES (2, NULL, '2025-12-31 00:44:15.626306', '2026-01-15 00:44:00', NULL, 'b''\x00''', '2025-12-17 00:44:00', NULL, NULL, 'PROMOTION_DISCOUNT', '2025-12-31 00:50:57.469788', NULL, '好活动', '{}');

-- ----------------------------
-- Table structure for quick_entries
-- ----------------------------
DROP TABLE IF EXISTS `quick_entries`;
CREATE TABLE `quick_entries` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `badge` varchar(20) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `icon_url` varchar(255) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `name` varchar(50) NOT NULL,
  `sort_order` int DEFAULT NULL,
  `target_id` bigint DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for review_tags
-- ----------------------------
DROP TABLE IF EXISTS `review_tags`;
CREATE TABLE `review_tags` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_212305laxs8mxe3m6y3nteagj` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for search_keywords
-- ----------------------------
DROP TABLE IF EXISTS `search_keywords`;
CREATE TABLE `search_keywords` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `count` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `keyword` varchar(100) NOT NULL,
  `type` varchar(20) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_b1ik45vt49p9i8c4bkox82mrf` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for store_business_hours
-- ----------------------------
DROP TABLE IF EXISTS `store_business_hours`;
CREATE TABLE `store_business_hours` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `close_time` time(6) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `day_of_week` int NOT NULL,
  `is_open` bit(1) NOT NULL,
  `open_time` time(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `store_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK80xirqrvvoqvaon5435d6de0q` (`store_id`),
  CONSTRAINT `FK80xirqrvvoqvaon5435d6de0q` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for store_images
-- ----------------------------
DROP TABLE IF EXISTS `store_images`;
CREATE TABLE `store_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `sort_order` int DEFAULT NULL,
  `store_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6s8ktg0dkmr6w1wc8jqwvk6tu` (`store_id`),
  CONSTRAINT `FK6s8ktg0dkmr6w1wc8jqwvk6tu` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for store_services
-- ----------------------------
DROP TABLE IF EXISTS `store_services`;
CREATE TABLE `store_services` (
  `service_type` varchar(20) NOT NULL,
  `store_id` bigint NOT NULL,
  PRIMARY KEY (`service_type`,`store_id`),
  KEY `FKtj4njgbt88gcr7oefl7mdmnti` (`store_id`),
  CONSTRAINT `FKtj4njgbt88gcr7oefl7mdmnti` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for store_special_date
-- ----------------------------
DROP TABLE IF EXISTS `store_special_date`;
CREATE TABLE `store_special_date` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `close_time` time(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_open` bit(1) NOT NULL,
  `open_time` time(6) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `special_date` date NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `store_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5nhljnbl2ikgleghw44mjcph9` (`store_id`),
  CONSTRAINT `FK5nhljnbl2ikgleghw44mjcph9` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for store_special_dates
-- ----------------------------
DROP TABLE IF EXISTS `store_special_dates`;
CREATE TABLE `store_special_dates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `business_hours` varchar(50) DEFAULT NULL,
  `date` date NOT NULL,
  `is_closed` bit(1) DEFAULT NULL,
  `store_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKibilxxwj3ydmtaxbxj3kx1gpj` (`store_id`),
  CONSTRAINT `FKibilxxwj3ydmtaxbxj3kx1gpj` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for store_tags
-- ----------------------------
DROP TABLE IF EXISTS `store_tags`;
CREATE TABLE `store_tags` (
  `store_id` bigint NOT NULL,
  `tag_name` varchar(50) NOT NULL,
  PRIMARY KEY (`store_id`,`tag_name`),
  CONSTRAINT `FKkn3jxcg61hmbljc4fxa5xkxek` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for stores
-- ----------------------------
DROP TABLE IF EXISTS `stores`;
CREATE TABLE `stores` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) NOT NULL,
  `business_hours` varchar(100) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `current_wait_time` int NOT NULL,
  `delivery_fee` decimal(10,2) DEFAULT '0.00',
  `is_active` tinyint(1) DEFAULT '1',
  `latitude` decimal(10,8) DEFAULT '0.00000000' COMMENT '纬度',
  `longitude` decimal(11,8) DEFAULT '0.00000000' COMMENT '经度',
  `minimum_order_amount` decimal(10,2) DEFAULT '0.00' COMMENT '最低起送价(冗余字段修复)',
  `name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `rating` decimal(3,2) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'OPEN' COMMENT '门店状态: OPEN, CLOSED, MAINTENANCE',
  `updated_at` datetime(6) NOT NULL,
  `address_json` json DEFAULT NULL,
  `business_hours_json` json DEFAULT NULL,
  `code` varchar(20) DEFAULT NULL,
  `config_json` json DEFAULT NULL,
  `delivery_radius` int DEFAULT '0',
  `is_auto_accept` bit(1) DEFAULT NULL,
  `is_online_payment` bit(1) DEFAULT NULL,
  `manager_name` varchar(50) DEFAULT NULL,
  `manager_phone` varchar(20) DEFAULT NULL,
  `min_order_amount` decimal(10,2) DEFAULT '0.00' COMMENT '最低起送价',
  `business_status` int DEFAULT '1' COMMENT '营业状态: 1-营业中, 0-休息中',
  `is_auto_receipt` tinyint(1) DEFAULT '1',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `close_time` varchar(255) DEFAULT NULL,
  `open_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ma58utc2vht2ri6cdft9hjtr4` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of stores
-- ----------------------------
INSERT INTO `stores` (`id`, `address`, `business_hours`, `created_at`, `current_wait_time`, `delivery_fee`, `is_active`, `latitude`, `longitude`, `minimum_order_amount`, `name`, `phone`, `rating`, `status`, `updated_at`, `address_json`, `business_hours_json`, `code`, `config_json`, `delivery_radius`, `is_auto_accept`, `is_online_payment`, `manager_name`, `manager_phone`, `min_order_amount`, `business_status`, `is_auto_receipt`, `province`, `city`, `district`, `close_time`, `open_time`) VALUES (1, '贵州大学', NULL, '2025-12-26 19:07:31.564893', 0, '0.00', 1, '23.12916200', '113.26443400', '0.00', '蔡徐坤奶茶', '11111111111', '5.00', 'OPEN', '2025-12-31 09:40:15.240302', NULL, NULL, '001', NULL, 0, 'b''\x00''', 'b''\x01''', NULL, NULL, '0.00', 1, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `stores` (`id`, `address`, `business_hours`, `created_at`, `current_wait_time`, `delivery_fee`, `is_active`, `latitude`, `longitude`, `minimum_order_amount`, `name`, `phone`, `rating`, `status`, `updated_at`, `address_json`, `business_hours_json`, `code`, `config_json`, `delivery_radius`, `is_auto_accept`, `is_online_payment`, `manager_name`, `manager_phone`, `min_order_amount`, `business_status`, `is_auto_receipt`, `province`, `city`, `district`, `close_time`, `open_time`) VALUES (2, '北京市朝阳区建国路88号', '09:00-22:00', '2025-12-26 20:16:07', 0, '5.00', 1, NULL, NULL, '0.00', '蜜雪冰城旗舰店', '010-88888888', '4.80', 'OPEN', '2025-12-28 22:09:45.092832', NULL, NULL, 'STORE001', NULL, 0, NULL, NULL, NULL, NULL, '20.00', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `stores` (`id`, `address`, `business_hours`, `created_at`, `current_wait_time`, `delivery_fee`, `is_active`, `latitude`, `longitude`, `minimum_order_amount`, `name`, `phone`, `rating`, `status`, `updated_at`, `address_json`, `business_hours_json`, `code`, `config_json`, `delivery_radius`, `is_auto_accept`, `is_online_payment`, `manager_name`, `manager_phone`, `min_order_amount`, `business_status`, `is_auto_receipt`, `province`, `city`, `district`, `close_time`, `open_time`) VALUES (3, '深圳市南山区科技园南区', '08:00-23:00', '2025-12-26 20:16:07', 0, '3.00', 1, NULL, NULL, '0.00', '蜜雪冰城深圳分店', '0755-66666666', '4.90', 'OPEN', '2025-12-26 20:16:07', NULL, NULL, 'STORE002', NULL, 0, NULL, NULL, NULL, NULL, '15.00', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `stores` (`id`, `address`, `business_hours`, `created_at`, `current_wait_time`, `delivery_fee`, `is_active`, `latitude`, `longitude`, `minimum_order_amount`, `name`, `phone`, `rating`, `status`, `updated_at`, `address_json`, `business_hours_json`, `code`, `config_json`, `delivery_radius`, `is_auto_accept`, `is_online_payment`, `manager_name`, `manager_phone`, `min_order_amount`, `business_status`, `is_auto_receipt`, `province`, `city`, `district`, `close_time`, `open_time`) VALUES (6, '上海市青浦区', NULL, '2025-12-27 09:32:11.533187', 0, '0.00', 1, '23.12916200', '113.26443400', '0.00', '美味奶茶', '000000000000', '5.00', 'OPEN', '2025-12-27 09:32:11.533187', NULL, NULL, '002', NULL, 0, 'b''\x00''', 'b''\x01''', NULL, NULL, '0.00', 1, 1, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `stores` (`id`, `address`, `business_hours`, `created_at`, `current_wait_time`, `delivery_fee`, `is_active`, `latitude`, `longitude`, `minimum_order_amount`, `name`, `phone`, `rating`, `status`, `updated_at`, `address_json`, `business_hours_json`, `code`, `config_json`, `delivery_radius`, `is_auto_accept`, `is_online_payment`, `manager_name`, `manager_phone`, `min_order_amount`, `business_status`, `is_auto_receipt`, `province`, `city`, `district`, `close_time`, `open_time`) VALUES (7, '贵州大学', NULL, '2025-12-28 21:52:32.267758', 0, '0.00', 1, '23.12916200', '113.26443400', '0.00', '新店1', '11111111111', '5.00', 'CLOSED', '2025-12-28 21:52:32.267758', NULL, NULL, '22000', NULL, 0, 'b''\x00''', 'b''\x01''', NULL, NULL, '0.00', 1, 1, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_announcements
-- ----------------------------
DROP TABLE IF EXISTS `sys_announcements`;
CREATE TABLE `sys_announcements` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `link_url` varchar(255) DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_announcements
-- ----------------------------
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (1, '恭喜您升级为 银卡会员！', '2025-12-28 16:10:43.825956', NULL, NULL, 'b''\x01''', NULL, '2025-12-28 16:10:43.820194', '会员等级变更', '2025-12-28 16:10:43.825956');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (2, '恭喜您升级为 金卡会员！', '2025-12-28 21:55:01.092269', NULL, NULL, 'b''\x01''', NULL, '2025-12-28 21:55:01.086346', '会员等级变更', '2025-12-28 21:55:01.092269');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (3, '恭喜您升级为 银卡会员！', '2025-12-28 22:08:59.416692', NULL, NULL, 'b''\x01''', NULL, '2025-12-28 22:08:59.416157', '会员等级变更', '2025-12-28 22:08:59.416692');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (4, '恭喜您升级为 银卡会员！', '2025-12-30 22:20:45.478616', NULL, NULL, 'b''\x01''', NULL, '2025-12-30 22:20:45.468588', '会员等级变更', '2025-12-30 22:20:45.478616');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (5, '恭喜您升级为 普通会员！', '2025-12-30 22:20:48.839631', NULL, NULL, 'b''\x01''', NULL, '2025-12-30 22:20:48.839632', '会员等级变更', '2025-12-30 22:20:48.839631');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (6, '恭喜您升级为 金卡会员！', '2025-12-30 22:20:51.295775', NULL, NULL, 'b''\x01''', NULL, '2025-12-30 22:20:51.295775', '会员等级变更', '2025-12-30 22:20:51.295775');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (7, '恭喜您升级为 普通会员！', '2025-12-30 22:21:09.895427', NULL, NULL, 'b''\x01''', NULL, '2025-12-30 22:21:09.895427', '会员等级变更', '2025-12-30 22:21:09.895427');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (8, '恭喜您升级为 金卡会员！', '2025-12-30 22:30:13.135936', NULL, NULL, 'b''\x01''', NULL, '2025-12-30 22:30:13.132847', '会员等级变更', '2025-12-30 22:30:13.135936');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (9, '恭喜您升级为 银卡会员！', '2025-12-30 22:32:58.565479', NULL, NULL, 'b''\x01''', NULL, '2025-12-30 22:32:58.555152', '会员等级变更', '2025-12-30 22:32:58.565479');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (10, '恭喜您升级为 金卡会员！', '2025-12-30 22:46:51.503718', NULL, NULL, 'b''\x01''', NULL, '2025-12-30 22:46:51.503719', '会员等级变更', '2025-12-30 22:46:51.503718');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (11, '恭喜您升级为 黄金会员！', '2025-12-30 23:38:34.914548', NULL, NULL, 'b''\x01''', NULL, '2025-12-30 23:38:34.885489', '会员等级变更', '2025-12-30 23:38:34.914548');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (12, '恭喜您升级为 黄金会员！', '2025-12-31 07:59:13.787471', NULL, NULL, 'b''\x01''', NULL, '2025-12-31 07:59:13.783934', '会员等级变更', '2025-12-31 07:59:13.787471');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (13, '恭喜您升级为 钻石会员！', '2025-12-31 09:14:19.368468', NULL, NULL, 'b''\x01''', NULL, '2025-12-31 09:14:19.356200', '会员等级变更', '2025-12-31 09:14:19.368468');
INSERT INTO `sys_announcements` (`id`, `content`, `created_at`, `end_time`, `image_url`, `is_active`, `link_url`, `start_time`, `title`, `updated_at`) VALUES (14, '恭喜您升级为 钻石会员！', '2025-12-31 09:39:01.494539', NULL, NULL, 'b''\x01''', NULL, '2025-12-31 09:39:01.491360', '会员等级变更', '2025-12-31 09:39:01.494539');

-- ----------------------------
-- Table structure for sys_backups
-- ----------------------------
DROP TABLE IF EXISTS `sys_backups`;
CREATE TABLE `sys_backups` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `file_size` bigint DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_backups
-- ----------------------------
INSERT INTO `sys_backups` (`id`, `created_at`, `file_name`, `file_path`, `file_size`, `status`) VALUES (1, '2025-12-28 01:35:35.020234', 'backup_1766856934992.sql', '/backups/backup_1766856934992.sql', 2097152, 'COMPLETED');
INSERT INTO `sys_backups` (`id`, `created_at`, `file_name`, `file_path`, `file_size`, `status`) VALUES (2, '2025-12-28 22:09:50.341210', 'backup_1766930990337.sql', '/backups/backup_1766930990337.sql', 2097152, 'COMPLETED');

-- ----------------------------
-- Table structure for sys_operation_logs
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_logs`;
CREATE TABLE `sys_operation_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(50) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `module` varchar(50) DEFAULT NULL,
  `params_json` text,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_operation_logs
-- ----------------------------
INSERT INTO `sys_operation_logs` (`id`, `action`, `created_at`, `ip`, `module`, `params_json`, `user_id`, `username`) VALUES (1, 'RESET_PASSWORD', '2025-12-28 16:56:51.036578', NULL, 'SYSTEM', '重置员工密码: 136039994107', 7, 'uuu');
INSERT INTO `sys_operation_logs` (`id`, `action`, `created_at`, `ip`, `module`, `params_json`, `user_id`, `username`) VALUES (2, 'RESET_PASSWORD', '2025-12-28 16:56:56.145870', NULL, 'SYSTEM', '重置员工密码: admin888', 7, 'uuu');
INSERT INTO `sys_operation_logs` (`id`, `action`, `created_at`, `ip`, `module`, `params_json`, `user_id`, `username`) VALUES (3, 'RESET_PASSWORD', '2025-12-28 19:27:04.303390', NULL, 'SYSTEM', '重置员工密码: test', 7, 'uuu');

-- ----------------------------
-- Table structure for sys_permissions
-- ----------------------------
DROP TABLE IF EXISTS `sys_permissions`;
CREATE TABLE `sys_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `type` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for sys_role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permissions`;
CREATE TABLE `sys_role_permissions` (
  `permission_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`permission_id`,`role_id`),
  KEY `FKn16g6ssysj9ncfabu2dj0s5u8` (`role_id`),
  CONSTRAINT `FKa6lycvisbrxexjyanm5grngc0` FOREIGN KEY (`permission_id`) REFERENCES `sys_permissions` (`id`),
  CONSTRAINT `FKn16g6ssysj9ncfabu2dj0s5u8` FOREIGN KEY (`role_id`) REFERENCES `sys_roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for sys_roles
-- ----------------------------
DROP TABLE IF EXISTS `sys_roles`;
CREATE TABLE `sys_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_pjdyd7mxv3d909iq031ko99xv` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_roles
-- ----------------------------
INSERT INTO `sys_roles` (`id`, `code`, `description`, `name`) VALUES (1, 'ADMIN', '拥有系统所有权限', '超级管理员');
INSERT INTO `sys_roles` (`id`, `code`, `description`, `name`) VALUES (2, 'SUPER_ADMIN', '拥有所有权限', '超级管理员');
INSERT INTO `sys_roles` (`id`, `code`, `description`, `name`) VALUES (3, 'STORE_MANAGER', '管理门店', '店长');
INSERT INTO `sys_roles` (`id`, `code`, `description`, `name`) VALUES (4, 'STAFF', '处理订单', '店员');

-- ----------------------------
-- Table structure for sys_user_roles
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_roles`;
CREATE TABLE `sys_user_roles` (
  `role_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`user_id`),
  KEY `FK9508r3u91qjr7onvpyf6203p8` (`user_id`),
  CONSTRAINT `FK9508r3u91qjr7onvpyf6203p8` FOREIGN KEY (`user_id`) REFERENCES `sys_users` (`id`),
  CONSTRAINT `FKmkhkspb26v193sxs3dhgu6s2p` FOREIGN KEY (`role_id`) REFERENCES `sys_roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_user_roles
-- ----------------------------
INSERT INTO `sys_user_roles` (`role_id`, `user_id`) VALUES (1, 1);
INSERT INTO `sys_user_roles` (`role_id`, `user_id`) VALUES (2, 2);
INSERT INTO `sys_user_roles` (`role_id`, `user_id`) VALUES (1, 6);
INSERT INTO `sys_user_roles` (`role_id`, `user_id`) VALUES (1, 7);

-- ----------------------------
-- Table structure for sys_users
-- ----------------------------
DROP TABLE IF EXISTS `sys_users`;
CREATE TABLE `sys_users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avatar` varchar(200) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `last_login_ip` varchar(45) DEFAULT NULL,
  `last_login_time` datetime(6) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `position` varchar(50) DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_lgllb14jd9kcm09b9enkkbh0q` (`username`),
  KEY `FK_sys_user_store` (`store_id`),
  CONSTRAINT `FK4xxetc57ogrpmxpupktbyfs2m` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`),
  CONSTRAINT `FK_sys_user_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_users
-- ----------------------------
INSERT INTO `sys_users` (`id`, `avatar`, `created_at`, `email`, `last_login_ip`, `last_login_time`, `password_hash`, `phone`, `real_name`, `status`, `updated_at`, `username`, `position`, `store_id`, `password`) VALUES (1, NULL, '2025-12-26 16:06:37', NULL, NULL, NULL, '$2a$10$9ClAZAJ5ztBY6a0VoFKKz.Cx0w1m2n49J7H4EsT2KCqo9K3rtLCii', '136039994107', '我', 'ACTIVE', '2025-12-28 21:47:38.248522', '136039994107', NULL, 1, NULL);
INSERT INTO `sys_users` (`id`, `avatar`, `created_at`, `email`, `last_login_ip`, `last_login_time`, `password_hash`, `phone`, `real_name`, `status`, `updated_at`, `username`, `position`, `store_id`, `password`) VALUES (2, NULL, '2025-12-26 16:51:22.192443', NULL, NULL, NULL, '$2a$10$YiNV40t3IMnB8NJu1JeUEuGrttCYFzNBl7WuhU2rEhMoHVJg8Mb1W', '13603994107', 'test', 'ACTIVE', '2025-12-28 21:55:18.634794', 'test', NULL, 7, NULL);
INSERT INTO `sys_users` (`id`, `avatar`, `created_at`, `email`, `last_login_ip`, `last_login_time`, `password_hash`, `phone`, `real_name`, `status`, `updated_at`, `username`, `position`, `store_id`, `password`) VALUES (4, NULL, '2025-12-26 17:12:23.976339', NULL, NULL, NULL, '$2a$10$8pVXPNyUS/KswTTje62xEuhjBJnABXFXe76bub5i0U0e8gy58/49O', '13966740343', 'adm_740343', 'ACTIVE', '2025-12-26 17:12:23.976339', 'adm_740343', NULL, NULL, NULL);
INSERT INTO `sys_users` (`id`, `avatar`, `created_at`, `email`, `last_login_ip`, `last_login_time`, `password_hash`, `phone`, `real_name`, `status`, `updated_at`, `username`, `position`, `store_id`, `password`) VALUES (5, NULL, '2025-12-26 17:13:52.796548', NULL, NULL, NULL, '$2a$10$YXlbpK9M8/OCUa939qO6pO/gh8B5a4uqlL93kqQTcipYgOYYC0LbK', '13888888888', 'admin888', 'ACTIVE', '2025-12-28 16:56:56.138366', 'admin888', NULL, NULL, NULL);
INSERT INTO `sys_users` (`id`, `avatar`, `created_at`, `email`, `last_login_ip`, `last_login_time`, `password_hash`, `phone`, `real_name`, `status`, `updated_at`, `username`, `position`, `store_id`, `password`) VALUES (6, NULL, '2025-12-26 17:21:04.140285', NULL, NULL, NULL, '$2a$10$S8DWKNAsWoNyZ/.5bGfKl.1RSxHPHUa.epSbX69ZgdkSP.Ooi0X6.', '13966740864', 'adm_740864', 'ACTIVE', '2025-12-26 17:21:04.140285', 'adm_740864', NULL, NULL, NULL);
INSERT INTO `sys_users` (`id`, `avatar`, `created_at`, `email`, `last_login_ip`, `last_login_time`, `password_hash`, `phone`, `real_name`, `status`, `updated_at`, `username`, `position`, `store_id`, `password`) VALUES (7, NULL, '2025-12-26 18:13:00.685770', NULL, NULL, NULL, '$2a$10$WgjVPOeogjF.PWdDHtJ4veACXCs16Qzozc3qUqcOVizZkuzqviXli', '13603994110', 'uuu', 'ACTIVE', '2025-12-26 18:13:00.685770', 'uuu', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for system_configs
-- ----------------------------
DROP TABLE IF EXISTS `system_configs`;
CREATE TABLE `system_configs` (
  `key` varchar(100) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `value_type` varchar(20) NOT NULL,
  `config_key` varchar(100) NOT NULL,
  `config_value` text,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user_addresses
-- ----------------------------
DROP TABLE IF EXISTS `user_addresses`;
CREATE TABLE `user_addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(50) NOT NULL COMMENT '城市',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `detail` varchar(255) NOT NULL,
  `district` varchar(50) NOT NULL COMMENT '区县',
  `is_default` tinyint(1) DEFAULT '0',
  `label` varchar(50) DEFAULT NULL,
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `name` varchar(100) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `postal_code` varchar(10) DEFAULT NULL,
  `province` varchar(50) NOT NULL COMMENT '省份',
  `type` varchar(20) DEFAULT 'RECEIVE',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_id` bigint NOT NULL,
  `last_used_at` datetime(6) DEFAULT NULL,
  `tag` varchar(20) DEFAULT NULL,
  `used_count` int DEFAULT '0',
  `is_history` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn2fisxyyu3l9wlch3ve2nocgp` (`user_id`),
  CONSTRAINT `FKn2fisxyyu3l9wlch3ve2nocgp` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user_addresses
-- ----------------------------
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (1, '深圳市', '2025-12-25 18:18:02', '公主殿下', '南山区', 0, NULL, '0E-7', '0E-7', '我', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 18:18:06', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (2, '深圳市', '2025-12-25 18:18:06', '公主殿下', '南山区', 0, NULL, '0E-7', '0E-7', '我', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 18:18:47', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (3, '深圳市', '2025-12-25 18:18:47', '公主殿下', '南山区', 0, NULL, '0E-7', '0E-7', '我', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 18:24:34', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (4, '深圳市', '2025-12-25 18:24:34', '公主殿下', '南山区', 0, NULL, '0E-7', '0E-7', '我', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 18:24:34', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (5, '深圳市', '2025-12-25 22:01:44', '河南省郑州市', '南山区', 0, NULL, '0E-7', '0E-7', '最帅的我', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 22:01:44', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (6, '深圳市', '2025-12-25 22:10:23', '河南省郑州市', '南山区', 0, NULL, '0E-7', '0E-7', '最帅的我', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 22:10:23', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (7, '深圳市', '2025-12-25 22:10:27', '河南省郑州市', '南山区', 0, NULL, '0E-7', '0E-7', '最帅的我', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 22:10:27', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (8, '深圳市', '2025-12-25 22:13:36', '河南省郑州市', '南山区', 0, NULL, '0E-7', '0E-7', '最帅的我', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 22:13:36', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (9, '深圳市', '2025-12-25 22:37:05', '11415', '南山区', 0, NULL, '0E-7', '0E-7', '凯尔西', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 22:37:05', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (10, '深圳市', '2025-12-25 22:39:06', '11415', '南山区', 0, NULL, '0E-7', '0E-7', '凯尔西', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 22:39:06', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (11, '深圳市', '2025-12-25 22:43:46', '河南省', '南山区', 0, NULL, '0E-7', '0E-7', '我', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 22:43:46', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (12, '深圳市', '2025-12-25 22:49:18', '我', '南山区', 0, NULL, '0E-7', '0E-7', '我', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-25 22:49:18', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (13, '北京市', '2025-12-26 20:16:07', '建国路88号', '朝阳区', 0, NULL, NULL, NULL, '张三', '13800138001', NULL, '北京市', 'RECEIVE', '2025-12-26 20:16:07', 1, NULL, '家', NULL, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (14, '上海市', '2025-12-26 20:16:07', '陆家嘴环路100号', '浦东新区', 0, NULL, NULL, NULL, '张三', '13800138001', NULL, '上海市', 'RECEIVE', '2025-12-26 20:16:07', 1, NULL, '公司', NULL, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (16, '北京市', '2025-12-26 20:20:57', '建国路88号', '朝阳区', 0, NULL, NULL, NULL, '张三', '13800138001', NULL, '北京市', 'RECEIVE', '2025-12-26 20:20:57', 1, NULL, '家', NULL, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (17, '上海市', '2025-12-26 20:20:57', '陆家嘴环路100号', '浦东新区', 0, NULL, NULL, NULL, '张三', '13800138001', NULL, '上海市', 'RECEIVE', '2025-12-26 20:20:57', 1, NULL, '公司', NULL, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (19, '深圳市', '2025-12-27 19:38:02', '赴台', '福田区', 0, NULL, '0E-7', '0E-7', '我', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-27 19:38:02', 11, NULL, '家', 0, 'b''\x00''');
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (20, '广州市', '2025-12-27 19:49:31', '嗡嗡嗡', '海珠区', 0, NULL, '0E-7', '0E-7', '期望', '13603994106', NULL, '广东省', 'RECEIVE', '2025-12-31 09:23:17', 1, NULL, '家', 0, 'b''\x00''');
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (21, '上海市', '2025-12-27 23:15:27', '上海市', '长宁区', 0, NULL, '0E-7', '0E-7', '张思', '13603994106', NULL, '上海市', 'RECEIVE', '2025-12-28 01:50:01', 11, NULL, '学校', 0, 'b''\x00''');
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (22, '深圳市', '2025-12-28 00:55:09', '粤海街道深圳大学', '南山区', 0, NULL, '22.5330000', '113.9365000', '测试用户', '13800138000', NULL, '广东省', 'RECEIVE', '2025-12-31 09:23:17', 1, NULL, '公司', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (23, '广州市', '2025-12-28 00:55:09', '五山街道华南理工大学', '天河区', 0, NULL, '23.1502000', '113.3418000', '测试用户', '13800138000', NULL, '广东省', 'RECEIVE', '2025-12-28 00:55:09', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (24, '深圳市', '2025-12-28 00:55:14', '粤海街道深圳大学', '南山区', 0, NULL, '22.5330000', '113.9365000', '测试用户', '13800138000', NULL, '广东省', 'RECEIVE', '2025-12-31 09:23:17', 1, NULL, '公司', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (25, '广州市', '2025-12-28 00:55:14', '五山街道华南理工大学', '天河区', 0, NULL, '23.1502000', '113.3418000', '测试用户', '13800138000', NULL, '广东省', 'RECEIVE', '2025-12-28 00:55:14', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (26, '深圳市', '2025-12-28 00:59:14', '粤海街道深圳大学', '南山区', 0, NULL, '22.5330000', '113.9365000', '测试用户', '13800138000', NULL, '广东省', 'RECEIVE', '2025-12-31 09:23:17', 1, NULL, '公司', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (27, '广州市', '2025-12-28 00:59:14', '五山街道华南理工大学', '天河区', 0, NULL, '23.1502000', '113.3418000', '测试用户', '13800138000', NULL, '广东省', 'RECEIVE', '2025-12-28 00:59:14', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (28, '宁波市', '2025-12-28 01:50:02', '贵州大学', '北仑区', 1, NULL, '0E-7', '0E-7', '诗人', '13603994106', NULL, '浙江省', 'RECEIVE', '2025-12-28 01:50:02', 11, NULL, '学校', 0, 'b''\x00''');
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (29, '深圳市', '2025-12-28 10:06:08', '粤海街道深圳大学', '南山区', 1, NULL, '22.5330000', '113.9365000', '测试用户', '13800138000', NULL, '广东省', 'RECEIVE', '2025-12-28 10:06:08', 1, NULL, '公司', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (30, '广州市', '2025-12-28 10:06:08', '五山街道华南理工大学', '天河区', 0, NULL, '23.1502000', '113.3418000', '测试用户', '13800138000', NULL, '广东省', 'RECEIVE', '2025-12-28 10:06:08', 1, NULL, '家', 0, NULL);
INSERT INTO `user_addresses` (`id`, `city`, `created_at`, `detail`, `district`, `is_default`, `label`, `latitude`, `longitude`, `name`, `phone`, `postal_code`, `province`, `type`, `updated_at`, `user_id`, `last_used_at`, `tag`, `used_count`, `is_history`) VALUES (31, '广州市', '2025-12-31 07:54:41', '珠江新城花城大道88号', '天河区', 0, NULL, NULL, NULL, '张三', '13812345678', NULL, '广东省', 'RECEIVE', '2025-12-31 09:34:42', 11, '2025-12-31 09:34:41.770127', NULL, 3, 'b''\x01''');

-- ----------------------------
-- Table structure for user_coupons
-- ----------------------------
DROP TABLE IF EXISTS `user_coupons`;
CREATE TABLE `user_coupons` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) DEFAULT NULL,
  `end_time` datetime(6) NOT NULL,
  `receive_time` datetime(6) DEFAULT NULL,
  `start_time` datetime(6) NOT NULL,
  `status` varchar(20) DEFAULT NULL,
  `is_used` bit(1) NOT NULL,
  `used_order_id` bigint DEFAULT NULL,
  `coupon_template_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `remaining_uses` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmqrbfrxy6twa3ljnyfojhooua` (`coupon_template_id`),
  KEY `FK654lvm2qu8l08pyg310mbd74h` (`user_id`),
  CONSTRAINT `FK654lvm2qu8l08pyg310mbd74h` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKmqrbfrxy6twa3ljnyfojhooua` FOREIGN KEY (`coupon_template_id`) REFERENCES `coupon_templates` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user_coupons
-- ----------------------------
INSERT INTO `user_coupons` (`id`, `code`, `end_time`, `receive_time`, `start_time`, `status`, `is_used`, `used_order_id`, `coupon_template_id`, `user_id`, `remaining_uses`) VALUES (1, NULL, '2026-01-04 21:09:21.962051', '2025-12-28 21:09:21.962051', '2025-12-28 21:09:21.962051', 'UNUSED', 'b''\x00''', NULL, 1, 1, 1);
INSERT INTO `user_coupons` (`id`, `code`, `end_time`, `receive_time`, `start_time`, `status`, `is_used`, `used_order_id`, `coupon_template_id`, `user_id`, `remaining_uses`) VALUES (2, NULL, '2026-01-04 21:09:22.067580', '2025-12-28 21:09:22.067580', '2025-12-28 21:09:22.067580', 'UNUSED', 'b''\x00''', NULL, 1, 8, 1);
INSERT INTO `user_coupons` (`id`, `code`, `end_time`, `receive_time`, `start_time`, `status`, `is_used`, `used_order_id`, `coupon_template_id`, `user_id`, `remaining_uses`) VALUES (3, NULL, '2026-01-04 21:09:27.475208', '2025-12-28 21:09:27.475208', '2025-12-28 21:09:27.475208', 'UNUSED', 'b''\x00''', NULL, 1, 11, 1);
INSERT INTO `user_coupons` (`id`, `code`, `end_time`, `receive_time`, `start_time`, `status`, `is_used`, `used_order_id`, `coupon_template_id`, `user_id`, `remaining_uses`) VALUES (4, NULL, '2026-01-04 22:09:18.100603', '2025-12-28 22:09:18.100603', '2025-12-28 22:09:18.100603', 'UNUSED', 'b''\x00''', NULL, 1, 10, 1);
INSERT INTO `user_coupons` (`id`, `code`, `end_time`, `receive_time`, `start_time`, `status`, `is_used`, `used_order_id`, `coupon_template_id`, `user_id`, `remaining_uses`) VALUES (5, NULL, '2026-01-07 09:39:37.610956', '2025-12-31 09:39:37.610956', '2025-12-31 09:39:37.610956', 'UNUSED', 'b''\x00''', NULL, 1, 11, 1);

-- ----------------------------
-- Table structure for user_favorite_products
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite_products`;
CREATE TABLE `user_favorite_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '收藏时间-自动生成',
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKtku5mgc6b7m5v7m4312hhqj02` (`user_id`,`product_id`),
  KEY `FK85wif3w88mq3p4ad5h972g9n9` (`product_id`),
  CONSTRAINT `FK15xaakjffx56a52b4dulxa0tu` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK85wif3w88mq3p4ad5h972g9n9` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user_favorite_products
-- ----------------------------
INSERT INTO `user_favorite_products` (`id`, `created_at`, `product_id`, `user_id`) VALUES (1, '2025-12-28 16:05:24.493659', 14, 1);
INSERT INTO `user_favorite_products` (`id`, `created_at`, `product_id`, `user_id`) VALUES (2, '2025-12-28 17:16:06.990313', 19, 11);
INSERT INTO `user_favorite_products` (`id`, `created_at`, `product_id`, `user_id`) VALUES (3, '2025-12-28 19:25:19.541195', 20, 11);
INSERT INTO `user_favorite_products` (`id`, `created_at`, `product_id`, `user_id`) VALUES (4, '2025-12-30 22:29:35.344525', 6, 11);

-- ----------------------------
-- Table structure for user_favorite_stores
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite_stores`;
CREATE TABLE `user_favorite_stores` (
  `store_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`store_id`,`user_id`),
  KEY `FK4w7t4sssn24b03hkuj58jp15b` (`user_id`),
  CONSTRAINT `FK4w7t4sssn24b03hkuj58jp15b` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK5tdt9miuoj7n32u3x90qofqlf` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user_invite_rewards
-- ----------------------------
DROP TABLE IF EXISTS `user_invite_rewards`;
CREATE TABLE `user_invite_rewards` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `reward_status` varchar(20) DEFAULT NULL,
  `invitee_id` bigint NOT NULL,
  `inviter_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4fyw4tkkrua6kpvy30sseyfgy` (`invitee_id`),
  KEY `FK1epngqia5q7ageic21kkbj35x` (`inviter_id`),
  CONSTRAINT `FK1epngqia5q7ageic21kkbj35x` FOREIGN KEY (`inviter_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK4fyw4tkkrua6kpvy30sseyfgy` FOREIGN KEY (`invitee_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user_recommendation_feedbacks
-- ----------------------------
DROP TABLE IF EXISTS `user_recommendation_feedbacks`;
CREATE TABLE `user_recommendation_feedbacks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(20) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKng3f7xovip5he0ap29sy94q2i` (`product_id`),
  KEY `FKajvohpdipok1v7gdq0tjp6w3a` (`user_id`),
  CONSTRAINT `FKajvohpdipok1v7gdq0tjp6w3a` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKng3f7xovip5he0ap29sy94q2i` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user_search_histories
-- ----------------------------
DROP TABLE IF EXISTS `user_search_histories`;
CREATE TABLE `user_search_histories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `keyword` varchar(100) NOT NULL,
  `type` varchar(20) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKad8nm10xcsrqff6dolyv1cpdt` (`user_id`),
  CONSTRAINT `FKad8nm10xcsrqff6dolyv1cpdt` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user_search_histories
-- ----------------------------
INSERT INTO `user_search_histories` (`id`, `created_at`, `keyword`, `type`, `user_id`) VALUES (1, '2025-12-30 23:30:02.868366', 'kaf拿铁', NULL, 11);
INSERT INTO `user_search_histories` (`id`, `created_at`, `keyword`, `type`, `user_id`) VALUES (2, '2025-12-30 23:30:03.760444', 'kaf拿铁拿铁', NULL, 11);
INSERT INTO `user_search_histories` (`id`, `created_at`, `keyword`, `type`, `user_id`) VALUES (3, '2025-12-30 23:30:16.908908', '咖啡', NULL, 11);
INSERT INTO `user_search_histories` (`id`, `created_at`, `keyword`, `type`, `user_id`) VALUES (4, '2025-12-30 23:30:18.199616', '咖啡奶茶', NULL, 11);
INSERT INTO `user_search_histories` (`id`, `created_at`, `keyword`, `type`, `user_id`) VALUES (5, '2025-12-30 23:30:19.551884', '咖啡奶茶', NULL, 11);
INSERT INTO `user_search_histories` (`id`, `created_at`, `keyword`, `type`, `user_id`) VALUES (6, '2025-12-31 08:01:35.389205', '咖啡奶茶', NULL, 11);
INSERT INTO `user_search_histories` (`id`, `created_at`, `keyword`, `type`, `user_id`) VALUES (7, '2025-12-31 08:01:36.156765', '咖啡奶茶奶茶', NULL, 11);
INSERT INTO `user_search_histories` (`id`, `created_at`, `keyword`, `type`, `user_id`) VALUES (8, '2025-12-31 08:01:37.608356', '咖啡奶茶奶茶', NULL, 11);
INSERT INTO `user_search_histories` (`id`, `created_at`, `keyword`, `type`, `user_id`) VALUES (9, '2025-12-31 08:01:58.334557', '咖啡奶茶', NULL, 11);
INSERT INTO `user_search_histories` (`id`, `created_at`, `keyword`, `type`, `user_id`) VALUES (10, '2025-12-31 08:02:02.316881', '咖啡奶茶', NULL, 11);
INSERT INTO `user_search_histories` (`id`, `created_at`, `keyword`, `type`, `user_id`) VALUES (11, '2025-12-31 08:31:36.087652', 'kaf', NULL, 11);

-- ----------------------------
-- Table structure for user_share_rewards
-- ----------------------------
DROP TABLE IF EXISTS `user_share_rewards`;
CREATE TABLE `user_share_rewards` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `reward_type` varchar(20) DEFAULT NULL,
  `reward_value` varchar(100) DEFAULT NULL,
  `share_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp6e2emo9ef34j0v4q5ysg25he` (`user_id`),
  CONSTRAINT `FKp6e2emo9ef34j0v4q5ysg25he` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user_shares
-- ----------------------------
DROP TABLE IF EXISTS `user_shares`;
CREATE TABLE `user_shares` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `channel` varchar(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `invite_code` varchar(50) DEFAULT NULL,
  `target_id` bigint DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `user_id` bigint NOT NULL,
  `share_channel` varchar(20) DEFAULT NULL,
  `share_type` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKalnl0jer446v0969es9irm8y6` (`user_id`),
  CONSTRAINT `FKalnl0jer446v0969es9irm8y6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user_tag_relation
-- ----------------------------
DROP TABLE IF EXISTS `user_tag_relation`;
CREATE TABLE `user_tag_relation` (
  `tag_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`tag_id`,`user_id`),
  KEY `FK1803w78nvxka0303attiu5yf` (`user_id`),
  CONSTRAINT `FK1803w78nvxka0303attiu5yf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK1dnxos4a1gxeijurhogtj9o6k` FOREIGN KEY (`tag_id`) REFERENCES `user_tags` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user_tags
-- ----------------------------
DROP TABLE IF EXISTS `user_tags`;
CREATE TABLE `user_tags` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `type` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avatar_url` varchar(255) DEFAULT NULL,
  `balance` decimal(10,2) NOT NULL,
  `birthday` date DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `gender` int DEFAULT NULL,
  `growth_value` int NOT NULL,
  `is_active` bit(1) NOT NULL,
  `last_login_at` datetime(6) DEFAULT NULL,
  `member_card_expire_date` date DEFAULT NULL,
  `member_card_no` varchar(50) DEFAULT NULL,
  `member_card_status` varchar(20) DEFAULT NULL,
  `nickname` varchar(50) NOT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `points` int NOT NULL,
  `province` varchar(50) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `wechat_openid` varchar(64) DEFAULT NULL,
  `member_level_id` bigint DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `registration_time` datetime(6) NOT NULL,
  `status` varchar(20) NOT NULL,
  `wechat_unionid` varchar(64) DEFAULT NULL,
  `inviter_id` bigint DEFAULT NULL,
  `invite_code` varchar(20) DEFAULT NULL,
  `push_marketing` bit(1) DEFAULT NULL,
  `push_notification_enabled` bit(1) DEFAULT NULL,
  `push_order_update` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_iix8tsqjhksjr3p872k05i5d` (`member_card_no`),
  UNIQUE KEY `UK_du5v5sr43g5bfnji4vb8hg5s3` (`phone`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UK_5y087isj5tb7ydwwgb1fqcul8` (`wechat_openid`),
  UNIQUE KEY `UK_6hglwlkeap17hv0j2n8gqi2q` (`invite_code`),
  UNIQUE KEY `uk_invite_code` (`invite_code`),
  KEY `FKd8ssleprm03mn0a4s04swxobu` (`member_level_id`),
  KEY `FKsd1au0076ct861gmth64xlln` (`inviter_id`),
  CONSTRAINT `FKd8ssleprm03mn0a4s04swxobu` FOREIGN KEY (`member_level_id`) REFERENCES `member_levels` (`id`),
  CONSTRAINT `FKsd1au0076ct861gmth64xlln` FOREIGN KEY (`inviter_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` (`id`, `avatar_url`, `balance`, `birthday`, `city`, `country`, `created_at`, `email`, `gender`, `growth_value`, `is_active`, `last_login_at`, `member_card_expire_date`, `member_card_no`, `member_card_status`, `nickname`, `password_hash`, `phone`, `points`, `province`, `updated_at`, `username`, `wechat_openid`, `member_level_id`, `password`, `registration_time`, `status`, `wechat_unionid`, `inviter_id`, `invite_code`, `push_marketing`, `push_notification_enabled`, `push_order_update`) VALUES (1, 'http://localhost:8081/uploads/1766825519079_9445b6a1.jpg', '0.00', '2025-12-11', NULL, NULL, '2025-12-25 13:06:48.999860', NULL, 0, 0, 'b''\x01''', '2025-12-31 07:48:25.547355', NULL, 'MT1766668413277765', NULL, 'sssaa', NULL, '13603994106', 6010, NULL, '2025-12-31 07:48:25.548334', 'shark', NULL, 2, '$2a$10$MCgX1nw3EIO3ZImBF2KVRunjJLgKb2Zk1h1/P5/ryrSskkpF6uHs6', '2025-12-25 13:06:48.967914', 'ACTIVE', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `users` (`id`, `avatar_url`, `balance`, `birthday`, `city`, `country`, `created_at`, `email`, `gender`, `growth_value`, `is_active`, `last_login_at`, `member_card_expire_date`, `member_card_no`, `member_card_status`, `nickname`, `password_hash`, `phone`, `points`, `province`, `updated_at`, `username`, `wechat_openid`, `member_level_id`, `password`, `registration_time`, `status`, `wechat_unionid`, `inviter_id`, `invite_code`, `push_marketing`, `push_notification_enabled`, `push_order_update`) VALUES (8, NULL, '0.00', NULL, NULL, NULL, '2025-12-26 17:12:24.045054', NULL, NULL, 0, 'b''\x01''', '2025-12-26 17:12:24.113915', NULL, NULL, NULL, 'adm_740343', NULL, '13966740343', 0, NULL, '2025-12-26 17:12:24.113914', 'adm_740343', NULL, NULL, '$2a$10$0XVwT9TGjYFzG4i3g/GPd.1mLU7VeRCsSK3CgUFBKAc17RDIAeFVi', '2025-12-26 17:12:24.044078', 'ADMIN', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `users` (`id`, `avatar_url`, `balance`, `birthday`, `city`, `country`, `created_at`, `email`, `gender`, `growth_value`, `is_active`, `last_login_at`, `member_card_expire_date`, `member_card_no`, `member_card_status`, `nickname`, `password_hash`, `phone`, `points`, `province`, `updated_at`, `username`, `wechat_openid`, `member_level_id`, `password`, `registration_time`, `status`, `wechat_unionid`, `inviter_id`, `invite_code`, `push_marketing`, `push_notification_enabled`, `push_order_update`) VALUES (9, NULL, '0.00', NULL, NULL, NULL, '2025-12-26 17:13:52.859415', NULL, NULL, 0, 'b''\x01''', '2025-12-26 18:10:50.459827', NULL, NULL, NULL, 'admin888', NULL, '13888888888', 0, NULL, '2025-12-26 18:10:50.460807', 'admin888', NULL, NULL, '$2a$10$7u/xhNJqK2786lO9qKG71uoq3Pfjs.ZoNO61MfhQAEs4oOgXfUydu', '2025-12-26 17:13:52.858439', 'ADMIN', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `users` (`id`, `avatar_url`, `balance`, `birthday`, `city`, `country`, `created_at`, `email`, `gender`, `growth_value`, `is_active`, `last_login_at`, `member_card_expire_date`, `member_card_no`, `member_card_status`, `nickname`, `password_hash`, `phone`, `points`, `province`, `updated_at`, `username`, `wechat_openid`, `member_level_id`, `password`, `registration_time`, `status`, `wechat_unionid`, `inviter_id`, `invite_code`, `push_marketing`, `push_notification_enabled`, `push_order_update`) VALUES (10, NULL, '0.00', NULL, NULL, NULL, '2025-12-26 17:21:04.254937', NULL, NULL, 0, 'b''\x01''', '2025-12-26 17:21:04.323632', NULL, NULL, NULL, 'adm_740864', NULL, '13966740864', 0, NULL, '2025-12-26 17:21:04.323632', 'adm_740864', NULL, NULL, '$2a$10$pwiL8ucCeC3twscYKR.A6.LGgNTz887N8Iwe9lzqJC2QRRSD1NHRS', '2025-12-26 17:21:04.248123', 'ADMIN', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `users` (`id`, `avatar_url`, `balance`, `birthday`, `city`, `country`, `created_at`, `email`, `gender`, `growth_value`, `is_active`, `last_login_at`, `member_card_expire_date`, `member_card_no`, `member_card_status`, `nickname`, `password_hash`, `phone`, `points`, `province`, `updated_at`, `username`, `wechat_openid`, `member_level_id`, `password`, `registration_time`, `status`, `wechat_unionid`, `inviter_id`, `invite_code`, `push_marketing`, `push_notification_enabled`, `push_order_update`) VALUES (11, '/uploads/1767144488237_fd5864a2.jpg', '0.00', '2025-12-17', NULL, NULL, '2025-12-26 18:13:00.806113', NULL, 0, 453, 'b''\x01''', '2025-12-31 09:36:27.800392', NULL, NULL, NULL, '老师作业好难好难好难！！！好多！', NULL, '13603994110', 536, NULL, '2025-12-31 09:39:06.473249', 'uuu', NULL, 3, '$2a$10$vWtU2Uzx7ZASIF6MNTK2MeWSKiCsWp8TeXTqkahKhx4z3awyZZS5m', '2025-12-26 18:13:00.799746', 'ADMIN', NULL, NULL, NULL, 'b''\x01''', 'b''\x01''', 'b''\x00''');

-- ----------------------------
-- Table structure for verification_codes
-- ----------------------------
DROP TABLE IF EXISTS `verification_codes`;
CREATE TABLE `verification_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `is_used` bit(1) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `sent_at` datetime(6) NOT NULL,
  `type` varchar(20) NOT NULL,
  `expire_time` datetime(6) NOT NULL,
  `used` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
