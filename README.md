# 🌱 Tometrics API

**Tometrics** is an advanced gardening metrics API designed to help users **plan gardens, manage plantings, track harvests**, and record critical data like **diseases, pests, and climate insights**. What sets Tometrics apart is its strong **focus on metrics** — empowering users to analyze and compare multiple plantings of the same vegetable over time to optimize yields and strategies.

## 🚀 Features

- 🧑‍🌾 **Garden Planning**: Organize and plan garden layouts and planting schedules.
- 🌿 **Planting Management**: Record multiple plantings of the same crop and track their individual outcomes.
- 📈 **Harvest Tracking**: Log harvests and analyze yields across seasons and plantings.
- 🐛 **Disease & Pest Logging**: Record occurrences of diseases and pests to monitor their impact.
- 🌍 **Geo & Climate Enrichment**: Automatically enrich records with:
  - Geographic location
  - Climate zone
  - Real-time and historical weather data
- 📊 **Comparative Metrics Engine**: Core focus on metrics — enables meaningful comparisons between plantings to drive gardening insights.

## 🧪 Tech Stack

Tometrics is built with a robust, modern Kotlin backend stack:

- ⚙️ **Ktor**: Lightweight asynchronous web framework for Kotlin.
- 💾 **PostgreSQL**: Primary database, accessed via:
  - **JDBI**: SQL convenience layer for clean, fluent database interactions.
  - **HikariCP**: High-performance JDBC connection pooling.
- 🛠️ **Flyway**: Schema migrations made easy.
- 🔐 **Authentication**:
  - **JWT**: Token-based authentication.
  - **Identity Provider Integration**: Support for OAuth/OpenID identity providers.
- 🔄 **Cron Jobs**: Scheduled tasks for data enrichment and background jobs.
- 🧾 **Mustache**: Templating for human-readable output or templated views.
- 📃 **OpenAPI + Swagger**: Automatically documented, interactive API docs.
- 🧪 **Testing Frameworks**:
  - **Testcontainers**: Real integration testing using containerized Postgres.
  - **Kotlin Test**: Unit and integration testing made easy.

## 📘 API Documentation

Interactive API docs are available via Swagger/OpenAPI at:

https://tometrics-api.onrender.com/swagger

## 📦 Development & Setup

1. Clone the repository
2. Configure environment variables
3. Start the server with your preferred build tool
4. Access Swagger at `localhost:8080/swagger` to begin exploring

### Run tests
```bash
./gradlew test
