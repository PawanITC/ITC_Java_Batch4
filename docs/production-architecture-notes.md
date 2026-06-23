# Production Architecture Notes

## Goal

Build this project the way a senior engineer would structure it for production:

1. local development stays simple
2. CI/CD is automated and repeatable
3. applications run on Kubernetes
4. Kubernetes runs on EKS
5. database runs on RDS
6. Kafka runs on MSK
7. observability is built in

This note is based on your current project and current Terraform, including:

1. `infrastructure/terraform-ecs/msk.tf`
2. `infrastructure/terraform-ecs/main.tf`

Important:

Your current Terraform is ECS-oriented, not EKS-oriented.

That means this note explains the target production direction, not just your current files.

---

## Simple Answer

You do **not** need MSK and RDS for local development or basic CI.

You **do** want MSK and RDS for deployed environments like:

1. staging
2. production
3. EKS-hosted runtime

Best practice:

1. local: Docker Compose
2. CI: test containers or temporary services
3. runtime: EKS + RDS + MSK

---

## Recommended Production Architecture

```text
Developer
   |
   v
Git Repository
   |
   v
CI Pipeline
   |- build
   |- unit tests
   |- integration tests
   |- docker image build
   |- push image to registry
   v
CD Pipeline
   |- terraform infra
   |- deploy to EKS
   |- rollout verification
   v
AWS Production Environment
   |- EKS cluster
   |- ALB / Ingress
   |- API Gateway service
   |- post-service
   |- feed-and-timeline-service
   |- search-discovery-service
   |- userprofile-service
   |- Redis
   |- RDS PostgreSQL
   |- MSK Kafka
   |- Schema Registry
   |- Prometheus / Grafana / Jaeger / OTel
```

---

## What Each Layer Should Do

## 1. Local Development

Use:

1. Docker Compose
2. local Kafka
3. local Postgres
4. local Schema Registry
5. local Redis

Purpose:

1. fast iteration
2. easy debugging
3. no cloud dependency

Do not optimize local setup for production infrastructure.

Local and production are different by design.

---

## 2. CI Pipeline

CI should do:

1. checkout code
2. run `mvn test`
3. run lint/format checks if you add them
4. build Docker images
5. push images to registry

CI should usually **not** depend on real MSK or real RDS.

Why:

1. slower
2. expensive
3. flaky if shared
4. harder to isolate

Better:

1. unit tests with mocks
2. integration tests with local Docker/Testcontainers

---

## 3. CD Pipeline

CD should do:

1. deploy infrastructure if needed
2. update Kubernetes manifests or Helm values
3. deploy images to EKS
4. wait for rollout
5. run smoke checks

This is where managed runtime services matter:

1. RDS
2. MSK
3. EKS
4. ALB

---

## 4. Runtime Infrastructure

For production-like deployment:

1. EKS for application workloads
2. RDS PostgreSQL for service databases
3. MSK for Kafka
4. external or self-hosted Schema Registry
5. Redis
6. monitoring stack

This is the right place to use MSK and RDS.

---

## Why EKS + RDS + MSK

### EKS

Use EKS because:

1. Kubernetes is the deployment platform
2. supports rolling deployment
3. supports service discovery
4. supports autoscaling
5. standard production operating model

### RDS

Use RDS because:

1. managed backups
2. managed failover
3. durable storage
4. operational simplicity

### MSK

Use MSK because:

1. managed Kafka brokers
2. durable message infrastructure
3. better production operations than self-managed brokers
4. integrates cleanly with AWS networking

---

## What `msk.tf` Is Doing Today

File:

1. `infrastructure/terraform-ecs/msk.tf`

What it currently provisions:

1. MSK security group
2. MSK cluster
3. TLS broker access on port `9094`

What that means:

1. your runtime applications can connect securely to Kafka over TLS
2. ECS task security group is allowed to reach MSK

Current limitation:

1. this Terraform is for ECS-style deployment, not EKS
2. it does not provision Schema Registry
3. it does not model Kubernetes access patterns

So `msk.tf` is useful conceptually, but it is not the finished EKS architecture.

---

## Stepwise Plan

## Phase 1. Stabilize The Application

Before infrastructure, the application must be deployment-ready.

Do this first:

1. finalize service boundaries
2. finalize Kafka contracts
3. make health endpoints reliable
4. make config environment-driven
5. make secrets externalized
6. add tests for business-critical paths

For your project, that means:

1. `post-service` stable
2. `feed-and-timeline-service` stable
3. gateway routes stable
4. Kafka outbox flow stable
5. Schema Registry integration stable

---

## Phase 2. Container Standards

Each service should have:

1. production Dockerfile
2. health endpoint
3. resource limits defined
4. external configuration through env vars
5. structured logs

Learn:

1. Docker image layering
2. JVM container tuning
3. readiness vs liveness probes

---

## Phase 3. CI Pipeline

Build CI first, before EKS deployment.

Pipeline stages:

1. checkout
2. dependency restore
3. unit tests
4. integration tests
5. package services
6. Docker build
7. image scan if possible
8. push to ECR

Learn:

1. Jenkins or GitHub Actions basics
2. artifact versioning
3. Docker image tagging
4. branch strategy
5. pipeline secrets management

