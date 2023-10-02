package org.example;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Student extends User {
    private String name;
    private String contactNo;
    private String entryNumber;
    private String program;
    private String department;
    private int batch;
    private String joiningDate;

    public Student (String username) {
        super(username, "Student");

        try {
            ResultSet rs = db.queryStudentWithStudentId(this.connection, this.username);
            if (rs != null) {
                if (rs.next()) {
                    this.name = rs.getString("name");
                    this.entryNumber = this.username;
                    this.program = rs.getString("program");
                    this.department = rs.getString("department");
                    this.batch = rs.getInt("batch");
                    this.joiningDate = rs.getString("joining_date");
                }
            }
        } catch (Exception e) {
            System.out.println("Problem in loading the user info");
        }
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    private void studentRecordScreen() {
        System.out.println("\n______Student Record Screen________\n");
        ResultSet rs_student_record = db.queryStudentRecord(connection, "student_record_" + this.username);
        int current_year = 1000;
        String current_semester = "E";
        System.out.println(String.format("%-35s\t\t%-40s\t\t%-10s\t\t%s", "Course code(l-t-p-s-c)", "Title", "Credit Category", "Grade"));
        try {
            while (rs_student_record.next()) {
                String courseId = rs_student_record.getString("course_id");
                String grade = rs_student_record.getString("grade");
                int offerId = rs_student_record.getInt("offerid");
                String category = rs_student_record.getString("category");
                ResultSet rs_course_details = db.queryCourseCatalog(connection, courseId);
                ResultSet rs_course_offer_details = db.queryCourseOfferingsWithOfferId(connection, offerId);
                rs_course_details.next();
                rs_course_offer_details.next();
                int year = rs_course_offer_details.getInt("year");
                String semester = rs_course_offer_details.getString("semester");
                if (year != current_year || !semester.equals(current_semester)) {
                    System.out.println("\nAcademic Section : " + year + "-" + semester);
                }
                String ltpsc = rs_course_details.getString("l")+"-"+rs_course_details.getString("t")+"-"+rs_course_details.getString("p")+"-"+rs_course_details.getString("s")+"-"+rs_course_details.getString("c");
                System.out.println(String.format("%-35s\t\t%-40s\t\t%-10s\t\t%s", courseId+"(" + ltpsc+")", rs_course_details.getString("title"), category, grade));
            }
        } catch (Exception e) {
            System.out.println("Some Error occurred");
        }

        boolean loopControl = true;
        Scanner scanner;
        scanner = new Scanner(System.in);
        List<String> options = Arrays.asList("1");

        while (loopControl) {
            System.out.println("\n(1) Back");

            System.out.print("Navigate to (1) : ");
            String option = scanner.nextLine();
            while (!options.contains(option)) {
                System.out.print("Number should be from the given options. Navigate to (1) : ");
                option = scanner.nextLine();
            }
            if (option.equals("1")) {

                loopControl = false;

            }
        }
    }

    private List<CourseOffering> getAllCourseOfferings(int year, String semester) {
        ResultSet rs = db.queryCourseOfferingsWithYearAndSemester(connection, year, semester);
        ResultSetFunctions rsFun = new ResultSetFunctions();
        List<CourseOffering> courseOfferings = rsFun.getCourseOfferings(rs);
        return courseOfferings;
    }

    private Course getCourse(String courseId) {
        ResultSetFunctions rsFun = new ResultSetFunctions();
        ResultSet rs = db.queryCourseCatalogWithCourseId(connection, courseId);
        return rsFun.getCourse(rs);
    }

    private String getFacultyName(String facultyId) {
        ResultSet rs = db.queryFacultyWithFacultyId(connection, facultyId);
        ResultSetFunctions rsFun = new ResultSetFunctions();
        String name = rsFun.getString(rs, "instructorid", 0);

        return name;
    }

    private void enrollForCourseOffering(int offerId, String courseId, int section) {
        ResultSet rs = db.queryAcademicSectionInfo(this.connection, "is_current_section", "TRUE", "-1", "-1");
        int acad_year = 0;
        String acad_semester = "W";
        try {
            if (rs != null) {
                if (rs.next()) {
                    acad_year = rs.getInt("academic_year");
                    acad_semester = rs.getString("academic_semester");
                }
            }

            double credit_limit = 19.5;
            double creditEarnedSecondLastSemester = 0;
            double creditEarnedLastSemester = 0;

            if (acad_year != this.batch) {
                if (acad_semester.equals("M")) {
                    creditEarnedSecondLastSemester = db.getCreditEarnedInAcademicSection(this.connection, acad_year-1, "M", this.username);
                    creditEarnedLastSemester =db.getCreditEarnedInAcademicSection(this.connection, acad_year-1, "W", this.username);
                } else {
                    creditEarnedSecondLastSemester = db.getCreditEarnedInAcademicSection(this.connection, acad_year-1, "W", this.username);
                    creditEarnedLastSemester = db.getCreditEarnedInAcademicSection(this.connection, acad_year, "M", this.username);
                }
                credit_limit = 1.25 * (creditEarnedLastSemester + creditEarnedSecondLastSemester) / 2;
            }

            double creditsRegisteredCurrentSemester = db.getCreditRegisteredInAcademicSection(this.connection, acad_year, acad_semester, this.username);
            ResultSet rs2 = db.queryCourseCatalog(this.connection, courseId);
            double creditOfTargetCourse = 999999;
            if (rs2 != null) {
                if (rs2.next()) {
                    creditOfTargetCourse = rs2.getDouble("c");
                }
            }

            System.out.println("creditsRegisteredCurrentSemester : " + creditsRegisteredCurrentSemester + "\ncreditOfTargetCourse : " + creditOfTargetCourse + "\nCredit limit : " + credit_limit);

            if (creditsRegisteredCurrentSemester + creditOfTargetCourse > credit_limit) {
                System.out.println("Credit Limit Exceeded");
            } else {
                String[] credit_categorization = null;
                String category = "";
                boolean isAllowedForBatch = false;
                ResultSet rs3 = db.queryCourseOfferingsWithOfferId(this.connection, offerId);
                if (rs3 != null) {
                    if (rs3.next()) {
                        Array arr = rs3.getArray("credit_categorization");
                        if (arr != null) {
                            credit_categorization = (String[]) arr.getArray();
                        }
                    }
                }

                if (credit_categorization == null) {
                    System.out.println("Batch not allowed");
                } else {
                    for (String str: credit_categorization) {
                        String[] parts = str.split("-");
                        if (parts[0].toUpperCase().equals(this.department.toUpperCase()) && parts[2].equals(Integer.toString(this.batch))) {
                            isAllowedForBatch = true;
                            category = parts[1];
                            break;
                        }
                    }
                }
                if (!isAllowedForBatch) {
                    System.out.println("Batch not allowed");
                } else {
                    int res1 = db.checkIfCourseAlreadyTaken(this.connection, courseId, this.username);
                    if (res1 > 0) {
                        System.out.println("Already enrolled");
                    } else if (res1 == 0) {
                        int response1 = db.addNewRowToStudentRecord(this.connection, courseId, offerId, category, this.username);
                        int response2 = db.addNewRowToOfferedCourse(this.connection, this.entryNumber, courseId, acad_year, acad_semester, section);
                    } else {
                        System.out.println("Some error occurred");
                    }

                }
            }


        } catch (Exception e) {
            System.out.println("Couldn't Enroll for the course");
        }
    }

    private void courseOfferingsScreen() {
        System.out.println("\n______Course Offerings Screen________\n");
        System.out.print("Academic Section (YYYY-M|YYYY-W|YYYY-S): ");
        Scanner scanner = new Scanner(System.in);
        String academic_section = scanner.nextLine();

        String[] values = academic_section.split("-");
        List<CourseOffering> courseOfferings = getAllCourseOfferings(Integer.parseInt(values[0]), values[1]);
        int i = 0;
        for (CourseOffering courseOffering : courseOfferings) {
            Course course = getCourse(courseOffering.getCourseId());
            String instructorName = getFacultyName(courseOffering.getFaculty());
            // TODO
            // add slot where ever needed
            System.out.println(String.format("%d. %s|%s|%f-%f-%f-%f-%f", i+1, courseOffering.getCourseId(), course.getTitle(), course.getL(), course.getT(), course.getP(), course.getS(), course.getC()));
            System.out.println(String.format("status: %s, session: %d-%s", courseOffering.getStatus(), courseOffering.getYear(), courseOffering.getSemester()));
            System.out.println(String.format("offered by: %s, slot: %s", course.getOfferingDepartment(), courseOffering.getSlot()));
            System.out.println(String.format("Instructor : %s", instructorName));

            i++;
        }
        System.out.println("\n\n");

        boolean loopControl = true;
        boolean isValidInput;
        int option = 1;
        while (loopControl) {
            System.out.println("(1) Back");
            if (!courseOfferings.isEmpty()) {
                System.out.println("(2) Enrol for a course");
                System.out.print("Navigate to [1...2] : ");
            } else {
                System.out.print("Navigate to [1] : ");
            }

            isValidInput = false;
            while (!isValidInput) {
                try {
                    if (scanner.hasNextInt()) {
                        option = scanner.nextInt();
                        if (option > 0 && ((!courseOfferings.isEmpty() && option <= 2) || (courseOfferings.isEmpty() && option <= 1))) {
                            isValidInput = true;
                        } else {
                            System.out.println("Invalid input! Please enter a valid input");
                        }
                    } else {
                        System.out.println("Invalid input! Please enter a valid input");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please enter a valid input");
                }
            }

            if (option == 1) {

                loopControl = false;

            } else {

                int offerId = 1;
                isValidInput = false;
                while (!isValidInput) {
                    System.out.print("OfferId (As shown in above table) : )");
                    try {
                        if (scanner.hasNextInt()) {
                            offerId = scanner.nextInt();
                            if (offerId > 0 && offerId <= i) {
                                isValidInput = true;
                                enrollForCourseOffering(courseOfferings.get(offerId-1).getOfferId(), courseOfferings.get(offerId-1).getCourseId(), courseOfferings.get(offerId-1).getSection());
                            } else {
                                System.out.println("Invalid input! Please enter a valid input");
                            }
                        } else {
                            System.out.println("Invalid input! Please enter a valid input");
                        }
                    } catch (Exception e) {
                        System.out.println("Exception : " + e);
                        System.out.println("Invalid input! Please enter a valid input");
                    }
                }
            }
        }
    }

    private List<Course> getAllCourses() {
        ResultSetFunctions rsFun = new ResultSetFunctions();
        ResultSet rs = db.queryCourseCatalog(connection, "");
        return rsFun.getCourses(rs);
    }

    private void courseCatalogScreen() {
        System.out.println("______________Course Catalog________________\n");
        List<Course> courses = getAllCourses();
        System.out.println(String.format("  %-35s\t\t%-40s\t\t%-10s\t\t%s", "Course code", "Title", "Department", "Prerequisites"));
        int course_count = 0;
        for (Course course : courses) {
            System.out.print(String.format("%d. %-35s\t\t%-40s\t\t%-10s\t\t",course_count+1, course.getCourse_id()+"(" + course.getL() + "-" + course.getT() + "-" + course.getP() + "-" + course.getS() + "-" + course.getC() + ")", course.getTitle(), course.getOfferingDepartment()));
            for (String item : course.getPreReq()) {
                System.out.print(item + ", ");
            }
            System.out.println();
            course_count++;
        }

        Scanner scanner;
        scanner = new Scanner(System.in);
        boolean loopControl = true;
        int option = 1;
        boolean isValidInput;
        while (loopControl) {
            System.out.println("\n");
            System.out.println("(1) Go Back");

            isValidInput = false;
            while (!isValidInput) {
                System.out.print("Navigate to [1] : ");
                try {
                    if (scanner.hasNextInt()) {
                        option = scanner.nextInt();
                        if (option == 1) {
                            isValidInput = true;
                        } else {
                            System.out.println("Invalid input! Please enter a valid input");
                        }
                    } else {
                        System.out.println("Invalid input! Please enter a valid input");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please enter a valid input");
                }
            }

            if (option == 1) {
                loopControl = false;
            }
        }
    }

    public void mainScreen() {

        Scanner scanner;
        scanner = new Scanner(System.in);

        int option = 1;
        boolean isValidInput;
        boolean isExit = false;
        while (!isExit) {
            System.out.println("\n_____________MainScreen______________");
            System.out.println("(1) Student Record");
            System.out.println("(2) Courses available for enrollment");
            System.out.println("(3) Course Catalog");
            System.out.println("(4) UG Curriculum");
            System.out.println("(5) Logout");

            isValidInput = false;
            while (!isValidInput) {
                System.out.print("Navigate to [1....6] : ");
                try {
                    if (scanner.hasNextInt()) {
                        option = scanner.nextInt();
                        if (option >= 1 && option <= 6) {
                            isValidInput = true;
                        } else {
                            System.out.println("Invalid input! Please enter a valid input");
                        }
                    } else {
                        System.out.println("Invalid input! Please enter a valid input");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please enter a valid input");
                }
            }

            if (option == 1) {
                studentRecordScreen();
            } else if (option == 2) {
                courseOfferingsScreen();
            } else if (option == 3) {
                courseCatalogScreen();
            } else if (option == 4) {
                // return UG curriculum
                // will have a back option which will again come back to mainscreen
            } else {
                logout();
                isExit = true;
            }
        }

    }




}
