import requests
import json

BASE_URL = "http://localhost:8081"
ADMIN_SECRET = "13603994106"

def create_admin():
    username = "admin888"
    password = "password123"
    
    register_data = {
        "username": username,
        "password": password,
        "phone": "13888888888",
        "role": "admin",
        "adminCode": ADMIN_SECRET
    }
    
    print(f"正在创建固定管理员账号: {username}")
    try:
        resp = requests.post(f"{BASE_URL}/api/v1/app/auth/register", json=register_data)
        print(f"响应: {resp.text}")
    except Exception as e:
        print(f"错误: {e}")

if __name__ == "__main__":
    create_admin()