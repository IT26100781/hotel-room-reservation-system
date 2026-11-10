# service

`ReviewService` goes here. It holds the business rules for a guest review, and servlets call it instead of the DAO.

Follow `StaffService` or `RoomService`:
- Mark it `@Service` and take the DAO (and any other component's service it needs) in the constructor.
- Check the logged-in user's `canX()` permission first and throw `SecurityException` if they are not allowed.
- Validate the input and throw `IllegalArgumentException` with a clear message the page can show.
- New ids use the `RV` prefix (RV01, RV02, ...).
- Guests add reviews (`canAddReviews()`); admins moderate them (`canModerateReviews()`).
- Check the booking belongs to the guest through the Reservation component's service.
