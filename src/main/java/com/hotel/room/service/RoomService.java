package com.hotel.room.service;

import com.hotel.room.dao.RoomDAO;
import com.hotel.room.model.Room;
import com.hotel.room.model.RoomStatus;
import com.hotel.user.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for room business operations.
 * Enforces business validation rules and permission checks matching the team's StaffService pattern.
 */
@Service
public class RoomService {

    private final RoomDAO roomDAO;

    public RoomService(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    /**
     * DTO containing summary counts calculated in a single pass.
     */
    public static class RoomMetrics {
        private final int total;
        private final int active;
        private final int maintenance;
        private final int retired;

        public RoomMetrics(int total, int active, int maintenance, int retired) {
            this.total = total;
            this.active = active;
            this.maintenance = maintenance;
            this.retired = retired;
        }

        public int getTotal() { return total; }
        public int getActive() { return active; }
        public int getMaintenance() { return maintenance; }
        public int getRetired() { return retired; }
    }

    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }

    public Room getRoomById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return roomDAO.findById(id.trim());
    }

    public Room getRoomByNumber(int roomNumber) {
        if (roomNumber <= 0) {
            return null;
        }
        return roomDAO.findByRoomNumber(roomNumber);
    }

    /**
     * Calculates dashboard metric counts in a single pass over the rooms list (1 query).
     */
    public RoomMetrics getMetrics(List<Room> rooms) {
        int active = 0;
        int maintenance = 0;
        int retired = 0;
        for (Room r : rooms) {
            if (r.getStatus() == RoomStatus.ACTIVE) {
                active++;
            } else if (r.getStatus() == RoomStatus.MAINTENANCE) {
                maintenance++;
            } else if (r.getStatus() == RoomStatus.RETIRED) {
                retired++;
            }
        }
        return new RoomMetrics(rooms.size(), active, maintenance, retired);
    }

    /**
     * Filter rooms by search keyword, room type, status, max price, and min capacity.
     */
    public List<Room> searchRooms(String searchTerm, String type, String status, Double maxPrice, Integer minCapacity) {
        List<Room> allRooms = getAllRooms();
        List<Room> filtered = new ArrayList<>();

        for (Room room : allRooms) {
            // Search keyword filter
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
                if (!status.equalsIgnoreCase(room.getStatus().getValue())) {
                    continue;
                }
            }

            // Room type filter
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

    /**
     * Validates room business rules. Throws IllegalArgumentException on invalid input.
     */
    private void validateRoom(Room room, boolean isUpdate) {
        if (room == null) {
            throw new IllegalArgumentException("Room details cannot be null");
        }
        if (room.getRoomNumber() <= 0) {
            throw new IllegalArgumentException("Room number must be greater than 0");
        }
        if (room.getRoomName() == null || room.getRoomName().trim().isEmpty()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }
        if (room.getRoomName().trim().length() > 100) {
            throw new IllegalArgumentException("Room name cannot exceed 100 characters");
        }
        if (room.getBaseRate() <= 0) {
            throw new IllegalArgumentException("Rate per night must be greater than 0");
        }
        if (room.getCapacity() < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1 guest");
        }
        if (room.getCapacity() > room.getMaxAllowedCapacity()) {
            throw new IllegalArgumentException("Maximum capacity for a " + room.getTypeDisplayName()
                    + " is " + room.getMaxAllowedCapacity() + " guests");
        }
        if (room.getRoomSize() <= 0) {
            throw new IllegalArgumentException("Room size must be greater than 0");
        }
        if (room.getStatus() == null) {
            throw new IllegalArgumentException("Room status must be selected");
        }

        // Duplicate room number check
        String excludeId = isUpdate ? room.getRoomId() : null;
        if (roomDAO.existsByRoomNumber(room.getRoomNumber(), excludeId)) {
            throw new IllegalArgumentException("Room number " + room.getRoomNumber() + " is already taken");
        }
    }

    /**
     * Add a new room with authorization and validation checks.
     */
    public void addRoom(User currentUser, Room room) {
        if (currentUser == null || !currentUser.canManageRooms()) {
            throw new SecurityException("You do not have permission to add rooms");
        }

        // Auto-generate ID if needed: R + room number (e.g. R204)
        if (room.getRoomId() == null || room.getRoomId().trim().isEmpty()) {
            room.setRoomId("R" + room.getRoomNumber());
        }

        if (roomDAO.findById(room.getRoomId()) != null) {
            throw new IllegalArgumentException("Room ID " + room.getRoomId() + " already exists");
        }

        validateRoom(room, false);

        boolean saved = roomDAO.save(room);
        if (!saved) {
            throw new IllegalStateException("Failed to insert room record into the database");
        }
    }

    /**
     * Update an existing room with authorization and validation checks.
     */
    public void updateRoom(User currentUser, Room room) {
        if (currentUser == null || !currentUser.canManageRooms()) {
            throw new SecurityException("You do not have permission to update rooms");
        }
        if (room.getRoomId() == null || room.getRoomId().trim().isEmpty()) {
            throw new IllegalArgumentException("Room ID is required for updates");
        }

        Room existing = roomDAO.findById(room.getRoomId());
        if (existing == null) {
            throw new IllegalArgumentException("Room with ID " + room.getRoomId() + " does not exist");
        }

        validateRoom(room, true);

        boolean updated = roomDAO.update(room);
        if (!updated) {
            throw new IllegalStateException("Failed to update room record in the database");
        }
    }

    /**
     * Soft-delete/retire a room so existing reservations remain valid.
     */
    public void retireRoom(User currentUser, String id) {
        if (currentUser == null || !currentUser.canManageRooms()) {
            throw new SecurityException("You do not have permission to retire rooms");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be empty");
        }
        Room room = roomDAO.findById(id.trim());
        if (room == null) {
            throw new IllegalArgumentException("Room with ID " + id + " does not exist");
        }

        boolean updated = roomDAO.updateStatus(id.trim(), RoomStatus.RETIRED.getValue());
        if (!updated) {
            throw new IllegalStateException("Failed to retire room " + id);
        }
    }
}
