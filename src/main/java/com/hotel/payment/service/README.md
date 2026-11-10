# service

<<<<<<< Updated upstream
This folder holds `PaymentService`, which contains the business rules for the payment component.
It decides whether an action is valid (for example, checking for duplicates, availability or permissions) before anything is saved.
It never touches the file directly; it asks the DAO to load or save data.
Servlets call the service, so all the important logic lives in one place.
=======
PaymentService authorizes staff, calculates booking totals, prevents duplicate payments, and handles Pending → Paid → Refunded and deletion. CardSimulator validates only demonstration cards and returns type/last four digits; it never calls a payment gateway.

See [Payment & Billing](../README.md) for setup, test cards and the reservation handoff.
>>>>>>> Stashed changes
