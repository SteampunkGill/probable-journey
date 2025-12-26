import mysql.connector

def get_product_cost(cursor, product_id):
    cursor.execute("SELECT name, cost_price FROM products WHERE id = %s", (product_id,))
    return cursor.fetchone()

def get_ingredient_cost(cursor, ingredient_id):
    cursor.execute("SELECT name, cost_per_unit FROM ingredients WHERE id = %s", (ingredient_id,))
    return cursor.fetchone()

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="13603994106",
        database="milktea_db"
    )
    cursor = conn.cursor()

    # 1. 检查初始状态 (假设商品ID 1 是珍珠奶茶)
    product_id = 1
    res = get_product_cost(cursor, product_id)
    if res:
        print(f"初始状态 - 商品: {res[0]}, 成本: {res[1]}")
    else:
        print("未找到商品ID 1")
        exit()

    # 2. 测试修改配方数量
    print("\n--- 测试1: 修改配方数量 ---")
    # 假设配方中 ingredient_id=1 (珍珠) 的数量
    cursor.execute("SELECT quantity FROM product_recipes WHERE product_id = %s AND ingredient_id = 1", (product_id,))
    old_qty = cursor.fetchone()[0]
    new_qty = old_qty + 10
    print(f"将珍珠数量从 {old_qty} 修改为 {new_qty}")
    
    cursor.execute("UPDATE product_recipes SET quantity = %s WHERE product_id = %s AND ingredient_id = 1", (new_qty, product_id))
    conn.commit()
    
    res = get_product_cost(cursor, product_id)
    print(f"修改后 - 商品: {res[0]}, 成本: {res[1]} (预期应增加 10 * 珍珠单价)")

    # 3. 测试修改原料单价
    print("\n--- 测试2: 修改原料单价 ---")
    ing_id = 1 # 珍珠
    res_ing = get_ingredient_cost(cursor, ing_id)
    old_price = res_ing[1]
    new_price = old_price + 0.01
    print(f"将原料 {res_ing[0]} 的单价从 {old_price} 修改为 {new_price}")
    
    cursor.execute("UPDATE ingredients SET cost_per_unit = %s WHERE id = %s", (new_price, ing_id))
    conn.commit()
    
    res = get_product_cost(cursor, product_id)
    print(f"修改原料单价后 - 商品: {res[0]}, 成本: {res[1]} (预期应随之更新)")

    # 还原数据 (可选)
    # cursor.execute("UPDATE product_recipes SET quantity = %s WHERE product_id = %s AND ingredient_id = 1", (old_qty, product_id))
    # cursor.execute("UPDATE ingredients SET cost_per_unit = %s WHERE id = %s", (old_price, ing_id))
    # conn.commit()

    cursor.close()
    conn.close()
except Exception as e:
    print(f"Error: {e}")