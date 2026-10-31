# service

`StaffService` holds the business rules for staff accounts. Servlets call it; it calls `StaffDAO`.

- **Validation:** name (not blank, at most 50 characters), email format, password (at least 8 characters, no spaces)
  and unique email.
- **Login:** checks the salted password hash with `PasswordUtil`, gives the same message for an unknown email
  and a wrong password, then refuses deactivated accounts.
- **Permissions:** every action takes the logged-in user and checks their `canX()` methods first.
- **Rules:** there must always be at least one active admin, and nobody can deactivate their own account.
- **IDs:** new accounts get the next `S01`, `S02`, ... id.
- **First run:** `createFirstAdmin` only works while no admin exists (used by the setup page).

Errors are reported as exceptions that the servlets turn into pages:
`SecurityException` = not allowed (403), `IllegalArgumentException` / `IllegalStateException` = a rule or input
problem (the message is shown on the form).
