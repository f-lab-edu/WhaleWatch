#!/bin/bash

# 메시지 손실률 검증 스크립트

BASE_URL=${BASE_URL:-"http://localhost:8083"}
COLLECTOR_URL=${COLLECTOR_URL:-"http://localhost:8081"}

echo "=========================================="
echo "Kafka 메시지 손실률 검증"
echo "=========================================="
echo ""

# 1. DLQ 메시지 수 확인
echo "1. DLQ 메시지 수 확인..."
DLQ_RESPONSE=$(curl -s "${BASE_URL}/api/dlq/count")
DLQ_COUNT=$(echo "$DLQ_RESPONSE" | jq -r '.totalCount // 0' 2>/dev/null || echo "0")

if [ -z "$DLQ_COUNT" ] || [ "$DLQ_COUNT" = "null" ]; then
    DLQ_COUNT=0
fi

echo "   DLQ 메시지 수: ${DLQ_COUNT}"

if [ "$DLQ_COUNT" -gt 0 ] 2>/dev/null; then
    echo "   ⚠️  DLQ에 메시지가 있습니다. 확인이 필요합니다."
    echo "   DLQ 메시지 조회: curl ${BASE_URL}/api/dlq/messages/recent?limit=10"
else
    echo "   ✅ DLQ 메시지 없음 (정상)"
fi

echo ""

# 2. DB 저장 메시지 수 확인
echo "2. DB 저장 메시지 수 확인..."
TRANSACTION_RESPONSE=$(curl -s "${BASE_URL}/api/transactions/list")
TRANSACTION_COUNT=$(echo "$TRANSACTION_RESPONSE" | jq '. | length // 0' 2>/dev/null || echo "0")

if [ -z "$TRANSACTION_COUNT" ] || [ "$TRANSACTION_COUNT" = "null" ]; then
    TRANSACTION_COUNT=0
fi

echo "   저장된 거래 수: ${TRANSACTION_COUNT}"

echo ""

# 3. DLQ 통계 확인
echo "3. DLQ 통계 확인..."
STATS_RESPONSE=$(curl -s "${BASE_URL}/api/dlq/stats")
if [ -n "$STATS_RESPONSE" ] && [ "$STATS_RESPONSE" != "null" ]; then
    echo "$STATS_RESPONSE" | jq '.' 2>/dev/null || echo "$STATS_RESPONSE"
else
    echo "   (통계 정보 없음)"
fi

echo ""

# 4. 최근 DLQ 메시지 확인 (있는 경우)
if [ "$DLQ_COUNT" -gt 0 ] 2>/dev/null; then
    echo "4. 최근 DLQ 메시지 (최대 5개)..."
    curl -s "${BASE_URL}/api/dlq/messages/recent?limit=5" | jq '.' 2>/dev/null || echo "   (메시지 조회 실패)"
fi

echo ""
echo "=========================================="
echo "검증 완료"
echo "=========================================="
