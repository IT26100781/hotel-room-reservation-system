package com.hotel.user.guest.dao;

import com.hotel.user.guest.model.Guest;
import com.hotel.user.guest.model.RegularGuest;
import com.hotel.user.guest.model.VipGuest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GuestDAO {
    private static final String SELECT = "SELECT id, name, email, password_hash, guest_type, active FROM guests";
    private final JdbcTemplate jdbcTemplate;

    public GuestDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Guest mapRow(ResultSet rs, int rowNum) throws SQLException {
        String type = rs.getString("guest_type");
        Guest guest;
        if ("REGULAR".equalsIgnoreCase(type)) {
            guest = new RegularGuest(rs.getString("id"), rs.getString("name"), rs.getString("email"), rs.getString("password_hash"));
        } else if ("VIP".equalsIgnoreCase(type)) {
            guest = new VipGuest(rs.getString("id"), rs.getString("name"), rs.getString("email"), rs.getString("password_hash"));
        } else {
            throw new IllegalStateException("Invalid guest type: " + type);
        }
        guest.setActive(rs.getBoolean("active"));
        return guest;
    }

    public List<Guest> findAllGuests() {
        return jdbcTemplate.query(SELECT + " ORDER BY id", this::mapRow);
    }

    public Guest findById(String id) {
        List<Guest> found = jdbcTemplate.query(SELECT + " WHERE id = ?", this::mapRow, id);
        return found.isEmpty() ? null : found.get(0);
    }

    public Guest findByEmail(String email) {
        List<Guest> found = jdbcTemplate.query(SELECT + " WHERE email = ?", this::mapRow, email);
        return found.isEmpty() ? null : found.get(0);
    }

    public boolean saveGuest(Guest guest) {
        return jdbcTemplate.update("INSERT INTO guests (id, name, email, password_hash, guest_type, active) VALUES (?, ?, ?, ?, ?, ?)",
                guest.getId(), guest.getName(), guest.getEmail(), guest.getPasswordHash(), guest.getGuestType(), guest.isActive()) == 1;
    }

    public boolean updateGuest(Guest guest) {
        return jdbcTemplate.update("UPDATE guests SET name = ?, email = ?, password_hash = ?, guest_type = ?, active = ? WHERE id = ?",
                guest.getName(), guest.getEmail(), guest.getPasswordHash(), guest.getGuestType(), guest.isActive(), guest.getId()) == 1;
    }

    // Keep guest records because bookings and reviews reference them.
    public boolean deleteGuest(String id) {
        return jdbcTemplate.update("UPDATE guests SET active = FALSE WHERE id = ?", id) == 1;
    }
}
