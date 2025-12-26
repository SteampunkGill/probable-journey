import requests
import json

# 1. 登录获取 token
login_url = "http://localhost:8081/api/v1/app/auth/login"
login_payload = {
    "username": "shark",
    "password": "123456"
}
login_res = requests.post(login_url, json=login_payload)
token = login_res.json()['data']['token']
print(f"Login successful, token: {token[:20]}...")

# 2. 上传头像
upload_url = "http://localhost:8081/api/common/upload/image"
# 随便找一个现有的图片文件，或者创建一个
with open("milk_tea/images/icons/home.png", "rb") as f:
    files = {'file': ('home.png', f, 'image/png')}
    upload_res = requests.post(upload_url, files=files)
print(f"Upload response: {upload_res.text}")
avatar_url = upload_res.json()['data']['url']
print(f"Upload successful, avatar_url: {avatar_url}")

# 3. 更新个人信息
update_url = "http://localhost:8081/api/v1/app/user/profile"
update_payload = {
    "nickname": "RooEngineer",
    "birthday": "1995-10-24",
    "avatarUrl": avatar_url
}
headers = {
    "Authorization": f"Bearer {token}",
    "Content-Type": "application/json"
}
update_res = requests.put(update_url, json=update_payload, headers=headers)
print(f"Update response: {update_res.text}")