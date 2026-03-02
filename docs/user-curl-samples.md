# UserController API – Sample cURL Requests

Base URL: `http://localhost:8080` (change if your server runs elsewhere.)

---

## 1. Login (public)

**POST** `/api/users/login`  
Returns a JWT token. No auth required.

```bash
curl -X POST "http://localhost:8080/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"your_username","password":"your_password"}'
```

Save the token for authenticated requests:

```bash
export JWT="<paste_token_here>"
```

---

## 2. Registration (public)

**POST** `/api/users/registration`  
Creates a new user (typically role USER, status PENDING). No auth required.

```bash
curl -X POST "http://localhost:8080/api/users/registration" \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"SecurePass123"}'
```

---

## 3. Change password (authenticated)

**PUT** `/api/users/password/change`  
Requires a valid JWT. The username is taken from the token; send only current and new password.

```bash
curl -X PUT "http://localhost:8080/api/users/password/change" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"currentPassword":"old_password","newPassword":"new_password"}'
```

---

## 4. Forgot password – request reset (public)

**PUT** `/api/users/password/forgot`  
Submits a forgot-password request for the given username. User status becomes FORGOT_PASSWORD_PENDING until a SYSADMIN approves. No auth required. System users cannot use this.

```bash
curl -X PUT "http://localhost:8080/api/users/password/forgot" \
  -H "Content-Type: application/json" \
  -d '{"username":"user_who_forgot"}'
```

---

## 5. Forgot password – approve reset (SYSADMIN only)

**PUT** `/api/users/password/forgot/approval/{userId}`  
SYSADMIN sets a new password for a user in FORGOT_PASSWORD_PENDING. Requires SYSADMIN JWT.

```bash
curl -X PUT "http://localhost:8080/api/users/password/forgot/approval/5" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"newPassword":"NewSecurePass456"}'
```

Replace `5` with the target user’s ID.

---

## Summary

| Method | Endpoint | Auth | Body |
|--------|----------|------|------|
| POST | `/api/users/login` | — | username, password |
| POST | `/api/users/registration` | — | username, password |
| PUT | `/api/users/password/change` | JWT (any role) | currentPassword, newPassword |
| PUT | `/api/users/password/forgot` | — | username |
| PUT | `/api/users/password/forgot/approval/{userId}` | JWT (SYSADMIN) | newPassword |

Use `$JWT` from the login response in the `Authorization: Bearer` header for protected endpoints.
