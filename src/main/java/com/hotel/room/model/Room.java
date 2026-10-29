package com.hotel.room.model;
public abstract class Room {
    private String roomId;
    private String roomName;
    private int roomNumber;
    private double baseRate;
    private int capacity;
    private int roomSize;
    private String imageUrl;
    private String status;

    public Room(String roomId, String roomName, int roomNumber, double baseRate, int capacity, int roomSize, String imageUrl, String status) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomNumber = roomNumber;
        this.baseRate = baseRate;
        this.capacity = capacity;
        this.roomSize = roomSize;
        this.imageUrl = imageUrl;
        this.status = status != null ? status : "active";
    }

    public abstract String[] getAmenities();
    public abstract String getDescription();

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

    public String getType() {
        return getClass().getSimpleName();
    }

    public String getTypeDisplayName() {
        return getClass().getSimpleName().replace("Room", "");
    }

    public String getFormattedRate() {
        return String.format("%,.2f", baseRate);
    }

    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }

    public boolean isMaintenance() {
        return "maintenance".equalsIgnoreCase(status);
    }

    public boolean isRetired() {
        return "retired".equalsIgnoreCase(status);
    }

    public String toFileString() {
        return String.format("%s|%s|%s|%d|%.2f|%d|%d|%s|%s",
                roomId,
                getClass().getSimpleName(),
                roomName,
                roomNumber,
                baseRate,
                capacity,
                roomSize,
                imageUrl != null ? imageUrl : "",
                status);
    }
}
