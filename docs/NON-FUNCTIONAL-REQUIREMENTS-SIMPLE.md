# Find It – Non-Functional Requirements (Simple List)

**System:** Find It Backend API  
**Version:** 1.0  

---

## Performance

- API responses within **3 seconds** under normal load.
- Login within **2 seconds**.
- List/search support **pagination or limits**.
- Use **indexes** on frequently filtered columns (status, outlet_id, user_id).
- Nearest-outlet search within **5 seconds** for typical use.

---

## Security

- **JWT** required for all protected endpoints (except login/register/onboarding).
- JWT **expiry** configurable; expired tokens rejected.
- Passwords stored with **BCrypt** (no plain text).
- **Role-based access** (SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER) enforced.
- Passwords and tokens **not in responses or logs**.
- JWT secret and DB password **configurable** (not hard-coded).
- **CORS** restricted to allowed front-end origins.
- **Input validation** on all user input; clear error on invalid input.
- Use **parameterised queries / ORM** (no SQL injection).

---

## Availability & Reliability

- **Structured error responses** (HTTP status, code, message).
- Non-critical failures (e.g. push) **do not fail** main operation.
- Handle **DB unavailability** without leaving inconsistent state.
- **Fail fast** on critical misconfiguration at startup.

---

## Scalability & Capacity

- API **stateless** (auth in JWT) for horizontal scaling.
- Schema and queries support **growth** (indexes, no N+1).
- **Configurable path** for uploaded files (images, receipts).

---

## API Usability

- **RESTful** (resource URLs, GET/POST/PUT/DELETE, correct status codes).
- **JSON** for request/response.
- **Consistent response format** (status, responseCode, responseMessage).
- **API docs** (e.g. Postman / OpenAPI) available.
- **Clear validation messages** for clients.

---

## Logging & Configuration

- **Log** important events (auth, errors) without sensitive data.
- **Request logging** (method, path) with sensitive data sanitised.
- **Externalised config** (DB, JWT, upload path, CORS) per environment.
- Optional **health/readiness** endpoint for deployment.

---

## Integration & Compatibility

- Support **HTTP** and **HTTPS** (HTTPS in production).
- **CORS** origins/methods configurable.
- Optional **push notifications** (e.g. FCM); push failure does not fail API.
- **MySQL** (or compatible) as database; connection configurable.

---

## Data & Compliance

- **Schema changes** manageable (e.g. migrations / ddl-auto) without data loss.
- **Audit fields** (created_by, modified_by, version) on key entities.
- **Referential integrity** and business rules enforced.
- **Backup/recovery** handled by deployment/infrastructure.

---

## Deployment

- **Port** and **context path** (e.g. /find-it) configurable.
- Single **deployable artifact** (e.g. JAR).
- **Secrets** not in source control; use config or environment.
