package org.example;

import java.io.FileWriter;
import java.sql.Array;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AcademicsOffice extends User {

    private String name;
    private String contactNo;
    private String joiningDate;

    public AcademicsOffice(String username) {
        super(username, "Academics");
    }

    private void addNewCourse(String course_id, String title, float l, float t, float p, float s, float c, String offeringDepartment, String[] preReq) {
        Course course = new Course(course_id, title, l, t, p, s, c, offeringDepartment, preReq);
        course.addNewCourse(connection);
    }

    private List<Course> getAllCourses() {
        ResultSetFunctions rsFun = new ResultSetFunctions();
        ResultSet rs = db.queryCourseCatalog(connection, "");
        return rsFun.getCourses(rs);
    }

    private Course getCourse(String courseId) {
        ResultSetFunctions rsFun = new ResultSetFunctions();
        ResultSet rs = db.queryCourseCatalogWithCourseId(connection, courseId);
        return rsFun.getCourse(rs);
    }

    private String takeInputUntilMatchesPattern(String pattern, String sample, String helper) {
        boolean isMatches = false;
        Scanner scanner;
        scanner = new Scanner(System.in);
        String input = "";
        while (!isMatches) {
            System.out.print(helper + "(eg" + sample + "): ");
            input = scanner.nextLine();
            if (input.matches(pattern)) {
                isMatches = true;
            }
        }

        return input;
    }

    private void addNewCourseScreen() {
        Scanner scanner;
        scanner = new Scanner(System.in);

        System.out.println("Please enter the details of the course to be added, ");
        System.out.print("Course Id (AAXXX) : ");
        String course_id = scanner.nextLine();
        System.out.print("Title : ");
        String title = scanner.nextLine();
        System.out.print("Offering department : ");
        String offering_department = scanner.nextLine();
        System.out.print("Credit Structure (l-t-p-s-c) : ");
        String ltpsc = scanner.nextLine();
        System.out.print("Prerequisites if any (as single space separated course codes) : ");
        String prerequisites = scanner.nextLine();

        String[] values = ltpsc.split("-");
        String[] pre_req = prerequisites.split(" ");

        addNewCourse(course_id, title, Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3]), Float.parseFloat(values[4]), offering_department, pre_req);
    }

    private void courseCatalogScreen() {
        System.out.println("______________Course Catalog________________\n");
        List<Course> courses = getAllCourses();
        for (Course course : courses) {
            System.out.print(course.getCourse_id() + "(" + course.getL() + "-" + course.getT() + "-" + course.getP() + "-" + course.getS() + "-" + course.getC() + ")" + "    "+
                    course.getTitle() + "    " + course.getOfferingDepartment() + "    ");
            for (String item : course.getPreReq()) {
                System.out.print(item + ", ");
            }
            System.out.println();
        }

        Scanner scanner;
        scanner = new Scanner(System.in);
        List<String> options = Arrays.asList("1", "2");
        boolean loopControl = true;
        while (loopControl) {
            System.out.println("\n");
            System.out.println("(1) Go Back");
            System.out.println("(2) Add a new Course");
            System.out.print("Navigate to [1....3] : ");
            String option = scanner.nextLine();
            while (!options.contains(option)) {
                System.out.print("Number should be from the given options. Navigate to [1....3] : ");
                option = scanner.nextLine();
            }
            if (option.equals("1")) {
                loopControl = false;
            } else if (option.equals("2")) {

                addNewCourseScreen();

            }
        }
    }

    private List<CourseOffering> getAllCourseOfferings(int year, String semester) {
        ResultSet rs = db.queryCourseOfferingsWithYearAndSemester(connection, year, semester);
        ResultSetFunctions rsFun = new ResultSetFunctions();
        List<CourseOffering> courseOfferings = rsFun.getCourseOfferings(rs);
        return courseOfferings;
    }

    private String getFacultyName(String facultyId) {
        ResultSet rs = db.queryFacultyWithFacultyId(connection, facultyId);
        ResultSetFunctions rsFun = new ResultSetFunctions();
        String name = rsFun.getString(rs, "instructorid", 0);

        return name;
    }

    private String getStudentName(String studentId) {
        ResultSet rs = db.queryStudentWithStudentId(connection, studentId);
        ResultSetFunctions rsFun = new ResultSetFunctions();
        String studentName = rsFun.getString(rs, "name", 0);
        return studentName;
    }

    private void copyGradesToStudentRecord(List<List<String>> arr, int offerId, String courseId) {
        for (List<String> element : arr) {
            db.updateStudentRecord(connection, "student_record_" + element.get(0), offerId, element.get(1), courseId);
        }
    }

    private void showOfferedCourseScreen(int offerId, String courseId) {
        ResultSet rs = db.queryOfferedCourse(connection, "offered_course_" + offerId);
        ResultSetFunctions rsFun = new ResultSetFunctions();
        List<List<String>> result = rsFun.getStudentIdAndGrade(rs);

        for (List<String> element : result) {
            String studentName = getStudentName(element.get(0));
            System.out.println(studentName.toUpperCase() + " | " + element.get(0).toUpperCase() + " | " + element.get(1) + "\n");
        }
        System.out.println("\n\n");
        System.out.println("(1) Back");
        System.out.println("(2) Copy grade to Student Record");

        Scanner scanner = new Scanner(System.in);

        List<String> options = Arrays.asList("1", "2");
        System.out.print("Navigate to [1...2] : ");
        String option = scanner.nextLine();
        while (!options.contains(option)) {
            System.out.print("Number should be from the given options. Navigate to [1....2] : ");
            option = scanner.nextLine();
        }
        if (option.equals("1")) {
            // TODO
            // change this
            mainScreen();

        } else if (option.equals("2")) {

            copyGradesToStudentRecord(result, offerId, courseId);
        }

    }
    private void courseOfferingsScreen() {
        System.out.println("\n______Course Offerings Screen________\n");
        System.out.print("Academic Section (YYYY-M|YYYY-W|YYYY-S): ");
        Scanner scanner = new Scanner(System.in);
        String academic_section = scanner.nextLine();

        String[] values = academic_section.split("-");
        List<CourseOffering> courseOfferings = getAllCourseOfferings(Integer.parseInt(values[0]), values[1]);
        int i = 1;
        for (CourseOffering courseOffering : courseOfferings) {
            Course course = getCourse(courseOffering.getCourseId());
            String instructorName = getFacultyName(courseOffering.getFaculty());
            // TODO
            // add slot where ever needed
            System.out.println(i + ".   " + courseOffering.getCourseId() + "|" + course.getTitle() + "|" + course.getL() + "-" + course.getT() + "-" + course.getP() + "-" + course.getS() + "-" + course.getC());
            System.out.println("status:" + courseOffering.getStatus() + ", session: " + courseOffering.getYear() + "-" + courseOffering.getSemester());
            System.out.println("offered by: " + course.getOfferingDepartment() + ", slot: " + courseOffering.getSlot());
            System.out.println("Instructor: " + instructorName + "\n");

            i++;
        }
        System.out.println("\n\n");

        List<String> options = Arrays.asList("1");
        if (!courseOfferings.isEmpty()) {
            options.add("2");
        }
        boolean loopControl = true;
        while (loopControl) {
            System.out.println("(1) Back");
            if (!courseOfferings.isEmpty()) {
                System.out.println("(2) Go to a Specific Course Offering");
                System.out.print("Navigate to [1...2] : ");
            } else {
                System.out.print("Navigate to [1] : ");
            }

            String option = scanner.nextLine();
            while (!options.contains(option)) {
                System.out.print("Number should be from the given options. Navigate to : ");
                option = scanner.nextLine();
            }
            if (option.equals("1")) {

                loopControl = false;

            } else if (option.equals("2")) {

                System.out.print("OfferId (As shown in above table) : )");
                String rowNumber = scanner.nextLine();
                int offerId = courseOfferings.get(Integer.parseInt(rowNumber)).getOfferId();
                String courseId = courseOfferings.get(Integer.parseInt(rowNumber)).getCourseId();
                showOfferedCourseScreen(offerId, courseId);

            }
        }

    }

    private void updateGradeWithOfferId(String studentId, int offerId, String grade) {
        db.updateStudentRecord(connection, "student_record_" + studentId, offerId, grade, "");
    }

    private List<String> getStudentInformation(String studentId) {
        List<String> info = new ArrayList<>();
        ResultSet rs = db.queryStudentWithStudentId(connection, studentId);
        try {
            rs.next();
            info.add(rs.getString("name"));
            info.add(rs.getString("department"));
            info.add(rs.getString("program"));
            info.add(rs.getString("batch"));

        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }
        return info;
    }

    private ResultSet getCourseAndGradeInformation(String studentId) {
        ResultSet rs = db.queryStudentRecordCourseCatalogAndCourseOffering(connection, "student_record_" + studentId);
        return rs;
    }

    private void generateTranscript(String studentId) {

        String fileName = "Transcript_" + studentId +".txt";
        List<String> studentInfo = getStudentInformation(studentId);
        ResultSet rs = getCourseAndGradeInformation(studentId);

        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(String.format("%50s\n\n", "TRANSCRIPT"));
            fileWriter.write(String.format("Student Name : %s\t\t\t", studentInfo.get(0).toUpperCase()));
            fileWriter.write(String.format("Entry No : %s\n", studentId.toUpperCase()));
            fileWriter.write(String.format("Program : %s\t\t\t", studentInfo.get(2)));
            fileWriter.write(String.format("Batch : %s\n", studentInfo.get(3)));
            fileWriter.write(String.format("Department : %s\n\n", studentInfo.get(1)));

            fileWriter.write(String.format("%-20s%-20s%-50s%-15s%-15s\n", "Academic Section", "Course Code", "Title", "Credits", "Grade"));
            try {
                while (rs.next()) {
                    fileWriter.write(String.format("%-20s%-20s%-50s%-15s%-15s\n", rs.getString("year")+"-"+rs.getString("semester"), rs.getString("course_id"), rs.getString("title"), rs.getString("c"), rs.getString("grade")));
                }
            } catch (Exception e) {
                System.out.println("exception : " + e);
            }

            fileWriter.close();
        } catch (Exception e) {
            System.out.println("Exception2 : " + e);
        }

    }

    private void studentRecordScreen(String studentId) {
        System.out.println("\n______Student Record Screen________\n");
        ResultSet rs_student_record = db.queryStudentRecord(connection, "student_record_" + studentId);
        int current_year = 1000;
        String current_semester = "E";
        try {
            while (rs_student_record.next()) {
                String courseId = rs_student_record.getString("course_id");
                String grade = rs_student_record.getString("grade");
                int offerId = rs_student_record.getInt("offerid");
                ResultSet rs_course_details = db.queryCourseCatalog(connection, courseId);
                ResultSet rs_course_offer_details = db.queryCourseOfferingsWithOfferId(connection, offerId);
                rs_course_details.next();
                rs_course_offer_details.next();
                int year = rs_course_offer_details.getInt("year");
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
        List<String> options = Arrays.asList("1", "2", "3");

        while (loopControl) {
            System.out.println("\n(1) Back");
            System.out.println("(2) Edit grade");
            System.out.println("(3) Generate transcript");
            System.out.println("(4) Graduation Check");

            System.out.print("Navigate to [1....4] : ");
            String option = scanner.nextLine();
            while (!options.contains(option)) {
                System.out.print("Number should be from the given options. Navigate to [1....4] : ");
                option = scanner.nextLine();
            }
            if (option.equals("1")) {

                loopControl = false;

            } else if (option.equals("2")) {

                System.out.println("Id of course for which grade need to be edited : ");
                int id = Integer.parseInt(scanner.nextLine());
                System.out.println("New grade : ");
                String grade = scanner.nextLine();
                // TODO
                // Check id is valid or not
                // check grade is valid or not
                updateGradeWithOfferId(studentId, id, grade);

            } else if (option.equals("3")) {

                try {
                    generateTranscript(studentId);
                    System.out.println("Success1");
                } catch (Exception e) {
                    System.out.println("Exception1 : " + e);
                }
            } else {
//                checkGraduationStatus(studentId);
            }
        }

    }

    private void showCurriculumForYearAndBranch(int year, String department) {
        int check = db.getNumberOfRowsUGCurriculumWithYearAndDepartment(connection, year, department);
        if (check == 0) {
            System.out.println("Curriculum for given batch and department doesn't exist");
        } else {
            ResultSet rs = db.queryUGCurriculum(connection, year, department);
            if (rs != null) {
                try {
                    while (rs.next()) {
                        System.out.println("Semester : " + rs.getInt("semester") + "\n");
                        Array arr = rs.getArray("core_courses");
                        String[] core_courses = (String[]) arr.getArray();
                        for (String course : core_courses) {
                            System.out.println(course);
                        }
                        System.out.println("Number of Program Electives : " + rs.getInt("program_electives"));
                        System.out.println("Number of Science Math Electives : " + rs.getInt("science_math_electives"));
                        System.out.println("Number of HS Electives : " + rs.getInt("hs_electives"));
                        System.out.println("Number of Open Electives : " + rs.getInt("open_electives"));
                    }
                } catch (Exception e) {
                    System.out.println("Exception : " + e);
                }
            } else {
                System.out.println("Curriculum for given year doesn't exist");
            }
        }

    }

    private void editCurriculumForYearAndBranch(int year, String department) {
        int check = db.getNumberOfRowsUGCurriculumWithYearAndDepartment(connection, year, department);
        if (check == 0) {
            System.out.println("Curriculum for given batch and department doesn't exist");
        } else {
            Scanner scanner;
            scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Semester (1-8) : ");
                int semester = Integer.parseInt(scanner.nextLine());
                if (semester == -1) {
                    break;
                }
                // TODO Handle options
                System.out.println("Core Courses (course codes separated by single spaces): ");
                String[] core_courses = scanner.nextLine().split(" ");
                System.out.println("Number of program electives : ");
                int program_ele = Integer.parseInt(scanner.nextLine());
                System.out.println("Number of Science Math electives : ");
                int science_math_ele = Integer.parseInt(scanner.nextLine());
                System.out.println("Number of HS electives : ");
                int hs_ele = Integer.parseInt(scanner.nextLine());
                System.out.println("Number of Open electives : ");
                int open_ele = Integer.parseInt(scanner.nextLine());
                int response = db.addNewRowToUGCurriculum(connection, "BTech", department, year, semester, core_courses, program_ele, science_math_ele, hs_ele, open_ele);

            }
        }
    }

    private void addCurriculumForYearAndBranch(int year, String department) {
        int check = db.getNumberOfRowsUGCurriculumWithYearAndDepartment(connection, year, department);
        if (check != 0) {
            System.out.println("Curriculum for given batch and department already exists");
        } else {
            Scanner scanner;
            scanner = new Scanner(System.in);
            int semester = 1;
            while (semester < 9) {
                System.out.println("Semester : " + semester);
                System.out.print("Core Courses (course codes separated by single spaces): ");
                String[] core_courses = scanner.nextLine().split(" ");
                System.out.print("Number of program electives : ");
                int program_ele = Integer.parseInt(scanner.nextLine());
                System.out.print("Number of Science Math electives : ");
                int science_math_ele = Integer.parseInt(scanner.nextLine());
                System.out.print("Number of HS electives : ");
                int hs_ele = Integer.parseInt(scanner.nextLine());
                System.out.print("Number of Open electives : ");
                int open_ele = Integer.parseInt(scanner.nextLine());
                int response = db.addNewRowToUGCurriculum(connection, "BTech", department, year, semester, core_courses, program_ele, science_math_ele, hs_ele, open_ele);
                semester++;
            }
            System.out.println("Added");
        }

    }

    private void UGCurriculumScreen() {
        System.out.println("\n______UG Curriculum________\n");
        Scanner scanner;
        scanner = new Scanner(System.in);
        boolean loopControl = true;
        while (loopControl) {
            System.out.println("(1) Back");
            System.out.println("(2) View Curriculum");
            System.out.println("(3) Edit Curriculum");
            System.out.println("(4) Add curriculum for new batch");
            System.out.print("Navigate to[1..4] : ");
            String option = scanner.nextLine();

            if (option.equals("1")) {
                loopControl = false;
            } else if (option.equals("2")) {
                System.out.print("Department : ");
                String department = scanner.nextLine();
                System.out.print("Batch : ");
                int batch = Integer.parseInt(scanner.nextLine());
                showCurriculumForYearAndBranch(batch, department);
            } else if (option.equals("3")) {
                System.out.print("Department : ");
                String department = scanner.nextLine();
                System.out.print("Batch : ");
                int batch = Integer.parseInt(scanner.nextLine());
                editCurriculumForYearAndBranch(batch, department);
            } else if (option.equals("4")){
                System.out.print("Department : ");
                String department = scanner.nextLine();
                System.out.print("Batch : ");
                int batch = Integer.parseInt(scanner.nextLine());
                addCurriculumForYearAndBranch(batch, department);
            } else {
                System.out.println("Option not available");
            }
        }

    }

    private void startAcademicSectionEvent(String column1, String column2) {
        Scanner scanner;
        scanner = new Scanner(System.in);
        boolean loopControl = true;
        boolean isExit = false;
        while (loopControl) {
            boolean isValidInput = false;
            int academic_year = 0;
            String academic_semester = "W";
            while (!isValidInput) {
                System.out.print("Academic Section (YYYY-M |  YYYY-W | YYYY-S): ");
                try {
                    String input = scanner.nextLine();
                    if (input.equals("-1")) {
                        isExit = true;
                        break;
                    }
                    String[] parts = input.split("-");
                    if (parts.length == 2) {
                        parts[1] = parts[1].toUpperCase();
                        try {
                            int year = Integer.parseInt(parts[0]);
                            if (year > 2010 && (parts[1].equals("W") || parts[1].equals("M") || parts[1].equals("S"))) {
                                academic_semester = parts[1];
                                academic_year = year;
                                isValidInput = true;
                            } else {
                                System.out.println("Invalid input! Please give valid input1.");
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid input! Please give valid input2.");
                        }
                    } else {
                        System.out.println("Invalid input! Please give valid input3.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please give valid input3.");
                }
            }

            if (isExit) {
                break;
            }
            isValidInput = false;
            String startDate = null;
            while (!isValidInput) {
                System.out.print("Event Start Date (YYYY-MM-DD): ");
                try {
                    String input = scanner.nextLine();
                    if (input.equals("-1")) {
                        isExit = true;
                        break;
                    }
                    DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
                    Date date = null;
                    try {
                        date = dateFormat.parse(input);
                        if (date != null) {
                            startDate = input;
                            isValidInput = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input! Please give valid input.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please give valid input.");
                }
            }

            if (isExit) {
                break;
            }

            isValidInput = false;
            String endDate = null;
            while (!isValidInput) {
                System.out.print("Event End Date (YYYY-MM-DD): ");
                try {
                    String input = scanner.nextLine();
                    if (input.equals("-1")) {
                        isExit = true;
                        break;
                    }
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = dateFormat.parse(input);
                        if (date != null) {
                            endDate = input;
                            isValidInput = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input! Please give valid input.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please give valid input.");
                }
            }
            if (isExit) {
                break;
            }
            int response1 = db.updateAcademicSectionInfo(connection, column1, startDate, academic_year, academic_semester);
            int response2 = db.updateAcademicSectionInfo(connection, column2, endDate, academic_year, academic_semester);
            if (response1 < 0 || response2 < 0) {
                System.out.println("Some Database error occurred. Please try again");
            } else if (response1 == 0) {
                System.out.println("Given Academic section doesn't exists. Check the input or add the academic section");
            } else {
                System.out.println("Data updated");
                loopControl = false;
            }
        }

    }

    private void extendAcademicSectionEvent(String column) {
        Scanner scanner;
        scanner = new Scanner(System.in);
        boolean loopControl = true;
        boolean isExit = false;
        while (loopControl) {
            boolean isValidInput = false;
            int academic_year = 0;
            String academic_semester = "W";
            while (!isValidInput) {
                System.out.print("Academic Section (YYYY-M |  YYYY-W | YYYY-S): ");
                try {
                    String input = scanner.nextLine();
                    if (input.equals("-1")) {
                        isExit = true;
                        break;
                    }
                    String[] parts = input.split("-");
                    if (parts.length == 2) {
                        parts[1] = parts[1].toUpperCase();
                        try {
                            int year = Integer.parseInt(parts[0]);
                            if (year > 2010 && (parts[1].equals("W") || parts[1].equals("M") || parts[1].equals("S"))) {
                                academic_semester = parts[1];
                                academic_year = year;
                                isValidInput = true;
                            } else {
                                System.out.println("Invalid input! Please give valid input.");
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid input! Please give valid input.");
                        }
                    } else {
                        System.out.println("Invalid input! Please give valid input.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please give valid input.");
                }
            }

            if (isExit) {
                break;
            }

            isValidInput = false;
            String endDate = scanner.nextLine();
            while (!isValidInput) {
                System.out.print("Event End Date (YYYY-MM-DD): ");
                try {
                    String input = scanner.nextLine();
                    if (input.equals("-1")) {
                        isExit = true;
                        break;
                    }
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = dateFormat.parse(input);
                        if (date != null) {
                            endDate = input;
                            isValidInput = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input! Please give valid input.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please give valid input.");
                }
            }

            if (isExit) {
                break;
            }

            int response = db.updateAcademicSectionInfo(connection, column, endDate, academic_year, academic_semester);
            if (response < 0) {
                System.out.println("Some Database error occurred. Please try again");
            } else if (response == 0) {
                System.out.println("Given Academic section doesn't exists. Check the input or add the academic section");
            } else {
                System.out.println("Data updated");
                loopControl = false;
            }
        }
    }

    private void endCurrentAcademicSection() {

        int response = db.updateCurrentSectionInAcademicSectionInfo(connection);
        if (response < 0) {
            System.out.println("Some Database error occurred. Please try again");
        } else if (response == 0) {
            System.out.println("No academic section is active now");
        } else {
            System.out.println("Academic section ended");
        }

    }

    private void startNewAcademicSection() {
        int check = db.queryAcademicSectionInfoWithStatus(this.connection, "TRUE");
        if (check == -1) {
            System.out.println("Sorry. Some error occurred");
        } else if (check == 1) {
            System.out.println("Please end the current academic section before starting the new.");
        } else {
            boolean isExit = false;
            while (true) {
                Scanner scanner;
                scanner = new Scanner(System.in);
                boolean isValidInput = false;
                int academic_year = 0;
                String academic_semester = "W";
                while (!isValidInput) {
                    System.out.print("Academic Section (YYYY-M |  YYYY-W | YYYY-S): ");
                    try {
                        String input = scanner.nextLine();
                        if (input.equals("-1")) {
                            isExit = true;
                            break;
                        }
                        String[] parts = input.split("-");
                        if (parts.length == 2) {
                            parts[1] = parts[1].toUpperCase();
                            try {
                                int year = Integer.parseInt(parts[0]);
                                if (year > 2010 && (parts[1].equals("W") || parts[1].equals("M") || parts[1].equals("S"))) {
                                    academic_semester = parts[1];
                                    academic_year = year;
                                    isValidInput = true;
                                } else {
                                    System.out.println("Invalid input! Please give valid input.");
                                }
                            } catch (Exception e) {
                                System.out.println("Invalid input! Please give valid input.");
                            }
                        } else {
                            System.out.println("Invalid input! Please give valid input.");
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input! Please give valid input.");
                    }
                }

                if (isExit) {
                    break;
                }

                ResultSet rs = db.queryAcademicSectionInfo(this.connection, "academic_year", String.format("%d", academic_year), "academic_semester", String.format("'%s'", academic_semester));
                try {
                    if (rs.next()) {
                        int response = db.changeStatusOfAcademicSection(this.connection, academic_year, academic_semester);
                        if (response > 0) {
                            System.out.println("Started New academic section");
                        } else {
                            System.out.println("Some error occurred. Please try again");
                        }
                    } else {
                        System.out.println("Given academic section doesn't exist");
                    }
                } catch (Exception e) {
                    System.out.println("Some error occurred. Please try again");
                }

                break;
            }

        }
    }

    private void addNewAcademicSection() {
        Scanner scanner;
        scanner = new Scanner(System.in);
        boolean isValidInput = false;
        int academic_year = 0;
        String academic_semester = "W";
        boolean isExit = false;
        while (true) {
            while (!isValidInput) {
                System.out.print("Academic Section (YYYY-M |  YYYY-W | YYYY-S): ");
                try {
                    String input = scanner.nextLine();
                    if (input.equals("-1")) {
                        isExit = true;
                        break;
                    }
                    String[] parts = input.split("-");
                    if (parts.length == 2) {
                        parts[1] = parts[1].toUpperCase();
                        try {
                            int year = Integer.parseInt(parts[0]);
                            if (year > 2010 && (parts[1].equals("W") || parts[1].equals("M") || parts[1].equals("S"))) {
                                academic_semester = parts[1];
                                academic_year = year;
                                isValidInput = true;
                            } else {
                                System.out.println("Invalid input! Please give valid input.");
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid input! Please give valid input.");
                        }
                    } else {
                        System.out.println("Invalid input! Please give valid input.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please give valid input.");
                }
            }

            if (isExit) {
                break;
            }

            isValidInput = false;
            String startDate = null;
            while (!isValidInput) {
                System.out.print("Academic Section Start Date (YYYY-MM-DD): ");
                try {
                    String input = scanner.nextLine();
                    if (input.equals("-1")) {
                        isExit = true;
                        break;
                    }
                    DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
                    Date date = null;
                    try {
                        date = dateFormat.parse(input);
                        if (date != null) {
                            startDate = input;
                            isValidInput = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input! Please give valid input.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please give valid input.");
                }
            }

            if (isExit) {
                break;
            }

            isValidInput = false;
            String endDate = null;
            while (!isValidInput) {
                System.out.print("Academic Section End Date (YYYY-MM-DD): ");
                try {
                    String input = scanner.nextLine();
                    if (input.equals("-1")) {
                        isExit = true;
                        break;
                    }
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = dateFormat.parse(input);
                        if (date != null) {
                            endDate = input;
                            isValidInput = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input! Please give valid input.");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input! Please give valid input.");
                }
            }

            if (isExit) {
                break;
            }

            int response = db.addNewRowToAcademicSectionInfo(connection, academic_year, academic_semester.toUpperCase(), startDate, endDate);
            if (response <= 0) {
                System.out.println("Sorry, some error occurred. Please check whether the data already exists");
            } else {
                System.out.println("Information added");
            }
            break;
        }


    }

    private void academicSectionEventsScreen() {
        boolean loopControl = true;
        Scanner scanner;
        scanner = new Scanner(System.in);
        boolean isValidInput;
        int option = 1;

        while (loopControl) {
            System.out.println("_______________Academic Section Events Screen_______________\n");
            System.out.println("\n(1) Back");
            System.out.println("(2) Start Event Course Registration");
            System.out.println("(3) Extend EventCourse Registration");
            System.out.println("(4) Start Event Course Offering by Faculty");
            System.out.println("(5) Extend Event Course Offering by Faculty");
            System.out.println("(6) Start Event Grade Upload by Faculty");
            System.out.println("(7) Extend Event Grade Upload by Faculty");
            System.out.println("(8) End Current Academic Section");
            System.out.println("(9) Add New Academics Section");
            System.out.println("(10) Start new academic section");

            isValidInput = false;

            while (!isValidInput) {
                System.out.print("Navigate to [1....10] : ");

                if (scanner.hasNextInt()) {
                    option = scanner.nextInt();
                    if (option > 0 && option <= 10) {
                        isValidInput = true;
                    } else {
                        System.out.println("Invalid input! Please give valid input");
                    }
                } else {
                    System.out.println("Invalid input! Please give valid input");
                }

            }
            if (option == 1) {

                loopControl = false;

            } else if (option == 2) {

                startAcademicSectionEvent("course_registration_start", "course_registration_end");

            } else if (option == 3) {

                extendAcademicSectionEvent("course_registration_end");

            } else if (option == 4) {

                startAcademicSectionEvent("course_offering_start", "course_offering_end");

            } else if (option == 5) {

                extendAcademicSectionEvent("course_offering_end");

            } else if (option == 6) {

                startAcademicSectionEvent("grade_upload_start", "grade_upload_end");

            } else if (option == 7) {

                extendAcademicSectionEvent("grade_upload_end");
            } else if (option == 8) {
                endCurrentAcademicSection();
            } else if (option == 9){
                addNewAcademicSection();
            } else {
                startNewAcademicSection();
            }
        }
    }

    public void mainScreen() {
        boolean loopControl = true;
        Scanner scanner;
        scanner = new Scanner(System.in);
        boolean isValidInput;
        while (loopControl) {
            System.out.println("\n___________Main Screen____________");
            System.out.println("(1) Course Catalog");
            System.out.println("(2) Course offerings");
            System.out.println("(3) Check Student Record");
            System.out.println("(4) UG Curriculum");
            System.out.println("(5) Start/End Academic Section Events");
            System.out.println("(6) Logout");


            isValidInput = false;
            int option = 1;
            while (!isValidInput) {
                System.out.print("Navigate to [1......6] : ");
                if (scanner.hasNextInt()) {
                    option = scanner.nextInt();
                    if (option > 0 && option <= 6) {
                        isValidInput = true;
                    } else {
                        System.out.println("Invalid input! Please enter a valid input");
                    }
                } else {
                    System.out.println("Invalid input! Please enter a valid input");
                }
            }
            if (option == 1) {

                courseCatalogScreen();

            } else if (option == 2) {

                courseOfferingsScreen();

            } else if (option == 3) {
                Scanner scanner1 = new Scanner(System.in);

                System.out.print("Entry number of the student : ");
                String studentId = scanner1.nextLine();

                studentRecordScreen(studentId);

            } else if (option == 4) {

                UGCurriculumScreen();

            } else if (option == 5) {
                academicSectionEventsScreen();
            } else {
                loopControl = false;
                logout();
            }
        }

    }




}
