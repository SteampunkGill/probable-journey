-- 1. 地址历史记录扩展
ALTER TABLE `user_addresses` ADD COLUMN `is_history` BOOLEAN DEFAULT FALSE COMMENT '是否为历史记录地址';

-- 2. 安全与设置 - 消息推送设置
ALTER TABLE `users` ADD COLUMN `push_notification_enabled` BOOLEAN DEFAULT TRUE COMMENT '是否开启消息推送';
ALTER TABLE `users` ADD COLUMN `push_order_update` BOOLEAN DEFAULT TRUE COMMENT '订单状态更新推送';
ALTER TABLE `users` ADD COLUMN `push_marketing` BOOLEAN DEFAULT FALSE COMMENT '营销活动推送';

-- 3. 社交功能 - 邀请与分享
ALTER TABLE `users` ADD COLUMN `invite_code` VARCHAR(20) DEFAULT NULL COMMENT '个人邀请码';
ALTER TABLE `users` ADD UNIQUE INDEX `uk_invite_code` (`invite_code`);

-- 4. 分享奖励记录表
CREATE TABLE `user_share_rewards` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `share_id` BIGINT NOT NULL,
  `reward_type` VARCHAR(20) COMMENT 'COUPON, POINTS',
  `reward_value` VARCHAR(100) COMMENT '奖励内容描述',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享奖励记录表';

-- 5. 邀请奖励记录表
CREATE TABLE `user_invite_rewards` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `inviter_id` BIGINT NOT NULL,
  `invitee_id` BIGINT NOT NULL,
  `reward_status` VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING, ISSUED',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_inviter` (`inviter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请奖励记录表';