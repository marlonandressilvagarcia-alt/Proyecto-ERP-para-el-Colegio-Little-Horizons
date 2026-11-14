package com.littlehorizons.erp.dao.impl;

import com.littlehorizons.erp.config.DatabaseConfig;
import com.littlehorizons.erp.dao.ClubDAO;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.util.LoggerUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClubDAOImpl implements ClubDAO {

    private final DatabaseConfig config = DatabaseConfig.getInstance();

    @Override
    public boolean create(Club club) {
        String sql = "INSERT INTO clubes (nombre_club, descripcion, id_profesor_supervisor, capacidad_maxima, horario_reunion, esta_activo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, club.getName());
            ps.setString(2, club.getDescription());
            if (club.getSupervisorTeacherId() != null) {
                ps.setInt(3, club.getSupervisorTeacherId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, club.getMaxCapacity());
            ps.setString(5, club.getMeetingSchedule());
            ps.setBoolean(6, club.isActive());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        club.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error creating club: " + club.getName(), e);
        }
        return false;
    }

    @Override
    public Optional<Club> findById(int id) {
        String sql = "SELECT * FROM clubes WHERE id_club = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClub(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding club by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Club> findByName(String name) {
        String sql = "SELECT * FROM clubes WHERE nombre_club = ?";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClub(rs));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding club by name: " + name, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Club> findAll() {
        List<Club> list = new ArrayList<>();
        String sql = "SELECT * FROM clubes ORDER BY nombre_club";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToClub(rs));
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error fetching all clubs", e);
        }
        return list;
    }

    @Override
    public int countActiveMembers(int clubId) {
        String sql = "SELECT COUNT(*) FROM membresias_club WHERE id_club = ? AND estado_membresia = 'Activa'";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error counting active members in club: " + clubId, e);
        }
        return 0;
    }

    @Override
    public boolean addMembership(ClubMembership membership) {
        String sql = "INSERT INTO membresias_club (id_estudiante, id_club, fecha_ingreso, estado_membresia) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, membership.getStudentId());
            ps.setInt(2, membership.getClubId());
            ps.setDate(3, Date.valueOf(membership.getJoinDate()));
            ps.setString(4, membership.getStatus());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        membership.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error adding club membership for student: " + membership.getStudentId(), e);
        }
        return false;
    }

    @Override
    public boolean withdrawMembership(int studentId, int clubId, String reason) {
        String sql = "UPDATE membresias_club SET estado_membresia = 'Retirada', fecha_retiro = ?, motivo_retiro = ? " +
                "WHERE id_estudiante = ? AND id_club = ? AND estado_membresia = 'Activa'";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setString(2, reason);
            ps.setInt(3, studentId);
            ps.setInt(4, clubId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LoggerUtil.error("Error withdrawing student " + studentId + " from club " + clubId, e);
        }
        return false;
    }

    @Override
    public List<ClubMembership> findMembershipsByStudent(int studentId) {
        List<ClubMembership> list = new ArrayList<>();
        String sql = "SELECT * FROM membresias_club WHERE id_estudiante = ? ORDER BY fecha_ingreso DESC";
        try (Connection conn = config.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date withdrawal = rs.getDate("fecha_retiro");
                    list.add(new ClubMembership(
                            rs.getInt("id_membresia"),
                            rs.getInt("id_estudiante"),
                            rs.getInt("id_club"),
                            rs.getDate("fecha_ingreso").toLocalDate(),
                            rs.getString("estado_membresia"),
                            withdrawal != null ? withdrawal.toLocalDate() : null,
                            rs.getString("motivo_retiro")
                    ));
                }
            }
        } catch (SQLException e) {
            LoggerUtil.error("Error finding memberships for student: " + studentId, e);
        }
        return list;
    }

    public static Club mapResultSetToClub(ResultSet rs) throws SQLException {
        int supervisorVal = rs.getInt("id_profesor_supervisor");
        Integer supervisor = rs.wasNull() ? null : supervisorVal;
        return new Club(
                rs.getInt("id_club"),
                rs.getString("nombre_club"),
                rs.getString("descripcion"),
                supervisor,
                rs.getInt("capacidad_maxima"),
                rs.getString("horario_reunion"),
                rs.getBoolean("esta_activo")
        );
    }
}
