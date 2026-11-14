package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.ResourceDAO;
import com.littlehorizons.erp.dao.ResourceLoanDAO;
import com.littlehorizons.erp.dao.impl.ResourceDAOImpl;
import com.littlehorizons.erp.dao.impl.ResourceLoanDAOImpl;
import com.littlehorizons.erp.model.*;
import com.littlehorizons.erp.util.LoggerUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service managing library and laboratory inventory, student/teacher loans, and returns.
 */
public class ResourceService {

    private final ResourceDAO resourceDAO;
    private final ResourceLoanDAO loanDAO;

    public ResourceService() {
        this.resourceDAO = new ResourceDAOImpl();
        this.loanDAO = new ResourceLoanDAOImpl();
    }

    public ResourceService(ResourceDAO resourceDAO, ResourceLoanDAO loanDAO) {
        this.resourceDAO = resourceDAO;
        this.loanDAO = loanDAO;
    }

    // -------------------------------------------------------------
    // Inventory
    // -------------------------------------------------------------
    public List<SchoolResource> getAllResources() {
        return resourceDAO.findAll();
    }

    public Optional<SchoolResource> getResourceById(int id) {
        return resourceDAO.findById(id);
    }

    public Optional<SchoolResource> getResourceByCode(String code) {
        return resourceDAO.findByCode(code);
    }

    public List<SchoolResource> getResourcesByCategory(int categoryId) {
        return resourceDAO.findByCategory(categoryId);
    }

    public List<ResourceCategory> getAllCategories() {
        return resourceDAO.findAllCategories();
    }

    public boolean registerResource(SchoolResource resource) {
        return resourceDAO.create(resource);
    }

    // -------------------------------------------------------------
    // Loans
    // -------------------------------------------------------------
    public boolean lendToStudent(int resourceId, int studentId, LocalDate loanDate, LocalDate expectedReturnDate, String observations) {
        Optional<SchoolResource> resOpt = resourceDAO.findById(resourceId);
        if (resOpt.isEmpty()) {
            LoggerUtil.warn("Resource not found: ID " + resourceId);
            return false;
        }

        SchoolResource resource = resOpt.get();
        if (!"Disponible".equalsIgnoreCase(resource.getStatus())) {
            LoggerUtil.warn("Cannot lend resource. Current status: " + resource.getStatus());
            return false;
        }

        StudentLoan loan = new StudentLoan(0, resourceId, studentId, loanDate, expectedReturnDate, null, "Activo", observations);
        boolean loanCreated = loanDAO.createStudentLoan(loan);
        if (loanCreated) {
            resourceDAO.updateStatus(resourceId, "Prestado");
            LoggerUtil.info("Resource " + resource.getCode() + " lent to student ID: " + studentId);
            return true;
        }
        return false;
    }

    public boolean lendToTeacher(int resourceId, int teacherId, LocalDate loanDate, LocalDate expectedReturnDate, String observations) {
        Optional<SchoolResource> resOpt = resourceDAO.findById(resourceId);
        if (resOpt.isEmpty()) {
            LoggerUtil.warn("Resource not found: ID " + resourceId);
            return false;
        }

        SchoolResource resource = resOpt.get();
        if (!"Disponible".equalsIgnoreCase(resource.getStatus())) {
            LoggerUtil.warn("Cannot lend resource. Current status: " + resource.getStatus());
            return false;
        }

        TeacherLoan loan = new TeacherLoan(0, resourceId, teacherId, loanDate, expectedReturnDate, null, "Activo", observations);
        boolean loanCreated = loanDAO.createTeacherLoan(loan);
        if (loanCreated) {
            resourceDAO.updateStatus(resourceId, "Prestado");
            LoggerUtil.info("Resource " + resource.getCode() + " lent to teacher ID: " + teacherId);
            return true;
        }
        return false;
    }

    public boolean returnStudentLoan(int loanId, String observations) {
        Optional<StudentLoan> loanOpt = loanDAO.findStudentLoanById(loanId);
        if (loanOpt.isEmpty()) {
            LoggerUtil.warn("Student loan not found: ID " + loanId);
            return false;
        }
        StudentLoan loan = loanOpt.get();
        boolean returned = loanDAO.returnStudentLoan(loanId, observations);
        if (returned) {
            resourceDAO.updateStatus(loan.getResourceId(), "Disponible");
            LoggerUtil.info("Student loan ID " + loanId + " returned successfully.");
            return true;
        }
        return false;
    }

    public boolean returnTeacherLoan(int loanId, String observations) {
        Optional<TeacherLoan> loanOpt = loanDAO.findTeacherLoanById(loanId);
        if (loanOpt.isEmpty()) {
            LoggerUtil.warn("Teacher loan not found: ID " + loanId);
            return false;
        }
        TeacherLoan loan = loanOpt.get();
        boolean returned = loanDAO.returnTeacherLoan(loanId, observations);
        if (returned) {
            resourceDAO.updateStatus(loan.getResourceId(), "Disponible");
            LoggerUtil.info("Teacher loan ID " + loanId + " returned successfully.");
            return true;
        }
        return false;
    }

    public List<StudentLoan> getActiveStudentLoans() {
        return loanDAO.findActiveStudentLoans();
    }

    public List<TeacherLoan> getActiveTeacherLoans() {
        return loanDAO.findActiveTeacherLoans();
    }

    public List<StudentLoan> getOverdueStudentLoans() {
        return loanDAO.findOverdueStudentLoans();
    }

    public List<TeacherLoan> getOverdueTeacherLoans() {
        return loanDAO.findOverdueTeacherLoans();
    }
}
