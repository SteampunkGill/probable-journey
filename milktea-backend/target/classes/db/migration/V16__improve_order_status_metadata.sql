-- 完善订单状态字段注释，明确各状态含义，不改变字段类型和逻辑
-- 对应后端 OrderService.java 中的状态映射逻辑优化
ALTER TABLE `orders` MODIFY COLUMN `status` varchar(20) NOT NULL COMMENT '订单状态: PENDING_PAYMENT-待支付, PAID-已支付/待接单, MAKING-制作中, READY-待取餐, DELIVERING-配送中, DELIVERED-已送达, COMPLETED-已完成, FINISHED-已结束, REFUNDING-退款中, REFUNDED-已退款, CANCELLED-已取消, REVIEWED-已评价';