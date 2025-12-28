import mysql.connector
import sys

def execute_sql(sql):
    try:
        conn = mysql.connector.connect(
            host="localhost",
            user="root",
            password="13603994106",
            database="milktea_db"
        )
        cursor = conn.cursor()
        cursor.execute(sql)
        if sql.strip().upper().startswith("SELECT") or sql.strip().upper().startswith("DESC") or sql.strip().upper().startswith("SHOW"):
            rows = cursor.fetchall()
            for row in rows:
                print(row)
        else:
            conn.commit()
            print("执行成功")
        cursor.close()
        conn.close()
    except Exception as e:
        print(f"错误: {e}")

if __name__ == "__main__":
    if len(sys.argv) > 1:
        execute_sql(sys.argv[1])
    else:
        print("请提供SQL语句")