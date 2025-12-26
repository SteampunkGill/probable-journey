import subprocess
import sys
import os
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

def count_local_files(root_dir="."):
    """统计本地文件总数（排除 .git, node_modules, target 目录）"""
    count = 0
    for root, dirs, files in os.walk(root_dir):
        # 排除 .git 目录和常见的忽略目录
        if '.git' in dirs:
            dirs.remove('.git')
        if 'node_modules' in dirs:
            dirs.remove('node_modules')
        if 'target' in dirs:
            dirs.remove('target')
        count += len(files)
    return count

def count_git_tracked_files():
    """统计 Git 跟踪的文件总数"""
    try:
        result = subprocess.run(
            "git ls-files",
            shell=True,
            text=True,
            capture_output=True,
            check=True
        )
        files = result.stdout.strip().split('\n')
        return len([f for f in files if f])
    except:
        return 0

def main():
    print("=== GitHub 一键上传脚本 (带计数验证) ===")
    
    # 1. 检查 git 状态
    if not run_command("git status"):
        print("错误: 当前目录似乎不是 Git 仓库，或者 Git 未安装。")
        sys.exit(1)

    # 2. 统计上传前的状态
    local_count = count_local_files()
    tracked_before = count_git_tracked_files()
    print(f"\n[统计] 本地文件总数 (排除 .git/node_modules/target): {local_count}")
    print(f"[统计] 当前 Git 已跟踪文件数: {tracked_before}")

    # 3. 添加所有更改
    print("\n--- 正在添加文件 ---")
    if not run_command("git add ."):
        sys.exit(1)

    # 4. 提交更改
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    commit_message = f"自动提交: {timestamp}"
    print(f"\n--- 正在提交更改: {commit_message} ---")
    # 允许 commit 失败（如果没有更改需要提交）
    subprocess.run(f'git commit -m "{commit_message}"', shell=True)

    # 5. 推送到远程仓库
    print("\n--- 正在推送到 GitHub ---")
    if run_command("git push origin main"):
        tracked_after = count_git_tracked_files()
        print(f"\n[统计] 上传后 Git 跟踪文件总数: {tracked_after}")
        print(f"[验证] 本地文件数 ({local_count}) vs Git 跟踪文件数 ({tracked_after})")
        
        if tracked_after >= local_count:
            print("验证通过：所有本地文件已同步至 Git。")
        else:
            diff = local_count - tracked_after
            print(f"提示：有 {diff} 个本地文件未被 Git 跟踪。")
            print("这通常是因为 .gitignore 忽略了某些文件，或者存在嵌套的 Git 仓库。")
            
        print("\n恭喜！所有文件已成功上传到 GitHub。")
    else:
        print("\n推送失败。请检查网络连接或远程仓库权限。")
        sys.exit(1)

if __name__ == "__main__":
    main()