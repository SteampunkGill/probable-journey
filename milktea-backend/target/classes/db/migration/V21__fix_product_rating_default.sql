-- 修复商品评分字段必填导致的新增失败问题
-- 按照“无侵入、强隔离”原则，为现有字段添加默认值，确保旧业务逻辑无感
ALTER TABLE products MODIFY COLUMN rating decimal(2,1) DEFAULT 5.0 COMMENT '商品评分-设置默认值以兼容新增逻辑';