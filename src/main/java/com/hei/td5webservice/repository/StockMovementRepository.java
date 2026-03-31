package com.hei.td5webservice.repository;

import com.hei.td5webservice.datasource.DataSource;
import com.hei.td5webservice.entity.StockMovement;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
public class StockMovementRepository {

    private final DataSource dataSource;

    public StockMovementRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<StockMovement> findStockByIngredientAtAndUnit(int ingredientId, String at, String unit) {
        String sql = "SELECT unit, value FROM stock_movement " +
                "WHERE id_ingredient = ? AND unit = ? AND created_at <= ?::timestamp " +
                "ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ps.setString(2, unit);
            ps.setString(3, at);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new StockMovement(
                            rs.getString("unit"),
                            rs.getDouble("value")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching stock", e);
        }
        return Optional.empty();
    }
}