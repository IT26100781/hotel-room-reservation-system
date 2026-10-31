# dao

`ReservationDAO` goes here. It is the only class that runs SQL on the `bookings` table (create it in `src/main/resources/db/04-bookings.sql`).

Follow `StaffDAO` (admin) or `RoomDAO` (room):
- Mark it `@Repository` and take a `JdbcTemplate` in the constructor.
- Use `?` placeholders in every query, never string concatenation.
- Write one private `mapRow` method that turns a row into the right model object (and subclass).
- Methods like `findAll`, `findById`, `save` and `update`; writes return `true` when exactly one row changed.

Bookings are cancelled with a status change, never deleted, because payments and reviews point at them.

It makes no business decisions; it just loads and stores rows.
