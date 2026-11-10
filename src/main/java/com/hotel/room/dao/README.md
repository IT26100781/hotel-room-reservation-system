# dao

`RoomDAO` is the only class that runs SQL on the `rooms` table (created and seeded by `src/main/resources/db/03-rooms.sql`).

- Spring creates it (`@Repository`) and passes in a `JdbcTemplate`. Every query uses `?` placeholders.
- `mapRow` uses `Room.create` so each row comes back as a `StandardRoom`, `DeluxeRoom` or `SuiteRoom`.
- Methods: `findAll`, `findById`, `findByRoomNumber`, `save`, `update`, `updateStatus`, `existsByRoomNumber`.

Rooms are never deleted, because bookings point at them. Retiring a room only changes its status.
