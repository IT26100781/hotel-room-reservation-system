# model

<<<<<<< Updated upstream
This folder holds the classes that describe what a payment is, such as `Payment`, `CashPayment` and `CardPayment`.
Each class keeps its own data in private fields and has the behaviour that belongs to it, like overridden methods in subclasses.
Each model also has `toFileString()` to turn the object into one line of `payments.txt`, and `fromFileString()` to turn a line back into an object.
Do not read files or handle web requests here, only describe the object.
=======
Immutable Payment base class, CashPayment and CardPayment subclasses, PaymentStatus, and the BookingBill/PaymentDetails page views. Cash and card override calculateTotal(); money uses BigDecimal.

See [Payment & Billing](../README.md) for setup, test cards and the reservation handoff.
>>>>>>> Stashed changes
