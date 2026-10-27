package com.hotel.user.admin.model;

import com.hotel.user.Role;
import com.hotel.user.User;


public class Staff extends User {
    private final Role role = Role.STAFF;
    protected boolean status; // true if Active false if Inactive

    public Staff(String id, String name, String email, String passwordHash,boolean status) {
        super(id,name,email,passwordHash);
        this.status = status;
    }
    public Staff(String id, String name, String email, String passwordHash) {
        super(id,name,email,passwordHash);
        this.status = true;
    }

    @Override
    public Role getRole() {
        return Role.STAFF;
    }


    // getters and setters

    public boolean isActive() {
        return status;
    }

    public void setActive(boolean status) {
        this.status = status;
    }

    @Override public boolean canViewGuestUsers() {return true;}
    @Override public boolean canManageGuestUsers() {
        return true;
    }


    @Override public boolean canRecordPayment() {
        return true;
    }


    @Override public boolean canManageAnyBooking() {
        return true;
    }

    @Override public boolean canCheckInGuests() {
        return true;
    }
    @Override public boolean canCheckOutGuests() {return true;}



}
