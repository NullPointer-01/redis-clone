#!/bin/sh
set -e

# Default values (can be overridden by Docker ENV)
ROLE=${REDIS_ROLE:-master}
PORT=${REDIS_PORT:-6379}
MASTER_HOST=${REDIS_MASTER_HOST:-none}
MASTER_PORT=${REDIS_MASTER_PORT:-6379}

# Build Java command according to role
if [ "$ROLE" = "slave" ] || [ "$ROLE" = "replica" ]; then
  echo "Starting SLAVE node on port $PORT (master: $MASTER_HOST:$MASTER_PORT)"
  exec java -jar app.jar \
      --port "$PORT" \
      --replicaof "$MASTER_HOST $MASTER_PORT"

else
  echo "Starting MASTER node on port $PORT"
  exec java -jar app.jar \
      --port "$PORT"
fi
