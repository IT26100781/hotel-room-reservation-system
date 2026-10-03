# dao

DAO means Data Access Object, and `ReviewDAO` goes here.
It is the only place that reads and writes `data/reviews.txt`, using `FileHelper` and the file names in `DataConfig`.
It has simple methods like `findAll()`, `findById()`, `save()`, `update()` and `delete()`.
It does not make business decisions, it just loads and stores records.
