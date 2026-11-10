package com.hotel.room.model;

/**
 * Enumeration representing the operational status of a hotel room.
 */
public enum RoomStatus {
    ACTIVE("active", "Active"),
    MAINTENANCE("maintenance", "Maintenance"),
    RETIRED("retired", "Retired");

    private final String value;
    private final String label;

    RoomStatus(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static RoomStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return ACTIVE;
        }
        for (RoomStatus status : values()) {
            if (status.value.equalsIgnoreCase(text.trim()) || status.name().equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid room status '" + text + "'. Must be active, maintenance, or retired.");
    }

    @Override
    public String toString() {
        return value;
    }
}
