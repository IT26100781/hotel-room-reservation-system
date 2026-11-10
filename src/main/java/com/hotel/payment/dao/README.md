# dao

<<<<<<< Updated upstream
DAO means Data Access Object, and `PaymentDAO` goes here.
It is the only place that reads and writes `data/payments.txt`, using `FileHelper` and the file names in `DataConfig`.
It has simple methods like `findAll()`, `findById()`, `save()`, `update()` and `delete()`.
It does not make business decisions, it just loads and stores records.
=======
PaymentDAO loads and stores the payments table using parameterized JdbcTemplate queries. BookingBillingAdapter temporarily bridges the shared booking schema until ReservationService is implemented. Mutations are coordinated transactionally by PaymentService.

See [Payment & Billing](../README.md) for setup, test cards and the reservation handoff.
>>>>>>> Stashed changes
