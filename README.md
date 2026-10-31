# Hotel Room Reservation System

SE1020 Object Oriented Programming group project: **The Orchid Estate**, a hotel booking website.
A Spring Boot web app (embedded Tomcat) with JSP views styled with Tailwind CSS, storing its data in **MySQL**
through Spring's `JdbcTemplate`.

## Running

You need **JDK 17+** and **MySQL 8** running locally.

1. Create the database once (MySQL Workbench or the `mysql` client):
   ```sql
   CREATE DATABASE hotel_reservation;
   ```
2. Start the app from the project root:
   ```bash
   ./mvnw spring-boot:run
   ```
   On Windows use `.\mvnw.cmd spring-boot:run`, or run `HotelReservationApplication` from IntelliJ.
3. Open http://localhost:8080.

The app connects as `root` / `root` on port `3306` by default. If your MySQL is different, set the
`DB_USER`, `DB_PASSWORD` or `DB_PORT` environment variables instead of editing `application.properties`.

The tables are created automatically on every start from `src/main/resources/db/*.sql`
(`CREATE TABLE IF NOT EXISTS`, so existing data is kept).

**First run:** open http://localhost:8080/admin. With no administrator yet, it sends you to a one-time
setup page to create the first admin account.

## Project structure

```
src/main/java/com/hotel/
  HotelReservationApplication.java   Spring Boot entry point (@ServletComponentScan finds the servlets)
  util/          PasswordUtil (salted SHA-256 hashing), SessionUtil (logged-in user)
  user/          User (abstract base, default-deny permissions), Role
    guest/       model dao service servlet   Guest, RegularGuest, VipGuest
    admin/       model dao service servlet   Staff, Admin (+ home page and staff portal)
  room/          model dao service servlet   Room, StandardRoom, DeluxeRoom, SuiteRoom
  reservation/   model dao service servlet
  payment/       model dao service servlet
  review/        model dao service servlet
src/main/resources/
  db/            one CREATE TABLE file per component (01-staff.sql ... 06-reviews.sql)
  static/        css/, js/, img/ shared by every page; prototype/ holds the static HTML designs
src/main/webapp/WEB-INF/views/
  common/        shared layout pieces included by every page (head, header, footer, staff sidebar)
  admin/ guest/ room/ reservation/ payment/ review/ main/   one folder of JSPs per component
```

Every component uses the same layers:

| Layer | Job |
|-------|-----|
| `model` | Classes that describe the object (inheritance and polymorphism live here) |
| `dao` | The only code that runs SQL, using `JdbcTemplate` and `?` placeholders |
| `service` | Business rules, validation and permission checks; throws exceptions with clear messages |
| `servlet` | Reads the request, calls the service, forwards to a JSP or redirects |

## Components and tables

Each table is written by exactly one component. Other components read through that component's service.

| Component | Table | SQL file |
|-----------|-------|----------|
| Admin / Staff | `staff` | `01-staff.sql` |
| Guest / User | `guests` | `02-guests.sql` |
| Room | `rooms` | `03-rooms.sql` |
| Reservation | `bookings` | `04-bookings.sql` |
| Payment (staff-only) | `payments` | `05-payments.sql` |
| Feedback / Review | `reviews` | `06-reviews.sql` |

ID prefixes: `S` staff, `G` guest, `R` room, `BK` booking, `P` payment, `RV` review.
Nothing that other tables point at is hard-deleted: guests, staff and rooms are deactivated or retired instead.

## Main URLs

| URL | Who | Page |
|-----|-----|------|
| `/` | everyone | Home |
| `/rooms`, `/rooms/detail?id=R101` | everyone | Room list and room details |
| `/register`, `/login` | guests | Create account, sign in |
| `/account`, `/profile` | signed-in guests | Guest home, profile and password |
| `/admin/login` | staff | Staff portal sign-in |
| `/admin` | staff | Dashboard |
| `/admin/rooms`, `/admin/guests`, `/admin/staff` | staff / admins | Manage rooms, guests and staff accounts |

Everything under `/admin/*` is protected by `StaffAuthFilter`. Guest-only pages are protected by `GuestAuthFilter`.
Each servlet still checks the user's `canX()` permission methods itself.

## Tests

```bash
./mvnw clean test
```

## Git workflow

Each member works on their own branch and opens a pull request into `main`. Keep `main` building:
run the tests before opening a pull request, and pull `main` into your branch regularly.
