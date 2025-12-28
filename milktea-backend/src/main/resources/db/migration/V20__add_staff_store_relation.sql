-- 为系统用户表增加门店关联字段
-- 遵循“只增不减”和“静默兼容”原则，字段允许为 NULL
ALTER TABLE sys_users ADD COLUMN store_id BIGINT DEFAULT NULL COMMENT '所属门店ID';

-- 添加外键约束（可选，根据项目习惯，此处为保证数据一致性建议添加，但设置为 SET NULL 以防破坏旧数据）
-- ALTER TABLE sys_users ADD CONSTRAINT fk_sys_user_store FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE SET NULL;