# WD38 — Component 5.4: Payment & Billing

Spring Boot backend and connected HTML/CSS/JavaScript for the Hotel Room Reservation System.
This project implements the payment component only. It records desk payments; it does not charge cards or send refunds.

The supplied Payments HTML is preserved in `reference/payments-original.html`.
The connected history page is `src/main/resources/static/staff/payments/list.html`; `index.html` redirects there.
The supplied record and invoice layouts are connected in `record.html` and `invoice.html`.
Its table, method filters, search, totals, refund section, invoice links and delete modal now use the Java backend.
The shared theme, layout, icon and font assets were not supplied; local CSS, SVG icons and system fonts are included.
The page structure and forest/brass palette follow the supplied HTML; exact original typography and theme require the group’s shared assets.
The application works without a CDN or frontend build step.

## Start in IntelliJ IDEA

1. Extract the ZIP and open the `payment-billing-backend` folder (or its `pom.xml`) in IntelliJ.
2. Set the project SDK to **JDK 17 or 21**. Reload the Maven project and let dependencies download.
3. Open `PaymentBillingApplication.java` and create/run its Application configuration.
4. In **Run → Edit Configurations**, set:
   - **Main class:** `lk.sliit.hotel.payment.PaymentBillingApplication`
   - **Working directory:** the extracted folder containing `pom.xml`
   - **Program arguments:** `--demo.password=DemoPayment42`
   - If Program arguments is hidden, enable it under **Modify options**.
5. Run the application. Wait for the console to show that Tomcat started on port 8080.
6. Open **http://localhost:8080/login.html**.
7. Sign in with username **staff** and password **DemoPayment42**.

The argument sets the password for the demo account. It is not a Java VM option.
Alternatively set the environment variable `DEMO_STAFF_PASSWORD` to your chosen password and omit that argument.
There is no password built into the Java source.

The default `demo` profile provides a temporary staff login and these illustrative booking fixtures:

| Booking ID | Guest | Amount before cash discount |
|---|---|---:|
| BK1024 | Nethmi Perera | LKR 124,200 (138,000 less 10% VIP) |
| BK1026 | Dilini Jayasuriya | LKR 84,000 |
| BK1031 | Nethmi Perera | LKR 100,800 (112,000 less 10% VIP) |
| BK1028 | Sarah Collins | LKR 55,500 |
| BK1027 | Ravindu Bandara | LKR 124,200 |
| BK1025 | Ashan Fernando | LKR 37,000 |

`BK0990` is a cancelled fixture and cannot accept a new payment.
Payments start empty. The original HTML's sample transactions are not inserted into the ledger.
The demo `guest` account uses the same configured password and is denied payment access.

### Terminal alternative

```bash
mvn test
mvn spring-boot:run -Dspring-boot.run.arguments=--demo.password=DemoPayment42
```

To make a runnable JAR:

```bash
mvn package
java -jar target/payment-billing-1.0.0.jar --demo.password=DemoPayment42
```

After login the payment pages are `/staff/payments/list.html`, `/staff/payments/record.html` and `/staff/payments/invoice.html?id=<payment-id>`.

Open the application through Spring Boot at port 8080. Do not double-click the HTML files or run them through a separate Live Server.

## What is implemented

| Brief requirement | Implementation |
|---|---|
| Create a payment against a booking | Validated POST endpoint and Record payment page |
| Read invoices, receipts and history | History table, detail endpoint and printable billing page |
| Update Pending → Paid → Refunded | Status buttons and PATCH endpoint; reverse/skipped transitions rejected |
| Hard-delete / void a transaction | Delete confirmation, file rewrite, HTTP DELETE |
| Save to payments.txt with handwritten I/O | Java NIO read, split, serialize, temporary file and replace |
| Staff-only | Server-side Spring Security roles STAFF/ADMIN; guests receive 403 |
| Encapsulation | Private Payment fields; status changes go through changeStatus |
| Inheritance | CashPayment and CardPayment extend Payment |
| Polymorphism | Each subclass overrides calculateTotal |
| Abstraction | Abstract Payment and PaymentRepository / BookingGateway interfaces |

The configurable **5% cash discount is an example**, because the brief does not state the rate.
Set `payment.cash-discount-rate=0.10` for 10%, or the environment variable `CASH_DISCOUNT_RATE`.
Saved payments retain their original discount rate. Card payments have no discount.
LKR is the currency used by the supplied frontend.

