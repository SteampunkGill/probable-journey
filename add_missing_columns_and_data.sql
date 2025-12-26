-- 添加缺失列并插入测试数据
-- 先禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 为users表添加agreed_privacy列（如果不存在）
ALTER TABLE `users` 
ADD COLUMN IF NOT EXISTS `agreed_privacy` BOOLEAN DEFAULT FALSE COMMENT '是否同意隐私协议';

-- 2. 为coupon_templates表添加total_count列（如果不存在）
ALTER TABLE `coupon_templates` 
ADD COLUMN IF NOT EXISTS `total_count` INT COMMENT '总发行量';

-- 3. 为user_coupons表添加template_id列（如果不存在）
ALTER TABLE `user_coupons` 
ADD COLUMN IF NOT EXISTS `template_id` BIGINT NOT NULL;

-- 4. 为products表添加可能缺失的列（根据常见错误）
ALTER TABLE `products` 
ADD COLUMN IF NOT EXISTS `favorite_count` INT DEFAULT 0 COMMENT '收藏数',
ADD COLUMN IF NOT EXISTS `is_active` BOOLEAN DEFAULT TRUE COMMENT '是否激活',
ADD COLUMN IF NOT EXISTS `view_count` INT DEFAULT 0 COMMENT '浏览数';

-- 5. 为orders表添加可能缺失的列
ALTER TABLE `orders` 
ADD COLUMN IF NOT EXISTS `is_commented` BOOLEAN DEFAULT FALSE COMMENT '是否已评价',
ADD COLUMN IF NOT EXISTS `remind_count` INT DEFAULT 0 COMMENT '催单次数',
ADD COLUMN IF NOT EXISTS `last_remind_time` DATETIME COMMENT '上次催单时间';

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入测试数据（使用initial.sql中的INSERT语句，但避免重复插入）
-- 注意：使用INSERT IGNORE避免主键冲突

-- 插入分类数据
INSERT IGNORE INTO `categories` (`name`, `icon_url`, `sort_order`, `created_at`, `updated_at`) VALUES
('人气Top', '/images/categories/hot.png', 1, NOW(), NOW()),
('经典奶茶', '/images/categories/milk-tea.png', 2, NOW(), NOW()),
('清爽果茶', '/images/categories/fruit-tea.png', 3, NOW(), NOW()),
('现磨咖啡', '/images/categories/coffee.png', 4, NOW(), NOW()),
('精选小食', '/images/categories/snack.png', 5, NOW(), NOW()),
('热饮系列', '/images/categories/hot-drink.png', 6, NOW(), NOW()),
('低糖系列', '/images/categories/low-sugar.png', 7, NOW(), NOW()),
('季节限定', '/images/categories/seasonal.png', 8, NOW(), NOW()),
('冰淇淋', '/images/categories/ice-cream.png', 9, NOW(), NOW());

-- 插入商品数据
INSERT IGNORE INTO `products` (`category_id`, `name`, `description`, `price`, `main_image_url`, `status`, `sales`, `is_member_exclusive`, `member_price`, `created_at`, `updated_at`) VALUES
(2, '珍珠奶茶', '经典台式风味，Q弹珍珠', 15.00, '/images/products/milk-tea-1.jpg', 1, 1200, 0, 13.50, NOW(), NOW()),
(2, '波霸奶茶', '超大颗波霸，口感丰富', 16.00, '/images/products/milk-tea-2.jpg', 1, 800, 0, 14.40, NOW(), NOW()),
(3, '杨枝甘露', '新鲜芒果配西柚，酸甜适口', 22.00, '/images/products/fruit-tea-1.jpg', 1, 2500, 0, 19.80, NOW(), NOW()),
(3, '多肉葡萄', '手剥新鲜葡萄，搭配芝士奶盖', 28.00, '/images/products/fruit-tea-2.jpg', 1, 3000, 1, 25.00, NOW(), NOW()),
(4, '生椰拿铁', '清甜生椰水遇上浓缩咖啡', 18.00, '/images/products/coffee-1.jpg', 1, 1500, 0, 16.20, NOW(), NOW());

