package com.hotel.user.admin.service;

import com.hotel.user.Role;
import com.hotel.user.User;
import com.hotel.user.admin.dao.StaffDAO;
import com.hotel.user.admin.model.Admin;
import com.hotel.user.admin.model.Staff;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static com.hotel.util.PasswordUtil.hash;
import static com.hotel.util.PasswordUtil.matchesPassword;

@Service
public class StaffService {
    // calls StaffDao
    private final StaffDAO staffDAO;
    // defines accepted email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    // constructor
    public StaffService(StaffDAO staffDAO) {
        this.staffDAO = staffDAO;
    }

    //auto increment staffId
    private String nextId() {
        //Staff ID format is S+ 2 numbers eg: S01
        //Create list to store staff objects
        List<Staff> staffList = staffDAO.findAllStaff();
        if (staffList.isEmpty()) {return "S01";}
        //loop to get largest integer value of id
        int largest =0;
        for (Staff staff : staffList) {
            try{
                int currentStaffID = Integer.parseInt(staff.getId().substring(1));
                if(currentStaffID > largest){
                    largest = currentStaffID;
                }
            }catch (NumberFormatException | StringIndexOutOfBoundsException e ){
                continue;
            }

        }
        // return with increasing largest Id by 1
        return String.format("S%02d", largest +1);

    }

