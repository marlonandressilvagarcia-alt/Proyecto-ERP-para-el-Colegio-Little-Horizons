package com.littlehorizons.erp.dao.impl;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.GuardianDAO;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuardianDAOImpl implements GuardianDAO {

    private final DatabaseConfig config = DatabaseConfig.getInstance();

    @Override
    public boolean create(Guardian guardian) {
        String sql = "INSERT INTO acudientes (tipo_documento, numero_documento, primer_nombre, segundo_nombre, " +
                "primer_apellido, segundo_apellido, parentesco, telefono_principal, telefono_secundario, " +
                "correo_electronico, direccion, ocupacion, es_responsable_financiero) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, guardian.getDocType());
            ps.setString(2, guardian.getDocNumber());
            ps.setString(3, guardian.getFirstName());
            ps.setString(4, guardian.getMiddleName());
            ps.setString(5, guardian.getLastName());
            ps.setString(6, guardian.getSecondLastName());
            ps.setString(7, guardian.getRelationship());
            ps.setString(8, guardian.getMainPhone());
            ps.setString(9, guardian.getSecondaryPhone());
            ps.setString(10, guardian.getEmail());
            ps.setString(11, guardian.getAddress());
            ps.setString(12, guardian.getOccupation());
            ps.setBoolean(13, guardian.isFinancialResponsible());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        guardian.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error creating guardian: " + guardian.getFullName(), e);
        }
        return false;
    }

    @Override
    public Optional<Guardian> findById(int id) {
        String sql = "SELECT * FROM acudientes WHERE id_acudiente = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGuardian(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding guardian by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Guardian> findByDocNumber(String docNumber) {
        String sql = "SELECT * FROM acudientes WHERE numero_documento = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, docNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGuardian(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding guardian by doc: " + docNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Guardian> findAll() {
        List<Guardian> list = new ArrayList<>();
        String sql = "SELECT * FROM acudientes ORDER BY primer_apellido, primer_nombre";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToGuardian(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error fetching all guardians", e);
        }
        return list;
    }

    @Override
    public boolean update(Guardian guardian) {
        String sql = "UPDATE acudientes SET tipo_documento = ?, primer_nombre = ?, segundo_nombre = ?, " +
                "primer_apellido = ?, segundo_apellido = ?, parentesco = ?, telefono_principal = ?, " +
                "telefono_secundario = ?, correo_electronico = ?, direccion = ?, ocupacion = ?, " +
                "es_responsable_financiero = ? WHERE id_acudiente = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guardian.getDocType());
            ps.setString(2, guardian.getFirstName());
            ps.setString(3, guardian.getMiddleName());
            ps.setString(4, guardian.getLastName());
            ps.setString(5, guardian.getSecondLastName());
            ps.setString(6, guardian.getRelationship());
            ps.setString(7, guardian.getMainPhone());
            ps.setString(8, guardian.getSecondaryPhone());
            ps.setString(9, guardian.getEmail());
            ps.setString(10, guardian.getAddress());
            ps.setString(11, guardian.getOccupation());
            ps.setBoolean(12, guardian.isFinancialResponsible());
            ps.setInt(13, guardian.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error updating guardian ID: " + guardian.getId(), e);
        }
        return false;
    }

    @Override
    public List<Student> findStudentsByGuardianId(int guardianId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT e.* FROM estudiantes e " +
                "JOIN estudiante_acudiente ea ON e.id_estudiante = ea.id_estudiante " +
                "WHERE ea.id_acudiente = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guardianId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(StudentDAOImpl.mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding students for guardian ID: " + guardianId, e);
        }
        return list;
    }

    public static Guardian mapResultSetToGuardian(ResultSet rs) throws SQLException {
        return new Guardian(
                rs.getInt("id_acudiente"),
                rs.getString("tipo_documento"),
                rs.getString("numero_documento"),
                rs.getString("primer_nombre"),
                rs.getString("segundo_nombre"),
                rs.getString("primer_apellido"),
                rs.getString("segundo_apellido"),
                rs.getString("parentesco"),
                rs.getString("telefono_principal"),
                rs.getString("telefono_secundario"),
                rs.getString("correo_electronico"),
                rs.getString("direccion"),
                rs.getString("ocupacion"),
                rs.getBoolean("es_responsable_financiero")
        );
    }
}
