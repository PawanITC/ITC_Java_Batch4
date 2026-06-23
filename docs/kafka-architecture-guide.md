# Kafka Architecture Guide

## Architecture Diagram

```text
                        +-------------------+
                        |      Client       |
                        +---------+---------+
                                  |
                                  | POST /api/posts
                                  v
                     +------------+-------------+
                     |        API Gateway       |
                     +------------+-------------+
                                  |
                                  v
                     +------------+-------------+
                     |       post-service       |
                     |  PostController          |
                     |  PostService             |
                     +------------+-------------+
                                  |
                   same DB tx     | save Post + OutboxEvent
                                  v
                     +------------+-------------+
                     |      postdb (Postgres)   |
                     |  posts                   |
                     |  outbox_events           |
                     +------------+-------------+
                                  |
                                  | scheduled polling
                                  v
                     +------------+-------------+
                     |      OutboxPublisher     |
                     +------------+-------------+
                                  |
                                  | Kafka publish
                                  v
                     +------------+-------------+
                     |         Kafka Broker     |
                     |      topic: post.created |
                     +------------+-------------+
                                  |
                                  | consumer group
                                  v
              +-------------------+-------------------+
              |     feed-and-timeline-service         |
              |  TimelineEventConsumer                |
              |  ProcessedEventService                |
              |  TimelineService                      |
              +-------------------+-------------------+
                                  |
                                  | save timeline state
                                  v
                     +------------+-------------+
                     |      feeddb (Postgres)   |
                     |  timeline_posts          |
                     |  processed_events        |
                     +--------------------------+
```

---

## Observability Diagram

```text
 +------------------+       +------------------+
 |    Kafka UI      |<----->|      Kafka       |
 +------------------+       +------------------+
           |                          ^
           |                          |
           v                          |
 +------------------+                 |
 | Schema Registry  |-----------------+
 +------------------+
           ^
           |
 +------------------+
 | Debezium Connect |
 +------------------+

 +------------------+       +------------------+       +------------------+
 | Kafka Exporter   |-----> |   Prometheus     |-----> |     Grafana      |
 +------------------+       +------------------+       +------------------+

 +------------------+       +------------------+
 | OTel Collector   |-----> |      Jaeger      |
 +------------------+       +------------------+
```

---

## Interview Questions And Answers

### 1. Why did you use Kafka here?

Answer:

Kafka decouples post creation from feed generation.

That gives:

1. independent scaling of writer and consumer services
2. asynchronous processing
3. better fault tolerance
4. replay capability if a consumer falls behind or restarts

---

### 2. Why did you use the outbox pattern?

Answer:

The outbox pattern solves the dual-write problem.

Without outbox:

1. DB insert may succeed
2. Kafka publish may fail
3. system becomes inconsistent

With outbox:

1. business row and outbox row are committed in one DB transaction
2. a separate publisher reads pending outbox rows
3. Kafka publish is retried independently

That makes event delivery much safer.

---

### 3. Why not publish directly to Kafka inside `createPost()`?

Answer:

Direct publish inside request flow is risky because:

1. DB write and Kafka write are separate systems
2. you do not get a single atomic commit across both
3. transient Kafka issues could break synchronous request handling

The outbox pattern separates correctness from transport timing.

---

### 4. What does idempotency mean in your feed consumer?

Answer:

Kafka can deliver the same message more than once.

Idempotency means:

1. processing the same event again does not corrupt data
2. duplicates are detected using `eventId`
3. processed ids are stored in `processed_events`

This prevents duplicate timeline updates.

---

### 5. Why do you also check for existing timeline rows if you already store processed events?

Answer:

That is defense in depth.

1. `processed_events` protects at the consumer/event level
2. timeline existence check protects at the data level
3. DB unique constraints protect at the persistence level

If one layer fails, another still reduces risk.

---

### 6. What is event versioning and why is it useful?

Answer:

Event versioning lets producers and consumers evolve independently.

In this project:

1. `post.created` includes `eventVersion`
2. feed consumer validates the version
3. incompatible changes can be rejected or routed differently later

This prevents silent contract drift.

---

### 7. What is the role of Schema Registry?

Answer:

Schema Registry stores message schemas centrally.

It becomes important when using:

1. Avro
2. Protobuf
3. strongly governed event contracts

Right now it is provisioned for future evolution, even though your active app flow still uses JSON payloads.

---

### 8. What is Debezium doing in your architecture?

Answer:

Debezium is provisioned as a CDC platform component.

It can:

1. read DB transaction logs
2. publish DB changes to Kafka
3. support outbox-table CDC if you choose that model later

Important:

