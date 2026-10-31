# service

`RoomService` holds the business rules for rooms. Servlets call it; it calls `RoomDAO`.

- **Validation:** room number above 0 and unique, name not blank, rate above 0, capacity between 1 and the
  room type's maximum, size above 0.
- **Permissions:** adding, editing and retiring check `canManageRooms()` (admins only).
- **Search:** `searchRooms` filters by keyword, type, status, maximum price and minimum guests.
- **Dashboard:** `getMetrics` counts total, active, maintenance and retired rooms in one pass.

Other components (for example Reservation) should get rooms through `getRoomById` and use the room's own
`calculateTotalRate(nights)` for pricing.
Errors are thrown as `SecurityException` (not allowed) or `IllegalArgumentException` (input or rule problem).
