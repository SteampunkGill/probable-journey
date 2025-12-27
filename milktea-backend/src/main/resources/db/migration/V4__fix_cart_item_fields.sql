-- 向 cart_items 表新增字段，修复 [Field 'is_selected' doesn't have a default value] 错误
-- 按照“只增不减”原则，如果字段已存在则修改其默认值，如果不存在则新增

-- 1. 修复 is_selected 字段
ALTER TABLE `cart_items` MODIFY COLUMN `is_selected` BIT(1) NOT NULL DEFAULT b'1' COMMENT '是否选中';

-- 2. 修复 is_valid 字段
ALTER TABLE `cart_items` MODIFY COLUMN `is_valid` BIT(1) NOT NULL DEFAULT b'1' COMMENT '是否有效';

-- 3. 修复 price_at_add 字段
ALTER TABLE `cart_items` MODIFY COLUMN `price_at_add` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '加入时的价格';

-- 4. 确保其他可能缺失的字段存在（如果不存在则添加）
-- 注意：MySQL 不支持 IF NOT EXISTS 在 ALTER TABLE ADD COLUMN 中，
-- 但由于 initial.sql 已经被修改，且 JPA ddl-auto: update 会尝试同步，
-- 这里提供增量 SQL 用于手动执行或 Flyway 同步。

-- 如果字段不存在，可以使用以下方式（在存储过程中或直接执行，这里采用直接执行，假设环境允许）
-- ALTER TABLE `cart_items` ADD COLUMN `original_price_at_add` DECIMAL(10, 2) DEFAULT NULL COMMENT '加入时的原价';
-- ALTER TABLE `cart_items` ADD COLUMN `invalid_reason` VARCHAR(255) DEFAULT NULL COMMENT '失效原因';