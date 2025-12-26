import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="13603994106",
        database="milktea_db"
    )
    cursor = conn.cursor()
    cursor.execute("DESCRIBE products")
    columns = cursor.fetchall()
    print("Products table structure:")
    for col in columns:
        print(col)
    cursor.close()
    conn.close()
except Exception as e:
    print(f"Error: {e}")