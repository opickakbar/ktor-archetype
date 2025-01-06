# Fact & Admin Statistics

This is a Ktor-based server application that provides APIs for managing facts and retrieving admin statistics. The application supports both general user and admin functionalities.

---

## Table of Contents
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [Running Tests](#running-tests)
- [API Documentation](#api-documentation)

---

## Prerequisites
Before you get started, ensure you have the following installed on your machine:

- **Java Development Kit (JDK)**: Version 17 or above
- **Gradle**: Version 7 or above
- **Internet Access**: Required to fetch random facts from the external API

---

## Setup Instructions

1. Install dependencies:
   ```bash
   ./gradlew dependencies
   ```

2. Set up the configuration:
   Take a look at the `application.yaml` file under `src/main/resources` with the following structure:
   ```yaml
   ktor:
     application:
       baseUrl: "http://localhost:8080"
       uselessFactApiUrl: "https://uselessfacts.jsph.pl/random.json?language=en"
       apiKey: "your-admin-api-key"
   ```
   Please adjust according your needs.

---

## Running the Application

To start the server in development mode:
```bash
./gradlew run
```

The application will be available at `http://localhost:8080` by default.

---

## Running Tests

To execute all tests:
```bash
./gradlew test
```

The tests include both integration and unit tests for controllers, services, and repositories.

---

## Build the application

To build the application:
```bash
./gradlew clean build
```

This will generate the JAR files in the build/libs directory.

---

## API Documentation

### Fact APIs
- **POST /facts**: Fetches a random fact and generates a shortened URL.
- **GET /facts**: Retrieves all stored facts.
- **GET /facts/{shortenedUrl}**: Retrieves a specific fact by its shortened URL.
- **GET /facts/{shortenedUrl}/redirect**: Redirects to the original fact URL.

### Admin APIs
- **GET /admin/statistics**: Retrieves access statistics. Requires `X-API-KEY` header.

#### Headers
For Admin APIs, include the following header:
```json
{
  "X-API-KEY": "your-admin-api-key"
}
```

## Thank You

Copyright © 2025 - Muhammad Taufik Akbar
