import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="13603994106",
        database="milktea_db"
    )
    cursor = conn.cursor()
    cursor.execute("SELECT id, username, status, is_active FROM users LIMIT 10")
    rows = cursor.fetchall()
    for row in rows:
        print(row)
    cursor.close()
    conn.close()
except Exception as e:
    print(f"Error: {e}")