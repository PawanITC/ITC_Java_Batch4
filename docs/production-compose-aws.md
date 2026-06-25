# Production Compose on AWS

Use `docker-compose.prod.yml` only as a production-like runtime definition for containers that talk to managed AWS services. It is not a replacement for EKS manifests.

## Managed services expected

- `RDS PostgreSQL` for:
  - `userprofile-service`
  - `feed-and-timeline-service`
  - `post-service`
- `MSK` for Kafka brokers
- `OpenSearch` for `search-discovery-service`
- `ElastiCache Redis` for:
  - `api-gateway` rate limiting
  - `feed-and-timeline-service` caching

## Required setup

1. Copy `.env.prod.example` to `.env.prod`
2. Fill real AWS endpoints and credentials
3. Build and run:

```powershell
docker compose --env-file .env.prod -f docker-compose.prod.yml up -d --build
```

## Important MSK note

Your current post/feed code uses Kafka plus a schema registry URL:

- `SPRING_KAFKA_BOOTSTRAP_SERVERS`
- `SPRING_KAFKA_SCHEMA_REGISTRY_URL`

MSK gives you Kafka brokers, but not Confluent Schema Registry. You need one of these:

1. an external Confluent-compatible schema registry
2. a move to AWS Glue Schema Registry with serializer changes in code
3. a switch away from Avro/schema-registry events

Without that, `post-service` event publishing and `feed-and-timeline-service` Avro consumption will not be production-complete.
