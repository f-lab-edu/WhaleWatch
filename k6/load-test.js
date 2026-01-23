import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// 커스텀 메트릭
const messageSendSuccessRate = new Rate('message_send_success');
const messageSendDuration = new Trend('message_send_duration');
const totalMessagesSent = new Counter('total_messages_sent');
const totalMessagesFailed = new Counter('total_messages_failed');

// 테스트 설정
export const options = {
    stages: [
        { duration: '30s', target: 10 },   // 30초간 10명으로 증가
        { duration: '1m', target: 50 },     // 1분간 50명으로 증가
        { duration: '2m', target: 100 },   // 2분간 100명으로 유지
        { duration: '1m', target: 50 },     // 1분간 50명으로 감소
        { duration: '30s', target: 0 },    // 30초간 0명으로 감소
    ],
    thresholds: {
        'http_req_duration': ['p(95)<500'], // 95% 요청이 500ms 이내
        'message_send_success': ['rate>0.99'], // 99% 이상 성공률
        'http_req_failed': ['rate<0.01'],      // 1% 미만 실패율
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';
const COINS = ['BTC', 'ETH', 'SOL'];

// 랜덤 코인 선택
function getRandomCoin() {
    return COINS[Math.floor(Math.random() * COINS.length)];
}

// 랜덤 거래 데이터 생성
function generateTransactionEvent() {
    const coin = getRandomCoin();
    const tradePrice = 50000 + Math.random() * 10000;
    const tradeVolume = 1 + Math.random() * 10;
    const askBid = Math.random() > 0.5 ? 'ASK' : 'BID';
    
    return JSON.stringify({
        id: 0,
        coin: coin,
        tradePrice: tradePrice,
        tradeVolume: tradeVolume,
        askBid: askBid,
        tradeTimestamp: Date.now()
    });
}

export default function () {
    const url = `${BASE_URL}/api/test/send/random`;
    
    const startTime = Date.now();
    const response = http.post(url, null, {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'send_random_message' },
    });
    const duration = Date.now() - startTime;
    
    const success = check(response, {
        'status is 200': (r) => r.status === 200,
        'response has success field': (r) => {
            try {
                const body = JSON.parse(r.body);
                return body.success === true;
            } catch (e) {
                return false;
            }
        },
    });
    
    // 메트릭 업데이트
    messageSendSuccessRate.add(success);
    messageSendDuration.add(duration);
    
    if (success) {
        totalMessagesSent.add(1);
    } else {
        totalMessagesFailed.add(1);
        console.error(`Failed to send message: ${response.status} - ${response.body}`);
    }
    
    sleep(1); // 1초 대기
}

// 테스트 후 통계 출력
export function handleSummary(data) {
    const totalRequests = data.metrics.http_reqs.values.count;
    const failedRequests = data.metrics.http_req_failed.values.rate * totalRequests;
    const successRequests = totalRequests - failedRequests;
    const successRate = (successRequests / totalRequests) * 100;
    
    const avgDuration = data.metrics.http_req_duration.values.avg || 0;
    const p95Duration = data.metrics.http_req_duration.values['p(95)'] || 0;
    const p99Duration = data.metrics.http_req_duration.values['p(99)'] || 0;
    const messageSuccessRate = data.metrics.message_send_success.values.rate || 0;
    
    return {
        'stdout': `
========================================
Kafka 부하테스트 결과
========================================
총 요청 수: ${totalRequests}
성공 요청: ${successRequests}
실패 요청: ${failedRequests}
성공률: ${successRate.toFixed(2)}%

평균 응답 시간: ${avgDuration.toFixed(2)}ms
P95 응답 시간: ${p95Duration.toFixed(2)}ms
P99 응답 시간: ${p99Duration > 0 ? p99Duration.toFixed(2) : 'N/A'}ms

메시지 전송 성공률: ${(messageSuccessRate * 100).toFixed(2)}%
========================================
        `,
    };
}
