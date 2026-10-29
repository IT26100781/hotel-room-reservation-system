package com.hotel.user.guest.service;

import com.hotel.user.User;
import com.hotel.user.guest.dao.GuestDAO;
import com.hotel.user.guest.model.Guest;
import com.hotel.user.guest.model.RegularGuest;
import com.hotel.user.guest.model.VipGuest;
import com.hotel.util.PasswordUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class GuestService {
    private static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,63}$");
    private final GuestDAO guestDAO;

    public GuestService(GuestDAO guestDAO) {
        this.guestDAO = guestDAO;
    }

    // Match the staff component's G01, G02 ... identifier convention.
    private String nextId() {
        int largest = 0;
        for (Guest guest : guestDAO.findAllGuests()) {
            if (guest.getId() != null && guest.getId().matches("G[0-9]+")) {
                largest = Math.max(largest, Integer.parseInt(guest.getId().substring(1)));
            }
        }
        return String.format("G%02d", largest + 1);
    }

    public synchronized Guest register(String name, String email, String password) {
        name = validateName(name);
        email = validateEmail(email);
        validatePassword(password);
        checkEmail(email, null);
        Guest guest = new RegularGuest(nextId(), name, email, PasswordUtil.hash(password));
        try {
            if (!guestDAO.saveGuest(guest)) throw new IllegalStateException("Unable to create guest account");
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Email already exists or account could not be created. Please try again.");
        }
        return guest;
    }

    public Guest login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required");
        }
        Guest guest = guestDAO.findByEmail(email.trim());
        if (guest == null || !PasswordUtil.matchesPassword(password, guest.getPasswordHash())) {
            throw new SecurityException("Invalid email or password");
        }
        if (!guest.isActive()) throw new SecurityException("User account has been disabled");
        return guest;
    }

    public List<Guest> listGuests(User currentUser) {
        if (currentUser == null || !currentUser.canViewGuestUsers()) throw new SecurityException("You don't have permission to view guests");
        return guestDAO.findAllGuests();
    }

    public Guest findGuestById(User currentUser, String id) {
        if (currentUser == null || !(currentUser.canViewGuestUsers() || owns(currentUser, id))) {
            throw new SecurityException("You don't have permission to view this guest");
        }
        return guestDAO.findById(id);
    }

    public Guest updateGuestDetails(User currentUser, String id, String name, String email) {
        requireEdit(currentUser, id);
        name = validateName(name);
        email = validateEmail(email);
        Guest guest = requireGuest(id);
        checkEmail(email, id);
        guest.setName(name);
        guest.setEmail(email);
        update(guest);
        return guest;
    }

    public Guest changePassword(User currentUser, String currentPassword, String newPassword, String confirmPassword) {
        if (!(currentUser instanceof Guest)) throw new SecurityException("Please sign in as a guest");
        Guest guest = requireGuest(currentUser.getId());
        if (!guest.isActive()) throw new SecurityException("User account has been disabled");
        if (!PasswordUtil.matchesPassword(currentPassword, guest.getPasswordHash())) throw new IllegalArgumentException("Current password is incorrect");
        validatePassword(newPassword);
        if (!newPassword.equals(confirmPassword)) throw new IllegalArgumentException("Passwords do not match");
        guest.setPasswordHash(PasswordUtil.hash(newPassword));
        update(guest);
        return guest;
    }

    public Guest setVip(User currentUser, String id, boolean vip) {
        requireManage(currentUser);
        Guest old = requireGuest(id);
        Guest guest = vip ? new VipGuest(id, old.getName(), old.getEmail(), old.getPasswordHash())
                : new RegularGuest(id, old.getName(), old.getEmail(), old.getPasswordHash());
        guest.setActive(old.isActive());
        update(guest);
        return guest;
    }

    public boolean setActive(User currentUser, String id, boolean active) {
        requireManage(currentUser);
        Guest guest = requireGuest(id);
        guest.setActive(active);
        update(guest);
        return true;
    }

    public void deactivateAccount(User currentUser) {
        if (!(currentUser instanceof Guest)) throw new SecurityException("Please sign in as a guest");
        requireGuest(currentUser.getId());
        if (!guestDAO.deleteGuest(currentUser.getId())) throw new IllegalStateException("Unable to deactivate account");
    }

    private boolean owns(User user, String id) {
        return user instanceof Guest && user.getId().equals(id);
    }

    private void requireEdit(User user, String id) {
        if (user == null || !(user.canManageGuestUsers() || owns(user, id))) throw new SecurityException("You don't have permission to update this guest");
        if (user instanceof Guest && !requireGuest(user.getId()).isActive()) throw new SecurityException("User account has been disabled");
    }

    private void requireManage(User user) {
        if (user == null || !user.canManageGuestUsers()) throw new SecurityException("You don't have permission to manage guests");
    }

    private Guest requireGuest(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Guest ID is required");
        Guest guest = guestDAO.findById(id);
        if (guest == null) throw new IllegalArgumentException("Invalid guest ID");
        return guest;
    }

    private void update(Guest guest) {
        try {
            if (!guestDAO.updateGuest(guest)) throw new IllegalStateException("Unable to update guest account");
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    private void checkEmail(String email, String id) {
        Guest existing = guestDAO.findByEmail(email);
        if (existing != null && !existing.getId().equals(id)) throw new IllegalArgumentException("Email already exists");
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        name = name.trim();
        if (name.length() > 50 || name.contains("|")) throw new IllegalArgumentException("Name must be at most 50 characters and cannot contain |");
        return name;
    }

    private String validateEmail(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be empty");
        email = email.trim();
        if (email.length() > 255 || !EMAIL.matcher(email).matches()) throw new IllegalArgumentException("Invalid email format");
        return email;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) throw new IllegalArgumentException("Password must be at least 8 characters");
        if (password.contains("|") || password.chars().anyMatch(Character::isWhitespace)) throw new IllegalArgumentException("Password cannot contain spaces or |");
    }
}
