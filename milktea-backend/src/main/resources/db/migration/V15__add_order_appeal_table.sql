-- 新增订单申诉表
CREATE TABLE IF NOT EXISTS `order_appeals` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `order_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `reason` varchar(255) NOT NULL COMMENT '申诉原因',
    `description` text COMMENT '详细描述',
    `amount` decimal(10,2) DEFAULT NULL COMMENT '订单花费/申请退款金额',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING, APPROVED, REJECTED',
    `admin_remark` text COMMENT '后台备注',
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_appeal_order` (`order_id`),
    KEY `FK_appeal_user` (`user_id`),
    CONSTRAINT `FK_appeal_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
    CONSTRAINT `FK_appeal_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 确保 orders 表有 is_commented 字段 (initial.sql 中已有，这里做幂等保护)
-- ALTER TABLE `orders` ADD COLUMN IF NOT EXISTS `is_commented` bit(1) DEFAULT b'0' COMMENT '是否已评价';