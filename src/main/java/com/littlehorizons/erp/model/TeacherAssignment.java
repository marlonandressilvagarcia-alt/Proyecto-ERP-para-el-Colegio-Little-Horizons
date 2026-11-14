package com.littlehorizons.erp.model;

/**
 * Assignment of a Subject to a Teacher for an academic year and section.
 */
public class TeacherAssignment {
    private int id;
    private int teacherId;
    private int subjectId;
    private int academicYear;
    private String section;

    public TeacherAssignment() {}

    public TeacherAssignment(int id, int teacherId, int subjectId, int academicYear, String section) {
        this.id = id;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.academicYear = academicYear;
        this.section = section;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
}
