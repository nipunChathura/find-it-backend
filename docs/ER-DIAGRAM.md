# Find It – Entity Relationship Diagram

This document describes the database schema for the Find It backend as an ER diagram. The diagram is defined in Mermaid format and can be rendered in GitHub, VS Code (with a Mermaid extension), or [mermaid.live](https://mermaid.live).

A simplified visual overview is available at [`../assets/findit-er-diagram.png`](../assets/findit-er-diagram.png).

## Mermaid ER Diagram

```mermaid
erDiagram
    users {
        bigint user_id PK
        varchar username
        varchar email
        varchar password
        varchar is_system_user
        varchar status
        varchar role
        timestamp last_login
        bigint merchant_id FK
        bigint sub_merchant_id FK
        bigint customer_id FK
        varchar profile_image_url
    }

    customers {
        bigint customer_id PK
        varchar first_name
        varchar last_name
        varchar nic
        varchar dob
        varchar gender
        varchar country_name
        varchar profile_image
        varchar email
        varchar phone_number
        varchar membership_type
        varchar status
    }

    merchants {
        bigint merchant_id PK
        varchar merchant_name
        varchar merchant_email
        varchar merchant_nic
        varchar merchant_profile_image
        varchar merchant_address
        varchar merchant_phone_number
        varchar merchant_type
        varchar status
        varchar inactive_reason
    }

    sub_merchants {
        bigint sub_merchant_id PK
        bigint merchant_id FK
        varchar merchant_name
        varchar merchant_email
        varchar merchant_nic
        varchar merchant_address
        varchar merchant_phone_number
        varchar merchant_type
        varchar status
        varchar inactive_reason
    }

    countries {
        bigint country_id PK
        varchar name
        varchar code
    }

    provinces {
        bigint province_id PK
        varchar name
        varchar description
        bigint code
    }

    districts {
        bigint district_id PK
        bigint province_id FK
        varchar name
        varchar description
        bigint code
    }

    cities {
        bigint city_id PK
        bigint district_id FK
        varchar name
        varchar description
    }

    outlets {
        bigint outlet_id PK
        varchar outlet_name
        bigint merchant_id FK
        bigint sub_merchant_id FK
        varchar business_registration_number
        varchar tax_identification_number
        varchar postal_code
        bigint province_id FK
        bigint district_id FK
        bigint city_id FK
        varchar contact_number
        varchar email_address
        varchar address_line_1
        varchar address_line_2
        varchar outlet_type
        varchar business_category
        double latitude
        double longitude
        varchar bank_name
        varchar bank_branch
        varchar account_number
        varchar account_holder_name
        varchar status
        date subscription_valid_until
        varchar remarks
        varchar onboarding_status
        double rating
    }

    categories {
        bigint category_id PK
        varchar category_name
        varchar category_description
        varchar category_image
        varchar category_type
        varchar status
    }

    items {
        bigint item_id PK
        varchar item_name
        varchar item_description
        bigint category_id FK
        bigint outlet_id FK
        decimal price
        boolean availability
        varchar item_image
        varchar status
    }

    discounts {
        bigint discount_id PK
        varchar discount_name
        varchar discount_type
        double discount_value
        date start_date
        date end_date
        varchar status
        varchar discount_image
    }

    discount_items {
        bigint discount_item_id PK
        bigint discount_id FK
        bigint item_id FK
    }

    customer_favorites {
        bigint id PK
        bigint customer_id FK
        bigint outlet_id FK
        varchar nickname
    }

    customer_search_history {
        bigint id PK
        bigint customer_id
        varchar search_text
        double latitude
        double longitude
        double distance_km
        bigint category_id
        varchar outlet_type
        timestamp created_at
    }

    notifications {
        bigint id PK
        bigint user_id
        varchar type
        varchar title
        varchar body
        boolean is_read
        timestamp created_at
    }

    payments {
        bigint payment_id PK
        bigint outlet_id FK
        varchar payment_type
        decimal amount
        date payment_date
        varchar paid_month
        varchar receipt_image
        varchar status
    }

    outlet_schedule {
        bigint id PK
        bigint outlet_id FK
        varchar schedule_type
        varchar day_of_week
        date special_date
        date start_date
        date end_date
        varchar open_time
        varchar close_time
        varchar is_closed
        varchar reason
        int priority
        varchar is_active
        varchar status
    }

    holiday_master {
        bigint id PK
        date holiday_date
        varchar name
        varchar description
    }

    feedbacks {
        bigint feedback_id PK
        varchar feedback_text
        double rating
        bigint customer_id FK
        bigint outlet_id FK
    }

    merchants ||--o{ sub_merchants : "has"
    merchants ||--o{ outlets : "owns"
    sub_merchants ||--o{ outlets : "owns"

    provinces ||--o{ districts : "contains"
    districts ||--o{ cities : "contains"
    outlets }o--|| provinces : "in"
    outlets }o--|| districts : "in"
    outlets }o--|| cities : "in"

    categories ||--o{ items : "has"
    outlets ||--o{ items : "sells"

    discounts ||--o{ discount_items : "includes"
    items ||--o{ discount_items : "in"

    customers ||--o{ customer_favorites : "has"
    outlets ||--o{ customer_favorites : "favorited_by"

    customers ||--o{ feedbacks : "gives"
    outlets ||--o{ feedbacks : "receives"

    outlets ||--o{ payments : "has"
    outlets ||--o{ outlet_schedule : "has"

    users }o--o| merchants : "merchant_id"
    users }o--o| sub_merchants : "sub_merchant_id"
    users }o--o| customers : "customer_id"
```

## Entity summary

| Table | Description |
|-------|-------------|
| **users** | System users (admin, merchant, sub-merchant, customer); links via merchant_id, sub_merchant_id, or customer_id |
| **customers** | End customers (profile, membership, contact) |
| **merchants** | Main merchants (business info, type, status) |
| **sub_merchants** | Sub-merchants under a merchant |
| **outlets** | Physical/store outlets (location, bank, subscription, onboarding) |
| **countries** | Country lookup (not linked by FK in entities; used in customer.country_name) |
| **provinces** | Province lookup |
| **districts** | District lookup (belongs to province) |
| **cities** | City lookup (belongs to district) |
| **categories** | Product/service categories |
| **items** | Items sold by an outlet (linked to category and outlet) |
| **discounts** | Discount definitions (type, value, dates) |
| **discount_items** | Many-to-many: which items get which discount |
| **customer_favorites** | Customer–outlet favorites (with optional nickname) |
| **customer_search_history** | Search history (customer_id, search_text, filters, created_at) |
| **notifications** | User notifications (user_id, type, title, body, is_read) |
| **payments** | Payments linked to an outlet |
| **outlet_schedule** | Opening hours / schedule per outlet (normal, temporary, emergency) |
| **holiday_master** | Holiday dates (used for closure logic) |
| **feedbacks** | Customer feedback and rating for outlets |

## Relationship summary

- **Merchant → SubMerchant**: One-to-many (merchant has many sub-merchants).
- **Merchant / SubMerchant → Outlet**: Outlets belong to either a merchant or a sub-merchant.
- **Province → District → City**: Hierarchy for location (provinces contain districts, districts contain cities).
- **Outlet**: References province, district, city; has many items, payments, schedules, feedbacks, and customer favorites.
- **Category → Item**: One-to-many; each item has one category.
- **Outlet → Item**: One-to-many; each item belongs to one outlet.
- **Discount ↔ Item**: Many-to-many via **discount_items**.
- **Customer ↔ Outlet**: Many-to-many via **customer_favorites** (favorites).
- **Customer → Feedback**, **Outlet → Feedback**: Customers give feedback for outlets.
- **User**: Logical links to merchant, sub_merchant, or customer via IDs (no JPA relations in entity).

## Abstract entity (audit fields)

Most entities extend `AbstractEntity`, which adds:

- `created_by`, `created_datetime`
- `modified_by`, `modified_datetime`
- `version` (optimistic locking)

These columns exist on the corresponding tables but are omitted from the diagram for clarity.
