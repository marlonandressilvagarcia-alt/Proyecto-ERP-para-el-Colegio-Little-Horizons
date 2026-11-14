package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.ResourceCategory;
import com.littlehorizons.erp.model.SchoolResource;
import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.TeacherLoan;
import com.littlehorizons.erp.service.ResourceService;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.ResourceView;

import java.time.LocalDate;
import java.util.List;

public class ResourceController {

    private final ResourceService service;
    private final ResourceView view;

    public ResourceController(ResourceService service, ResourceView view) {
        this.service = service;
        this.view = view;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            int option = view.displayMenu();
            switch (option) {
                case 1 -> listResources();
                case 2 -> registerResource();
                case 3 -> lendToStudent();
                case 4 -> lendToTeacher();
                case 5 -> returnStudentLoan();
                case 6 -> returnTeacherLoan();
                case 7 -> viewActiveLoans();
                case 8 -> viewOverdueLoans();
                case 9 -> back = true;
            }
        }
    }

    private void listResources() {
        List<SchoolResource> resources = service.getAllResources();
        view.displayResourceList(resources);
        InputValidator.pressEnterToContinue();
    }

    private void registerResource() {
        List<ResourceCategory> categories = service.getAllCategories();
        SchoolResource resource = view.promptNewResource(categories);
        boolean success = service.registerResource(resource);
        if (success) {
            ConsoleFormatter.printSuccess("Recurso registrado exitosamente con ID: " + resource.getId());
        } else {
            ConsoleFormatter.printError("Error al registrar el recurso. Verifique si el código ya existe.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void lendToStudent() {
        int resourceId = InputValidator.readInt("ID del Recurso a prestar: ", 1, 999999);
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        LocalDate loanDate = InputValidator.readDate("Fecha de préstamo (YYYY-MM-DD)", true);
        LocalDate returnDate = InputValidator.readDate("Fecha límite de devolución (YYYY-MM-DD)", false);
        String obs = InputValidator.readString("Observaciones (opcional): ", false);

        boolean success = service.lendToStudent(resourceId, studentId, loanDate, returnDate, obs);
        if (success) {
            ConsoleFormatter.printSuccess("Préstamo concedido al estudiante exitosamente. Recurso marcado como 'Prestado'.");
        } else {
            ConsoleFormatter.printError("No se pudo realizar el préstamo (verifique si el recurso está Disponible).");
        }
        InputValidator.pressEnterToContinue();
    }

    private void lendToTeacher() {
        int resourceId = InputValidator.readInt("ID del Recurso a prestar: ", 1, 999999);
        int teacherId = InputValidator.readInt("ID del Profesor: ", 1, 999999);
        LocalDate loanDate = InputValidator.readDate("Fecha de préstamo (YYYY-MM-DD)", true);
        LocalDate returnDate = InputValidator.readDate("Fecha límite de devolución (YYYY-MM-DD)", false);
        String obs = InputValidator.readString("Observaciones (opcional): ", false);

        boolean success = service.lendToTeacher(resourceId, teacherId, loanDate, returnDate, obs);
        if (success) {
            ConsoleFormatter.printSuccess("Préstamo concedido al profesor exitosamente. Recurso marcado como 'Prestado'.");
        } else {
            ConsoleFormatter.printError("No se pudo realizar el préstamo (verifique si el recurso está Disponible).");
        }
        InputValidator.pressEnterToContinue();
    }

    private void returnStudentLoan() {
        int loanId = InputValidator.readInt("ID del Préstamo de Estudiante: ", 1, 999999);
        String obs = InputValidator.readString("Estado en que se entrega el recurso: ", true);

        boolean success = service.returnStudentLoan(loanId, obs);
        if (success) {
            ConsoleFormatter.printSuccess("Devolución registrada exitosamente. El recurso vuelve a estar 'Disponible'.");
        } else {
            ConsoleFormatter.printError("Error al procesar la devolución del préstamo.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void returnTeacherLoan() {
        int loanId = InputValidator.readInt("ID del Préstamo de Profesor: ", 1, 999999);
        String obs = InputValidator.readString("Estado en que se entrega el recurso: ", true);

        boolean success = service.returnTeacherLoan(loanId, obs);
        if (success) {
            ConsoleFormatter.printSuccess("Devolución registrada exitosamente. El recurso vuelve a estar 'Disponible'.");
        } else {
            ConsoleFormatter.printError("Error al procesar la devolución del préstamo.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewActiveLoans() {
        List<StudentLoan> studentLoans = service.getActiveStudentLoans();
        List<TeacherLoan> teacherLoans = service.getActiveTeacherLoans();

        view.displayStudentLoans(studentLoans, "PRÉSTAMOS ACTIVOS");
        System.out.println();
        view.displayTeacherLoans(teacherLoans, "PRÉSTAMOS ACTIVOS");
        InputValidator.pressEnterToContinue();
    }

    private void viewOverdueLoans() {
        List<StudentLoan> studentLoans = service.getOverdueStudentLoans();
        List<TeacherLoan> teacherLoans = service.getOverdueTeacherLoans();

        ConsoleFormatter.printWarning("LISTADO DE PRÉSTAMOS VENCIDOS (FECHA LÍMITE SUPERADA)");
        view.displayStudentLoans(studentLoans, "PRÉSTAMOS VENCIDOS");
        System.out.println();
        view.displayTeacherLoans(teacherLoans, "PRÉSTAMOS VENCIDOS");
        InputValidator.pressEnterToContinue();
    }
}
