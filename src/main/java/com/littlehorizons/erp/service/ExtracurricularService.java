package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.ClubDAO;
import com.littlehorizons.erp.dao.EventDAO;
import com.littlehorizons.erp.dao.impl.ClubDAOImpl;
import com.littlehorizons.erp.dao.impl.EventDAOImpl;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.SchoolEvent;
import com.littlehorizons.erp.util.LoggerUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service managing extracurricular activities (Clubs & Institutional Events).
 */
public class ExtracurricularService {

    private final ClubDAO clubDAO;
    private final EventDAO eventDAO;

    public ExtracurricularService() {
        this.clubDAO = new ClubDAOImpl();
        this.eventDAO = new EventDAOImpl();
    }

    public ExtracurricularService(ClubDAO clubDAO, EventDAO eventDAO) {
        this.clubDAO = clubDAO;
        this.eventDAO = eventDAO;
    }

    // -------------------------------------------------------------
    // Clubs
    // -------------------------------------------------------------
    public List<Club> getAllClubs() {
        return clubDAO.findAll();
    }

    public Optional<Club> getClubById(int id) {
        return clubDAO.findById(id);
    }

    public boolean registerClub(Club club) {
        return clubDAO.create(club);
    }

    /**
     * Enrolls a student in a club after verifying that capacity has not been exceeded.
     */
    public boolean joinClub(int studentId, int clubId) {
        Optional<Club> clubOpt = clubDAO.findById(clubId);
        if (clubOpt.isEmpty()) {
            LoggerUtil.warn("Cannot join club: Club not found with ID " + clubId);
            return false;
        }

        Club club = clubOpt.get();
        int currentMembers = clubDAO.countActiveMembers(clubId);
        if (currentMembers >= club.getMaxCapacity()) {
            LoggerUtil.warn(String.format("Club '%s' is full (Capacity: %d, Current: %d)", club.getName(), club.getMaxCapacity(), currentMembers));
            return false;
        }

        ClubMembership membership = new ClubMembership(0, studentId, clubId, LocalDate.now(), "Activa", null, null);
        return clubDAO.addMembership(membership);
    }

    public boolean withdrawFromClub(int studentId, int clubId, String reason) {
        return clubDAO.withdrawMembership(studentId, clubId, reason);
    }

    public List<ClubMembership> getStudentClubMemberships(int studentId) {
        return clubDAO.findMembershipsByStudent(studentId);
    }

    public int getClubActiveMemberCount(int clubId) {
        return clubDAO.countActiveMembers(clubId);
    }

    // -------------------------------------------------------------
    // Events
    // -------------------------------------------------------------
    public List<SchoolEvent> getAllEvents() {
        return eventDAO.findAll();
    }

    public Optional<SchoolEvent> getEventById(int id) {
        return eventDAO.findById(id);
    }

    public boolean registerEvent(SchoolEvent event) {
        return eventDAO.create(event);
    }

    public boolean registerStudentInEvent(int eventId, int studentId, String role, String observations) {
        EventParticipant participant = new EventParticipant(0, eventId, studentId, role, "Participante", observations);
        return eventDAO.registerParticipant(participant);
    }

    public boolean recordCompetitionPosition(int eventId, int studentId, String position) {
        return eventDAO.updateParticipantPosition(eventId, studentId, position);
    }

    public List<EventParticipant> getEventParticipants(int eventId) {
        return eventDAO.findParticipantsByEvent(eventId);
    }
}
