-- 1. 创建更新商品成本的存储过程
DELIMITER //

DROP PROCEDURE IF EXISTS calculate_and_update_product_cost //

CREATE PROCEDURE calculate_and_update_product_cost(IN p_product_id BIGINT)
BEGIN
    DECLARE v_total_cost DECIMAL(10, 2) DEFAULT 0.00;

    -- 计算该商品所有配方原料的总成本
    SELECT SUM(pr.quantity * i.cost_per_unit)
    INTO v_total_cost
    FROM product_recipes pr
    JOIN ingredients i ON pr.ingredient_id = i.id
    WHERE pr.product_id = p_product_id;

    -- 如果没有配方，成本设为 0
    IF v_total_cost IS NULL THEN
        SET v_total_cost = 0.00;
    END IF;

    -- 更新 products 表
    UPDATE products 
    SET cost_price = v_total_cost,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_product_id;
END //

DELIMITER ;

-- 2. 创建 product_recipes 表的触发器

-- 插入配方时更新成本
DELIMITER //
DROP TRIGGER IF EXISTS trig_product_recipes_insert //
CREATE TRIGGER trig_product_recipes_insert
AFTER INSERT ON product_recipes
FOR EACH ROW
BEGIN
    CALL calculate_and_update_product_cost(NEW.product_id);
END //

-- 更新配方（如修改数量）时更新成本
DROP TRIGGER IF EXISTS trig_product_recipes_update //
CREATE TRIGGER trig_product_recipes_update
AFTER UPDATE ON product_recipes
FOR EACH ROW
BEGIN
    -- 如果 product_id 变了（虽然通常不会），更新新旧两个商品
    CALL calculate_and_update_product_cost(OLD.product_id);
    IF NEW.product_id <> OLD.product_id THEN
        CALL calculate_and_update_product_cost(NEW.product_id);
    END IF;
END //

-- 删除配方时更新成本
DROP TRIGGER IF EXISTS trig_product_recipes_delete //
CREATE TRIGGER trig_product_recipes_delete
AFTER DELETE ON product_recipes
FOR EACH ROW
BEGIN
    CALL calculate_and_update_product_cost(OLD.product_id);
END //

-- 3. 创建 ingredients 表的触发器

-- 当原料的单位成本改变时，更新所有关联商品的成本
DROP TRIGGER IF EXISTS trig_ingredients_cost_update //
CREATE TRIGGER trig_ingredients_cost_update
AFTER UPDATE ON ingredients
FOR EACH ROW
BEGIN
    -- 只有当单位成本发生变化时才触发
    IF OLD.cost_per_unit <> NEW.cost_per_unit OR (OLD.cost_per_unit IS NULL AND NEW.cost_per_unit IS NOT NULL) THEN
        -- 找到所有使用该原料的商品并更新
        BEGIN
            DECLARE done INT DEFAULT FALSE;
            DECLARE v_product_id BIGINT;
            DECLARE cur CURSOR FOR 
                SELECT DISTINCT product_id FROM product_recipes WHERE ingredient_id = NEW.id;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

            OPEN cur;
            read_loop: LOOP
                FETCH cur INTO v_product_id;
                IF done THEN
                    LEAVE read_loop;
                END IF;
                CALL calculate_and_update_product_cost(v_product_id);
            END LOOP;
            CLOSE cur;
        END;
    END IF;
END //

DELIMITER ;