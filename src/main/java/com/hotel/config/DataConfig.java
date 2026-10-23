package com.hotel.config;

/**
 * Shared constants for file storage. Every DAO builds its file path from these,
 * so the data folder and file names are defined in exactly one place.
 */
public final class DataConfig {

    /** Folder holding all data files, relative to the project root (working directory). */
    public static final String DATA_DIR = "data";

    /** Field separator used in every data file. */
    public static final String DELIMITER = "|";

    public static final String GUESTS_FILE   = "guests.txt";
    public static final String ROOMS_FILE    = "rooms.txt";
    public static final String BOOKINGS_FILE = "bookings.txt";
    public static final String PAYMENTS_FILE = "payments.txt";
    public static final String STAFF_FILE    = "staff.txt";
    public static final String REVIEWS_FILE  = "reviews.txt";

    private DataConfig() {
        // constants only, never instantiated
    }
}
