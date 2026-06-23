# Kafka Architecture Notes

## Purpose

This document explains how Kafka is working in this project end to end:

1. How `post-service` creates a post
2. How the outbox pattern publishes an event
3. How `feed-and-timeline-service` consumes that event
4. How Kafka UI, Schema Registry, Debezium, Prometheus, Grafana, Jaeger, and OpenTelemetry fit around Kafka

---

## High-Level Flow

The current application flow is:

1. Client sends `POST /api/posts`
2. `post-service` saves a `Post`
3. `post-service` also saves an `OutboxEvent` in the same database transaction
4. `OutboxPublisher` reads unpublished outbox rows
5. `OutboxPublisher` sends the payload to Kafka topic `post.created`
6. `feed-and-timeline-service` consumes from `post.created`
7. Feed service updates the `timeline_posts` table
8. Feed service stores the processed event id so duplicates are ignored later

That means Kafka is the asynchronous bridge between write-side post creation and read-side timeline updates.

---

## Why This Design Exists

Without Kafka:

1. `post-service` would need to call feed logic directly
2. Post creation and feed update would be tightly coupled
3. A feed failure could break post creation

With Kafka + outbox:

1. `post-service` owns writing posts
2. `feed-and-timeline-service` only reacts to events
3. If feed is temporarily down, posts can still be created
4. Feed catches up later by consuming Kafka messages
5. The outbox prevents the classic failure where DB write succeeds but event publish is lost

---

## Request Entry Point

File: `backend/postAndTimeline/src/main/java/com/itc/linkedin/postandtimeline/controller/PostController.java`

What happens:

1. The controller receives `POST /api/posts`
2. It tries to resolve user identity from:
   - `X-User-Id`
   - `X-Username`
   - fallback JWT identity
3. It rejects the request with `401` if identity is missing
4. It delegates to `PostService.createPost(...)`

Relevant code responsibilities:

1. Identity resolution
2. Validation of the incoming request
3. Forwarding to business logic

This controller does not know anything about Kafka. Kafka starts one layer deeper, in the service and outbox logic.

---

## Post-Service Write Path

File: `backend/postAndTimeline/src/main/java/com/itc/linkedin/postandtimeline/service/PostService.java`

This is the core write-side logic.

### What `createPost(...)` does

1. Builds a `Post` entity
2. Saves it to the `posts` table
3. Builds a `PostCreatedEvent`
4. Serializes that event to JSON
5. Builds an `OutboxEvent`
6. Saves the outbox row
7. Returns the API response

### Important detail: one transaction

`createPost(...)` is marked `@Transactional`.

That means:

1. The `Post` row and `OutboxEvent` row are saved in the same database transaction
2. If the transaction fails, neither is committed
3. This guarantees consistency between business data and event data

### Event fields

The `PostCreatedEvent` currently contains:

1. `eventId`
2. `eventType`
3. `eventVersion`
4. `occurredAt`
5. `postId`
6. `authorId`
7. `authorName`
8. `authorHeadline`
9. `content`
10. `createdAt`

Why these matter:

1. `eventId` supports idempotency
2. `eventType` makes the payload self-describing
3. `eventVersion` supports contract evolution
4. Business fields carry the actual post data feed needs

---

## Outbox Table

File: `backend/postAndTimeline/src/main/java/com/itc/linkedin/postandtimeline/outbox/OutboxEvent.java`

The outbox table stores pending events before Kafka publish.

Fields:

1. `aggregateType`
2. `aggregateId`
3. `eventType`
4. `topic`
5. `payload`
6. `published`
7. `createdAt`
8. `publishedAt`

Meaning:

1. `payload` is the serialized event body
2. `topic` tells the publisher where to send it
3. `published=false` means still pending
4. After successful Kafka send, `published=true`

This table is the buffer between the database transaction and Kafka.

---

## Outbox Publisher

File: `backend/postAndTimeline/src/main/java/com/itc/linkedin/postandtimeline/outbox/OutboxPublisher.java`

This is the component that moves data from the outbox table into Kafka.

### How it works

1. Runs every 5 seconds because of `@Scheduled(fixedDelay = 5000)`
2. Reads the oldest unpublished outbox rows
3. Sends each message to Kafka using `KafkaTemplate`
4. Waits for Kafka send acknowledgment using `.get()`
5. Marks the outbox row as published
6. Stores `publishedAt`

