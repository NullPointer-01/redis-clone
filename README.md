# Redis Clone (Java)

A lightweight **Redis re-implementation in Java**, built to understand the internals of Redis such as event loops, data structures, persistence, and replication.

## Overview
This project implements core Redis functionality from scratch, focusing on performance, networking, and system design concepts behind an in-memory data store.

## Features
- **Non-blocking Event Loop** with I/O multiplexing  
- **Core Data Structures**
  - Strings
  - Lists
  - Hashes
  - Sorted Sets (ZSets)
  - GeoSpatial data
- **Pub/Sub messaging**
- **Streams**
- **Transactions**
- **Asynchronous Master–Slave Replication **
- **Append-Only File (AOF) Persistence**

## Tech Stack
- **Language:** Java  
- **Build Tool:** Maven  
- **Networking:** Java NIO  
- **Containerization:** Docker & Docker Compose  


## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/NullPointer-01/redis-clone.git

cd redis-clone

mvn clean package

java -jar target/redis-clone.jar

docker build -t redis-clone .
docker run -p 6379:6379 redis-clone

docker-compose up
```

### 2. Example Usage
```bash
redis-cli -p 6379

SET user:1 Alice
GET user:1

LPUSH tasks "build redis clone"
HSET profile name Alice age 24

MULTI
SET a 10
SET b 20
EXEC
```

