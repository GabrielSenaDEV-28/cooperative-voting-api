# Cooperative Voting API

REST API developed with Java and Spring Boot for managing cooperative voting sessions.

The application allows the creation of voting topics, opening voting sessions,
casting YES/NO votes, enforcing one vote per associate per topic, and retrieving
aggregated voting results.

The project also implements the JSON-based mobile communication protocol defined
by the challenge, supporting FORMULARIO and SELECAO screen definitions.

## Technologies

- Java 21
- Spring Boot 4.1
- Spring Web MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Bean Validation
- Lombok
- Maven
- JUnit
- Mockito

## Architecture

The project is organized primarily by business feature, keeping the main voting
concepts separated into `topic`, `session`, and `vote` packages.

```text
src/main/java/com/gabrielsena/cooperative_voting_api
├── config
├── domain
│   ├── exception
│   ├── topic
│   ├── session
│   └── vote
└── presentation
    ├── exception
    └── mobile
```

The `domain` packages contain the business entities, services, repositories,
controllers, and DTOs related to each feature.

The `presentation/mobile` package is responsible for adapting application data
to the JSON screen contract expected by the mobile client.

Database schema evolution is managed by Flyway migrations, while Hibernate
validates the mapped schema at application startup.

## Requirements

To run the application locally, the following are required:

- Java 21
- PostgreSQL
- Git
- No local Maven installation is required, since the project includes Maven Wrapper

## Configuration

The application uses environment variables for database credentials and for the
mobile callback base URL.

| Variable | Required | Description | Default |
| --- | --- | --- | - |
| `DB_URL` | No | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost:5432/cooperative_voting` |
| `DB_USERNAME` | Yes | PostgreSQL username | postgres |
| `DB_PASSWORD` | Yes | PostgreSQL password | your_password |
| `CALLBACK_BASE_URL` | No | Base URL used when generating mobile callback URLs | `http://localhost:8080` |

The callback base URL is configurable so the same application can generate
callbacks for local environments, emulators, physical devices, or a deployed
environment.

Example:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export CALLBACK_BASE_URL=http://localhost:8080
```

## Database Setup

Create a PostgreSQL database named:

```text
cooperative_voting
```

Example using PostgreSQL CLI:

```bash
createdb cooperative_voting
```

There is no need to manually create tables.

Database schema creation and evolution are managed automatically by Flyway when
the application starts.

The current migrations create the structures required for:

- voting topics
- voting sessions
- votes
- database constraints that protect voting integrity

## Running the Application

Clone the repository and enter the project directory:

```bash
git clone <repository-url>
cd cooperative-voting-api
```

Configure the required environment variables:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
```

Optionally configure the callback URL:

```bash
export CALLBACK_BASE_URL=http://localhost:8080
```

Run the application using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

The API will be available locally at:

```text
http://localhost:8080
```

Flyway migrations are executed automatically during application startup.

## Running the Tests

Run the complete automated test suite with:

```bash
./mvnw test
```
The automated test suite currently contains 41 tests covering domain rules,
service behavior, HTTP controllers, request validation, voting session behavior,
duplicate vote protection, database constraint conflict handling, and voting
result calculation.

## API Documentation

All API endpoints are currently versioned under:

```text
/api/v1
```

### Create Voting Topic

Creates a new voting topic.

**Endpoint**

```http
POST /api/v1/topics
```

**Request**

```json
{
  "title": "Should the cooperative approve the new investment?"
}
```

**Response — 201 Created**

```json
{
  "id": "0b6f18b5-94fd-4d55-a307-73855b4a7df1",
  "title": "Should the cooperative approve the new investment?"
}
```

The topic title is required and cannot be blank.

---

### Open Voting Session

Opens a voting session for an existing topic.

**Endpoint**

```http
POST /api/v1/topics/{topicId}/sessions
```

**Request**

```json
{
  "durationMinutes": 5
}
```

`durationMinutes` must be greater than zero.

When `durationMinutes` is omitted, the voting session remains open for **1 minute by default**.

Example:

```json
{}
```

**Response — 201 Created**

```json
{
  "id": "11d43a1c-b44c-4538-8d62-cae869bb4973",
  "topicId": "0b6f18b5-94fd-4d55-a307-73855b4a7df1",
  "openedAt": "2026-09-07T12:00:00Z",
  "closesAt": "2026-09-07T12:05:00Z"
}
```

Only one voting session can be created for each topic.

---

### Cast Vote

Registers an associate's vote on a topic.

**Endpoint**

```http
POST /api/v1/topics/{topicId}/votes
```

**Request**

```json
{
  "associateId": "79d821df-99ca-4dd6-a35e-cc2d0c207c52",
  "choice": "YES"
}
```

`choice` accepts only:

```text
YES
NO
```

Each associate is identified by a UUID and can vote only once per topic.

Votes are accepted only while the voting session is open.

**Response — 201 Created**

```json
{
  "id": "17b936de-bf65-4fc6-a39f-d9f4b9547e34",
  "topicId": "0b6f18b5-94fd-4d55-a307-73855b4a7df1",
  "associateId": "79d821df-99ca-4dd6-a35e-cc2d0c207c52",
  "choice": "YES",
  "votedAt": "2026-09-07T12:02:00Z"
}
```

Duplicate voting is protected both by application validation and by a database
unique constraint on the topic and associate combination.

---

### Get Voting Result

Returns the aggregated voting result for a topic.

**Endpoint**

```http
GET /api/v1/topics/{topicId}/votes/result
```

**Response — 200 OK**

