package org.example;

public class CourseOffering{
    private int offerId;

    private String courseId;
    private String facultyId;
    private int section;
    private int year;
    private String semester;

    private String slot;
    private float cgpa;
    private String[] allowed_batches;
    private String status;

    public CourseOffering(String courseId, int offerId, String faculty, int section, int year, String semester, float cgpa, String[] allowed_batches, String status, String slot) {

        this.offerId = offerId;
        this.courseId = courseId;
        this.facultyId = faculty;
        this.section = section;
        this.year = year;
        this.semester = semester;
        this.slot = slot;
        this.cgpa = cgpa;
        this.allowed_batches = allowed_batches;
        this.status = status;
    }

    public int getOfferId() {
        return offerId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFaculty() {
        return facultyId;
    }

    public int getSection() {
        return section;
    }

    public String getSemester() {
        return semester;
    }

    public int getYear() {
        return year;
    }

    public String getSlot() {
        return slot;
    }

    public float getCgpa() {
        return cgpa;
    }

    public String[] getAllowed_batches() {
        return allowed_batches;
    }

    public String getStatus() {
        return status;
    }
}
