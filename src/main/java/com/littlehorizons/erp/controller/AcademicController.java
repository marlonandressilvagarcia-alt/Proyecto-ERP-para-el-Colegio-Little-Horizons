package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.AcademicRecord;
import com.littlehorizons.erp.model.Attendance;
import com.littlehorizons.erp.model.Subject;
import com.littlehorizons.erp.service.AcademicService;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.AcademicView;

import java.time.LocalDate;
import java.util.List;

public class AcademicController {

    private final AcademicService academicService;
    private final AcademicView academicView;

    public AcademicController(AcademicService academicService, AcademicView academicView) {
        this.academicService = academicService;
        this.academicView = academicView;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            int option = academicView.displayMenu();
            switch (option) {
                case 1 -> recordGrade();
                case 2 -> consultGradesByPeriod();
                case 3 -> calculateStudentPeriodGPA();
                case 4 -> calculateStudentSubjectAverage();
                case 5 -> calculateSubjectCourseAverage();
                case 6 -> consultAcademicHistory();
                case 7 -> recordAttendance();
                case 8 -> checkAttendanceAlerts();
                case 9 -> listSubjects();
                case 10 -> back = true;
            }
        }
    }

    private void recordGrade() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        int subjectId = InputValidator.readInt("ID de la Materia: ", 1, 999999);
        int teacherId = InputValidator.readInt("ID del Profesor que califica: ", 1, 999999);
        int year = InputValidator.readInt("Año Académico (ej: 2024): ", 2020, 2030);
        String period = InputValidator.readOption("Período Académico (I, II, III, IV, Final): ", new String[]{"I", "II", "III", "IV", "Final"});
        double gradeValue = InputValidator.readDouble("Calificación (0.0 a 5.0): ", 0.0, 5.0);
        String obs = InputValidator.readString("Observaciones sobre el desempeño: ", false);

        boolean success = academicService.recordGrade(studentId, subjectId, teacherId, year, period, gradeValue, obs);
        if (success) {
            ConsoleFormatter.printSuccess(String.format("Nota %.1f registrada correctamente para el estudiante #%d.", gradeValue, studentId));
        } else {
            ConsoleFormatter.printError("Error al registrar la calificación.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void consultGradesByPeriod() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        int year = InputValidator.readInt("Año Académico: ", 2020, 2030);
        String period = InputValidator.readOption("Período (I, II, III, IV, Final): ", new String[]{"I", "II", "III", "IV", "Final"});

        List<AcademicRecord> records = academicService.getStudentGradesByPeriod(studentId, year, period);
        double gpa = academicService.calculateStudentPeriodGPA(studentId, year, period);
        academicView.displayGradeRecords(records, gpa);
        InputValidator.pressEnterToContinue();
    }

    private void calculateStudentPeriodGPA() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        int year = InputValidator.readInt("Año Académico: ", 2020, 2030);
        String period = InputValidator.readOption("Período (I, II, III, IV, Final): ", new String[]{"I", "II", "III", "IV", "Final"});

        double gpa = academicService.calculateStudentPeriodGPA(studentId, year, period);
        ConsoleFormatter.printSuccess(String.format("El Promedio General (GPA) del estudiante #%d en el período %s-%d es: %.2f / 5.0",
                studentId, period, year, gpa));
        InputValidator.pressEnterToContinue();
    }

    private void calculateStudentSubjectAverage() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        int subjectId = InputValidator.readInt("ID de la Materia: ", 1, 999999);
        int year = InputValidator.readInt("Año Académico: ", 2020, 2030);

        double avg = academicService.calculateStudentSubjectAverage(studentId, subjectId, year);
        ConsoleFormatter.printSuccess(String.format("El Promedio del estudiante #%d en la materia #%d es: %.2f / 5.0",
                studentId, subjectId, avg));
        InputValidator.pressEnterToContinue();
    }

    private void calculateSubjectCourseAverage() {
        int subjectId = InputValidator.readInt("ID de la Materia: ", 1, 999999);
        int year = InputValidator.readInt("Año Académico: ", 2020, 2030);
        String period = InputValidator.readOption("Período: ", new String[]{"I", "II", "III", "IV", "Final"});

        double avg = academicService.calculateSubjectCourseAverage(subjectId, year, period);
        ConsoleFormatter.printSuccess(String.format("El Promedio General del Curso en la materia #%d (Periodo %s-%d) es: %.2f / 5.0",
                subjectId, period, year, avg));
        InputValidator.pressEnterToContinue();
    }

    private void consultAcademicHistory() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        List<AcademicRecord> history = academicService.getStudentAcademicHistory(studentId);
        academicView.displayAcademicHistory(studentId, history);
        InputValidator.pressEnterToContinue();
    }

    private void recordAttendance() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        int subjectId = InputValidator.readInt("ID de la Materia: ", 1, 999999);
        LocalDate date = InputValidator.readDate("Fecha de asistencia (YYYY-MM-DD)", true);
        String status = InputValidator.readOption("Estado (Presente, Ausente, Tardanza, Excusa Médica): ",
                new String[]{"Presente", "Ausente", "Tardanza", "Excusa Médica"});
        String obs = InputValidator.readString("Observaciones (opcional): ", false);
        int teacherId = InputValidator.readInt("ID del Profesor que toma lista: ", 1, 999999);

        boolean success = academicService.recordAttendance(studentId, subjectId, date, status, obs, teacherId);
        if (success) {
            ConsoleFormatter.printSuccess("Asistencia registrada correctamente.");
        } else {
            ConsoleFormatter.printError("No se pudo registrar la asistencia.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void checkAttendanceAlerts() {
        int studentId = InputValidator.readInt("ID del Estudiante a verificar: ", 1, 999999);
        List<Attendance> absences = academicService.getUnexcusedAbsences(studentId);
        academicView.displayAbsences(studentId, absences);
        InputValidator.pressEnterToContinue();
    }

    private void listSubjects() {
        List<Subject> subjects = academicService.getAllSubjects();
        academicView.displaySubjects(subjects);
        InputValidator.pressEnterToContinue();
    }
}
