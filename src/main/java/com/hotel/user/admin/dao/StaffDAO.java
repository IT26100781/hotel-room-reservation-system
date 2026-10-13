package com.hotel.user.admin.dao;

import com.hotel.user.admin.model.Staff;
import com.hotel.util.FileHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    public List<Staff> findAllStaff() throws IOException {
        List<Staff> staff = new ArrayList<>();
        List<String> lines =  FileHelper.readLines("Staff.txt");
        for (String line : lines) {
            Staff s = Staff.fromFileString(line);
            if(s != null){
                staff.add(s);
            }
        }
        return staff;
    }


}
