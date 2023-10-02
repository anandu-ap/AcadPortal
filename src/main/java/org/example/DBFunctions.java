package org.example;

import java.sql.*;
import java.util.List;

public class DBFunctions {
    private static String dbname = "myuserdb";
    private static String username = "myuser";
    private static  String password = "myuser@2";
    public Connection connect_to_db () {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbname, username, password);
        } catch (Exception e) {
            System.out.println("Database connection could not  accomplished");
        }

        return  conn;
    }

    public void addNewRowToUsers(Connection conn, String username, String password, String role) {
        Statement statement;
        try {
            String query = String.format("INSERT INTO acadschema.users(username, password, role) VALUES('%s', '%s', '%s')", username, password, role);
            statement = conn.createStatement();
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
    }

    public void addNewRowToStudents(Connection conn, String studentId, String name, String department, String program, String batch, String contact, String joining_date) {
        Statement statement;
        try {
            String query = String.format("INSERT INTO acadschema.students(studentid, name, department, program, batch, contact, joining_date) VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s')", studentId, name, department, program, batch, contact, joining_date);
            statement = conn.createStatement();
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
    }

    public void addNewRowToFaculties(Connection conn, String instructorid, String name, String department, String contact) {
        Statement statement;
        try {
            String query = String.format("INSERT INTO acadschema.faculties(instructorid, name, department, contact) VALUES('%s', '%s', '%s', '%s')", instructorid, name, department, contact);
            statement = conn.createStatement();
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
    }

    public void addNewRowToAcademicsOffice(Connection conn, String userid, String name, String contact) {
        Statement statement;
        try {
            String query = String.format("INSERT INTO acadschema.academics_office(userid, name, contact) VALUES('%s', '%s', '%s')", userid, name, contact);
            statement = conn.createStatement();
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
    }

    public int addNewRowToCourseCatalog(Connection conn, String course_id, String title, float l, float t, float p, float s, float c, String[] pre_req, String department) {
        PreparedStatement statement;
        try {
            String query = "INSERT INTO acadschema.course_catalog(course_id, title, l, t, p, s, c, pre_req, department) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = conn.prepareStatement(query);
            statement.setString(1, course_id);
            statement.setString(2, title);
            statement.setFloat(3, l);
            statement.setFloat(4, t);
            statement.setFloat(5, p);
            statement.setFloat(6, s);
            statement.setFloat(7, c);
            Array arr = conn.createArrayOf("text", pre_req);
            statement.setArray(8, arr);
            statement.setString(9, department);
            statement.executeUpdate();
            return 0;
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
            return 1;
        }
    }

    public int addNewRowToUGCurriculum(Connection conn, String program, String department, int batch, int semester, String[] core_courses, int program_electives, int science_math_electives, int hs_electives, int open_electives) {
        PreparedStatement statement;
        try {
            String query = "INSERT INTO acadschema.ug_curriculum(program, department, batch, semester, core_courses, program_electives, science_math_electives, hs_electives, open_electives) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = conn.prepareStatement(query);
            statement.setString(1, program);
            statement.setString(2, department);
            statement.setInt(3, batch);
            statement.setInt(4, semester);
            statement.setArray(5, conn.createArrayOf("text", core_courses));
            statement.setInt(6, program_electives);
            statement.setInt(7, science_math_electives);
            statement.setInt(8, hs_electives);
            statement.setInt(9, open_electives);
            statement.executeUpdate();
            System.out.println("Row added");
            return 0;
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
            return 1;
        }
    }

    public int addNewRowToCourseOfferings(Connection conn, String course_id, int section, String semester, int year, String faculty, float req_cgpa, List<String> credit_categorization, String status, String slot) {
        PreparedStatement statement;
        try {
            String query = "INSERT INTO acadschema.course_offerings(course_id, section, semester, year, faculty, req_cgpa, credit_categorization, status, slot) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = conn.prepareStatement(query);
            statement.setString(1, course_id);
            statement.setInt(2, section);
            statement.setString(3, semester);
            statement.setInt(4, year);
            statement.setString(5, faculty);
            statement.setFloat(6, req_cgpa);
            statement.setArray(7, conn.createArrayOf("text", credit_categorization.toArray()));

            statement.setString(8, status);
            statement.setString(9, slot);
            statement.executeUpdate();
            System.out.println("Course offering added");
            return 0;
        } catch (Exception e) {
            System.out.println("Exception : " + e);
            return 1;
        }
    }

    public void createStudentRecord(Connection conn, String tableName) {
        Statement statement;
        try {
            String query = "CREATE TABLE acadschema." + tableName + "(course_offer_id INTEGER PRIMARY KEY NOT NULL, course_id CHAR(5) references acadschema.course_catalog(course_id), grade CHAR(2) references acadschema.grades(grade), category text not null)";
            statement = conn.createStatement();
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
    }

    public void updateStudentRecord(Connection conn, String tableName, int offerId, String grade, String course_id) {
        Statement statement;
        ResultSet rs = null;
        try {
            String check_query = String.format("SELECT * FROM acadschema.%s WHERE course_offer_id = %d", tableName, offerId);
            statement = conn.createStatement();
            rs = statement.executeQuery(check_query);
            if (rs.next()) {
                String query = String.format("UPDATE acadschema.%s SET grade = '%s' WHERE course_offer_id = %d", tableName, grade, offerId);
                statement = conn.createStatement();
                statement.executeUpdate(query);
                System.out.println("Grade updated");
            } else if(!course_id.equals("")) {
                String query = String.format("INSERT INTO acadschema.%s(course_offer_id, course_id, grade)  VALUES(%d, '%s', '%s')", tableName, offerId, course_id, grade);
                statement = conn.createStatement();
                statement.executeUpdate(query);
                System.out.println("Grade updated");
            }
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
    }

    public void createOfferedCourseTable(Connection conn, String tableName) {
        Statement statement;
        try {
            String query = String.format("CREATE TABLE acadschema.%s (studentid TEXT PRIMARY KEY NOT NULL, grade CHAR references acadschema.grades(grade))", tableName);
            statement = conn.createStatement();
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
    }

    public void updateOfferedCourseTable(Connection conn, String studentId, String grade) {
        Statement statement;
        try {
            String query = String.format("UPDATE acadschema.%s SET grade = '%s' WHERE studentid = '%s'", grade, studentId);
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Added grade");
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
    }

    public ResultSet queryCourseOfferingsWithOfferId(Connection conn, int offerId) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT * FROM acadschema.course_offerings WHERE offerid = %d", offerId);
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public ResultSet queryCourseOfferingsWithYearAndSemester(Connection conn, int year, String semester) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT * FROM acadschema.course_offerings WHERE year = %d and semester = '%s'", year, semester);
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public ResultSet queryStudentRecord(Connection conn, String tableName) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT * FROM acadschema.%s AS t1, acadschema.course_offerings AS t2 WHERE t1.course_offer_id = t2.offerid ORDER BY t2.year,CASE t2.semester WHEN 'M' THEN 1 WHEN 'W' THEN 2 WHEN 'S' THEN 3 END", tableName);
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public ResultSet queryFacultyWithFacultyId(Connection conn, String facultyId) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT * FROM acadschema.faculties WHERE instructorid = '%s'", facultyId);
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public ResultSet queryUsers(Connection conn, String username) {
        PreparedStatement statement;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM acadschema.users WHERE username = ?";
            statement = conn.prepareStatement(query);
            statement.setString(1, username);
            rs = statement.executeQuery();

        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public ResultSet queryCourseCatalog(Connection conn, String courseId) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query;
            if (courseId.equals("")) {
                query = "SELECT * FROM acadschema.course_catalog";
            } else {
                query = String.format("SELECT * FROM acadschema.course_catalog WHERE course_id = '%s'", courseId);
            }
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public ResultSet queryCourseCatalogWithCourseId(Connection conn, String courseId) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT * FROM acadschema.course_catalog WHERE course_id = '%s'", courseId);
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public ResultSet queryOfferedCourse(Connection conn, String tableName) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT * FROM acadschema.%s", tableName);
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public ResultSet queryStudentWithStudentId(Connection conn, String studentId) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT * FROM acadschema.students WHERE studentid = '%s'", studentId);
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public ResultSet queryStudentRecordCourseCatalogAndCourseOffering(Connection conn, String tableName) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT t1.*, t2.*, t3.* FROM acadschema.%s AS t1, acadschema.course_offerings AS t2, acadschema.course_catalog as t3 WHERE t1.course_offer_id = t2.offerid and t1.course_id = t3.course_id ORDER BY t2.year,CASE t2.semester WHEN 'M' THEN 1 WHEN 'W' THEN 2 WHEN 'S' THEN 3 END", tableName);
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public ResultSet queryCourseOfferingsWithFacultyIdAndAcademicSection(Connection conn, String username, int year, String semester) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query;
            if (year == -1) {
                query = String.format("SELECT * FROM acadschema.course_offerings AS t1, WHERE t1.faculty = '%s'", username);
            } else {
                query = String.format("SELECT * FROM acadschema.course_offerings AS t1  WHERE t1.faculty = '%s' and and t1.year = %d and t1.semester = '%s'", username, year, semester);
            }

            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public int updateAcademicSectionInfo(Connection conn, String column_name, String value, int year, String semester) {
        Statement statement;
        int result;
        try {
            String query = String.format("UPDATE acadschema.academic_section_info SET %s = '%s' WHERE academic_year = %d and academic_semester = '%s'", column_name, value, year, semester);
            statement = conn.createStatement();
            result = statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
            result = -1;
        }
        return result;
    }

    public int addNewRowToAcademicSectionInfo(Connection conn, int year, String semester, String startDate, String endDate) {
        Statement statement;
        try {
            String query = String.format("INSERT INTO acadschema.academic_section_info(academic_year, academic_semester, academic_section_start, academic_section_end, is_current_section) VALUES(%d, '%s', '%s', '%s', FALSE)", year, semester, startDate, endDate);
            statement = conn.createStatement();
            statement.executeUpdate(query);
            return 1;
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
            return 0;
        }
    }

    public int queryAcademicSectionInfoWithStatus(Connection conn, String status) {
        Statement statement;
        ResultSet rs = null;
        int result = -1;
        try {
            String query = String.format("SELECT COUNT(*) FROM acadschema.academic_section_info WHERE is_current_section = TRUE");
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return result;
    }

    public ResultSet queryAcademicSectionInfo(Connection conn, String column, String value, String column2, String value2) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query;
            if (column2.equals("-1")) {
                query = String.format("SELECT * FROM acadschema.academic_section_info WHERE %s = %s", column, value);
            } else {
                query = String.format("SELECT * FROM acadschema.academic_section_info WHERE %s = %s and %s = %s", column, value, column2, value2);
            }

            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }

        return rs;
    }

    public ResultSet queryUGCurriculum(Connection conn, int year, String department) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT * FROM acadschema.ug_curriculum WHERE batch = %d and department = '%s'", year, department);
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return rs;
    }

    public int getNumberOfRowsUGCurriculumWithYearAndDepartment(Connection conn, int year, String department) {
        Statement statement;
        int result = 0;
        try {
            String query = String.format("SELECT COUNT(*) FROM acadschema.ug_curriculum WHERE batch = %d and department = '%s'", year, department);
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return result;
    }

    public int queryCourseOfferingsWithYearSemesterCourseId(Connection connection, String acad_semester, int acad_year, String courseId) {
        Statement statement;
        int result = 0;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT COUNT(*) FROM acadschema.course_offerings WHERE year = %d and semester = '%s' and course_id = '%s'", acad_year, acad_semester, courseId);
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
            result = -1;
        }
        return result;
    }

    public int changeStatusOfAcademicSection(Connection connection, int academic_year, String academic_semester) {
        Statement statement;
        int result = -1;
        try {
            String query = String.format("UPDATE acadschema.academic_section_info SET is_current_section = TRUE WHERE academic_year =  '%s' and academic_semester = '%s'", academic_year, academic_semester);
            statement = connection.createStatement();
            result = statement.executeUpdate(query);
            return result;
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
            return result;
        }
    }

    public int updateCurrentSectionInAcademicSectionInfo(Connection connection) {
        Statement statement;
        int result = -1;
        try {
            String query = String.format("UPDATE acadschema.academic_section_info SET is_current_section = FALSE WHERE is_current_section = TRUE");
            statement = connection.createStatement();
            result = statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return 0;
    }

    public double getCreditRegisteredInAcademicSection(Connection conn, int year, String semester, String studentId) {
        Statement statement;
        double result = 0;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT SUM(t1.c) FROM acadschema.course_catalog as t1, acadschema.student_record_%s as t2, acadschema.course_offerings as t3 WHERE t2.course_offer_id = t3.offerid and t3.year = %d and t3.semester = '%s' and t2.course_id = t1.course_id", studentId, year, semester);
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
            result = -1;
        }
        return result;
    }

    public double getCreditEarnedInAcademicSection(Connection conn, int year, String semester, String studentId) {
        Statement statement;
        double result = 0;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT SUM(t1.c) FROM acadschema.course_catalog as t1, acadschema.student_record_%s as t2, acadschema.course_offerings as t3 WHERE t2.course_offer_id = t3.offerid and t3.year = %d and t3.semester = '%s' and t2.course_id = t1.course_id and t2.grade != 'E' and t2.grade != 'F' and t2.grade != 'I' and t2.grade != 'W' and t2.grade != 'NA' and t2.grade != 'NP' and t2.grade != 'NF'", studentId, year, semester);
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            if (rs != null) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
            result = -1;
        }
        return result;
    }

    public int addNewRowToStudentRecord(Connection conn, String courseId, int offerId, String category, String studentId) {
        Statement statement;
        int result = -1;
        try {
            String query = String.format("INSERT INTO acadschema.student_record_%s(course_id, course_offer_id, category, grade) VALUES('%s', '%s', '%s', 'NA')", studentId, courseId, offerId, category);
            statement = conn.createStatement();
            result = statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return result;
    }

    public int addNewRowToOfferedCourse(Connection conn, String studentId, String courseId, int year, String semester, int section) {
        Statement statement;
        int result;
        try {
            String query = String.format("INSERT INTO acadschema.%s%d%s%d(studentid, grade) VALUES('%s', 'NA')", courseId, year, semester, section, studentId);
            statement = conn.createStatement();
            result = statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
            result = -1;
        }
        return result;
    }

    public int checkIfCourseAlreadyTaken(Connection connection, String courseId, String studentId) {
        Statement statement;
        int result = -1;
        ResultSet rs = null;
        try {
            String query = String.format("SELECT COUNT(*) FROM acadschema.student_record_%s WHERE course_id = '%s' and (grade != 'E' or grade != 'F' or grade != 'I' or grade != 'W')", studentId, courseId);
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            if (rs != null) {
                if(rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Task could not  accomplished");
        }
        return  result;
    }

}
