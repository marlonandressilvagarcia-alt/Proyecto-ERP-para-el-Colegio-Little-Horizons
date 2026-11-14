package com.littlehorizons.erp.dao.impl;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.AcademicRecordDAO;
import com.littlehorizons.erp.model.AcademicRecord;
import com.littlehorizons.erp.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AcademicRecordDAOImpl implements AcademicRecordDAO {

    private final DatabaseConfig config = DatabaseConfig.getInstance();

    @Override
    public boolean createOrUpdate(AcademicRecord record) {
        String sql = "INSERT INTO registros_academicos (id_estudiante, id_materia, id_profesor, año_academico, " +
                "periodo_academico, valor_nota, observaciones, fecha_registro) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE id_profesor = VALUES(id_profesor), valor_nota = VALUES(valor_nota), " +
                "observaciones = VALUES(observaciones), fecha_registro = VALUES(fecha_registro)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, record.getStudentId());
            ps.setInt(2, record.getSubjectId());
            ps.setInt(3, record.getTeacherId());
            ps.setInt(4, record.getAcademicYear());
            ps.setString(5, record.getAcademicPeriod());
            ps.setDouble(6, record.getGradeValue());
            ps.setString(7, record.getObservations());
            ps.setDate(8, Date.valueOf(record.getRecordDate()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        record.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error creating/updating academic record for student ID: " + record.getStudentId(), e);
        }
        return false;
    }

    @Override
    public Optional<AcademicRecord> findById(int id) {
        String sql = "SELECT * FROM registros_academicos WHERE id_registro = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRecord(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding academic record by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<AcademicRecord> findByStudentAndPeriod(int studentId, int year, String period) {
        List<AcademicRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM registros_academicos WHERE id_estudiante = ? AND año_academico = ? AND periodo_academico = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, year);
            ps.setString(3, period);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRecord(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding records for student " + studentId + " in period " + period, e);
        }
        return list;
    }

    @Override
    public List<AcademicRecord> findByStudentAndSubject(int studentId, int subjectId, int year) {
        List<AcademicRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM registros_academicos WHERE id_estudiante = ? AND id_materia = ? AND año_academico = ? ORDER BY periodo_academico";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRecord(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding records for student " + studentId + " and subject " + subjectId, e);
        }
        return list;
    }

    @Override
    public List<AcademicRecord> findBySubjectAndPeriod(int subjectId, int year, String period) {
        List<AcademicRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM registros_academicos WHERE id_materia = ? AND año_academico = ? AND periodo_academico = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ps.setInt(2, year);
            ps.setString(3, period);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRecord(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding records for subject " + subjectId + " in period " + period, e);
        }
        return list;
    }

    @Override
    public List<AcademicRecord> findByStudentHistory(int studentId) {
        List<AcademicRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM registros_academicos WHERE id_estudiante = ? ORDER BY año_academico DESC, periodo_academico, id_materia";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRecord(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding academic history for student " + studentId, e);
        }
        return list;
    }

    public static AcademicRecord mapResultSetToRecord(ResultSet rs) throws SQLException {
        return new AcademicRecord(
                rs.getInt("id_registro"),
                rs.getInt("id_estudiante"),
                rs.getInt("id_materia"),
                rs.getInt("id_profesor"),
                rs.getInt("año_academico"),
                rs.getString("periodo_academico"),
                rs.getDouble("valor_nota"),
                rs.getString("observaciones"),
                rs.getDate("fecha_registro").toLocalDate()
        );
    }
}
