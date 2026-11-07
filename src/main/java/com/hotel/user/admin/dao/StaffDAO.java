package com.hotel.user.admin.dao;

import com.hotel.user.Role;
import com.hotel.user.admin.model.Admin;
import com.hotel.user.admin.model.Staff;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Staff DAO is the class that handles all the staff functions related to the database
 */
@Repository
public class StaffDAO {
    private final JdbcTemplate jdbcTemplate;

    public StaffDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Decide whether to create a Staff object or admin object
     *
     */
    private Staff mapRow(ResultSet rs, int rowNum) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        String role = rs.getString("role");
        boolean active = rs.getBoolean("active");
        boolean mustChangePassword = rs.getBoolean("must_change_password");

        if (role.equalsIgnoreCase(Role.STAFF.name())) {
            Staff staff = new Staff(id, name, email, passwordHash,active);
            staff.setMustChangePassword(mustChangePassword);
            return staff;
        }
        if (role.equalsIgnoreCase(Role.ADMIN.name())) {
            Staff admin = new Admin(id, name, email, passwordHash,active);
            admin.setMustChangePassword(mustChangePassword);
            return admin;
        }
        throw new IllegalStateException("Invalid role");
    }

    public List<Staff> findAllStaff() {
        String sql = "SELECT id, name, email, password_hash, role, active,must_change_password FROM staff ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public Staff findById(String id) {
        String sql = "SELECT id, name, email, password_hash, role, active,must_change_password FROM staff WHERE id = ?";
        List<Staff> found = jdbcTemplate.query(sql, this::mapRow, id);
        return found.isEmpty() ? null : found.get(0);
    }

    public Staff findByEmail(String email) {
        String sql = "SELECT id, name, email, password_hash, role, active,must_change_password FROM staff WHERE email = ?";
        List<Staff> found = jdbcTemplate.query(sql, this::mapRow, email);
        return found.isEmpty() ? null : found.get(0);
    }

    public boolean saveStaff(Staff staff) {
        String sql = "INSERT INTO staff (id, name, email, password_hash, role, active,must_change_password) VALUES (?, ?, ?, ?, ?, ?,?)";
        int rows = jdbcTemplate.update(sql,
                staff.getId(),
                staff.getName(),
                staff.getEmail(),
                staff.getPasswordHash(),
                staff.getRole().name(),
                staff.isActive(),
                staff.isMustChangePassword());
        return rows == 1;
    }

    public boolean updateStaff(Staff updated) {
        String sql = "UPDATE staff SET name = ?, email = ?, password_hash = ?, role = ?, active = ?,must_change_password=? WHERE id = ?";
        int rows = jdbcTemplate.update(sql,
                updated.getName(),
                updated.getEmail(),
                updated.getPasswordHash(),
                updated.getRole().name(),
                updated.isActive(),
                updated.isMustChangePassword(),
                updated.getId());

        return rows == 1;
    }

    public boolean deleteStaff(String id) {
        String sql = "DELETE FROM staff WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows == 1;
    }
}
