# Kafka 부하테스트 (k6)

Kafka 메시징 안정성 강화 검증을 위한 부하테스트 스크립트입니다.

## 목표

- 메시지 손실률 0% 검증
- 처리량 측정
- 재시도 및 DLQ 동작 확인

## 사전 요구사항

1. k6 설치
   ```bash
   # macOS
   brew install k6
   
   # Linux
   sudo gpg -k
   sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
   echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
   sudo apt-get update
   sudo apt-get install k6
   
   # Windows
   choco install k6
   ```

2. 서비스 실행
   ```bash
   docker-compose up -d
   ```

## 테스트 실행

### 1. 기본 부하테스트 (랜덤 메시지 전송)

```bash
k6 run k6/load-test.js
```

환경 변수 설정:
```bash
BASE_URL=http://localhost:8081 k6 run k6/load-test.js
```

### 2. 배치 부하테스트 (대량 메시지 전송)

```bash
BATCH_SIZE=100 BASE_URL=http://localhost:8081 k6 run k6/load-test-batch.js
```

## 테스트 시나리오

### 시나리오 1: 점진적 부하 증가
- 30초: 10명 → 50명
- 1분: 50명 → 100명
- 2분: 100명 유지
- 1분: 100명 → 50명
- 30초: 50명 → 0명

### 시나리오 2: 배치 전송
- 1분: 5명 → 10명
- 2분: 10명 유지
- 1분: 10명 → 0명

## 검증 항목

### 1. 메시지 손실률 검증

테스트 후 다음 API로 확인:

```bash
# DB에 저장된 메시지 수 확인
curl http://localhost:8083/api/transactions/list | jq '. | length'

# DLQ 메시지 수 확인 (0이어야 함)
curl http://localhost:8083/api/dlq/count

# DLQ 통계 확인
curl http://localhost:8083/api/dlq/stats
```

**기대 결과:**
- DLQ 메시지 수 = 0 (정상 메시지는 DLQ로 가지 않음)
- DB 저장 메시지 수 ≈ 전송 메시지 수

### 2. 처리량 측정

k6 결과에서 확인:
- `http_reqs`: 초당 요청 수 (RPS)
- `message_send_success`: 메시지 전송 성공률

### 3. 재시도 동작 확인

의도적으로 실패 메시지를 전송하여 재시도 및 DLQ 전송 확인:

```bash
# 유효하지 않은 메시지 전송 (coin=null)
curl -X POST http://localhost:8081/api/test/send \
  -H "Content-Type: application/json" \
  -d '{"id":0,"coin":null,"tradePrice":50000,"tradeVolume":1,"askBid":"ASK","tradeTimestamp":1234567890}'

# DLQ 확인
curl http://localhost:8083/api/dlq/messages/recent?limit=10
```

## 결과 해석

### 성공 기준
- ✅ 메시지 손실률 0%
- ✅ DLQ 메시지 수 = 0 (정상 메시지)
- ✅ HTTP 성공률 > 99%
- ✅ 평균 응답 시간 < 500ms

### 실패 시 확인 사항
1. Kafka 연결 상태 확인
2. Consumer 처리 지연 확인
3. DB 연결 상태 확인
4. DLQ 메시지 확인

## 주의사항

- 테스트 전에 DB와 Kafka가 정상 동작하는지 확인
- 테스트 후 DLQ 메시지가 있는지 반드시 확인
- 대량 테스트 시 DB 용량 주의
