package com.littlehorizons.erp.dao.impl;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.InvoiceDAO;
import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceDAOImpl implements InvoiceDAO {

    private final DatabaseConfig config = DatabaseConfig.getInstance();

    @Override
    public boolean create(Invoice invoice) {
        String sql = "INSERT INTO facturas (numero_factura, id_pago, fecha_emision, cufe, nombre_archivo_txt, ruta_archivo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, invoice.getInvoiceNumber());
            ps.setInt(2, invoice.getPaymentId());
            ps.setDate(3, Date.valueOf(invoice.getIssueDate()));
            ps.setString(4, invoice.getCufe());
            ps.setString(5, invoice.getTxtFileName());
            ps.setString(6, invoice.getFilePath());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        invoice.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            // Check if column was named nombre_archivo_pdf in older schema and fallback
            if (e.getMessage() != null && e.getMessage().contains("nombre_archivo_txt")) {
                return fallbackCreatePdfColumn(invoice);
            }
            LoggerUtil.error("Error creating electronic invoice: " + invoice.getInvoiceNumber(), e);
        }
        return false;
    }

    private boolean fallbackCreatePdfColumn(Invoice invoice) {
        String sql = "INSERT INTO facturas (numero_factura, id_pago, fecha_emision, cufe, nombre_archivo_pdf, ruta_archivo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, invoice.getInvoiceNumber());
            ps.setInt(2, invoice.getPaymentId());
            ps.setDate(3, Date.valueOf(invoice.getIssueDate()));
            ps.setString(4, invoice.getCufe());
            ps.setString(5, invoice.getTxtFileName());
            ps.setString(6, invoice.getFilePath());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        invoice.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException ex) {
            LoggerUtil.error("Fallback error creating invoice: " + invoice.getInvoiceNumber(), ex);
        }
        return false;
    }

    @Override
    public Optional<Invoice> findById(int id) {
        String sql = "SELECT * FROM facturas WHERE id_factura = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding invoice by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) {
        String sql = "SELECT * FROM facturas WHERE numero_factura = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding invoice by number: " + invoiceNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> findByPaymentId(int paymentId) {
        String sql = "SELECT * FROM facturas WHERE id_pago = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding invoice by payment ID: " + paymentId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Invoice> findAll() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM facturas ORDER BY fecha_emision DESC, id_factura DESC";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error fetching all invoices", e);
        }
        return list;
    }

    @Override
    public String getNextInvoiceNumber() {
        String sql = "SELECT COUNT(*) FROM facturas";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int nextSeq = rs.getInt(1) + 1;
                return String.format("FAC-LH-%06d", nextSeq);
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error calculating next invoice number", e);
        }
        return "FAC-LH-" + System.currentTimeMillis();
    }

    public static Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        String fileName = null;
        try {
            fileName = rs.getString("nombre_archivo_txt");
        } catch (SQLException ignored) {}
        if (fileName == null) {
            try {
                fileName = rs.getString("nombre_archivo_pdf");
            } catch (SQLException ignored) {}
        }

        return new Invoice(
                rs.getInt("id_factura"),
                rs.getString("numero_factura"),
                rs.getInt("id_pago"),
                rs.getDate("fecha_emision").toLocalDate(),
                rs.getString("cufe"),
                fileName,
                rs.getString("ruta_archivo")
        );
    }
}
