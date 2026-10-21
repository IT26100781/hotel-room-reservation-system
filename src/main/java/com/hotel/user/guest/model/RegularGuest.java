package com.hotel.user.guest.model;

public class RegularGuest extends Guest {
    public RegularGuest(String id, String name, String email, String passwordHash) {
        super(id,name,email,passwordHash);
        this.guestType = "Regular";

    }
    @Override public double getDiscount() {
        return 0.00;
    }
}
