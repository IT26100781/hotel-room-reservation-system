# dao

DAO means Data Access Object. `StaffDAO` is the only class that runs SQL on the `staff` table
(created by `src/main/resources/db/01-staff.sql`).

- Spring creates it (`@Repository`) and passes in a `JdbcTemplate` through the constructor.
- Every query uses `?` placeholders, never string concatenation, so it is safe from SQL injection.
- `mapRow` turns one database row into the right object: a `role` of `ADMIN` becomes an `Admin`,
  `STAFF` becomes a `Staff`, and anything else is an error.
- Methods: `findAllStaff`, `findById`, `findByEmail`, `saveStaff`, `updateStaff`, `deleteStaff`.
  Writes return `true` only when exactly one row changed.

Accounts are normally deactivated. `deleteStaff` permanently removes a row, and `StaffService` decides when that is allowed.
The DAO makes no business decisions; it just loads and stores rows.
