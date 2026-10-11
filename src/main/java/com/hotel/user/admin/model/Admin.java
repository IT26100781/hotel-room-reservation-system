package com.hotel.user.admin.model;

import com.hotel.user.Role;
import com.hotel.util.FileHelper;

public class Admin extends Staff{
    private final Role role = Role.ADMIN;

    public Admin(String id, String name, String email, String passwordHash,boolean status) {
        super(id,name,email,passwordHash,status);

    }
    public Admin(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);

    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }


    @Override public boolean canManageRooms() {
        return true;
    }
    @Override public boolean canViewStaffUsers() {
        return true;
    }
    @Override public boolean canViewAdminUsers() {
        return true;
    }
    @Override public boolean canManageStaffUsers() {return true;}
    @Override public boolean canManageAdminUsers() {return true;}

    @Override public boolean canModerateReviews() {
        return true;
    }


}
