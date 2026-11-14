package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Student grade enrollment record for an academic year.
 */
public class Enrollment {
    private int id;
    private int studentId;
    private int gradeId;
    private int academicYear;
    private String status;
    private LocalDate enrollmentDate;
    private LocalDate withdrawalDate;
    private String observations;

    public Enrollment() {}

    public Enrollment(int id, int studentId, int gradeId, int academicYear, String status,
                      LocalDate enrollmentDate, LocalDate withdrawalDate, String observations) {
        this.id = id;
        this.studentId = studentId;
        this.gradeId = gradeId;
        this.academicYear = academicYear;
        this.status = status;
        this.enrollmentDate = enrollmentDate;
        this.withdrawalDate = withdrawalDate;
        this.observations = observations;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getGradeId() { return gradeId; }
    public void setGradeId(int gradeId) { this.gradeId = gradeId; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public LocalDate getWithdrawalDate() { return withdrawalDate; }
    public void setWithdrawalDate(LocalDate withdrawalDate) { this.withdrawalDate = withdrawalDate; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
}
