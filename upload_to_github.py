import subprocess
import sys
from datetime import datetime

def run_command(command):
    """执行 shell 命令并返回结果"""
    print(f"正在执行: {command}")
    try:
        result = subprocess.run(
            command,
            shell=True,
            check=True,
            text=True,
            capture_output=True
        )
        if result.stdout:
            print(result.stdout)
        return True
    except subprocess.CalledProcessError as e:
        print(f"错误: 命令执行失败")
        print(f"错误输出: {e.stderr}")
        return False

def main():
    print("=== GitHub 一键上传脚本 ===")
    
    # 1. 检查 git 状态
    if not run_command("git status"):
        print("错误: 当前目录似乎不是 Git 仓库，或者 Git 未安装。")
        sys.exit(1)

    # 2. 添加所有更改
    print("\n--- 正在添加文件 ---")
    if not run_command("git add ."):
        sys.exit(1)

    # 3. 提交更改
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    commit_message = f"自动提交: {timestamp}"
    print(f"\n--- 正在提交更改: {commit_message} ---")
    # 允许 commit 失败（如果没有更改需要提交）
    subprocess.run(f'git commit -m "{commit_message}"', shell=True)

    # 4. 推送到远程仓库
    print("\n--- 正在推送到 GitHub ---")
    # 注意：这里假设分支名为 main，根据之前的 git status 结果确认是 main
    if run_command("git push origin main"):
        print("\n恭喜！所有文件已成功上传到 GitHub。")
    else:
        print("\n推送失败。请检查网络连接或远程仓库权限。")
        sys.exit(1)

if __name__ == "__main__":
    main()