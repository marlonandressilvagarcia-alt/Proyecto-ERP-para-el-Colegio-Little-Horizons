package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherAssignment;
import com.littlehorizons.erp.service.AcademicService;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.TeacherView;

import java.util.List;
import java.util.Optional;

public class TeacherController {

    private final AcademicService academicService;
    private final TeacherView teacherView;

    public TeacherController(AcademicService academicService, TeacherView teacherView) {
        this.academicService = academicService;
        this.teacherView = teacherView;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            int option = teacherView.displayMenu();
            switch (option) {
                case 1 -> listTeachers();
                case 2 -> registerTeacher();
                case 3 -> consultTeacher();
                case 4 -> assignSubject();
                case 5 -> back = true;
            }
        }
    }

    private void listTeachers() {
        List<Teacher> teachers = academicService.getAllTeachers();
        teacherView.displayTeacherList(teachers);
        InputValidator.pressEnterToContinue();
    }

    private void registerTeacher() {
        Teacher teacher = teacherView.promptNewTeacher();
        boolean success = academicService.registerTeacher(teacher);
        if (success) {
            ConsoleFormatter.printSuccess("Profesor registrado exitosamente con ID: " + teacher.getId());
        } else {
            ConsoleFormatter.printError("Error al registrar profesor. Verifique correo o documento.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void consultTeacher() {
        int id = InputValidator.readInt("Ingrese ID del Profesor: ", 1, 999999);
        Optional<Teacher> opt = academicService.getTeacherById(id);
        if (opt.isPresent()) {
            List<TeacherAssignment> assignments = academicService.getTeacherAssignments(id, 2024);
            teacherView.displayTeacherDetails(opt.get(), assignments);
        } else {
            ConsoleFormatter.printWarning("Profesor no encontrado con ID: " + id);
        }
        InputValidator.pressEnterToContinue();
    }

    private void assignSubject() {
        int teacherId = InputValidator.readInt("ID del Profesor: ", 1, 999999);
        int subjectId = InputValidator.readInt("ID de la Materia: ", 1, 999999);
        int year = InputValidator.readInt("Año Académico (ej: 2024): ", 2020, 2030);
        String section = InputValidator.readString("Sección o Grupo (A, B, C): ", true);

        boolean success = academicService.assignSubjectToTeacher(teacherId, subjectId, year, section);
        if (success) {
            ConsoleFormatter.printSuccess("Asignación de carga académica guardada exitosamente.");
        } else {
            ConsoleFormatter.printError("Error al asignar materia al profesor.");
        }
        InputValidator.pressEnterToContinue();
    }
}
