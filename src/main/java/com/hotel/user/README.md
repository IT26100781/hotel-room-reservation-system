# user

This folder holds `User`, the abstract parent class of everyone who can log in, and `Role` (GUEST, STAFF, ADMIN).
Guests and staff both extend `User`: `User -> Guest -> RegularGuest / VIPGuest` and `User -> Staff -> Admin`.
All permission methods like `canManageRooms()` return false in `User`, and each subclass overrides only the ones it is allowed.
The `guest` and `admin` subfolders are separate components with separate owners and separate data files.
