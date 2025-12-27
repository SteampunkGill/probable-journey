-- 修复 cart_items 表字段默认值缺失的问题
-- 按照“只增不减”原则，修改现有字段的默认值

-- 1. 确保 is_selected 字段有默认值 1 (true)
ALTER TABLE `cart_items` ALTER COLUMN `is_selected` SET DEFAULT 1;

-- 2. 确保 is_valid 字段有默认值 1 (true)
ALTER TABLE `cart_items` ALTER COLUMN `is_valid` SET DEFAULT 1;

-- 3. 确保 price_at_add 字段有默认值 0.00
ALTER TABLE `cart_items` ALTER COLUMN `price_at_add` SET DEFAULT 0.00;