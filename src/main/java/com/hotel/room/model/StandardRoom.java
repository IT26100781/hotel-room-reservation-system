package com.hotel.room.model;

public class StandardRoom extends Room {
    public StandardRoom(String roomId, String roomName, int roomNumber, double baseRate, int capacity, int roomSize, String imageUrl, String status) {
        super(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
    }

    @Override
    public String[] getAmenities() {
        return new String[]{
                "Queen Bed / Twin Beds",
                "Garden View",
                "Rain Shower",
                "Free Fast Wi-Fi",
                "In-room Safe",
                "Electric Kettle & Tea/Coffee"
        };
    }

    @Override
    public String getDescription() {
        return "Queen bed, garden view and a rain shower. Calm and simple.";
    }
}