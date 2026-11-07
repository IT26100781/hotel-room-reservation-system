package com.hotel.user.admin;

import com.hotel.user.User;
import com.hotel.user.admin.dao.StaffDAO;
import com.hotel.user.admin.model.Admin;
import com.hotel.user.admin.model.Staff;
import com.hotel.user.admin.service.StaffService;
import com.hotel.util.PasswordUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StaffServiceTests {

    private StaffDAO dao;
    private StaffService service;
    private Admin admin;
    private Staff staff;

    @BeforeEach
    void setUp() {
        dao = mock(StaffDAO.class);
        service = new StaffService(dao);

        admin = new Admin("S01", "Admin One", "admin@test.lk", "hash");

        staff = new Staff("S02", "Staff Two", "staff@test.lk", "hash");

        // Safe defaults for DAO queries.
        when(dao.findAllStaff()).thenReturn(List.of());
        when(dao.findByEmail(any())).thenReturn(null);
    }

    @Test
    void plainStaffCannotDeleteEvenWithUnknownId() {
        // Arrange: nothing to set up, the DAO knows no "S99"

        // Act + Assert: a plain staff user is refused before any lookup happens
        assertThrows(SecurityException.class, () -> service.deleteStaff(staff, "S99"));

        // and nothing was deleted
        verify(dao, never()).deleteStaff(any());
    }


    // --------------------------------------------------
    // deleteStaff
    // --------------------------------------------------

    @Test
    void adminCanDeleteADeactivatedStaffMember() {
        staff.setActive(false);

        when(dao.findById("S02")).thenReturn(staff);
        when(dao.deleteStaff("S02")).thenReturn(true);

        assertDoesNotThrow(
                () -> service.deleteStaff(admin, "S02")
        );

        verify(dao).deleteStaff("S02");
    }

    @Test
    void adminCannotDeleteAnActiveStaffAccount() {
        when(dao.findById("S02")).thenReturn(staff);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteStaff(admin, "S02")
        );

        verify(dao, never()).deleteStaff(any());
    }

    @Test
    void adminCannotDeleteTheirOwnAccount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteStaff(admin, "S01")
        );

        verify(dao, never()).deleteStaff(any());
    }

    @Test
    void adminCannotDeleteAStaffMemberWithAnUnknownId() {
        when(dao.findById("S99")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteStaff(admin, "S99")
        );

        assertEquals("Invalid staff id", exception.getMessage());
        verify(dao, never()).deleteStaff(any());
    }

    @Test
    void adminCanDeleteADeactivatedAdminWhenTheyAreTheOnlyActiveAdmin() {
        Admin secondAdmin = new Admin("S02", "Admin Two", "admin2@test.lk", "hash");
        secondAdmin.setActive(false);


        when(dao.findById("S02")).thenReturn(secondAdmin);
        when(dao.findAllStaff()).thenReturn(
                List.of(admin, secondAdmin)
        );
        when(dao.deleteStaff("S02")).thenReturn(true);

        assertDoesNotThrow(
                () -> service.deleteStaff(admin, "S02")
        );

        verify(dao).deleteStaff("S02");
    }

    @Test
    void deleteStaffThrowsWhenDaoReportsFailure() {
        staff.setActive(false);

        when(dao.findById("S02")).thenReturn(staff);
        when(dao.deleteStaff("S02")).thenReturn(false);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.deleteStaff(admin, "S02")
        );

        assertEquals("Delete staff failed", exception.getMessage());
    }

    // --------------------------------------------------
    // addStaff / addAdmin
    // --------------------------------------------------

    @Test
    void plainStaffCannotAddAnotherStaffMember() {
        assertThrows(
                SecurityException.class,
                () -> service.addStaff(staff, "New Staff", "new@test.lk", "password1")
        );

        verify(dao, never()).saveStaff(any());
    }

    @Test
    void adminCanAddAnActiveStaffMemberWithIdS01WhenListIsEmpty() {
        when(dao.findAllStaff()).thenReturn(List.of());

        Staff created = service.addStaff(admin, "New Staff", "new@test.lk", "password1");
        assertEquals(Staff.class, created.getClass());
        assertEquals("S01", created.getId());
        assertTrue(created.isActive());

        verify(dao).saveStaff(created);
    }

    @Test
    void newStaffReceivesTheNextIdAfterTheLargestExistingId() {
        Staff first = new Staff(
                "S01", "First Staff",
                "first@test.lk", "hash"
        );

        Staff third = new Staff(
                "S03", "Third Staff",
                "third@test.lk", "hash"
        );

        when(dao.findAllStaff()).thenReturn(List.of(first, third));

        Staff created = service.addStaff(
                admin, "Fourth Staff",
                "fourth@test.lk", "password1"
        );

        assertEquals("S04", created.getId());
        verify(dao).saveStaff(created);
    }

    @Test
    void newStaffPasswordIsStoredAsAHash() {
        Staff created = service.addStaff(
                admin, "New Staff",
                "new@test.lk", "password1"
        );

        assertTrue(
                PasswordUtil.matchesPassword(
                        "password1", created.getPasswordHash()
                )
        );

        assertNotEquals("password1", created.getPasswordHash());
    }

    @Test
    void adminCannotAddAStaffMemberWithAnExistingEmail() {
        when(dao.findByEmail("existing@test.lk")).thenReturn(staff);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStaff(
                        admin, "New Staff",
                        "existing@test.lk", "password1"
                )
        );

        verify(dao, never()).saveStaff(any());
    }

    @Test
    void adminCannotAddAStaffMemberWithABlankName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStaff(
                        admin, " ",
                        "new@test.lk", "password1"
                )
        );

        verify(dao, never()).saveStaff(any());
    }

    @Test
    void adminCannotAddAStaffMemberWithANameLongerThan50Characters() {
        String longName = "A".repeat(51);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStaff(
                        admin, longName,
                        "new@test.lk", "password1"
                )
        );

        verify(dao, never()).saveStaff(any());
    }

    @Test
    void adminCannotAddAStaffMemberWithAnInvalidEmail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStaff(
                        admin, "New Staff",
                        "not-an-email", "password1"
                )
        );

        verify(dao, never()).saveStaff(any());
    }

    @Test
    void adminCannotAddAStaffMemberWithAShortPassword() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStaff(admin, "New Staff", "new@test.lk", "short")
        );

        verify(dao, never()).saveStaff(any());
    }

    @Test
    void adminCannotAddAStaffMemberWithASpaceInThePassword() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStaff(
                        admin, "New Staff",
                        "new@test.lk", "pass word1"
                )
        );

        verify(dao, never()).saveStaff(any());
    }

    @Test
    void adminCanAddAnAdminAndReceivesAnAdminObject() {
        Staff created = service.addAdmin(
                admin, "New Admin",
                "newadmin@test.lk", "password1"
        );

        assertInstanceOf(Admin.class, created);
        assertTrue(created.isActive());
        verify(dao).saveStaff(created);
    }

    // --------------------------------------------------
    // login
    // --------------------------------------------------

    @Test
    void userCanLogInWithTheCorrectEmailAndPassword() {
        Staff user = new Staff(
                "S02", "Staff Two",
                "staff@test.lk",
                PasswordUtil.hash("password1")
        );

        when(dao.findByEmail("staff@test.lk")).thenReturn(user);

        User result = service.login("staff@test.lk", "password1");

        assertSame(user, result);
    }

    @Test
    void wrongPasswordAndUnknownEmailReturnTheSameErrorMessage() {
        Staff user = new Staff(
                "S02", "Staff Two",
                "staff@test.lk",
                PasswordUtil.hash("password1")
        );

        when(dao.findByEmail("staff@test.lk")).thenReturn(user);
        when(dao.findByEmail("unknown@test.lk")).thenReturn(null);

        SecurityException wrongPassword = assertThrows(
                SecurityException.class,
                () -> service.login("staff@test.lk", "wrongpass")
        );

        SecurityException unknownEmail = assertThrows(
                SecurityException.class,
                () -> service.login("unknown@test.lk", "password1")
        );

        assertEquals(
                "Invalid email or password",
                wrongPassword.getMessage()
        );
        assertEquals(
                wrongPassword.getMessage(),
                unknownEmail.getMessage()
        );
    }

    @Test
    void deactivatedUserCannotLogInWithTheCorrectPassword() {
        Staff user = new Staff(
                "S02", "Staff Two",
                "staff@test.lk",
                PasswordUtil.hash("password1")
        );
        user.setActive(false);

        when(dao.findByEmail("staff@test.lk")).thenReturn(user);

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> service.login("staff@test.lk", "password1")
        );

        assertEquals(
                "User Account has been disabled",
                exception.getMessage()
        );
    }

    @Test
    void userCannotLogInWithABlankEmail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.login(" ", "password1")
        );
    }

    @Test
    void userCannotLogInWithABlankPassword() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.login("staff@test.lk", " ")
        );
    }

    // --------------------------------------------------
    // changeRole / setActive
    // --------------------------------------------------

    @Test
    void theOnlyActiveAdminCannotBeDemoted() {
        when(dao.findById("S01")).thenReturn(admin);
        when(dao.findAllStaff()).thenReturn(List.of(admin));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.changeRole(admin, "S01")
        );

        verify(dao, never()).updateStaff(any());
    }

    @Test
    void adminCanBeDemotedWhenAnotherActiveAdminExists() {
        Admin secondAdmin = new Admin(
                "S02", "Admin Two",
                "admin2@test.lk", "hash"
        );

        when(dao.findById("S02")).thenReturn(secondAdmin);
        when(dao.findAllStaff()).thenReturn(List.of(admin, secondAdmin));

        Staff result = service.changeRole(admin, "S02");

        assertEquals(Staff.class, result.getClass());
        verify(dao).updateStaff(result);
    }

    @Test
    void userCannotDeactivateTheirOwnAccount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.setActive(admin, "S01", false)
        );

        verify(dao, never()).updateStaff(any());
    }

    @Test
    void plainStaffCannotChangeAnotherUsersActiveStatus() {
        assertThrows(
                SecurityException.class,
                () -> service.setActive(staff, "S03", false)
        );

        verify(dao, never()).updateStaff(any());
    }

    // --------------------------------------------------
    // createFirstAdmin
    // --------------------------------------------------

    @Test
    void firstAdminIsCreatedWhenNoActiveAdminExists() {
        when(dao.findAllStaff()).thenReturn(List.of());

        Staff created = service.createFirstAdmin(
                "First Admin", "firstadmin@test.lk", "password1"
        );

        assertInstanceOf(Admin.class, created);
        assertTrue(created.isActive());
        assertEquals("S01", created.getId());

        verify(dao).saveStaff(created);
    }

    @Test
    void firstAdminCannotBeCreatedWhenAnActiveAdminAlreadyExists() {
        when(dao.findAllStaff()).thenReturn(List.of(admin));

        assertThrows(
                IllegalStateException.class,
                () -> service.createFirstAdmin(
                        "Another Admin",
                        "another@test.lk",
                        "password1"
                )
        );

        verify(dao, never()).saveStaff(any());
    }

    // --------------------------------------------------
    // Updates
    // --------------------------------------------------

    @Test
    void userCannotChangeEmailToOneAlreadyUsedByAnotherAccount() {
        Staff anotherStaff = new Staff(
                "S03", "Another Staff",
                "taken@test.lk", "hash"
        );

        when(dao.findById("S02")).thenReturn(staff);
        when(dao.findByEmail("taken@test.lk")).thenReturn(anotherStaff);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateStaffDetails(
                        admin, "S02",
                        "Updated Staff", "taken@test.lk"
                )
        );

        verify(dao, never()).updateStaff(any());
    }

    @Test
    void userCanKeepTheirOwnEmailWhenUpdatingOtherFields() {
        when(dao.findById("S01")).thenReturn(admin);
        when(dao.findByEmail("admin@test.lk")).thenReturn(admin);

        Staff result = service.updateAdminDetails(
                admin, "S01",
                "Updated Admin", "admin@test.lk"
        );

        assertEquals("Updated Admin", result.getName());
        assertEquals("admin@test.lk", result.getEmail());

        verify(dao).updateStaff(admin);
    }
}
