# model

This folder holds the classes that describe a room.

- `Room` is abstract and holds the shared data (number, name, rate, capacity, size, image, status) in private fields.
- `StandardRoom`, `DeluxeRoom` and `SuiteRoom` extend it and override the behaviour that differs:
  amenities, description, `getMaxAllowedCapacity()` and `calculateTotalRate(nights)`
  (a suite gives 5% off for stays of 3 or more nights).
- `Room.create(...)` is the one factory method that builds the right subclass from a type name.
- `RoomStatus` is an enum: `ACTIVE` (bookable), `MAINTENANCE` (temporarily hidden), `RETIRED` (permanently out of service).

Models only describe the object. They do not run SQL or handle web requests.
