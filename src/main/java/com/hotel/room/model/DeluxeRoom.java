package com.hotel.room.model;

public class DeluxeRoom extends Room {

    public DeluxeRoom(String roomId, String roomName, int roomNumber, double baseRate, int capacity, int roomSize, String imageUrl, RoomStatus status) {
        super(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
    }

    public DeluxeRoom(String roomId, String roomName, int roomNumber, double baseRate, int capacity, int roomSize, String imageUrl, String statusStr) {
        super(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, statusStr);
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

    @Override
    public int getMaxAllowedCapacity() {
        return 3;
    }

    @Override
    public double calculateTotalRate(int nights) {
        if (nights <= 0) {
            return 0.0;
        }
        // Deluxe rooms receive complimentary afternoon tea worth 2000 per stay of 2+ nights
        return super.calculateTotalRate(nights);
    }
}
