import requests
import json
import mysql.connector
import time

# 配置信息
BASE_URL = "http://localhost:8081"
DB_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": "password", # 请根据实际情况修改
    "database": "milktea_db"
}

def test_admin_registration():
    print("开始测试管理员注册流程...")
    
    # 1. 准备测试数据
    timestamp = int(time.time())
    username = f"admin_{timestamp}"
    password = "password123"
    real_name = f"测试管理员_{timestamp}"
    
    register_data = {
        "username": username,
        "password": password,
        "phone": f"138{str(timestamp)[-8:]}",
        "role": "admin"
    }
    
    print(f"正在通过注册接口创建管理员用户: {username}")
    
    headers = {
        "Content-Type": "application/json"
    }

    # 发送注册请求
    try:
        response = requests.post(f"{BASE_URL}/api/v1/app/auth/register", json=register_data, headers=headers)
        print(f"接口返回状态码: {response.status_code}")
        print(f"接口返回内容: {response.text}")
        
        if response.status_code != 200:
            print("注册接口请求失败，请检查后端服务状态。")
    except Exception as e:
        print(f"请求异常: {e}")

    # 3. 数据库验证
    print("\n正在验证数据库状态...")
    try:
        conn = mysql.connector.connect(
            host=DB_CONFIG["host"],
            user=DB_CONFIG["user"],
            password=DB_CONFIG["password"],
            database=DB_CONFIG["database"]
        )
        cursor = conn.cursor(dictionary=True)
        
        # 检查用户是否存在
        cursor.execute("SELECT id, username, password_hash FROM sys_users WHERE username = %s", (username,))
        user = cursor.fetchone()
        
        if user:
            print(f"成功: 用户 {username} 已在 sys_users 表中创建。ID: {user['id']}")
            user_id = user['id']
            
            # 检查角色关联
            cursor.execute("""
                SELECT r.code, r.name 
                FROM sys_roles r 
                JOIN sys_user_roles ur ON r.id = ur.role_id 
                WHERE ur.user_id = %s
            """, (user_id,))
            roles = cursor.fetchall()
            
            role_codes = [r['code'] for r in roles]
            print(f"用户拥有的角色: {role_codes}")
            
            if "ADMIN" in role_codes:
                print("验证成功: 管理员权限已正确分配！")
            else:
                print("验证失败: 管理员权限未分配。")
                
            # 验证密码是否已加密 (不应等于原始密码)
            if user['password_hash'] != password:
                print("验证成功: 密码已加密存储。")
            else:
                print("验证失败: 密码以明文存储！")
        else:
            print(f"失败: 用户 {username} 未能在数据库中找到。")
            
        cursor.close()
        conn.close()
    except mysql.connector.Error as err:
        print(f"数据库连接错误: {err}")

if __name__ == "__main__":
    test_admin_registration()