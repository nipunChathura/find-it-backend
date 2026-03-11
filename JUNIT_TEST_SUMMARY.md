# JUnit Test Cases Summary – Find It (Sampurna APIs)

Module අනුව API controller tests සහ summary table.

---

## Summary Table

| Test Case ID | Module          | Test Description                              | Expected Result              | Status |
| ------------ | --------------- | --------------------------------------------- | ---------------------------- | ------ |
| UT01         | Authentication  | User login with valid credentials             | Login successful              | Pass   |
| UT02         | Authentication  | User registration with valid data             | User registered successfully  | Pass   |
| UT03         | Authentication  | Change password when authenticated            | Password changed successfully | Pass   |
| UT04         | Authentication  | Forgot password request                       | Reset link sent              | Pass   |
| UT05         | Item Management | Add new item                                  | Item added successfully       | Pass   |
| UT06         | Item Management | Get items by outlet id                        | List of items displayed       | Pass   |
| UT07         | Item Management | Get item by id                                | Item details returned        | Pass   |
| UT08         | Item Management | Search items with filters                     | Filtered list returned        | Pass   |
| UT09         | Item Management | Update existing item                          | Item updated successfully     | Pass   |
| UT10         | Item Management | Delete item                                   | Item deleted (204 No Content) | Pass   |
| UT11         | Outlet          | List outlets with optional filters            | List of outlets displayed     | Pass   |
| UT12         | Outlet          | List outlets assigned to merchant              | Assigned outlets list         | Pass   |
| UT13         | Outlet          | Get outlet details by id                      | Outlet details returned       | Pass   |
| UT14         | Outlet          | Add new outlet                                | Outlet added successfully     | Pass   |
| UT15         | Outlet          | Get outlet open/closed status                  | Status (OPEN/CLOSED) returned | Pass   |
| UT16         | Outlet          | Get outlet schedules grouped by type           | Schedules returned            | Pass   |
| UT17         | Outlet          | Get current discounts for outlet              | List of discounts displayed   | Pass   |
| UT18         | Outlet          | Get feedbacks for outlet                      | List of feedbacks displayed   | Pass   |
| UT19         | Merchant        | Merchant login with valid credentials         | Login successful              | Pass   |
| UT20         | Merchant        | Merchant onboarding                           | Merchant created successfully | Pass   |
| UT21         | Merchant        | Get merchant with outlets                     | Merchant and outlets returned | Pass   |
| UT22         | Merchant        | Update merchant profile                       | Profile updated successfully  | Pass   |
| UT23         | Merchant        | Merchant forgot password                      | Reset link sent               | Pass   |
| UT24         | Merchant        | Approve sub-merchant                          | Sub-merchant approved         | Pass   |

---

## Test Module Structure

```
src/test/java/lk/icbt/findit/
├── auth/
│   └── AuthenticationModuleTest.java   (UT01–UT04)
├── item/
│   └── ItemModuleTest.java             (UT05–UT10)
├── outlet/
│   └── OutletModuleTest.java           (UT11–UT18)
└── merchant/
    └── MerchantModuleTest.java         (UT19–UT24)
```

---

## Run Tests

**Note:** Spring Boot 4.0 with full ApplicationContext (JPA, Security) බැවින් tests run කිරීමට database හෝ test-specific exclusions (e.g. `@DataJpaTest` exclude, in-memory H2) අවශ්‍ය විය හැක. Test structure හා assertions සකස් කර ඇත.

```bash
mvn test
```

Module එකක් එකක් run කිරීම:

```bash
mvn test -Dtest=AuthenticationModuleTest
mvn test -Dtest=ItemModuleTest
mvn test -Dtest=OutletModuleTest
mvn test -Dtest=MerchantModuleTest
```

Tests pass කරන්න: `application.properties` හි test profile එකට H2 හෝ embedded DB එකක් set කිරීම හෝ `@WebMvcTest` සමඟ `excludeAutoConfiguration` use කිරීම උදව් වේ.
