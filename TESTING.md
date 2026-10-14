# Validation — 14 October 2026

- Maven compiled the Java sources with release 17 and packaged the runnable Spring Boot JAR.
- 23 automated tests passed (18 API/security tests and 5 model/file tests).
- Tests cover authentication, staff/admin access, guest rejection, CSRF, validation, cash/card
  calculations, rounding, lifecycle transitions, search, deletion, refunds and file persistence.
- Added coverage for the supplied VIP/cash example (138,000 → 124,200 → 117,990), immutable
  invoice snapshots after booking removal, server-owned prices, altered client amounts,
  optional client amounts and compatibility with existing ten-field payment records.
- All JavaScript files passed Node syntax checks.
- Headless Google Chrome browser checks passed against a temporary ledger on port 8081:
  staff login, cash/card previews, conditional card fields, pending payment creation,
  invoice display, mark paid, receipt display, search, method filters, refund and deletion.
- Inspected desktop record and invoice screenshots and the mobile record layout.
  Record/invoice pages also passed mobile viewport overflow checks at 390px width.
- Generated an A4 receipt PDF through the browser's print rendering.
- Browser checks reported no JavaScript runtime errors. The user's data/payments.txt was
  not used for test transactions.

Run backend checks with `mvn test`. They use temporary files. The browser checks were run
as a separate temporary Playwright script, not added as a project dependency.

The exact original shared theme/fonts and the full assignment brief were not supplied.
Booking and user integration with the rest of the group project still needs its real adapters.
