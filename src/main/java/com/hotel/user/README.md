# user

This folder holds `User`, the abstract parent class of everyone who can log in, and `Role` (GUEST, STAFF, ADMIN).
Guests and staff both extend `User`: `User -> Guest -> RegularGuest / VipGuest` and `User -> Staff -> Admin`.
The shared fields (id, name, email, password hash) are `protected` so subclasses can use the inherited state;
other classes go through the getters.
All permission methods like `canManageRooms()` return false in `User` (default-deny), and each subclass overrides only the ones it is allowed.
The `guest` and `admin` subfolders are separate components with separate owners and separate tables (`guests` and `staff`).
