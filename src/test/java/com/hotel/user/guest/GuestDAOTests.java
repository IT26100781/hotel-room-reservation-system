package com.hotel.user.guest;

import com.hotel.user.guest.dao.GuestDAO;
import com.hotel.user.guest.model.Guest;
import com.hotel.user.guest.model.VipGuest;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GuestDAOTests {
    @Test
    void readsGuestTableAndRestoresSubtypeAndInactiveStatus() throws Exception {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("id")).thenReturn("G01");
        when(rs.getString("name")).thenReturn("Guest");
        when(rs.getString("email")).thenReturn("guest@example.com");
        when(rs.getString("password_hash")).thenReturn("hash");
        when(rs.getString("guest_type")).thenReturn("VIP");
        when(rs.getBoolean("active")).thenReturn(false);
        when(jdbc.query(eq("SELECT id, name, email, password_hash, guest_type, active FROM guests ORDER BY id"), org.mockito.ArgumentMatchers.<RowMapper<Guest>>any()))
                .thenAnswer(invocation -> List.of(invocation.<RowMapper<Guest>>getArgument(1).mapRow(rs, 0)));
        Guest guest = new GuestDAO(jdbc).findAllGuests().get(0);
        assertInstanceOf(VipGuest.class, guest);
        assertEquals("G01", guest.getId());
        assertFalse(guest.isActive());
    }

    @Test
    void deletionPreservesGuestRecord() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.update("UPDATE guests SET active = FALSE WHERE id = ?", "G01")).thenReturn(1);
        assertTrue(new GuestDAO(jdbc).deleteGuest("G01"));
        verify(jdbc).update("UPDATE guests SET active = FALSE WHERE id = ?", "G01");
        verifyNoMoreInteractions(jdbc);
    }
}
