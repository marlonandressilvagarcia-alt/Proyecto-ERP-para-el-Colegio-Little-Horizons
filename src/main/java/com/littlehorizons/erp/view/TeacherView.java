package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherAssignment;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;

import java.time.LocalDate;
import java.util.List;

/**
 * Console view for teachers directory and load assignments.
 */
public class TeacherView {

    public int displayMenu() {
        ConsoleFormatter.printHeader("MÓDULO DE GESTIÓN DE PROFESORES Y CARGA ACADÉMICA");
        System.out.println("  1. Listar directorio general de profesores");
        System.out.println("  2. Registrar nuevo profesor");
        System.out.println("  3. Consultar profesor y su carga asignada");
        System.out.println("  4. Asignar materia a un profesor");
        System.out.println("  5. Volver al Menú Principal");
        System.out.println();
        return InputValidator.readInt("Seleccione una opción (1-5): ", 1, 5);
    }

    public void displayTeacherList(List<Teacher> teachers) {
        ConsoleFormatter.printSubHeader("DIRECTORIO GENERAL DE PROFESORES (" + teachers.size() + " REGISTROS)");
        if (teachers.isEmpty()) {
            System.out.println("  No hay profesores registrados.");
            return;
        }
        System.out.printf("%-4s %-12s %-30s %-32s %-15s %-8s%n", "ID", "DOCUMENTO", "NOMBRE COMPLETO", "ESPECIALIZACIÓN", "TELÉFONO", "ESTADO");
        ConsoleFormatter.printDivider();
        for (Teacher t : teachers) {
            System.out.printf("%-4d %-12s %-30s %-32s %-15s %-8s%n",
                    t.getId(),
                    t.getDocType() + " " + t.getDocNumber(),
                    t.getFullName(),
                    t.getSpecialization(),
                    t.getPhone() != null ? t.getPhone() : "N/A",
                    t.getStatus());
        }
    }

    public void displayTeacherDetails(Teacher t, List<TeacherAssignment> assignments) {
        ConsoleFormatter.printSubHeader("FICHA DEL DOCENTE: " + t.getFullName().toUpperCase());
        System.out.printf("  ID:               %d%n", t.getId());
        System.out.printf("  Documento:        %s %s%n", t.getDocType(), t.getDocNumber());
        System.out.printf("  Especialización:  %s%n", t.getSpecialization());
        System.out.printf("  Correo:           %s%n", t.getEmail());
        System.out.printf("  Teléfono:         %s%n", t.getPhone());
        System.out.printf("  Fecha Contrato:   %s%n", t.getHireDate());
        System.out.printf("  Estado:           %s%n", t.getStatus());

        System.out.println("\n  Asignaturas Asignadas para el Período Activo:");
        if (assignments.isEmpty()) {
            System.out.println("    [Sin asignaturas asignadas]");
        } else {
            for (TeacherAssignment ta : assignments) {
                System.out.printf("    - Materia ID: %d | Año: %d | Sección: %s%n",
                        ta.getSubjectId(), ta.getAcademicYear(), ta.getSection());
            }
        }
    }

    public Teacher promptNewTeacher() {
        ConsoleFormatter.printSubHeader("REGISTRO DE NUEVO PROFESOR");
        String docType = InputValidator.readOption("Tipo de documento (CC/CE/Pasaporte): ", new String[]{"CC", "CE", "Pasaporte"});
        String docNumber = InputValidator.readString("Número de documento: ", true);
        String firstName = InputValidator.readString("Primer nombre: ", true);
        String middleName = InputValidator.readString("Segundo nombre (opcional): ", false);
        String lastName = InputValidator.readString("Primer apellido: ", true);
        String secondLastName = InputValidator.readString("Segundo apellido (opcional): ", false);
        String email = InputValidator.readString("Correo electrónico institucional: ", true);
        String phone = InputValidator.readString("Teléfono de contacto: ", true);
        String specialization = InputValidator.readString("Área de especialización docente: ", true);
        LocalDate hireDate = InputValidator.readDate("Fecha de contratación (YYYY-MM-DD)", true);

        return new Teacher(0, docType, docNumber, firstName, middleName, lastName, secondLastName,
                email, phone, specialization, hireDate, "Activo");
    }
}
