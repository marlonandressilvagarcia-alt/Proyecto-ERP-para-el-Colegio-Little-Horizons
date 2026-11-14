package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.*;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;

import java.time.LocalDate;
import java.util.List;

/**
 * Console view for resource management, loans, and returns.
 */
public class ResourceView {

    public int displayMenu() {
        ConsoleFormatter.printHeader("MÓDULO DE GESTIÓN DE RECURSOS (BIBLIOTECA Y LABORATORIOS)");
        System.out.println("  1. Listar inventario general de recursos");
        System.out.println("  2. Registrar nuevo recurso en inventario");
        System.out.println("  3. Prestar recurso a un ESTUDIANTE");
        System.out.println("  4. Prestar recurso a un PROFESOR");
        System.out.println("  5. Registrar devolución de préstamo (Estudiante)");
        System.out.println("  6. Registrar devolución de préstamo (Profesor)");
        System.out.println("  7. Consultar préstamos activos");
        System.out.println("  8. Consultar préstamos VENCIDOS");
        System.out.println("  9. Volver al Menú Principal");
        System.out.println();
        return InputValidator.readInt("Seleccione una opción (1-9): ", 1, 9);
    }

    public void displayResourceList(List<SchoolResource> resources) {
        ConsoleFormatter.printSubHeader("INVENTARIO DE RECURSOS (" + resources.size() + " REGISTROS)");
        System.out.printf("%-4s %-10s %-42s %-15s %-20s%n", "ID", "CÓDIGO", "NOMBRE DEL RECURSO", "ESTADO", "UBICACIÓN FÍSICA");
        ConsoleFormatter.printDivider();
        for (SchoolResource r : resources) {
            System.out.printf("%-4d %-10s %-42s %-15s %-20s%n",
                    r.getId(), r.getCode(), r.getName(), r.getStatus(), r.getPhysicalLocation());
        }
    }

    public void displayStudentLoans(List<StudentLoan> loans, String title) {
        ConsoleFormatter.printSubHeader(title + " (ESTUDIANTES)");
        if (loans.isEmpty()) {
            System.out.println("  No se encontraron préstamos.");
            return;
        }
        System.out.printf("%-4s %-12s %-15s %-14s %-14s %-10s%n", "ID", "RECURSO ID", "ESTUDIANTE ID", "FECHA PRÉST.", "DEV. ESPERADA", "ESTADO");
        ConsoleFormatter.printDivider();
        for (StudentLoan l : loans) {
            System.out.printf("%-4d %-12d %-15d %-14s %-14s %-10s%n",
                    l.getId(), l.getResourceId(), l.getStudentId(),
                    l.getLoanDate().toString(), l.getExpectedReturnDate().toString(), l.getStatus());
        }
    }

    public void displayTeacherLoans(List<TeacherLoan> loans, String title) {
        ConsoleFormatter.printSubHeader(title + " (PROFESORES)");
        if (loans.isEmpty()) {
            System.out.println("  No se encontraron préstamos.");
            return;
        }
        System.out.printf("%-4s %-12s %-15s %-14s %-14s %-10s%n", "ID", "RECURSO ID", "PROFESOR ID", "FECHA PRÉST.", "DEV. ESPERADA", "ESTADO");
        ConsoleFormatter.printDivider();
        for (TeacherLoan l : loans) {
            System.out.printf("%-4d %-12d %-15d %-14s %-14s %-10s%n",
                    l.getId(), l.getResourceId(), l.getTeacherId(),
                    l.getLoanDate().toString(), l.getExpectedReturnDate().toString(), l.getStatus());
        }
    }

    public SchoolResource promptNewResource(List<ResourceCategory> categories) {
        ConsoleFormatter.printSubHeader("REGISTRO DE NUEVO RECURSO DE INVENTARIO");
        String code = InputValidator.readString("Código único de inventario (ej: LIB-011, LAB-008): ", true);
        String name = InputValidator.readString("Nombre del recurso: ", true);

        System.out.println("\nCategorías disponibles:");
        for (ResourceCategory c : categories) {
            System.out.printf("  %d. %s (%s)%n", c.getId(), c.getName(), c.getDescription());
        }
        int catId = InputValidator.readInt("Seleccione ID de Categoría: ", 1, categories.size());
        String desc = InputValidator.readString("Descripción del recurso: ", false);
        LocalDate acqDate = InputValidator.readDate("Fecha de adquisición (YYYY-MM-DD)", true);
        String loc = InputValidator.readString("Ubicación física (ej: Biblioteca - Estante A1): ", true);

        return new SchoolResource(0, code, name, catId, desc, acqDate, loc, "Disponible");
    }
}
