# Find It – Non-Functional Requirements

**Document Title:** Non-Functional Requirements  
**System:** Find It (Backend / API)  
**Version:** 1.0  
**Status:** Draft  

---

## 1. Introduction

### 1.1 Purpose

This document defines the **non-functional requirements** (NFRs) for the Find It system. NFRs describe how the system shall behave in terms of performance, security, availability, and other quality attributes, rather than what functions it provides.

### 1.2 Scope

- **In scope:** Backend API and its runtime behaviour, security, deployability, and operational characteristics.
- **Out of scope:** Front-end (web/mobile) NFRs unless they directly affect or depend on the API.

### 1.3 Reference Documents

- [Core System Functional Requirements](CORE-SYSTEM-FUNCTIONAL-REQUIREMENTS.md)
- [Functionality by Role](FUNCTIONALITY-BY-ROLE.md)

---

## 2. Performance

| ID | Requirement | Description | Priority |
|----|-------------|-------------|----------|
| NFR-PERF-01 | API response time | The system shall respond to typical API requests (e.g. list, get by ID, search) within a target of **3 seconds** under normal load (e.g. single user or low concurrency). | Must |
| NFR-PERF-02 | Login response time | Authentication (login) requests shall complete within **2 seconds** under normal conditions. | Must |
| NFR-PERF-03 | Search and list operations | List and search operations (e.g. outlets, items, merchants) shall support **pagination or reasonable result limits** to avoid excessive response size and time. | Should |
| NFR-PERF-04 | Database usage | The system shall use indexed columns for frequent filters (e.g. status, outlet_id, user_id) to keep query time acceptable as data grows. | Must |
| NFR-PERF-05 | Nearest-outlet search | The “nearest outlets” search (location-based) shall complete within a defined acceptable time (e.g. **5 seconds**) for typical radius and filters. | Should |

---

## 3. Security

| ID | Requirement | Description | Priority |
|----|-------------|-------------|----------|
| NFR-SEC-01 | Authentication | The system shall require **JWT (JSON Web Token)** Bearer authentication for all protected API endpoints except explicitly public ones (e.g. login, registration, onboarding). | Must |
| NFR-SEC-02 | Token validity | JWT tokens shall have a **configurable expiration** (e.g. 12 hours); expired tokens shall be rejected. | Must |
| NFR-SEC-03 | Password storage | User passwords shall be stored using a **strong one-way hashing algorithm** (e.g. BCrypt); plain-text passwords shall not be stored or logged. | Must |
| NFR-SEC-04 | Role-based access | The system shall enforce **role-based access control** (RBAC) so that only authorised roles (SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER) can access the endpoints assigned to them. | Must |
| NFR-SEC-05 | Sensitive data in responses | Passwords, current/new password fields, and tokens shall **not appear in API responses or logs** (e.g. via response DTOs and log sanitisation). | Must |
| NFR-SEC-06 | Secret configuration | JWT secret and other sensitive configuration (e.g. DB password) shall be **configurable via configuration files or environment variables**, not hard-coded in source. | Must |
| NFR-SEC-07 | CORS | The system shall restrict **Cross-Origin Resource Sharing (CORS)** to known front-end origins (e.g. configured allowed origins and methods) to reduce cross-site request risks. | Must |
| NFR-SEC-08 | Input validation | All user-provided input (request body, path, query) shall be **validated** (e.g. format, length, allowed values) before processing; invalid input shall result in a clear error response. | Must |
| NFR-SEC-09 | SQL injection | Data access shall use **parameterised queries / ORM** (e.g. JPA/Hibernate) to prevent SQL injection. | Must |

---

## 4. Availability & Reliability

| ID | Requirement | Description | Priority |
|----|-------------|-------------|----------|
| NFR-AVL-01 | Graceful degradation | In case of non-critical failures (e.g. optional push notification failure), the system shall **not fail the main user operation** (e.g. save feedback, add favorite); errors may be logged and handled gracefully. | Should |
| NFR-AVL-02 | Error responses | The system shall return **consistent, structured error responses** (e.g. HTTP status, error code, message) so clients can handle failures predictably. | Must |
| NFR-AVL-03 | Database connectivity | The system shall handle **database unavailability** (e.g. connection loss) without leaving inconsistent state where feasible; connection pooling and retry behaviour should be configurable. | Should |
| NFR-AVL-04 | Startup checks | The application shall **fail fast** on critical misconfiguration (e.g. missing JWT secret, invalid DB URL) at startup rather than at first use. | Should |

---

## 5. Scalability & Capacity

| ID | Requirement | Description | Priority |
|----|-------------|-------------|----------|
| NFR-SCL-01 | Stateless API | The API shall be **stateless** (authorisation state in JWT); no server-side session storage is required for authentication, to allow horizontal scaling. | Must |
| NFR-SCL-02 | Database growth | Schema and queries shall be designed so that **core operations remain viable** as the number of users, merchants, outlets, and items grows (e.g. indexes, avoiding N+1). | Should |
| NFR-SCL-03 | File storage | Uploaded files (e.g. images) shall be stored in a **configurable path** so that storage can be moved or scaled (e.g. local disk or future cloud storage). | Should |

