# Neovarsity E-Commerce Capstone Project

A production-oriented e-commerce platform built using a microservices architecture with Java 21, Spring Boot, Apache Kafka, Docker, and AWS. The project follows a monorepo structure to simplify development, dependency management, and version control while allowing each microservice to evolve independently.

---

# Repository Structure

The project is organized as a **Gradle Monorepo**, where every microservice resides in its own directory under a single Git repository.

```
neovarsity-capstone/
│
├── cart-service/
├── notification-service/
├── order-service/
├── payment-service/
├── product-service/
├── user-service/
│
├── docs/
│   └── collections/
│       └── neovarsity-capstone/
│
├── local-dev-setup/
│   └── wiremock/
│
└── README.md
```

---

# Monorepo Architecture

This repository follows the **Monorepo** approach, where all Spring Boot microservices are maintained within a single Git repository.

Each service is implemented as an independent Gradle project inside its own directory.

Examples include:

* `cart-service`
* `notification-service`
* `order-service`
* `payment-service`
* `product-service`
* `user-service`

## Benefits of the Monorepo

* Single source of truth for all services.
* Easier dependency and version management.
* Simplified code reviews for cross-service changes.
* Shared CI/CD pipelines.
* Easier local development and debugging.

Although the services are stored in one repository, they are independently buildable and deployable, following microservice architecture principles.

---

# API Collections (Bruno)

Instead of Postman, this project uses **Bruno** for API development and testing.

Bruno stores collections as plain text files, making them easy to version control and review within Git.

All API collections are located under:

```
docs/collections/neovarsity-capstone/
```

The collections are organized by service and contain:

* API request definitions
* Environment variables
* Authentication configuration
* Sample request payloads
* Test endpoints

## Why Bruno?

Compared to traditional Postman collections, Bruno offers several advantages:

* Collections are stored as plain text.
* Git-friendly with meaningful diffs.
* No cloud account required.
* Faster collaboration between developers.
* Easy to review through pull requests.

Developers can simply open the repository in Bruno and start testing APIs without importing or exporting collections.

---

# Local Development Setup

The repository includes Docker Compose configurations to simplify local development.

All Docker Compose files are located under:

```
local-dev-setup/
```

Instead of maintaining one large Docker Compose file, each microservice can run their dependent services with a wiremock.

Example structure:

```
local-dev-setup/
│
├── wiremock
│    └── cart-service-mock.json
│    └── notification-service-mock.json
│    └── order-service-mock.json
│    └── payment-service-mock.json
│    └── product-service-mock.json
│    └── user-service-mock.json
├── docker-compose.database.yaml
│
├── docker-compose.kafka.yaml
│
├── docker-compose.redis.yaml
│
├── docker-compose.elastic-search.yaml
│
├── docker-compose.gateway.yaml
│
└── kong.yaml
```

Each compose file starts only the infrastructure required for that particular service, such as:

* MySQL
* MongoDB
* Redis
* Kafka
* Elasticsearch

This approach provides several benefits:

* Faster startup time.
* Lower memory and CPU usage.
* Easier debugging.
* Independent service development.
* Reduced complexity compared to maintaining one large compose file.

Developers working on a single service only need to start the dependencies required for that service.

---

# Technology Stack

* Java 21
* Spring Boot
* Gradle
* Apache Kafka
* MySQL
* MongoDB
* Redis
* Elasticsearch
* Docker
* Docker Compose
* AWS ECS
* Kong API Gateway

---

# Getting Started

1. Clone the repository.
2. Start the required infrastructure using the Docker Compose file under `local-dev-setup/<service>/`.
3. Open the project in your preferred IDE.
4. Build the required Gradle project.
5. Run the Spring Boot application.
6. Use the Bruno collections under `docs/collections/neovarsity-capstone/` to test the APIs.

---

# Development Philosophy

This repository is designed to support independent microservice development while maintaining a unified project structure. The monorepo approach, combined with Bruno API collections and service-specific Docker Compose environments, provides a streamlined developer experience and simplifies collaboration throughout the project lifecycle.
