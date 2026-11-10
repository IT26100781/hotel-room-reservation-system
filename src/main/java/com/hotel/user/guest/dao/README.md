# Guest DAO

`GuestDAO` follows `StaffDAO`: Spring injects `JdbcTemplate`, parameterized SQL accesses the `guests` table, and row mapping creates `RegularGuest` or `VipGuest` instances including their active status.

Methods: `findAllGuests`, `findById`, `findByEmail`, `saveGuest`, `updateGuest`, and `deleteGuest`. Deletion deactivates the record so existing bookings and reviews retain their guest reference.
