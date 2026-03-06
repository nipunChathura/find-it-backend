# Find It – Functionality by Role

This document describes the functionality available to **Admin / Sysadmin**, **Customer**, and **Merchant** (including Sub-merchant) in the Find It system. Each section lists what that role can do.

---

## 1. Admin / Sysadmin

**Roles:** `SYSADMIN`, `ADMIN`  
**Note:** User approval and forgot-password approval are **SYSADMIN only**. Merchant/outlet/payment operations are available to both SYSADMIN and ADMIN unless stated otherwise.

### 1.1 User management (SYSADMIN only for add/update/approve/reject)

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| Add user | `POST /api/admin/users` | SYSADMIN |
| Update user | `PUT /api/admin/users/{userId}` | SYSADMIN |
| Update user status | `PUT /api/admin/users/{userId}/status` | SYSADMIN |
| Approve user (pending) | `PUT /api/admin/users/approval/{userId}` | SYSADMIN |
| Reject user | `PUT /api/admin/users/reject/{userId}` | SYSADMIN |
| List all users | `GET /api/admin/users` (optional: status, search) | SYSADMIN, ADMIN |
| Approve forgot-password request | `PUT /api/users/password/forgot/approval/{userId}` | SYSADMIN |

### 1.2 Merchant & sub-merchant management

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| List all merchants (and sub-merchants) | `GET /api/admin/merchants` (search, status, merchantType) | SYSADMIN, ADMIN |
| Onboard merchant (add) | `POST /api/admin/merchants` | SYSADMIN, ADMIN |
| Approve merchant | `PUT /api/admin/merchants/approval/{merchantId}` | SYSADMIN, ADMIN |
| Reject merchant | `PUT /api/admin/merchants/reject/{merchantId}` | SYSADMIN, ADMIN |
| Update merchant | `PUT /api/admin/merchants/{merchantId}` | SYSADMIN, ADMIN |
| Change merchant status (e.g. active/inactive) | `PUT /api/admin/merchants/{merchantId}/status` | SYSADMIN, ADMIN |
| Add sub-merchant under a merchant | `POST /api/admin/merchants/{merchantId}/sub-merchants` | SYSADMIN, ADMIN |
| Approve sub-merchant | `PUT /api/admin/merchants/{merchantId}/sub-merchants/{subMerchantId}/approval` | SYSADMIN, ADMIN |
| Reject sub-merchant | `PUT /api/admin/merchants/{merchantId}/sub-merchants/{subMerchantId}/reject` | SYSADMIN, ADMIN |

### 1.3 Outlet management

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| List all outlets | `GET /api/admin/outlets` (search, status, outletType) | SYSADMIN, ADMIN |
| Verify outlet payment | `PUT /api/admin/outlets/{outletId}/verify-payment` | SYSADMIN, ADMIN |
| Update outlet status | `PUT /api/admin/outlets/{outletId}/status` | SYSADMIN, ADMIN |

### 1.4 Payment management

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| Approve payment | `PUT /api/admin/payments/{paymentId}/approve` | SYSADMIN, ADMIN |

### 1.5 Customer management

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| Create customer | `POST /api/customers` | SYSADMIN, ADMIN |
| Get customer by ID | `GET /api/customers/{customerId}` | SYSADMIN, ADMIN |
| List customers | `GET /api/customers` (search, status, membershipType) | SYSADMIN, ADMIN |
| Get customer search history | `GET /api/customers/{customerId}/search-history` | SYSADMIN, ADMIN |
| Update customer | `PUT /api/customers/{customerId}` | SYSADMIN, ADMIN |
| Delete customer | `DELETE /api/customers/{customerId}` | SYSADMIN, ADMIN |

### 1.6 Dashboard (analytics)

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| Dashboard summary (KPIs) | `GET /api/dashboard/summary` | SYSADMIN, ADMIN |
| Merchant summary | `GET /api/dashboard/merchant-summary` | SYSADMIN, ADMIN |
| Activity (e.g. last N months) | `GET /api/dashboard/activity?months=6` | SYSADMIN, ADMIN |
| Monthly income | `GET /api/dashboard/monthly-income?months=12` | SYSADMIN, ADMIN |

