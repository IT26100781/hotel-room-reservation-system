# data

This folder holds the six `.txt` files where all the app's data is saved (there is no database).
Each line is one record, and the fields are separated by `|`, for example `R1|SUITE|25000|4|active`.
Only one component writes each file (Room writes `rooms.txt`, and so on), but any component may read any file.
Do not edit these files by hand while the app is running.
