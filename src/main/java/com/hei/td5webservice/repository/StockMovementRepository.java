package com.hei.td5webservice.repository;

import com.hei.td5webservice.datasource.DataSource;
import com.hei.td5webservice.entity.CreateStockMovement;
import com.hei.td5webservice.entity.StockMovement;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StockMovementRepository {

    private final DataSource dataSource;

    public StockMovementRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Optional<StockMovement> findStockByIngredientAtAndUnit(int ingredientId, String at, String unit) {
        String sql = "SELECT id, movement_date, unit, quantity, type FROM stock_movement " +
                "WHERE id_ingredient = ? AND unit = ? AND movement_date <= ?::timestamp " +
                "ORDER BY movement_date DESC LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ps.setString(2, unit);
            ps.setString(3, at);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching stock", e);
        }
        return Optional.empty();
    }


    public List<StockMovement> findByIngredientAndDateRange(int ingredientId, Instant from, Instant to) {
        String sql = "SELECT id, movement_date, unit, quantity, type FROM stock_movement " +
                "WHERE id_ingredient = ? AND movement_date >= ? AND movement_date <= ? " +
                "ORDER BY movement_date DESC";
        List<StockMovement> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ps.setTimestamp(2, Timestamp.from(from));
            ps.setTimestamp(3, Timestamp.from(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching stock movements", e);
        }
        return list;
    }


    public List<StockMovement> saveAll(int ingredientId, List<CreateStockMovement> creations) {
        String sql = "INSERT INTO stock_movement (id_ingredient, unit, quantity, type) " +
                "VALUES (?, ?, ?, ?) RETURNING id, movement_date, unit, quantity, type";
        List<StockMovement> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            for (CreateStockMovement c : creations) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, ingredientId);
                    ps.setString(2, c.getUnit());
                    ps.setDouble(3, c.getQuantity());
                    ps.setString(4, c.getType());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            result.add(map(rs));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving stock movements", e);
        }
        return result;
    }

    private StockMovement map(ResultSet rs) throws SQLException {
        return new StockMovement(
                rs.getInt("id"),
                rs.getTimestamp("movement_date").toInstant(),
                rs.getString("unit"),
                rs.getDouble("quantity"),
                rs.getString("type")
        );
    }
}