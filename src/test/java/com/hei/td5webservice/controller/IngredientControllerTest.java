package com.hei.td5webservice.controller;

import com.hei.td5webservice.entity.CreateStockMovement;
import com.hei.td5webservice.entity.Ingredient;
import com.hei.td5webservice.entity.StockMovement;
import com.hei.td5webservice.repository.IngredientRepository;
import com.hei.td5webservice.repository.StockMovementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IngredientController.class)
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredientRepository ingredientRepository;

    @MockBean
    private StockMovementRepository stockMovementRepository;


    @Test
    void getAllIngredients_returns200() throws Exception {
        when(ingredientRepository.findAll())
                .thenReturn(List.of(new Ingredient(1, "Riz", "OTHER", 1500)));

        mockMvc.perform(get("/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Riz"));
    }


    @Test
    void getIngredientById_found_returns200() throws Exception {
        when(ingredientRepository.findById(1))
                .thenReturn(Optional.of(new Ingredient(1, "Riz", "OTHER", 1500)));

        mockMvc.perform(get("/ingredients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    void getIngredientById_notFound_returns404() throws Exception {
        when(ingredientRepository.findById(99))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/ingredients/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ingredient.id=99 is not found"));
    }


    @Test
    void getStockMovements_found_returns200() throws Exception {
        when(ingredientRepository.findById(1))
                .thenReturn(Optional.of(new Ingredient(1, "Riz", "OTHER", 1500)));
        when(stockMovementRepository.findByIngredientAndDateRange(eq(1), any(), any()))
                .thenReturn(List.of(
                        new StockMovement(1, Instant.now(), "KG", 10.0, "ENTREE")
                ));

        mockMvc.perform(get("/ingredients/1/stockMovements")
                        .param("from", "2024-01-01T00:00:00Z")
                        .param("to", "2024-12-31T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].unit").value("KG"));
    }


    @Test
    void getStockMovements_ingredientNotFound_returns404() throws Exception {
        when(ingredientRepository.findById(99))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/ingredients/99/stockMovements")
                        .param("from", "2024-01-01T00:00:00Z")
                        .param("to", "2024-12-31T23:59:59Z"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ingredient.id=99 is not found"));
    }


    @Test
    void addStockMovements_success_returns200() throws Exception {
        when(ingredientRepository.findById(1))
                .thenReturn(Optional.of(new Ingredient(1, "Riz", "OTHER", 1500)));
        when(stockMovementRepository.saveAll(eq(1), any()))
                .thenReturn(List.of(
                        new StockMovement(1, Instant.now(), "KG", 5.0, "SORTIE")
                ));

        mockMvc.perform(post("/ingredients/1/stockMovements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"unit\":\"KG\",\"quantity\":5.0,\"type\":\"SORTIE\"}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("SORTIE"));
    }


    @Test
    void addStockMovements_ingredientNotFound_returns404() throws Exception {
        when(ingredientRepository.findById(99))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/ingredients/99/stockMovements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"unit\":\"KG\",\"quantity\":5.0,\"type\":\"SORTIE\"}]"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ingredient.id=99 is not found"));
    }
}