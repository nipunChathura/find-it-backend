# Find It – Core System Functional Requirements

**Document Title:** Core System Functional Requirements  
**System:** Find It (Backend / API)  
**Version:** 1.0  
**Status:** Draft  

---

## 1. Introduction

### 1.1 Purpose

This document defines the **core functional requirements** of the Find It system. It is intended for stakeholders, developers, and testers to understand what the system shall do from a user and business perspective.

### 1.2 Scope

- **In scope:** Backend API behaviour that supports Admin/Sysadmin, Customer, and Merchant (including Sub-merchant) roles. Requirements are expressed as capabilities the system must provide.
- **Out of scope:** Non-functional requirements (performance, security standards, infrastructure), UI/UX details, and third-party integration specifications are not covered in this document.

### 1.3 Definitions

| Term | Definition |
|------|------------|
| **Admin** | User with role ADMIN; can manage merchants, outlets, customers, payments, and view dashboard; cannot manage users. |
| **SYSADMIN** | User with role SYSADMIN; full user lifecycle (add, update, approve, reject) and forgot-password approval; all Admin capabilities. |
| **Merchant** | Business entity that can own outlets and sub-merchants; has a linked user with role MERCHANT. |
| **Sub-merchant** | Business entity under a merchant; owns its own outlets; has a linked user with role SUBMERCHANT. |
| **Outlet** | A physical or logical store/branch belonging to a merchant or sub-merchant. |
| **Customer** | End-user who searches for outlets/items, manages favorites and feedback; has a linked user with role CUSTOMER. |

### 1.4 Reference Documents

- [Functionality by Role](FUNCTIONALITY-BY-ROLE.md)
- [ER Diagram](ER-DIAGRAM.md)
- Use case diagrams in `docs/diagrams/`

---

## 2. System Actors

| Actor | Description |
|-------|-------------|
| **SYSADMIN** | System administrator; manages users (add, update, approve, reject), approves forgot-password requests. |
| **ADMIN** | Administrator; manages merchants, sub-merchants, outlets, customers, payments; views dashboard; does not manage users. |
| **MERCHANT** | Main merchant; manages own profile, sub-merchants, outlets (including approval), items, categories, discounts, payments. |
| **SUBMERCHANT** | Sub-merchant; manages own outlets (no outlet approval), items, categories, discounts, payments; no sub-merchant management. |
| **CUSTOMER** | End customer; searches outlets/items, manages favorites and search history, submits feedback, manages profile. |

---

## 3. Core Functional Requirements

Requirements are grouped by functional area. Each requirement has a unique ID (FR-xxx), title, description, primary actor(s), and priority (M = Must have, S = Should have, C = Could have).

---

### 3.1 Authentication & Identity

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-AUTH-01 | User login (admin/merchant) | The system shall allow a user with username and password to log in and receive a JWT for admin or merchant/sub-merchant access. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-AUTH-02 | Customer login | The system shall allow a customer to log in with email and password and receive a JWT for customer-app access. | CUSTOMER | M |
| FR-AUTH-03 | Customer registration (onboarding) | The system shall allow a new customer to register (onboarding) without authentication. | CUSTOMER (unauthenticated) | M |
| FR-AUTH-04 | Merchant onboarding | The system shall allow a new merchant or sub-merchant to register (onboarding) without authentication; account remains pending until approved. | MERCHANT, SUBMERCHANT (unauthenticated) | M |
| FR-AUTH-05 | Change password (authenticated) | The system shall allow an authenticated user to change their own password by providing current and new password. | All authenticated | M |
| FR-AUTH-06 | Forgot password request | The system shall allow a user to request a password reset by username; request is stored for admin approval. | All (unauthenticated for request) | S |
| FR-AUTH-07 | Approve forgot-password request | The system shall allow SYSADMIN to approve a forgot-password request for a user. | SYSADMIN | M |

---

