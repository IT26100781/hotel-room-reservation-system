# service

`ReservationService` goes here. It holds the business rules for a booking, and servlets call it instead of the DAO.

Follow `StaffService` or `RoomService`:
- Mark it `@Service` and take the DAO (and any other component's service it needs) in the constructor.
- Check the logged-in user's `canX()` permission first and throw `SecurityException` if they are not allowed.
- Validate the input and throw `IllegalArgumentException` with a clear message the page can show.
- New ids use the `BK` prefix (BK01, BK02, ...).
- Get rooms through `RoomService` and price a stay with `room.calculateTotalRate(nights)`, then apply the guest's `getDiscount()`.
- The room list's "Book now" buttons link to `/reservation/book?roomId=...`.
