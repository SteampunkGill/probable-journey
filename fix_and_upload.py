import subprocess
import sys
import os
import shutil
from datetime import datetime

def run_command(command, cwd=None):
    """执行 shell 命令并返回结果"""
    print(f"正在执行: {command} (目录: {cwd if cwd else '当前目录'})")
    try:
        result = subprocess.run(
            command,
            shell=True,
            check=True,
            text=True,
            capture_output=True,
            cwd=cwd
        )
        if result.stdout:
            print(result.stdout)
        return True
    except subprocess.CalledProcessError as e:
        print(f"错误: 命令执行失败")
        print(f"错误输出: {e.stderr}")
        return False

def remove_nested_git(root_dir="."):
    """删除子目录中的 .git 文件夹，以便主仓库可以跟踪它们"""
    nested_git_dirs = []
    for root, dirs, files in os.walk(root_dir):
        if root == ".": continue # 跳过根目录的 .git
        if '.git' in dirs:
            git_path = os.path.join(root, '.git')
            nested_git_dirs.append(git_path)
    
    if not nested_git_dirs:
        print("未发现嵌套的 .git 目录。")
        return

    for git_dir in nested_git_dirs:
        print(f"正在删除嵌套的 Git 目录: {git_dir}")
        try:
            # Windows 下删除 .git 目录可能需要处理只读文件
            subprocess.run(f'rmdir /s /q "{git_dir}"', shell=True, check=True)
            print(f"已成功删除: {git_dir}")
        except Exception as e:
            print(f"删除失败 {git_dir}: {e}")

def count_local_files(root_dir="."):
    """统计本地文件总数（排除 .git, node_modules, target 目录）"""
    count = 0
    for root, dirs, files in os.walk(root_dir):
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
    print("=== GitHub 修复并上传脚本 ===")
    
    # 1. 删除嵌套的 .git 目录
    print("\n--- 步骤 1: 清理嵌套的 Git 仓库 ---")
    remove_nested_git()

    # 2. 强制重新添加受影响的目录
    print("\n--- 步骤 2: 重新添加文件 ---")
    # 先从暂存区移除（如果之前被当作子模块添加了）
    run_command("git rm -r --cached milk_tea milktea-backend")
    # 重新添加
    run_command("git add .")

    # 3. 统计状态
    local_count = count_local_files()
    tracked_count = count_git_tracked_files()
    print(f"\n[统计] 本地文件总数: {local_count}")
    print(f"[统计] Git 跟踪文件数: {tracked_count}")

    # 4. 提交更改
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    commit_message = f"修复嵌套仓库并上传所有文件: {timestamp}"
    print(f"\n--- 步骤 3: 提交更改 ---")
    subprocess.run(f'git commit -m "{commit_message}"', shell=True)

    # 5. 推送到远程仓库
    print("\n--- 步骤 4: 推送到 GitHub ---")
    if run_command("git push origin main"):
        print(f"\n验证: 本地 {local_count} vs Git {tracked_count}")
        print("\n恭喜！所有文件（包括 milk_tea 和 backend）已成功上传到 GitHub。")
    else:
        print("\n推送失败。")

if __name__ == "__main__":
    main()