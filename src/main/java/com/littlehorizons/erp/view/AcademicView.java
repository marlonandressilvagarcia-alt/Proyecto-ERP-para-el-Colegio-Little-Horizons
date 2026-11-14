package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.AcademicRecord;
import com.littlehorizons.erp.model.Attendance;
import com.littlehorizons.erp.model.Subject;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;

import java.util.List;

/**
 * Console view for academic management, grades, averages, and attendance.
 */
public class AcademicView {

    public int displayMenu() {
        ConsoleFormatter.printHeader("MÓDULO DE GESTIÓN ACADÉMICA");
        System.out.println("  1. Registrar calificación de estudiante");
        System.out.println("  2. Consultar boletín / notas de estudiante por período");
        System.out.println("  3. Calcular promedio general (GPA) del estudiante en un período");
        System.out.println("  4. Calcular promedio de un estudiante en una materia");
        System.out.println("  5. Calcular promedio general del curso en una materia");
        System.out.println("  6. Consultar historial académico completo de estudiante");
        System.out.println("  7. Registrar asistencia diaria de estudiante");
        System.out.println("  8. Consultar alertas de inasistencias (> 3 ausencias)");
        System.out.println("  9. Listar asignaturas / materias del colegio");
        System.out.println("  10. Volver al Menú Principal");
        System.out.println();
        return InputValidator.readInt("Seleccione una opción (1-10): ", 1, 10);
    }

    public void displayGradeRecords(List<AcademicRecord> records, double gpa) {
        ConsoleFormatter.printSubHeader("BOLETÍN DE CALIFICACIONES");
        if (records.isEmpty()) {
            System.out.println("  No se encontraron notas registradas para este período.");
            return;
        }
        System.out.printf("%-6s %-12s %-12s %-8s %-30s%n", "ID", "MATERIA ID", "PERÍODO", "NOTA", "OBSERVACIONES");
        ConsoleFormatter.printDivider();
        for (AcademicRecord r : records) {
            System.out.printf("%-6d %-12d %-12s %-8.1f %-30s%n",
                    r.getId(), r.getSubjectId(), r.getAcademicPeriod(), r.getGradeValue(),
                    r.getObservations() != null ? r.getObservations() : "");
        }
        ConsoleFormatter.printDivider();
        System.out.printf("PROMEDIO GENERAL DEL PERÍODO (GPA): %.2f / 5.0%n", gpa);
    }

    public void displayAcademicHistory(int studentId, List<AcademicRecord> history) {
        ConsoleFormatter.printSubHeader("HISTORIAL ACADÉMICO COMPLETO - ESTUDIANTE #" + studentId);
        if (history.isEmpty()) {
            System.out.println("  El estudiante no posee historial de notas registradas.");
            return;
        }
        System.out.printf("%-6s %-8s %-10s %-12s %-8s %-30s%n", "AÑO", "PERIODO", "MATERIA", "PROFESOR", "NOTA", "OBSERVACIONES");
        ConsoleFormatter.printDivider();
        for (AcademicRecord r : history) {
            System.out.printf("%-6d %-8s %-10d %-12d %-8.1f %-30s%n",
                    r.getAcademicYear(), r.getAcademicPeriod(), r.getSubjectId(),
                    r.getTeacherId(), r.getGradeValue(),
                    r.getObservations() != null ? r.getObservations() : "");
        }
    }

    public void displaySubjects(List<Subject> subjects) {
        ConsoleFormatter.printSubHeader("CATÁLOGO DE ASIGNATURAS (" + subjects.size() + " REGISTROS)");
        System.out.printf("%-4s %-10s %-40s %-8s %-10s%n", "ID", "CÓDIGO", "NOMBRE DE LA ASIGNATURA", "GRADO ID", "HORAS/SEM");
        ConsoleFormatter.printDivider();
        for (Subject s : subjects) {
            System.out.printf("%-4d %-10s %-40s %-8d %-10d%n",
                    s.getId(), s.getCode(), s.getName(), s.getGradeId(), s.getWeeklyHours());
        }
    }

    public void displayAbsences(int studentId, List<Attendance> absences) {
        ConsoleFormatter.printSubHeader("REPORTE DE AUSENCIAS - ESTUDIANTE #" + studentId);
        if (absences.isEmpty()) {
            System.out.println("  El estudiante no tiene ausencias injustificadas registradas.");
            return;
        }
        System.out.printf("Total de ausencias: %d%n", absences.size());
        if (absences.size() >= 3) {
            ConsoleFormatter.printWarning("¡ALERTA CRÍTICA!: Estudiante supera el umbral de 3 faltas. Requiere citación a acudiente.");
        }
        for (Attendance a : absences) {
            System.out.printf("  - Fecha: %s | Materia ID: %d | Obs: %s%n",
                    a.getAttendanceDate(), a.getSubjectId(), a.getObservations());
        }
    }
}
