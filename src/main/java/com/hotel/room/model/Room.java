package com.hotel.room.model;

public class Room {
    protected String roomId;
    protected String type;
    protected double rate;
    protected int capacity;
    protected String[] amenities;
    protected String status; //  "active", "maintenance", "retired"

    public Room() {
    }

    // Full Parameterized Constructor
    public Room(String roomId, String type, double rate, int capacity, String[] amenities, String status) {
        this.roomId = roomId;
        this.type = type;
        this.rate = rate;
        this.capacity = capacity;
        this.amenities = amenities;
        this.status = status;
    }

    // Getters and Setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String[] getAmenities() {
        return amenities;
    }

    public void setAmenities(String[] amenities) {
        this.amenities = amenities;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getDescription() {
        return "Room ID: " + roomId + " | Type: " + type + " | Rate: $" + rate + " | Status: " + status;
    }

    public double calculateRate() {
        return this.rate;
    }
}