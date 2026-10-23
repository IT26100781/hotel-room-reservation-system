# servlet

This folder holds the classes that handle requests from the browser, such as `GuestRegisterServlet`, `LoginServlet` and `GuestProfileServlet`.
A servlet reads the form inputs, gets the logged-in user with `SessionUtil`, and checks their permission with the `canX()` methods.
It then calls the service and shows a JSP page from `WEB-INF/views/guest/`.
Keep servlets short: no business rules or file code here.
