package com.hotel.room;

import com.hotel.room.dao.RoomDAO;
import com.hotel.room.model.DeluxeRoom;
import com.hotel.room.model.Room;
import com.hotel.room.model.RoomStatus;
import com.hotel.room.model.StandardRoom;
import com.hotel.room.model.SuiteRoom;
import com.hotel.room.service.RoomService;
import com.hotel.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Room model polymorphism, RoomService business rules, and RoomMetrics.
 * Uses an in-memory fake DAO to avoid a real database connection.
 */
class RoomModelAndServiceTest {

    private RoomService roomService;
    private List<Room> fakeDb;
    private User staffUser;
    private User guestUser;

    @BeforeEach
    void setUp() {
        fakeDb = new ArrayList<>();

        // Fake DAO backed by an in-memory list — no JDBC / Spring context needed
        RoomDAO mockDao = new RoomDAO(null) {
            @Override
            public List<Room> findAll() {
                return new ArrayList<>(fakeDb);
            }

            @Override
            public Room findById(String id) {
                return fakeDb.stream()
                        .filter(r -> r.getRoomId().equalsIgnoreCase(id))
                        .findFirst().orElse(null);
            }

            @Override
            public Room findByRoomNumber(int roomNumber) {
                return fakeDb.stream()
                        .filter(r -> r.getRoomNumber() == roomNumber)
                        .findFirst().orElse(null);
            }

            @Override
            public boolean save(Room room) {
                fakeDb.add(room);
                return true;
            }

            @Override
            public boolean update(Room room) {
                for (int i = 0; i < fakeDb.size(); i++) {
                    if (fakeDb.get(i).getRoomId().equalsIgnoreCase(room.getRoomId())) {
                        fakeDb.set(i, room);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean updateStatus(String id, String status) {
                Room r = findById(id);
                if (r != null) {
                    r.setStatus(RoomStatus.fromString(status));
                    return true;
                }
                return false;
            }

            @Override
            public boolean existsByRoomNumber(int roomNumber, String excludeRoomId) {
                return fakeDb.stream().anyMatch(r ->
                        r.getRoomNumber() == roomNumber
                        && (excludeRoomId == null || !r.getRoomId().equalsIgnoreCase(excludeRoomId)));
            }
        };

        roomService = new RoomService(mockDao);

        // A staff user that can manage rooms
        staffUser = mock(User.class);
        when(staffUser.canManageRooms()).thenReturn(true);

        // A guest user that cannot manage rooms
        guestUser = mock(User.class);
        when(guestUser.canManageRooms()).thenReturn(false);
    }

    // -------------------------------------------------------------------------
    // Model polymorphism
    // -------------------------------------------------------------------------

    @Test
    void testRoomTypeDisplayNames() {
        Room standard = new StandardRoom("R101", "Standard Room 101", 101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE);
        Room deluxe   = new DeluxeRoom  ("R201", "Deluxe Room 201",   201, 28000.0, 3, 44, "dlx.jpg", RoomStatus.ACTIVE);
        Room suite    = new SuiteRoom   ("R301", "Estate Suite 301",  301, 46000.0, 4, 62, "ste.jpg", RoomStatus.ACTIVE);

        assertEquals("Standard", standard.getTypeDisplayName());
        assertEquals("Deluxe",   deluxe.getTypeDisplayName());
        assertEquals("Suite",    suite.getTypeDisplayName());
    }

    @Test
    void testRoomAmenitiesAndDescriptions() {
        Room standard = new StandardRoom("R101", "Standard Room 101", 101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE);
        Room deluxe   = new DeluxeRoom  ("R201", "Deluxe Room 201",   201, 28000.0, 3, 44, "dlx.jpg", RoomStatus.ACTIVE);
        Room suite    = new SuiteRoom   ("R301", "Estate Suite 301",  301, 46000.0, 4, 62, "ste.jpg", RoomStatus.ACTIVE);

        assertTrue(standard.getAmenities().length > 0, "Standard room must have amenities");
        assertTrue(deluxe.getAmenities().length   > 0, "Deluxe room must have amenities");
        assertTrue(suite.getAmenities().length    > 0, "Suite must have amenities");

        assertNotNull(standard.getDescription());
        assertNotNull(deluxe.getDescription());
        assertNotNull(suite.getDescription());
    }

    @Test
    void testMaxAllowedCapacity() {
        Room standard = new StandardRoom("R101", "S", 101, 10000.0, 2, 30, "s.jpg", RoomStatus.ACTIVE);
        Room deluxe   = new DeluxeRoom  ("R201", "D", 201, 20000.0, 3, 40, "d.jpg", RoomStatus.ACTIVE);
        Room suite    = new SuiteRoom   ("R301", "E", 301, 40000.0, 4, 60, "e.jpg", RoomStatus.ACTIVE);

        assertEquals(2, standard.getMaxAllowedCapacity());
        assertEquals(3, deluxe.getMaxAllowedCapacity());
        assertEquals(4, suite.getMaxAllowedCapacity());
    }

    @Test
    void testCalculateTotalRate() {
        // Standard: base × nights
        Room standard = new StandardRoom("R1", "S", 1, 10000.0, 2, 30, "s.jpg", RoomStatus.ACTIVE);
        assertEquals(30000.0, standard.calculateTotalRate(3), 0.01);

        // Suite: 5 % discount for 3+ nights
        Room suite = new SuiteRoom("R2", "E", 2, 10000.0, 4, 60, "e.jpg", RoomStatus.ACTIVE);
        double expected = 10000.0 * 3 * 0.95; // 28500.0
        assertEquals(expected, suite.calculateTotalRate(3), 0.01);

        // 0 or negative nights → 0
        assertEquals(0.0, standard.calculateTotalRate(0));
        assertEquals(0.0, standard.calculateTotalRate(-1));
    }

    @Test
    void testRoomStatusEnum() {
        assertEquals("active",      RoomStatus.ACTIVE.getValue());
        assertEquals("maintenance", RoomStatus.MAINTENANCE.getValue());
        assertEquals("retired",     RoomStatus.RETIRED.getValue());

        assertEquals(RoomStatus.ACTIVE,      RoomStatus.fromString("active"));
        assertEquals(RoomStatus.MAINTENANCE, RoomStatus.fromString("MAINTENANCE"));
        assertEquals(RoomStatus.RETIRED,     RoomStatus.fromString("retired"));

        // Unknown string → default ACTIVE
        assertThrows(IllegalArgumentException.class,() -> RoomStatus.fromString("unknown"));
        assertEquals(RoomStatus.ACTIVE, RoomStatus.fromString(null));
    }

    @Test
    void testRoomFactory() {
        Room r = Room.create("StandardRoom", "R1", "S", 1, 10000.0, 2, 30, "s.jpg", RoomStatus.ACTIVE);
        assertInstanceOf(StandardRoom.class, r);

        Room d = Room.create("DeluxeRoom", "R2", "D", 2, 20000.0, 3, 40, "d.jpg", RoomStatus.MAINTENANCE);
        assertInstanceOf(DeluxeRoom.class, d);

        Room e = Room.create("SuiteRoom", "R3", "E", 3, 40000.0, 4, 60, "e.jpg", RoomStatus.RETIRED);
        assertInstanceOf(SuiteRoom.class, e);

        // Unknown type must throw
        assertThrows(IllegalStateException.class, () ->
                Room.create("BungalowRoom", "R4", "X", 4, 10000.0, 2, 25, "x.jpg", RoomStatus.ACTIVE));
    }

    // -------------------------------------------------------------------------
    // RoomService – addRoom
    // -------------------------------------------------------------------------

    @Test
    void testAddRoom_success() {
        Room r = new StandardRoom("R101", "Pine Standard", 101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE);
        assertDoesNotThrow(() -> roomService.addRoom(staffUser, r));
        assertEquals(1, fakeDb.size());
    }

    @Test
    void testAddRoom_duplicateRoomNumber_throws() {
        Room r1 = new StandardRoom("R101", "Pine Standard", 101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE);
        assertDoesNotThrow(() -> roomService.addRoom(staffUser, r1));

        Room r2 = new DeluxeRoom("R102", "Duplicate Number", 101, 25000.0, 3, 40, "dlx.jpg", RoomStatus.ACTIVE);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> roomService.addRoom(staffUser, r2));
        assertTrue(ex.getMessage().contains("101"), "Error must mention the duplicate room number");
    }

    @Test
    void testAddRoom_zeroRate_throws() {
        Room r = new StandardRoom("R103", "Zero Rate", 103, 0.0, 2, 30, "std.jpg", RoomStatus.ACTIVE);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> roomService.addRoom(staffUser, r));
        assertTrue(ex.getMessage().toLowerCase().contains("rate"));
    }

    @Test
    void testAddRoom_negativeRate_throws() {
        Room r = new StandardRoom("R104", "Negative Rate", 104, -500.0, 2, 30, "std.jpg", RoomStatus.ACTIVE);
        assertThrows(IllegalArgumentException.class, () -> roomService.addRoom(staffUser, r));
    }

    @Test
    void testAddRoom_capacityExceedsMax_throws() {
        // StandardRoom max is 2 — setting 3 must fail
        Room r = new StandardRoom("R105", "Over Capacity", 105, 15000.0, 3, 30, "std.jpg", RoomStatus.ACTIVE);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> roomService.addRoom(staffUser, r));
        assertTrue(ex.getMessage().toLowerCase().contains("capacity"));
    }

