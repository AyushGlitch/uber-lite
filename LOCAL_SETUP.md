# Local Setup and Startup

This document explains how to build and run the project locally, start supporting services (Postgres, Kafka, Redis) with Docker, and how to run an ephemeral Docker stack so data is not persisted between runs.

## Purpose
Provide an up-to-date, copy-pasteable local development guide for the repository so contributors can build and run services locally.

## Table of contents
- Prerequisites
- Clone
- Build
- Run (local JVM)
- Run individual services
- Docker: run supporting services (ephemeral)
- Docker: remove persistence / cleanup
- Flyway / DB migration notes
- Kafka notes
- Troubleshooting

## Prerequisites
- Java JDK 11 or newer
- Maven 3.6+
- Docker (optional, recommended for DB/Kafka)
- Docker Compose (optional)

## Clone
Replace `<repo_url>` with the actual repository remote.

```bash
git clone <repo_url>
cd uber-lite
```

## Build
From repository root run:

```bash
mvn clean package -DskipTests
```

This builds all modules. You can also build and run a single service by changing directory into that service folder (for example `rider-service`) and running the same command there.

## Run (local JVM)
Run a service directly from source with Maven. Example: start `rider-service`:

```bash
cd rider-service
mvn spring-boot:run
```

If you prefer to run the packaged jar:

```bash
cd rider-service
java -jar target/rider-service-0.0.1-SNAPSHOT.jar
```

Use `-Dspring-boot.run.profiles=local` or environment variables to override configuration where appropriate.

## Run individual services
There are multiple services in the repo (for example `rider-service`, `driver-service`, `location-service`, `uber-simulator`). Each has its own `application.yml`/`application.properties` under `src/main/resources`.

To run one service:
1. cd into the service folder
2. Ensure required backing services (DB, Kafka, Redis) are reachable
3. Run `mvn spring-boot:run` or `java -jar target/<artifact>.jar`

## Docker — ephemeral supporting services (Postgres, Kafka, Zookeeper, Redis)
If you want to run the DB/Kafka/Redis locally with Docker without persisting data between runs, don't use named (persisted) volumes. The repo root already contains a `docker-compose.yml`; if it uses named volumes, either remove them or use the commands below to ensure ephemeral behavior.

Example ephemeral compose (create `docker-compose.local.yml` if you want a small dedicated file):

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_USER: rider_db
      POSTGRES_PASSWORD: rider_db_pass
      POSTGRES_DB: riderdb
    ports:
      - "5432:5432"

  zookeeper:
    image: bitnami/zookeeper:latest
    environment:
      ALLOW_ANONYMOUS_LOGIN: "yes"
    ports:
      - "2181:2181"

  kafka:
    image: bitnami/kafka:latest
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENERS: PLAINTEXT://:9092
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
    ports:
      - "9092:9092"

  redis:
    image: redis:7
    ports:
      - "6379:6379"
```

Start the ephemeral stack:

```bash
docker-compose -f docker-compose.local.yml up --build -d
```

Important: `docker stop` followed by `docker start` restarts the same container and preserves its internal filesystem. To get a clean ephemeral state use `docker-compose down -v` (removes anonymous volumes) or remove containers.

## Docker: remove persistence / cleanup
To ensure no persisted data remains between runs:

- Stop and remove containers and anonymous volumes created by compose:

```bash
docker-compose -f docker-compose.local.yml down -v
```

- Force recreate containers and renew anonymous volumes on next up:

```bash
docker-compose -f docker-compose.local.yml up --force-recreate --renew-anon-volumes -d
```

- Remove all stopped containers (be careful, this is destructive):

```bash
docker container prune -f
```

- Remove all unused images/volumes (very destructive):

```bash
docker system prune -af --volumes
```

If your repository `docker-compose.yml` defines named volumes, remove or comment them out to avoid persistence.

## Flyway / DB migration notes
- Flyway is used to manage schema. If you see errors like `Migration checksum mismatch` it means a migration file that was applied previously was modified. Options:
  - Revert the migration file to the version that has the original checksum
  - Or run `flyway repair` against the DB (only in development) to update Flyway schema history

To run flyway repair from a containerized Postgres client or a host with Flyway CLI installed:

```bash
# Example using Flyway CLI (host) or inside a container where flyway is available
flyway -url=jdbc:postgresql://localhost:5432/riderdb -user=rider_db -password=rider_db_pass repair
```

Or use psql to drop and recreate the database for a clean start (development only):

```bash
# destroy DB (development)
psql -h localhost -U postgres -c "DROP DATABASE IF EXISTS riderdb; CREATE DATABASE riderdb;"
```

If Hibernate complains about missing columns (for example `Schema-validation: missing column [created_at] in table [riders]`) it means the DB schema doesn't match your entities. Fixes:
- Ensure correct migration scripts exist under `src/main/resources/db/migration` for the service and include the missing column
- Re-run Flyway migration or recreate the DB for a fresh start

## Kafka notes
- Ensure `spring.kafka.bootstrap-servers` is set correctly (e.g., `localhost:9092`).
- To list topics inside a Kafka container:

```bash
docker exec -it <kafka_container_name> kafka-topics.sh --bootstrap-server localhost:9092 --list
```

- Topics won't be auto-created if your broker or client config disables auto-creation, or if the application hasn't produced messages yet.

## Redis notes
- The project exposes a `RedisConfig` bean that configures a `RedisTemplate<String,String>`. Make sure the running Redis instance is reachable on the host/port configured.
- If Spring fails to start with:

```
Parameter 0 of method redisTemplate in ... RedisConfig required a bean of type 'org.springframework.data.redis.connection.RedisConnectionFactory' that could not be found.
```

then the application is missing the dependency that provides a `RedisConnectionFactory` (for example `spring-boot-starter-data-redis` or an auto-configured connection factory). Ensure the dependency is present in the service's `pom.xml` and that configuration properties (host/port) are correct.

## Troubleshooting
- Flyway checksum mismatch: either restore the migrated SQL file or run `flyway repair` on development DB.
- Schema validation errors for missing columns: check migration scripts and apply missing migrations, or reset DB for local development.
- Password authentication failed for DB user: verify `spring.datasource.username` and `spring.datasource.password` in the service's `application.yml` or environment variables and match them to the DB container's env vars.
- Data appears after stopping and starting containers: stopping/starting preserves the container filesystem; use `docker rm` or `docker-compose down` to fully remove containers.

## Quick reference — common commands

Start DB/Kafka/Redis (ephemeral):

```bash
docker-compose -f docker-compose.local.yml up -d
```

Stop & remove containers + anonymous volumes:

```bash
docker-compose -f docker-compose.local.yml down -v
```

Build all modules:

```bash
mvn clean package -DskipTests
```

Run a single service:

```bash
cd rider-service
mvn spring-boot:run
```

Repair Flyway (development only):

```bash
flyway -url=jdbc:postgresql://localhost:5432/riderdb -user=rider_db -password=rider_db_pass repair
```

--

If you'd like, I can also:
- Add service-specific quick commands for `rider-service`, `driver-service`, and `location-service` (e.g., ports, example env overrides)
- Create a `docker-compose.local.yml` in the repo with the example above
- Detect and list named volumes in the repo's `docker-compose.yml` and suggest edits to remove them

Tell me which of the above you want next.
