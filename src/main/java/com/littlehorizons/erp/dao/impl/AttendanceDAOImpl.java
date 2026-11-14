package com.littlehorizons.erp.dao.impl;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.AttendanceDAO;
import com.littlehorizons.erp.model.Attendance;
import com.littlehorizons.erp.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAOImpl implements AttendanceDAO {

    private final DatabaseConfig config = DatabaseConfig.getInstance();

    @Override
    public boolean recordAttendance(Attendance attendance) {
        String sql = "INSERT INTO asistencias (id_estudiante, id_materia, fecha_asistencia, estado_asistencia, observaciones, registrado_por) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE estado_asistencia = VALUES(estado_asistencia), observaciones = VALUES(observaciones), registrado_por = VALUES(registrado_por)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attendance.getStudentId());
            ps.setInt(2, attendance.getSubjectId());
            ps.setDate(3, Date.valueOf(attendance.getAttendanceDate()));
            ps.setString(4, attendance.getStatus());
            ps.setString(5, attendance.getObservations());
            if (attendance.getRegisteredBy() != null) {
                ps.setInt(6, attendance.getRegisteredBy());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error recording attendance for student ID: " + attendance.getStudentId(), e);
        }
        return false;
    }

    @Override
    public List<Attendance> findByStudent(int studentId, int subjectId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE id_estudiante = ? AND id_materia = ? ORDER BY fecha_asistencia DESC";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding attendance for student ID: " + studentId, e);
        }
        return list;
    }

    @Override
    public List<Attendance> findUnexcusedAbsences(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE id_estudiante = ? AND estado_asistencia = 'Ausente' ORDER BY fecha_asistencia DESC";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding unexcused absences for student ID: " + studentId, e);
        }
        return list;
    }

    public static Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        int registeredByVal = rs.getInt("registrado_por");
        Integer registeredBy = rs.wasNull() ? null : registeredByVal;
        return new Attendance(
                rs.getInt("id_asistencia"),
                rs.getInt("id_estudiante"),
                rs.getInt("id_materia"),
                rs.getDate("fecha_asistencia").toLocalDate(),
                rs.getString("estado_asistencia"),
                rs.getString("observaciones"),
                registeredBy
        );
    }
}
