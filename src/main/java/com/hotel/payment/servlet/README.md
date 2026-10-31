# servlet

This folder holds the servlets for a payment, such as `RecordPaymentServlet` and `PaymentListServlet`.

Follow the admin or room servlets:
- `@WebServlet("/...")` with `@Autowired` for the service.
- Get the logged-in user with `SessionUtil.getCurrentUser(...)`.
- Call the service; after a successful POST, redirect (Post/Redirect/Get). On an error, put `error` in the
  request and forward back to the JSP.
- Catch `SecurityException` and send `403`.
- Staff-only pages go under `/admin/...` so `StaffAuthFilter` protects them automatically.

Pages are in `WEB-INF/views/payment/`. Keep servlets short: no business rules or SQL here.
