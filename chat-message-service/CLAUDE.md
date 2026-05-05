# CLAUDE.md — chat-message-service

Go service that owns all persistent chat data: conversations, participants, and message history. Also consumes Kafka messages for persistence and produces participant lifecycle events. See [README.md](README.md) for full API reference and env vars.

## Commands

```bash
make build          # compile to bin/server
make run            # run the binary (loads .env)
make migrate-up     # apply migrations (requires DATABASE_URL)
make migrate-down   # rollback all migrations
make sqlc           # regenerate Go types from SQL (run after editing internal/db/queries/*.sql)
make docker-build   # build scratch image
make compose-up     # start via docker compose
```

Copy `env-example` → `.env` before running locally.

## Source layout

```
cmd/server/main.go                    # entry: wires config → app → http server
internal/
  config/config.go                    # reads env vars into Config struct
  app/
    app.go                            # dependency wiring (db, kafka, handlers, router)
    db.go                             # pgx pool setup
  db/
    queries/                          # .sql files — edit these, then run `make sqlc`
      conversations.sql
      participants.sql
      messages.sql
    sqlc/                             # generated — do not edit
    migrations/                       # golang-migrate up/down files (0001..0009)
    common/pg_utils.go
  model/                              # domain structs (conversation, participant, message)
  repository/                         # DB access; *_repo_sqlc.go wraps SQLC-generated queries
    conversation_repo_sqlc.go
    message_repo_sqlc.go
    participant_repo_sqlc.go
  service/                            # business logic; calls repository + kafka
  rest/
    router.go                         # Chi router setup + middleware chain
    handler/                          # HTTP handlers (conversation, message, participant)
    interceptor/                      # auth_middleware, conversation_id_middleware, require_role
    response/response.go
  kafka/
    consumer.go                       # consumes chat.messages → persists to DB
    producer.go                       # publishes participant events
    message.go                        # Kafka message struct + MapKafkaMessage()
  grpc/                               # scaffolding only, not yet implemented
```

## Key patterns

**SQLC workflow:** SQL queries live in `internal/db/queries/*.sql`. After any change run `make sqlc` to regenerate `internal/db/sqlc/`. Never edit generated files directly.

**Repository pattern:** Each aggregate (conversation, participant, message) has an interface in `*_repository.go` and a SQLC-backed implementation in `*_repo_sqlc.go`. Handlers depend on the interface, not the concrete type.

**Router middleware chain (`rest/router.go`):** `RequestID → Logger → Recoverer → URLFormat → SetContentType(JSON) → AuthMiddleware`. `AuthMiddleware` validates the Bearer JWT (accepts multiple issuers via comma-separated `JWT_ISSUER` — both `chat-web` and `chat-delivery-service` call this service).

**Kafka consumer (`kafka/consumer.go`):** Manual commit, polls batches up to 100 records. Poison-pill handling: unmarshalable records are committed and skipped so they don't block the partition. Uses `context.Canceled` check to suppress log noise on clean shutdown.

**Kafka producer (`kafka/producer.go`):** `AllISRAcks`, 10s delivery timeout. Record key is `conversationId` (string) to ensure per-conversation ordering.

**Cursor-based pagination with visibility guard:** `GetMessagesByConversationCursor` in `queries/messages.sql` uses `id < cursor` (keyset) and filters by `joined_at` so users never see messages sent before they joined. The `mid` query param defaults to `MaxInt64` to return the latest page when not specified.

**Private conversation deduplication (`conversation_repo_sqlc.go`):** Wrapped in a transaction. Checks for an existing conversation using:
```sql
WHERE user_uuid IN (uuid1, uuid2)
GROUP BY conversation_id HAVING COUNT(*) = 2
```
Returns the existing conversation ID if found rather than creating a duplicate.

**Base path:** All routes are mounted under `/messaging` (set by the caller in `app.go`).
