package com.hotel.room.model;

/**
 * Abstract base class representing a hotel room.
 * Subclasses (StandardRoom, DeluxeRoom, SuiteRoom) override amenities, descriptions,
 * maximum capacities, and stay pricing calculations.
 */
public abstract class Room {
    private String roomId;
    private String roomName;
    private int roomNumber;
    private double baseRate;
    private int capacity;
    private int roomSize;
    private String imageUrl;
    private RoomStatus status;

    public Room(String roomId, String roomName, int roomNumber, double baseRate, int capacity, int roomSize, String imageUrl, RoomStatus status) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomNumber = roomNumber;
        this.baseRate = baseRate;
        this.capacity = capacity;
        this.roomSize = roomSize;
        this.imageUrl = imageUrl;
        this.status = status != null ? status : RoomStatus.ACTIVE;
    }

    public Room(String roomId, String roomName, int roomNumber, double baseRate, int capacity, int roomSize, String imageUrl, String statusStr) {
        this(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, RoomStatus.fromString(statusStr));
    }

    /**
     * Factory method for creating a Room subclass instance based on exact type string.
     */
    public static Room create(String type, String roomId, String roomName, int roomNumber,
                              double baseRate, int capacity, int roomSize, String imageUrl, RoomStatus status) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Room type cannot be empty");
        }
        switch (type.trim()) {
            case "StandardRoom":
                return new StandardRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
            case "DeluxeRoom":
                return new DeluxeRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
            case "SuiteRoom":
                return new SuiteRoom(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
            default:
                throw new IllegalStateException("Unknown room type: " + type + ". Expected StandardRoom, DeluxeRoom, or SuiteRoom.");
        }
    }

    public static Room create(String type, String roomId, String roomName, int roomNumber,
                              double baseRate, int capacity, int roomSize, String imageUrl, String statusStr) {
        return create(type, roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, RoomStatus.fromString(statusStr));
    }

    public abstract String[] getAmenities();
    public abstract String getDescription();

    /**
     * Calculates the total price for a given number of nights.
     * Subclasses can override this to apply special packages, inclusive services, or multi-night discounts.
     */
    public double calculateTotalRate(int nights) {
        if (nights <= 0) {
            return 0.0;
        }
        return baseRate * nights;
    }

    /**
     * Maximum guest occupancy allowed for this room category.
     */
    public abstract int getMaxAllowedCapacity();

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

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

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
        return status == RoomStatus.ACTIVE;
    }

    public boolean isMaintenance() {
        return status == RoomStatus.MAINTENANCE;
    }

    public boolean isRetired() {
        return status == RoomStatus.RETIRED;
    }
}
