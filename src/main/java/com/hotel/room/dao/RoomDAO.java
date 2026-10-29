package com.hotel.room.dao;

import com.hotel.room.model.DeluxeRoom;
import com.hotel.room.model.Room;
import com.hotel.room.model.StandardRoom;
import com.hotel.room.model.SuiteRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


//`  Spring Repository for Room entity using Spring's JdbcTemplate.

@Repository
public class RoomDAO {

    private final JdbcTemplate jdbcTemplate;

//    RowMapper to map database records to the appropriate polymorphic Room subclass.

    private final RowMapper<Room> roomRowMapper = new RowMapper<Room>() {
        @Override
        public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
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

            String normalized = type.trim().toLowerCase();
            if (normalized.contains("suite")) {
                return new SuiteRoom(id, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
            } else if (normalized.contains("deluxe")) {
                return new DeluxeRoom(id, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
            } else {
                return new StandardRoom(id, roomName, roomNumber, baseRate, capacity, roomSize, imageUrl, status);
            }
        }
    };

    @Autowired
    public RoomDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

//    Default constructor for testing/mocking subclasses if needed.

    protected RoomDAO() {
        this.jdbcTemplate = null;
    }

//Add all rooms ordered by room number.
    public List<Room> findAll() {
        String sql = "SELECT * FROM rooms ORDER BY room_number ASC";
        return jdbcTemplate.query(sql, roomRowMapper);
    }

    /**
     * Find a single room by its unique ID (e.g., 'R101').
     */
    public Room findById(String id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, roomRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Find a single room by its room number (e.g., 101).
     */
    public Room findByRoomNumber(int roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try {
            return jdbcTemplate.queryForObject(sql, roomRowMapper, roomNumber);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Insert a new room record.
     */
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
                room.getStatus());
        return rows > 0;
    }

    /**
     * Update an existing room record.
     */
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
                room.getStatus(),
                room.getRoomId());
        return rows > 0;
    }

    /**
     * Update status (active, maintenance, retired) for a room.
     */
    public boolean updateStatus(String id, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql, status, id);
        return rows > 0;
    }

    /**
     * Delete a room by ID.
     */
    public boolean delete(String id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    /**
     * Check if room number already exists, optionally excluding a specific room ID (for updates).
     */
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
