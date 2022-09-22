import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '15m', target: 1000 }, // simulate ramp-up of traffic from 1 to 1000 users over 15 minutes.
    { duration: '10m', target: 1000 }, // stay at 1000 users for 10 minutes
    { duration: '5m', target: 0 }, // ramp-down to 0 users
  ],
  thresholds: {
    'http_req_duration': ['p(99)<100'] // 99% of requests must complete below 1.5s
    // 'logged in successfully': ['p(99)<1500'], // 99% of requests must complete below 1.5s
  },
};

const BASE_URL = 'https://test:8081';

export default () => {
  const statusRes = http.get(`${BASE_URL}/status`);

  check(statusRes, {
    'success': (r) => r.status === 200,
    'shed': (r) => r.status === 503,
    'error':(r) => r.status === 500,
    'time_exceeded' : (r) => r.timings.duration > 100,
    'http_2': (r) => r.proto === 'HTTP/2.0',
    'http_1': (r) => r.proto !== 'HTTP/2.0',
  });

  sleep(1);
};

