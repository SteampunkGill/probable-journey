-- 为 coupon_templates 表的现有字段添加默认值，确保旧逻辑和新逻辑的兼容性
-- 遵循“只增不减、静默兼容”原则

ALTER TABLE coupon_templates 
    MODIFY COLUMN acquire_limit int NOT NULL DEFAULT 1 COMMENT '每人限领数量',
    MODIFY COLUMN is_active bit(1) NOT NULL DEFAULT b'1' COMMENT '是否激活',
    MODIFY COLUMN remaining_quantity int NOT NULL DEFAULT 0 COMMENT '剩余数量',
    MODIFY COLUMN usage_scope varchar(20) NOT NULL DEFAULT 'ALL' COMMENT '使用范围',
    MODIFY COLUMN created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    MODIFY COLUMN updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间';

-- 确保现有数据的逻辑一致性
UPDATE coupon_templates SET acquire_limit = 1 WHERE acquire_limit IS NULL;
UPDATE coupon_templates SET is_active = b'1' WHERE is_active IS NULL;
UPDATE coupon_templates SET usage_scope = 'ALL' WHERE usage_scope IS NULL;