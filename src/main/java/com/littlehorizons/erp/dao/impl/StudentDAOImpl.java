package com.littlehorizons.erp.dao.impl;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.StudentDAO;
import com.littlehorizons.erp.model.Enrollment;
import com.littlehorizons.erp.model.Grade;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAOImpl implements StudentDAO {

    private final DatabaseConfig config = DatabaseConfig.getInstance();

    @Override
    public boolean create(Student student) {
        String sql = "INSERT INTO estudiantes (tipo_documento, numero_documento, primer_nombre, segundo_nombre, " +
                "primer_apellido, segundo_apellido, fecha_nacimiento, genero, direccion, telefono, " +
                "correo_electronico, tipo_sangre, alergias, condiciones_medicas, fecha_ingreso, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getDocType());
            ps.setString(2, student.getDocNumber());
            ps.setString(3, student.getFirstName());
            ps.setString(4, student.getMiddleName());
            ps.setString(5, student.getLastName());
            ps.setString(6, student.getSecondLastName());
            ps.setDate(7, Date.valueOf(student.getBirthDate()));
            ps.setString(8, student.getGender());
            ps.setString(9, student.getAddress());
            ps.setString(10, student.getPhone());
            ps.setString(11, student.getEmail());
            ps.setString(12, student.getBloodType());
            ps.setString(13, student.getAllergies());
            ps.setString(14, student.getMedicalConditions());
            ps.setDate(15, Date.valueOf(student.getEnrollmentDate()));
            ps.setString(16, student.getStatus());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error creating student: " + student.getFullName(), e);
        }
        return false;
    }

    @Override
    public Optional<Student> findById(int id) {
        String sql = "SELECT * FROM estudiantes WHERE id_estudiante = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding student by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByDocNumber(String docNumber) {
        String sql = "SELECT * FROM estudiantes WHERE numero_documento = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, docNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding student by document: " + docNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes ORDER BY primer_apellido, primer_nombre";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error fetching all students", e);
        }
        return list;
    }

    @Override
    public boolean update(Student student) {
        String sql = "UPDATE estudiantes SET tipo_documento = ?, primer_nombre = ?, segundo_nombre = ?, " +
                "primer_apellido = ?, segundo_apellido = ?, fecha_nacimiento = ?, genero = ?, direccion = ?, " +
                "telefono = ?, correo_electronico = ?, tipo_sangre = ?, alergias = ?, " +
                "condiciones_medicas = ?, estado = ? WHERE id_estudiante = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getDocType());
            ps.setString(2, student.getFirstName());
            ps.setString(3, student.getMiddleName());
            ps.setString(4, student.getLastName());
            ps.setString(5, student.getSecondLastName());
            ps.setDate(6, Date.valueOf(student.getBirthDate()));
            ps.setString(7, student.getGender());
            ps.setString(8, student.getAddress());
            ps.setString(9, student.getPhone());
            ps.setString(10, student.getEmail());
            ps.setString(11, student.getBloodType());
            ps.setString(12, student.getAllergies());
            ps.setString(13, student.getMedicalConditions());
            ps.setString(14, student.getStatus());
            ps.setInt(15, student.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error updating student ID: " + student.getId(), e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM estudiantes WHERE id_estudiante = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error deleting student ID: " + id, e);
        }
        return false;
    }

    @Override
    public List<Guardian> findGuardiansByStudentId(int studentId) {
        List<Guardian> list = new ArrayList<>();
        String sql = "SELECT a.* FROM acudientes a " +
                "JOIN estudiante_acudiente ea ON a.id_acudiente = ea.id_acudiente " +
                "WHERE ea.id_estudiante = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(GuardianDAOImpl.mapResultSetToGuardian(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding guardians for student ID: " + studentId, e);
        }
        return list;
    }

    @Override
    public boolean linkStudentGuardian(int studentId, int guardianId, boolean isPrimary, boolean isEmergency) {
        String sql = "INSERT INTO estudiante_acudiente (id_estudiante, id_acudiente, es_principal, es_contacto_emergencia) " +
                "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE es_principal = VALUES(es_principal), es_contacto_emergencia = VALUES(es_contacto_emergencia)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, guardianId);
            ps.setBoolean(3, isPrimary);
            ps.setBoolean(4, isEmergency);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error linking student " + studentId + " with guardian " + guardianId, e);
        }
        return false;
    }

    @Override
    public Optional<Enrollment> getStudentEnrollment(int studentId, int year) {
        String sql = "SELECT * FROM matriculas WHERE id_estudiante = ? AND año_academico = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Enrollment en = new Enrollment(
                            rs.getInt("id_matricula"),
                            rs.getInt("id_estudiante"),
                            rs.getInt("id_grado"),
                            rs.getInt("año_academico"),
                            rs.getString("estado_matricula"),
                            rs.getDate("fecha_matricula").toLocalDate(),
                            rs.getDate("fecha_retiro") != null ? rs.getDate("fecha_retiro").toLocalDate() : null,
                            rs.getString("observaciones")
                    );
                    return Optional.of(en);
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error getting student enrollment for year: " + year, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean enrollStudent(Enrollment enrollment) {
        String sql = "INSERT INTO matriculas (id_estudiante, id_grado, año_academico, estado_matricula, fecha_matricula, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id_grado = VALUES(id_grado), estado_matricula = VALUES(estado_matricula)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getGradeId());
            ps.setInt(3, enrollment.getAcademicYear());
            ps.setString(4, enrollment.getStatus());
            ps.setDate(5, Date.valueOf(enrollment.getEnrollmentDate()));
            ps.setString(6, enrollment.getObservations());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error enrolling student ID: " + enrollment.getStudentId(), e);
        }
        return false;
    }

    @Override
    public List<Grade> findAllGrades() {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grados ORDER BY nivel_numerico ASC";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Grade(rs.getInt("id_grado"), rs.getString("nombre_grado"), rs.getInt("nivel_numerico")));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error fetching all grades", e);
        }
        return list;
    }

    @Override
    public Optional<Grade> findGradeById(int id) {
        String sql = "SELECT * FROM grados WHERE id_grado = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Grade(rs.getInt("id_grado"), rs.getString("nombre_grado"), rs.getInt("nivel_numerico")));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding grade by ID: " + id, e);
        }
        return Optional.empty();
    }

    public static Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id_estudiante"),
                rs.getString("tipo_documento"),
                rs.getString("numero_documento"),
                rs.getString("primer_nombre"),
                rs.getString("segundo_nombre"),
                rs.getString("primer_apellido"),
                rs.getString("segundo_apellido"),
                rs.getDate("fecha_nacimiento").toLocalDate(),
                rs.getString("genero"),
                rs.getString("direccion"),
                rs.getString("telefono"),
                rs.getString("correo_electronico"),
                rs.getString("tipo_sangre"),
                rs.getString("alergias"),
                rs.getString("condiciones_medicas"),
                rs.getDate("fecha_ingreso").toLocalDate(),
                rs.getString("estado")
        );
    }
}
