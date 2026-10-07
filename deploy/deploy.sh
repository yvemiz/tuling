#!/bin/bash

# ==============================================================================
# 项目一键 Docker 部署脚本 (适用于 CentOS / Ubuntu / Debian / 宝塔面板环境)
# ==============================================================================

set -e

# 输出带颜色的日志
green() { echo -e "\033[32m$1\033[0m"; }
yellow() { echo -e "\033[33m$1\033[0m"; }
red() { echo -e "\033[31m$1\033[0m"; }

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    red "错误: 未检测到 Docker，请在宝塔面板『软件商店』中安装 Docker，或执行安装命令后再试。"
    exit 1
fi

# 检查 Docker Compose
COMPOSE_CMD=""
if docker compose version &> /dev/null; then
    COMPOSE_CMD="docker compose"
elif command -v docker-compose &> /dev/null; then
    COMPOSE_CMD="docker-compose"
else
    red "错误: 未检测到 Docker Compose，请在宝塔面板 Docker 模块中启用 Compose 或安装 docker-compose。"
    exit 1
fi

# 确保必要的本地挂载目录存在
mkdir -p ./data/mysql ./data/redis ./data/upload

ACTION=${1:-"up"}

case $ACTION in
    up)
        green ">>> 正在构建镜像并启动所有容器..."
        $COMPOSE_CMD up -d --build
        green ">>> 容器启动成功！正在查看运行状态..."
        $COMPOSE_CMD ps
        
        # 尝试动态检测当前服务器 IP (兼顾公网与内网)
        SERVER_IP=$(curl -s --connect-timeout 2 ifconfig.me 2>/dev/null || ip route get 1.1.1.1 2>/dev/null | awk '{print $7}' || echo "<服务器IP>")
        echo ""
        green "=========================================================="
        green "项目已成功部署上线！"
        green "  - 用户端门户首页:  http://${SERVER_IP}:8080"
        green "  - 管理端后台系统:  http://${SERVER_IP}:8081"
        green "  (后端API与数据库已安全闭环在Docker内网，无需对外暴露)"
        green "=========================================================="
        ;;
    down)
        yellow ">>> 正在停止并移除容器..."
        $COMPOSE_CMD down
        green ">>> 服务已停止。"
        ;;
    restart)
        yellow ">>> 正在重启所有容器..."
        $COMPOSE_CMD restart
        green ">>> 服务已重启。"
        ;;
    logs)
        yellow ">>> 查看运行日志 (Ctrl+C 退出)..."
        $COMPOSE_CMD logs -f
        ;;
    ps)
        $COMPOSE_CMD ps
        ;;
    *)
        echo "使用方式: ./deploy.sh [up|down|restart|logs|ps]"
        ;;
esac
