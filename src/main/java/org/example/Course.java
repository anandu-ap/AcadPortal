package org.example;

import java.sql.Connection;

public class Course {
    private final String course_id;
    private final String title;
    private final float l;
    private final float t;
    private final float p;
    private final float s;
    private final float c;
    private final String offeringDepartment;
    private final String[] preReq;

    public Course(String course_id, String title, float l, float t, float p, float s, float c, String offeringDepartment, String[] preReq) {
        this.course_id = course_id;
        this.title = title;
        this.l = l;
        this.t = t;
        this.p = p;
        this.s = s;
        this.c = c;
        this.offeringDepartment = offeringDepartment;
        this.preReq = preReq;
    }

    public String getCourse_id() {
        return course_id;
    }

    public String getTitle() {
        return title;
    }

    public String getOfferingDepartment() {
        return offeringDepartment;
    }

    public String[] getPreReq() {
        return preReq;
    }

    public float getL() {
        return l;
    }

    public float getT() {
        return t;
    }

    public float getP() {
        return p;
    }

    public float getS() {
        return s;
    }

    public float getC() {
        return c;
    }

    public void addNewCourse(Connection connection) {
        DBFunctions db = new DBFunctions();
        db.addNewRowToCourseCatalog(
                connection,
                course_id,
                title,
                l,
                t,
                p,
                s,
                c,
                preReq,
                offeringDepartment
        );
    }


}
