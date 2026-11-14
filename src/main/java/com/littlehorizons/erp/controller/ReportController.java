package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.TeacherLoan;
import com.littlehorizons.erp.service.ReportService;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.ReportView;

import java.util.List;
import java.util.Map;

public class ReportController {

    private final ReportService reportService;
    private final ReportView reportView;

    public ReportController(ReportService reportService, ReportView reportView) {
        this.reportService = reportService;
        this.reportView = reportView;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            int option = reportView.displayMenu();
            switch (option) {
                case 1 -> generateHonorRoll();
                case 2 -> generateFinancialReport();
                case 3 -> generateDefaultersReport();
                case 4 -> generateOverdueLoansReport();
                case 5 -> back = true;
            }
        }
    }

    private void generateHonorRoll() {
        int year = InputValidator.readInt("Año Académico (ej: 2024): ", 2020, 2030);
        String period = InputValidator.readOption("Período (I, II, III, IV, Final): ",
                new String[]{"I", "II", "III", "IV", "Final"});

        List<ReportService.StudentPerformance> topStudents = reportService.getTopStudents(year, period, 5);
        reportView.displayHonorRoll(topStudents, period, year);
        InputValidator.pressEnterToContinue();
    }

    private void generateFinancialReport() {
        double total = reportService.getTotalRevenueCollected();
        Map<String, Double> breakdown = reportService.getRevenueByPaymentMethod();
        reportView.displayFinancialReport(total, breakdown);
        InputValidator.pressEnterToContinue();
    }

    private void generateDefaultersReport() {
        List<Integer> defaulterIds = reportService.getDefaulterStudentIds();
        reportView.displayDefaultersSummary(defaulterIds);
        InputValidator.pressEnterToContinue();
    }

    private void generateOverdueLoansReport() {
        List<StudentLoan> studentLoans = reportService.getOverdueStudentLoans();
        List<TeacherLoan> teacherLoans = reportService.getOverdueTeacherLoans();
        reportView.displayOverdueLoansReport(studentLoans, teacherLoans);
        InputValidator.pressEnterToContinue();
    }
}
