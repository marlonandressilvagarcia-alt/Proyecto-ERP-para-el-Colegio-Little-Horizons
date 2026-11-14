package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;

import java.util.List;
import java.util.Optional;

public interface GuardianDAO {
    boolean create(Guardian guardian);
    Optional<Guardian> findById(int id);
    Optional<Guardian> findByDocNumber(String docNumber);
    List<Guardian> findAll();
    boolean update(Guardian guardian);
    List<Student> findStudentsByGuardianId(int guardianId);
}
