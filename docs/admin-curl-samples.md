# AdminController API – Sample cURL Requests

Base URL: `http://localhost:8080` (change if your server runs elsewhere.)

All admin endpoints require a JWT. Use a SYSADMIN or ADMIN user to get a token, then pass it in the `Authorization` header.

---

## 1. Get JWT (login as admin)

Login first to get a token. Use a user with role **SYSADMIN** or **ADMIN**.

```bash
curl -X POST "http://localhost:8080/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"your_admin_username","password":"your_password"}'
```

Copy the `token` from the response and set it for the next requests:

```bash
export JWT="<paste_token_here>"
```

---

## 2. Approve merchant (SYSADMIN or ADMIN)

**PUT** `/api/admin/merchants/approval/{merchantId}`  
No request body.

```bash
curl -X PUT "http://localhost:8080/api/admin/merchants/approval/1" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json"
```

---

## 3. Update merchant (SYSADMIN or ADMIN)

**PUT** `/api/admin/merchants/{merchantId}`  
Body: merchant profile fields.

```bash
curl -X PUT "http://localhost:8080/api/admin/merchants/1" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantName": "Updated Merchant Ltd",
    "merchantEmail": "updated@merchant.com",
    "merchantNic": "199012345678",
    "merchantProfileImage": null,
    "merchantAddress": "123 New Street, Colombo",
    "merchantPhoneNumber": "0771234567",
    "merchantType": "GOLD"
  }'
```

**merchantType** must be one of: `FREE`, `SILVER`, `GOLD`, `PLATINUM`, `DIAMOND`.  
Phone: Sri Lankan mobile (e.g. `0771234567` or `+94771234567`).

---

## 4. Change merchant status (SYSADMIN or ADMIN)

**PUT** `/api/admin/merchants/{merchantId}/status`  
Body: `status` (required), `inactiveReason` (optional, use when status is INACTIVE).

```bash
# Set to ACTIVE
curl -X PUT "http://localhost:8080/api/admin/merchants/1/status" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"status":"ACTIVE"}'

# Set to INACTIVE with reason
curl -X PUT "http://localhost:8080/api/admin/merchants/1/status" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"status":"INACTIVE","inactiveReason":"Contract terminated"}'

# Set to PENDING
curl -X PUT "http://localhost:8080/api/admin/merchants/1/status" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"status":"PENDING"}'
```

**status** must be one of: `ACTIVE`, `INACTIVE`, `PENDING`.

---

## 5. Approve user (SYSADMIN only)

**PUT** `/api/admin/users/approval/{userId}`  
No request body. Only users with role **SYSADMIN** can call this.

```bash
curl -X PUT "http://localhost:8080/api/admin/users/approval/5" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json"
```

---

## Summary

| Method | Endpoint | Role | Body |
|--------|----------|------|------|
| PUT | `/api/admin/merchants/approval/{merchantId}` | SYSADMIN, ADMIN | — |
| PUT | `/api/admin/merchants/{merchantId}` | SYSADMIN, ADMIN | MerchantRequest (JSON) |
| PUT | `/api/admin/merchants/{merchantId}/status` | SYSADMIN, ADMIN | status, optional inactiveReason |
| PUT | `/api/admin/users/approval/{userId}` | SYSADMIN | — |

Replace `{merchantId}` and `{userId}` with actual IDs. Use the token from step 1 in `Authorization: Bearer <token>`.
