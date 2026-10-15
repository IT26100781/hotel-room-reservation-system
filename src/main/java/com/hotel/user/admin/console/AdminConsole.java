package com.hotel.user.admin.console;

import com.hotel.user.User;
import com.hotel.user.admin.dao.StaffDAO;
import com.hotel.user.admin.model.Admin;
import com.hotel.user.admin.model.Staff;
import com.hotel.user.admin.service.StaffService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminConsole {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        final StaffDAO staffDAO = new StaffDAO();
        StaffService staffService = new StaffService(staffDAO);
        User currentUser = null;
        boolean isLoggedIn = false;

        boolean hasAnyAdmin= staffService.hasAnyAdminRole();
        if (!hasAnyAdmin) {
            System.out.println("Welcome to Admin Console");
            System.out.println("Initializing System......");
            System.out.println("Please create a Admin account to continue...");
            System.out.println("Press any key to continue...");
            input.nextLine();
            System.out.println("creating a new Admin user...");
            System.out.println("Please enter your email address to continue...");
            String email = input.nextLine().trim();
            System.out.println("please enter your name to continue...");
            String name = input.nextLine().trim();
            System.out.println("please enter your password to continue...");
            String password = input.nextLine();
            try {
                staffService.createFirstAdmin(name,email, password);
                System.out.println("Admin has been created");
            }catch (Exception e){
                System.out.println("Error while creating Admin");
                System.out.println(e.getMessage());
            }

        }
        System.out.println("Welcome to Admin Console");
        while (true) {
            System.out.println("please login to continue.");
            System.out.println("press any key to continue.press 0 to exit");
            if(input.nextLine().equals("0")){
                break;
            };
            System.out.println("Please enter your email address to continue...");
            String email = input.nextLine().trim();
            System.out.println("please enter your password to continue...");
            String password = input.nextLine();
            try {
                currentUser =staffService.login(email, password);
                isLoggedIn = true;
            }catch (Exception e){
                System.out.println("Error while logging in");
                System.out.println(e.getMessage());
            }
            while (isLoggedIn){
                System.out.println("Main Menu");
                System.out.println("1. List Staff");
                System.out.println("2. Add Staff Member");
                System.out.println("3. Add Admin Member");
                System.out.println("4. Edit Staff Details");
                System.out.println("5. Edit Admin Details");
                System.out.println("6. Change Role");
                System.out.println("7. Activate/deactivate Accounts");
                System.out.println("8. Logout");
                System.out.println("0. Exit");
                System.out.println("Please enter your choice to continue...");
                char choice = input.nextLine().charAt(0);
                if(choice == '0'){
                    return;
                }else if(choice == '1'){
                    try{
                        printStaffTable(staffService.listStaff(currentUser));
                    } catch (Exception e) {
                        System.out.println("Error while Listing Staff");
                    }
                } else if(choice == '2'){
                    System.out.println("adding a new Staff member");
                    if(currentUser.canManageStaffUsers()){
                        System.out.println("enter new Staff member name");
                        String newName = input.nextLine().trim();
                        System.out.println("enter new Staff member email");
                        String newEmail = input.nextLine().trim();
                        System.out.println("enter new Staff member password");
                        String newPassword = input.nextLine();
                        try {
                           Staff newStaff = staffService.addStaff(currentUser, newName, newEmail, newPassword);
                            System.out.println("New Staff member has been added");
                            System.out.println("Staff ID: " + newStaff.getId());
                        }catch (Exception e){
                            System.out.println("Error while adding a new Staff member");
                        }
                    }else {
                        System.out.println("Sorry, You are not allowed to perform this action");
                    }
                }else if(choice == '3'){
                    System.out.println("adding a new Admin member");
                    if(currentUser.canManageAdminUsers()){
                        System.out.println("enter new Admin member name");
                        String newName = input.nextLine().trim();
                        System.out.println("enter new Admin member email");
                        String newEmail = input.nextLine().trim();
                        System.out.println("enter new Admin member password");
                        String newPassword = input.nextLine();
                        try {
                            Staff newAdmin = staffService.addAdmin(currentUser, newName, newEmail, newPassword);
                            System.out.println("New Admin member has been added");
                            System.out.println("Staff ID: " + newAdmin.getId());
                        }catch (Exception e){
                            System.out.println("Error while adding a new Admin member");
                        }
                    }else {
                        System.out.println("Sorry, You are not allowed to perform this action");
                    }
                } else if (choice == '4') {
                    System.out.println("Editing Staff member");
                    if(currentUser.canManageStaffUsers()){
                        System.out.println("enter Staff member ID");
                        String newId = input.nextLine().trim();
                        System.out.println("enter Staff member new name");
                        String newName = input.nextLine().trim();
                        System.out.println("enter Staff member new email");
                        String newEmail = input.nextLine().trim();
                        try {
                            Staff newStaff = staffService.updateStaffDetails(currentUser, newId, newName, newEmail);
                            System.out.println("Staff member details has been updated");
                            List<Staff> staffList = new ArrayList<>();
                            staffList.add(newStaff);
                            printStaffTable(staffList);
                        }catch (Exception e){
                            System.out.println("Error while updating Staff member");
                        }
                    }else {
                        System.out.println("Sorry, You are not allowed to perform this action");
                    }
                }else if (choice == '5') {
                    System.out.println("Editing Admin member");
                    if(currentUser.canManageAdminUsers()){
                        System.out.println("enter Admin member ID");
                        String newId = input.nextLine().trim();
                        System.out.println("enter Admin member new name");
                        String newName = input.nextLine().trim();
                        System.out.println("enter Admin member new email");
                        String newEmail = input.nextLine().trim();
                        try {
                            Staff newStaff = staffService.updateAdminDetails(currentUser, newId, newName, newEmail);
                            System.out.println("Admin member details has been updated");
                            List<Staff> staffList = new ArrayList<>();
                            staffList.add(newStaff);
                            printStaffTable(staffList);
                        }catch (Exception e){
                            System.out.println("Error while updating Admin member");
                        }
                    }else {
                        System.out.println("Sorry, You are not allowed to perform this action");
                    }
                }else if (choice == '6') {
                    System.out.println("Changing Role");
                    if(currentUser.canManageAdminUsers()){
                        System.out.println("enter member ID");
                        String newId = input.nextLine().trim();
                        try {
                            Staff newStaff = staffService.changeRole(currentUser, newId);
                            System.out.println("User role of member Id "+newStaff.getId()+" has been changed to role "+newStaff.getRole());
                        }catch (Exception e){
                            System.out.println("Error while changing Role");
                            System.out.println(e.getMessage());
                        }

                    }else {
                        System.out.println("Sorry, You are not allowed to perform this action");
                    }
                }else if (choice == '7') {
                    System.out.println("Activate/Deactivate Staff");
                    if(currentUser.canManageAdminUsers()){
                        System.out.println("enter member ID");
                        String newId = input.nextLine().trim();
                        System.out.println("Do you want to activate or deactivate Staff? type true to activate and false to deactivate Staff");
                        boolean activated = input.nextLine().equalsIgnoreCase("true")? true : false;
                        try {
                            boolean ifWorked= staffService.setActive(currentUser,newId,activated);
                            if(ifWorked) {
                                System.out.println("Staff has been Activated/Deactivated");
                            }
                        }catch (Exception e){
                            System.out.println("Error while activating Staff");
                            System.out.println(e.getMessage());
                        }
                    }else {
                        System.out.println("Sorry, You are not allowed to perform this action");
                    }
                }else if (choice == '8') {
                    System.out.println("Logging out");
                    currentUser = null;
                    isLoggedIn = false;

                }else if (choice == '0') {
                    System.out.println("Exiting Admin Console");
                    return;
                }else {
                    System.out.println("Invalid choice");
                }


            }

        }



    }
    public static void printStaffTable(List<Staff> list) {
        String format = "%-5s %-20s %-28s %-6s %-8s%n";

        System.out.printf(format, "ID", "Name", "Email", "Role", "Status");
        System.out.println("-".repeat(71));

        for (Staff person : list) {
            String status = person.isActive() ? "active" : "inactive";

            System.out.printf(format,
                    person.getId(),
                    person.getName(),
                    person.getEmail(),
                    person.getRole(),
                    status
            );

        }
    }

}
