-- 插入积分商品
INSERT INTO `point_exchange_items` (`name`, `description`, `point_cost`, `stock`, `image_url`, `status`) VALUES
('经典珍珠奶茶兑换券', '可兑换中杯珍珠奶茶一杯', 1500, 100, 'http://localhost:8081/uploads/1766720332092_0bd7261b.png', 'AVAILABLE'),
('杨枝甘露兑换券', '可兑换中杯杨枝甘露一杯', 2200, 50, 'http://localhost:8081/uploads/1766720347887_6959c38b.png', 'AVAILABLE'),
('奶茶店定制帆布袋', '简约时尚，环保耐用', 3000, 30, 'http://localhost:8081/uploads/1766720377864_5dd85f12.png', 'AVAILABLE'),
('10元全场通用券', '满20元可用', 1000, 500, 'http://localhost:8081/uploads/1766720398648_959f4ae2.png', 'AVAILABLE');

-- 为测试用户(ID=1)插入奶茶店地址
INSERT INTO `user_addresses` (`user_id`, `name`, `phone`, `province`, `city`, `district`, `detail`, `longitude`, `latitude`, `tag`, `is_default`) VALUES
(1, '测试用户', '13800138000', '广东省', '深圳市', '南山区', '粤海街道深圳大学', 113.9365, 22.5330, '公司', 1),
(1, '测试用户', '13800138000', '广东省', '广州市', '天河区', '五山街道华南理工大学', 113.3418, 23.1502, '家', 0);