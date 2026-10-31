# Guest service

`GuestService` follows the admin component's service pattern and owns registration, login, input validation, email uniqueness, password hashing, profile ownership, and staff permissions.

Guests register as active regular guests. Only users with `canManageGuestUsers()` may grant VIP status or reactivate accounts. Guests can edit their own details, change their own password after verifying the current password, and deactivate their own account. `listGuests` and lookup check view permissions; guests may look up their own record.

Guest IDs use G01, G02, and so on. Like staff IDs, allocation scans existing records; registration is synchronized within one application instance. Multiple application instances should use a database sequence or another database-managed ID allocation strategy.
