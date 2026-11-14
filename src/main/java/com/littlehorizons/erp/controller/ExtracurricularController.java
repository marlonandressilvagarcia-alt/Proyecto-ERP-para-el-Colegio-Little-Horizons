package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.SchoolEvent;
import com.littlehorizons.erp.service.ExtracurricularService;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;
import com.littlehorizons.erp.view.ExtracurricularView;

import java.util.List;

public class ExtracurricularController {

    private final ExtracurricularService service;
    private final ExtracurricularView view;

    public ExtracurricularController(ExtracurricularService service, ExtracurricularView view) {
        this.service = service;
        this.view = view;
    }

    public void run() {
        boolean back = false;
        while (!back) {
            int option = view.displayMenu();
            switch (option) {
                case 1 -> listClubs();
                case 2 -> joinClub();
                case 3 -> withdrawFromClub();
                case 4 -> viewStudentClubs();
                case 5 -> listEvents();
                case 6 -> registerInEvent();
                case 7 -> recordCompetitionPosition();
                case 8 -> viewEventParticipants();
                case 9 -> back = true;
            }
        }
    }

    private void listClubs() {
        List<Club> clubs = service.getAllClubs();
        view.displayClubList(clubs, service::getClubActiveMemberCount);
        InputValidator.pressEnterToContinue();
    }

    private void joinClub() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        int clubId = InputValidator.readInt("ID del Club al que desea ingresar: ", 1, 999999);

        boolean success = service.joinClub(studentId, clubId);
        if (success) {
            ConsoleFormatter.printSuccess("Estudiante inscrito exitosamente en el club.");
        } else {
            ConsoleFormatter.printError("No se pudo inscribir en el club (verifique si se superó el cupo máximo o si el club existe).");
        }
        InputValidator.pressEnterToContinue();
    }

    private void withdrawFromClub() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        int clubId = InputValidator.readInt("ID del Club: ", 1, 999999);
        String reason = InputValidator.readString("Motivo del retiro: ", true);

        boolean success = service.withdrawFromClub(studentId, clubId, reason);
        if (success) {
            ConsoleFormatter.printSuccess("Membresía cancelada exitosamente.");
        } else {
            ConsoleFormatter.printError("No se pudo procesar el retiro del club.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewStudentClubs() {
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        List<ClubMembership> memberships = service.getStudentClubMemberships(studentId);
        view.displayStudentMemberships(studentId, memberships);
        InputValidator.pressEnterToContinue();
    }

    private void listEvents() {
        List<SchoolEvent> events = service.getAllEvents();
        view.displayEventList(events);
        InputValidator.pressEnterToContinue();
    }

    private void registerInEvent() {
        int eventId = InputValidator.readInt("ID del Evento: ", 1, 999999);
        int studentId = InputValidator.readInt("ID del Estudiante: ", 1, 999999);
        String role = InputValidator.readOption("Rol (Participante, Competidor, Organizador, Asistente): ",
                new String[]{"Participante", "Competidor", "Organizador", "Asistente"});
        String obs = InputValidator.readString("Observaciones (nombre del proyecto/equipo): ", false);

        boolean success = service.registerStudentInEvent(eventId, studentId, role, obs);
        if (success) {
            ConsoleFormatter.printSuccess("Estudiante registrado en el evento exitosamente.");
        } else {
            ConsoleFormatter.printError("Error al registrar participante en el evento.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void recordCompetitionPosition() {
        int eventId = InputValidator.readInt("ID del Evento: ", 1, 999999);
        int studentId = InputValidator.readInt("ID del Estudiante competidor: ", 1, 999999);
        String position = InputValidator.readOption("Posición obtenida: ",
                new String[]{"1er Lugar", "2do Lugar", "3er Lugar", "Participante"});

        boolean success = service.recordCompetitionPosition(eventId, studentId, position);
        if (success) {
            ConsoleFormatter.printSuccess("Posición de competencia guardada con éxito.");
        } else {
            ConsoleFormatter.printError("Error al actualizar la posición del competidor.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewEventParticipants() {
        int eventId = InputValidator.readInt("ID del Evento: ", 1, 999999);
        List<EventParticipant> participants = service.getEventParticipants(eventId);
        view.displayEventParticipants(eventId, participants);
        InputValidator.pressEnterToContinue();
    }
}
