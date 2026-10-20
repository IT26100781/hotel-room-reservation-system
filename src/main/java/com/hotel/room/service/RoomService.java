package com.hotel.room.service;

import com.hotel.room.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoomService {

    private static final String FILE_PATH = "rooms.txt";

    // 1. CREATE
    public boolean addRoom(Room room) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(room.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. READ ALL
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return rooms;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 9) continue; // Columns 9ක් තිබේදැයි පරීක්ෂා කිරීම

                String roomId = parts[0].trim();
                String roomType = parts[1].trim();
                String roomName = parts[2].trim();
                int roomNumber = Integer.parseInt(parts[3].trim());
                double baseRate = Double.parseDouble(parts[4].trim());
                int capacity = Integer.parseInt(parts[5].trim());
                int roomSize = Integer.parseInt(parts[6].trim());
                String imageUrl = parts[7].trim();
                String status = parts[8].trim();

                Room room = createRoomInstance(roomType, roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
                if (room != null) {
                    rooms.add(room);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    // Instance Creation Helper
    private Room createRoomInstance(String type, String roomId, String roomName, int roomNum, double rate, int cap, int size, String img, String status) {
        switch (type) {
            case "StandardRoom":
                return new StandardRoom(roomId, roomName, roomNum, rate, cap, size, img, status);
            case "DeluxeRoom":
                return new DeluxeRoom(roomId, roomName, roomNum, rate, cap, size, img, status);
            case "SuiteRoom":
                return new SuiteRoom(roomId, roomName, roomNum, rate, cap, size, img, status);
            default:
                return null;
        }
    }

    // 3. SEARCH & FILTER
    public List<Room> searchRooms(String type, Double maxPrice, Integer minCapacity) {
        List<Room> allRooms = getAllRooms();
        List<Room> filteredRooms = new ArrayList<>();

        for (Room room : allRooms) {
            if ("retired".equalsIgnoreCase(room.getStatus())) {
                continue;
            }

            boolean matchesType = (type == null || type.isEmpty() || "ALL".equalsIgnoreCase(type) || room.getClass().getSimpleName().equalsIgnoreCase(type));
            boolean matchesPrice = (maxPrice == null || room.getBaseRate() <= maxPrice);
            boolean matchesCap = (minCapacity == null || room.getCapacity() >= minCapacity);

            if (matchesType && matchesPrice && matchesCap) {
                filteredRooms.add(room);
            }
        }
        return filteredRooms;
    }

    // 4. UPDATE
    public boolean updateRoom(Room updatedRoom) {
        List<Room> rooms = getAllRooms();
        boolean found = false;

        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomId().equalsIgnoreCase(updatedRoom.getRoomId())) {
                rooms.set(i, updatedRoom);
                found = true;
                break;
            }
        }

        return found && saveAllRoomsToFile(rooms);
    }

    // 5. SOFT DELETE
    public boolean deleteRoom(String roomId) {
        List<Room> rooms = getAllRooms();
        boolean found = false;

        for (Room room : rooms) {
            if (room.getRoomId().equalsIgnoreCase(roomId)) {
                room.setStatus("retired");
                found = true;
                break;
            }
        }

        return found && saveAllRoomsToFile(rooms);
    }

    private boolean saveAllRoomsToFile(List<Room> rooms) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Room room : rooms) {
                writer.write(room.toFileString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}