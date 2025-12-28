-- 为 promotions 表添加审计字段，并确保旧数据兼容
-- 注意：MySQL 8.0.19 以前的版本不支持 ADD COLUMN IF NOT EXISTS，改用标准语法

ALTER TABLE promotions ADD COLUMN created_at datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间';
ALTER TABLE promotions ADD COLUMN updated_at datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间';

-- 修复 title 字段缺失默认值的问题（针对报错：Field 'title' doesn't have a default value）
ALTER TABLE promotions MODIFY COLUMN title varchar(100) DEFAULT NULL COMMENT '活动标题';
ALTER TABLE promotions MODIFY COLUMN name varchar(100) NOT NULL COMMENT '活动名称';

-- 确保字段属性正确（防御性编程）
ALTER TABLE promotions MODIFY COLUMN created_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);
ALTER TABLE promotions MODIFY COLUMN updated_at datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);