### Why waiting for `.get()` matters

Without `.get()`:

1. Send could fail asynchronously
2. Code might mark the outbox row as published too early

With `.get()`:

1. The code waits for broker acknowledgment
2. Only acknowledged sends are marked published

This makes the outbox implementation much safer.

---

## Kafka Topic

The main topic in this path is:

1. `post.created`

Who writes to it:

1. `OutboxPublisher`

Who reads from it:

1. `feed-and-timeline-service`

Kafka itself does not understand business meaning. It only stores ordered records in topics and lets consumer groups read them.

---

## Feed Consumer

File: `backend/feedAndTimeline/src/main/java/com/itc/linkedin/feedAndTimeline/kafka/consumer/TimelineEventConsumer.java`

This is the read-side consumer.

### What happens for `post.created`

1. Kafka listener receives the raw JSON string
2. `ObjectMapper` deserializes it into `PostCreatedEvent`
3. Event contract is validated
4. `eventId` is checked against previously processed events
5. If already processed, it is skipped
6. If new, it calls `TimelineService.handlePostCreated(...)`
7. After successful processing, the event id is recorded in `processed_events`

### Why idempotency exists

Kafka delivery is at-least-once in most practical setups.

That means duplicates can happen because of:

1. retries
2. consumer restarts
3. rebalancing
4. transient failures after processing but before offset commit

Idempotency means:

1. duplicate event arrives
2. service sees it already processed
3. duplicate is ignored

That protects your timeline table from repeated inserts or repeated updates.

---

## Feed Timeline Update Logic

File: `backend/feedAndTimeline/src/main/java/com/itc/linkedin/feedAndTimeline/service/TimelineService.java`

### For `post.created`

`handlePostCreated(...)` does this:

1. Checks whether a timeline row already exists for `(timelineUserId, postId)`
2. If it exists, skips
3. If not, creates a `TimelinePost`
4. Saves it to `timeline_posts`

This is a second layer of protection:

1. consumer-level idempotency via `processed_events`
2. data-level duplicate prevention via existence check and unique constraint

### For other events

The same service also reacts to:

1. `post.deleted`
2. `post.liked`
3. `comment.created`

Those mutate existing timeline rows instead of creating new ones.

---

## Processed Events Table

Files:

1. `backend/feedAndTimeline/src/main/java/com/itc/linkedin/feedAndTimeline/entity/ProcessedEvent.java`
2. `backend/feedAndTimeline/src/main/java/com/itc/linkedin/feedAndTimeline/service/ProcessedEventService.java`

Purpose:

1. store which event ids were already handled
2. prevent duplicate processing

Fields include:

1. `consumerName`
2. `topic`
3. `eventId`
4. `eventVersion`
5. `processedAt`

This is how your feed consumer achieves durable idempotency.

---

## Event Versioning

Currently versioning was added for `post.created`.

Why versioning matters:

1. event schemas change over time
2. consumers and producers may not deploy at the same moment
3. versioning lets you evolve contracts safely

Your feed consumer currently accepts:

1. `eventType = post.created`
2. `eventVersion = 1`

If a future incompatible version appears, the consumer can reject or route it differently.

---

## How Kafka Broker Fits In

File: `observability/docker-compose-observability.yml`

Service:

1. `kafka`

What it does:

1. stores topic data
2. accepts producer writes
3. serves consumer reads
4. tracks consumer groups and offsets

Important config:

1. `KAFKA_LISTENERS`
2. `KAFKA_ADVERTISED_LISTENERS`

In Docker, your services connect to Kafka using:

1. `kafka:9092`

That hostname works because all containers share the same Docker network.

---

## What Kafka UI Does

File: `observability/docker-compose-observability.yml`

Service:

1. `kafka-ui`

Purpose:

1. inspect topics
2. inspect partitions
3. inspect messages
4. inspect consumer groups
5. inspect Schema Registry
6. inspect Kafka Connect / Debezium

Use it to answer questions like:

1. Did `post.created` receive messages?
2. Is `feed-timeline-group` consuming them?
3. Is consumer lag growing?
4. Are Debezium connectors registered?

URL:

1. `http://localhost:8090`

---

## What Schema Registry Does

File: `observability/docker-compose-observability.yml`

Service:

1. `schema-registry`

Purpose:

1. store schemas centrally
2. support schema evolution
3. validate Avro-based contracts at runtime