### 1.7 Shared with merchant (read/write outlets, items, etc.)

Admin can also use the same outlet, item, category, discount, payment, province, district, notification and image APIs as merchants (see Merchant section). For example: list/update outlets, manage items, categories, discounts, payments, and view outlet feedbacks/discounts.

---

## 2. Customer

**Role:** `CUSTOMER`  
**Access:** All customer-specific APIs are under `/api/customer-app/**` and require customer login (JWT).

### 2.1 Authentication & profile

| Functionality | Endpoint / Action | Public? |
|---------------|-------------------|--------|
| Customer login | `POST /api/customer-app/login` (email, password) | Yes |
| Change profile image | `PUT /api/customer-app/profile/image` | No (customer only) |
| Change password | `PUT /api/customer-app/password` | No (customer only) |

**Note:** Customer registration (onboarding) is public: `POST /api/customers/onboarding`. Customer login is also available at `POST /api/customers/login`.

### 2.2 Search & discover

| Functionality | Endpoint / Action |
|---------------|-------------------|
| Search nearest outlets | `POST /api/customer-app/outlets/nearest` (location, max distance, optional category/outlet type). Returns open outlets with matching items; includes favorite info. |
| Search items | `GET /api/customer-app/items/search` (search, categoryId, outletId, status, availability) |

### 2.3 Search history

| Functionality | Endpoint / Action |
|---------------|-------------------|
| Create search history | `POST /api/customer-app/search-history` |
| List my search history | `GET /api/customer-app/search-history` |
| Get one search history | `GET /api/customer-app/search-history/{id}` |
| Update search history | `PUT /api/customer-app/search-history/{id}` |
| Delete search history | `DELETE /api/customer-app/search-history/{id}` |

### 2.4 Favorites (outlets)

| Functionality | Endpoint / Action |
|---------------|-------------------|
| Add outlet to favorites | `POST /api/customer-app/favorites` (outletId, optional nickname) |
| List my favorites | `GET /api/customer-app/favorites` |
| Get one favorite | `GET /api/customer-app/favorites/{id}` |
| Update favorite (e.g. nickname) | `PUT /api/customer-app/favorites/{id}` |
| Remove favorite | `DELETE /api/customer-app/favorites/{id}` |

### 2.5 Feedback

| Functionality | Endpoint / Action |
|---------------|-------------------|
| Submit feedback for an outlet | `POST /api/customer-app/feedback` (outletId, feedbackText, rating) |
| List my feedbacks | `GET /api/customer-app/feedback` |

### 2.6 Notifications & images

| Functionality | Endpoint / Action |
|---------------|-------------------|
| Notifications (list, get, mark read, etc.) | `GET/PUT /api/notifications/**` |
| Image upload (e.g. profile) | `POST /api/images/upload?type=profile` |

### 2.7 Read-only access (shared with other roles)

Customers can **only read** (no create/update/delete) on:

- Categories: `GET /api/categories`, `GET /api/categories/{id}`
- Outlet discounts: `GET /api/outlets/{outletId}/discounts`
- Outlet feedbacks: `GET /api/outlets/{outletId}/feedbacks`, `GET /api/outlets/{outletId}/feedbacks/count`
- Items: `GET /api/items/**` (search/list)
- Discounts by outlet: `GET /api/discounts/outlet/{outletId}`

---

## 3. Merchant (and Sub-merchant)

**Roles:** `MERCHANT`, `SUBMERCHANT`  
Merchants own outlets and sub-merchants; sub-merchants own their own outlets. Many APIs are shared; some actions (e.g. approve outlet, manage sub-merchants) are **MERCHANT only**.

### 3.1 Authentication & profile

| Functionality | Endpoint / Action | Role | Public? |
|---------------|-------------------|------|--------|
| Merchant login | `POST /api/merchants/login` (username, password) | MERCHANT, SUBMERCHANT | Yes |
| Merchant onboarding (register) | `POST /api/merchants/onboarding` | - | Yes |
| Get my merchant/sub-merchant with outlets | `GET /api/merchants/with-outlets` | MERCHANT, SUBMERCHANT | No |
| Update my merchant profile | `PUT /api/merchants/profile` | MERCHANT only | No |

