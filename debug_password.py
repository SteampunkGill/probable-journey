import bcrypt

def debug_bcrypt():
    print("=== BCrypt 密码加密调试工具 ===")
    
    # 1. 模拟正常流程
    password = "testpassword123"
    password_bytes = password.encode('utf-8')
    
    # 生成盐值并加密
    salt = bcrypt.gensalt()
    hashed = bcrypt.hashpw(password_bytes, salt)
    
    print(f"\n[1. 正常流程]")
    print(f"原始密码: {password}")
    print(f"生成的哈希值: {hashed.decode('utf-8')}")
    
    # 验证
    is_match = bcrypt.checkpw(password_bytes, hashed)
    print(f"验证结果: {'成功' if is_match else '失败'}")

    # 2. 模拟“二次哈希”陷阱
    double_hashed = bcrypt.hashpw(hashed, bcrypt.gensalt())
    print(f"\n[2. 二次哈希陷阱]")
    print(f"二次哈希值: {double_hashed.decode('utf-8')}")
    is_match_double = bcrypt.checkpw(password_bytes, double_hashed)
    print(f"用原始密码验证二次哈希: {'成功' if is_match_double else '失败 (这就是问题所在)'}")

    # 3. 数据库长度检查模拟
    print(f"\n[3. 存储长度检查]")
    print(f"哈希值长度: {len(hashed.decode('utf-8'))} 字符")

    print("\n=== 排查建议 ===")
    print("1. 检查数据库中存储的密码是否以 '$2a$' 或 '$2b$' 开头。")
    print("2. 检查代码中是否存在对 passwordEncoder.encode() 的重复调用。")
    print("3. 确认登录时传入的 password 是原始明文。")

if __name__ == "__main__":
    try:
        debug_bcrypt()
    except ImportError:
        print("错误: 请先安装 bcrypt 库: pip install bcrypt")