Recommended image tagging:

1. commit SHA
2. branch tag for non-prod
3. release tag for prod

---

## Phase 4. Infrastructure As Code

Then create production infrastructure properly.

For EKS architecture, provision:

1. VPC
2. public/private subnets
3. NAT gateways if needed
4. EKS cluster
5. managed node groups or Fargate profiles
6. RDS
7. MSK
8. security groups
9. IAM roles
10. ECR repositories

Learn:

1. Terraform modules
2. remote state
3. state locking
4. environment separation
5. IAM least privilege

Important:

Do not keep adding EKS logic into the current `terraform-ecs` folder forever.

Better:

1. keep ECS Terraform separate
2. create `terraform-eks` or refactor into reusable modules

---

## Phase 5. Kubernetes Manifests

Once EKS exists, define Kubernetes deployment objects.

Per service you need:

1. `Deployment`
2. `Service`
3. `ConfigMap`
4. `Secret` reference
5. `HorizontalPodAutoscaler` later
6. `Ingress` or ALB integration

Learn:

1. Deployments
2. Services
3. Ingress
4. ConfigMaps
5. Secrets
6. rolling updates
7. probes

Prefer:

1. Helm charts
2. or Kustomize

Avoid managing large raw YAML manually across many services.

---

## Phase 6. Connect EKS To RDS And MSK

At runtime, your application pods should receive:

1. DB host, port, db name
2. DB username/password from secret manager
3. MSK bootstrap brokers
4. Schema Registry URL
5. Redis URL
6. Keycloak URLs

Learn:

1. Kubernetes secrets
2. AWS Secrets Manager
3. IRSA
4. VPC networking
5. security groups for pods/nodes

---

## Phase 7. Observability

For production, add:

1. metrics
2. traces
3. centralized logs
4. alerting

At minimum:

1. Prometheus
2. Grafana
3. Jaeger or Tempo
4. OpenTelemetry Collector
5. CloudWatch logs if on AWS

Kafka-specific observability:

1. consumer lag
2. DLT volume
3. topic throughput
4. broker health

Learn:

1. RED metrics
2. JVM metrics
3. Kafka lag monitoring
4. alert thresholds

---

## Phase 8. Release Strategy

Once deployments are working, harden delivery.

Use:

1. rolling deployments
2. image immutability
3. environment promotion
4. rollback strategy

Senior-level concerns:

1. what happens on bad deploy
2. how fast rollback works
3. whether schema changes are backward compatible
4. whether Kafka event version changes are safe

---

## What To Learn Step By Step

Do not try to learn everything at once.

## Step 1. Strong Docker + Spring Boot Ops Basics

Learn:

1. Dockerfiles
2. env-based configuration
3. health checks
4. service startup dependencies

## Step 2. CI/CD Fundamentals

Learn:

1. Jenkins pipelines or GitHub Actions
2. build/test/package flow
3. Docker build and push
4. artifact promotion

## Step 3. Kubernetes Basics

Learn:

1. pods
2. deployments
3. services
4. ingress
5. configmaps
6. secrets

## Step 4. EKS On AWS

Learn:

1. EKS cluster architecture
2. node groups
3. IAM roles
4. ALB ingress controller
5. cluster networking

## Step 5. Managed Data Services

Learn:

1. RDS networking and backups
2. MSK connectivity and auth
3. Schema Registry deployment strategy
4. Redis hosting strategy

## Step 6. Terraform Properly

Learn:

1. module structure
2. variables/outputs
3. remote state
4. workspace or environment separation
5. plan/apply discipline

## Step 7. Production Reliability

Learn:

1. autoscaling
2. rate limiting
3. retries and timeouts
4. circuit breakers
5. DLT handling
6. observability and alerts

---

## Suggested Execution Order For You

Follow this order:

1. finish local architecture
2. complete tests
3. create CI pipeline
4. create ECR image publishing
5. create EKS Terraform
6. create Kubernetes manifests/Helm charts
7. connect EKS to RDS and MSK
8. deploy one service first
9. deploy all services
10. add observability and alerts

Do not start by deploying everything to EKS immediately.

That is the wrong order.

---

## Senior Engineer Mindset

A senior engineer thinks in this order:

1. correctness
2. repeatability
3. operability
4. security
5. scalability
6. cost

For your project, that means asking:

1. Can I reproduce the environment?
2. Can I test it before deploy?
3. Can I roll back safely?
4. Can I monitor it?
5. Can I rotate secrets?
6. Can I evolve schemas safely?

---

## What You Should Do Now

Immediate next steps:

1. keep local Docker Compose for development
2. keep CI independent from real AWS services
3. design one clean EKS target architecture
4. create separate Terraform for EKS-based infra
5. deploy app pods to EKS
6. connect them to RDS and MSK

---

## Final Recommendation

Best production path for your project:

1. local development on Docker Compose
2. CI pipeline for tests and image builds
3. CD pipeline for deployment
4. EKS for applications
5. RDS for PostgreSQL
6. MSK for Kafka
7. Schema Registry as a real runtime dependency
8. Prometheus/Grafana/Jaeger/OTel for observability

That is the architecture you should move toward.

