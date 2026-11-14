package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Enrollment;
import com.littlehorizons.erp.model.Grade;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentDAO {
    boolean create(Student student);
    Optional<Student> findById(int id);
    Optional<Student> findByDocNumber(String docNumber);
    List<Student> findAll();
    boolean update(Student student);
    boolean delete(int id);
    List<Guardian> findGuardiansByStudentId(int studentId);
    boolean linkStudentGuardian(int studentId, int guardianId, boolean isPrimary, boolean isEmergency);
    Optional<Enrollment> getStudentEnrollment(int studentId, int year);
    boolean enrollStudent(Enrollment enrollment);
    List<Grade> findAllGrades();
    Optional<Grade> findGradeById(int id);
}
