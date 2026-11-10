package com.hotel.user.admin.model;

import com.hotel.user.Role;
import com.hotel.user.User;

/**
 * Staff class is the class that manages internal system operations
 *
 * Hierarchy:  User -> Staff -> Admin
 *
 * Staff is able to view and manage guests, check in and check out guests and collect payments
 */
public class Staff extends User {
    protected boolean active; // true if Active false if Inactive
    protected boolean mustChangePassword;

    public Staff(String id, String name, String email, String passwordHash, boolean active) {
        super(id, name, email, passwordHash);
        this.active = active;
    }

    public Staff(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);
        this.active = true;
    }

    @Override
    public Role getRole() {
        return Role.STAFF;
    }

    // getters and setters

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean canViewGuestUsers() {
        return true;
    }

    @Override
    public boolean canManageGuestUsers() {
        return true;
    }

    @Override
    public boolean canRecordPayment() {
        return true;
    }

    @Override
    public boolean canManageAnyBooking() {
        return true;
    }

    @Override
    public boolean canCheckInGuests() {
        return true;
    }

    @Override
    public boolean canCheckOutGuests() {
        return true;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
}
