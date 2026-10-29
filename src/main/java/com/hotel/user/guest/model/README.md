# Guest models

`Guest` extends `User` with guest type and active status. `RegularGuest` uses the persisted type `REGULAR` and no discount; `VipGuest` uses `VIP` and a 10% discount. New guests default to active. Guests may book for themselves and add reviews; staff-management permissions remain denied.

Persistence belongs to `GuestDAO`, which uses the relational `guests` table and restores the appropriate subtype.
