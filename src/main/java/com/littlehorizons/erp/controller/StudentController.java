package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Enrollment;
import com.littlehorizons.erp.model.Grade;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.service.StudentService;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.StudentView;

import java.util.List;
import java.util.Optional;

public class StudentController {

    private final StudentService studentService;
    private final StudentView studentView;

    public StudentController(StudentService studentService, StudentView studentView) {
        this.studentService = studentService;
        this.studentView = studentView;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            int option = studentView.displayMenu();
            switch (option) {
                case 1 -> listAllStudents();
                case 2 -> registerStudent();
                case 3 -> searchStudent();
                case 4 -> enrollStudent();
                case 5 -> registerGuardian();
                case 6 -> linkStudentGuardian();
                case 7 -> viewGuardiansForStudent();
                case 8 -> back = true;
            }
        }
    }

    private void listAllStudents() {
        List<Student> students = studentService.getAllStudents();
        studentView.displayStudentList(students);
        InputValidator.pressEnterToContinue();
    }

    private void registerStudent() {
        Student newStudent = studentView.promptNewStudent();
        boolean success = studentService.registerStudent(newStudent);
        if (success) {
            ConsoleFormatter.printSuccess("Estudiante registrado exitosamente con ID: " + newStudent.getId());
        } else {
            ConsoleFormatter.printError("No se pudo registrar al estudiante. Verifique documento o logs.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void searchStudent() {
        String doc = InputValidator.readString("Ingrese el documento del estudiante: ", true);
        Optional<Student> studentOpt = studentService.getStudentByDoc(doc);
        if (studentOpt.isPresent()) {
            Student s = studentOpt.get();
            List<Guardian> guardians = studentService.getGuardiansForStudent(s.getId());
            Optional<Enrollment> enOpt = studentService.getEnrollment(s.getId(), 2024);
            Grade grade = null;
            if (enOpt.isPresent()) {
                grade = studentService.getAllGrades().stream()
                        .filter(g -> g.getId() == enOpt.get().getGradeId())
                        .findFirst().orElse(null);
            }
            studentView.displayStudentDetails(s, guardians, enOpt.orElse(null), grade);
        } else {
            ConsoleFormatter.printWarning("No se encontró ningún estudiante con documento: " + doc);
        }
        InputValidator.pressEnterToContinue();
    }

    private void enrollStudent() {
        int studentId = InputValidator.readInt("Ingrese ID del estudiante: ", 1, 999999);
        Optional<Student> studentOpt = studentService.getStudentById(studentId);
        if (studentOpt.isEmpty()) {
            ConsoleFormatter.printError("Estudiante no encontrado.");
            InputValidator.pressEnterToContinue();
            return;
        }

        List<Grade> grades = studentService.getAllGrades();
        System.out.println("\nGrados Disponibles:");
        for (Grade g : grades) {
            System.out.printf("  %d. %s%n", g.getId(), g.getName());
        }
        int gradeId = InputValidator.readInt("Seleccione ID del Grado: ", 1, grades.size());
        int year = InputValidator.readInt("Año Académico (ej: 2024): ", 2020, 2030);
        String obs = InputValidator.readString("Observaciones de matrícula: ", false);

        boolean success = studentService.enrollStudentInGrade(studentId, gradeId, year, obs);
        if (success) {
            ConsoleFormatter.printSuccess("Matrícula procesada correctamente para el estudiante.");
        } else {
            ConsoleFormatter.printError("Error al procesar la matrícula.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void registerGuardian() {
        Guardian guardian = studentView.promptNewGuardian();
        boolean success = studentService.registerGuardian(guardian);
        if (success) {
            ConsoleFormatter.printSuccess("Acudiente registrado con éxito con ID: " + guardian.getId());
        } else {
            ConsoleFormatter.printError("Error al registrar acudiente. Verifique número de documento.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void linkStudentGuardian() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        int guardianId = InputValidator.readInt("ID del Acudiente: ", 1, 999999);
        boolean isPrimary = InputValidator.readBoolean("¿Es el acudiente principal para comunicaciones?");
        boolean isEmergency = InputValidator.readBoolean("¿Es contacto de emergencia?");

        boolean success = studentService.linkStudentWithGuardian(studentId, guardianId, isPrimary, isEmergency);
        if (success) {
            ConsoleFormatter.printSuccess("Vínculo entre estudiante y acudiente establecido con éxito.");
        } else {
            ConsoleFormatter.printError("No se pudo asociar el acudiente con el estudiante.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewGuardiansForStudent() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        List<Guardian> guardians = studentService.getGuardiansForStudent(studentId);
        if (guardians.isEmpty()) {
            ConsoleFormatter.printWarning("El estudiante no tiene acudientes registrados.");
        } else {
            System.out.println("\nAcudientes del Estudiante #" + studentId + ":");
            for (Guardian g : guardians) {
                System.out.printf("  - %s (%s) | Tel: %s | Email: %s%n",
                        g.getFullName(), g.getRelationship(), g.getMainPhone(), g.getEmail());
            }
        }
        InputValidator.pressEnterToContinue();
    }
}
