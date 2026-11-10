# dao

`PaymentDAO` goes here. It is the only class that runs SQL on the `payments` table (create it in `src/main/resources/db/05-payments.sql`).

Follow `StaffDAO` (admin) or `RoomDAO` (room):
- Mark it `@Repository` and take a `JdbcTemplate` in the constructor.
- Use `?` placeholders in every query, never string concatenation.
- Write one private `mapRow` method that turns a row into the right model object (and subclass).
- Methods like `findAll`, `findById`, `save` and `update`; writes return `true` when exactly one row changed.

Card payments store only the last 4 digits and the card type, never the full number.

It makes no business decisions; it just loads and stores rows.
