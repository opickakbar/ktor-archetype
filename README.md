# Ktor Project Archetype

This repository serves as my personal template/archetype for Ktor-based server applications. Ktor is a functional, non-blocking backend framework designed for building asynchronous, high-performance applications. This repo provides a robust foundation with an implemented MVC pattern following the DRY concept. It also includes commonly used dependencies pre-installed to accelerate development.

## 🚀 Features
- **Non-blocking Architecture:** Built with Ktor's asynchronous, functional programming capabilities for high-performance applications.
- **MVC Architecture:** Clean separation of concerns for maintainability.
- **Pre-configured Dependencies:** Includes popular libraries for faster setup.
- **Scalable Structure:** Ready to support small to large-scale applications.

---

## 📋 Table of Contents
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [Running Tests](#running-tests)
- [Build Instructions](#build-instructions)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

---

## 🛠️ Prerequisites
Ensure you have the following installed on your machine:

- **Java Development Kit (JDK):** Version 17 or above
- **Gradle:** Version 7 or above

---

## ⚙️ Setup Instructions

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/opickakbar/ktor-project-archetype.git
   cd ktor-project-archetype
   ```

2. **Install Dependencies:**
   ```bash
   ./gradlew dependencies
   ```

3. **Configure the Application:**
   Update `application.yaml` under `src/main/resources`:
   ```yaml
   ktor:
     application:
       baseUrl: "http://localhost:8080"
       apiKey: "your-api-key"
   ```

---

## 🚀 Running the Application

Start the server in development mode:
```bash
./gradlew run
```
The application will be available at `http://localhost:8080` by default.

---

## ✅ Running Tests

Run all tests:
```bash
./gradlew test
```
This includes both unit and integration tests for controllers, services, and repositories.

---

## 🏗️ Build Instructions

To build the application:
```bash
./gradlew clean build
```
The JAR files will be generated in the `build/libs` directory.

---

## 🗂️ Project Structure

```plaintext
src/
├── main/
│   ├── kotlin/       # Application source code
│   └── resources/    # Configuration files
├── test/             # Unit and integration tests
└── build.gradle.kts  # Build configuration
```

---

**Built with ❤️**
