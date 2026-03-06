# Outlet Opening Hours API – Examples

Base URL: `/api/outlets`. All endpoints require `Authorization: Bearer <token>` and roles SYSADMIN, ADMIN, MERCHANT, or SUBMERCHANT.

## 1. List Outlets
**GET** `/api/outlets?name=&status=`
- Query: `name` (optional), `status` (optional)
- Response: `[{ "id": 1, "name": "Outlet 1", "status": "ACTIVE", "currentStatus": "OPEN" }]`

## 2. Get Outlet Schedules (grouped)
**GET** `/api/outlets/{id}/schedules`
- Response: `{ "NORMAL": [...], "EMERGENCY": [...], "TEMPORARY": [...], "DAILY": [...] }`

## 3. Check Open Status
**GET** `/api/outlets/{id}/status?datetime=2026-03-05T11:00`
- Response: `{ "outletId": 1, "status": "OPEN", "openTime": "10:00", "closeTime": "15:00" }`

## 4. Create Schedule
**POST** `/api/outlets/{id}/schedules`
- Body: `{ "scheduleType": "EMERGENCY", "specialDate": "2026-03-05", "openTime": "10:00", "closeTime": "15:00", "isClosed": false, "reason": "Power Cut" }`
- Response: Created schedule object with `id`

## 5. Update Schedule
**PUT** `/api/outlets/{id}/schedules/{scheduleId}` – same body as create

## 6. Delete Schedule
**DELETE** `/api/outlets/{id}/schedules/{scheduleId}`
- Response: `{ "message": "Schedule deleted successfully" }`

Validation: `openTime`/`closeTime` must be HH:mm; for TEMPORARY, `startDate` and `endDate` required with `endDate` >= `startDate`.
