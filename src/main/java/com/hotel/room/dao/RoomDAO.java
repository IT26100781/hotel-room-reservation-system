package com.hotel.room.dao;

import com.hotel.room.model.Room;
import com.hotel.room.model.RoomStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Spring Repository for Room entity using Spring's JdbcTemplate.
 * Matches the team's StaffDAO pattern: short methods, this::mapRow, no try/catch boilerplate.
 */
@Repository
public class RoomDAO {

    private final JdbcTemplate jdbcTemplate;

    public RoomDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Room.create(
                rs.getString("room_type"),
                rs.getString("id"),
                rs.getString("room_name"),
                rs.getInt("room_number"),
                rs.getDouble("base_rate"),
                rs.getInt("capacity"),
                rs.getInt("room_size"),
                rs.getString("image_url"),
                RoomStatus.fromString(rs.getString("status"))
        );
    }

    public List<Room> findAll() {
        String sql = "SELECT id, room_number, room_name, room_type, base_rate, capacity, room_size, image_url, status FROM rooms ORDER BY room_number ASC";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public Room findById(String id) {
        String sql = "SELECT id, room_number, room_name, room_type, base_rate, capacity, room_size, image_url, status FROM rooms WHERE id = ?";
        List<Room> list = jdbcTemplate.query(sql, this::mapRow, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Room findByRoomNumber(int roomNumber) {
        String sql = "SELECT id, room_number, room_name, room_type, base_rate, capacity, room_size, image_url, status FROM rooms WHERE room_number = ?";
        List<Room> list = jdbcTemplate.query(sql, this::mapRow, roomNumber);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean save(Room room) {
        String sql = "INSERT INTO rooms (id, room_number, room_name, room_type, base_rate, capacity, room_size, image_url, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int rows = jdbcTemplate.update(sql,
                room.getRoomId(),
                room.getRoomNumber(),
                room.getRoomName(),
                room.getType(),
                room.getBaseRate(),
                room.getCapacity(),
                room.getRoomSize(),
                room.getImageUrl(),
                room.getStatus().getValue());
        return rows == 1;
    }

    public boolean update(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_name = ?, room_type = ?, base_rate = ?, "
                   + "capacity = ?, room_size = ?, image_url = ?, status = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql,
                room.getRoomNumber(),
                room.getRoomName(),
                room.getType(),
                room.getBaseRate(),
                room.getCapacity(),
                room.getRoomSize(),
                room.getImageUrl(),
                room.getStatus().getValue(),
                room.getRoomId());
        return rows == 1;
    }

    public boolean updateStatus(String id, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql, status, id);
        return rows == 1;
    }

    public boolean existsByRoomNumber(int roomNumber, String excludeRoomId) {
        if (excludeRoomId != null) {
            String sql = "SELECT COUNT(*) FROM rooms WHERE room_number = ? AND id <> ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roomNumber, excludeRoomId);
            return count != null && count > 0;
        } else {
            String sql = "SELECT COUNT(*) FROM rooms WHERE room_number = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roomNumber);
            return count != null && count > 0;
        }
    }
}
