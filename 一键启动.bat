@echo off
chcp 65001 > nul
title 奶茶项目一键启动脚本

:menu
cls
echo ======================================================
echo           奶茶项目一键启动工具 (期末作业版)
echo ======================================================
echo.
echo   1. 使用 Docker 启动 (推荐: 包含数据库、后端、前端)
echo   2. 仅启动 Docker 数据库 (3308端口)
echo   3. 本地开发模式 (手动启动后端和前端)
echo   4. 停止并清理 Docker 容器
echo   5. 退出
echo.
echo ======================================================
set /p choice=请输入选项 (1-5): 

if "%choice%"=="1" goto docker_all
if "%choice%"=="2" goto docker_db
if "%choice%"=="3" goto local_dev
if "%choice%"=="4" goto docker_stop
if "%choice%"=="5" goto exit
goto menu

:docker_all
echo.
echo [信息] 正在检查 Docker 运行状态...
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] Docker 未启动或引擎未就绪！
    echo [提示] 1. 请确保 Docker Desktop 已启动
    echo [提示] 2. 如果已启动，请尝试右键 Docker 图标选择 "Restart Docker"
    echo [提示] 3. 确保 WSL2 运行正常 (在终端输入 wsl --status 检查)
    pause
    goto menu
)

echo [信息] 准备启动 Docker 环境...
call :clean_ports
echo.
echo [信息] 正在通过 Docker Compose 启动全栈环境...
echo [提示] 首次启动可能需要几分钟下载镜像和构建，请耐心等待...
docker-compose up -d --build
if %errorlevel% neq 0 (
    echo.
    echo [错误] Docker Compose 启动失败！
    echo [诊断] 发现管道连接错误 (pipe/dockerDesktopLinuxEngine)，这通常是 Docker 引擎崩溃导致的。
    echo [建议] 请彻底退出 Docker Desktop 并重新以管理员身份运行它，然后重试。
    pause
    goto menu
)
echo.
echo [成功] 服务已在后台启动：
echo   - 数据库: localhost:3308
echo   - 后端接口: localhost:8088
echo   - 前端页面: http://localhost:8888
echo.
pause
goto menu

:docker_db
echo.
echo [信息] 准备启动数据库...
for %%p in (3306 3308) do (
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%%p ^| findstr LISTENING 2^>nul') do (
        echo [清理] 发现端口 %%p 被进程 %%a 占用，正在终止...
        taskkill /F /PID %%a >nul 2>&1
    )
)
echo.
echo [信息] 正在启动 MySQL 数据库容器...
docker-compose up -d database
echo.
echo [成功] 数据库已启动 (端口: 3308)
pause
goto menu

:clean_ports
echo.
echo [信息] 正在清理端口占用 (8081, 5173, 3306, 3308, 8088, 8888)...
for %%p in (8081 5173 3306 3308 8088 8888) do (
    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%%p ^| findstr LISTENING 2^>nul') do (
        echo [清理] 发现端口 %%p 被进程 %%a 占用，正在终止...
        taskkill /F /PID %%a >nul 2>&1
    )
)
echo [成功] 端口清理完成。
goto :eof

:local_dev
echo.
echo [信息] 准备启动本地开发环境...
call :clean_ports
echo.
echo [警告] 本地模式需要您已安装 Java 17+ 和 Node.js
echo [信息] 正在启动后端服务 (新窗口)...
start "奶茶后端" cmd /c "cd milktea-backend && mvn spring-boot:run"
echo [信息] 正在启动前端服务 (新窗口)...
start "奶茶前端" cmd /c "cd milktea_front && npm install && npm run dev"
echo.
echo [提示] 请确保数据库已启动 (可使用选项2)
pause
goto menu

:docker_stop
echo.
echo [信息] 正在停止容器...
docker-compose down
echo [成功] 所有容器已停止并移除。
pause
goto menu

:exit
exit