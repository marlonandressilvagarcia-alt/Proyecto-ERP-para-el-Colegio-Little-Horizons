package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.TeacherLoan;

import java.util.List;
import java.util.Optional;

public interface ResourceLoanDAO {
    boolean createStudentLoan(StudentLoan loan);
    boolean createTeacherLoan(TeacherLoan loan);
    boolean returnStudentLoan(int loanId, String observations);
    boolean returnTeacherLoan(int loanId, String observations);
    Optional<StudentLoan> findStudentLoanById(int id);
    Optional<TeacherLoan> findTeacherLoanById(int id);
    List<StudentLoan> findActiveStudentLoans();
    List<TeacherLoan> findActiveTeacherLoans();
    List<StudentLoan> findOverdueStudentLoans();
    List<TeacherLoan> findOverdueTeacherLoans();
}