---

## 6. Usability (API Usability)

| ID | Requirement | Description | Priority |
|----|-------------|-------------|----------|
| NFR-USE-01 | REST conventions | The API shall follow **RESTful conventions** (resource-based URLs, HTTP methods GET/POST/PUT/DELETE, appropriate status codes). | Must |
| NFR-USE-02 | JSON | Request and response bodies shall use **JSON** as the primary data format. | Must |
| NFR-USE-03 | Consistent response structure | Success and error responses shall use a **consistent structure** (e.g. status, responseCode, responseMessage, and payload where applicable). | Must |
| NFR-USE-04 | API documentation | The system shall support or provide **API documentation** (e.g. Postman collection, OpenAPI/Swagger) so that integrators can discover and test endpoints. | Should |
| NFR-USE-05 | Validation messages | Validation failures shall return **clear, actionable messages** (e.g. field-level or summary) to help clients correct requests. | Should |

---

## 7. Maintainability & Operability

| ID | Requirement | Description | Priority |
|----|-------------|-------------|----------|
| NFR-MNT-01 | Logging | The system shall **log** significant events (e.g. authentication attempts, errors, key business actions) with an appropriate level (e.g. INFO, WARN, ERROR) and without logging sensitive data (passwords, tokens). | Must |
| NFR-MNT-02 | Request logging | The system may log **incoming API requests** (e.g. method, path, user) for troubleshooting and audit; request bodies containing passwords or tokens shall be sanitised or excluded. | Should |
| NFR-MNT-03 | Structured logging | Logs shall be in a **consistent, parseable format** (e.g. standard logging framework output) to support log aggregation and analysis. | Could |
| NFR-MNT-04 | Configuration | Environment-specific settings (e.g. database URL, JWT secret, upload path, CORS origins) shall be **externalised** (e.g. application.properties, environment variables) so that the same build can be deployed to different environments. | Must |
| NFR-MNT-05 | Health / readiness | The system may expose **health or readiness endpoints** (e.g. for load balancers or Kubernetes) to indicate that the application is running and able to serve traffic. | Could |

---

## 8. Compatibility & Integration

| ID | Requirement | Description | Priority |
|----|-------------|-------------|----------|
| NFR-INT-01 | HTTP/HTTPS | The system shall support **HTTP** (and **HTTPS** in production) for API communication. | Must |
| NFR-INT-02 | CORS configuration | Allowed **origins, methods, and headers** for CORS shall be configurable to support known front-end applications (e.g. Angular app on a given origin). | Must |
| NFR-INT-03 | Push notifications | The system may integrate with an **external push notification service** (e.g. Firebase FCM) for optional mobile push; failure of push shall not fail the primary API operation. | Should |
| NFR-INT-04 | Database | The system shall support **MySQL** (or compatible) as the primary relational database; connection parameters shall be configurable. | Must |

---

## 9. Data Management & Compliance

| ID | Requirement | Description | Priority |
|----|-------------|-------------|----------|
| NFR-DAT-01 | Schema evolution | Database schema changes shall be **manageable** (e.g. via JPA/Hibernate ddl-auto or migrations) so that existing data is not lost inappropriately during updates. | Must |
| NFR-DAT-02 | Audit fields | Critical business entities shall support **audit fields** (e.g. created_by, created_datetime, modified_by, modified_datetime, version) for traceability and optimistic locking where applicable. | Must |
| NFR-DAT-03 | Data integrity | The system shall enforce **referential integrity** (e.g. foreign keys) and business rules (e.g. unique constraints) so that data remains consistent. | Must |
| NFR-DAT-04 | Backup and recovery | Database **backup and recovery** are the responsibility of the deployment/infrastructure; the application shall not prevent standard backup procedures. | Should |

---

## 10. Deployment & Environment

| ID | Requirement | Description | Priority |
|----|-------------|-------------|----------|
| NFR-DEP-01 | Port and context path | The application **port** and **context path** (e.g. /find-it) shall be configurable so that the service can be deployed behind reverse proxies or on specified ports. | Must |
| NFR-DEP-02 | Build artifact | The system shall be buildable as a **single deployable artifact** (e.g. executable JAR) that includes the application and dependencies. | Must |
| NFR-DEP-03 | External configuration | **Secrets and environment-specific values** shall not be committed to source control; production values shall be supplied via configuration or environment. | Must |

---

## 11. Summary Table

| Category | Must | Should | Could |
|----------|------|--------|-------|
| Performance | 2 | 2 | 0 |
| Security | 8 | 0 | 0 |
| Availability & Reliability | 1 | 3 | 0 |
| Scalability & Capacity | 1 | 2 | 0 |
| Usability (API) | 3 | 2 | 0 |
| Maintainability & Operability | 2 | 2 | 2 |
| Compatibility & Integration | 3 | 1 | 0 |
| Data Management & Compliance | 3 | 1 | 0 |
| Deployment & Environment | 3 | 0 | 0 |

---

## 12. Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | (Draft) | - | Initial non-functional requirements. |
