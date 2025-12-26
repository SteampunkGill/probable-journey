import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="13603994106",
        database="milktea_db"
    )
    cursor = conn.cursor(dictionary=True)
    print("--- sys_roles 表内容 ---")
    cursor.execute("SELECT * FROM sys_roles")
    roles = cursor.fetchall()
    for role in roles:
        print(role)
    
    print("\n--- sys_users 表结构 ---")
    cursor.execute("DESC sys_users")
    for col in cursor.fetchall():
        print(col)
        
    cursor.close()
    conn.close()
except Exception as e:
    print(f"Error: {e}")