Your current active flow does not use Debezium as the publisher. It uses the application scheduler `OutboxPublisher`.

---

### 9. What is Kafka UI used for?

Answer:

Kafka UI helps inspect operational state quickly.

You can use it to see:

1. topics
2. partitions
3. messages
4. consumer groups
5. lag
6. schema registry
7. Kafka Connect / Debezium

---

### 10. What are Prometheus and Grafana doing for Kafka?

Answer:

Kafka Exporter exposes broker and consumer metrics.

Prometheus scrapes them.

Grafana visualizes them.

That gives visibility into:

1. topic offsets
2. consumer lag
3. throughput trends
4. backlog growth

---

### 11. What failure happens if feed service is down?

Answer:

Post creation can still succeed because:

1. post is stored in DB
2. outbox event is stored in DB
3. Kafka keeps the message
4. feed service can consume later when it comes back

That is one of the main benefits of async decoupling.

---

### 12. What failure happens if Kafka is down?

Answer:

Two cases:

1. If Kafka is down during the HTTP request:
   The post still persists because the request only writes DB and outbox row.
2. If Kafka is down during scheduled publishing:
   `OutboxPublisher` fails and leaves `published=false`.

That means the event remains pending and can be retried later.

---

### 13. How would you improve this further?

Answer:

Reasonable next steps:

1. move all event types to versioned contracts
2. use Avro and Schema Registry end to end
3. use Debezium outbox CDC instead of scheduler polling
4. add retention/retry policies explicitly
5. add alerting on consumer lag and DLT growth

---

## Demo Script

Use this sequence to demonstrate the architecture live.

### 1. Start observability stack

```powershell
docker compose -f observability/docker-compose-observability.yml up -d
```

### 2. Start application stack

```powershell
docker compose up -d --build
```

### 3. Check Kafka broker and UI

```powershell
docker compose ps
docker compose -f observability/docker-compose-observability.yml ps
```

Open:

1. Kafka UI: `http://localhost:8090`
2. Grafana: `http://localhost:3001`
3. Prometheus: `http://localhost:9090`
4. Jaeger: `http://localhost:16686`

### 4. Verify topic exists

In Kafka UI:

1. open cluster `local`
2. open topic `post.created`
3. confirm partitions exist

### 5. Create a post

Example request:

```powershell
curl -X POST http://localhost:8085/api/posts ^
  -H "Content-Type: application/json" ^
  -H "X-User-Id: user.demo" ^
  -H "X-Username: user.demo" ^
  -d "{\"content\":\"Kafka outbox demo post\"}"
```

### 6. Explain what just happened

Say:

1. request reached `post-service`
2. `Post` row was saved
3. `OutboxEvent` row was saved in same transaction
4. scheduler picked the outbox row
5. event was published to Kafka topic `post.created`
6. feed consumer processed it
7. timeline row was created

### 7. Show Kafka message

In Kafka UI:

1. open topic `post.created`
2. inspect latest message
3. point out:
   - `eventId`
   - `eventType`
   - `eventVersion`
   - business payload fields

### 8. Show consumer group

In Kafka UI:

1. open consumer groups
2. open `feed-timeline-group`
3. show lag and committed offsets

### 9. Show metrics

In Grafana:

1. open Kafka dashboard
2. show topic offsets moving
3. show consumer lag

### 10. Show feed-side result

Call the feed endpoint:

```powershell
curl http://localhost:8085/api/timeline/ -H "X-User-Id: user.demo"
```

This shows the timeline entry created from Kafka consumption.

---

## Fast Talking Points

Use these short lines if you need compact answers during a review or interview:

1. "Post creation is decoupled from feed generation through Kafka."
2. "I used the outbox pattern to avoid DB/Kafka dual-write inconsistency."
3. "The publisher is asynchronous, so request latency is not tied to Kafka availability."
4. "The feed consumer is idempotent using processed event tracking."
5. "The event contract is versioned to support safe evolution."
6. "Kafka UI, Prometheus, and Grafana give both message-level and metric-level visibility."
7. "Schema Registry and Debezium are provisioned for the next step toward CDC and Avro governance."

---

## Important Clarification For Your Current Project

You now have both platform support and application logic.

Platform support:

1. Kafka
2. Kafka UI
3. Schema Registry
4. Debezium Connect
5. Kafka Exporter
6. Prometheus
7. Grafana
8. Jaeger
9. OTel Collector

Active application event flow:

1. application-managed outbox publisher
2. not Debezium-based publishing yet

If later you move to Debezium outbox CDC, then:

1. the app would stop polling outbox rows itself
2. Debezium would read the outbox table and publish to Kafka