Implementation assumption: this version uses **one current payment per booking**.
A booking may have one PENDING or PAID record; update that record instead of adding another.
Refunded historical records can remain. Instalments, partial refunds and balance calculations are not implemented.
Java obtains the amount from BookingGateway. An optional client `amount` is checked against that amount to reject stale or altered prices.
The record page has no editable amount field. VIP discounts belong to booking pricing; Java calculates the VIP-adjusted amount and then CashPayment applies its saved cash rate.
For the supplied BK1024 example: 138,000 × 0.90 = 124,200; 124,200 × 0.95 = **LKR 117,990**, a combined 14.5% discount.
The record page previews this calculation; the saved total always comes from Java.
New payments with detailed booking data save an immutable billing snapshot (guest, room, dates, room total and VIP rate).
Invoices continue using this snapshot after booking changes. Legacy payments without a snapshot show only known payment information.

## Backend files to study

All Java files are under `src/main/java/lk/sliit/hotel/payment/`.

| File / folder | Responsibility |
|---|---|
| model/Payment.java | Shared fields and permitted status changes |
| model/CashPayment.java | Cash discount calculation |
| model/CardPayment.java | Card total and optional brand/last-four reference |
| repository/FilePaymentRepository.java | Handwritten text-file persistence |
| service/PaymentService.java | CRUD rules, totals, invoice data and refund queue |
| controller/PaymentController.java | HTTP endpoints for the frontend |
| dto/ | Validated request fields and response shapes |
| config/SecurityConfig.java | Staff access, sessions and CSRF |
| integration/BookingGateway.java | Connection point to the booking teammate's service |
| integration/DemoBookingGateway.java | Standalone demo fixtures; replace for group integration |

No database, JPA or Lombok is required. Monetary arithmetic uses BigDecimal.
Java records in the dto folder are simple immutable request/response containers.

## API used by the frontend

Every payment endpoint requires an authenticated STAFF or ADMIN.

| HTTP | Path | Purpose |
|---|---|---|
| POST | /api/payments | Create a payment |
| GET | /api/payments | History; optional q, method and status filters |
| GET | /api/payments/{id} | One payment |
| PATCH | /api/payments/{id}/status | Change status |
| DELETE | /api/payments/{id} | Hard-delete |
| GET | /api/payments/{id}/invoice | Invoice, receipt or refund-record data |
| GET | /api/payments/summary | Collected/refunded totals, awaiting bookings, refund queue |
| GET | /api/payments/bookings | Active bookings without a current payment |
| GET | /api/payments/booking/{bookingId} | Derived paid flag and payment records for a booking |
| GET | /api/payments/settings | Cash discount and currency |
| GET | /api/csrf | Obtain the session's CSRF header name and token |
| POST | /api/login | Form-encoded username and password |
| POST | /api/logout | End the session |
| GET | /api/session | Logged-in username and roles |

Example create body (`amount` is optional; when sent it must match the server booking amount after VIP discount):

```json
{
  "bookingId": "BK1028",
  "amount": "55500.00",
  "method": "CASH",
  "status": "PAID",
  "paymentDate": "2026-01-10",
  "cardBrand": "",
  "lastFour": ""
}
```

The server returns `discountAmount` and `total`; with a 5% discount the total is 52725.00.
For a card use `"method": "CARD"`; optional brands are VISA, MASTERCARD, AMEX or OTHER.
`lastFour` accepts only four digits or an empty string. No full card number, expiry or CVV is collected.

Update body: `{"status":"REFUNDED"}` for a payment already marked PAID.
Creation accepts PENDING or PAID. Creating REFUNDED directly is rejected.
Dates use YYYY-MM-DD, and future payment dates are rejected using Sri Lanka time.
Errors return JSON with a `message`; field-validation errors also include an `errors` object.

### Reuse the JavaScript with your frontend

`js/api.js` contains PaymentAPI.create, list, update, remove, invoice, summary, bookings and settings.
It sends the session cookie and a CSRF header on every write.
It refreshes the CSRF token after login and never automatically retries a payment write.

The connected history page preserves the supplied selectors:

