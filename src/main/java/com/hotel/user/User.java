package com.hotel.user;

/**
 * Shared base class for everyone who can log in.
 *
 * Hierarchy:  User -> Guest -> (RegularGuest, VIPGuest)
 *             User -> Staff -> Admin
 *
 * Permission methods are DEFAULT-DENY: they all return false here, and subclasses
 * override only the ones they are allowed. Servlets must call these on the server
 * before every protected action (never rely on hiding buttons in the JSP).
 */
public abstract class User {

    protected String id;
    protected String name;
    protected String email;
    protected String passwordHash;   // stored as "salt:hash", never the raw password

    protected User(String id, String name, String email, String passwordHash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    /** Each subclass reports its own role. */
    public abstract Role getRole();


    // ---- permissions (default-deny) ----

    public boolean canManageRooms() {
        return false;
    }

    public boolean canManageGuestUsers() {
        return false;
    }
    public boolean canManageStaffUsers() {return false;}
    public boolean canManageAdminUsers() {return false;}

    public boolean canRecordPayment() {
        return false;
    }

    public boolean canBookForThemselves() {return false;}
    public boolean canManageAnyBooking() {
        return false;
    }

    public boolean canCheckInGuests() {
        return false;
    }
    public boolean canCheckOutGuests() {return false;}

    public boolean canAddReviews() {return false;}
    public boolean canModerateReviews() {
        return false;
    }

    public boolean canViewStaffUsers() {return false;}
    public boolean canViewAdminUsers() {return false;}
    public boolean canViewGuestUsers() {return false;}


    // ---- getters / setters ----

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
