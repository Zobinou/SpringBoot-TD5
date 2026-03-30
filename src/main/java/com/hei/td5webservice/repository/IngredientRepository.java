package com.hei.td5webservice.repository;

import com.hei.td5webservice.datasource.DataSource;
import com.hei.td5webservice.entity.Ingredient;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class IngredientRepository {

    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT id, name, category, price FROM ingredient";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ingredients.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching ingredients", e);
        }
        return ingredients;
    }

    public Optional<Ingredient> findById(int id) {
        String sql = "SELECT id, name, category, price FROM ingredient WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching ingredient by id", e);
        }
        return Optional.empty();
    }

    private Ingredient map(ResultSet rs) throws SQLException {
        return new Ingredient(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("price")
        );
    }
}