package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.*;

public class Faculty extends User {

    private String name;
    private String contactNo;
    private String department;
    private String joiningDate;

    public Faculty(String username) {
        super(username, "Faculty");
        try {
            ResultSet rs = db.queryFacultyWithFacultyId(this.connection, this.username);
            if (rs != null) {
                if (rs.next()) {
                    this.name = rs.getString("name");
                    this.department = rs.getString("department");
                    this.joiningDate = rs.getString("joining_date");
                }
            }
        } catch (Exception e) {
            System.out.println("Problem in loading the user info");
        }
    }

    private List<Course> getAllCourses() {
        ResultSetFunctions rsFun = new ResultSetFunctions();
        ResultSet rs = db.queryCourseCatalog(connection, "");
        return rsFun.getCourses(rs);
    }

    private void offerNewCourse(String courseId) {
        Scanner scanner;
        scanner = new Scanner(System.in);
        boolean isValidInput = false;
        float req_cgpa = 0;
        boolean isExit = false;
        while (true) {
            while (!isValidInput) {
                System.out.print("Required CGPA (>= if any) : ");
                try {
                    String input = scanner.nextLine();
                    req_cgpa = Float.parseFloat(input);
                    if (req_cgpa < 0) {
                        isExit = true;
                        break;
                    }
                    if (req_cgpa >= 0.0f && req_cgpa <= 10.0f) {
                        isValidInput = true;
                    } else {
                        System.out.println("Invalid Input. Please enter a valid cgpa.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid Input. Please enter a valid cgpa.");
                }
            }

            if (isExit) {
                break;
            }

            isValidInput = false;
            List<String> slots = Arrays.asList("Science or HSS Core", "General or HSS Core", "Maths or Science Core", "Program Core and Minor", "HSS or Program Core", "Math Core, Minor or Open Elective", "Science or HSS Core", "HSS or Science Core or Program Elective", "Program Core or Department Elective", "Program Core or HSS Elective", "Open Elective or Program Elective", "Program Elective or Open Elective");
            String slot = slots.get(3);
            int i=1;
            for (String slt: slots) {
                System.out.println(String.format("(%d) %s", i, slt));
                i++;
            }
            while (!isValidInput) {
                System.out.print("Slot (1-12): ");
                try {
                    if (scanner.hasNextInt()) {
                        int inp = scanner.nextInt();
                        if (inp < 0) {
                            isExit = true;
                            break;
                        }
                        if (inp > 0 && inp < 13) {
                            slot = slots.get(inp - 1);
                            isValidInput = true;
                        } else {
                            System.out.println("Invalid input! Please enter valid input");
                        }
                    } else {
                        System.out.println("Invalid input! Please enter valid input");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please enter valid input");
                }
            }

            if (isExit) {
                break;
            }

            List<String> cred_cat = new ArrayList<>();
            List<String> departments = Arrays.asList("Computer Science and Engineering", "Civil Engineering", "Chemical Engineering", "Electrical Engineering", "Mechanical Engineering", "Biomedical Engineering", "Metallurgical and Materials Engineering", "Department of Mathematics", "Physics", "Department of Chemistry", "Humanities and Social Sciences", "All Departments");
            int dept_count = 1;
            for (String dept: departments) {
                System.out.println(String.format("(%d) %s", dept_count, dept));
                dept_count++;
            }
            List<String> categories = Arrays.asList("Science Requirement Core", "Science Elective", "General Engineering Requirement", "Program Core", "Program Elective", "Humanities and Social Science Core", "Humanities and Social Science Elective", "Capstone Projects", "Industrial Internship and Comprehensive Viva", "Extra Curricular", "Open Elective");
            int cat_count = 1;
            System.out.println("\n");
            for (String cat: categories) {
                System.out.println(String.format("(%d) %s", cat_count, cat));
                cat_count++;
            }
            System.out.println("\n");
            int count = 1;
            isValidInput = false;
            while (!isValidInput) {
                System.out.print("Credit Categorization" + count + " (year-department-category): ");
                try {
                    String inp = scanner.nextLine();
                    if (inp.equals("-1")) {
                        isExit = true;
                        break;
                    } else if (inp.equals("-2")) {
                        isValidInput = true;
                        System.out.println("Hi");
                    } else {
                        String[] arr = inp.split("-");
                        if (arr.length == 3) {
                            try {
                                int year = Integer.parseInt(arr[0]);
                                int department = Integer.parseInt(arr[1]);
                                int category = Integer.parseInt(arr[2]);
                                if (year >= 2015 && department>0 && department < 13 && category > 0 && category < 12) {

                                    String element = String.format("%s-%s-%s", departments.get(department-1), categories.get(category-1), arr[0]);

                                    cred_cat.add(element);
                                    count++;
                                } else {
                                    System.out.println("Invalid Input! Please enter valid year-department-category");
                                }
                            } catch (Exception e) {
                                System.out.println("Invalid Input! Please enter valid year-department-category");
                            }
                        } else {
                            System.out.println("Invalid Input! Please enter valid year-department-category");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Invalid Input! Please enter valid year-department-category");
                }

            }

            if (isExit) {
                break;
            }

            DBFunctions dbFunctions = new DBFunctions();
            ResultSet rs1 = db.queryAcademicSectionInfo(this.connection, "is_current_section", "TRUE", "-1", "-1");
            ResultSetFunctions rsFun = new ResultSetFunctions();
            int acad_year = 0;
            String acad_semester = "M";
            if (rs1 != null) {
                try {
                    if (rs1.next()) {
                        acad_year = rs1.getInt("academic_year");
                        acad_semester = rs1.getString("academic_semester");

                        int existing_sections = db.queryCourseOfferingsWithYearSemesterCourseId(this.connection, acad_semester, acad_year, courseId);
                        if (existing_sections == -1) {
                            System.out.println("Sorry an error occurred");
                        } else {
                            int response = db.addNewRowToCourseOfferings(this.connection, courseId, existing_sections+1, acad_semester, acad_year, this.username, req_cgpa, cred_cat, "Enrolling", slot);
                            if (response == 1) {
                                System.out.println("So is Error");
                            } else {
                                String tableName = String.format("%s%d%s%d", courseId, acad_year, acad_semester, existing_sections+1);
                                db.createOfferedCourseTable(this.connection, tableName);
                                System.out.println("Course offered");
                            }
                        }
                    } else {
                        System.out.println("Sorry some error occurred");
                    }
                } catch (Exception e) {
                    System.out.println("Exception : " + e);
                }
            } else {
                System.out.println("Sorry some error occurred");
            }

            break;
        }

    }

    private void courseCatalogScreen() {
        System.out.println("______________Course Catalog________________\n");
        List<Course> courses = getAllCourses();
        System.out.println(String.format("%-35s\t\t%-40s\t\t%-10s\t\t%s", "Course code", "Title", "Department", "Prerequisites"));
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
            System.out.println("(2) Offer Course");

            isValidInput = false;
            while (!isValidInput) {
                System.out.print("Navigate to [1..2] : ");
                try {
                    if (scanner.hasNextInt()) {
                        option = scanner.nextInt();
                        if (option > 0 && option <=2) {
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
                isValidInput = false;
                String courseId;
                int course_number = 0;
                while (!isValidInput) {
                    System.out.print("Course number : ");
                    try {
                        if (scanner.hasNextInt()) {
                            course_number = scanner.nextInt();
                            if (course_number > 0 && course_number <= course_count) {
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

                offerNewCourse(courses.get(course_number-1).getCourse_id());

            }
        }
    }

    private String getFacultyName(String facultyId) {
        ResultSet rs = db.queryFacultyWithFacultyId(connection, facultyId);
        ResultSetFunctions rsFun = new ResultSetFunctions();
        String name = rsFun.getString(rs, "instructorid", 0);

        return name;
    }

    private Course getCourse(String courseId) {
        ResultSetFunctions rsFun = new ResultSetFunctions();
        ResultSet rs = db.queryCourseCatalogWithCourseId(connection, courseId);
        return rsFun.getCourse(rs);
    }

    private String getStudentName(String studentId) {
        ResultSet rs = db.queryStudentWithStudentId(connection, studentId);
        ResultSetFunctions rsFun = new ResultSetFunctions();
        String studentName = rsFun.getString(rs, "name", 0);
        return studentName;
    }

    private void updateGradeInOfferedCourse(String studentId, String grade) {
        db.updateOfferedCourseTable(connection, studentId, grade);
    }

    private void updateGradesInOfferedCourseFromCSVFile(String filePath) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineData = line.split(",");
                String studentId = lineData[0];
                String grade = lineData[1];
                updateGradeInOfferedCourse(studentId, grade);

            }
        } catch (IOException e) {
            System.out.println("Some error occurred");
        }
    }

    private void showOfferedCourseScreen(int offerId) {
        ResultSet rs = db.queryOfferedCourse(connection, "offered_course_" + offerId);
        ResultSetFunctions rsFun = new ResultSetFunctions();
        List<List<String>> result = rsFun.getStudentIdAndGrade(rs);

        for (List<String> element : result) {
            String studentName = getStudentName(element.get(0));
            System.out.println(studentName.toUpperCase() + " | " + element.get(0).toUpperCase() + " | " + element.get(1) + "\n");
        }
        System.out.println("\n\n");
        Scanner scanner = new Scanner(System.in);

        List<String> options = Arrays.asList("1", "2");
        boolean loopControl = true;
        while (loopControl) {
            System.out.println("(1) Back");
            System.out.println("(2) Update Grade");
            System.out.print("Navigate to [1...2] : ");
            String option = scanner.nextLine();
            while (!options.contains(option)) {
                System.out.print("Number should be from the given options. Navigate to [1....2] : ");
                option = scanner.nextLine();
            }
            if (option.equals("1")) {
                // TODO
                // change this
                loopControl = false;

            } else if (option.equals("2")) {
                System.out.print("Upload via .csv file(A)/Enter manually(B)");
                String entry_type = scanner.nextLine();
                if (entry_type.equals("A")) {
                    System.out.print("Path to the file : ");
                    String filePath = scanner.nextLine();
                    updateGradesInOfferedCourseFromCSVFile(filePath);
                } else {
                    System.out.print("Student Id : ");
                    String studentId = scanner.nextLine();
                    System.out.print("New grade : ");
                    String newGrade = scanner.nextLine();
                    updateGradeInOfferedCourse(studentId, newGrade);
                }
            }
        }

        myCoursesScreen();

    }

    private void myCoursesScreen() {

        System.out.println("\n______My Courses________\n");
        System.out.print("Academic Section (YYYY-M|YYYY-W|YYYY-S) : ");
        Scanner scanner = new Scanner(System.in);
        String academic_section = scanner.nextLine();

        String[] values = academic_section.split("-");
        ResultSet rs = null;
        if (academic_section.isEmpty()) {
            rs = db.queryCourseOfferingsWithFacultyIdAndAcademicSection(connection, this.username, -1, "");
        } else {
            rs = db.queryCourseOfferingsWithFacultyIdAndAcademicSection(connection, this.username, Integer.parseInt(values[0]), values[1]);
        }
        try {
           while (rs.next()) {
               Course course = getCourse(rs.getString("course_id"));
               String instructorName = getFacultyName(rs.getString("faculty"));
               // TODO
               // add slot where ever needed
               System.out.println(rs.getInt("offerId") + ".   " + course.getCourse_id() + "|" + course.getTitle() + "|" + course.getL() + "-" + course.getT() + "-" + course.getP() + "-" + course.getS() + "-" + course.getC());
               System.out.println("status:" + rs.getString("status") + ", session: " + rs.getInt("year") + "-" + rs.getString("semester"));
               System.out.println("offered by: " + course.getOfferingDepartment() + ", slot: " + rs.getInt("slot"));
               System.out.println("Instructor: " + instructorName + "\n");

           }
        } catch (Exception e) {

        }
        System.out.println("\n");
        System.out.println("(1) Go Back");
        System.out.println("(2) Select Course offering : ");

        List<String> options = Arrays.asList("1", "2");
        boolean loopControl = true;
        while (loopControl) {
            System.out.print("Navigate to [1..2] : ");
            String option = scanner.nextLine();
            while (!options.contains(option)) {
                System.out.print("Number should be from the given options. Navigate to [1..2] : ");
                option = scanner.nextLine();
            }
            if (option.equals("1")) {
                loopControl = false;
            } else {

                System.out.print("offerId : ");
                int offerId = Integer.parseInt(scanner.nextLine());
                //TODO
                // Validate courseID
                showOfferedCourseScreen(offerId);
            }
        }
        mainScreen();
    }

    private void studentRecordScreen(String studentId) {
        System.out.println("\n______Student Record Screen________\n");
        ResultSet rs_student_record = db.queryStudentRecord(connection, "student_record_" + studentId);
        System.out.println("Yes1");
        int current_year = 1000;
        String current_semester = "E";
        try {
            while (rs_student_record.next()) {
                String courseId = rs_student_record.getString("course_id");
                String grade = rs_student_record.getString("grade");
                int offerId = rs_student_record.getInt("offerid");
                ResultSet rs_course_details = db.queryCourseCatalog(connection, courseId);
                System.out.println("Yes2");
                ResultSet rs_course_offer_details = db.queryCourseOfferingsWithOfferId(connection, offerId);
                System.out.println("Yes3");
                rs_course_details.next();
                rs_course_offer_details.next();
                int year = rs_course_offer_details.getInt("year");
                System.out.println("Yes4");
                String semester = rs_course_offer_details.getString("semester");
                if (year != current_year || !semester.equals(current_semester)) {
                    System.out.println("\nAcademic Section : " + year + "-" + semester);
                }
                System.out.println("Id : " + offerId + "    " + courseId + "  " + rs_course_details.getString("title") + " (" +
                        rs_course_details.getString("l") + "-" +
                        rs_course_details.getString("t") + "-" +
                        rs_course_details.getString("p") + "-" +
                        rs_course_details.getString("s") + "-" +
                        rs_course_details.getString("c") + ")    =>    " +
                        grade);
            }
        } catch (Exception e) {

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

        mainScreen();


    }

    public void mainScreen() {
        boolean loopControl = true;
        while (loopControl) {
            System.out.println("\n______Main Screen________\n");
            System.out.println("(1) Course Catalog");
            System.out.println("(2) Course offerings");//View
            System.out.println("(3) My Courses");
            System.out.println("(4) Check Student Record");
            System.out.println("(5) UG Curriculum");
            System.out.println("(6) Logout");

            Scanner scanner;
            scanner = new Scanner(System.in);
            boolean isValidInput = false;
            int option = 1;
            while (!isValidInput) {
                System.out.print("Navigate to [1....6] : ");
                try {
                    if (scanner.hasNextInt()) {
                        option = scanner.nextInt();
                        if (option > 0 && option <= 6) {
                            isValidInput = true;
                        } else {
                            System.out.println("Invalid input! Please give a valid input");
                        }
                    } else {
                        System.out.println("Invalid input! Please give a valid input");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please give a valid input");
                }
                System.out.print("Number should be from the given options. Navigate to [1....6] : ");
            }

            if (option == 1) {

                courseCatalogScreen();

            } else if (option == 2) {

//            courseOfferingsScreen();

            } else if (option == 3) {

                myCoursesScreen();

            } else if (option == 4) {

                System.out.print("Entry number of the student : ");
                String studentId = scanner.nextLine();

                studentRecordScreen(studentId);

            } else if (option == 5) {
                System.out.print("Department : ");
                String department = scanner.nextLine();
                System.out.print("Batch : ");
                String batch = scanner.nextLine();

//            UGCurriculumScreen(department, batch);

            } else {
                logout();
                loopControl = false;
            }
        }
    }
    }