    // validators for email, name and password
    private void validateEmail(String email) throws IllegalArgumentException{
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if(email.contains(" ")) {
            throw new IllegalArgumentException("Email cannot contain spaces");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

    }
    private void validatePassword(String password) throws IllegalArgumentException{
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if(password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if(password.contains("|")){
            throw new IllegalArgumentException("Password contains invalid symbols");
        }
        if(password.contains(" ")){
            throw new IllegalArgumentException("Password cannot contain spaces");
        }
    }
    private void validateName(String name) throws IllegalArgumentException{
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if(name.length() > 50) {
            throw new IllegalArgumentException("Name cannot exceed 50 characters");
        }
        if (name.contains("|")){
            throw new IllegalArgumentException("Name contains invalid symbols");
        }
    }

    //add new staff to system
    public Staff addStaff(User currentUser,String name,String email,String password){
        boolean canAddStaff = currentUser.canManageStaffUsers();
        if(!canAddStaff){
            throw new SecurityException("You don't have permission to add staff");
        }
        Staff staff = createAccount(name,email,password,Role.STAFF);
        return  staff;
    }
    // add new admin to system
    public Staff addAdmin(User currentUser,String name,String email,String password) {
        boolean canAddAdmin = currentUser.canManageAdminUsers();
        if(!canAddAdmin){
            throw new SecurityException("You don't have permission to add admin");
        }
        Staff admin = createAccount(name,email,password,Role.ADMIN);
        return admin;
    }
    // login
    public User login(String email,String password) throws IOException {
        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if(password == null || password.isBlank()){
            throw new IllegalArgumentException("Password cannot be empty");
        }
        email = email.trim();
        Staff staff = staffDAO.findByEmail(email);
        if(staff == null){
            throw new SecurityException("Invalid email or password");
        }
        boolean isPasswordMatch = matchesPassword(password,staff.getPasswordHash());
        if(!isPasswordMatch){
            throw new SecurityException("Invalid email or password");
        }else if (!staff.isActive()) {
            throw new SecurityException("User Account has been disabled");
        }
        return staff;
    }
    public List<Staff> listStaff(User currentUser){
        if(currentUser.canViewStaffUsers()){
            return staffDAO.findAllStaff();
        }else{
            throw new SecurityException("You don't have permission to view staff");
        }
    }
    // find staff by id
    public Staff findStaffById(User currentUser,String id){
        if(currentUser.canViewStaffUsers()){
            return staffDAO.findById(id);
        }else {
            throw new SecurityException("You don't have permission to view staff");
        }
    }
    // update staff details
    public Staff updateStaffDetails(User currentUser,String id,String name,String email) {
        // checks whether either user changes their own profile or has access to edit other staff profiles
        boolean canUpdateStaff = currentUser.canManageStaffUsers() || currentUser.getId().equals(id);
        if(!canUpdateStaff){
            throw new SecurityException("You don't have permission to update staff");
        }
        Staff staff = updateAccount(id,name,email);
        return staff;
    }
    // update staff details
    public Staff updateAdminDetails(User currentUser,String id,String name,String email) {
        // checks whether either user changes their own profile or has access to edit other staff profiles
        boolean canUpdateAdmin = currentUser.canManageAdminUsers() || currentUser.getId().equals(id);
        if(!canUpdateAdmin){
            throw new SecurityException("You don't have permission to update admin");
        }
        Staff admin = updateAccount(id,name,email);
        return admin;
    }
    // change role
    public Staff changeRole (User currentUser,String id) {
        boolean canChangeRole = currentUser.canManageAdminUsers();
        if(!canChangeRole){
            throw new SecurityException("You don't have permission to change role");
        }
        Staff staff = staffDAO.findById(id);
        if(staff == null){
            throw new IllegalArgumentException("Invalid staff id");
        }
        Role role = staff.getRole();
        if(role == Role.ADMIN){
            if(!areMoreThanOneAdmin() && staff.isActive()){
                throw new IllegalArgumentException("There must be at least one Admin Role");
            }
            Staff newStaff = new Staff(id,staff.getName(),staff.getEmail(),staff.getPasswordHash(),staff.isActive());
            staffDAO.updateStaff(newStaff);
            return newStaff;
        } else if (role == Role.STAFF) {
            Admin newAdmin = new Admin(id,staff.getName(),staff.getEmail(),staff.getPasswordHash(),staff.isActive());
            staffDAO.updateStaff(newAdmin);
            return newAdmin;
        }else{
            throw new IllegalArgumentException("Invalid role");
        }

    }
    // set user active
    public boolean setActive(User currentUser,String id,boolean isActive) {
        boolean canSetActive = currentUser.canManageAdminUsers() && !currentUser.getId().equals(id);
        if(!canSetActive){
            throw new SecurityException(currentUser.getId().equals(id)?"You can't deactivate your own account":"You don't have permission to set active");
        }
        Staff staff = staffDAO.findById(id);
        if(staff == null){
            throw new IllegalArgumentException("Invalid staff id");
        }
        if(staff.getRole() == Role.ADMIN && !isActive && staff.isActive()){
            if(!areMoreThanOneAdmin()){
                throw new IllegalArgumentException("There must be at least one Admin Role");
            }
        }
        staff.setActive(isActive);
        staffDAO.updateStaff(staff);
        return true;
    }

    //helpers
    private Staff createAccount(String name,String email,String password,Role role) {
        validatePassword(password);
        validateName(name);
        name = name.trim();
        if (email != null) {
            email = email.trim();
        }
        validateEmail(email);

        if(role == null){
            throw new IllegalArgumentException("Role cannot be null");
        }
        if(staffDAO.findByEmail(email)!= null){
            throw new IllegalArgumentException("Email already exists");
        }
        String id = nextId();
        String passwordHash = hash(password);
        if (role == Role.STAFF) {
            Staff user = new Staff(id,name,email,passwordHash,true);
            staffDAO.saveStaff(user);
            return user;
        }else if (role == Role.ADMIN) {
            Admin user= new Admin(id,name,email,passwordHash,true);
            staffDAO.saveStaff(user);
            return user;
        }
        return null;
    }
    private Staff updateAccount(String id,String name,String email) {
        if(id == null || id.isEmpty()){
            throw new IllegalArgumentException("ID cannot be empty");
        }
        validateName(name);
        name = name.trim();
        if (email != null) {
            email = email.trim();
        }
        validateEmail(email);
        Staff staff = staffDAO.findById(id);
        if(staff == null){
            throw new SecurityException("Invalid id");
        }
        staff.setName(name);
        if(staffDAO.findByEmail(email)!= null && !staffDAO.findByEmail(email).getId().equals(id)){

            throw  new IllegalArgumentException("Email already exists ");
        }
        staff.setEmail(email);
        staffDAO.updateStaff(staff);
        return staff;
    }
    private boolean areMoreThanOneAdmin() {
        List<Staff> staffList = staffDAO.findAllStaff();
        int count =0;
        for(Staff staff : staffList){
            if(staff.getRole() == Role.ADMIN && staff.isActive()){
                count++;
            }
        }
        return count > 1;
    }
    public boolean hasAnyAdminRole() {
        List<Staff> staffList = staffDAO.findAllStaff();
        for(Staff staff : staffList) {
            if (staff.getRole() == Role.ADMIN && staff.isActive()) {
                return true;
            }
        }
        return false;
    }
    public Staff createFirstAdmin(String name,String email,String password){
        boolean hasAnyAdminRole = hasAnyAdminRole();
        if(hasAnyAdminRole){
            throw new IllegalStateException("Admin Role already exists");
        }else{
            return createAccount(name,email,password,Role.ADMIN);
        }

    }
}
