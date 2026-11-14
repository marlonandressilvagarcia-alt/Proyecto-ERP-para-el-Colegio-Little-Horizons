package com.littlehorizons.erp.dao.impl;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.ResourceDAO;
import com.littlehorizons.erp.model.ResourceCategory;
import com.littlehorizons.erp.model.SchoolResource;
import com.littlehorizons.erp.util.LoggerUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResourceDAOImpl implements ResourceDAO {

    private final DatabaseConfig config = DatabaseConfig.getInstance();

    @Override
    public boolean create(SchoolResource resource) {
        String sql = "INSERT INTO recursos (codigo_recurso, nombre_recurso, id_categoria, descripcion, " +
                "fecha_adquisicion, ubicacion_fisica, estado_recurso) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, resource.getCode());
            ps.setString(2, resource.getName());
            ps.setInt(3, resource.getCategoryId());
            ps.setString(4, resource.getDescription());
            if (resource.getAcquisitionDate() != null) {
                ps.setDate(5, Date.valueOf(resource.getAcquisitionDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, resource.getPhysicalLocation());
            ps.setString(7, resource.getStatus());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        resource.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error creating resource: " + resource.getName(), e);
        }
        return false;
    }

    @Override
    public Optional<SchoolResource> findById(int id) {
        String sql = "SELECT * FROM recursos WHERE id_recurso = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToResource(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding resource by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<SchoolResource> findByCode(String code) {
        String sql = "SELECT * FROM recursos WHERE codigo_recurso = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToResource(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding resource by code: " + code, e);
        }
        return Optional.empty();
    }

    @Override
    public List<SchoolResource> findAll() {
        List<SchoolResource> list = new ArrayList<>();
        String sql = "SELECT * FROM recursos ORDER BY id_categoria, nombre_recurso";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToResource(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error fetching all resources", e);
        }
        return list;
    }

    @Override
    public List<SchoolResource> findByCategory(int categoryId) {
        List<SchoolResource> list = new ArrayList<>();
        String sql = "SELECT * FROM recursos WHERE id_categoria = ? ORDER BY nombre_recurso";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToResource(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding resources by category: " + categoryId, e);
        }
        return list;
    }

    @Override
    public boolean updateStatus(int resourceId, String newStatus) {
        String sql = "UPDATE recursos SET estado_recurso = ? WHERE id_recurso = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, resourceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error updating resource status: " + resourceId, e);
        }
        return false;
    }

    @Override
    public List<ResourceCategory> findAllCategories() {
        List<ResourceCategory> list = new ArrayList<>();
        String sql = "SELECT * FROM categorias_recurso ORDER BY id_categoria";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ResourceCategory(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre_categoria"),
                        rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error fetching resource categories", e);
        }
        return list;
    }

    public static SchoolResource mapResultSetToResource(ResultSet rs) throws SQLException {
        Date acqDate = rs.getDate("fecha_adquisicion");
        return new SchoolResource(
                rs.getInt("id_recurso"),
                rs.getString("codigo_recurso"),
                rs.getString("nombre_recurso"),
                rs.getInt("id_categoria"),
                rs.getString("descripcion"),
                acqDate != null ? acqDate.toLocalDate() : null,
                rs.getString("ubicacion_fisica"),
                rs.getString("estado_recurso")
        );
    }
}