    @Test
    void testAddRoom_noPermission_throws() {
        Room r = new StandardRoom("R106", "Perm Test", 106, 15000.0, 2, 30, "std.jpg", RoomStatus.ACTIVE);
        assertThrows(SecurityException.class, () -> roomService.addRoom(guestUser, r));
        assertThrows(SecurityException.class, () -> roomService.addRoom(null, r));
    }

    // -------------------------------------------------------------------------
    // RoomService – updateRoom
    // -------------------------------------------------------------------------

    @Test
    void testUpdateRoom_success() {
        Room r = new StandardRoom("R101", "Pine Standard", 101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE);
        assertDoesNotThrow(() -> roomService.addRoom(staffUser, r));

        r.setRoomName("Pine Standard Updated");
        r.setBaseRate(19000.0);
        assertDoesNotThrow(() -> roomService.updateRoom(staffUser, r));

        assertEquals("Pine Standard Updated", fakeDb.get(0).getRoomName());
    }

    @Test
    void testUpdateRoom_nonExistentRoom_throws() {
        Room r = new StandardRoom("GHOST", "Ghost Room", 999, 15000.0, 2, 30, "g.jpg", RoomStatus.ACTIVE);
        assertThrows(IllegalArgumentException.class, () -> roomService.updateRoom(staffUser, r));
    }

