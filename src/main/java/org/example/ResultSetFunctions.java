package org.example;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResultSetFunctions {
    public String getString(ResultSet rs, String columnLabel, int dataType) {
        String result = null;
        try {
            if (dataType == 0) {
                rs.next();
                result = rs.getString(columnLabel);
            } else {
                rs.next();
                result = Integer.toString(rs.getInt(columnLabel));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
//        System.out.println("Result : " + result);
        return result;
    }

    public List<Course> getCourses(ResultSet rs) {
        List<Course> result = new ArrayList<>();
        try {
            while (rs.next()) {
                String course_id = rs.getString("course_id");
                String title = rs.getString("title");
                String offering_department = rs.getString("department");
                float l = rs.getFloat("l");
                float t = rs.getFloat("t");
                float p = rs.getFloat("p");
                float s = rs.getFloat("s");
                float c = rs.getFloat("c");
                Array pre_req_Array = rs.getArray("pre_req");
                String[] pre_req = (String[]) pre_req_Array.getArray();

                Course course = new Course(course_id, title, l, t, p, s, c, offering_department, pre_req);
                result.add(course);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }

    public Course getCourse(ResultSet rs) {
        Course result = null;
        try {
            rs.next();
            String course_id = rs.getString("course_id");
            String title = rs.getString("title");
            String offering_department = rs.getString("department");
            float l = rs.getFloat("l");
            float t = rs.getFloat("t");
            float p = rs.getFloat("p");
            float s = rs.getFloat("s");
            float c = rs.getFloat("c");
            Array pre_req_Array = rs.getArray("pre_req");
            String[] pre_req = (String[]) pre_req_Array.getArray();

            result = new Course(course_id, title, l, t, p, s, c, offering_department, pre_req);

        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }

    public List<CourseOffering> getCourseOfferings(ResultSet rs) {
        List<CourseOffering> result = new ArrayList<>();
        try {
            while (rs.next()) {
                int offerId = rs.getInt("offerid");
                String courseId = rs.getString("course_id");
                int section = rs.getInt("section");
                int year = rs.getInt("year");
                String semester = rs.getString("semester");
                float cgpa = rs.getFloat("req_cgpa");
                Array arr = rs.getArray("credit_categorization");
                String[] allowed_batches = (String[]) arr.getArray();
                String slot = rs.getString("slot");
                String facultyId = rs.getString("faculty");
                String status = rs.getString("status");

                CourseOffering courseOffering = new CourseOffering(courseId, offerId, facultyId, section, year, semester, cgpa, allowed_batches, status, slot);
                result.add(courseOffering);
            }
        } catch (Exception e) {
            System.out.println("Some error occurred");
        }
        return result;
    }

    public List<List<String>> getStudentIdAndGrade(ResultSet rs) {
        List<List<String>> result = new ArrayList<>();
        try {
            while (rs.next()) {
                String studentId = rs.getString("studentid");
                String grade = rs.getString("grade");
                List<String> element = new ArrayList<>();
                element.add(studentId);
                element.add(grade);
                result.add(element);
            }
        } catch (Exception e) {
            System.out.println("Some error occurred");
        }
        return result;
    }



}
