# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`chat-presence-service` is a Python/FastAPI microservice that tracks and broadcasts user presence (online/offline/away) for the broader chat system described in the parent `CLAUDE.md`. It lives alongside Spring Boot (user profiles), Go (message persistence), and Quarkus (WebSocket/Kafka fan-out) services.

## Package manager

This project uses [`uv`](https://docs.astral.sh/uv/). Do not use `pip` or `poetry` directly.

```bash
uv run fastapi dev main.py        # run dev server with auto-reload
uv run uvicorn main:app           # run production-style

uv add <package>                  # add a dependency
uv add --dev <package>            # add a dev dependency (pytest, ruff, etc.)
uv sync                           # install all deps from lock file
uv run pytest                     # run tests
uv run pytest tests/test_foo.py   # run a single test file
uv run ruff check .               # lint
uv run ruff format .              # format
```

Python version is pinned to `3.9` via `.python-version`.

## Stack

- **FastAPI** — HTTP and WebSocket endpoints
- **uvicorn** — ASGI server
- **uv** — dependency and virtualenv management

## Cross-cutting integration

This service plugs into the same system as the other chat services. Relevant shared contracts from the parent repo:

- **JWT validation:** tokens are HMAC-SHA256 signed by `chat-web` BFF with `JWT_SECRET`. Service tokens have `exp: 10m` and a per-service `aud` claim. Validate using `JWT_SECRET` from env.
- **Kafka:** bootstrap address via `KAFKA_BOOTSTRAP` env var. Topic conventions follow `chat.*` namespace.
- **Identity:** `useruuid` (UUID) is the canonical user identity across all services — it is the JWT `sub` claim.

## Application entry point

`main.py` is the entry point. Wire the FastAPI `app` instance here; keep route modules under an `app/` or `src/` package as the service grows.
