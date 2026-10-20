package com.hotel.room.model;

public abstract class Room {
    private String roomId;
    private String roomName; // එකතු කළා: e.g. "Estate Suite", "Deluxe Valley Room"
    private int roomNumber;
    private double baseRate;
    private int capacity;
    private int roomSize;    // එකතු කළා: e.g. 24, 34, 62 (m²)
    private String imageUrl; // එකතු කළා: image link
    private String status;   // "active", "maintenance", "retired"

    public Room(String roomId, String roomName, int roomNumber, double baseRate, int capacity, int roomSize, String imageUrl, String status) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomNumber = roomNumber;
        this.baseRate = baseRate;
        this.capacity = capacity;
        this.roomSize = roomSize;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    // Abstract methods
    public abstract String[] getAmenities();
    public abstract String getDescription();

    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public double getBaseRate() { return baseRate; }
    public void setBaseRate(double baseRate) { this.baseRate = baseRate; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getRoomSize() { return roomSize; }
    public void setRoomSize(int roomSize) { this.roomSize = roomSize; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // File saving helper (CSV format updated)
    public String toFileString() {
        return String.format("%s,%s,%s,%d,%.2f,%d,%d,%s,%s",
                roomId,
                getClass().getSimpleName(),
                roomName,
                roomNumber,
                baseRate,
                capacity,
                roomSize,
                imageUrl,
                status);
    }
}