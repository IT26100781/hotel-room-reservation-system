# util

This folder holds small helper classes that every component can use.

- `PasswordUtil` hashes passwords with a random salt and SHA-256 (`hash`) and checks a typed password
  against a stored hash (`matchesPassword`). Passwords are never stored or logged in plain text.
- `SessionUtil` stores and gets the logged-in user in the `HttpSession` (`login`, `getCurrentUser`,
  `isLoggedIn`, `logout`). Always take the current user from here, never from a form field or URL.

Use these helpers instead of writing your own password or session code.
