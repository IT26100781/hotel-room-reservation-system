package com.hotel.room.model;

public class SuiteRoom extends Room {
    public SuiteRoom(String roomId, String roomName, int roomNumber, double baseRate, int capacity, int roomSize, String imageUrl, String status) {
        super(roomId, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
    }

    @Override
public String[] getAmenities() {
        return new String[]{
                "Panoramic Terrace",
                "Separate Living Room",
                "Soaking Tub by Window",
                "Breakfast & Afternoon Tea Included",
                "Station Pickup",
                "Fast Wi-Fi",
                "Smart TV",
                "Fireplace for Cool Nights"
        };
    }

    @Override
    public String getDescription() {
        return "Our largest rooms sit on the top floor of the old planter's bungalow. A separate living room opens onto a panoramic terrace with daily afternoon tea service.";
    }}