    @Test
    void testUpdateRoom_noPermission_throws() {
        Room r = new StandardRoom("R101", "Pine Standard", 101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE);
        assertDoesNotThrow(() -> roomService.addRoom(staffUser, r));
        assertThrows(SecurityException.class, () -> roomService.updateRoom(guestUser, r));
    }

    // -------------------------------------------------------------------------
    // RoomService – retireRoom
    // -------------------------------------------------------------------------

    @Test
    void testRetireRoom_success() {
        Room r = new StandardRoom("R101", "Pine Standard", 101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE);
        assertDoesNotThrow(() -> roomService.addRoom(staffUser, r));

        assertDoesNotThrow(() -> roomService.retireRoom(staffUser, "R101"));
        assertEquals(RoomStatus.RETIRED, fakeDb.get(0).getStatus());
    }

    @Test
    void testRetireRoom_nonExistent_throws() {
        assertThrows(IllegalArgumentException.class, () -> roomService.retireRoom(staffUser, "GHOST"));
    }

    @Test
    void testRetireRoom_noPermission_throws() {
        Room r = new StandardRoom("R101", "Pine Standard", 101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE);
        assertDoesNotThrow(() -> roomService.addRoom(staffUser, r));
        assertThrows(SecurityException.class, () -> roomService.retireRoom(guestUser, "R101"));
        assertThrows(SecurityException.class, () -> roomService.retireRoom(null, "R101"));
    }

    // -------------------------------------------------------------------------
    // RoomService – search and metrics (single pass)
    // -------------------------------------------------------------------------

    @Test
    void testSearchAndMetrics() {
        assertDoesNotThrow(() -> {
            roomService.addRoom(staffUser, new StandardRoom("R101", "Pine Standard",  101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE));
            roomService.addRoom(staffUser, new DeluxeRoom  ("R201", "Valley Deluxe",  201, 28000.0, 3, 44, "dlx.jpg", RoomStatus.MAINTENANCE));
            roomService.addRoom(staffUser, new SuiteRoom   ("R301", "Estate Suite",   301, 46000.0, 4, 62, "ste.jpg", RoomStatus.RETIRED));
        });

        List<Room> all = roomService.getAllRooms();
        RoomService.RoomMetrics metrics = roomService.getMetrics(all);

        assertEquals(3, metrics.getTotal());
        assertEquals(1, metrics.getActive());
        assertEquals(1, metrics.getMaintenance());
        assertEquals(1, metrics.getRetired());
    }

    @Test
    void testSearchByStatus() {
        assertDoesNotThrow(() -> {
            roomService.addRoom(staffUser, new StandardRoom("R101", "Pine Standard", 101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE));
            roomService.addRoom(staffUser, new DeluxeRoom  ("R201", "Valley Deluxe", 201, 28000.0, 3, 44, "dlx.jpg", RoomStatus.MAINTENANCE));
        });

        List<Room> activeRooms = roomService.searchRooms(null, null, "active", null, null);
        assertEquals(1, activeRooms.size());
        assertEquals("R101", activeRooms.get(0).getRoomId());

        List<Room> maintenanceRooms = roomService.searchRooms(null, null, "maintenance", null, null);
        assertEquals(1, maintenanceRooms.size());
        assertEquals("R201", maintenanceRooms.get(0).getRoomId());
    }

    @Test
    void testSearchByKeyword() {
        assertDoesNotThrow(() -> {
            roomService.addRoom(staffUser, new StandardRoom("R101", "Pine Standard", 101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE));
            roomService.addRoom(staffUser, new DeluxeRoom  ("R201", "Valley Deluxe", 201, 28000.0, 3, 44, "dlx.jpg", RoomStatus.ACTIVE));
        });

        List<Room> results = roomService.searchRooms("pine", null, null, null, null);
        assertEquals(1, results.size());
        assertEquals("R101", results.get(0).getRoomId());
    }

    @Test
    void testSearchByMaxPrice() {
        assertDoesNotThrow(() -> {
            roomService.addRoom(staffUser, new StandardRoom("R101", "Pine Standard", 101, 18500.0, 2, 32, "std.jpg", RoomStatus.ACTIVE));
            roomService.addRoom(staffUser, new DeluxeRoom  ("R201", "Valley Deluxe", 201, 28000.0, 3, 44, "dlx.jpg", RoomStatus.ACTIVE));
        });

        List<Room> affordable = roomService.searchRooms(null, null, null, 20000.0, null);
        assertEquals(1, affordable.size());
        assertEquals("R101", affordable.get(0).getRoomId());
    }
}
