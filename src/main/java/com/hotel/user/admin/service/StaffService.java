package com.hotel.user.admin.service;

import com.hotel.user.admin.dao.StaffDAO;

import java.io.IOException;

public class StaffService {
    private final StaffDAO staffDAO;
    public StaffService(StaffDAO staffDAO) {
        this.staffDAO = staffDAO;
    }

}
