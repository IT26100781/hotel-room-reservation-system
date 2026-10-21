package com.hotel.user.guest.model;

import com.hotel.user.Role;
import com.hotel.user.User;

public class Guest extends User {

    protected String guestType;

    public Guest(String id, String name, String email, String passwordHash) {
        super(id,name,email,passwordHash);
    }
    @Override public Role getRole(){
        return Role.GUEST;
    }
    @Override public String toFileString(){
        return "";
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



}
