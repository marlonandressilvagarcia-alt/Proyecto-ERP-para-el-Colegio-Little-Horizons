package com.littlehorizons.erp.service;

import com.littlehorizons.erp.model.*;
import com.littlehorizons.erp.util.LoggerUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    /**
     * Genera un reporte en archivo de texto plano (cuadro_de_honor.txt) con la misma
     * estructura visual que se imprime en consola para el Cuadro de Honor.
     *
     * @param topStudents Lista de estudiantes con su GPA calculado
     * @param period Período académico (ej: I, II, III, IV, Final)
     * @param year Año académico (ej: 2024, 2025)
     * @return true si el archivo fue generado correctamente, false en caso de error
     */
    public boolean exportHonorRollToTxt(List<StudentPerformance> topStudents, String period, int year) {
        return exportHonorRollToTxt(topStudents, period, year, "cuadro_de_honor.txt");
    }

    public boolean exportHonorRollToTxt(List<StudentPerformance> topStudents, String period, int year, String fileName) {
        File file = new File(fileName);
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false))) {
            pw.println("================================================================================");
            pw.println("                    COLEGIO INTERNACIONAL LITTLE HORIZONS");
            pw.printf("        CUADRO DE HONOR - EXCELENCIA ACADÉMICA (%s - %d)%n", period, year);
            pw.println("================================================================================");
            pw.printf("Fecha de generación: %s%n%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            if (topStudents == null || topStudents.isEmpty()) {
                pw.println("  No hay registros académicos suficientes para calcular el cuadro de honor.");
            } else {
                pw.printf("%-6s %-32s %-15s %-10s%n", "PUESTO", "ESTUDIANTE", "DOCUMENTO", "PROMEDIO");
                pw.println("--------------------------------------------------------------------------------");
                int rank = 1;
                for (StudentPerformance sp : topStudents) {
                    pw.printf("%-6s %-32s %-15s %-10.2f / 5.0%n",
                            "#" + rank,
                            sp.getStudent().getFullName(),
                            sp.getStudent().getDocType() + " " + sp.getStudent().getDocNumber(),
                            sp.getGpa());
                    rank++;
                }
                pw.println("--------------------------------------------------------------------------------");
                pw.printf("Total de estudiantes destacados: %d%n", topStudents.size());
            }
            pw.println("================================================================================");
            return true;
        } catch (IOException e) {
            LoggerUtil.error("Error al exportar cuadro_de_honor.txt", e);
            return false;
        }
    }
}