### 3.2 User Management (Admin)

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-USER-01 | Add user | The system shall allow SYSADMIN to create a new user (username, email, password, role, optional merchant/sub-merchant/customer link, status). | SYSADMIN | M |
| FR-USER-02 | Update user | The system shall allow SYSADMIN to update an existing user’s profile (username, email, role, links, status). | SYSADMIN | M |
| FR-USER-03 | Update user status | The system shall allow SYSADMIN to change a user’s status (e.g. active/inactive). | SYSADMIN | M |
| FR-USER-04 | Approve user | The system shall allow SYSADMIN to approve a user in pending status. | SYSADMIN | M |
| FR-USER-05 | Reject user | The system shall allow SYSADMIN to reject a user in pending status, optionally with a reason. | SYSADMIN | M |
| FR-USER-06 | List users | The system shall allow SYSADMIN and ADMIN to list all users with optional filters (status, search). | SYSADMIN, ADMIN | M |

---

### 3.3 Merchant & Sub-merchant Management

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-MERCH-01 | List merchants | The system shall allow Admin to list all merchants and sub-merchants with optional filters (search, status, merchant type). | SYSADMIN, ADMIN | M |
| FR-MERCH-02 | Onboard merchant (admin) | The system shall allow Admin to onboard a new merchant (create merchant record and optionally outlets). | SYSADMIN, ADMIN | M |
| FR-MERCH-03 | Approve merchant | The system shall allow Admin to approve a merchant in pending status. | SYSADMIN, ADMIN | M |
| FR-MERCH-04 | Reject merchant | The system shall allow Admin to reject a merchant in pending status, optionally with a reason. | SYSADMIN, ADMIN | M |
| FR-MERCH-05 | Update merchant | The system shall allow Admin to update a merchant’s profile (name, email, address, phone, type, etc.). | SYSADMIN, ADMIN | M |
| FR-MERCH-06 | Change merchant status | The system shall allow Admin to set a merchant’s status (e.g. active, inactive) and optional inactive reason. | SYSADMIN, ADMIN | M |
| FR-MERCH-07 | Add sub-merchant (admin) | The system shall allow Admin to add a sub-merchant under a given merchant. | SYSADMIN, ADMIN | M |
| FR-MERCH-08 | Approve sub-merchant (admin) | The system shall allow Admin to approve a sub-merchant in pending status. | SYSADMIN, ADMIN | M |
| FR-MERCH-09 | Reject sub-merchant (admin) | The system shall allow Admin to reject a sub-merchant, optionally with a reason. | SYSADMIN, ADMIN | M |
| FR-MERCH-10 | Merchant update own profile | The system shall allow a logged-in MERCHANT to update their own merchant profile. | MERCHANT | M |
| FR-MERCH-11 | Add sub-merchant (merchant) | The system shall allow a MERCHANT to add a sub-merchant under their own merchant account. | MERCHANT | M |
| FR-MERCH-12 | Approve sub-merchant (merchant) | The system shall allow a MERCHANT to approve a pending sub-merchant under their account. | MERCHANT | M |
| FR-MERCH-13 | Reject sub-merchant (merchant) | The system shall allow a MERCHANT to reject a sub-merchant under their account, optionally with a reason. | MERCHANT | M |
| FR-MERCH-14 | Update sub-merchant status (merchant) | The system shall allow a MERCHANT to update a sub-merchant’s status (e.g. active/inactive) and optional reason. | MERCHANT | M |
| FR-MERCH-15 | Get my merchant/sub-merchant with outlets | The system shall allow a logged-in MERCHANT or SUBMERCHANT to retrieve their own merchant or sub-merchant profile including linked outlets. | MERCHANT, SUBMERCHANT | M |

---

### 3.4 Outlet Management

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-OUT-01 | List outlets | The system shall allow Admin to list all outlets with optional filters (search, status, outlet type). Merchant/Sub-merchant shall see only their own outlets. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-OUT-02 | Add outlet | The system shall allow Admin, MERCHANT, or SUBMERCHANT to create an outlet (linked to merchant or sub-merchant) with address, location, business details, and optional bank info. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-OUT-03 | Update outlet | The system shall allow Admin or the owning Merchant/Sub-merchant to update an outlet’s details. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-OUT-04 | Approve outlet | The system shall allow the owning MERCHANT to approve an outlet (e.g. after onboarding). | MERCHANT | M |
| FR-OUT-05 | Submit outlet payment | The system shall allow the owning MERCHANT or SUBMERCHANT to submit payment for an outlet (e.g. subscription); outlet may remain pending until admin verifies. | MERCHANT, SUBMERCHANT | M |
| FR-OUT-06 | Verify outlet payment | The system shall allow Admin to verify an outlet’s payment (e.g. confirm subscription and update validity). | SYSADMIN, ADMIN | M |
| FR-OUT-07 | Update outlet status | The system shall allow Admin to update an outlet’s status (e.g. active/inactive). | SYSADMIN, ADMIN | M |
| FR-OUT-08 | Get outlet details | The system shall allow authorised roles to retrieve a single outlet by ID (with current open/closed status where applicable). | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER (read for relevant endpoints) | M |
| FR-OUT-09 | Manage outlet schedules | The system shall allow Admin or the outlet owner to create, update, and delete opening-hour schedules for an outlet (normal weekly, temporary date range, emergency/daily override). | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-OUT-10 | Get outlet schedules | The system shall allow authorised roles to retrieve an outlet’s schedules. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-OUT-11 | Get outlet status (open/closed) | The system shall provide the current operational status (e.g. OPEN/CLOSED) for an outlet based on schedules and optional holiday logic. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER | M |

