package com.hei.td5webservice.repository;

import com.hei.td5webservice.datasource.DataSource;
import com.hei.td5webservice.entity.Dish;
import com.hei.td5webservice.entity.Ingredient;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DishRepository {

    private final DataSource dataSource;

    public DishRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Dish> findAll() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT id, name, dish_type, price FROM dish";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                List<Ingredient> ingredients = findIngredientsByDishId(conn, id);
                dishes.add(new Dish(id, name, price, ingredients));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching dishes", e);
        }
        return dishes;
    }

    public Optional<Dish> findById(int id) {
        String sql = "SELECT id, name, dish_type, price FROM dish WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    List<Ingredient> ingredients = findIngredientsByDishId(conn, id);
                    return Optional.of(new Dish(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            ingredients
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching dish by id", e);
        }
        return Optional.empty();
    }

    public void updateIngredients(int dishId, List<Ingredient> ingredients) {
        String deleteSql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        String insertSql = "INSERT INTO dish_ingredient (id_dish, id_ingredient) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                    deletePs.setInt(1, dishId);
                    deletePs.executeUpdate();
                }
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    for (Ingredient ing : ingredients) {
                        insertPs.setInt(1, dishId);
                        insertPs.setInt(2, ing.getId());
                        insertPs.addBatch();
                    }
                    insertPs.executeBatch();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating dish ingredients", e);
        }
    }

    private List<Ingredient> findIngredientsByDishId(Connection conn, int dishId) throws SQLException {
        List<Ingredient> list = new ArrayList<>();
        String sql = "SELECT i.id, i.name, i.category, i.price " +
                "FROM ingredient i " +
                "JOIN dish_ingredient di ON i.id = di.id_ingredient " +
                "WHERE di.id_dish = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Ingredient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("price")
                    ));
                }
            }
        }
        return list;
    }
}