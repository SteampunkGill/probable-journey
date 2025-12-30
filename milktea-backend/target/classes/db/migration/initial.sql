CREATE DATABASE `milktea_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE DATABASE `milktea_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

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

CREATE TABLE `allergens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(50) NOT NULL,
  `icon_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1tipxa3jfm7x5gcbqlmn6h7on` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

-- 初始化会员等级数据
INSERT INTO `member_levels` (`id`, `name`, `min_growth_value`, `discount_rate`, `description`, `created_at`, `updated_at`) VALUES
(1, '普通会员', 0, 1.00, '注册即可加入', NOW(), NOW()),
(2, '黄金会员', 1000, 0.95, '消费满1000元升级', NOW(), NOW()),
(3, '钻石会员', 5000, 0.88, '消费满5000元升级', NOW(), NOW());

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
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `order_refund_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `refund_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtp1pufg85a5clnusv2ltydxn8` (`refund_id`),
  CONSTRAINT `FKtp1pufg85a5clnusv2ltydxn8` FOREIGN KEY (`refund_id`) REFERENCES `order_refunds` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `order_review_appends` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `review_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9rg9nkraeoq87d2hhes0to52p` (`review_id`),
  CONSTRAINT `FK9rg9nkraeoq87d2hhes0to52p` FOREIGN KEY (`review_id`) REFERENCES `order_reviews` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `order_review_likes` (
  `created_at` datetime(6) DEFAULT NULL,
  `review_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`review_id`,`user_id`),
  KEY `FKjwn3csdfuiu1wi3vf19yu5pij` (`user_id`),
  CONSTRAINT `FK98p7uhat6vbn1seewheffohtl` FOREIGN KEY (`review_id`) REFERENCES `order_reviews` (`id`),
  CONSTRAINT `FKjwn3csdfuiu1wi3vf19yu5pij` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `order_review_tags` (
  `review_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`review_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `page_configs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_json` json NOT NULL,
  `module` varchar(50) NOT NULL,
  `page` varchar(50) NOT NULL,
  `sort_order` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `print_templates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `is_default` bit(1) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_allergens` (
  `allergen_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`allergen_id`,`product_id`),
  KEY `FKps3ek2a1xvrk0h8dqaqn29hb1` (`product_id`),
  CONSTRAINT `FK95v23b3wm2nnbjks4up3gwvkg` FOREIGN KEY (`allergen_id`) REFERENCES `allergens` (`id`),
  CONSTRAINT `FKps3ek2a1xvrk0h8dqaqn29hb1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_allergens_map` (
  `allergen_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`allergen_id`,`product_id`),
  KEY `FKsuw88cchfxh48qfelmr1gdiur` (`product_id`),
  CONSTRAINT `FKsuw88cchfxh48qfelmr1gdiur` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKt15xps1xpc9qvj57lutkag2nj` FOREIGN KEY (`allergen_id`) REFERENCES `allergens` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `product_ingredients_map` (
  `ingredient_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`ingredient_id`,`product_id`),
  KEY `FKr65gr2gla0dp8v4b61pekoqwa` (`product_id`),
  CONSTRAINT `FKr65gr2gla0dp8v4b61pekoqwa` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKt8r944lgsosl1lj524ptjivb3` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredients` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `product_recipes` (
  `quantity` decimal(10,2) NOT NULL,
  `ingredient_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`ingredient_id`,`product_id`),
  KEY `FKdhkjblef758govm787foorpxw` (`product_id`),
  CONSTRAINT `FK1mj09yf5hkjh6tjpw33rudgsr` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredients` (`id`),
  CONSTRAINT `FKdhkjblef758govm787foorpxw` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `product_spec_combination_inventories` (
  `created_at` datetime(6) DEFAULT NULL,
  `low_stock_threshold` int DEFAULT NULL,
  `stock` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `combination_id` bigint NOT NULL,
  PRIMARY KEY (`combination_id`),
  CONSTRAINT `FKbn0qdd8thcaks15m4fxvl7opw` FOREIGN KEY (`combination_id`) REFERENCES `product_spec_combinations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_spec_combination_items` (
  `combination_id` bigint NOT NULL,
  `spec_item_id` bigint NOT NULL,
  PRIMARY KEY (`combination_id`,`spec_item_id`),
  KEY `FKt6wfo9ybuxn8hsl2wtm5plcpd` (`spec_item_id`),
  CONSTRAINT `FK6unw4g53jb6bjtpvt37bmjtgp` FOREIGN KEY (`combination_id`) REFERENCES `product_spec_combinations` (`id`),
  CONSTRAINT `FKt6wfo9ybuxn8hsl2wtm5plcpd` FOREIGN KEY (`spec_item_id`) REFERENCES `product_spec_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `product_spec_combination_prices` (
  `specItemId` bigint NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `combination_id` bigint NOT NULL,
  PRIMARY KEY (`combination_id`,`specItemId`),
  CONSTRAINT `FKs6emtmchwxv1uvh0bu6c8a2rq` FOREIGN KEY (`combination_id`) REFERENCES `product_spec_combinations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `product_spec_prices` (
  `price_adjustment` decimal(10,2) NOT NULL,
  `product_id` bigint NOT NULL,
  `spec_item_id` bigint NOT NULL,
  PRIMARY KEY (`product_id`,`spec_item_id`),
  KEY `FKl569reeft2r4npx423cf89pj0` (`spec_item_id`),
  CONSTRAINT `FK3sn420dee36ypq8o7x8qxc1gf` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKl569reeft2r4npx423cf89pj0` FOREIGN KEY (`spec_item_id`) REFERENCES `product_spec_items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `review_tags` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_212305laxs8mxe3m6y3nteagj` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `store_services` (
  `service_type` varchar(20) NOT NULL,
  `store_id` bigint NOT NULL,
  PRIMARY KEY (`service_type`,`store_id`),
  KEY `FKtj4njgbt88gcr7oefl7mdmnti` (`store_id`),
  CONSTRAINT `FKtj4njgbt88gcr7oefl7mdmnti` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `store_tags` (
  `store_id` bigint NOT NULL,
  `tag_name` varchar(50) NOT NULL,
  PRIMARY KEY (`store_id`,`tag_name`),
  CONSTRAINT `FKkn3jxcg61hmbljc4fxa5xkxek` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sys_backups` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `file_size` bigint DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `sys_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `type` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sys_role_permissions` (
  `permission_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`permission_id`,`role_id`),
  KEY `FKn16g6ssysj9ncfabu2dj0s5u8` (`role_id`),
  CONSTRAINT `FKa6lycvisbrxexjyanm5grngc0` FOREIGN KEY (`permission_id`) REFERENCES `sys_permissions` (`id`),
  CONSTRAINT `FKn16g6ssysj9ncfabu2dj0s5u8` FOREIGN KEY (`role_id`) REFERENCES `sys_roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sys_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_pjdyd7mxv3d909iq031ko99xv` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sys_user_roles` (
  `role_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`user_id`),
  KEY `FK9508r3u91qjr7onvpyf6203p8` (`user_id`),
  CONSTRAINT `FK9508r3u91qjr7onvpyf6203p8` FOREIGN KEY (`user_id`) REFERENCES `sys_users` (`id`),
  CONSTRAINT `FKmkhkspb26v193sxs3dhgu6s2p` FOREIGN KEY (`role_id`) REFERENCES `sys_roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `user_favorite_stores` (
  `store_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`store_id`,`user_id`),
  KEY `FK4w7t4sssn24b03hkuj58jp15b` (`user_id`),
  CONSTRAINT `FK4w7t4sssn24b03hkuj58jp15b` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK5tdt9miuoj7n32u3x90qofqlf` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `user_search_histories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `keyword` varchar(100) NOT NULL,
  `type` varchar(20) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKad8nm10xcsrqff6dolyv1cpdt` (`user_id`),
  CONSTRAINT `FKad8nm10xcsrqff6dolyv1cpdt` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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

CREATE TABLE `user_tag_relation` (
  `tag_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`tag_id`,`user_id`),
  KEY `FK1803w78nvxka0303attiu5yf` (`user_id`),
  CONSTRAINT `FK1803w78nvxka0303attiu5yf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK1dnxos4a1gxeijurhogtj9o6k` FOREIGN KEY (`tag_id`) REFERENCES `user_tags` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_tags` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `type` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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