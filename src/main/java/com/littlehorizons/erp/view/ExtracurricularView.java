package com.littlehorizons.erp.view;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.SchoolEvent;
import com.littlehorizons.erp.util.ConsoleFormatter;
import com.littlehorizons.erp.util.InputValidator;

import java.util.List;

/**
 * Console view for extracurricular clubs and events.
 */
public class ExtracurricularView {

    public int displayMenu() {
        ConsoleFormatter.printHeader("MÓDULO DE ACTIVIDADES EXTRACURRICULARES");
        System.out.println("  1. Listar catálogo de clubes escolares");
        System.out.println("  2. Inscribir estudiante en un club (con control de cupo)");
        System.out.println("  3. Retirar estudiante de un club");
        System.out.println("  4. Consultar clubes de un estudiante");
        System.out.println("  5. Listar calendario de eventos institucionales");
        System.out.println("  6. Inscribir estudiante en un evento");
        System.out.println("  7. Registrar posición obtenida en evento con competencia");
        System.out.println("  8. Consultar participantes y ganadores de un evento");
        System.out.println("  9. Volver al Menú Principal");
        System.out.println();
        return InputValidator.readInt("Seleccione una opción (1-9): ", 1, 9);
    }

    public void displayClubList(List<Club> clubs, java.util.function.Function<Integer, Integer> activeCountFunc) {
        ConsoleFormatter.printSubHeader("CATÁLOGO DE CLUBES (" + clubs.size() + " REGISTROS)");
        System.out.printf("%-4s %-38s %-12s %-30s%n", "ID", "NOMBRE DEL CLUB", "CUPOS OCUP", "HORARIO DE REUNIÓN");
        ConsoleFormatter.printDivider();
        for (Club c : clubs) {
            int current = activeCountFunc.apply(c.getId());
            System.out.printf("%-4d %-38s %-12s %-30s%n",
                    c.getId(),
                    c.getName(),
                    current + " / " + c.getMaxCapacity(),
                    c.getMeetingSchedule());
        }
    }

    public void displayEventList(List<SchoolEvent> events) {
        ConsoleFormatter.printSubHeader("CALENDARIO DE EVENTOS INSTITUCIONALES (" + events.size() + " EVENTOS)");
        System.out.printf("%-4s %-40s %-12s %-12s %-12s %-20s%n", "ID", "NOMBRE DEL EVENTO", "TIPO", "FECHA", "COMPETENCIA", "LUGAR");
        ConsoleFormatter.printDivider();
        for (SchoolEvent e : events) {
            System.out.printf("%-4d %-40s %-12s %-12s %-12s %-20s%n",
                    e.getId(),
                    e.getName(),
                    e.getEventType(),
                    e.getEventDate().toString(),
                    e.isHasCompetition() ? "SÍ" : "NO",
                    e.getLocation());
        }
    }

    public void displayStudentMemberships(int studentId, List<ClubMembership> memberships) {
        ConsoleFormatter.printSubHeader("MEMBRESÍAS EN CLUBES - ESTUDIANTE #" + studentId);
        if (memberships.isEmpty()) {
            System.out.println("  El estudiante no tiene participaciones en clubes.");
            return;
        }
        for (ClubMembership m : memberships) {
            System.out.printf("  - Club ID #%d | Ingreso: %s | Estado: %s%s%n",
                    m.getClubId(), m.getJoinDate(), m.getStatus(),
                    m.getWithdrawalReason() != null ? " (Motivo retiro: " + m.getWithdrawalReason() + ")" : "");
        }
    }

    public void displayEventParticipants(int eventId, List<EventParticipant> participants) {
        ConsoleFormatter.printSubHeader("PARTICIPANTES DEL EVENTO #" + eventId);
        if (participants.isEmpty()) {
            System.out.println("  No hay participantes registrados para este evento.");
            return;
        }
        System.out.printf("%-6s %-14s %-15s %-15s %-30s%n", "ID", "ESTUDIANTE ID", "ROL", "POSICIÓN", "OBSERVACIONES");
        ConsoleFormatter.printDivider();
        for (EventParticipant p : participants) {
            System.out.printf("%-6d %-14d %-15s %-15s %-30s%n",
                    p.getId(), p.getStudentId(), p.getRole(),
                    p.getPosition() != null ? p.getPosition() : "Participante",
                    p.getObservations() != null ? p.getObservations() : "");
        }
    }
}
