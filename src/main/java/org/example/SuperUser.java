package org.example;

import java.util.Scanner;

public class SuperUser extends User {

    public SuperUser(String username) {
        super(username, "Superuser");
    }


    public void addAcademicsStaff(String staffId, String password, String name, String contact) {
        db.addNewRowToUsers(connection, staffId, password, "Academics");
        db.addNewRowToAcademicsOffice(connection, staffId, name, contact);
    }

    public void addFaculty(String facultyId, String password, String name, String contact, String department) {
        db.addNewRowToUsers(connection, facultyId, password, "Faculty");
        db.addNewRowToFaculties(connection, facultyId, name, department, contact);
    }

    public void addStudent(String studentId, String password, String name, String contact, String department, String program, String batch, String joining_date) {
        // joining date format 'YYYY-MM-DD'
        db.addNewRowToUsers(connection, studentId, password, "Student");
        db.addNewRowToStudents(connection, studentId, name, department, program, batch, contact, joining_date);
        db.createStudentRecord(connection, "student_record_" + studentId);
    }

    public void mainScreen() {
        boolean loopControle = true;
        Scanner scanner = new Scanner(System.in);
        boolean isValidInput;
        int option = 1;
        while (loopControle) {
            System.out.print("\n__________MainScreen____________");
            System.out.println("(1) addAcademicsStaff");
            System.out.println("(2) addFaculty");
            System.out.println("(3) addStudent");
            System.out.println("(4) logout");
            isValidInput = false;
            while (!isValidInput) {
                System.out.print("navigate to [1....4] : ");
                if (scanner.hasNextInt()) {
                    option = scanner.nextInt();
                    if (option > 0 && option <= 4) {
                        isValidInput = true;
                    } else {
                        System.out.print("Invalid input! Please give valid input");
                    }
                } else {
                    System.out.print("Invalid input! Please give valid input");
                }
            }

            if (option == 1) {
                Scanner scanner1 = new Scanner(System.in);
                isValidInput = false;
                while (!isValidInput) {
                    System.out.println("(staffId#password#name#contact)");
                    try {
                        String input = scanner1.nextLine();
                        String[] parts = input.split("#");
                        if (parts.length == 4) {
                            addAcademicsStaff(parts[0], parts[1], parts[2], parts[3]);
                            isValidInput = true;
                        } else {
                            System.out.print("Invalid input! Please give valid input");
                        }
                    } catch (Exception e) {
                        System.out.print("Invalid input! Please give valid input");
                    }
                }
            } else if (option == 2) {
                Scanner scanner1 = new Scanner(System.in);
                isValidInput = false;
                while (!isValidInput) {
                    System.out.println("(facultyId#password#name#contact#department)");
                    try {
                        String input = scanner1.nextLine();
                        String[] parts = input.split("#");
                        if (parts.length == 5) {
                            addFaculty(parts[0], parts[1], parts[2], parts[3], parts[4]);
                            isValidInput = true;
                        } else {
                            System.out.print("Invalid input! Please give valid input");
                        }
                    } catch (Exception e) {
                        System.out.print("Invalid input! Please give valid input");
                    }
                }
            } else if (option == 3) {
                Scanner scanner1 = new Scanner(System.in);

                isValidInput = false;
                while (!isValidInput) {
                    System.out.println("(studentId#password#name#contact#department#program#batch#joiningdate(YYYY-MM-DD))");
                    try {
                        String input = scanner1.nextLine();
                        String[] parts = input.split("#");
                        if (parts.length == 8) {
                            addStudent(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
                            isValidInput = true;
                        } else {
                            System.out.print("Invalid input! Please give valid input");
                        }
                    } catch (Exception e) {
                        System.out.print("Invalid input! Please give valid input");
                    }
                }
            } else {
                logout();
                loopControle = false;
            }

        }
    }
}
