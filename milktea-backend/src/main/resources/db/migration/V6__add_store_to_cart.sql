-- 向 cart_items 表新增 store_id 字段
ALTER TABLE cart_items ADD COLUMN store_id BIGINT DEFAULT NULL COMMENT '门店ID' AFTER user_id;

-- 如果存在唯一约束导致无法重复添加商品，可以考虑删除该约束（如果业务允许）
-- 或者在代码层面处理（已在 CartService 中处理：改为累加数量）