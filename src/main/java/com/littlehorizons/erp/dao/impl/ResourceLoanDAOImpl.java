package com.littlehorizons.erp.dao.impl;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.ResourceLoanDAO;
import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.TeacherLoan;
import com.littlehorizons.erp.util.LoggerUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResourceLoanDAOImpl implements ResourceLoanDAO {

    private final DatabaseConfig config = DatabaseConfig.getInstance();

    @Override
    public boolean createStudentLoan(StudentLoan loan) {
        String sql = "INSERT INTO prestamos_estudiante (id_recurso, id_estudiante, fecha_prestamo, fecha_devolucion_esperada, estado_prestamo, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, loan.getResourceId());
            ps.setInt(2, loan.getStudentId());
            ps.setDate(3, Date.valueOf(loan.getLoanDate()));
            ps.setDate(4, Date.valueOf(loan.getExpectedReturnDate()));
            ps.setString(5, loan.getStatus());
            ps.setString(6, loan.getObservations());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        loan.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error creating student loan for resource ID: " + loan.getResourceId(), e);
        }
        return false;
    }

    @Override
    public boolean createTeacherLoan(TeacherLoan loan) {
        String sql = "INSERT INTO prestamos_profesor (id_recurso, id_profesor, fecha_prestamo, fecha_devolucion_esperada, estado_prestamo, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, loan.getResourceId());
            ps.setInt(2, loan.getTeacherId());
            ps.setDate(3, Date.valueOf(loan.getLoanDate()));
            ps.setDate(4, Date.valueOf(loan.getExpectedReturnDate()));
            ps.setString(5, loan.getStatus());
            ps.setString(6, loan.getObservations());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        loan.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error creating teacher loan for resource ID: " + loan.getResourceId(), e);
        }
        return false;
    }

    @Override
    public boolean returnStudentLoan(int loanId, String observations) {
        String sql = "UPDATE prestamos_estudiante SET estado_prestamo = 'Devuelto', fecha_devolucion_real = ?, observaciones = ? " +
                "WHERE id_prestamo = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setString(2, observations);
            ps.setInt(3, loanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error returning student loan ID: " + loanId, e);
        }
        return false;
    }

    @Override
    public boolean returnTeacherLoan(int loanId, String observations) {
        String sql = "UPDATE prestamos_profesor SET estado_prestamo = 'Devuelto', fecha_devolucion_real = ?, observaciones = ? " +
                "WHERE id_prestamo = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setString(2, observations);
            ps.setInt(3, loanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error returning teacher loan ID: " + loanId, e);
        }
        return false;
    }

    @Override
    public Optional<StudentLoan> findStudentLoanById(int id) {
        String sql = "SELECT * FROM prestamos_estudiante WHERE id_prestamo = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date actualReturn = rs.getDate("fecha_devolucion_real");
                    return Optional.of(new StudentLoan(
                            rs.getInt("id_prestamo"),
                            rs.getInt("id_recurso"),
                            rs.getInt("id_estudiante"),
                            rs.getDate("fecha_prestamo").toLocalDate(),
                            rs.getDate("fecha_devolucion_esperada").toLocalDate(),
                            actualReturn != null ? actualReturn.toLocalDate() : null,
                            rs.getString("estado_prestamo"),
                            rs.getString("observaciones")
                    ));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding student loan by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TeacherLoan> findTeacherLoanById(int id) {
        String sql = "SELECT * FROM prestamos_profesor WHERE id_prestamo = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date actualReturn = rs.getDate("fecha_devolucion_real");
                    return Optional.of(new TeacherLoan(
                            rs.getInt("id_prestamo"),
                            rs.getInt("id_recurso"),
                            rs.getInt("id_profesor"),
                            rs.getDate("fecha_prestamo").toLocalDate(),
                            rs.getDate("fecha_devolucion_esperada").toLocalDate(),
                            actualReturn != null ? actualReturn.toLocalDate() : null,
                            rs.getString("estado_prestamo"),
                            rs.getString("observaciones")
                    ));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding teacher loan by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<StudentLoan> findActiveStudentLoans() {
        List<StudentLoan> list = new ArrayList<>();
        String sql = "SELECT * FROM prestamos_estudiante WHERE estado_prestamo = 'Activo' ORDER BY fecha_devolucion_esperada";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new StudentLoan(
                        rs.getInt("id_prestamo"),
                        rs.getInt("id_recurso"),
                        rs.getInt("id_estudiante"),
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        rs.getDate("fecha_devolucion_esperada").toLocalDate(),
                        null,
                        rs.getString("estado_prestamo"),
                        rs.getString("observaciones")
                ));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding active student loans", e);
        }
        return list;
    }

    @Override
    public List<TeacherLoan> findActiveTeacherLoans() {
        List<TeacherLoan> list = new ArrayList<>();
        String sql = "SELECT * FROM prestamos_profesor WHERE estado_prestamo = 'Activo' ORDER BY fecha_devolucion_esperada";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TeacherLoan(
                        rs.getInt("id_prestamo"),
                        rs.getInt("id_recurso"),
                        rs.getInt("id_profesor"),
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        rs.getDate("fecha_devolucion_esperada").toLocalDate(),
                        null,
                        rs.getString("estado_prestamo"),
                        rs.getString("observaciones")
                ));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding active teacher loans", e);
        }
        return list;
    }

    @Override
    public List<StudentLoan> findOverdueStudentLoans() {
        List<StudentLoan> list = new ArrayList<>();
        String sql = "SELECT * FROM prestamos_estudiante WHERE estado_prestamo = 'Activo' AND fecha_devolucion_esperada < CURDATE()";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new StudentLoan(
                        rs.getInt("id_prestamo"),
                        rs.getInt("id_recurso"),
                        rs.getInt("id_estudiante"),
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        rs.getDate("fecha_devolucion_esperada").toLocalDate(),
                        null,
                        "Vencido",
                        rs.getString("observaciones")
                ));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding overdue student loans", e);
        }
        return list;
    }

    @Override
    public List<TeacherLoan> findOverdueTeacherLoans() {
        List<TeacherLoan> list = new ArrayList<>();
        String sql = "SELECT * FROM prestamos_profesor WHERE estado_prestamo = 'Activo' AND fecha_devolucion_esperada < CURDATE()";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TeacherLoan(
                        rs.getInt("id_prestamo"),
                        rs.getInt("id_recurso"),
                        rs.getInt("id_profesor"),
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        rs.getDate("fecha_devolucion_esperada").toLocalDate(),
                        null,
                        "Vencido",
                        rs.getString("observaciones")
                ));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding overdue teacher loans", e);
        }
        return list;
    }
}
