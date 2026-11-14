package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Academic grade record entity. Grade values range between 0.0 and 5.0 (Colombian educational system).
 */
public class AcademicRecord {
    private int id;
    private int studentId;
    private int subjectId;
    private int teacherId;
    private int academicYear;
    private String academicPeriod; // I, II, III, IV, Final
    private double gradeValue;
    private String observations;
    private LocalDate recordDate;

    public AcademicRecord() {}

    public AcademicRecord(int id, int studentId, int subjectId, int teacherId, int academicYear,
                          String academicPeriod, double gradeValue, String observations, LocalDate recordDate) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.teacherId = teacherId;
        this.academicYear = academicYear;
        this.academicPeriod = academicPeriod;
        this.gradeValue = gradeValue;
        this.observations = observations;
        this.recordDate = recordDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public String getAcademicPeriod() { return academicPeriod; }
    public void setAcademicPeriod(String academicPeriod) { this.academicPeriod = academicPeriod; }

    public double getGradeValue() { return gradeValue; }
    public void setGradeValue(double gradeValue) { this.gradeValue = gradeValue; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    @Override
    public String toString() {
        return String.format("[%d] Estudiante #%d - Materia #%d - Periodo %s: %.1f", id, studentId, subjectId, academicPeriod, gradeValue);
    }
}
