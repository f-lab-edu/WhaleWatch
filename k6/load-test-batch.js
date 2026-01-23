import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// 커스텀 메트릭
const batchSendSuccessRate = new Rate('batch_send_success');
const batchSendDuration = new Trend('batch_send_duration');
const totalBatchesSent = new Counter('total_batches_sent');
const totalBatchesFailed = new Counter('total_batches_failed');

// 테스트 설정
export const options = {
    stages: [
        { duration: '1m', target: 5 },      // 1분간 5명으로 증가
        { duration: '2m', target: 10 },     // 2분간 10명으로 유지
        { duration: '1m', target: 0 },      // 1분간 0명으로 감소
    ],
    thresholds: {
        'http_req_duration': ['p(95)<2000'], // 95% 요청이 2초 이내
        'batch_send_success': ['rate>0.95'], // 95% 이상 성공률
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';
const BATCH_SIZE = parseInt(__ENV.BATCH_SIZE || '100');

export default function () {
    const url = `${BASE_URL}/api/test/send/batch?count=${BATCH_SIZE}`;
    
    const startTime = Date.now();
    const response = http.post(url, null, {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'send_batch_messages' },
    });
    const duration = Date.now() - startTime;
    
    const success = check(response, {
        'status is 200': (r) => r.status === 200,
        'response has success field': (r) => {
            try {
                const body = JSON.parse(r.body);
                return body.successRate >= 95; // 배치 내 95% 이상 성공
            } catch (e) {
                return false;
            }
        },
    });
    
    // 메트릭 업데이트
    batchSendSuccessRate.add(success);
    batchSendDuration.add(duration);
    
    if (success) {
        totalBatchesSent.add(1);
        try {
            const body = JSON.parse(response.body);
            console.log(`Batch sent: total=${body.total}, success=${body.success}, failed=${body.failed}`);
        } catch (e) {
            // ignore
        }
    } else {
        totalBatchesFailed.add(1);
        console.error(`Failed to send batch: ${response.status} - ${response.body}`);
    }
    
    sleep(5); // 5초 대기 (배치는 부하가 크므로)
}

export function handleSummary(data) {
    const totalBatches = data.metrics.http_reqs.values.count;
    const failedBatches = data.metrics.http_req_failed.values.rate * totalBatches;
    const successBatches = totalBatches - failedBatches;
    const successRate = (successBatches / totalBatches) * 100;
    
    return {
        'stdout': `
========================================
Kafka 배치 부하테스트 결과
========================================
총 배치 수: ${totalBatches}
성공 배치: ${successBatches}
실패 배치: ${failedBatches}
배치 성공률: ${successRate.toFixed(2)}%

평균 응답 시간: ${data.metrics.http_req_duration.values.avg.toFixed(2)}ms
P95 응답 시간: ${data.metrics.http_req_duration.values['p(95)'].toFixed(2)}ms
========================================
        `,
    };
}
