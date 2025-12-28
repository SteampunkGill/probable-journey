-- 向 orders 表新增 balance_discount_amount 字段，用于记录余额抵扣金额
ALTER TABLE orders ADD COLUMN balance_discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '余额抵扣金额';

-- 修复 orders 表中缺失默认值的字段
ALTER TABLE orders MODIFY COLUMN balance_used DECIMAL(10, 2) DEFAULT 0.00 COMMENT '余额抵扣金额';
ALTER TABLE orders MODIFY COLUMN points_used INT DEFAULT 0 COMMENT '使用的积分';
ALTER TABLE orders MODIFY COLUMN points_discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '积分抵扣金额';
ALTER TABLE orders MODIFY COLUMN package_fee DECIMAL(10, 2) DEFAULT 0.00 COMMENT '包装费';
ALTER TABLE orders MODIFY COLUMN is_rated BIT(1) DEFAULT b'0' COMMENT '是否已评价';
ALTER TABLE orders MODIFY COLUMN product_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '商品总额';
ALTER TABLE orders MODIFY COLUMN pay_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '实付金额';
ALTER TABLE orders MODIFY COLUMN type VARCHAR(20) DEFAULT 'PICKUP' COMMENT '订单类型';
ALTER TABLE orders MODIFY COLUMN actual_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '实际支付金额';

-- 修复 order_items 表中缺失默认值的字段
ALTER TABLE order_items MODIFY COLUMN created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间';
ALTER TABLE order_items MODIFY COLUMN price_at_order DECIMAL(10, 2) DEFAULT 0.00 COMMENT '下单时价格';
ALTER TABLE order_items MODIFY COLUMN subtotal DECIMAL(10, 2) DEFAULT 0.00 COMMENT '小计';

-- 修复 order_status_timelines 表中缺失默认值的字段
ALTER TABLE order_status_timelines MODIFY COLUMN created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间';
ALTER TABLE order_status_timelines MODIFY COLUMN is_current BIT(1) DEFAULT b'1' COMMENT '是否当前状态';
ALTER TABLE order_status_timelines MODIFY COLUMN status_text VARCHAR(50) DEFAULT '' COMMENT '状态文本';
ALTER TABLE order_status_timelines MODIFY COLUMN time DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '状态时间';