#!/bin/bash

# Baize 项目中间件部署脚本
# 适用于 k3s Kubernetes 集群

set -e

echo "======================================"
echo "  Baize 中间件部署脚本 (k3s)"
echo "======================================"
echo ""

# 检查 kubectl 是否可用
if ! command -v kubectl &> /dev/null; then
    echo "❌ 错误: kubectl 命令未找到，请先安装 kubectl 或确认 k3s 已安装"
    exit 1
fi

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "📁 部署配置文件目录: $SCRIPT_DIR"
echo ""

# 部署命名空间
echo "🔧 1/4 部署命名空间..."
kubectl apply -f "$SCRIPT_DIR/00-namespace.yaml"
echo "✅ 命名空间部署完成"
echo ""

# 部署 PostgreSQL
echo "🔧 2/4 部署 PostgreSQL..."
kubectl apply -f "$SCRIPT_DIR/01-postgres.yaml"
echo "✅ PostgreSQL 部署完成"
echo ""

# 部署 Redis
echo "🔧 3/4 部署 Redis..."
kubectl apply -f "$SCRIPT_DIR/02-redis.yaml"
echo "✅ Redis 部署完成"
echo ""

# 部署 Nacos
echo "🔧 4/4 部署 Nacos..."
kubectl apply -f "$SCRIPT_DIR/03-nacos.yaml"
echo "✅ Nacos 部署完成"
echo ""

echo "======================================"
echo "  所有服务部署已提交！"
echo "======================================"
echo ""
echo "📊 查看 Pod 状态："
echo "  kubectl get pods -n baize -w"
echo ""
echo "📊 查看所有服务："
echo "  kubectl get svc -n baize"
echo ""
echo "🌐 服务访问地址："
echo "  PostgreSQL: localhost:30432"
echo "  Redis: localhost:30379"
echo "  Nacos: http://localhost:30848/nacos"
echo ""
echo "🔐 连接信息："
echo "  PostgreSQL 用户: postgres, 密码: baize@2023"
echo "  Nacos 默认账号: nacos/nacos"
echo ""
echo "📝 查看日志示例："
echo "  kubectl logs -f deployment/postgres -n baize"
echo "  kubectl logs -f deployment/redis -n baize"
echo "  kubectl logs -f deployment/nacos -n baize"
echo ""
