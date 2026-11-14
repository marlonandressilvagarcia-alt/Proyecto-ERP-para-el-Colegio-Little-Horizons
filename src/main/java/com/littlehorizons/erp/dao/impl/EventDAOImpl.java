package com.littlehorizons.erp.dao.impl;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.EventDAO;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.SchoolEvent;
import com.littlehorizons.erp.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventDAOImpl implements EventDAO {

    private final DatabaseConfig config = DatabaseConfig.getInstance();

    @Override
    public boolean create(SchoolEvent event) {
        String sql = "INSERT INTO eventos (nombre_evento, descripcion, tipo_evento, fecha_evento, lugar, tiene_competencia, id_organizador, esta_activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getEventType());
            ps.setDate(4, Date.valueOf(event.getEventDate()));
            ps.setString(5, event.getLocation());
            ps.setBoolean(6, event.isHasCompetition());
            if (event.getOrganizerTeacherId() != null) {
                ps.setInt(7, event.getOrganizerTeacherId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setBoolean(8, event.isActive());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        event.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error creating event: " + event.getName(), e);
        }
        return false;
    }

    @Override
    public Optional<SchoolEvent> findById(int id) {
        String sql = "SELECT * FROM eventos WHERE id_evento = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding event by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<SchoolEvent> findAll() {
        List<SchoolEvent> list = new ArrayList<>();
        String sql = "SELECT * FROM eventos ORDER BY fecha_evento DESC";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error fetching all events", e);
        }
        return list;
    }

    @Override
    public boolean registerParticipant(EventParticipant participant) {
        String sql = "INSERT INTO participantes_evento (id_evento, id_estudiante, rol_participacion, posicion_obtenida, observaciones) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE rol_participacion = VALUES(rol_participacion), posicion_obtenida = VALUES(posicion_obtenida), observaciones = VALUES(observaciones)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, participant.getEventId());
            ps.setInt(2, participant.getStudentId());
            ps.setString(3, participant.getRole());
            ps.setString(4, participant.getPosition());
            ps.setString(5, participant.getObservations());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        participant.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error registering participant for event: " + participant.getEventId(), e);
        }
        return false;
    }

    @Override
    public boolean updateParticipantPosition(int eventId, int studentId, String position) {
        String sql = "UPDATE participantes_evento SET posicion_obtenida = ? WHERE id_evento = ? AND id_estudiante = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, position);
            ps.setInt(2, eventId);
            ps.setInt(3, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error updating participant position in event " + eventId, e);
        }
        return false;
    }

    @Override
    public List<EventParticipant> findParticipantsByEvent(int eventId) {
        List<EventParticipant> list = new ArrayList<>();
        String sql = "SELECT * FROM participantes_evento WHERE id_evento = ? ORDER BY posicion_obtenida";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new EventParticipant(
                            rs.getInt("id_participacion"),
                            rs.getInt("id_evento"),
                            rs.getInt("id_estudiante"),
                            rs.getString("rol_participacion"),
                            rs.getString("posicion_obtenida"),
                            rs.getString("observaciones")
                    ));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding participants for event ID: " + eventId, e);
        }
        return list;
    }

    public static SchoolEvent mapResultSetToEvent(ResultSet rs) throws SQLException {
        int organizerVal = rs.getInt("id_organizador");
        Integer organizer = rs.wasNull() ? null : organizerVal;
        return new SchoolEvent(
                rs.getInt("id_evento"),
                rs.getString("nombre_evento"),
                rs.getString("descripcion"),
                rs.getString("tipo_evento"),
                rs.getDate("fecha_evento").toLocalDate(),
                rs.getString("lugar"),
                rs.getBoolean("tiene_competencia"),
                organizer,
                rs.getBoolean("esta_activo")
        );
    }
}
