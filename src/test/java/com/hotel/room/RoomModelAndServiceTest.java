package com.hotel.room;

import com.hotel.room.dao.RoomDAO;
import com.hotel.room.model.DeluxeRoom;
import com.hotel.room.model.Room;
import com.hotel.room.model.StandardRoom;
import com.hotel.room.model.SuiteRoom;
import com.hotel.room.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomModelAndServiceTest {

    private RoomService roomService;
    private List<Room> fakeDb;

    @BeforeEach
    void setUp() {
        fakeDb = new ArrayList<>();
        // Mock RoomDAO for pure unit testing without external database dependency
        RoomDAO mockDao = new RoomDAO() {
            @Override
            public List<Room> findAll() {
                return new ArrayList<>(fakeDb);
            }

            @Override
            public Room findById(String id) {
                return fakeDb.stream().filter(r -> r.getRoomId().equalsIgnoreCase(id)).findFirst().orElse(null);
            }

            @Override
            public Room findByRoomNumber(int roomNumber) {
                return fakeDb.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);
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
                    r.setStatus(status);
                    return true;
                }
                return false;
            }

            @Override
            public boolean existsByRoomNumber(int roomNumber, String excludeRoomId) {
                return fakeDb.stream().anyMatch(r -> r.getRoomNumber() == roomNumber &&
                        (excludeRoomId == null || !r.getRoomId().equalsIgnoreCase(excludeRoomId)));
            }
        };

        roomService = new RoomService(mockDao);
    }

    @Test
    void testRoomPolymorphismAndAmenities() {
        Room standard = new StandardRoom("R101", "Standard Room 101", 101, 18500.0, 2, 32, "std.jpg", "active");
        Room deluxe = new DeluxeRoom("R201", "Deluxe Room 201", 201, 28000.0, 3, 44, "dlx.jpg", "active");
        Room suite = new SuiteRoom("R301", "Estate Suite 301", 301, 46000.0, 4, 62, "ste.jpg", "active");

        assertEquals("Standard", standard.getTypeDisplayName());
        assertEquals("Deluxe", deluxe.getTypeDisplayName());
        assertEquals("Suite", suite.getTypeDisplayName());

        assertTrue(standard.getAmenities().length > 0);
        assertTrue(deluxe.getAmenities().length > 0);
        assertTrue(suite.getAmenities().length > 0);

        assertNotNull(standard.getDescription());
        assertNotNull(deluxe.getDescription());
        assertNotNull(suite.getDescription());
    }

    @Test
    void testAddRoomAndPreventDuplicates() {
        Room r1 = new StandardRoom("R101", "Pine Standard", 101, 18500.0, 2, 32, "std.jpg", "active");
        assertTrue(roomService.addRoom(r1));

        // Duplicate room number should fail
        Room r2 = new DeluxeRoom("R102", "Duplicate Number Room", 101, 25000.0, 3, 40, "dlx.jpg", "active");
        assertFalse(roomService.addRoom(r2));

        // Negative rate should fail
        Room r3 = new StandardRoom("R103", "Invalid Rate", 103, -100.0, 2, 30, "std.jpg", "active");
        assertFalse(roomService.addRoom(r3));
    }

    @Test
    void testRoomSearchAndCounters() {
        roomService.addRoom(new StandardRoom("R101", "Pine Standard", 101, 18500.0, 2, 32, "std.jpg", "active"));
        roomService.addRoom(new DeluxeRoom("R201", "Valley Deluxe", 201, 28000.0, 3, 44, "dlx.jpg", "maintenance"));
        roomService.addRoom(new SuiteRoom("R301", "Estate Suite", 301, 46000.0, 4, 62, "ste.jpg", "retired"));

        assertEquals(3, roomService.getTotalCount());
        assertEquals(1, roomService.getActiveCount());
        assertEquals(1, roomService.getMaintenanceCount());
        assertEquals(1, roomService.getRetiredCount());

        List<Room> activeRooms = roomService.searchRooms(null, null, "active", null, null);
        assertEquals(1, activeRooms.size());
        assertEquals("R101", activeRooms.get(0).getRoomId());
    }
}
