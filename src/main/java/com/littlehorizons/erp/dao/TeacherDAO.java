package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherAssignment;

import java.util.List;
import java.util.Optional;

public interface TeacherDAO {
    boolean create(Teacher teacher);
    Optional<Teacher> findById(int id);
    Optional<Teacher> findByDocNumber(String docNumber);
    List<Teacher> findAll();
    boolean update(Teacher teacher);
    boolean assignSubject(TeacherAssignment assignment);
    List<TeacherAssignment> findAssignmentsByTeacher(int teacherId, int year);
}
