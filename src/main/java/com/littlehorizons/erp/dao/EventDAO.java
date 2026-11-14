package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.SchoolEvent;

import java.util.List;
import java.util.Optional;

public interface EventDAO {
    boolean create(SchoolEvent event);
    Optional<SchoolEvent> findById(int id);
    List<SchoolEvent> findAll();
    boolean registerParticipant(EventParticipant participant);
    boolean updateParticipantPosition(int eventId, int studentId, String position);
    List<EventParticipant> findParticipantsByEvent(int eventId);
}
