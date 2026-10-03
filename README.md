# Hotel Room Reservation System

SE1020 Object Oriented Programming group project. A Spring Boot web app with JSP views
(Tailwind CSS) and plain pipe-delimited `.txt` file storage. There is no database.

## Running

Requires JDK 17+. From the project root:

```bash
./mvnw spring-boot:run
```

Then open http://localhost:8080. On Windows use `mvnw.cmd spring-boot:run`, or run
`HotelReservationApplication` from IntelliJ. The working directory must be the project root
so that the `data/` folder is found.

## Project structure

```
src/main/java/com/hotel/
  HotelReservationApplication.java   Spring Boot entry point
  config/        DataConfig: data folder, delimiter, file names
  util/          FileHelper (read/write data files), SessionUtil (logged-in user)
  user/          User (abstract base, default-deny permissions), Role
    guest/       model service servlet dao   Guest, RegularGuest, VIPGuest
    admin/       model service servlet dao   Staff, Admin
  room/          model service servlet dao
  reservation/   model service servlet dao
  payment/       model service servlet dao
  review/        model service servlet dao
src/main/webapp/WEB-INF/views/   guest/ admin/ room/ reservation/ payment/ review/
data/            guests.txt rooms.txt bookings.txt payments.txt staff.txt reviews.txt
```

## Components and data ownership

Each file is written by exactly one component. Any component may read any file.

| Component              | Writes       | Reads (read-only)        |
|------------------------|--------------|--------------------------|
| Guest / User           | guests.txt   | none                     |
| Room                   | rooms.txt    | none                     |
| Reservation            | bookings.txt | rooms.txt, guests.txt    |
| Payment (staff-only)   | payments.txt | bookings.txt, guests.txt |
| Admin / Staff          | staff.txt    | none                     |
| Feedback / Review      | reviews.txt  | bookings.txt, guests.txt |

## File formats (`|` delimited, dates as `YYYY-MM-DD`)

```
guests.txt    id | name | email | passwordHash | type | vip | status
rooms.txt     id | type | rate | capacity | status
bookings.txt  id | guestId | roomId | checkIn | checkOut | status | paymentStatus | checkedIn
payments.txt  id | bookingId | amount | method | status
staff.txt     id | name | email | passwordHash | role | status
reviews.txt   id | bookingId | guestId | rating | verified | text
```

ID prefixes: `G` guest, `R` room, `BK` booking, `P` payment, `S` staff, `RV` review.

## Git workflow

Each member works on their own branch and opens a pull request into `main`. Keep `main` building.
