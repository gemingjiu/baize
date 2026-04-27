#!/bin/bash

# Baize 中间件端口转发管理脚本
# 用于本地开发环境通过 port-forward 访问 K3s 中的服务

set -e

# 自动设置 KUBECONFIG（如果用户目录有配置的话）
export KUBECONFIG="${KUBECONFIG:-$HOME/.kube/config}"
NAMESPACE="baize"
LOG_DIR="/tmp/baize-portforward"
mkdir -p "$LOG_DIR"

# 服务配置 (支持多端口，用空格分隔)
declare -A SERVICES=(
    ["nacos"]="8848:8848 9848:9848"
    ["postgres"]="5432:5432"
    ["redis"]="6379:6379"
)

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 kubectl 是否可用
check_kubectl() {
    if ! command -v kubectl &> /dev/null; then
        error "kubectl 命令未找到，请先安装 kubectl 或确认 K3s 已安装"
        exit 1
    fi

    # 检查是否可以访问集群
    if ! kubectl cluster-info &> /dev/null; then
        error "无法访问 Kubernetes 集群，请检查 KUBECONFIG 配置"
        exit 1
    fi
}

# 启动端口转发
start_forward() {
    local service=$1
    local ports=$2

    # 检查是否已在运行
    if pkill -0 -f "kubectl port-forward.*svc/$service" 2>/dev/null; then
        warn "$service 端口转发已在运行"
        return
    fi

    info "启动 $service 端口转发 ($ports)..."
    nohup kubectl port-forward "svc/$service" $ports -n "$NAMESPACE" \
        > "$LOG_DIR/$service.log" 2>&1 &
    sleep 1

    if pkill -0 -f "kubectl port-forward.*svc/$service" 2>/dev/null; then
        info "$service 端口转发启动成功"
    else
        error "$service 端口转发启动失败，请查看日志: $LOG_DIR/$service.log"
    fi
}

# 停止端口转发
stop_forward() {
    local service=$1

    if pkill -f "kubectl port-forward.*svc/$service" 2>/dev/null; then
        info "$service 端口转发已停止"
    else
        warn "$service 端口转发未在运行"
    fi
}

# 查看状态
status() {
    echo ""
    echo "======================================"
    echo "  Baize 中间件端口转发状态"
    echo "======================================"
    echo ""

    local running=false
    for service in "${!SERVICES[@]}"; do
        if pgrep -f "kubectl port-forward.*svc/$service" > /dev/null; then
            local ports="${SERVICES[$service]}"
            if [ "$service" = "nacos" ]; then
                info "✅ $service: 运行中 (8848 HTTP, 9848 gRPC)"
            else
                local port="${ports%%:*}"
                info "✅ $service: 运行中 (localhost:$port)"
            fi
            running=true
        else
            warn "❌ $service: 未运行"
        fi
    done

    echo ""
    if [ "$running" = true ]; then
        info "日志目录: $LOG_DIR"
    fi
    echo ""
}

# 启动所有
start_all() {
    info "启动所有端口转发..."
    echo ""
    for service in "${!SERVICES[@]}"; do
        start_forward "$service" "${SERVICES[$service]}"
    done
    sleep 1
    status
}

# 停止所有
stop_all() {
    info "停止所有端口转发..."
    for service in "${!SERVICES[@]}"; do
        stop_forward "$service"
    done
    sleep 1
    status
}

# 查看日志
logs() {
    local service=$1
    if [ -z "$service" ]; then
        error "请指定服务名称: nacos, postgres, redis"
        exit 1
    fi
    if [ ! -f "$LOG_DIR/$service.log" ]; then
        error "日志文件不存在: $LOG_DIR/$service.log"
        exit 1
    fi
    tail -f "$LOG_DIR/$service.log"
}

# 帮助信息
show_help() {
    cat << EOF
Baize 中间件端口转发管理脚本

用法:
  $0 start     启动所有端口转发
  $0 stop      停止所有端口转发
  $0 restart   重启所有端口转发
  $0 status    查看转发状态
  $0 logs <服务>  查看指定服务日志 (nacos, postgres, redis)
  $0 help      显示此帮助信息

服务列表:
  nacos     :8848 (HTTP), 9848 (gRPC)
  postgres  :5432 -> localhost:5432
  redis     :6379 -> localhost:6379

示例:
  $0 start
  $0 logs nacos
  $0 stop

EOF
}

# 主逻辑
check_kubectl

case "${1:-status}" in
    start)
        start_all
        ;;
    stop)
        stop_all
        ;;
    restart)
        stop_all
        sleep 2
        start_all
        ;;
    status)
        status
        ;;
    logs)
        logs "$2"
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        error "未知命令: $1"
        echo ""
        show_help
        exit 1
        ;;
esac
