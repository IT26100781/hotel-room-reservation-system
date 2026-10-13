package com.hotel.user.admin.dao;

import com.hotel.config.DataConfig;
import com.hotel.user.admin.model.Staff;
import com.hotel.util.FileHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    public List<Staff> findAllStaff() throws IOException {
        List<Staff> staff = new ArrayList<>();
        List<String> lines =  FileHelper.readLines(DataConfig.STAFF_FILE);
        for (String line : lines) {
            Staff s = Staff.fromFileString(line);
            if(s != null){
                staff.add(s);
            }
        }
        return staff;
    }
    public Staff findById(String id) throws IOException {
        for (Staff b : findAllStaff()) {
            if (b.getId().equals(id)) {
                return b;
            }
        }
        return null;
    }
    public Staff findByEmail(String email) throws IOException {
        for (Staff b : findAllStaff()) {
            if (b.getEmail().equalsIgnoreCase(email)) {
                return b;
            }
        }
        return null;
    }
    public void saveStaff(Staff staff) throws IOException {
        String line = staff.toFileString();

        FileHelper.appendLine(DataConfig.STAFF_FILE, line);

    }
    public boolean updateStaff(Staff updated) throws IOException {
        List<Staff> staff = findAllStaff();
        boolean isFound = false;
        for(int i = 0; i < staff.size(); i++){
            if(staff.get(i).getId().equals(updated.getId())){
                staff.set(i, updated);
                isFound = true;
                break;
            }
        }
        if(!isFound){
            return false;
        }
        writeAll(staff);
        return true;
    }
    public boolean deleteStaff(String id) throws IOException {
        List<Staff> staff = findAllStaff();
        for (int i = 0; i < staff.size(); i++) {
            if (staff.get(i).getId().equals(id)) {
                staff.remove(i);
                writeAll(staff);
                return true;
            }
        }
        return false;

    }
    private void writeAll(List<Staff> staff) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Staff b : staff) {
            lines.add(b.toFileString());
        }
        FileHelper.writeLines(DataConfig.STAFF_FILE,lines);
    }


}
