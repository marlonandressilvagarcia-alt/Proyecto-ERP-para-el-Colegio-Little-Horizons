package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Daily student attendance entity.
 * States: Presente, Ausente, Tardanza, Excusa Médica
 */
public class Attendance {
    private int id;
    private int studentId;
    private int subjectId;
    private LocalDate attendanceDate;
    private String status;
    private String observations;
    private Integer registeredBy; // teacher id

    public Attendance() {}

    public Attendance(int id, int studentId, int subjectId, LocalDate attendanceDate,
                      String status, String observations, Integer registeredBy) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.observations = observations;
        this.registeredBy = registeredBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public Integer getRegisteredBy() { return registeredBy; }
    public void setRegisteredBy(Integer registeredBy) { this.registeredBy = registeredBy; }

    @Override
    public String toString() {
        return String.format("[%d] Estudiante #%d - Fecha %s: %s", id, studentId, attendanceDate, status);
    }
}
