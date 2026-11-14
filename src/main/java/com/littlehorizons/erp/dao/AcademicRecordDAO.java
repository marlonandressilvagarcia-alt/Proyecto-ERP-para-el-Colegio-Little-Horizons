package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.AcademicRecord;

import java.util.List;
import java.util.Optional;

public interface AcademicRecordDAO {
    boolean createOrUpdate(AcademicRecord record);
    Optional<AcademicRecord> findById(int id);
    List<AcademicRecord> findByStudentAndPeriod(int studentId, int year, String period);
    List<AcademicRecord> findByStudentAndSubject(int studentId, int subjectId, int year);
    List<AcademicRecord> findBySubjectAndPeriod(int subjectId, int year, String period);
    List<AcademicRecord> findByStudentHistory(int studentId);
}
