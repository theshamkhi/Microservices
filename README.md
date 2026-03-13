# 🔗 Microservices Communication — Spring Boot

> Demonstrating 3 ways for microservices to communicate over HTTP:
> **RestTemplate**, **WebClient**, and **OpenFeign**

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Communication Patterns](#communication-patterns)
- [Testing](#testing)

---

## Overview

This project contains two independent Spring Boot microservices:

| Service | Port | Role |
|---------|------|------|
| **Service B** | `8082` | Data provider — exposes a User REST API |
| **Service A** | `8081` | Consumer — calls Service B using 3 different HTTP clients |

The goal is to understand and compare three approaches Spring offers for service-to-service HTTP communication.

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                     Client                          │
│              (browser / Postman / curl)             │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP Request
                       ▼
┌─────────────────────────────────────────────────────┐
│                   Service A :8081                   │
│                                                     │
│  GET /rest-template/{id}  →  RestTemplateService    │
│  GET /web-client/{id}     →  WebClientService       │
│  GET /feign/{id}          →  UserFeignService       │
│                                 │                   │
└─────────────────────────────────┼───────────────────┘
                                  │ HTTP GET /users/{id}
                                  ▼
┌─────────────────────────────────────────────────────┐
│                   Service B :8082                   │
│                                                     │
│              GET /users/{id}                        │
│         returns User { id, name, email, role }      │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## Project Structure

```
microservices/
├── service-a/
│   └── src/main/java/com/micro/servicea/
│       ├── ServiceAApplication.java      # Entry point — @EnableFeignClients
│       ├── config/
│       │   └── AppConfig.java            # RestTemplate & WebClient beans
│       ├── model/
│       │   └── User.java                 # DTO to deserialize Service B responses
│       ├── client/
│       │   └── UserFeignClient.java      # Feign declarative interface
│       ├── service/
│       │   ├── RestTemplateService.java  # Blocking HTTP client
│       │   ├── WebClientService.java     # Reactive HTTP client
│       │   └── UserFeignService.java     # Service layer wrapping the Feign client
│       └── controller/
│           └── UserController.java       # Exposes the 3 endpoints
│
└── service-b/
    └── src/main/java/com/micro/serviceb/
        ├── ServiceBApplication.java      # Entry point
        ├── model/
        │   └── User.java                 # User entity
        └── controller/
            └── UserController.java       # GET /users/{id}
```

---

## Prerequisites

- Java 17+
- Maven 3.8+

---

## Getting Started

**Start Service B first** (it must be running before Service A tries to call it):

```bash
# Terminal 1
cd service-b
./mvnw spring-boot:run
```

Then start Service A:

```bash
# Terminal 2
cd service-a
./mvnw spring-boot:run
```

Both services are ready when you see:
```
Started ServiceBApplication on port 8082
Started ServiceAApplication on port 8081
```

---

## API Endpoints

### Service B — `http://localhost:8082`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users/{id}` | Returns a user by ID (valid IDs: 1, 2, 3) |

**Example response:**
```json
{
  "id": 1,
  "name": "Alice Martin",
  "email": "alice@example.com",
  "role": "ADMIN"
}
```

---

### Service A — `http://localhost:8081`

| Method | Endpoint | Client Used |
|--------|----------|-------------|
| GET | `/rest-template/{id}` | RestTemplate (blocking) |
| GET | `/web-client/{id}` | WebClient (reactive) |
| GET | `/feign/{id}` | OpenFeign (declarative) |

All three endpoints return the **same JSON** — only the internal mechanism differs.

---

## Communication Patterns

### 1. RestTemplate — Classic Blocking

```java
restTemplate.getForObject("http://localhost:8082/users/{id}", User.class, id);
```

- Synchronous — the thread **waits** until the response arrives
- Simple and easy to understand
- ⚠️ In maintenance mode — Spring recommends WebClient for new projects

---

### 2. WebClient — Reactive Non-Blocking

```java
webClient.get()
    .uri("/users/{id}", id)
    .retrieve()
    .bodyToMono(User.class);
```

- Asynchronous — the thread is **freed** while waiting for the response
- Returns `Mono<User>` (a reactive type)
- Best choice for high-throughput applications

---

### 3. OpenFeign — Declarative Interface

```java
@FeignClient(name = "service-b", url = "http://localhost:8082")
public interface UserFeignClient {
    @GetMapping("/users/{id}")
    User getUserById(@PathVariable("id") Long id);
}
```

- No implementation code — Spring generates the HTTP client automatically
- Reads like a normal Java method call
- The interface is wrapped in a `UserFeignService` to respect the layered architecture
- Best choice for readability and maintainability

---

### Layered Architecture

All three approaches follow the same consistent pattern:

```
Controller  →  Service            →  HTTP Client
Controller  →  RestTemplateService →  RestTemplate
Controller  →  WebClientService    →  WebClient
Controller  →  UserFeignService    →  UserFeignClient (interface)
```

The controller never depends directly on any HTTP client — it always goes through a service layer. This keeps each layer focused on a single responsibility and makes the code easier to maintain and extend.

---

### Comparison

| | RestTemplate | WebClient | OpenFeign |
|---|---|---|---|
| **Blocking** | ✅ Yes | ❌ No | ✅ Yes |
| **Code style** | Imperative | Reactive | Declarative |
| **Boilerplate** | Medium | Medium | Minimal |
| **Learning curve** | Easy | Hard | Easy |
| **Spring recommendation** | Legacy | ✅ Preferred | ✅ Preferred |

---

## Testing

Import `microservices-collection.json` into Postman, or use curl:

```bash
# Direct — Service B
curl http://localhost:8082/users/1

# Via RestTemplate
curl http://localhost:8081/rest-template/1

# Via WebClient
curl http://localhost:8081/web-client/1

# Via Feign
curl http://localhost:8081/feign/1

# 404 case
curl http://localhost:8081/feign/99
```

All four calls above should return identical JSON for the same user ID.