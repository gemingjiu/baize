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

# ---------------------------------------------------------------------------
# 凭据占位符校验：01-postgres.yaml / 03-nacos.yaml 中的 Secret 均为占位符，
# 部署前必须替换为真实值（或改用 kubectl create secret 注入）。
# ---------------------------------------------------------------------------
if grep -q "CHANGE_ME_" "$SCRIPT_DIR/01-postgres.yaml" "$SCRIPT_DIR/03-nacos.yaml"; then
    echo "❌ 错误: 检测到未替换的凭据占位符（CHANGE_ME_*）"
    echo "   请先编辑 01-postgres.yaml（POSTGRES_PASSWORD）与 03-nacos.yaml"
    echo "   （NACOS_AUTH_TOKEN / NACOS_AUTH_IDENTITY_KEY / NACOS_AUTH_IDENTITY_VALUE），"
    echo "   或改用 kubectl create secret 自行注入后删除清单中的 Secret 段。"
    exit 1
fi

echo "📁 部署配置文件目录: $SCRIPT_DIR"
echo ""

# 部署命名空间
echo "🔧 1/5 部署命名空间..."
kubectl apply -f "$SCRIPT_DIR/00-namespace.yaml"
echo "✅ 命名空间部署完成"
echo ""

# 部署 PostgreSQL
echo "🔧 2/5 部署 PostgreSQL..."
kubectl apply -f "$SCRIPT_DIR/01-postgres.yaml"
echo "✅ PostgreSQL 部署完成"
echo ""

# 部署 Redis
echo "🔧 3/5 部署 Redis..."
kubectl apply -f "$SCRIPT_DIR/02-redis.yaml"
echo "✅ Redis 部署完成"
echo ""

# 部署 Nacos
echo "🔧 4/5 部署 Nacos..."
kubectl apply -f "$SCRIPT_DIR/03-nacos.yaml"
echo "✅ Nacos 部署完成"
echo ""

# ---------------------------------------------------------------------------
# 部署应用层（T2.4 韧性骨架 + T2.2 指标采集）
# 中间件就绪后再部署应用，否则启动探针会长时间失败。
# 若集群未安装 Prometheus Operator，ServiceMonitor/PrometheusRule 会 apply 失败，
# 此时用 SKIP_MONITORING=1 跳过（CRD 不存在属预期，不影响应用运行）。
# ---------------------------------------------------------------------------
echo "⏳ 等待中间件就绪..."
kubectl wait --for=condition=available --timeout=300s \
    deployment/postgres deployment/redis deployment/nacos -n baize || true
echo ""

echo "🔧 5/5 部署应用服务..."
kubectl apply -f "$SCRIPT_DIR/10-app-deployments.yaml"
kubectl apply -f "$SCRIPT_DIR/11-app-pdb.yaml"
kubectl apply -f "$SCRIPT_DIR/12-app-services.yaml"

if [ "${SKIP_MONITORING:-0}" = "1" ]; then
    echo "⏭️  已跳过 ServiceMonitor/PrometheusRule（SKIP_MONITORING=1）"
else
    # CRD 不存在时 kubectl 会报错，这里显式降级为警告，避免中断中间件已完成的部署
    kubectl apply -f "$SCRIPT_DIR/13-servicemonitor.yaml" \
        || echo "⚠️  ServiceMonitor 部署失败：请确认集群已安装 Prometheus Operator（或用 SKIP_MONITORING=1 跳过）"
    kubectl apply -f "$SCRIPT_DIR/14-prometheusrule.yaml" \
        || echo "⚠️  PrometheusRule 部署失败：请确认集群已安装 Prometheus Operator（或用 SKIP_MONITORING=1 跳过）"
fi
echo "✅ 应用服务部署完成"
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
echo "  Gateway: http://localhost:8000（经 Service/Ingress 暴露）"
echo ""
echo "🔐 连接信息："
echo "  PostgreSQL 用户: postgres, 密码: 见 01-postgres.yaml Secret（部署前替换）"
echo "  Nacos 默认账号: nacos/nacos（建议部署后立即改密）"
echo ""
echo "📝 查看日志示例："
echo "  kubectl logs -f deployment/postgres -n baize"
echo "  kubectl logs -f deployment/redis -n baize"
echo "  kubectl logs -f deployment/nacos -n baize"
echo ""