---

### 3.5 Categories, Items & Discounts

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-CAT-01 | Create category | The system shall allow Admin, MERCHANT, or SUBMERCHANT to create a category (name, description, type, image, status). | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-CAT-02 | Update category | The system shall allow Admin, MERCHANT, or SUBMERCHANT to update a category. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-CAT-03 | List/get categories | The system shall allow Admin, Merchant, Sub-merchant, and Customer to list and get categories (read-only for Customer). | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER | M |
| FR-CAT-04 | Delete category | The system shall allow Admin, MERCHANT, or SUBMERCHANT to delete a category (subject to business rules). | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | S |
| FR-ITEM-01 | Create item | The system shall allow Admin or outlet owner (Merchant/Sub-merchant) to create an item linked to a category and outlet (name, description, price, availability, image, status). | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-ITEM-02 | Update item | The system shall allow Admin or outlet owner to update an item. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-ITEM-03 | Get/list items | The system shall allow Admin, Merchant, Sub-merchant to get or list items with filters; Customer shall be able to search/list items (read-only). | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER | M |
| FR-ITEM-04 | Delete item | The system shall allow Admin or outlet owner to delete an item. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | S |
| FR-DISC-01 | Create discount | The system shall allow Admin or Merchant/Sub-merchant to create a discount (name, type, value, dates, status) and link items via discount_items. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-DISC-02 | Update discount | The system shall allow Admin or Merchant/Sub-merchant to update a discount. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-DISC-03 | Get discounts by outlet | The system shall allow Admin, Merchant, Sub-merchant, and Customer to retrieve active discounts for an outlet (read-only for Customer). | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER | M |
| FR-DISC-04 | Delete discount | The system shall allow Admin or Merchant/Sub-merchant to delete a discount. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | S |

---

### 3.6 Customer Management

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-CUST-01 | Create customer (admin) | The system shall allow Admin to create a customer (profile, contact, membership type, status). | SYSADMIN, ADMIN | M |
| FR-CUST-02 | Get customer | The system shall allow Admin to retrieve a customer by ID. | SYSADMIN, ADMIN | M |
| FR-CUST-03 | List customers | The system shall allow Admin to list customers with optional filters (search, status, membership type). | SYSADMIN, ADMIN | M |
| FR-CUST-04 | Update customer | The system shall allow Admin to update a customer’s profile. | SYSADMIN, ADMIN | M |
| FR-CUST-05 | Delete customer | The system shall allow Admin to delete a customer (subject to business rules). | SYSADMIN, ADMIN | S |
| FR-CUST-06 | Get customer search history (admin) | The system shall allow Admin to retrieve search history for a given customer. | SYSADMIN, ADMIN | M |
| FR-CUST-07 | Update customer profile (self) | The system shall allow a Customer to update their own profile image and password. | CUSTOMER | M |

---

### 3.7 Search & Discovery (Customer)

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-SRCH-01 | Search nearest outlets | The system shall allow a Customer to search for nearest outlets by current location (lat/long), max distance, and optional category and outlet type; results shall include only outlets that are currently open and have matching items, and shall indicate favorite status and nickname when the outlet is in the customer’s favorites. | CUSTOMER | M |
| FR-SRCH-02 | Search items | The system shall allow a Customer to search items by text, category, outlet, status, and availability. | CUSTOMER | M |
| FR-SRCH-03 | Manage search history | The system shall allow a Customer to create, list, get, update, and delete their own search history entries. | CUSTOMER | M |

