# Find It – Use Case Diagrams (Draw.io)

Three use case diagrams are provided, one per role. They are in **Draw.io (diagrams.net) XML** format and can be opened directly in Draw.io for editing and presentation.

## Files

| File | Description |
|------|-------------|
| `use-case-admin-sysadmin.drawio.xml` | Use cases for **Admin / Sysadmin** (user, merchant, outlet, payment, customer, dashboard) |
| `use-case-customer.drawio.xml`       | Use cases for **Customer** (login, search, favorites, feedback, notifications) |
| `use-case-merchant.drawio.xml`       | Use cases for **Merchant / Sub-merchant** (outlets, items, discounts, payments, schedules) |
| `system-architecture.drawio.xml`      | **System architecture** (clients, Find It backend, MySQL, file storage) |
| `class-diagram.drawio.xml`            | **Class diagram** (entity classes, inheritance from AbstractEntity, associations) |

## How to open in Draw.io

1. **Draw.io Desktop:**  
   **File → Open from → Device** and select the `.drawio.xml` file.

2. **draw.io in browser (app.diagrams.net):**  
   **File → Open from → Device** and select the `.drawio.xml` file.

3. **Rename for default association (optional):**  
   If you prefer Draw.io to open these by default, you can rename the extension to `.drawio` (e.g. `use-case-admin-sysadmin.drawio`). The XML content is valid for both `.drawio` and `.drawio.xml`.

## Styling (modern diagram)

- **System boundary:** Dashed rounded rectangle with role-specific colour (blue Admin, purple Customer, green Merchant).
- **Actor:** UML actor shape on the left.
- **Use cases:** Ellipses; main use cases in amber/green/orange; read-only or shared in light blue.
- **Associations:** Simple lines from actor to each use case.

You can change colours, fonts, and layout in Draw.io after opening.

## Export for presentation

In Draw.io: **File → Export as → PNG / SVG / PDF** to get an image or PDF for slides or docs.