### 3.2 Sub-merchant management (MERCHANT only)

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| Add sub-merchant | `POST /api/sub-merchants` (linked to authenticated merchant) | MERCHANT |
| Approve sub-merchant | `PUT /api/merchants/sub-merchants/{subMerchantId}/approve` | MERCHANT |
| Reject sub-merchant | `PUT /api/merchants/sub-merchants/{subMerchantId}/reject` | MERCHANT |
| Update sub-merchant status | `PUT /api/merchants/sub-merchants/{subMerchantId}/status` | MERCHANT |

**Note:** SYSADMIN/ADMIN can also add sub-merchants via `POST /api/admin/merchants/{merchantId}/sub-merchants`.

### 3.3 Outlet management

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| List outlets (own only for MERCHANT/SUBMERCHANT) | `GET /api/outlets` (name, status) | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Add outlet | `POST /api/outlets` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Approve outlet (after onboarding) | `PUT /api/outlets/{outletId}/approve` | MERCHANT only |
| Submit payment for outlet | `PUT /api/outlets/{outletId}/submit-payment` | MERCHANT, SUBMERCHANT |
| Update outlet | `PUT /api/outlets/{outletId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Get outlet by ID | `GET /api/outlets/{outletId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Get outlet status (OPEN/CLOSED) | `GET /api/outlets/{outletId}/status` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Get outlet schedules | `GET /api/outlets/{outletId}/schedules` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Create/update schedules (opening hours) | `POST /api/outlets/{outletId}/schedules`, `PUT /api/outlets/{outletId}/schedules/{scheduleId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Delete schedule | `DELETE /api/outlets/{outletId}/schedules/{scheduleId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Get outlet discounts | `GET /api/outlets/{outletId}/discounts` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER |
| Get outlet feedbacks / count | `GET /api/outlets/{outletId}/feedbacks`, `GET /api/outlets/{outletId}/feedbacks/count` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER |

### 3.4 Items

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| Create item | `POST /api/items` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Update item | `PUT /api/items/{itemId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Get item | `GET /api/items/{itemId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| List/search items | `GET /api/items` (search, categoryId, outletId, status, availability) | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Delete item | `DELETE /api/items/{itemId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |

### 3.5 Categories

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| Create category | `POST /api/categories` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Update category | `PUT /api/categories/{categoryId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Get category | `GET /api/categories/{categoryId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER |
| List categories | `GET /api/categories` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER |
| Delete category | `DELETE /api/categories/{categoryId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |

### 3.6 Discounts

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| Create discount | `POST /api/discounts` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Get discounts by outlet | `GET /api/discounts/outlet/{outletId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER |
| Get discount by ID | `GET /api/discounts/{discountId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Update discount | `PUT /api/discounts/{discountId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Delete discount | `DELETE /api/discounts/{discountId}` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |

### 3.7 Payments

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| Create/list/update payments for outlets | `POST /api/payments`, `GET /api/payments`, etc. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |

### 3.8 Location & other

| Functionality | Endpoint / Action | Role |
|---------------|-------------------|------|
| Provinces | `GET /api/provinces/**` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Districts | `GET /api/districts/**` | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT |
| Notifications | `GET/PUT /api/notifications/**` | All authenticated |
| Image upload | `POST /api/images/upload` | All authenticated |

---

## Summary

| Role | Main functions |
|------|----------------|
| **SYSADMIN** | Full user lifecycle (add, update, approve, reject); forgot-password approval; everything ADMIN can do. |
| **ADMIN** | Merchant/sub-merchant/outlet/customer management; dashboard; approve/reject merchants and sub-merchants; verify payments; no user add/approve/reject. |
| **CUSTOMER** | Login; profile & password; search nearest outlets & items; search history; favorites; feedback; read-only on categories, outlet discounts, outlet feedbacks, items. |
| **MERCHANT** | Login; profile; own outlets & sub-merchants; approve/reject sub-merchants and outlets; items, categories, discounts, payments; provinces/districts; notifications. |
| **SUBMERCHANT** | Same as merchant for **own** outlets only (no sub-merchant management, no outlet approve; can submit payment). |
