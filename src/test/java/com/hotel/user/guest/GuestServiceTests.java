package com.hotel.user.guest;

import com.hotel.user.admin.model.Staff;
import com.hotel.user.guest.dao.GuestDAO;
import com.hotel.user.guest.model.Guest;
import com.hotel.user.guest.model.RegularGuest;
import com.hotel.user.guest.model.VipGuest;
import com.hotel.user.guest.service.GuestService;
import com.hotel.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GuestServiceTests {
    private GuestDAO dao;
    private GuestService service;
    private Guest guest;
    private Staff staff;

    @BeforeEach
    void setUp() {
        dao = mock(GuestDAO.class);
        service = new GuestService(dao);
        guest = new RegularGuest("G01", "Guest One", "one@example.com", PasswordUtil.hash("password1"));
        staff = new Staff("S01", "Staff", "staff@example.com", "hash");
        when(dao.findById("G01")).thenReturn(guest);
        when(dao.updateGuest(any())).thenReturn(true);
    }

    @Test
    void registrationAlwaysCreatesActiveRegularGuestWithHashedPassword() {
        when(dao.findAllGuests()).thenReturn(List.of(guest));
        when(dao.saveGuest(any())).thenReturn(true);
        Guest created = service.register(" New Guest ", " new@example.com ", "password2");
        assertEquals("G02", created.getId());
        assertEquals("New Guest", created.getName());
        assertEquals("REGULAR", created.getGuestType());
        assertTrue(created.isActive());
        assertTrue(PasswordUtil.matchesPassword("password2", created.getPasswordHash()));
        verify(dao).saveGuest(created);
    }

    @Test
    void duplicateEmailAndInvalidInputsAreRejectedBeforeSave() {
        when(dao.findByEmail("one@example.com")).thenReturn(guest);
        assertThrows(IllegalArgumentException.class, () -> service.register("Name", "one@example.com", "password1"));
        assertThrows(IllegalArgumentException.class, () -> service.register(" ", "new@example.com", "password1"));
        assertThrows(IllegalArgumentException.class, () -> service.register("Name", "bad-email", "password1"));
        assertThrows(IllegalArgumentException.class, () -> service.register("Name", "new@example.com", "short"));
        verify(dao, never()).saveGuest(any());
    }

    @Test
    void loginRejectsWrongPasswordsAndInactiveAccounts() {
        when(dao.findByEmail("one@example.com")).thenReturn(guest);
        assertThrows(SecurityException.class, () -> service.login("one@example.com", "wrong"));
        assertSame(guest, service.login(" one@example.com ", "password1"));
        guest.setActive(false);
        assertThrows(SecurityException.class, () -> service.login("one@example.com", "password1"));
    }

    @Test
    void guestCannotReadOrEditAnotherGuestOrManageVipStatus() {
        assertThrows(SecurityException.class, () -> service.findGuestById(guest, "G02"));
        assertThrows(SecurityException.class, () -> service.updateGuestDetails(guest, "G02", "Name", "two@example.com"));
        assertThrows(SecurityException.class, () -> service.listGuests(guest));
        assertThrows(SecurityException.class, () -> service.setVip(guest, "G01", true));
        assertThrows(SecurityException.class, () -> service.setActive(guest, "G01", true));
        assertThrows(SecurityException.class, () -> service.listGuests(null));
        verify(dao, never()).updateGuest(any());
    }

    @Test
    void guestCanUpdateOwnDetailsWithoutChangingTypeOrPassword() {
        String hash = guest.getPasswordHash();
        Guest updated = service.updateGuestDetails(guest, "G01", "New Name", "new@example.com");
        assertEquals("New Name", updated.getName());
        assertEquals(hash, updated.getPasswordHash());
        assertEquals("REGULAR", updated.getGuestType());
        verify(dao).updateGuest(updated);
    }

    @Test
    void passwordChangeRequiresCurrentPasswordAndConfirmation() {
        assertThrows(IllegalArgumentException.class, () -> service.changePassword(guest, "wrong", "password2", "password2"));
        assertThrows(IllegalArgumentException.class, () -> service.changePassword(guest, "password1", "password2", "different"));
        Guest updated = service.changePassword(guest, "password1", "password2", "password2");
        assertTrue(PasswordUtil.matchesPassword("password2", updated.getPasswordHash()));
        assertFalse(PasswordUtil.matchesPassword("password1", updated.getPasswordHash()));
    }

    @Test
    void staffCanUpgradeAndDowngradeWithoutReactivatingGuest() {
        guest.setActive(false);
        Guest vip = service.setVip(staff, "G01", true);
        assertInstanceOf(VipGuest.class, vip);
        assertEquals("VIP", vip.getGuestType());
        assertEquals(0.10, vip.getDiscount());
        assertFalse(vip.isActive());
        when(dao.findById("G01")).thenReturn(vip);
        Guest regular = service.setVip(staff, "G01", false);
        assertInstanceOf(RegularGuest.class, regular);
        assertEquals(guest.getPasswordHash(), regular.getPasswordHash());
        assertFalse(regular.isActive());
    }

    @Test
    void selfDeactivationUsesSoftDeleteAndOwnSessionIdentity() {
        when(dao.deleteGuest("G01")).thenReturn(true);
        service.deactivateAccount(guest);
        verify(dao).deleteGuest("G01");
        assertThrows(SecurityException.class, () -> service.deactivateAccount(staff));
    }

    @Test
    void unsuccessfulWritesDoNotReportSuccess() {
        when(dao.updateGuest(any())).thenReturn(false);
        assertThrows(IllegalStateException.class, () -> service.setActive(staff, "G01", false));
    }
}
