# MerchantController API – Sample cURL Requests

Base URL: `http://localhost:8080` (change if your server runs elsewhere.)

Endpoints that require a merchant JWT need a user with role **MERCHANT** (main merchant). Use **POST /api/merchants/login** to get a token.

---

## 1. Merchant / sub-merchant login (public)

**POST** `/api/merchants/login`  
Login for merchant or sub-merchant users. Returns JWT and `merchantId` / `subMerchantId`. No auth required.

```bash
curl -X POST "http://localhost:8080/api/merchants/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"merchant_username","password":"your_password"}'
```

Save the token for protected endpoints:

```bash
export JWT="<paste_token_here>"
```

---

## 2. Merchant onboarding (public)

**POST** `/api/merchants/onboarding`  
Register a new merchant (status PENDING until admin approves). No auth required.

```bash
curl -X POST "http://localhost:8080/api/merchants/onboarding" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantName": "My Store Ltd",
    "merchantEmail": "store@example.com",
    "merchantNic": "199012345678",
    "merchantProfileImage": null,
    "merchantAddress": "100 Main Road, Colombo",
    "merchantPhoneNumber": "0771234567",
    "merchantType": "SILVER"
  }'
```

**merchantType** must be one of: `FREE`, `SILVER`, `GOLD`, `PLATINUM`, `DIAMOND`.  
Phone: Sri Lankan mobile (e.g. `0771234567` or `+94771234567`).

---

## 3. Update merchant profile (MERCHANT JWT)

**PUT** `/api/merchants/profile`  
Update the authenticated merchant’s profile. Requires main merchant JWT.

```bash
curl -X PUT "http://localhost:8080/api/merchants/profile" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantName": "My Store Ltd (Updated)",
    "merchantEmail": "updated@example.com",
    "merchantNic": "199012345678",
    "merchantProfileImage": null,
    "merchantAddress": "200 New Street, Colombo",
    "merchantPhoneNumber": "0779876543",
    "merchantType": "GOLD"
  }'
```

---

## 4. Approve sub-merchant (MERCHANT JWT)

**PUT** `/api/merchants/sub-merchants/{subMerchantId}/approve`  
Approve a pending sub-merchant that belongs to your merchant. No request body.

```bash
curl -X PUT "http://localhost:8080/api/merchants/sub-merchants/1/approve" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json"
```

Replace `1` with the sub-merchant ID.

---

## 5. Update sub-merchant status (MERCHANT JWT)

**PUT** `/api/merchants/sub-merchants/{subMerchantId}/status`  
Change status of a sub-merchant that belongs to your merchant. **status** is required; **inactiveReason** is optional (use when status is INACTIVE).

```bash
# Set to ACTIVE
curl -X PUT "http://localhost:8080/api/merchants/sub-merchants/1/status" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"status":"ACTIVE"}'

# Set to INACTIVE with reason
curl -X PUT "http://localhost:8080/api/merchants/sub-merchants/1/status" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"status":"INACTIVE","inactiveReason":"Contract ended"}'

# Set to PENDING
curl -X PUT "http://localhost:8080/api/merchants/sub-merchants/1/status" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"status":"PENDING"}'
```

**status** must be one of: `ACTIVE`, `INACTIVE`, `PENDING`.

---

## 6. Change password – merchant (MERCHANT JWT)

**PUT** `/api/merchants/password/change`  
Change password for the authenticated main merchant user.

```bash
curl -X PUT "http://localhost:8080/api/merchants/password/change" \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"currentPassword":"old_password","newPassword":"new_password"}'
```

---

## 7. Forgot password – merchant (public)

**PUT** `/api/merchants/password/forgot`  
Request password reset for a main merchant user. Submits for admin approval. No auth required.

```bash
curl -X PUT "http://localhost:8080/api/merchants/password/forgot" \
  -H "Content-Type: application/json" \
  -d '{"username":"merchant_username"}'
```

---

## Summary

| Method | Endpoint | Auth | Body |
|--------|----------|------|------|
| POST | `/api/merchants/login` | — | username, password |
| POST | `/api/merchants/onboarding` | — | MerchantRequest (JSON) |
| PUT | `/api/merchants/profile` | MERCHANT JWT | MerchantRequest (JSON) |
| PUT | `/api/merchants/sub-merchants/{subMerchantId}/approve` | MERCHANT JWT | — |
| PUT | `/api/merchants/sub-merchants/{subMerchantId}/status` | MERCHANT JWT | status, optional inactiveReason |
| PUT | `/api/merchants/password/change` | MERCHANT JWT | currentPassword, newPassword |
| PUT | `/api/merchants/password/forgot` | — | username |

Use the token from **POST /api/merchants/login** in `Authorization: Bearer <token>` for endpoints that require MERCHANT JWT.