Right now your app-level events are JSON strings.

Schema Registry matters when you move to:

1. Avro
2. Protobuf
3. schema-managed structured events

It becomes especially important when many services publish and consume the same event families.

URL:

1. `http://localhost:8081`

---

## What Debezium Connect Does

File: `observability/docker-compose-observability.yml`

Service:

1. `debezium-connect`

Purpose:

1. read database change logs
2. convert DB changes into Kafka events
3. publish those events into Kafka automatically

Important clarification:

Your current post flow is not using Debezium for the outbox publish.

Your current flow is:

1. application writes outbox row
2. application scheduler publishes to Kafka

Debezium would be a different model:

1. application writes business row or outbox row
2. Debezium watches DB transaction log
3. Debezium publishes DB changes to Kafka

So Debezium is ready in the platform, but your active implementation is still application-managed outbox publishing.

URL:

1. `http://localhost:8084`

---

## What Kafka Exporter Does

File: `observability/docker-compose-observability.yml`

Service:

1. `kafka-exporter`

Purpose:

1. expose Kafka metrics in Prometheus format

Metrics include things like:

1. topic offsets
2. consumer lag
3. consumer group state visibility

Kafka exporter does not move data. It only exposes metrics for monitoring.

---

## What Prometheus Does

Files:

1. `observability/docker-compose-observability.yml`
2. `observability/prometheus/prometheus.yml`

Purpose:

1. scrape metrics from services
2. scrape metrics from Kafka exporter
3. store time-series data

This lets you graph:

1. message growth
2. consumer lag over time
3. service metrics

URL:

1. `http://localhost:9090`

---

## What Grafana Does

File: `observability/docker-compose-observability.yml`

Service:

1. `grafana`

Purpose:

1. visualize Prometheus metrics
2. show Kafka dashboard panels
3. make lag and offset trends easier to see than in raw Prometheus

URL:

1. `http://localhost:3001`

---

## What Jaeger and OpenTelemetry Do

File: `observability/docker-compose-observability.yml`

Services:

1. `otel-collector`
2. `jaeger`

Purpose:

1. collect tracing data from services
2. store and visualize request traces

This is not Kafka transport, but it helps you observe the services that use Kafka.

Example use:

1. request enters gateway
2. request reaches post-service
3. post-service saves DB row
4. later feed-service consumes event

Tracing helps analyze the service side of that path.

---

## Current Architecture Summary

### Write side

1. `post-service`
2. `posts` table
3. `outbox_events` table
4. `OutboxPublisher`
5. Kafka topic `post.created`

### Read side

1. `feed-and-timeline-service`
2. `TimelineEventConsumer`
3. `processed_events` table
4. `timeline_posts` table

### Platform side

1. Kafka broker
2. Kafka UI
3. Schema Registry
4. Debezium Connect
5. Kafka exporter
6. Prometheus
7. Grafana
8. Jaeger
9. OpenTelemetry Collector

---

## How To Verify the Flow Yourself

1. Start the stack
2. Open Kafka UI at `http://localhost:8090`
3. Check topic `post.created`
4. Create a post through the API
5. Refresh topic messages in Kafka UI
6. Confirm a message appears in `post.created`
7. Check consumer group `feed-timeline-group`
8. Confirm feed consumer lag goes back toward zero
9. Open Grafana at `http://localhost:3001`
10. Open the Kafka dashboard and watch offsets and lag

---

## Simple Mental Model

Use this mental model:

1. `post-service` writes business data
2. outbox stores the event safely
3. publisher moves the event into Kafka
4. Kafka buffers and delivers it
5. feed consumer reacts to it
6. processed-event tracking prevents duplicates
7. observability stack shows whether all of that is healthy

---

## One Important Clarification

You now have both:

1. application-managed outbox publishing
2. Debezium platform support

Those are not the same thing.

Current active implementation:

1. `OutboxPublisher` publishes from the application

Future optional implementation:

1. Debezium watches outbox table and publishes changes automatically

If you later switch to Debezium outbox pattern, the scheduler publisher can be removed and Debezium becomes the publisher.

---

## Useful URLs

1. Kafka UI: `http://localhost:8090`
2. Schema Registry: `http://localhost:8081`
3. Debezium Connect: `http://localhost:8084`
4. Prometheus: `http://localhost:9090`
5. Grafana: `http://localhost:3001`
6. Jaeger: `http://localhost:16686`

