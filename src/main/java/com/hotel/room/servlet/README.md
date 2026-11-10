# servlet

| URL | Class | Job |
|-----|-------|-----|
| `/rooms` | `PublicRoomListServlet` | Public room list with filters (only active rooms) |
| `/rooms/detail?id=` | `PublicRoomDetailServlet` | Public room page (retired rooms show 404) |
| `/admin/rooms` | `RoomManageServlet` | Staff room list with counts |
| `/admin/rooms/form` | `RoomFormServlet` | Add form, or edit form with `?id=` |
| `/admin/rooms/save` | `RoomSaveServlet` | Saves the add or edit form |
| `/admin/rooms/retire` | `RoomRetireServlet` | Retires a room (POST) |

The `/admin/rooms` pages are behind `StaffAuthFilter` and also check `canManageRooms()`.
Pages are in `WEB-INF/views/room/`. Keep servlets short: no business rules or SQL here.
