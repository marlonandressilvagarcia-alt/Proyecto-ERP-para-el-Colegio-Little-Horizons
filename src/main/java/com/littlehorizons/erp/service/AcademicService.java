package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.AcademicRecordDAO;
import com.littlehorizons.erp.dao.AttendanceDAO;
import com.littlehorizons.erp.dao.SubjectDAO;
import com.littlehorizons.erp.dao.TeacherDAO;
import com.littlehorizons.erp.dao.impl.AcademicRecordDAOImpl;
import com.littlehorizons.erp.dao.impl.AttendanceDAOImpl;
import com.littlehorizons.erp.dao.impl.SubjectDAOImpl;
import com.littlehorizons.erp.dao.impl.TeacherDAOImpl;
import com.littlehorizons.erp.model.*;
import com.littlehorizons.erp.util.LoggerUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Service managing academic operations, teacher assignments, grades, averages calculation, and attendance.
 */
public class AcademicService {

    private final TeacherDAO teacherDAO;
    private final SubjectDAO subjectDAO;
    private final AcademicRecordDAO recordDAO;
    private final AttendanceDAO attendanceDAO;

    public AcademicService() {
        this.teacherDAO = new TeacherDAOImpl();
        this.subjectDAO = new SubjectDAOImpl();
        this.recordDAO = new AcademicRecordDAOImpl();
        this.attendanceDAO = new AttendanceDAOImpl();
    }

    public AcademicService(TeacherDAO teacherDAO, SubjectDAO subjectDAO, AcademicRecordDAO recordDAO, AttendanceDAO attendanceDAO) {
        this.teacherDAO = teacherDAO;
        this.subjectDAO = subjectDAO;
        this.recordDAO = recordDAO;
        this.attendanceDAO = attendanceDAO;
    }

    // -------------------------------------------------------------
    // Teachers & Assignments
    // -------------------------------------------------------------
    public boolean registerTeacher(Teacher teacher) {
        if (teacherDAO.findByDocNumber(teacher.getDocNumber()).isPresent()) {
            LoggerUtil.warn("Teacher doc number already registered: " + teacher.getDocNumber());
            return false;
        }
        return teacherDAO.create(teacher);
    }

    public List<Teacher> getAllTeachers() {
        return teacherDAO.findAll();
    }

    public Optional<Teacher> getTeacherById(int id) {
        return teacherDAO.findById(id);
    }

    public boolean assignSubjectToTeacher(int teacherId, int subjectId, int year, String section) {
        TeacherAssignment assignment = new TeacherAssignment(0, teacherId, subjectId, year, section);
        return teacherDAO.assignSubject(assignment);
    }

    public List<TeacherAssignment> getTeacherAssignments(int teacherId, int year) {
        return teacherDAO.findAssignmentsByTeacher(teacherId, year);
    }

    // -------------------------------------------------------------
    // Subjects & Student Enrollment
    // -------------------------------------------------------------
    public List<Subject> getAllSubjects() {
        return subjectDAO.findAll();
    }

    public List<Subject> getSubjectsByGrade(int gradeId) {
        return subjectDAO.findByGrade(gradeId);
    }

    public Optional<Subject> getSubjectById(int id) {
        return subjectDAO.findById(id);
    }

    public boolean enrollStudentInSubject(int studentId, int subjectId, int year, String period, String section) {
        SubjectEnrollment enrollment = new SubjectEnrollment(0, studentId, subjectId, year, period, section, LocalDate.now(), "Activa");
        return subjectDAO.enrollStudentInSubject(enrollment);
    }

    public List<Subject> getSubjectsByStudent(int studentId, int year, String period) {
        return subjectDAO.findSubjectsByStudent(studentId, year, period);
    }

    // -------------------------------------------------------------
    // Grades & Calculations (Using Stream API and Lambdas)
    // -------------------------------------------------------------
    public boolean recordGrade(int studentId, int subjectId, int teacherId, int year, String period, double gradeValue, String observations) {
        if (gradeValue < 0.0 || gradeValue > 5.0) {
            LoggerUtil.warn("Invalid grade value attempted: " + gradeValue);
            return false;
        }
        AcademicRecord record = new AcademicRecord(0, studentId, subjectId, teacherId, year, period, gradeValue, observations, LocalDate.now());
        return recordDAO.createOrUpdate(record);
    }

    public List<AcademicRecord> getStudentGradesByPeriod(int studentId, int year, String period) {
        return recordDAO.findByStudentAndPeriod(studentId, year, period);
    }

    public List<AcademicRecord> getStudentAcademicHistory(int studentId) {
        return recordDAO.findByStudentHistory(studentId);
    }

    /**
     * Calculates the student's overall GPA for a specific academic period using Java Stream API.
     */
    public double calculateStudentPeriodGPA(int studentId, int year, String period) {
        List<AcademicRecord> records = recordDAO.findByStudentAndPeriod(studentId, year, period);
        OptionalDouble avg = records.stream()
                .mapToDouble(AcademicRecord::getGradeValue)
                .average();
        return avg.isPresent() ? Math.round(avg.getAsDouble() * 10.0) / 10.0 : 0.0;
    }

    /**
     * Calculates the average grade of a student in a specific subject across periods using Java Stream API.
     */
    public double calculateStudentSubjectAverage(int studentId, int subjectId, int year) {
        List<AcademicRecord> records = recordDAO.findByStudentAndSubject(studentId, subjectId, year);
        OptionalDouble avg = records.stream()
                .mapToDouble(AcademicRecord::getGradeValue)
                .average();
        return avg.isPresent() ? Math.round(avg.getAsDouble() * 10.0) / 10.0 : 0.0;
    }

    /**
     * Calculates the course general average for a subject in a specific period using Java Stream API.
     */
    public double calculateSubjectCourseAverage(int subjectId, int year, String period) {
        List<AcademicRecord> records = recordDAO.findBySubjectAndPeriod(subjectId, year, period);
        OptionalDouble avg = records.stream()
                .mapToDouble(AcademicRecord::getGradeValue)
                .average();
        return avg.isPresent() ? Math.round(avg.getAsDouble() * 10.0) / 10.0 : 0.0;
    }

    // -------------------------------------------------------------
    // Attendance Tracking
    // -------------------------------------------------------------
    public boolean recordAttendance(int studentId, int subjectId, LocalDate date, String status, String observations, Integer teacherId) {
        Attendance attendance = new Attendance(0, studentId, subjectId, date, status, observations, teacherId);
        return attendanceDAO.recordAttendance(attendance);
    }

    public List<Attendance> getAttendanceForStudent(int studentId, int subjectId) {
        return attendanceDAO.findByStudent(studentId, subjectId);
    }

    public List<Attendance> getUnexcusedAbsences(int studentId) {
        return attendanceDAO.findUnexcusedAbsences(studentId);
    }

    public boolean hasAttendanceAlert(int studentId) {
        return attendanceDAO.findUnexcusedAbsences(studentId).size() >= 3;
    }
}
