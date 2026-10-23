# dao

DAO means Data Access Object, and `GuestDAO` goes here.
It is the only place that reads and writes `data/guests.txt`, using `FileHelper` and the file names in `DataConfig`.
It has simple methods like `findAll()`, `findById()`, `save()`, `update()` and `delete()`.
It does not make business decisions, it just loads and stores records.
