import mysql.connector
import re

def apply_sql_file(filename, cursor, conn):
    with open(filename, 'r', encoding='utf-8') as f:
        sql_content = f.read()

    # 处理 DELIMITER
    # 简单的正则来分割 SQL 语句，考虑到 DELIMITER //
    
    # 先移除注释
    sql_content = re.sub(r'--.*', '', sql_content)
    
    # 寻找 DELIMITER 块
    parts = re.split(r'DELIMITER\s+(\S+)', sql_content)
    
    current_delimiter = ';'
    
    for i in range(len(parts)):
        part = parts[i].strip()
        if not part:
            continue
            
        if i % 2 == 1:
            # 这是新的 delimiter 符号
            current_delimiter = part
            continue
        
        # 这是 SQL 语句块，使用当前的 delimiter 分割
        statements = part.split(current_delimiter)
        for stmt in statements:
            stmt = stmt.strip()
            if stmt:
                try:
                    print(f"Executing statement: {stmt[:50]}...")
                    cursor.execute(stmt)
                    conn.commit()
                except Exception as e:
                    print(f"Error executing statement: {e}")

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="13603994106",
        database="milktea_db"
    )
    cursor = conn.cursor()
    
    print("Applying triggers and stored procedures...")
    apply_sql_file('create_cost_calculation_triggers.sql', cursor, conn)
    
    print("Triggers applied successfully.")
    
    # 初始运行一次，更新现有商品的成本
    print("Updating existing product costs...")
    cursor.execute("SELECT id FROM products")
    product_ids = cursor.fetchall()
    for (pid,) in product_ids:
        cursor.execute("CALL calculate_and_update_product_cost(%s)", (pid,))
    conn.commit()
    print("Existing product costs updated.")

    cursor.close()
    conn.close()
except Exception as e:
    print(f"Error: {e}")