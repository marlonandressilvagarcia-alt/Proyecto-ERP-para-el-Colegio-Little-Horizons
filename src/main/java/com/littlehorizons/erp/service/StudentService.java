package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.GuardianDAO;
import com.littlehorizons.erp.dao.StudentDAO;
import com.littlehorizons.erp.dao.impl.GuardianDAOImpl;
import com.littlehorizons.erp.dao.impl.StudentDAOImpl;
import com.littlehorizons.erp.model.Enrollment;
import com.littlehorizons.erp.model.Grade;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.LoggerUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service orchestrating Student and Guardian management operations.
 */
public class StudentService {

    private final StudentDAO studentDAO;
    private final GuardianDAO guardianDAO;

    public StudentService() {
        this.studentDAO = new StudentDAOImpl();
        this.guardianDAO = new GuardianDAOImpl();
    }

    public StudentService(StudentDAO studentDAO, GuardianDAO guardianDAO) {
        this.studentDAO = studentDAO;
        this.guardianDAO = guardianDAO;
    }

    public boolean registerStudent(Student student) {
        if (studentDAO.findByDocNumber(student.getDocNumber()).isPresent()) {
            LoggerUtil.warn("Student registration failed: document number already exists " + student.getDocNumber());
            return false;
        }
        return studentDAO.create(student);
    }

    public Optional<Student> getStudentById(int id) {
        return studentDAO.findById(id);
    }

    public Optional<Student> getStudentByDoc(String docNumber) {
        return studentDAO.findByDocNumber(docNumber);
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    public boolean updateStudent(Student student) {
        return studentDAO.update(student);
    }

    public boolean registerGuardian(Guardian guardian) {
        if (guardianDAO.findByDocNumber(guardian.getDocNumber()).isPresent()) {
            LoggerUtil.warn("Guardian registration failed: document number already exists " + guardian.getDocNumber());
            return false;
        }
        return guardianDAO.create(guardian);
    }

    public Optional<Guardian> getGuardianById(int id) {
        return guardianDAO.findById(id);
    }

    public List<Guardian> getAllGuardians() {
        return guardianDAO.findAll();
    }

    public boolean linkStudentWithGuardian(int studentId, int guardianId, boolean isPrimary, boolean isEmergency) {
        return studentDAO.linkStudentGuardian(studentId, guardianId, isPrimary, isEmergency);
    }

    public List<Guardian> getGuardiansForStudent(int studentId) {
        return studentDAO.findGuardiansByStudentId(studentId);
    }

    public List<Student> getStudentsForGuardian(int guardianId) {
        return guardianDAO.findStudentsByGuardianId(guardianId);
    }

    public boolean enrollStudentInGrade(int studentId, int gradeId, int year, String observations) {
        Enrollment enrollment = new Enrollment(0, studentId, gradeId, year, "Activa", LocalDate.now(), null, observations);
        return studentDAO.enrollStudent(enrollment);
    }

    public Optional<Enrollment> getEnrollment(int studentId, int year) {
        return studentDAO.getStudentEnrollment(studentId, year);
    }

    public List<Grade> getAllGrades() {
        return studentDAO.findAllGrades();
    }
}
