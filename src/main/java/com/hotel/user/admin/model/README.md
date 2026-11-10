# model

This folder holds the classes that describe a staff account: `Staff` and `Admin`.

- `Staff extends User` adds the `active` flag (`isActive()` / `setActive()`) and turns on the front-desk permissions:
  view and manage guests, record payments, manage any booking, check guests in and out.
- `Admin extends Staff` keeps everything a `Staff` can do and also turns on the admin permissions:
  manage rooms, view and manage staff and admin accounts, moderate reviews.
- `getRole()` returns a constant in each class (`Role.STAFF` / `Role.ADMIN`), so the class itself is the role.
  Promoting someone creates an `Admin` object for the same person instead of changing a field.

Models only describe the object. They do not run SQL or handle web requests.