```json
{
  "topicId": "0b6f18b5-94fd-4d55-a307-73855b4a7df1",
  "yesVotes": 3,
  "noVotes": 2,
  "totalVotes": 5
}
```

Vote aggregation is performed directly by the database instead of loading all
votes into application memory.

For an existing topic without votes, all counters are returned as zero.

## Mobile JSON Protocol

The application implements the mobile communication contract using the `FORMULARIO` and `SELECAO` screen types.

Callback URLs are generated using the configurable `CALLBACK_BASE_URL`.

### Voting Topic Form

Returns the screen definition used by the mobile client to create a voting topic.

**Endpoint**

```http
GET /api/v1/topics/form
```

**Example Response**

```json
{
  "tipo": "FORMULARIO",
  "titulo": "Cadastrar pauta",
  "itens": [
    {
      "id": "title",
      "tipo": "INPUT_TEXTO",
      "titulo": "Título da pauta",
      "valor": ""
    }
  ],
  "acoes": [
    {
      "texto": "Cancelar",
      "url": "http://localhost:8080"
    },
    {
      "texto": "Cadastrar",
      "url": "http://localhost:8080/api/v1/topics",
      "body": {}
    }
  ]
}
```

When the user submits the form, the mobile client adds the values entered in the
form to the action body using each item's `id` as the property name.

For example, the `title` input produces:

```json
{
  "title": "Approve the new cooperative investment"
}
```

which is sent to:

```http
POST /api/v1/topics
```

---

### Vote Selection

Returns a selection screen containing the available voting options.

**Endpoint**

```http
GET /api/v1/topics/{topicId}/votes/form?associateId={associateId}
```

**Example Response**

```json
{
  "tipo": "SELECAO",
  "titulo": "Vote na pauta",
  "itens": [
    {
      "texto": "Sim",
      "url": "http://localhost:8080/api/v1/topics/{topicId}/votes",
      "body": {
        "choice": "YES",
        "associateId": "{associateId}"
      }
    },
    {
      "texto": "Não",
      "url": "http://localhost:8080/api/v1/topics/{topicId}/votes",
      "body": {
        "choice": "NO",
        "associateId": "{associateId}"
      }
    }
  ]
}
```

When an option is selected, the mobile client sends the item's `body` to its
corresponding `url` using an HTTP POST request.

The `associateId` is embedded in each option so the vote request contains all
information required by the voting endpoint.

### Voting Result and Mobile Contract

Voting results are exposed through the regular REST endpoint:

```http
GET /api/v1/topics/{topicId}/votes/result
```

The challenge defines `FORMULARIO` and `SELECAO` as mobile screen contracts but
does not define a separate screen type for displaying voting results.

For this reason, the application returns the result as regular JSON instead of
introducing an undocumented mobile screen type.

## Error Handling

The API uses centralized exception handling to provide consistent JSON error
responses.

Example:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Associate has already voted on this topic"
}
```

Validation errors follow the same response structure:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "durationMinutes: must be greater than 0"
}
```

Business exceptions are translated to appropriate HTTP responses by a global
exception handler, keeping HTTP concerns separated from domain rules.

Common error scenarios include:

- `400 Bad Request` — invalid request data
- `404 Not Found` — voting topic not found
- `409 Conflict` — voting session already exists
- `409 Conflict` — voting session does not exist or is closed
- `409 Conflict` — associate has already voted on the topic

## Technical Decisions and Assumptions

### Database migrations

Flyway is responsible for database schema evolution.

Hibernate is configured with `ddl-auto=validate`, so entity mappings are
validated against the existing schema instead of modifying it automatically.

### One voting session per topic

The challenge does not define reopening or creating multiple voting sessions for
the same topic.

The application therefore allows one voting session per topic.

The service performs an application-level existence check to provide a clear
conflict response during normal requests, while a database unique constraint
provides the final integrity guarantee under concurrent requests.

Session persistence uses `saveAndFlush` so database constraint violations are
detected within the service operation and translated into the same domain
conflict response.

### Voting session state

Session status is not persisted as a separate field.

Whether a session is open or closed is derived from its `closesAt` timestamp.
This avoids maintaining duplicated state or requiring a scheduled process to
close sessions.

### Duplicate vote protection

The application checks whether an associate has already voted before accepting a
vote, providing an application-level error response.

A database unique constraint on `(voting_topic_id, associate_id)` provides the
final integrity guarantee, including concurrent requests.

### Time handling

Voting timestamps are represented using `Instant`.

A `Clock` is injected into time-dependent services, making time-based behavior
deterministic and easier to test.

### Voting result aggregation

Vote totals are calculated by PostgreSQL using `COUNT` and `GROUP BY`.

The application receives only the aggregated values instead of loading every vote
into memory.

Vote records remain the source of truth; separate YES/NO counters are not
persisted, avoiding synchronization and concurrency complexity.

### API versioning

All application endpoints are exposed under the `/api/v1` prefix.

This provides an explicit API version boundary while keeping the implementation
simple.

## Logging

The application logs relevant business events using structured, parameterized
logging.

Successful operations such as topic creation, session opening, and vote
registration are logged at `INFO` level.

Expected rejected operations, such as duplicate votes or attempts to vote in a
closed session, are logged at `WARN` level.

Vote choices are intentionally not included in logs.

## Cloud Deployment

The application is deployed on Railway and is publicly available at:

https://cooperative-voting-api-production.up.railway.app

The production environment uses PostgreSQL hosted on Railway. Database
credentials and environment-specific configuration are provided through the
environment variables described in the Configuration section.

The production `CALLBACK_BASE_URL` is:

https://cooperative-voting-api-production.up.railway.app