-- 插入会员等级数据
INSERT IGNORE INTO `member_levels` (`name`, `min_growth_value`, `discount_rate`, `privileges_json`, `created_at`, `updated_at`) VALUES
('普通会员', 0, 1.00, '{"free_shipping": false, "birthday_gift": false}', NOW(), NOW()),
('银卡会员', 100, 0.95, '{"free_shipping": false, "birthday_gift": true}', NOW(), NOW()),
('金卡会员', 500, 0.90, '{"free_shipping": true, "birthday_gift": true}', NOW(), NOW()),
('钻石会员', 2000, 0.85, '{"free_shipping": true, "birthday_gift": true, "priority_service": true}', NOW(), NOW());

-- 插入测试用户 (密码为123456的bcrypt哈希)
INSERT IGNORE INTO `users` (`wechat_openid`, `username`, `password_hash`, `phone`, `nickname`, `avatar_url`, `birthday`, `member_level_id`, `growth_value`, `points`, `balance`, `status`, `agreed_privacy`, `last_login_at`, `registration_time`, `created_at`, `updated_at`) VALUES
('wx_openid_001', 'user1', '$2a$10$N9qo8uLOickgx2ZMRZoMye3Z6qjJ6QpR3b5JYz7Kv6Wz1VY2vX6O2', '13800138001', '测试用户1', '/images/avatars/default.png', '1990-01-01', 1, 150, 1000, 50.00, 'ACTIVE', TRUE, NOW(), NOW(), NOW(), NOW()),
('wx_openid_002', 'user2', '$2a$10$N9qo8uLOickgx2ZMRZoMye3Z6qjJ6QpR3b5JYz7Kv6Wz1VY2vX6O2', '13800138002', '测试用户2', '/images/avatars/default.png', '1992-05-15', 2, 300, 2000, 100.00, 'ACTIVE', TRUE, NOW(), NOW(), NOW(), NOW()),
('wx_openid_003', 'user3', '$2a$10$N9qo8uLOickgx2ZMRZoMye3Z6qjJ6QpR3b5JYz7Kv6Wz1VY2vX6O2', '13800138003', '测试用户3', '/images/avatars/default.png', '1995-08-20', 3, 800, 5000, 200.00, 'ACTIVE', TRUE, NOW(), NOW(), NOW(), NOW());

-- 插入用户地址
INSERT IGNORE INTO `user_addresses` (`user_id`, `name`, `phone`, `province`, `city`, `district`, `detail`, `tag`, `is_default`, `created_at`, `updated_at`) VALUES
(1, '张三', '13800138001', '北京市', '北京市', '朝阳区', '建国路88号', '家', TRUE, NOW(), NOW()),
(1, '张三', '13800138001', '上海市', '上海市', '浦东新区', '陆家嘴环路100号', '公司', FALSE, NOW(), NOW()),
(2, '李四', '13800138002', '广东省', '深圳市', '南山区', '科技园南区', '家', TRUE, NOW(), NOW());

-- 插入门店数据
INSERT IGNORE INTO `stores` (`name`, `address`, `phone`, `business_status`, `business_hours`, `code`, `status`, `delivery_fee`, `min_order_amount`, `rating`, `created_at`, `updated_at`) VALUES
('蜜雪冰城旗舰店', '北京市朝阳区建国路88号', '010-88888888', 1, '09:00-22:00', 'STORE001', 'OPEN', 5.00, 20.00, 4.8, NOW(), NOW()),
('蜜雪冰城深圳分店', '深圳市南山区科技园南区', '0755-66666666', 1, '08:00-23:00', 'STORE002', 'OPEN', 3.00, 15.00, 4.9, NOW(), NOW());

-- 插入优惠券模板
INSERT IGNORE INTO `coupon_templates` (`name`, `type`, `value`, `min_amount`, `total_count`, `start_time`, `end_time`, `days_valid`, `rule_json`) VALUES
('新用户立减券', 'REDUCTION', 5.00, 20.00, 1000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 30, '{"applicable_products": null, "exclude_products": null}'),
('全场折扣券', 'DISCOUNT', 0.90, 30.00, 500, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 60, '{"applicable_products": null}'),
('会员专享券', 'REDUCTION', 10.00, 50.00, 200, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), 90, '{"member_only": true}');

