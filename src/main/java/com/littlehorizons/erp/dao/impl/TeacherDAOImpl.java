package com.littlehorizons.erp.dao.impl;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.TeacherDAO;
import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherAssignment;
import com.littlehorizons.erp.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherDAOImpl implements TeacherDAO {

    private final DatabaseConfig config = DatabaseConfig.getInstance();

    @Override
    public boolean create(Teacher teacher) {
        String sql = "INSERT INTO profesores (tipo_documento, numero_documento, primer_nombre, segundo_nombre, " +
                "primer_apellido, segundo_apellido, correo_electronico, telefono, especializacion, fecha_contratacion, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, teacher.getDocType());
            ps.setString(2, teacher.getDocNumber());
            ps.setString(3, teacher.getFirstName());
            ps.setString(4, teacher.getMiddleName());
            ps.setString(5, teacher.getLastName());
            ps.setString(6, teacher.getSecondLastName());
            ps.setString(7, teacher.getEmail());
            ps.setString(8, teacher.getPhone());
            ps.setString(9, teacher.getSpecialization());
            ps.setDate(10, Date.valueOf(teacher.getHireDate()));
            ps.setString(11, teacher.getStatus());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        teacher.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error creating teacher: " + teacher.getFullName(), e);
        }
        return false;
    }

    @Override
    public Optional<Teacher> findById(int id) {
        String sql = "SELECT * FROM profesores WHERE id_profesor = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding teacher by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Teacher> findByDocNumber(String docNumber) {
        String sql = "SELECT * FROM profesores WHERE numero_documento = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, docNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding teacher by doc: " + docNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Teacher> findAll() {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM profesores ORDER BY primer_apellido, primer_nombre";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTeacher(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error fetching all teachers", e);
        }
        return list;
    }

    @Override
    public boolean update(Teacher teacher) {
        String sql = "UPDATE profesores SET tipo_documento = ?, primer_nombre = ?, segundo_nombre = ?, " +
                "primer_apellido = ?, segundo_apellido = ?, correo_electronico = ?, telefono = ?, " +
                "especializacion = ?, estado = ? WHERE id_profesor = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacher.getDocType());
            ps.setString(2, teacher.getFirstName());
            ps.setString(3, teacher.getMiddleName());
            ps.setString(4, teacher.getLastName());
            ps.setString(5, teacher.getSecondLastName());
            ps.setString(6, teacher.getEmail());
            ps.setString(7, teacher.getPhone());
            ps.setString(8, teacher.getSpecialization());
            ps.setString(9, teacher.getStatus());
            ps.setInt(10, teacher.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error updating teacher ID: " + teacher.getId(), e);
        }
        return false;
    }

    @Override
    public boolean assignSubject(TeacherAssignment assignment) {
        String sql = "INSERT INTO asignaciones_profesor (id_profesor, id_materia, año_academico, seccion) " +
                "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE seccion = VALUES(seccion)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignment.getTeacherId());
            ps.setInt(2, assignment.getSubjectId());
            ps.setInt(3, assignment.getAcademicYear());
            ps.setString(4, assignment.getSection());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error assigning subject to teacher ID: " + assignment.getTeacherId(), e);
        }
        return false;
    }

    @Override
    public List<TeacherAssignment> findAssignmentsByTeacher(int teacherId, int year) {
        List<TeacherAssignment> list = new ArrayList<>();
        String sql = "SELECT * FROM asignaciones_profesor WHERE id_profesor = ? AND año_academico = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TeacherAssignment(
                            rs.getInt("id_asignacion"),
                            rs.getInt("id_profesor"),
                            rs.getInt("id_materia"),
                            rs.getInt("año_academico"),
                            rs.getString("seccion")
                    ));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding assignments for teacher ID: " + teacherId, e);
        }
        return list;
    }

    public static Teacher mapResultSetToTeacher(ResultSet rs) throws SQLException {
        return new Teacher(
                rs.getInt("id_profesor"),
                rs.getString("tipo_documento"),
                rs.getString("numero_documento"),
                rs.getString("primer_nombre"),
                rs.getString("segundo_nombre"),
                rs.getString("primer_apellido"),
                rs.getString("segundo_apellido"),
                rs.getString("correo_electronico"),
                rs.getString("telefono"),
                rs.getString("especializacion"),
                rs.getDate("fecha_contratacion").toLocalDate(),
                rs.getString("estado")
        );
    }
}
