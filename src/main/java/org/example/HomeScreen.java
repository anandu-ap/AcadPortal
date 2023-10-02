package org.example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeScreen {

    public void homeScreen() {
        boolean isExit = false;
        User user = null;

        while (true) {
            System.out.println("\n_____________Welcome to the application______________\nlogin to continue.......");

            GeneralFunctions ge = new GeneralFunctions();
            while (true) {
                String[] credentials = ge.LoginScreen();
                if (credentials[0].equals("-1")) {
                    isExit = true;
                    break;
                }
                DBFunctions dbFun = new DBFunctions();
                Connection connection = dbFun.connect_to_db();
                ResultSet rs = dbFun.queryUsers(connection, credentials[0]);
                try {
                    if (rs != null && rs.next()) {
                        String password = rs.getString("password");
                        String role = rs.getString("role");
                        if (password != null && password.equals(credentials[1])) {
                            if (role.equals("Academics")) {
                                user = new AcademicsOffice(credentials[0]);
                            } else if (role.equals("Faculty")) {
                                user = new Faculty(credentials[0]);
                            } else if (role.equals("Student")) {
                                user = new Student(credentials[0]);
                            } else {
                                user = new SuperUser(credentials[0]);
                            }
                            user.loginUser();
                            System.out.println("User Logged In");
                            break;
                        } else {
                            System.out.println("Invalid username or password.2");
                        }
                    } else {
                        System.out.println("User doesn't exist.");
                    }
                } catch (SQLException e) {
                    System.out.println("Sorry. Some error occurred.");
                }
            }

            if (isExit) {
                break;
            }

            if (user instanceof SuperUser) {
                SuperUser currentUser = (SuperUser) user;
                currentUser.mainScreen();
            } else if (user instanceof AcademicsOffice) {
                AcademicsOffice currentUser = (AcademicsOffice) user;
                currentUser.mainScreen();
            } else if (user instanceof Faculty) {
                Faculty currentUser = (Faculty) user;
                currentUser.mainScreen();
            } else {
                Student currentUser = (Student) user;
                currentUser.mainScreen();
            }
        }
    }
}
