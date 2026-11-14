package com.littlehorizons.erp.service;

import com.littlehorizons.erp.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service aggregating cross-module reports, analytics, and institutional statistics.
 */
public class ReportService {

    private final StudentService studentService;
    private final AcademicService academicService;
    private final ExtracurricularService extracurricularService;
    private final FinancialService financialService;
    private final ResourceService resourceService;

    public ReportService(StudentService studentService, AcademicService academicService,
                         ExtracurricularService extracurricularService, FinancialService financialService,
                         ResourceService resourceService) {
        this.studentService = studentService;
        this.academicService = academicService;
        this.extracurricularService = extracurricularService;
        this.financialService = financialService;
        this.resourceService = resourceService;
    }

    public static class StudentPerformance {
        private final Student student;
        private final double gpa;

        public StudentPerformance(Student student, double gpa) {
            this.student = student;
            this.gpa = gpa;
        }

        public Student getStudent() { return student; }
        public double getGpa() { return gpa; }
    }

    /**
     * Finds top performing students for a given year and period using Stream API.
     */
    public List<StudentPerformance> getTopStudents(int year, String period, int limit) {
        List<Student> students = studentService.getAllStudents();
        return students.stream()
                .map(s -> new StudentPerformance(s, academicService.calculateStudentPeriodGPA(s.getId(), year, period)))
                .filter(sp -> sp.getGpa() > 0.0)
                .sorted((a, b) -> Double.compare(b.getGpa(), a.getGpa()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Financial statistics: Total revenue collected.
     */
    public double getTotalRevenueCollected() {
        return financialService.getAllPayments().stream()
                .mapToDouble(Payment::getAmountPaid)
                .sum();
    }

    /**
     * Breakdown of payments by payment method using Stream API grouping.
     */
    public Map<String, Double> getRevenueByPaymentMethod() {
        return financialService.getAllPayments().stream()
                .collect(Collectors.groupingBy(
                        Payment::getPaymentMethod,
                        Collectors.summingDouble(Payment::getAmountPaid)
                ));
    }

    public List<Integer> getDefaulterStudentIds() {
        return financialService.getDefaulterStudentIds();
    }

    public List<StudentLoan> getOverdueStudentLoans() {
        return resourceService.getOverdueStudentLoans();
    }

    public List<TeacherLoan> getOverdueTeacherLoans() {
        return resourceService.getOverdueTeacherLoans();
    }
}
