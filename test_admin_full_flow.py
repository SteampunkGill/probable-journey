import requests
import json
import time

# 配置信息
BASE_URL = "http://localhost:8081"
ADMIN_SECRET = "13603994106"

def test_admin_flow():
    print("=== 开始管理员全流程测试 ===")
    
    # 1. 准备测试数据
    timestamp = int(time.time())
    # 缩短用户名长度，确保在 20 字符以内
    username = f"adm_{str(timestamp)[-6:]}"
    password = "password123"
    phone = f"139{str(timestamp)[-8:]}"
    
    # 2. 注册管理员
    print(f"\n[步骤1] 正在注册管理员: {username}")
    register_data = {
        "username": username,
        "password": password,
        "phone": phone,
        "role": "admin",
        "adminCode": ADMIN_SECRET
    }
    
    try:
        reg_resp = requests.post(f"{BASE_URL}/api/v1/app/auth/register", json=register_data)
        print(f"注册响应状态码: {reg_resp.status_code}")
        print(f"注册响应内容: {reg_resp.text}")
        
        if reg_resp.status_code != 200:
            print("注册失败，停止测试。")
            return
            
        # 3. 登录
        print(f"\n[步骤2] 正在登录管理员: {username}")
        login_data = {
            "username": username,
            "password": password
        }
        login_resp = requests.post(f"{BASE_URL}/api/v1/app/auth/login", json=login_data)
        print(f"登录响应状态码: {login_resp.status_code}")
        login_result = login_resp.json()
        print(f"登录响应内容: {json.dumps(login_result, indent=2, ensure_ascii=False)}")
        
        if login_resp.status_code != 200 or not login_result.get('data'):
            print("登录失败，停止测试。")
            return
            
        token = login_result['data']['token']
        is_admin = login_result['data']['isAdmin']
        
        print(f"获取到 Token: {token[:20]}...")
        print(f"isAdmin 字段值: {is_admin}")
        
        if is_admin is True:
            print("验证成功: 登录响应正确识别了管理员身份！")
        else:
            print("验证失败: 登录响应未识别管理员身份。")
            
        # 4. 访问管理员接口验证权限
        print(f"\n[步骤3] 尝试访问管理员后台接口: /api/admin/orders/pending")
        headers = {
            "Authorization": f"Bearer {token}"
        }
        admin_resp = requests.get(f"{BASE_URL}/api/admin/orders/pending", headers=headers)
        print(f"后台接口响应状态码: {admin_resp.status_code}")
        
        if admin_resp.status_code == 200:
            print("验证成功: 管理员 Token 可以正常访问后台接口！")
        elif admin_resp.status_code == 403:
            print("验证失败: 访问被拒绝 (403 Forbidden)，权限判断逻辑可能有误。")
        else:
            print(f"访问异常，状态码: {admin_resp.status_code}")
            print(f"响应内容: {admin_resp.text}")

    except Exception as e:
        print(f"测试过程中发生异常: {e}")

if __name__ == "__main__":
    test_admin_flow()