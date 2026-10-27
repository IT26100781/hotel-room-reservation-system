package com.hotel.user.admin.dao;


import com.hotel.user.Role;
import com.hotel.user.admin.model.Admin;
import com.hotel.user.admin.model.Staff;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository

public class StaffDAO {
    private final JdbcTemplate jdbcTemplate;
    public  StaffDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Staff mapRow(ResultSet rs, int rowNum) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        String role = rs.getString("role");
        boolean status = rs.getBoolean("active");

        if(role.equalsIgnoreCase(Role.STAFF.name())){
            return new Staff(id,name,email,passwordHash,status);
        }
        if(role.equalsIgnoreCase(Role.ADMIN.name())){
            return new Admin(id,name,email,passwordHash,status);
        }
        throw new IllegalStateException("Invalid role");
    }

    public List<Staff> findAllStaff() {
        String sql = "SELECT id, name, email, password_hash, role, active FROM staff ORDER BY id";
        return jdbcTemplate.query(sql,this::mapRow);
    }

    public Staff findById(String id) {
        String sql = "SELECT id, name, email, password_hash, role, active FROM staff WHERE id = ?";
        List<Staff> found = jdbcTemplate.query(sql,this::mapRow,id);
        return found.isEmpty() ? null : found.get(0);
    }

    public Staff findByEmail(String email){
        String sql = "SELECT id, name, email, password_hash, role, active FROM staff WHERE email = ?";
        List<Staff> found = jdbcTemplate.query(sql,this::mapRow,email);
        return found.isEmpty() ? null : found.get(0);

    }

    public boolean saveStaff(Staff staff) {
        String sql= "INSERT INTO staff (id, name, email, password_hash, role, active) VALUES (?, ?, ?, ?, ?, ?)";
        int rows = jdbcTemplate.update(sql,staff.getId(),staff.getName(),staff.getEmail(),staff.getPasswordHash(),staff.getRole().name(),staff.isActive());
        return rows == 1;
    }
    public boolean updateStaff(Staff updated){
        String sql ="UPDATE staff SET name = ?, email = ?, password_hash = ?, role = ?, active = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql,updated.getName(),updated.getEmail(),updated.getPasswordHash(),updated.getRole().name(),updated.isActive(),updated.getId());
        return rows == 1;

    }

    public boolean deleteStaff(String id){
        String sql = "DELETE FROM staff WHERE id = ?";
        int rows = jdbcTemplate.update(sql,id);
        return rows == 1;
    }



}
