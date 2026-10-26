package com.hotel.room.service;

import com.hotel.room.dao.RoomDAO;
import com.hotel.room.model.DeluxeRoom;
import com.hotel.room.model.Room;
import com.hotel.room.model.StandardRoom;
import com.hotel.room.model.SuiteRoom;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for room business operations.
 * Delegates all database persistence to RoomDAO.
 */
public class RoomService {

    private final RoomDAO roomDAO;

    public RoomService() {
        this.roomDAO = new RoomDAO();
    }

    public RoomService(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    /**
     * Retrieve all rooms.
     */
    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }

    /**
     * Retrieve a room by its ID.
     */
    public Room getRoomById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return roomDAO.findById(id.trim());
    }

    //     get a room by its room number.

    public Room getRoomByNumber(int roomNumber) {
        return roomDAO.findByRoomNumber(roomNumber);
    }
    //    Filter rooms by search term, type, status, max price, and min capacity.
    public List<Room> searchRooms(String searchTerm, String type, String status, Double maxPrice, Integer minCapacity) {
        List<Room> allRooms = getAllRooms();
        List<Room> filtered = new ArrayList<>();

        for (Room room : allRooms) {
            // Search keyword filter (by room name, id, or number)
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String term = searchTerm.trim().toLowerCase();
                boolean matchesKeyword = room.getRoomName().toLowerCase().contains(term)
                        || room.getRoomId().toLowerCase().contains(term)
                        || String.valueOf(room.getRoomNumber()).contains(term);
                if (!matchesKeyword) {
                    continue;
                }
            }

            // Status filter
            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
                if (!status.equalsIgnoreCase(room.getStatus())) {
                    continue;
                }
            }

            // Type filter
            if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type)) {
                String normalizedType = type.trim().toLowerCase();
                String roomType = room.getType().toLowerCase();
                if (!roomType.contains(normalizedType)) {
                    continue;
                }
            }

            // Max price filter
            if (maxPrice != null && room.getBaseRate() > maxPrice) {
                continue;
            }

            // Min capacity filter
            if (minCapacity != null && room.getCapacity() < minCapacity) {
                continue;
            }

            filtered.add(room);
        }
        return filtered;
    }

    //    Add a new room after business validation.
    public boolean addRoom(Room room) {
        if (room == null) {
            return false;
        }

        // Validate room number
        if (room.getRoomNumber() <= 0) {
            return false;
        }

        // Prevent duplicate room numbers
        if (roomDAO.existsByRoomNumber(room.getRoomNumber(), null)) {
            return false;
        }

        // Auto-generate Room ID if not provided (e.g. R + room number: R204)
        if (room.getRoomId() == null || room.getRoomId().trim().isEmpty()) {
            room.setRoomId("R" + room.getRoomNumber());
        }

        // Check if ID is already in use
        if (roomDAO.findById(room.getRoomId()) != null) {
            return false;
        }

        // Validate rate and capacity
        if (room.getBaseRate() < 0 || room.getCapacity() <= 0) {
            return false;
        }

        if (room.getStatus() == null || room.getStatus().trim().isEmpty()) {
            room.setStatus("active");
        }

        return roomDAO.save(room);
    }

    //    Update an existing room after business validation.

    public boolean updateRoom(Room room) {
        if (room == null || room.getRoomId() == null) {
            return false;
        }

        // Check that room exists
        Room existing = roomDAO.findById(room.getRoomId());
        if (existing == null) {
            return false;
        }

        // Validate uniqueness of room number
        if (roomDAO.existsByRoomNumber(room.getRoomNumber(), room.getRoomId())) {
            return false;
        }

        if (room.getBaseRate() < 0 || room.getCapacity() <= 0) {
            return false;
        }

        return roomDAO.update(room);
    }

    //    add a room.

    public boolean retireRoom(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return roomDAO.updateStatus(id.trim(), "retired");
    }

//    Permanently delete a room.
    public boolean deleteRoom(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return roomDAO.delete(id.trim());
    }
//    Helper to instantiate a Room subclass based on type name.

    public Room createRoomInstance(String type, String roomId, String roomName, int roomNumber,
                                   double baseRate, int capacity, int roomSize, String imageUrl, String status) {
        if (type == null) {
            type = "StandardRoom";
        }
        String normalized = type.trim().toLowerCase();
        if (normalized.contains("suite")) {
            return new SuiteRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
        } else if (normalized.contains("deluxe")) {
            return new DeluxeRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
        } else {
            return new StandardRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
        }
    }

    // Dashboard Statistics Helpers
    public int getTotalCount() {
        return getAllRooms().size();
    }

    public int getActiveCount() {
        int count = 0;
        for (Room r : getAllRooms()) {
            if ("active".equalsIgnoreCase(r.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public int getMaintenanceCount() {
        int count = 0;
        for (Room r : getAllRooms()) {
            if ("maintenance".equalsIgnoreCase(r.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public int getRetiredCount() {
        int count = 0;
        for (Room r : getAllRooms()) {
            if ("retired".equalsIgnoreCase(r.getStatus())) {
                count++;
            }
        }
        return count;
    }
}