---

### 3.8 Favorites & Feedback (Customer)

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-FAV-01 | Add favorite | The system shall allow a Customer to add an outlet to their favorites with an optional nickname. | CUSTOMER | M |
| FR-FAV-02 | List/get/update/delete favorites | The system shall allow a Customer to list their favorites, get one by ID, update (e.g. nickname), and remove a favorite. | CUSTOMER | M |
| FR-FDB-01 | Submit feedback | The system shall allow a Customer to submit feedback (rating, optional text) for an outlet. | CUSTOMER | M |
| FR-FDB-02 | List my feedbacks | The system shall allow a Customer to list feedbacks they have submitted. | CUSTOMER | M |
| FR-FDB-03 | Get outlet feedbacks | The system shall allow Admin, Merchant, Sub-merchant, and Customer to retrieve feedbacks and feedback count for an outlet. | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT, CUSTOMER | M |

---

### 3.9 Payments

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-PAY-01 | Create/record payment | The system shall allow Admin or outlet owner (Merchant/Sub-merchant) to create or record a payment for an outlet (type, amount, date, receipt image, status). | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-PAY-02 | List/get payments | The system shall allow Admin or outlet owner to list and get payments (e.g. by outlet). | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-PAY-03 | Approve payment | The system shall allow Admin to approve a payment (e.g. after verification). | SYSADMIN, ADMIN | M |

---

### 3.10 Notifications & Media

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-NOT-01 | List notifications | The system shall allow an authenticated user to list their own notifications (e.g. with unread filter). | All authenticated | M |
| FR-NOT-02 | Get/mark notification | The system shall allow an authenticated user to get a notification by ID and mark it as read. | All authenticated | M |
| FR-NOT-03 | Create notification (system) | The system shall create in-app notifications for relevant events (e.g. pending approval, customer action confirmations). | System | S |
| FR-MED-01 | Upload image | The system shall allow an authenticated user to upload an image (e.g. profile, receipt) and return a reference (e.g. file name/URL) for use in profile or payment. | All authenticated | M |

---

### 3.11 Location & Reference Data

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-LOC-01 | List provinces/districts | The system shall allow Admin, Merchant, and Sub-merchant to list provinces and districts (e.g. for address selection). | SYSADMIN, ADMIN, MERCHANT, SUBMERCHANT | M |
| FR-LOC-02 | List countries | The system shall allow public or authenticated access to list countries where applicable. | Public / All | C |

---

### 3.12 Dashboard & Reporting (Admin)

| ID | Requirement | Description | Actor(s) | Priority |
|----|-------------|-------------|----------|----------|
| FR-DASH-01 | Dashboard summary | The system shall provide a dashboard summary (KPIs) for Admin (e.g. user counts, merchant/outlet counts, pending counts). | SYSADMIN, ADMIN | M |
| FR-DASH-02 | Merchant summary | The system shall provide a merchant-focused summary for Admin. | SYSADMIN, ADMIN | M |
| FR-DASH-03 | Activity report | The system shall provide activity data (e.g. over a configurable number of months) for Admin. | SYSADMIN, ADMIN | S |
| FR-DASH-04 | Monthly income | The system shall provide monthly income data (e.g. over a configurable number of months) for Admin. | SYSADMIN, ADMIN | S |

---

## 4. Requirement Summary by Actor

| Actor | Must (M) | Should (S) | Could (C) |
|-------|---------|-----------|----------|
| SYSADMIN | User management (add, update, approve, reject), forgot-password approval, all Admin capabilities | - | - |
| ADMIN | Merchant/sub-merchant/outlet/customer/payment management, dashboard summary & merchant summary, verify outlet payment | Activity report, monthly income | - |
| MERCHANT | Profile, sub-merchants, outlets (add, approve, update), schedules, items, categories, discounts, payments, get outlet feedbacks | - | - |
| SUBMERCHANT | Outlets (add, update, submit payment), schedules, items, categories, discounts, payments, get outlet feedbacks | - | - |
| CUSTOMER | Login, register, profile, search nearest outlets, search items, search history, favorites, feedback, view categories/outlet discounts/feedbacks, notifications, image upload | - | - |

---

## 5. Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | (Draft) | - | Initial core functional requirements. |
