import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8100';
const VUS = Number(__ENV.VUS || 20);
const PASSWORD = 'LoadTest1!pass';

export const options = {
    scenarios: {
        transactions: {
            executor: 'constant-vus',
            vus: VUS,
            duration: __ENV.DURATION || '60s',
        },
    },
    thresholds: {
        // fail the run if the api starts rejecting or crawling
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<1000'],
    },
};

// runs once before the test, every virtual user gets its own account
// so the per user rate limit does not kick in
export function setup() {
    const tokens = [];

    for (let i = 1; i <= VUS; i++) {
        const email = `loadtest_user_${i}_${Date.now()}@titanguard.local`;
        const body = JSON.stringify({ email: email, password: PASSWORD });
        const params = { headers: { 'Content-Type': 'application/json' } };

        http.post(`${BASE_URL}/api/v1/auth/signUp`, body, params);

        const signIn = http.post(`${BASE_URL}/api/v1/auth/signIn`, body, params);
        const token = signIn.json('data.token');

        if (!token) {
            throw new Error(`could not sign in user ${email}, got ${signIn.status}`);
        }
        tokens.push(token);
    }

    console.log(`created ${tokens.length} users`);
    return { tokens: tokens };
}

export default function (data) {
    // __VU starts at 1, array starts at 0
    const token = data.tokens[(__VU - 1) % data.tokens.length];

    const response = http.post(
        `${BASE_URL}/api/v1/transactions`,
        JSON.stringify({ amount: 100.50 }),
        {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            tags: { name: 'createTransaction' },
        }
    );

    check(response, {
        'accepted': (r) => r.status === 202,
        'not rate limited': (r) => r.status !== 429,
    });
}