| HTML selector | Backend connection |
|---|---|
| #payments-table tbody | PaymentAPI.list() |
| [data-filter-target] | Search loaded payment/booking/guest values |
| [data-chip] | Filter CASH/CARD locally |
| #collected, #awaiting, #refunded | PaymentAPI.summary() |
| #delete-modal / #confirm-delete | PaymentAPI.remove(id) |
| Invoice link | invoice.html?id=... → PaymentAPI.invoice(id) |
| #refund-list | Cancelled bookings with PAID records → update to REFUNDED |

Untrusted text is inserted with textContent, not interpreted as HTML.
The old data-user="admin" attribute is not used as authorization.

## Joining the group project

1. Keep the group's existing Spring Boot application. Copy in the payment package and static payment assets, and merge the three starter dependencies from this pom.
2. Implement **BookingGateway** using the real reservation service. Its findAll() must return BookingInfo objects containing the real booking ID, guest name, room label, status and amount due after VIP discounts. Supply `BillingDetails` for the room total before VIP discounts, VIP rate, guest email and stay dates. The amount due must equal `billing.amountDue()`; do not subtract VIP twice. A cancelled booking must map to status CANCELLED. The brief does not provide the bookings.txt schema, so this module does not invent or overwrite that file.
3. Replace the demo login with the staff/user team's authentication. Map legitimate staff/admin users to ROLE_STAFF / ROLE_ADMIN. Preserve server-side method authorization and the payment URL rules when merging the shared SecurityFilterChain. Do not install a second competing login system.
4. Activate a non-demo profile, for example `--spring.profiles.active=team`. DemoSecurityConfig and DemoBookingGateway then stop loading; your real adapters must be present.
5. The booking team can call PaymentService.bookingPaymentState(bookingId) in a staff-authorized flow, or use its endpoint, to derive whether a booking is paid. Do not assume this component writes a paid flag into bookings.txt. After deletion/refund, the derived paid flag becomes false if no PAID record remains. Check-in remains the booking component's responsibility.

The entire hotel system cannot be fully integrated until the teammates supply their actual booking and authentication classes. The included demo runs independently so the payment work can be demonstrated now.

## File format and persistence

Runtime file: `data/payments.txt`, relative to the working directory.
Override with `--payment.file=/absolute/path/payments.txt` if needed.
The application creates the directory and empty file on first run.

Each new line has eleven pipe-separated fields. Existing ten-field records remain readable:

```text
id|bookingId|amount|method|status|paymentDate|discountRate|createdAt|cardBrand|lastFour|billingSnapshot
```

The brief leaves the detailed schema unspecified. The extra fields preserve the payment date, discount and optional masked-card display. `billingSnapshot` is Base64-encoded JSON (encoding, not encryption); it prevents names or room labels from breaking pipe-delimited rows. It is empty for legacy records without detailed booking information.
The old five-field comment in the supplied HTML is not a complete storage format for this implementation.
Do not point this app at an old five-field payments.txt without first migrating/backing it up.
IDs reject delimiters/newlines. Corrupt records cause a clear error instead of being silently discarded.
Each mutation reads the file and writes a replacement through a temporary file.
Run **one Java application process per payments.txt**; synchronization protects concurrent requests within that process.

## Demonstration checklist

1. Log in as staff and record a cash payment for BK1025 with amount 37000. The total is 35150 at 5%.
2. Create it as PENDING. The billing document says INVOICE and does not claim money was received.
3. In history choose Mark paid. Open its billing document again; it now says RECEIPT.
4. Record a refund after refunding at the desk. The history and refunded total update.
5. Delete the record and confirm that its line disappears from payments.txt.
6. Restart the application to demonstrate that remaining records persist.
7. Try the guest account; it cannot enter payment pages or APIs.

Automated tests cover monetary rounding, file reloads/corruption, validation, authorization, CSRF,
status transitions, filtering, deletion, summaries, cancelled-booking refunds and session login.
Run `mvn test` to execute them.

## Technical references

- Spring Boot 3.5 system requirements: https://docs.spring.io/spring-boot/3.5/system-requirements.html
- Spring Security 6.5 CSRF: https://docs.spring.io/spring-security/reference/6.5/servlet/exploits/csrf.html

Scope: section 5.4, Payment & Billing (staff-only), as described in the conversation. The full assignment document was not present in the workspace for verification.

The demo booking adapter and staff login allow this component to run independently. Real booking/user integration still requires the group’s services.
The pasted examples contain inconsistent illustrative payment/booking IDs; no sample paid/refunded transactions are imported as real ledger entries.
