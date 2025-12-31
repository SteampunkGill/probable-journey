import pymysql
import os

# 数据库连接配置
config = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '13603994106',
    'database': 'milktea_db',
    'charset': 'utf8mb4',
    'cursorclass': pymysql.cursors.DictCursor
}

output_file = 'milktea-backend/src/main/resources/db/migration/initial.sql'

def export_db():
    try:
        connection = pymysql.connect(**config)
        with connection.cursor() as cursor:
            # 获取所有表
            cursor.execute("SHOW TABLES")
            tables = cursor.fetchall()
            
            with open(output_file, 'w', encoding='utf-8') as f:
                f.write("-- 自动生成的数据库导出脚本\n")
                f.write("SET NAMES utf8mb4;\n")
                f.write("SET FOREIGN_KEY_CHECKS = 0;\n\n")
                
                for table_dict in tables:
                    table_name = list(table_dict.values())[0]
                    print(f"正在导出表结构: {table_name}")
                    
                    # 导出创表语句
                    cursor.execute(f"SHOW CREATE TABLE `{table_name}`")
                    create_table_sql = cursor.fetchone()['Create Table']
                    f.write(f"-- ----------------------------\n")
                    f.write(f"-- Table structure for {table_name}\n")
                    f.write(f"-- ----------------------------\n")
                    f.write(f"DROP TABLE IF EXISTS `{table_name}`;\n")
                    f.write(f"{create_table_sql};\n\n")
                    
                    # 导出数据
                    print(f"正在导出表数据: {table_name}")
                    cursor.execute(f"SELECT * FROM `{table_name}`")
                    rows = cursor.fetchall()
                    
                    if rows:
                        f.write(f"-- ----------------------------\n")
                        f.write(f"-- Records of {table_name}\n")
                        f.write(f"-- ----------------------------\n")
                        
                        for row in rows:
                            columns = ", ".join([f"`{k}`" for k in row.keys()])
                            values = []
                            for v in row.values():
                                if v is None:
                                    values.append("NULL")
                                elif isinstance(v, (int, float)):
                                    values.append(str(v))
                                else:
                                    # 转义单引号
                                    escaped_v = str(v).replace("'", "''")
                                    values.append(f"'{escaped_v}'")
                            
                            values_str = ", ".join(values)
                            f.write(f"INSERT INTO `{table_name}` ({columns}) VALUES ({values_str});\n")
                        f.write("\n")
                
                f.write("SET FOREIGN_KEY_CHECKS = 1;\n")
                
        print(f"\n导出成功！文件已保存至: {output_file}")
        
    except Exception as e:
        print(f"发生错误: {e}")
    finally:
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    export_db()