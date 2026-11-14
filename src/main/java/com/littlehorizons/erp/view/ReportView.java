package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.TeacherLoan;
import com.littlehorizons.erp.service.ReportService;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;

import java.util.List;
import java.util.Map;

/**
 * Console view for institutional statistics and executive reports.
 */
public class ReportView {

    public int displayMenu() {
        ConsoleFormatter.printHeader("MÓDULO DE REPORTES Y ESTADÍSTICAS INSTITUCIONALES");
        System.out.println("  1. Cuadro de Honor - Top 5 Estudiantes de Excelencia Académica (Streams)");
        System.out.println("  2. Resumen Financiero y Recaudos por Medio de Pago (Streams)");
        System.out.println("  3. Informe Ejecutivo de Morosidad en Pagos");
        System.out.println("  4. Auditoría de Préstamos Vencidos (Biblioteca y Laboratorio)");
        System.out.println("  5. Volver al Menú Principal");
        System.out.println();
        return InputValidator.readInt("Seleccione una opción (1-5): ", 1, 5);
    }

    public void displayHonorRoll(List<ReportService.StudentPerformance> topStudents, String period, int year) {
        ConsoleFormatter.printSubHeader(String.format("CUADRO DE HONOR - EXCELENCIA ACADÉMICA (%s - %d)", period, year));
        if (topStudents.isEmpty()) {
            System.out.println("  No hay registros académicos suficientes para calcular el cuadro de honor.");
            return;
        }
        int rank = 1;
        System.out.printf("%-6s %-32s %-15s %-10s%n", "PUESTO", "ESTUDIANTE", "DOCUMENTO", "PROMEDIO");
        ConsoleFormatter.printDivider();
        for (ReportService.StudentPerformance sp : topStudents) {
            System.out.printf("%-6s %-32s %-15s %-10.2f / 5.0%n",
                    "#" + rank,
                    sp.getStudent().getFullName(),
                    sp.getStudent().getDocType() + " " + sp.getStudent().getDocNumber(),
                    sp.getGpa());
            rank++;
        }
    }

    public void displayFinancialReport(double totalRevenue, Map<String, Double> breakdown) {
        ConsoleFormatter.printSubHeader("INFORME DE RECAUDO FINANCIERO INSTITUCIONAL");
        System.out.printf("RECAUDO TOTAL CONSOLIDADO: $%,15.2f COP%n", totalRevenue);
        ConsoleFormatter.printDivider();
        System.out.println("Desglose por Método de Pago:");
        breakdown.forEach((method, amount) -> {
            double percent = (totalRevenue > 0) ? (amount / totalRevenue) * 100.0 : 0.0;
            System.out.printf("  - %-18s: $%,13.2f COP (%.1f%%)%n", method, amount, percent);
        });
    }

    public void displayDefaultersSummary(List<Integer> defaulterIds) {
        ConsoleFormatter.printSubHeader("INFORME DE CARTERA MOROSA");
        System.out.printf("Total de estudiantes con morosidad: %d%n", defaulterIds.size());
        if (!defaulterIds.isEmpty()) {
            System.out.println("IDs de estudiantes en mora: " + defaulterIds);
        }
    }

    public void displayOverdueLoansReport(List<StudentLoan> studentLoans, List<TeacherLoan> teacherLoans) {
        ConsoleFormatter.printSubHeader("AUDITORÍA DE PRÉSTAMOS VENCIDOS");
        System.out.printf("Préstamos vencidos en estudiantes: %d%n", studentLoans.size());
        System.out.printf("Préstamos vencidos en profesores:  %d%n", teacherLoans.size());
    }
}
