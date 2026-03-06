# Find It API – cURL request samples

**Base URL:** `http://localhost:9090/find-it`

Replace `YOUR_JWT_TOKEN` with the token from login. On Windows (cmd/PowerShell), use `%JWT%` or set `$env:JWT="..."` and use `$env:JWT` in the Authorization header.

---

## 1. Auth – get token

**User login**
```bash
curl -X POST "http://localhost:9090/find-it/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"your_username","password":"your_password"}'
```

**Merchant login**
```bash
curl -X POST "http://localhost:9090/find-it/api/merchants/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"store@example.com","password":"your_password"}'
```

---

## 2. Notifications

**Send notification (saves to DB; optional FCM push if `token` provided)**
```bash
curl -X POST "http://localhost:9090/find-it/api/notifications/send" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "type": "ADMIN",
    "title": "Welcome",
    "body": "Your account is ready.",
    "token": null
  }'
```

**Get unread notifications for user**
```bash
curl -X GET "http://localhost:9090/find-it/api/notifications/unread/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Mark notification as read**
```bash
curl -X POST "http://localhost:9090/find-it/api/notifications/read/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Get notifications by type**
```bash
curl -X GET "http://localhost:9090/find-it/api/notifications/type/1?type=ADMIN" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 3. Merchant / Sub-merchant with outlets

**Get my merchant or sub-merchant with all assigned outlets** (MERCHANT and SUBMERCHANT only; use token from Merchant login)
```bash
curl -X GET "http://localhost:9090/find-it/api/merchants/with-outlets" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Add merchant (admin) – response includes merchant with outlets (empty for new)**
```bash
curl -X POST "http://localhost:9090/find-it/api/admin/merchants" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantName": "New Merchant Ltd",
    "merchantEmail": "new@example.com",
    "merchantAddress": "123 Street",
    "merchantPhoneNumber": "0771234567",
    "merchantType": "GOLD"
  }'
```

**Add sub-merchant (admin) – response includes sub-merchant with outlets (empty for new)**
```bash
curl -X POST "http://localhost:9090/find-it/api/admin/merchants/1/sub-merchants" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantName": "Branch A",
    "merchantEmail": "brancha@example.com",
    "merchantAddress": "50 Branch Rd",
    "merchantPhoneNumber": "0779876543",
    "merchantType": "SILVER"
  }'
```

---

## 4. Customer

**Customer onboarding (public)**
```bash
curl -X POST "http://localhost:9090/find-it/api/customers/onboarding" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane@example.com",
    "phoneNumber": "0771234567",
    "countryName": "Sri Lanka",
    "membershipType": "SILVER",
    "username": "janedoe",
    "password": "Pass123"
  }'
```

**List customers (admin)**
```bash
curl -X GET "http://localhost:9090/find-it/api/customers?search=&status=&membershipType=" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Create customer (admin)**
```bash
curl -X POST "http://localhost:9090/find-it/api/customers" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phoneNumber": "0771234567",
    "countryName": "Sri Lanka",
    "membershipType": "SILVER",
    "status": "ACTIVE"
  }'
```

---

## 5. Outlets

**List outlets**
```bash
curl -X GET "http://localhost:9090/find-it/api/outlets?name=&status=" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Add outlet**
```bash
curl -X POST "http://localhost:9090/find-it/api/outlets" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": 1,
    "subMerchantId": null,
    "outletName": "Main Branch",
    "contactNumber": "0771234567",
    "emailAddress": "branch@example.com",
    "addressLine1": "123 Main St",
    "outletType": "PHYSICAL_STORE",
    "businessCategory": "RESTAURANT"
  }'
```

---

## 6. Items

**Search items**
```bash
curl -X GET "http://localhost:9090/find-it/api/items?search=&categoryId=&outletId=&status=&availability=" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Create item**
```bash
curl -X POST "http://localhost:9090/find-it/api/items" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "itemName": "Sample Product",
    "itemDescription": "Description",
    "categoryId": 1,
    "outletId": 1,
    "price": 99.99,
    "availability": true,
    "status": "ACTIVE"
  }'
```

---

## 7. Payments

**List payments**
```bash
curl -X GET "http://localhost:9090/find-it/api/payments?outletId=&status=" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Approve payment (admin)**
```bash
curl -X PUT "http://localhost:9090/find-it/api/admin/payments/1/approve" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Using a saved token (Bash)

```bash
# After login, save token (example with jq)
export JWT=$(curl -s -X POST "http://localhost:9090/find-it/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"your_password"}' | jq -r '.token')

# Then use in requests (e.g. merchant with outlets – use Merchant login token for MERCHANT/SUBMERCHANT)
curl -X GET "http://localhost:9090/find-it/api/merchants/with-outlets" \
  -H "Authorization: Bearer $JWT"
```
