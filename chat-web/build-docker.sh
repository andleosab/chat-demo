#!/usr/bin/env bash
# preserve the env file for local development, only copy if it doesn't exist
[ -f .env.local.docker ] || cp env-example .env.local.docker
docker build -f Dockerfile.distroless -t chat-web:dist .