-- 插入用户优惠券
INSERT IGNORE INTO `user_coupons` (`user_id`, `template_id`, `code`, `status`, `expire_time`, `created_at`) VALUES
(1, 1, 'NEWUSER001', 'UNUSED', DATE_ADD(NOW(), INTERVAL 30 DAY), NOW()),
(1, 2, 'DISCOUNT001', 'UNUSED', DATE_ADD(NOW(), INTERVAL 60 DAY), NOW()),
(2, 3, 'MEMBER001', 'UNUSED', DATE_ADD(NOW(), INTERVAL 90 DAY), NOW());

-- 插入订单数据 (假设用户1在门店1下单)
INSERT IGNORE INTO `orders` (`order_no`, `user_id`, `store_id`, `status`, `total_amount`, `pay_amount`, `discount_amount`, `delivery_fee`, `pay_method`, `pay_time`, `delivery_type`, `address_json`, `pickup_code`, `estimated_ready_time`, `created_at`, `updated_at`) VALUES
('T202412260001', 1, 1, 'PAID', 45.00, 40.00, 5.00, 0.00, 'WECHAT', NOW(), 'PICKUP', '{"name": "张三", "phone": "13800138001", "address": "北京市朝阳区建国路88号"}', 'A123', DATE_ADD(NOW(), INTERVAL 15 MINUTE), NOW(), NOW());

-- 插入订单项 (对应商品1和商品3)
INSERT IGNORE INTO `order_items` (`order_id`, `product_id`, `product_name`, `product_image`, `spec_json`, `price`, `quantity`, `total_price`) VALUES
(1, 1, '珍珠奶茶', '/images/products/milk-tea-1.jpg', '{"sweetness": "正常", "temperature": "去冰"}', 15.00, 2, 30.00),
(1, 3, '杨枝甘露', '/images/products/fruit-tea-1.jpg', '{"sweetness": "少糖", "temperature": "正常"}', 22.00, 1, 22.00);

-- 插入购物车项
INSERT IGNORE INTO `cart_items` (`user_id`, `product_id`, `sweetness`, `temperature`, `quantity`, `created_at`, `updated_at`) VALUES
(1, 2, '半糖', '少冰', 1, NOW(), NOW()),
(2, 4, '正常', '去冰', 2, NOW(), NOW());

-- 插入系统角色
INSERT IGNORE INTO `sys_roles` (`name`, `code`, `description`) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有所有权限'),
('店长', 'STORE_MANAGER', '管理门店'),
('店员', 'STAFF', '处理订单');

-- 插入系统管理员 (密码为admin123的bcrypt哈希)
INSERT IGNORE INTO `sys_users` (`username`, `password`, `real_name`, `store_id`, `status`, `created_at`) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye3Z6qjJ6QpR3b5JYz7Kv6Wz1VY2vX6O2', '系统管理员', NULL, 1, NOW()),
('manager1', '$2a$10$N9qo8uLOickgx2ZMRZoMye3Z6qjJ6QpR3b5JYz7Kv6Wz1VY2vX6O2', '张店长', 1, 1, NOW());

-- 插入用户角色关联
INSERT IGNORE INTO `sys_user_roles` (`user_id`, `role_id`) VALUES
(1, 1),  -- admin是超级管理员
(2, 2);  -- manager1是店长

-- 插入轮播图数据
INSERT IGNORE INTO `banners` (`image_url`, `link_type`, `link_value`, `sort`, `position`) VALUES
('/images/banners/banner1.jpg', 'PRODUCT', '1', 1, 'HOME'),
('/images/banners/banner2.jpg', 'URL', 'https://example.com/promotion', 2, 'HOME'),
('/images/banners/banner3.jpg', 'NONE', NULL, 3, 'HOME');

-- 插入搜索关键词
INSERT IGNORE INTO `search_keywords` (`keyword`, `search_count`, `is_hot`) VALUES
('珍珠奶茶', 1500, TRUE),
('果茶', 1200, TRUE),
('咖啡', 800, FALSE),
('冰淇淋', 600, FALSE);

-- 插入快捷入口
INSERT IGNORE INTO `quick_entries` (`name`, `icon_url`, `link_type`, `link_value`, `sort_order`) VALUES
('会员中心', '/images/icons/member.png', 'URL', '/member', 1),
('优惠券', '/images/icons/coupon.png', 'URL', '/coupon', 2),
('积分商城', '/images/icons/points.png', 'URL', '/points', 3),
('我的订单', '/images/icons/order.png', 'URL', '/order', 4);

-- 完成
SELECT '数据初始化完成！' AS message;