# JMeter Performance Tests

This folder contains JMeter test plans for the LinkedIn clone API.

## Why JMeter Is Added Here

JMeter is not part of the application runtime. It is a performance test asset that runs from your machine or CI/CD and sends traffic to the deployed API Gateway.

Use it to validate:

- API latency
- throughput
- HTTP error rate
- API Gateway behavior
- JVM heap and GC under load
- database connection pool pressure
- Redis/feed behavior
- OpenSearch/search behavior
- Kafka event activity for write flows
- Kubernetes CPU/memory usage in Grafana

## Prerequisites

Install Java and Apache JMeter, then verify:

```powershell
jmeter --version
```

If `jmeter` is not in PATH, run it with the full path to `jmeter.bat`.

## Get A JWT Token

Most API endpoints require a Keycloak token.

Simple way:

1. Login to the frontend in the browser.
2. Open Chrome DevTools.
3. Go to Network.
4. Click an API request.
5. Copy the `Authorization` header value.
6. Remove the `Bearer ` prefix when passing it to JMeter.

## Run Baseline Test

From repo root:

```powershell
jmeter -n `
  -t performance/jmeter/linkedin-api-smoke.jmx `
  -l performance/jmeter/results/linkedin-api-smoke.jtl `
  -e `
  -o performance/jmeter/results/html-report `
  -JAPI_BASE_URL="http://ac2511ef9149847f0ae9ed56e0f05ba3-876930999.eu-west-2.elb.amazonaws.com:8085" `
  -JJWT_TOKEN="paste-token-without-Bearer" `
  -JTHREADS=20 `
  -JRAMP_SECONDS=60 `
  -JDURATION_SECONDS=300 `
  -JSEARCH_QUERY="test"
```

Open the HTML report:

```text
performance/jmeter/results/html-report/index.html
```

## Run While Watching Grafana

Open Grafana dashboard:

```text
LinkedIn Clone - Full Stack Observability
```

Watch:

- API Gateway request rate
- HTTP 5xx errors
- JVM heap
- GC pause rate
- Hikari active/pending DB connections
- Redis pod CPU/memory
- Search service request rate
- Kubernetes pod CPU/memory

## Run From Jenkins CI/CD

The `Jenkinsfile` has an optional `JMeter Performance Test` stage. It uses a Dockerized JMeter runner, so JMeter does not need to be installed on the Jenkins agent. The pipeline uses `justb4/jmeter:5.5`; do not change this to `justb4/jmeter:5.6.3` because that tag is not published on Docker Hub.

Build with parameters:

```text
RUN_PERFORMANCE_TEST = true
PERF_API_BASE_URL = http://ac2511ef9149847f0ae9ed56e0f05ba3-876930999.eu-west-2.elb.amazonaws.com:8085
PERF_JWT_TOKEN = paste-token-without-Bearer
PERF_THREADS = 10
PERF_RAMP_SECONDS = 60
PERF_DURATION_SECONDS = 300
PERF_WAIT_SECONDS = 120
PERF_SEARCH_QUERY = test
```

Keep `RUN_PERFORMANCE_TEST=false` for normal builds so CI does not create unnecessary production traffic.

After the Jenkins build finishes, check:

- archived `.jtl` result file
- `JMeter Performance Report`
- Grafana dashboard during the test window

## Suggested Test Stages

Start small:

```powershell
-JTHREADS=10 -JRAMP_SECONDS=60 -JDURATION_SECONDS=300
```

Then increase:

```powershell
-JTHREADS=25 -JRAMP_SECONDS=120 -JDURATION_SECONDS=600
```

Then stress:

```powershell
-JTHREADS=50 -JRAMP_SECONDS=180 -JDURATION_SECONDS=900
```

Stop increasing traffic if you see:

- HTTP 5xx errors
- p95 latency above your target
- pod restarts
- high JVM GC
- Hikari pending connections above zero
- CPU/memory alerts

## Presentation Explanation

Say:

> I added JMeter performance testing to simulate real user traffic through the API Gateway. While JMeter generates load, Prometheus and Grafana show the impact on Kubernetes pods, JVM memory, API latency, database connection pools, Redis, OpenSearch, and business endpoints. This validates that the system is not only functional, but observable under load.
