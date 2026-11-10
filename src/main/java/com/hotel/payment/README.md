# Payment & Billing — IT26101177

Staff-only payment ledger and simulated card payments. Uses the existing
`hotel_reservation` MySQL database and the shared Orchid Estate JSP theme.
The `HotelReservationApplication` entry point needs no changes.

## Pages

- `/admin/payments`: searchable history, method/status filters, totals, mark paid,
  refund confirmation and delete confirmation.
- `/admin/payments/record`: choose a booking, cash/card, pending/paid and transaction date.
  Amount is calculated on the server; it cannot be changed by editing the form.
- `/admin/payments/invoice?id=P1` (also accepts `bookingId`): invoice for pending
  entries, receipt for paid/refunded entries; Print supports saving as PDF.

Sign in through `/admin/login` with your staff/admin account. Guests cannot access
these pages. Every service operation checks staff permissions, and payment POSTs
also require a session CSRF token.

## Demonstrating card payments

Choose Card, enter any cardholder name (2–100 characters), a current/future expiry
month typed as `MM/YY` (for example `12/30`), and any three-digit test CVV.

| Test number | Result |
| --- | --- |
| `4242 4242 4242 4242` | Visa success |
| `5555 5555 5555 4444` | Mastercard success |
| `4000 0000 0000 0002` | Declined when marked paid |

This is a simulation, with no gateway or real charge/refund. Other card numbers
are rejected. Full card numbers, cardholder names, expiry and CVV are never saved
in the database or returned in form values. Only card type and last four digits
are stored. A pending card entry remembers the simulated card; marking it paid
uses that simulated result without collecting the fields again.

## OOP and business rules

- **Encapsulation:** immutable `Payment` fields, exposed through getters.
- **Inheritance:** `CashPayment` and `CardPayment` extend `Payment`.
- **Polymorphism:** each overrides `calculateTotal()`. Cash applies 5%; card adds
  no method discount. Guest VIP discount (10%) is applied first. Existing room
  pricing is reused, including the Suite long-stay discount.
- Money uses `BigDecimal`, rounded to two decimal places. The saved amount and
  pricing rates are used for the ledger and receipt totals.
- One ledger entry per booking, protected by a unique database constraint.
- Create as Pending or Paid; only Pending → Paid → Refunded is allowed.
  Refund marks the ledger; staff confirm money has been returned for cash.
- Deleting a mistaken entry resets the booking's payment status to pending.
- Cancelled bookings cannot receive new payments or move from pending to paid.
- Changes to payments and booking payment status share one transaction.
  Booking row locks serialize competing submissions; a locked sequence produces IDs.

## Handoff to the Reservation member

The reservation folders currently contain README placeholders, not a running
`ReservationService`. `BookingBillingAdapter` is the narrow temporary bridge to
`bookings`, `guests` and `rooms`; replace this adapter with calls to the future
reservation service without changing the payment UI or payment model.

Bookings must already exist. The adapter reads their IDs, room, guest, dates and
status and updates only `bookings.payment_status`. Payment creation does not
create a reservation or take over the reservation member's work.

The optional `scripts/payment-demo.sql` creates three labelled demo bookings and
an inactive demo guest without a usable password. It was loaded locally for this
project demonstration. On another machine, run it explicitly against the shared
database if needed; it is not part of automatic application startup.

## Database and verification

`src/main/resources/db/05-payments.sql` keeps the original table, upgrades its
status constraint to allow pending, and creates `payment_sequence`. Existing
payment rows are preserved. Spring initializes these on startup.

Run `sh mvnw test`. `PaymentTests` exercises real MySQL with rollback-only test
fixtures: pricing, masked-card storage, status transitions, duplicate rejection,
invalid/declined cards, cancellation and staff-only access. The existing project
suite also requires the local MySQL database configured in application.properties.
