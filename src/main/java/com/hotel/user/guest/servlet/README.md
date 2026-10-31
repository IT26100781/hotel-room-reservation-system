# Guest servlets

The guest servlets use the same `@WebServlet`, injected service, `SessionUtil`, JSP forward, and redirect pattern as the admin servlets.

| Route | Methods | Handler |
| --- | --- | --- |
| `/register` | GET, POST | GuestRegisterServlet |
| `/login` | GET, POST | GuestLoginServlet |
| `/logout` | POST (GET redirects to account) | GuestLogoutServlet |
| `/account` | GET | GuestAccountServlet |
| `/profile` | GET, POST | GuestProfileServlet |
| `/profile/password` | POST | GuestPasswordServlet |
| `/profile/deactivate` | POST | GuestDeactivateServlet |
| `/admin/guests` | GET | GuestListServlet |
| `/admin/guests/update` | POST | GuestUpdateServlet |
| `/admin/guests/vip` | POST | GuestVipServlet |
| `/admin/guests/status` | POST | GuestStatusServlet |

`GuestAuthFilter` protects account, profile, and logout routes, reloads the guest from storage, and signs out disabled or missing accounts. The existing `StaffAuthFilter` protects the admin guest routes; the service additionally enforces guest-management permissions. Profile actions derive the guest ID from the session. `GuestServlet` holds shared servlet helpers.
