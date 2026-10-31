package com.hotel.user.guest.model;

public class VipGuest extends Guest {
    public VipGuest(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);
        this.guestType = "VIP";

    }

    @Override public double getDiscount() { return 0.10; }
}
