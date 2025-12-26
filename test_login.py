import requests
import json

url = "http://localhost:8081/api/v1/app/auth/login"
payload = {
    "username": "shark",
    "password": "123456"
}
headers = {
    "Content-Type": "application/json"
}

response = requests.post(url, data=json.dumps(payload), headers=headers)
print(response.text)