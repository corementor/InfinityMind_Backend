# InfinityMind Backend

REST API backend for **MindExpanse** — a math learning platform that generates arithmetic exercises and evaluates user answers.

## Tech Stack

- Java 17, Spring Boot 3.4.0
- Spring Security + JWT (JJWT 0.12.x)
- Spring Data JPA + PostgreSQL
- Spring Mail (Gmail SMTP)
- Lombok, Log4j2 / SLF4J

## Project Structure

```
src/main/java/io/corementor/infinitymind/
├── config/         # Security & CORS configuration
├── controller/     # REST controllers (Auth, Math)
├── dto/            # Request/Response DTOs
├── exception/      # Custom exceptions
├── filter/         # JWT auth filter
├── model/          # JPA entity (User)
├── repository/     # Spring Data JPA repository
└── service/        # Business logic (Auth, JWT, Email, Math)
```

## API Endpoints

### Authentication — `/api/v1/auth` (public)

| Method | Path              | Description                        |
|--------|-------------------|------------------------------------|
| POST   | `/signup`         | Register a new user                |
| POST   | `/login`          | Authenticate and receive tokens    |
| POST   | `/refresh`        | Refresh access token               |
| POST   | `/validate-token` | Validate a Bearer token            |

### Math — `/api/v1/math` (requires JWT)

| Method | Path                       | Description                            |
|--------|----------------------------|----------------------------------------|
| GET    | `/generateArray`           | Generate a 2D array of random numbers  |
| POST   | `/verify-additions`        | Verify addition answers with carries   |
| POST   | `/verify-subtractions`     | Verify subtraction answers with borrows|
| POST   | `/verify-multiplications`  | Verify multiplication answers          |
| POST   | `/verify-division`         | Verify division answers                |
| POST   | `/division/questions`      | Generate division questions            |
| POST   | `/division/verify`         | Verify division answers (detailed)     |

## Security

- Stateless JWT authentication (access + refresh tokens)
- Access token expiry: `86400000 ms` (24h)
- Refresh token expiry: `604800000 ms` (7 days)
- BCrypt password encoding
- CORS allowed origins: `https://mindexpanse.corementor.io`, `http://localhost:5173`

## Environment Variables

| Variable               | Description                     |
|------------------------|---------------------------------|
| `PORT_NUMBER`          | Server port                     |
| `JDBC_DATABASE_URL`    | PostgreSQL JDBC base URL        |
| `JDBC_DATABASE_NAME`   | Database name                   |
| `JDBC_DATABASE_USERNAME` | Database username             |
| `JDBC_DATABASE_PASSWORD` | Database password             |
| `SECRET_KEY`           | JWT signing secret              |

## Running Locally

**Prerequisites:** Java 17, Maven, PostgreSQL

```bash
# Set environment variables, then:
./mvnw spring-boot:run
```

## Docker

```bash
docker build -t infinitymind-backend .
docker run -p 8080:8080 \
  -e PORT_NUMBER=8080 \
  -e JDBC_DATABASE_URL=jdbc:postgresql://host:5432/ \
  -e JDBC_DATABASE_NAME=mind_expanse_db \
  -e JDBC_DATABASE_USERNAME=postgres \
  -e JDBC_DATABASE_PASSWORD=secret \
  -e SECRET_KEY=your_jwt_secret \
  infinitymind-backend
```

## Running Tests

```bash
./mvnw test
```
