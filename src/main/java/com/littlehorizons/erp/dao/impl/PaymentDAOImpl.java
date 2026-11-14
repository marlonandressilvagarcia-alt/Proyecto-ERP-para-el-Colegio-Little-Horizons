package com.littlehorizons.erp.dao.impl;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.PaymentDAO;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;
import com.littlehorizons.erp.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentDAOImpl implements PaymentDAO {

    private final DatabaseConfig config = DatabaseConfig.getInstance();

    @Override
    public boolean create(Payment payment) {
        String sql = "INSERT INTO pagos (id_estudiante, id_acudiente, id_concepto, monto_pagado, fecha_pago, " +
                "metodo_pago, numero_recibo, año_academico, periodo_academico, notas) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getStudentId());
            if (payment.getGuardianId() != null) {
                ps.setInt(2, payment.getGuardianId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, payment.getConceptId());
            ps.setDouble(4, payment.getAmountPaid());
            ps.setDate(5, Date.valueOf(payment.getPaymentDate()));
            ps.setString(6, payment.getPaymentMethod());
            ps.setString(7, payment.getReceiptNumber());
            if (payment.getAcademicYear() != null) {
                ps.setInt(8, payment.getAcademicYear());
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            ps.setString(9, payment.getAcademicPeriod());
            ps.setString(10, payment.getNotes());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        payment.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error creating payment: " + payment.getReceiptNumber(), e);
        }
        return false;
    }

    @Override
    public Optional<Payment> findById(int id) {
        String sql = "SELECT * FROM pagos WHERE id_pago = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding payment by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Payment> findByReceiptNumber(String receiptNumber) {
        String sql = "SELECT * FROM pagos WHERE numero_recibo = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, receiptNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding payment by receipt: " + receiptNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Payment> findAll() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM pagos ORDER BY fecha_pago DESC";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error fetching all payments", e);
        }
        return list;
    }

    @Override
    public List<Payment> findByStudent(int studentId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM pagos WHERE id_estudiante = ? ORDER BY fecha_pago DESC";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding payments for student ID: " + studentId, e);
        }
        return list;
    }

    @Override
    public List<Payment> findByGuardian(int guardianId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM pagos WHERE id_acudiente = ? ORDER BY fecha_pago DESC";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guardianId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding payments for guardian ID: " + guardianId, e);
        }
        return list;
    }

    @Override
    public List<PaymentConcept> findAllConcepts() {
        List<PaymentConcept> list = new ArrayList<>();
        String sql = "SELECT * FROM conceptos_pago WHERE esta_activo = TRUE ORDER BY id_concepto";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new PaymentConcept(
                        rs.getInt("id_concepto"),
                        rs.getString("codigo_concepto"),
                        rs.getString("nombre_concepto"),
                        rs.getString("descripcion"),
                        rs.getDouble("monto_base"),
                        rs.getBoolean("es_recurrente"),
                        rs.getBoolean("esta_activo")
                ));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error fetching payment concepts", e);
        }
        return list;
    }

    @Override
    public Optional<PaymentConcept> findConceptById(int id) {
        String sql = "SELECT * FROM conceptos_pago WHERE id_concepto = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new PaymentConcept(
                            rs.getInt("id_concepto"),
                            rs.getString("codigo_concepto"),
                            rs.getString("nombre_concepto"),
                            rs.getString("descripcion"),
                            rs.getDouble("monto_base"),
                            rs.getBoolean("es_recurrente"),
                            rs.getBoolean("esta_activo")
                    ));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding concept by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Integer> findDefaulterStudentIds() {
        List<Integer> list = new ArrayList<>();
        // Students with no payments in current year or missing monthly recurring payments
        String sql = "SELECT e.id_estudiante FROM estudiantes e " +
                "WHERE e.estado = 'Activo' AND e.id_estudiante NOT IN (" +
                "  SELECT DISTINCT p.id_estudiante FROM pagos p " +
                "  WHERE p.fecha_pago >= DATE_SUB(CURDATE(), INTERVAL 60 DAY)" +
                ")";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getInt("id_estudiante"));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error identifying defaulters", e);
        }
        return list;
    }

    public static Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        int guardianVal = rs.getInt("id_acudiente");
        Integer guardianId = rs.wasNull() ? null : guardianVal;
        int yearVal = rs.getInt("año_academico");
        Integer academicYear = rs.wasNull() ? null : yearVal;

        return new Payment(
                rs.getInt("id_pago"),
                rs.getInt("id_estudiante"),
                guardianId,
                rs.getInt("id_concepto"),
                rs.getDouble("monto_pagado"),
                rs.getDate("fecha_pago").toLocalDate(),
                rs.getString("metodo_pago"),
                rs.getString("numero_recibo"),
                academicYear,
                rs.getString("periodo_academico"),
                rs.getString("notas")
        );
    }
}
