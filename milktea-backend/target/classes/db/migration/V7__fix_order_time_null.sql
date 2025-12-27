-- 向 orders 表新增 order_time 字段（如果不存在），并确保其可空
-- 实际上 initial.sql 中已经定义了 order_time，但可能在某些环境下未正确同步或存在约束冲突
-- 这里执行增量更新，确保 order_time 存在且允许为 NULL

ALTER TABLE `orders` MODIFY COLUMN `order_time` DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '订单创建时间';