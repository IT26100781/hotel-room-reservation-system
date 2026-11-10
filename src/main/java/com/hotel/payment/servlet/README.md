# servlet

<<<<<<< Updated upstream
This folder holds the classes that handle requests from the browser, such as `RecordPaymentServlet` and `PaymentListServlet`.
A servlet reads the form inputs, gets the logged-in user with `SessionUtil`, and checks their permission with the `canX()` methods.
It then calls the service and shows a JSP page from `WEB-INF/views/payment/`.
Keep servlets short: no business rules or file code here.
=======
PaymentServlet handles /admin/payments and its record, invoice, paid, refund and delete routes. StaffAuthFilter protects these routes; the service checks permissions and POSTs validate a session CSRF token. Successful POSTs redirect; JSPs display safe validation errors.

See [Payment & Billing](../README.md) for setup, test cards and the reservation handoff.
>>>>>>> Stashed changes
