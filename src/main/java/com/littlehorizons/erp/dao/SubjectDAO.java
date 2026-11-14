package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Subject;
import com.littlehorizons.erp.model.SubjectEnrollment;

import java.util.List;
import java.util.Optional;

public interface SubjectDAO {
    boolean create(Subject subject);
    Optional<Subject> findById(int id);
    Optional<Subject> findByCode(String code);
    List<Subject> findAll();
    List<Subject> findByGrade(int gradeId);
    boolean enrollStudentInSubject(SubjectEnrollment enrollment);
    List<Subject> findSubjectsByStudent(int studentId, int year, String period);
}
