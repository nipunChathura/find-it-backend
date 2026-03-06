# Find It – Use Case Diagram Summaries

Three use case diagrams describe the system by actor: Admin/Sysadmin, Customer, and Merchant (including Sub-merchant). Below is one summary paragraph per diagram.

---

## 1. Admin / Sysadmin Use Case Diagram – Summary

The **Admin / Sysadmin** use case diagram shows the capabilities of the system administrator and admin roles within the Find It Admin System. The actor (Admin or Sysadmin) can manage users (add, update, approve, and reject users; SYSADMIN can also approve forgot-password requests), manage merchants (list, onboard, approve, reject, and change status), manage sub-merchants (add, approve, and reject), manage outlets (list all outlets, verify outlet payment, and update outlet status), approve payments, manage customers (full CRUD and view customer search history), view the dashboard (summary, merchant summary, activity, and monthly income), and manage categories, items, and discounts (shared with the merchant side). The diagram separates system administration and operational oversight from the customer- and merchant-facing flows.

---

## 2. Customer Use Case Diagram – Summary

The **Customer** use case diagram describes the Find It Customer App from the end-user’s perspective. The actor is the Customer, who can log in or register (onboarding), manage their profile (e.g. change profile image and password), search for nearest outlets (by location, distance, and optional category), search items (by category, outlet, and availability), manage search history (create, list, update, and delete entries), manage favorites (add, list, update, and remove outlets, with optional nicknames), submit feedback for outlets (rating and comment), view notifications, and view categories, outlet discounts, and outlet feedbacks in read-only mode. The diagram focuses on discovery, convenience, and engagement (favorites and feedback) without any back-office or merchant management.

---

## 3. Merchant / Sub-merchant Use Case Diagram – Summary

The **Merchant / Sub-merchant** use case diagram covers the Find It Merchant System used by business users. The actor is the Merchant or Sub-merchant, who can log in or onboard (register as a merchant), update their own profile (main merchant only), manage sub-merchants (add, approve, reject, and update status; main merchant only), manage outlets (add, update, approve, and submit payment), manage outlet schedules (normal opening hours, temporary, and emergency overrides), manage items (CRUD per outlet), manage categories and discounts (CRUD and link items to discounts), manage payments (submit and list payments for outlets), and view outlet feedbacks and discounts, provinces and districts, and notifications and image uploads. Sub-merchants have the same capabilities for their own outlets but cannot manage sub-merchants or approve outlets; the diagram makes the split between merchant and sub-merchant responsibilities clear.
