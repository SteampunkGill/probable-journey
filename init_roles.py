import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="13603994106",
        database="milktea_db"
    )
    cursor = conn.cursor()
    
    # 插入 ADMIN 角色，根据实际表结构：id, code, description, name
    print("正在初始化系统角色数据...")
    cursor.execute("INSERT INTO sys_roles (code, name, description) VALUES (%s, %s, %s)", 
                   ("ADMIN", "超级管理员", "拥有系统所有权限"))
    
    conn.commit()
    print("成功初始化 ADMIN 角色！")
    
    cursor.close()
    conn.close()
except Exception as e:
    print(f"Error: {e}")