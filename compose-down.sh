#!/usr/bin/env bash
set -e

echo "======================================="
echo "Stopping service chat-user-service..."
echo "======================================="
(cd chat-user-service && ./stop-services.sh)

echo "======================================="
echo "Stopping service chat-delivery-service..."
echo "======================================="
(cd chat-delivery-service && ./stop-services.sh)   

echo "======================================="
echo "Stopping service chat-message-service..."
echo "======================================="
(cd chat-message-service && make compose-down )

echo "======================================="
echo "Stopping chat-web..."
echo "======================================="
(cd chat-web && ./compose-down.sh)

echo "======================================="
echo "Stopping infra..."
echo "======================================="
(cd chat-infra/docker && ./stop-services-ephemeral.sh)

echo "Services stopped successfully."