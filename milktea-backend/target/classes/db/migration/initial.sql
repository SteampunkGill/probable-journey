
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 用户与会员体系 (User Center)
-- 会员等级表
CREATE TABLE `member_levels` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(50) NOT NULL COMMENT '等级名称',
  `min_growth_value` INT NOT NULL DEFAULT 0 COMMENT '所需最小成长值',
  `discount_rate` DECIMAL(3,2) DEFAULT 1.00 COMMENT '会员折扣率(如0.90)',
  `privileges_json` JSON COMMENT '权益配置(免运费、生日礼等)',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';

-- 用户表 
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `wechat_openid` VARCHAR(64) COMMENT '微信OpenID',
  `wechat_unionid` VARCHAR(64) COMMENT '微信UnionID',
  `username` VARCHAR(50) COMMENT '账号',
  `password_hash` VARCHAR(255) COMMENT '密码',
  `phone` VARCHAR(20) COMMENT '手机号',
  `nickname` VARCHAR(64) COMMENT '昵称',
  `avatar_url` VARCHAR(255) COMMENT '头像',
  `birthday` DATE COMMENT '生日',
  `member_level_id` BIGINT COMMENT '会员等级ID',
  `inviter_id` BIGINT COMMENT '邀请人ID(用于分销绑定)',
  `growth_value` INT DEFAULT 0 COMMENT '成长值',
  `points` INT DEFAULT 0 COMMENT '当前积分',
  `balance` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '余额',
  `member_card_no` VARCHAR(50) COMMENT '实体卡号',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
  `agreed_privacy` BOOLEAN DEFAULT FALSE COMMENT '是否同意隐私协议',
  `is_deleted` BOOLEAN DEFAULT FALSE COMMENT '是否已注销',
  `last_login_at` DATETIME COMMENT '最后登录时间',
  `registration_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`wechat_openid`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_inviter` (`inviter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户地址表 
CREATE TABLE `user_addresses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `province` VARCHAR(50) NOT NULL,
  `city` VARCHAR(50) NOT NULL,
  `district` VARCHAR(50) NOT NULL,
  `detail` VARCHAR(200) NOT NULL,
  `longitude` DECIMAL(10, 7),
  `latitude` DECIMAL(10, 7),
  `tag` VARCHAR(20) COMMENT '标签: 家/公司',
  `is_default` BOOLEAN DEFAULT FALSE,
  `used_count` INT DEFAULT 0 COMMENT '使用次数(用于排序)',
  `last_used_at` DATETIME COMMENT '最后使用时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- 用户标签表
CREATE TABLE `user_tags` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '标签名: 价格敏感、新品尝鲜',
  `type` VARCHAR(20) DEFAULT 'MANUAL' COMMENT '类型: MANUAL手动/AUTO自动',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签定义';

CREATE TABLE `user_tag_relation` (
  `user_id` BIGINT NOT NULL,
  `tag_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签关联';

-- 2. 商品与库存 (Product & Inventory)

-- 商品分类
CREATE TABLE `categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT DEFAULT NULL,
  `name` VARCHAR(50) NOT NULL,
  `icon_url` VARCHAR(255),
  `sort_order` INT DEFAULT 0,
  `is_active` BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类';

-- 商品表
CREATE TABLE `products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category_id` BIGINT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `main_image_url` VARCHAR(255),
  `price` DECIMAL(10, 2) NOT NULL COMMENT '基础价格',
  `is_member_exclusive` BOOLEAN DEFAULT FALSE COMMENT '是否会员专享',
  `member_price` DECIMAL(10, 2) COMMENT '会员专享价',
  `stock` INT DEFAULT 0 COMMENT '当前库存',
  `alert_threshold` INT DEFAULT 10 COMMENT '库存预警阈值',
  `sales` INT DEFAULT 0 COMMENT '累计销量',
  `status` TINYINT DEFAULT 1 COMMENT '1:上架 0:下架',
  `cost_price` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '成本价格',
  -- 高级筛选字段
  `sugar_content` VARCHAR(50) COMMENT '含糖量: 25g',
  `calories` VARCHAR(50) COMMENT '卡路里: 250kcal',
  `default_sweetness` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '默认糖度',
  `default_temperature` VARCHAR(20) DEFAULT 'ICE' COMMENT '默认温度',
  `support_sweetness` JSON COMMENT '支持的糖度选项JSON',
  `support_temperature` JSON COMMENT '支持的温度选项JSON',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
-- 如果需要添加营养信息表
CREATE TABLE `product_nutritions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `name` VARCHAR(50) NOT NULL COMMENT '营养成分名称: 蛋白质/脂肪/碳水化合物',
  `value` VARCHAR(50) NOT NULL COMMENT '含量值',
  `unit` VARCHAR(20) COMMENT '单位: g/mg',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`),
  CONSTRAINT `fk_product_nutritions_product` FOREIGN KEY (`product_id`) 
    REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品营养成分表';
-- 原料/配方表 
CREATE TABLE `ingredients` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `unit` VARCHAR(20) COMMENT '单位: g/ml',
  `stock` DECIMAL(10,2) COMMENT '原料库存',
  `cost_per_unit` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '单位成本',
  `alert_threshold` DECIMAL(10,2) COMMENT '预警阈值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='原料表';

CREATE TABLE `product_recipes` (
  `product_id` BIGINT NOT NULL,
  `ingredient_id` BIGINT NOT NULL,
  `quantity` DECIMAL(10,2) NOT NULL COMMENT '消耗数量',
  PRIMARY KEY (`product_id`, `ingredient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品配方关联';
-- 商品关联关系表
CREATE TABLE `product_related_map` (
  `main_product_id` BIGINT NOT NULL COMMENT '主商品ID',
  `related_product_id` BIGINT NOT NULL COMMENT '关联商品ID',
  `relation_type` VARCHAR(20) COMMENT '关联类型: SIMILAR(相似)/COMPLEMENTARY(互补)/BUNDLE(套餐)/CROSS_SELL(交叉销售)',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`main_product_id`, `related_product_id`),
  KEY `idx_main_product` (`main_product_id`),
  KEY `idx_related_product` (`related_product_id`),
  CONSTRAINT `fk_related_main_product` FOREIGN KEY (`main_product_id`) 
    REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_related_related_product` FOREIGN KEY (`related_product_id`) 
    REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_not_same_product` CHECK (`main_product_id` != `related_product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品关联关系表';
-- 商品规格项 (糖度、温度)
CREATE TABLE `product_specs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `type` VARCHAR(20) NOT NULL COMMENT 'SWEETNESS, TEMPERATURE',
  `name` VARCHAR(50) NOT NULL COMMENT '正常、五分糖、去冰等',
  `is_default` BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格项';

-- 商品定制项 (加料)
CREATE TABLE `product_options` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `type` VARCHAR(20) NOT NULL COMMENT 'TOPPING(加料), SIZE(杯型)',
  `name` VARCHAR(50) NOT NULL COMMENT '选项名: 珍珠/大杯',
  `price` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '加价',
  `stock` INT COMMENT '独立库存(如限量Topping)',
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品定制选项';

-- 3. 营销与活动 (Marketing)
-- 优惠券模板
CREATE TABLE `coupon_templates` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `type` VARCHAR(20) NOT NULL COMMENT 'REDUCTION:满减, DISCOUNT:折扣',
  `value` DECIMAL(10, 2) NOT NULL COMMENT '减免金额或折扣率',
  `min_amount` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '使用门槛',
  `total_count` INT COMMENT '总发行量',
  `received_count` INT DEFAULT 0 COMMENT '已领取量',
  `start_time` DATETIME,
  `end_time` DATETIME,
  `days_valid` INT COMMENT '领取后有效天数',
  `rule_json` JSON COMMENT '复杂规则: 指定商品ID等',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板';

-- 用户优惠券
CREATE TABLE `user_coupons` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `template_id` BIGINT NOT NULL,
  `code` VARCHAR(50) COMMENT '核销码',
  `status` VARCHAR(20) DEFAULT 'UNUSED' COMMENT 'UNUSED, USED, EXPIRED',
  `expire_time` DATETIME NOT NULL,
  `used_order_id` BIGINT COMMENT '使用于哪个订单',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券实例';

-- 促销活动 
CREATE TABLE `promotions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `type` VARCHAR(50) NOT NULL COMMENT 'FULL_REDUCE, SECOND_HALF, FLASH_SALE',
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME NOT NULL,
  `rules_json` JSON NOT NULL COMMENT '存储具体规则参数',
  `is_active` BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='促销活动表';

-- 轮播图
CREATE TABLE `banners` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `image_url` VARCHAR(255) NOT NULL,
  `link_type` VARCHAR(20) COMMENT 'PRODUCT, URL, NONE',
  `link_value` VARCHAR(255),
  `sort` INT DEFAULT 0,
  `position` VARCHAR(20) DEFAULT 'HOME',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告轮播图';

-- 推荐与反馈
CREATE TABLE `user_recommendation_feedbacks` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `action` VARCHAR(20) COMMENT 'CLICK, PURCHASE, DISLIKE',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐算法反馈数据';

-- 4. 订单与支付 (Orders)


-- 订单主表
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号T2024...',
  `user_id` BIGINT NOT NULL,
  `store_id` BIGINT NOT NULL,
  `status` VARCHAR(20) NOT NULL COMMENT 'PAID, MAKING, READY, DELIVERED, REFUNDING...',
  `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总额',
  `pay_amount` DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
  `discount_amount` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '优惠金额',
  `delivery_fee` DECIMAL(10, 2) DEFAULT 0.00,
  -- 支付信息
  `pay_method` VARCHAR(20) COMMENT 'ALIPAY, WECHAT',
  `transaction_id` VARCHAR(64) COMMENT '第三方支付流水号',
  `pay_time` DATETIME,
  `order_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
  -- 履约信息
  `delivery_type` VARCHAR(20) NOT NULL COMMENT 'PICKUP, DELIVERY',
  `address_json` JSON COMMENT '快照: 收货地址信息',
  `pickup_code` VARCHAR(10) COMMENT '取餐码',
  `queue_number` INT COMMENT '当日排队号',
  `estimated_ready_time` DATETIME COMMENT '预估完成时间',
  `actual_ready_time` DATETIME COMMENT '实际制作完成时间',
  -- 互动与备注
  `remark` VARCHAR(200) COMMENT '用户备注',
  `remind_count` INT DEFAULT 0 COMMENT '催单次数',
  `last_remind_time` DATETIME COMMENT '上次催单时间',
  `is_commented` BOOLEAN DEFAULT FALSE COMMENT '是否已评价',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user` (`user_id`),
  KEY `idx_store_status` (`store_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 订单详情表
CREATE TABLE `order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `product_name` VARCHAR(100) NOT NULL COMMENT '快照名',
  `product_image` VARCHAR(255) COMMENT '快照图',
  `spec_json` JSON COMMENT '规格快照: {temp: "去冰", sugar: "半糖", toppings: [...]}',
  `price` DECIMAL(10, 2) NOT NULL COMMENT '单价快照',
  `quantity` INT NOT NULL,
  `total_price` DECIMAL(10, 2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品详情';

-- 订单退款记录
CREATE TABLE `order_refunds` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `order_no` VARCHAR(32) NOT NULL,
  `amount` DECIMAL(10, 2) NOT NULL,
  `reason` VARCHAR(200),
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED',
  `reply` VARCHAR(200) COMMENT '商家回复',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后退款单';

-- 投诉建议表
CREATE TABLE `complaints` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `order_id` BIGINT,
  `type` VARCHAR(50) NOT NULL COMMENT '投诉类型: 服务、质量、配送、其他',
  `content` TEXT NOT NULL,
  `images_json` JSON,
  `phone` VARCHAR(20),
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING, PROCESSING, RESOLVED',
  `reply` TEXT,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉建议表';

-- 订单评价与追评
CREATE TABLE `order_reviews` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `score` TINYINT NOT NULL COMMENT '1-5分',
  `content` TEXT COMMENT '评价内容',
  `images_json` JSON COMMENT '图片列表',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价';

CREATE TABLE `order_review_appends` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `review_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价追评';


-- 5. 门店与系统管理 (Store & System)


-- 门店表
CREATE TABLE `stores` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `address` VARCHAR(200) NOT NULL,
  `phone` VARCHAR(20),
  `business_status` TINYINT DEFAULT 1 COMMENT '1:营业 0:休息',
  `business_hours` VARCHAR(50) COMMENT '09:00-22:00',
  `is_auto_receipt` BOOLEAN DEFAULT TRUE COMMENT '是否自动接单',
  `current_wait_time` INT DEFAULT 0 COMMENT '当前预估等待(分钟)',
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否激活',
  `code` VARCHAR(20) UNIQUE COMMENT '门店编码',
  `address_json` JSON COMMENT '地址详情JSON',
  `manager_name` VARCHAR(50) COMMENT '负责人姓名',
  `manager_phone` VARCHAR(20) COMMENT '负责人电话',
  `status` VARCHAR(20) NOT NULL DEFAULT 'OPEN' COMMENT '状态',
  `business_hours_json` JSON COMMENT '营业时间JSON',
  `latitude` DECIMAL(10, 8) DEFAULT 0 COMMENT '纬度',
  `longitude` DECIMAL(11, 8) DEFAULT 0 COMMENT '经度',
  `delivery_radius` INT DEFAULT 0 COMMENT '配送半径',
  `delivery_fee` DECIMAL(10, 2) DEFAULT 0 COMMENT '配送费',
  `min_order_amount` DECIMAL(10, 2) DEFAULT 0 COMMENT '最低起送价',
  `is_auto_accept` BOOLEAN DEFAULT FALSE COMMENT '是否自动接单',
  `is_online_payment` BOOLEAN DEFAULT TRUE COMMENT '是否支持在线支付',
  `config_json` JSON COMMENT '配置JSON',
  `rating` DECIMAL(3, 2) DEFAULT 5.00 COMMENT '门店评分',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店表';

-- 打印机配置 
CREATE TABLE `print_printers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `store_id` BIGINT NOT NULL,
  `name` VARCHAR(50) COMMENT '前台打印机',
  `sn` VARCHAR(64) NOT NULL COMMENT '设备序列号',
  `key` VARCHAR(64) COMMENT '设备Key',
  `type` VARCHAR(20) COMMENT 'FEIE, YILIAN',
  `status` TINYINT DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印机设备';

CREATE TABLE `print_templates` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50),
  `content` TEXT COMMENT '打印模板内容',
  `is_default` BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小票打印模板';

-- RBAC 权限管理
CREATE TABLE `sys_roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '店长/店员',
  `code` VARCHAR(50) NOT NULL UNIQUE,
  `description` VARCHAR(100),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE `sys_permissions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT DEFAULT 0,
  `name` VARCHAR(50) NOT NULL COMMENT '菜单或按钮名',
  `code` VARCHAR(50) NOT NULL COMMENT '权限标识',
  `type` TINYINT COMMENT '1:菜单 2:按钮',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

CREATE TABLE `sys_role_permissions` (
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联';

-- 管理员/员工表
CREATE TABLE `sys_users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `real_name` VARCHAR(50),
  `store_id` BIGINT COMMENT '所属门店, null为总店',
  `status` TINYINT DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台管理员/员工';

CREATE TABLE `sys_user_roles` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联';

-- 操作日志 
CREATE TABLE `sys_operation_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT,
  `username` VARCHAR(50),
  `module` VARCHAR(50) COMMENT '操作模块',
  `action` VARCHAR(50) COMMENT '操作动作',
  `ip` VARCHAR(50),
  `params_json` TEXT COMMENT '请求参数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志';

-- 系统全局配置 
CREATE TABLE `system_configs` (
  `config_key` VARCHAR(100) NOT NULL,
  `config_value` TEXT,
  `description` VARCHAR(255),
  PRIMARY KEY (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置KV表';

-- 消息通知与推送记录
CREATE TABLE `notifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100),
  `content` TEXT,
  `target_type` VARCHAR(20) COMMENT 'ALL, MEMBER_LEVEL, INDIVIDUAL',
  `target_value` VARCHAR(100),
  `sent_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `status` VARCHAR(20) DEFAULT 'SENT',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推送任务记录';

-- 6. 用户互动与收藏 (User Interaction & Favorites)


-- 用户收藏门店
CREATE TABLE `user_favorite_stores` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `store_id` BIGINT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_store` (`user_id`, `store_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_store` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏门店';

-- 用户收藏商品
CREATE TABLE `user_favorite_products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏商品';

-- 用户搜索历史
CREATE TABLE `user_search_histories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `keyword` VARCHAR(100) NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户搜索历史';

-- 用户分享记录 
CREATE TABLE `user_shares` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `share_type` VARCHAR(20) COMMENT 'PRODUCT, STORE, COUPON',
  `target_id` BIGINT COMMENT '分享目标ID',
  `share_channel` VARCHAR(20) COMMENT 'WECHAT, QQ, WEIBO',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户分享记录';

-- 过敏原信息 
CREATE TABLE `allergens` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '过敏原名称: 花生、牛奶',
  `icon_url` VARCHAR(255),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='过敏原定义';

CREATE TABLE `product_allergens` (
  `product_id` BIGINT NOT NULL,
  `allergen_id` BIGINT NOT NULL,
  PRIMARY KEY (`product_id`, `allergen_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品过敏原关联';

-- 商品图片表 
CREATE TABLE `product_images` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `image_url` VARCHAR(255) NOT NULL,
  `sort_order` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- 门店图片表
CREATE TABLE `store_images` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `store_id` BIGINT NOT NULL,
  `image_url` VARCHAR(255) NOT NULL,
  `sort_order` INT DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_store` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店图片表';

-- 门店营业时间特殊日期 
CREATE TABLE `store_special_dates` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `store_id` BIGINT NOT NULL,
  `date` DATE NOT NULL,
  `business_hours` VARCHAR(50) COMMENT '特殊营业时间',
  `is_closed` BOOLEAN DEFAULT FALSE COMMENT '是否闭店',
  PRIMARY KEY (`id`),
  KEY `idx_store` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店特殊日期';

-- 门店营业时间 
CREATE TABLE `store_business_hours` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `store_id` BIGINT NOT NULL,
  `day_of_week` TINYINT NOT NULL COMMENT '1-7 (周一至周日)',
  `open_time` TIME NOT NULL,
  `close_time` TIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_store_day` (`store_id`, `day_of_week`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店营业时间';

-- 购物车项
CREATE TABLE `cart_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `spec_id` BIGINT,
  `sweetness` VARCHAR(20),
  `temperature` VARCHAR(20),
  `quantity` INT NOT NULL DEFAULT 1,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车项';

-- 购物车项定制
CREATE TABLE `cart_item_customizations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `cart_item_id` BIGINT NOT NULL,
  `option_id` BIGINT NOT NULL COMMENT 'product_options.id',
  `quantity` INT DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `idx_cart_item` (`cart_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车项定制';

-- 订单项定制
CREATE TABLE `order_item_customizations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_item_id` BIGINT NOT NULL,
  `option_id` BIGINT NOT NULL COMMENT 'product_options.id',
  `quantity` INT DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `idx_order_item` (`order_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项定制';

-- 订单状态时间线
CREATE TABLE `order_status_timelines` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `occurred_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态时间线';

-- 订单退款图片
CREATE TABLE `order_refund_images` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `refund_id` BIGINT NOT NULL,
  `image_url` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_refund` (`refund_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单退款图片';

-- 订单评价图片
CREATE TABLE `order_review_images` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `review_id` BIGINT NOT NULL,
  `image_url` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_review` (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价图片';

-- 积分兑换商品
CREATE TABLE `point_exchange_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `point_cost` INT NOT NULL,
  `stock` INT DEFAULT 0,
  `image_url` VARCHAR(255),
  `status` VARCHAR(20) DEFAULT 'AVAILABLE' COMMENT 'AVAILABLE, SOLD_OUT, DISABLED',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分兑换商品';

-- 积分兑换记录
CREATE TABLE `point_exchange_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `order_no` VARCHAR(64) NOT NULL,
  `total_points` INT NOT NULL,
  `total_items` INT NOT NULL,
  `status` VARCHAR(20) DEFAULT 'COMPLETED',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分兑换记录';

-- 积分交易记录
CREATE TABLE `point_transactions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(20) NOT NULL COMMENT 'EARN, SPEND, EXPIRE',
  `amount` INT NOT NULL,
  `balance_after` INT NOT NULL,
  `related_id` BIGINT COMMENT '关联订单/兑换ID',
  `remark` VARCHAR(200),
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分交易记录';

-- 验证码记录
CREATE TABLE `verification_codes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `phone` VARCHAR(20) NOT NULL,
  `code` VARCHAR(10) NOT NULL,
  `type` VARCHAR(20) COMMENT 'LOGIN, REGISTER, RESET_PASSWORD',
  `expire_time` DATETIME NOT NULL,
  `used` BOOLEAN DEFAULT FALSE,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码记录';

-- 页面配置 
CREATE TABLE `page_configs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `page` VARCHAR(50) NOT NULL COMMENT 'HOME, CATEGORY',
  `module` VARCHAR(50) NOT NULL COMMENT 'BANNER, QUICK_ENTRY',
  `config_json` JSON NOT NULL COMMENT '配置内容',
  `sort_order` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面配置';

-- 快捷入口
CREATE TABLE `quick_entries` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `icon_url` VARCHAR(255),
  `link_type` VARCHAR(20) COMMENT 'PRODUCT, CATEGORY, URL',
  `link_value` VARCHAR(255),
  `sort_order` INT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快捷入口';

-- 搜索关键词 
CREATE TABLE `search_keywords` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `keyword` VARCHAR(100) NOT NULL,
  `search_count` INT DEFAULT 0,
  `is_hot` BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索关键词';

-- 评价标签
CREATE TABLE `review_tags` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '标签: 服务好、出餐快',
  `type` VARCHAR(20) COMMENT 'POSITIVE, NEGATIVE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价标签';

CREATE TABLE `order_review_tags` (
  `review_id` BIGINT NOT NULL,
  `tag_id` BIGINT NOT NULL,
  PRIMARY KEY (`review_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价标签关联';

-- 每日统计
CREATE TABLE `daily_statistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `store_id` BIGINT,
  `date` DATE NOT NULL,
  `order_count` INT DEFAULT 0,
  `total_sales` DECIMAL(10,2) DEFAULT 0.00,
  `new_users` INT DEFAULT 0,
  `avg_order_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '客单价',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_store_date` (`store_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日统计';

-- 支付记录
CREATE TABLE `payments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `transaction_id` VARCHAR(64) COMMENT '第三方支付流水号',
  `amount` DECIMAL(10,2) NOT NULL,
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT 'SUCCESS, FAILED',
  `pay_time` DATETIME,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录';

-- 文件上传记录
CREATE TABLE `files` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `original_name` VARCHAR(255) NOT NULL,
  `stored_name` VARCHAR(255) NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  `size` BIGINT,
  `mime_type` VARCHAR(100),
  `uploader_id` BIGINT,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件上传记录';


-- 初始化基础数据


-- 初始化会员等级
INSERT INTO `member_levels` (`name`, `min_growth_value`, `discount_rate`, `privileges_json`) VALUES 
('普通会员', 0, 1.00, '{"birthday": false}'),
('黄金会员', 500, 0.95, '{"birthday": true}'),
('钻石会员', 2000, 0.88, '{"birthday": true, "speedy": true}');

-- 初始化系统配置
INSERT INTO `system_configs` (`config_key`, `config_value`, `description`) VALUES
('points_rule', '{"rate": 1, "use_rate": 100, "max_use": 100}', '积分抵现规则'),
('voice_remind', '{"new_order": true, "refund": true}', '语音提醒设置'),
('search_hot_words', '["珍珠奶茶", "杨枝甘露", "新品"]', '热门搜索推荐'),
('wechat_config', '{"appId": "wx1234567890", "appSecret": "abcdef1234567890"}', '微信小程序配置'),
('payment_gateway_url', 'https://openapi.alipaydev.com/gateway.do', '支付网关地址'),
('map_api_key', 'map_key_123456', '地图API密钥'),
('member_benefits', '生日双倍积分,每月领券,优先出餐', '会员权益描述');

-- 初始化超级管理员
INSERT INTO `sys_users` (`username`, `password`, `real_name`, `status`) VALUES
('admin', '$2a$10$xyz...', '超级管理员', 1);

-- 初始化一个测试用户 (ID=1)，用于开发阶段的兜底逻辑
INSERT INTO `users` (`id`, `username`, `password_hash`, `phone`, `nickname`, `status`, `registration_time`) VALUES
(1, 'testuser', '$2a$10$8.IlyE9uKwS.A.D68Z971u.6BaX.Y.P.8.Y.P.8.Y.P.8.Y.P.8', '13800138000', '测试用户', 'ACTIVE', NOW());

-- 初始化门店
INSERT INTO `stores` (`name`, `address`, `phone`, `business_status`, `business_hours`) VALUES
('深大店', '深圳市南山区粤海街道深圳大学', '0755-12345678', 1, '09:00-22:00'),
('科技园店', '深圳市南山区科苑路15号', '0755-87654321', 1, '08:30-21:30');

-- 初始化分类
INSERT INTO `categories` (`name`, `icon_url`, `sort_order`) VALUES
('人气Top', '/images/categories/hot.png', 1),
('经典奶茶', '/images/categories/milk-tea.png', 2),
('清爽果茶', '/images/categories/fruit-tea.png', 3),
('现磨咖啡', '/images/categories/coffee.png', 4),
('精选小食', '/images/categories/snack.png', 5);

INSERT INTO `categories` (`name`, `icon_url`, `sort_order`) VALUES
('热饮系列', '/images/categories/hot-drink.png', 6),
('低糖系列', '/images/categories/low-sugar.png', 7),
('季节限定', '/images/categories/seasonal.png', 8),
('冰淇淋', '/images/categories/ice-cream.png', 9);

-- 初始化商品
INSERT INTO `products` (`category_id`, `name`, `description`, `price`, `cost_price`, `main_image_url`, `status`, `sales`, `is_member_exclusive`, `member_price`) VALUES
(2, '珍珠奶茶', '经典台式风味，Q弹珍珠', 15.00, 5.00, '/images/products/milk-tea-1.jpg', 1, 1200, 0, 13.50),
(2, '波霸奶茶', '超大颗波霸，口感丰富', 16.00, 6.00, '/images/products/milk-tea-2.jpg', 1, 800, 0, 14.40),
(3, '杨枝甘露', '新鲜芒果配西柚，酸甜适口', 22.00, 8.00, '/images/products/fruit-tea-1.jpg', 1, 2500, 0, 19.80),
(3, '多肉葡萄', '手剥新鲜葡萄，搭配芝士奶盖', 28.00, 10.00, '/images/products/fruit-tea-2.jpg', 1, 3000, 1, 25.00),
(4, '生椰拿铁', '清甜生椰水遇上浓缩咖啡', 18.00, 7.00, '/images/products/coffee-1.jpg', 1, 1500, 0, 16.20);

-- 初始化规格
INSERT INTO `product_specs` (`product_id`, `type`, `name`, `is_default`) VALUES
(1, 'TEMPERATURE', '常规冰', 1), (1, 'TEMPERATURE', '少冰', 0), (1, 'TEMPERATURE', '去冰', 0), (1, 'TEMPERATURE', '热', 0),
(1, 'SWEETNESS', '标准糖', 1), (1, 'SWEETNESS', '七分糖', 0), (1, 'SWEETNESS', '五分糖', 0), (1, 'SWEETNESS', '三分糖', 0), (1, 'SWEETNESS', '不加糖', 0);

-- 初始化加料
INSERT INTO `product_options` (`product_id`, `type`, `name`, `price`) VALUES
(1, 'TOPPING', '珍珠', 2.00), (1, 'TOPPING', '椰果', 2.00), (1, 'TOPPING', '布丁', 3.00), (1, 'TOPPING', '芝士奶盖', 5.00);

-- 初始化优惠券模板
INSERT INTO `coupon_templates` (`name`, `type`, `reduction_amount`, `min_amount`, `total_count`, `received_count`, `start_time`, `end_time`) VALUES
('新人5元无门槛', 'REDUCTION', 5.00, 0.00, 1000, 100, '2024-01-01 00:00:00', '2025-12-31 23:59:59'),
('满30减10', 'REDUCTION', 10.00, 30.00, 500, 50, '2024-01-01 00:00:00', '2025-12-31 23:59:59'),
('分享专享8折券', 'DISCOUNT', 0.80, 20.00, 2000, 200, '2024-01-01 00:00:00', '2025-12-31 23:59:59');

-- 初始化轮播图
INSERT INTO `banners` (`image_url`, `link_type`, `link_value`, `sort`) VALUES
('/images/banners/banner1.jpg', 'PRODUCT', '3', 1),
('/images/banners/banner2.jpg', 'URL', 'https://example.com/activity', 2);

-- 初始化积分商品
INSERT INTO `point_exchange_items` (`name`, `description`, `point_cost`, `stock`, `image_url`, `status`) VALUES
('5元代金券', '全场通用，无门槛', 500, 999, '/images/points/coupon-5.png', 'AVAILABLE'),
('定制马克杯', '奶茶店专属定制', 2000, 50, '/images/points/cup.png', 'AVAILABLE');

SET FOREIGN_KEY_CHECKS = 1;