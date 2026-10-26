package com.hotel.room.dao;

import com.hotel.room.model.DeluxeRoom;
import com.hotel.room.model.Room;
import com.hotel.room.model.StandardRoom;
import com.hotel.room.model.SuiteRoom;
import com.hotel.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Room entity using MySQL database via JDBC.
 */
public class RoomDAO {

    /**
     * Retrieve all rooms ordered by room number.
     */
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Room room = mapRowToRoom(rs);
                if (room != null) {
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Find a single room by its unique ID (e.g., 'R101').
     */
    public Room findById(String id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToRoom(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find a single room by its room number (e.g., 101).
     */
    public Room findByRoomNumber(int roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToRoom(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insert a new room record.
     */
    public boolean save(Room room) {
        String sql = "INSERT INTO rooms (id, room_number, room_name, room_type, base_rate, capacity, room_size, image_url, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, room.getRoomId());
            stmt.setInt(2, room.getRoomNumber());
            stmt.setString(3, room.getRoomName());
            stmt.setString(4, room.getType());
            stmt.setDouble(5, room.getBaseRate());
            stmt.setInt(6, room.getCapacity());
            stmt.setInt(7, room.getRoomSize());
            stmt.setString(8, room.getImageUrl());
            stmt.setString(9, room.getStatus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update an existing room.
     */
    public boolean update(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_name = ?, room_type = ?, base_rate = ?, "
                   + "capacity = ?, room_size = ?, image_url = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomName());
            stmt.setString(3, room.getType());
            stmt.setDouble(4, room.getBaseRate());
            stmt.setInt(5, room.getCapacity());
            stmt.setInt(6, room.getRoomSize());
            stmt.setString(7, room.getImageUrl());
            stmt.setString(8, room.getStatus());
            stmt.setString(9, room.getRoomId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update status (active, maintenance, retired) for a room.
     */
    public boolean updateStatus(String id, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a room by ID.
     */
    public boolean delete(String id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if room number already exists, optionally excluding a specific room ID (for updates).
     */
    public boolean existsByRoomNumber(int roomNumber, String excludeRoomId) {
        String sql = "SELECT COUNT(*) FROM rooms WHERE room_number = ?"
                   + (excludeRoomId != null ? " AND id <> ?" : "");
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomNumber);
            if (excludeRoomId != null) {
                stmt.setString(2, excludeRoomId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Factory method mapping a ResultSet row to the appropriate polymorphic Room subclass.
     */
    private Room mapRowToRoom(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        int roomNumber = rs.getInt("room_number");
        String roomName = rs.getString("room_name");
        
        String type;
        try {
            type = rs.getString("room_type");
        } catch (SQLException ignored) {
            type = rs.getString("type");
        }

        double baseRate = rs.getDouble("base_rate");
        int capacity = rs.getInt("capacity");
        int roomSize = rs.getInt("room_size");
        String imageUrl = rs.getString("image_url");
        String status = rs.getString("status");

        if (type == null) {
            type = "StandardRoom";
        }

        String normalizedType = type.trim().toLowerCase();
        if (normalizedType.contains("suite")) {
            return new SuiteRoom(id, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
        } else if (normalizedType.contains("deluxe")) {
            return new DeluxeRoom(id, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
        } else {
            return new StandardRoom(id, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
        }
    }
}
