# CLAUDE.md — chat-user-service

Spring Boot 3.5 service that is a pure profile store — no credentials, no token issuance. Provisioned automatically by `chat-web` on registration. See [README.md](README.md) for full API and env vars.

## Commands

```bash
./mvnw spring-boot:run    # local (requires Postgres on localhost:5432/chat-user)
./mvnw package            # build jar
./docker-build.sh         # build Docker image
docker compose up         # run via compose (copy env-example → .env.docker first)
```

## Source layout

```
src/main/java/org/demo/user/
  UserApplication.java
  data/
    entity/UserEntity.java          # JPA entity; dual-key: id (sequence) + user_id (UUID)
    repository/UserRepository.java  # Spring Data JPA
  service/
    UserService.java                # interface
    impl/UserServiceImpl.java       # implementation
  rest/
    controller/UserController.java  # @RestController, base path /api/users
    model/                          # CreateUserRequest/Response, ErrorResponse, PaginatedResponse
  mapper/UserMapper.java            # MapStruct DTO ↔ entity
  security/SecurityConfig.java      # OAuth2 resource server (JWT only, no form login)
  exception/GlobalExceptionHandler.java
src/main/resources/
  application.yaml                  # full config; ddl-auto: update in dev, none in ST/PR/ND profiles
  data.sql                          # seed: 3 test users via ON CONFLICT DO NOTHING
```

## Key patterns

**Dual-key entity (`UserEntity.java`):** `id` (BIGINT sequence) is the internal surrogate. `user_id` (UUID, unique) is the public identifier referenced by all other services. The `user_id` is generated via `@PrePersist` if not provided, but the BFF always passes the Better Auth UUID to keep identity consistent.

**JWT security (`SecurityConfig.java`):** Uses `spring-boot-starter-oauth2-resource-server` only — no form login or basic auth. Configured with `NimbusJwtDecoder.withSecretKey(...)` using the shared `JWT_SECRET`. Context path is `/user`, so all routes are reachable at `/user/api/users`.

**Schema management:** `ddl-auto: update` in default (dev) profile. Disabled (`none`) in `ST`, `PR`, `ND` spring profiles — manage schema externally in those environments.

**Env var mapping (`application.yaml`):**
```yaml
datasource:
  url: ${db.url}
  username: ${db.username}
  password: ${db.password}
app.jwt.secret: ${JWT_SECRET}
```

## Inactive scaffolding

- `spring-boot-starter-integration` deps are declared but not active — planned `UserCreated` Kafka event
- `PaginatedResponse<T>` / `PaginationMetadata` — present but not wired to any endpoint
