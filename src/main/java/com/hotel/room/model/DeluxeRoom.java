package com.hotel.room.model;

public class DeluxeRoom extends Room {
    public DeluxeRoom(String roomId, String roomName, int roomNumber, double baseRate, int capacity, int roomSize, String imageUrl, String status) {
        super(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
    }

    @Override
    public String[] getAmenities() {
        return new String[]{
                "King Bed & Daybed",
                "Private Valley Balcony",
                "Deep Bathtub",
                "Minibar & Refreshments",
                "Free Fast Wi-Fi",
                "Espresso Machine"
        };
    }

    @Override
    public String getDescription() {
        return "King bed, private valley balcony, a deep bathtub and a daybed.";
    }
}