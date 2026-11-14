package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Enrollment;
import com.littlehorizons.erp.model.Grade;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;

import java.time.LocalDate;
import java.util.List;

/**
 * Console view for student and guardian operations.
 */
public class StudentView {

    public int displayMenu() {
        ConsoleFormatter.printHeader("MÓDULO DE GESTIÓN DE ESTUDIANTES Y ACUDIENTES");
        System.out.println("  1. Listar todos los estudiantes");
        System.out.println("  2. Registrar nuevo estudiante");
        System.out.println("  3. Consultar estudiante por documento o ID");
        System.out.println("  4. Matricular estudiante en un grado");
        System.out.println("  5. Registrar acudiente");
        System.out.println("  6. Asociar acudiente con estudiante");
        System.out.println("  7. Consultar acudientes de un estudiante");
        System.out.println("  8. Volver al Menú Principal");
        System.out.println();
        return InputValidator.readInt("Seleccione una opción (1-8): ", 1, 8);
    }

    public void displayStudentList(List<Student> students) {
        ConsoleFormatter.printSubHeader("DIRECTORIO GENERAL DE ESTUDIANTES (" + students.size() + " REGISTROS)");
        if (students.isEmpty()) {
            System.out.println("  No hay estudiantes registrados.");
            return;
        }
        System.out.printf("%-4s %-12s %-30s %-12s %-15s %-10s%n", "ID", "DOCUMENTO", "NOMBRE COMPLETO", "FECHA NAC", "TELÉFONO", "ESTADO");
        ConsoleFormatter.printDivider();
        for (Student s : students) {
            System.out.printf("%-4d %-12s %-30s %-12s %-15s %-10s%n",
                    s.getId(),
                    s.getDocType() + " " + s.getDocNumber(),
                    s.getFullName(),
                    s.getBirthDate().toString(),
                    s.getPhone() != null ? s.getPhone() : "N/A",
                    s.getStatus()
            );
        }
    }

    public void displayStudentDetails(Student s, List<Guardian> guardians, Enrollment enrollment, Grade grade) {
        ConsoleFormatter.printSubHeader("FICHA TÉCNICA DEL ESTUDIANTE: " + s.getFullName().toUpperCase());
        System.out.printf("  ID:                   %d%n", s.getId());
        System.out.printf("  Documento:            %s %s%n", s.getDocType(), s.getDocNumber());
        System.out.printf("  Fecha Nacimiento:     %s (Género: %s)%n", s.getBirthDate(), s.getGender());
        System.out.printf("  Dirección:            %s%n", s.getAddress());
        System.out.printf("  Teléfono:             %s%n", s.getPhone());
        System.out.printf("  Correo Electrónico:   %s%n", s.getEmail());
        System.out.printf("  Tipo de Sangre:       %s%n", s.getBloodType());
        System.out.printf("  Alergias:             %s%n", s.getAllergies());
        System.out.printf("  Condiciones Médicas:  %s%n", s.getMedicalConditions());
        System.out.printf("  Estado en Colegio:    %s%n", s.getStatus());
        if (enrollment != null && grade != null) {
            System.out.printf("  Matrícula Actual:     %s (%d) - Estado: %s%n", grade.getName(), enrollment.getAcademicYear(), enrollment.getStatus());
        }

        System.out.println("\n  Acudientes Registrados:");
        if (guardians == null || guardians.isEmpty()) {
            System.out.println("    [Sin acudientes vinculados]");
        } else {
            for (Guardian g : guardians) {
                System.out.printf("    - %s (%s): %s | Tel: %s | Resp. Financiero: %s%n",
                        g.getFullName(), g.getRelationship(), g.getDocType() + " " + g.getDocNumber(),
                        g.getMainPhone(), g.isFinancialResponsible() ? "SÍ" : "NO");
            }
        }
    }

    public Student promptNewStudent() {
        ConsoleFormatter.printSubHeader("REGISTRO DE NUEVO ESTUDIANTE");
        String docType = InputValidator.readOption("Tipo de documento (TI/CC/CE/Pasaporte): ", new String[]{"TI", "CC", "CE", "Pasaporte"});
        String docNumber = InputValidator.readString("Número de documento: ", true);
        String firstName = InputValidator.readString("Primer nombre: ", true);
        String middleName = InputValidator.readString("Segundo nombre (opcional): ", false);
        String lastName = InputValidator.readString("Primer apellido: ", true);
        String secondLastName = InputValidator.readString("Segundo apellido (opcional): ", false);
        LocalDate birthDate = InputValidator.readDate("Fecha de nacimiento (YYYY-MM-DD)", false);
        String gender = InputValidator.readOption("Género (M/F/Otro): ", new String[]{"M", "F", "Otro"});
        String address = InputValidator.readString("Dirección residencial: ", true);
        String phone = InputValidator.readString("Teléfono de contacto: ", true);
        String email = InputValidator.readString("Correo institucional o personal: ", true);
        String bloodType = InputValidator.readString("Tipo de sangre (ej: O+, A+): ", true);
        String allergies = InputValidator.readString("Alergias conocidas: ", false);
        String medicalConditions = InputValidator.readString("Condiciones médicas: ", false);

        return new Student(0, docType, docNumber, firstName, middleName, lastName, secondLastName,
                birthDate, gender, address, phone, email, bloodType, allergies, medicalConditions,
                LocalDate.now(), "Activo");
    }

    public Guardian promptNewGuardian() {
        ConsoleFormatter.printSubHeader("REGISTRO DE NUEVO ACUDIENTE");
        String docType = InputValidator.readOption("Tipo de documento (CC/CE/Pasaporte): ", new String[]{"CC", "CE", "Pasaporte"});
        String docNumber = InputValidator.readString("Número de documento: ", true);
        String firstName = InputValidator.readString("Primer nombre: ", true);
        String middleName = InputValidator.readString("Segundo nombre (opcional): ", false);
        String lastName = InputValidator.readString("Primer apellido: ", true);
        String secondLastName = InputValidator.readString("Segundo apellido (opcional): ", false);
        String relationship = InputValidator.readString("Parentesco (Padre, Madre, Abuelo, Tutor Legal, etc.): ", true);
        String mainPhone = InputValidator.readString("Teléfono principal: ", true);
        String secPhone = InputValidator.readString("Teléfono secundario: ", false);
        String email = InputValidator.readString("Correo electrónico: ", true);
        String address = InputValidator.readString("Dirección: ", true);
        String occupation = InputValidator.readString("Ocupación: ", false);
        boolean isFin = InputValidator.readBoolean("¿Es el responsable financiero de pagos?");

        return new Guardian(0, docType, docNumber, firstName, middleName, lastName, secondLastName,
                relationship, mainPhone, secPhone, email, address, occupation, isFin);
    }
}
