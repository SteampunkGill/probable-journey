-- 向表 orders 修改字段 order_time 为可空并添加默认值
ALTER TABLE orders MODIFY COLUMN order_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间';