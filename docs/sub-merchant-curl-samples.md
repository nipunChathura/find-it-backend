# SubMerchantController API – Sample cURL Requests

Base URL: `http://localhost:8080` (change if your server runs elsewhere.)

---

## How the API identifies Merchant vs Admin

The Add sub-merchant API does **not** use a query parameter or header to know if the caller is a Merchant or an Admin. It uses the **JWT** and the **user record in the database**:

1. **JWT**  
   The client sends `Authorization: Bearer <token>`. The token was issued at login and contains the **username** (subject).

2. **Security context**  
   `JwtFilter` validates the token, loads the user (e.g. via `CustomUserDetailsService`), and sets Spring’s `SecurityContext`. The controller reads the authenticated username with `SecurityContextHolder.getContext().getAuthentication().getName()`.

3. **User lookup**  
   The service receives this **username** and loads the **User** entity from the database (via `UserRepository.findByUsername`). The `User` has:
   - **role** (e.g. `MERCHANT`, `SYSADMIN`, `ADMIN`)
   - **merchantId** (set for main merchant users; null for SYSADMIN/ADMIN)
   - **subMerchantId** (set for sub-merchant users; null for main merchant and admin)

4. **Caller type**
   - **Treated as “Merchant” (main merchant):**  
     `role == MERCHANT` **and** `merchantId != null` **and** `subMerchantId == null`  
     → Sub-merchant is created as **ACTIVE**, and the request’s `merchantId` must equal this user’s `merchantId`.
   - **Treated as “Admin” (SYSADMIN or ADMIN):**  
     Any other allowed role (e.g. `SYSADMIN`, `ADMIN`) or missing/invalid user  
     → Sub-merchant is created as **PENDING**; the request’s `merchantId` can be any valid merchant.

So the **same endpoint and same request body** are used for both; the backend decides behaviour from the **JWT → username → User (role, merchantId, subMerchantId)**.

---

## 1. Add sub-merchant (SYSADMIN, ADMIN, or MERCHANT)

**POST** `/api/sub-merchants`  
**merchantId** is **required** in the request body (main merchant ID under which the sub-merchant is added).

- **MERCHANT:** Send your own `merchantId`; it must match the authenticated user’s merchant. Sub-merchant is created as **ACTIVE**.
- **SYSADMIN / ADMIN:** Send `merchantId` for the parent merchant. Sub-merchant is created as **PENDING**.

Get a JWT first (e.g. from `/api/users/login` for admin, or `/api/merchants/login` for merchant), then:

```bash
export JWT="<paste_token_here>"
```

**As MERCHANT** (use your merchant ID; sub-merchant is ACTIVE):

```bash
curl -X POST "http://localhost:8080/api/sub-merchants" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": 1,
    "merchantName": "Branch Manager",
    "merchantEmail": "branch@example.com",
    "merchantNic": "199512345678",
    "merchantProfileImage": null,
    "merchantAddress": "50 Branch Road, Kandy",
    "merchantPhoneNumber": "0772223344",
    "merchantType": "SILVER"
  }'
```

**As SYSADMIN or ADMIN** (specify parent merchant; sub-merchant is PENDING):

```bash
curl -X POST "http://localhost:8080/api/sub-merchants" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": 1,
    "merchantName": "New Sub-Merchant",
    "merchantEmail": "sub@example.com",
    "merchantNic": "199812345678",
    "merchantProfileImage": null,
    "merchantAddress": "75 Sub Street, Galle",
    "merchantPhoneNumber": "0773334455",
    "merchantType": "GOLD"
  }'
```

**merchantType** must be one of: `FREE`, `SILVER`, `GOLD`, `PLATINUM`, `DIAMOND`.  
Phone: Sri Lankan mobile (e.g. `0771234567` or `+94771234567`).

---

## 2. Change password – sub-merchant user (MERCHANT JWT)

**PUT** `/api/sub-merchants/password/change`  
For the authenticated **sub-merchant** user (user with `subMerchantId` set). Use a JWT from **POST /api/merchants/login** as a sub-merchant user.

```bash
curl -X PUT "http://localhost:8080/api/sub-merchants/password/change" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"currentPassword":"old_password","newPassword":"new_password"}'
```

---

## 3. Forgot password – sub-merchant (public)

**PUT** `/api/sub-merchants/password/forgot`  
Request password reset for a **sub-merchant** user (user with `subMerchantId`). Submits for admin approval. No auth required.

```bash
curl -X PUT "http://localhost:8080/api/sub-merchants/password/forgot" \
  -H "Content-Type: application/json" \
  -d '{"username":"sub_merchant_username"}'
```

---

## Summary

| Method | Endpoint | Auth | Body |
|--------|----------|------|------|
| POST | `/api/sub-merchants` | SYSADMIN, ADMIN, or MERCHANT JWT | SubMerchantAddRequest (merchantId required) |
| PUT | `/api/sub-merchants/password/change` | MERCHANT JWT (sub-merchant user) | currentPassword, newPassword |
| PUT | `/api/sub-merchants/password/forgot` | — | username |

- **Add sub-merchant:** `merchantId` is always required. MERCHANT must send their own merchant ID (validated); sub-merchant created ACTIVE. SYSADMIN/ADMIN send any merchant ID; sub-merchant created PENDING.
- **Change password:** Must be logged in as a sub-merchant user (JWT from merchant login with `subMerchantId` in response).
- **Forgot password:** Only for users that have `subMerchantId` set.
