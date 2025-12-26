import mysql.connector

def execute_sql():
    try:
        # 连接数据库
        conn = mysql.connector.connect(
            host="localhost",
            user="root",
            password="13603994106",
            database="milktea_db"
        )
        cursor = conn.cursor()
        
        print("开始执行数据库同步脚本...")

        # 1. 向原料表新增单位成本字段
        try:
            cursor.execute("ALTER TABLE `ingredients` ADD COLUMN `cost_per_unit` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '单位成本'")
            print("成功：向 ingredients 表添加了 cost_per_unit 字段")
        except mysql.connector.Error as err:
            if err.errno == 1060: # Duplicate column name
                print("跳过：ingredients 表已存在 cost_per_unit 字段")
            else:
                print(f"错误：{err}")

        # 2. 向商品表新增成本价格字段
        try:
            cursor.execute("ALTER TABLE `products` ADD COLUMN `cost_price` DECIMAL(10, 2) DEFAULT 0.00 COMMENT '成本价格'")
            print("成功：向 products 表添加了 cost_price 字段")
        except mysql.connector.Error as err:
            if err.errno == 1060: # Duplicate column name
                print("跳过：products 表已存在 cost_price 字段")
            else:
                print(f"错误：{err}")

        # 3. 更新现有测试数据的成本价（可选，为了演示效果）
        cursor.execute("UPDATE `products` SET `cost_price` = `price` * 0.4 WHERE `cost_price` = 0")
        print(f"成功：更新了 {cursor.rowcount} 条商品的默认成本价")

        conn.commit()
        print("数据库同步完成！")
        
        cursor.close()
        conn.close()
    except Exception as e:
        print(f"连接错误: {e}")

if __name__ == "__main__":
    execute_sql()