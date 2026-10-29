package com.hotel.user.guest.model;

import com.hotel.user.Role;
import com.hotel.user.User;

public class Guest extends User {

    protected String guestType;
    protected Boolean isActive = true;

    public Guest(String id, String name, String email, String passwordHash) {
        super(id,name,email,passwordHash);
    }
    @Override public Role getRole(){
        return Role.GUEST;
    }
    public String getGuestType(){
        return guestType;
    }
    public void setGuestType(String guestType){
        this.guestType = guestType;
    }
    public double getDiscount() {
        return 0;
    }

    public Boolean getActive() {
        return isActive;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    @Override public boolean canBookForThemselves() { return true; }
    @Override public boolean canAddReviews() { return true; }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
