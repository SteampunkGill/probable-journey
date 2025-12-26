import mysql.connector
from decimal import Decimal

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="13603994106",
        database="milktea_db"
    )
    cursor = conn.cursor()

    # 1. 查找一个有配方的商品
    cursor.execute("""
        SELECT p.id, p.name, p.cost_price 
        FROM products p 
        JOIN product_recipes pr ON p.id = pr.product_id 
        LIMIT 1
    """)
    product = cursor.fetchone()
    
    if not product:
        print("数据库中没有带配方的商品，请先运行 insert_ingredients_test_data.sql")
        exit()
        
    p_id, p_name, p_cost = product
    p_cost = Decimal(str(p_cost))
    print(f"测试商品: {p_name} (ID: {p_id}), 当前成本: {p_cost}")

    # 2. 查找该商品的一个原料
    cursor.execute("""
        SELECT i.id, i.name, i.cost_per_unit, pr.quantity 
        FROM ingredients i 
        JOIN product_recipes pr ON i.id = pr.ingredient_id 
        WHERE pr.product_id = %s 
        LIMIT 1
    """, (p_id,))
    ingredient = cursor.fetchone()
    i_id, i_name, i_price, pr_qty = ingredient
    i_price = Decimal(str(i_price))
    pr_qty = Decimal(str(pr_qty))
    print(f"测试原料: {i_name} (ID: {i_id}), 单价: {i_price}, 数量: {pr_qty}")

    # 3. 测试修改配方数量
    print("\n--- 测试1: 修改配方数量 ---")
    new_qty = pr_qty + Decimal('10.00')
    print(f"将 {i_name} 数量从 {pr_qty} 修改为 {new_qty}")
    cursor.execute("UPDATE product_recipes SET quantity = %s WHERE product_id = %s AND ingredient_id = %s", (new_qty, p_id, i_id))
    conn.commit()
    
    cursor.execute("SELECT cost_price FROM products WHERE id = %s", (p_id,))
    new_p_cost = Decimal(str(cursor.fetchone()[0]))
    diff = new_p_cost - p_cost
    expected_diff = Decimal('10.00') * i_price
    print(f"修改后商品成本: {new_p_cost}, 差异: {diff}, 预期差异: {expected_diff}")

    # 4. 测试修改原料单价
    print("\n--- 测试2: 修改原料单价 ---")
    price_increase = Decimal('1.00')
    new_i_price = i_price + price_increase
    print(f"将原料 {i_name} 的单价从 {i_price} 修改为 {new_i_price}")
    cursor.execute("UPDATE ingredients SET cost_per_unit = %s WHERE id = %s", (new_i_price, i_id))
    conn.commit()
    
    cursor.execute("SELECT cost_price FROM products WHERE id = %s", (p_id,))
    final_p_cost = Decimal(str(cursor.fetchone()[0]))
    diff2 = final_p_cost - new_p_cost
    expected_diff2 = new_qty * price_increase
    print(f"修改原料单价后商品成本: {final_p_cost}, 差异: {diff2}, 预期差异: {expected_diff2}")

    cursor.close()
    conn.close()
except Exception as e:
    import traceback
    traceback.print_exc()
    print(f"Error: {e}")