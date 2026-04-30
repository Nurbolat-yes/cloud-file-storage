[README (2).md](https://github.com/user-attachments/files/27236855/README.2.md)
# ☁️ Cloud File Storage

A full-stack personal cloud file storage application — similar to Google Drive — that allows users to upload, organize, search, move, rename, preview, and download files through a modern web interface.
The frontend implemented by https://github.com/zhukovsd/cloud-storage-frontend/. I just get frontend from here. I focused on backend.

**Live Demo:**
- 🌐 Frontend: [http://13.60.208.77:80](http://13.60.208.77/)
- 📖 Swagger UI: [http://13.60.208.77:8080/swagger-ui/index.html](http://13.60.208.77:8080/swagger-ui/index.html#/)

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [API Reference](#api-reference)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Variables](#environment-variables)
  - [Run with Docker Compose](#run-with-docker-compose)
  - [Run Backend Locally](#run-backend-locally)
  - [Run Frontend Locally](#run-frontend-locally)
- [Deployment (AWS EC2)](#deployment-aws-ec2)
- [Database Migrations](#database-migrations)
- [Testing](#testing)

---

## ✨ Features

- **User Authentication** — Registration, login, and logout with session-based auth stored in Redis
- **File Upload** — Upload one or multiple files at once via drag-and-drop or file picker
- **Folder Management** — Create nested folder structures
- **File Browser** — Navigate folders with breadcrumb navigation; toggle between tile and list view
- **File Search** — Search files and folders by name across the entire storage
- **Move / Rename** — Move or rename files and folders
- **Download** — Download individual files or folders (packed as `.zip`)
- **File Preview** — Preview media and documents directly in the browser
- **Dark Mode** — Toggle between light and dark themes
- **Per-User Isolation** — Each user's files are stored in a dedicated namespace (`user-{id}-files/`) in MinIO
- **Role-Based Access** — Users have `USER` or `ADMIN` roles

---

## 🛠 Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Spring Boot | 3.4.4 | Application framework |
| Spring Security | — | Authentication & authorization |
| Spring Session + Redis | — | Distributed session management |
| Spring Data JPA + Hibernate | — | ORM / database access |
| PostgreSQL | 17 | Relational database (user data) |
| MinIO SDK | 9.0.0 | Object storage client |
| Liquibase | — | Database schema migrations |
| MapStruct | 1.5.5 | DTO ↔ Entity mapping |
| Lombok | — | Boilerplate reduction |
| SpringDoc OpenAPI (Swagger) | 2.8.8 | API documentation |
| Gradle | 8.5 | Build tool |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| React | 19 | UI framework |
| Vite | 6 | Build tool & dev server |
| MUI (Material UI) | 6.4.5 | Component library |
| React Router DOM | 7 | Client-side routing |
| Axios | 1.7.9 | HTTP client |
| Framer Motion | 12 | Animations |
| React Player | 2.16 | Media file preview |
| React Selecto | 1.26 | Multi-select via drag |
| React Moveable | 0.56 | Drag interactions |

### Infrastructure
| Service | Image | Purpose |
|---|---|---|
| PostgreSQL | `postgres:17` | User database |
| MinIO | `minio/minio:RELEASE.2025-09-07T16-13-09Z` | File object storage |
| Redis | `redis:alpine` | Session storage |
| Nginx | `nginx:1.27-alpine` | Frontend web server |

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────┐
│                  AWS EC2                     │
│                                             │
│  ┌──────────┐      ┌─────────────────────┐  │
│  │  Nginx   │      │   Spring Boot API   │  │
│  │ :80      │      │   :8080             │  │
│  │ (React)  │      │                     │  │
│  └──────────┘      └──────────┬──────────┘  │
│                               │             │
│         ┌─────────────────────┼──────────┐  │
│         │                     │          │  │
│  ┌──────▼──────┐  ┌───────────▼───┐  ┌───▼──┐  │
│  │ PostgreSQL  │  │    MinIO      │  │Redis│  │
│  │ :5432       │  │ :9000/:9001   │  │:6379│  │
│  │ (users)     │  │ (files)       │  │(sess│  │
│  └─────────────┘  └───────────────┘  └─────┘  │
└─────────────────────────────────────────────┘
```

Each user's files are namespaced in MinIO as:
```
user-{userId}-files/
  ├── documents/
  │   └── report.pdf
  ├── images/
  │   └── photo.jpg
  └── notes.txt
```

---

## 📁 Project Structure

```
Cloud file storage/
├── docker-compose.yaml          # Orchestrates all services
├── backend/
│   ├── Dockerfile               # Multi-stage build (Gradle → JRE Alpine)
│   ├── build.gradle             # Dependencies and build config
│   └── src/
│       ├── main/
│       │   ├── java/by/nurbolat/cloud_file_storage/
│       │   │   ├── CloudFileStorageApplication.java
│       │   │   ├── config/
│       │   │   │   ├── MinioConfig.java       # MinIO client bean
│       │   │   │   └── SecurityConfig.java    # Spring Security filter chain
│       │   │   ├── controller/
│       │   │   │   ├── MinioController.java   # File/folder REST endpoints
│       │   │   │   ├── UserAuthController.java # Sign-up / sign-in / sign-out
│       │   │   │   └── UserController.java    # Get current user
│       │   │   ├── dto/
│       │   │   │   ├── minio/  (File, Folder, Resource, ResourceType)
│       │   │   │   └── user/   (UserCreateDto, UserLoginDto, UserReadDto)
│       │   │   ├── entity/
│       │   │   │   ├── User.java
│       │   │   │   └── Roles.java             # USER, ADMIN
│       │   │   ├── exception/                 # Global and auth exception handlers
│       │   │   ├── mapper/
│       │   │   │   └── UserMapper.java        # MapStruct mapper
│       │   │   ├── repository/
│       │   │   │   ├── MinioRepository.java   # MinIO operations
│       │   │   │   └── UserRepository.java    # JPA repository
│       │   │   └── service/
│       │   │       ├── impl/
│       │   │       │   ├── MinioServiceImpl.java
│       │   │       │   ├── UserAuthServiceImpl.java
│       │   │       │   └── UserServiceImpl.java
│       │   │       ├── MiniService.java
│       │   │       ├── UserAuthService.java
│       │   │       └── UserService.java
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/changelog/              # Liquibase migration scripts
│       └── test/
│           ├── integration/
│           │   ├── BaseIntegrationTest.java   # Testcontainers base
│           │   └── UserAuthControllerIT.java
│           └── unit/
│               └── UserMapperTest.java
└── frontend/
    ├── Dockerfile               # Multi-stage build (Node → Nginx Alpine)
    ├── nginx.conf               # Nginx config with gzip + SPA fallback
    ├── vite.config.js
    ├── package.json
    └── src/
        ├── pages/               # Index, SignIn, SignUp, Files, ErrorPage
        ├── components/          # Header, FileBrowser, Selection, Inputs, etc.
        ├── context/             # Auth, Storage navigation, Theme, Notifications
        ├── services/fetch/      # API call wrappers (Axios)
        ├── modals/              # Rename, FolderCreate, FilePreview, FileTasks
        ├── exception/           # Typed HTTP exception classes
        └── assets/icons/        # 300+ file-type SVG icons
```

---

## 📡 API Reference

Base URL: `http://13.60.208.77:8080/api`

Full interactive documentation: [Swagger UI](http://13.60.208.77:8080/swagger-ui/index.html#/)

### Authentication — `/api/auth`

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/auth/sign-up` | Register a new user | No |
| `POST` | `/api/auth/sign-in` | Login | No |
| `POST` | `/api/auth/sign-out` | Logout and invalidate session | Yes |

**Request body for sign-up / sign-in:**
```json
{
  "email": "user@example.com",
  "password": "secret"
}
```

**Response (201 / 200):**
```json
{
  "id": 1,
  "username": "user@example.com"
}
```

---

### User — `/api`

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/user/me` | Get currently authenticated user | Yes |

---

### Resources (Files & Folders) — `/api`

All resource endpoints require authentication. The `path` parameter is relative to the user's root (e.g., `documents/` or `documents/file.pdf`). Use `path=/` for the root directory.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/resource?path=<dir>` | Upload one or more files to a directory |
| `GET` | `/api/resource?path=<path>` | Get info about a file or folder |
| `GET` | `/api/resource/download?path=<path>` | Download file or folder as ZIP |
| `DELETE` | `/api/resource?path=<path>` | Delete a file or folder |
| `GET` | `/api/resource/move?from=<path>&to=<path>` | Move / rename a resource |
| `GET` | `/api/resource/search?query=<query>` | Search resources by name |
| `POST` | `/api/directory?path=<path>` | Create a new folder |
| `GET` | `/api/directory?path=<path>` | List contents of a folder |

---

## 🚀 Getting Started

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) & [Docker Compose](https://docs.docker.com/compose/install/) v2+
- Java 21 (for local backend development)
- Node.js 22+ (for local frontend development)

### Environment Variables

Create a `.env` file in the project root (next to `docker-compose.yaml`):

```env
# PostgreSQL
DB_NAME=cloud_storage
DB_USER=postgres
DB_PASSWORD=your_db_password

# MinIO
MINIO_URL=http://minio:9000
MINIO_USER=minioadmin
MINIO_PASSWORD=your_minio_password
MINIO_BUCKET=storage

# Redis
REDIS_PASSWORD=your_redis_password
```

> ⚠️ Never commit `.env` to version control.

### Run with Docker Compose

```bash
# Clone the repository
git clone <repository-url>
cd "Cloud file storage"

# Create your .env file (see above)

# Start all services
docker compose up -d

# View logs
docker compose logs -f

# Stop all services
docker compose down
```

Services will be available at:

| Service | URL |
|---------|-----|
| Frontend | http://localhost:80 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| MinIO Console | http://localhost:9001 |

> **First run:** MinIO requires the bucket (`MINIO_BUCKET`) to be created manually. Log into the MinIO console at `:9001` with your credentials and create the bucket.

### Run Backend Locally

```bash
cd backend

# Create a local .env file with your config, then:
./gradlew bootRun
```

Make sure PostgreSQL, MinIO, and Redis are running (either locally or via Docker).

### Run Frontend Locally

```bash
cd frontend
npm install
npm run dev
```

The dev server starts at `http://localhost:5173`. The `VITE_BASE` environment variable can be set to change the base path.

---

## ☁️ Deployment (AWS EC2)

The application is deployed on an **AWS EC2** instance using Docker Compose.

### Quick Deploy Steps

1. SSH into the EC2 instance.
2. Install Docker and Docker Compose.
3. Clone the repository or copy the project files.
4. Create the `.env` file with production credentials.
5. Build and push the Docker images (or pull pre-built ones):
   - Frontend image: `nurbolatdocker/cloud-file-front`
6. Run `docker compose up -d`.

### Security Group / Firewall Ports

Ensure the following inbound rules are open in your EC2 Security Group:

| Port | Protocol | Purpose |
|------|----------|---------|
| 80 | TCP | Frontend (Nginx) |
| 8080 | TCP | Backend API |
| 9001 | TCP | MinIO Console (restrict to your IP) |

> Ports 5432 (PostgreSQL), 9000 (MinIO API), and 6379 (Redis) should **not** be exposed publicly.

### Backend — Run as a Standalone JAR

If deploying the backend without Docker:

```bash
cd backend
./gradlew bootJar
java -jar build/libs/cloud_file_storage-0.0.1-SNAPSHOT.jar
```

---

## 🗄 Database Migrations

Schema migrations are managed with **Liquibase** and run automatically on application startup.

Migration files are located at:
```
backend/src/main/resources/db/changelog/changes/
```

| File | Description |
|------|-------------|
| `2026-04-02-12-25-00-create-basic-tables.xml` | Initial schema — creates `users` table |
| `2026-04-02-14-31-00-change-role-to-roles-in-user-tables.xml` | Rename role column |
| `2026-04-25-17-33-00-change-basic-table-user.xml` | Modify user table structure |

---

## 🧪 Testing

The project uses **JUnit 5** and **Testcontainers** for integration tests (PostgreSQL spun up in Docker).

```bash
cd backend

# Run all tests
./gradlew test

# Run only unit tests
./gradlew test --tests "*.unit.*"

# Run only integration tests
./gradlew test --tests "*.integration.*"
```

Test classes:
- `UserAuthControllerIT` — Integration tests for registration and login endpoints
- `UserMapperTest` — Unit tests for the MapStruct user mapper

---

## 📄 License

This project is for personal/educational use. See individual dependencies for their respective licenses.
