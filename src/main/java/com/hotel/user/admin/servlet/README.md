# servlet

This folder holds the staff portal servlets, plus the site home page.

| URL | Class | Job |
|-----|-------|-----|
| `/` | `HomeServlet` | Home page (`main/index.jsp`) |
| `/admin/setup` | `StaffSetupServlet` | One-time page to create the first admin |
| `/admin/login` | `StaffLoginServlet` | Staff sign-in |
| `/admin/logout` | `StaffLogoutServlet` | Sign out (POST only) |
| `/admin` | `AdminDashboardServlet` | Dashboard |
| `/admin/staff` | `StaffListServlet` | List of staff accounts |
| `/admin/staff/form` | `StaffFormServlet` | Add form, or edit form with `?id=` |
| `/admin/staff/save` | `StaffSaveServlet` | Saves the add or edit form |
| `/admin/staff/role` | `StaffRoleServlet` | Switch between Staff and Admin |
| `/admin/staff/status` | `StaffStatusServlet` | Activate or deactivate an account |

`StaffAuthFilter` runs before every `/admin/*` request and sends anyone who is not signed in as staff to
`/admin/login` (the login and setup pages are let through). This protects other components' admin pages too.

Every servlet follows the same steps: get the user with `SessionUtil`, call the service, then
redirect after a successful POST (Post/Redirect/Get) or forward back to the JSP with an `error` message.
Pages are in `WEB-INF/views/admin/`. Keep servlets short: no business rules or SQL here.
