package com.hei.td5webservice.controller;

import com.hei.td5webservice.entity.Ingredient;
import com.hei.td5webservice.entity.StockMovement;
import com.hei.td5webservice.repository.IngredientRepository;
import com.hei.td5webservice.repository.StockMovementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientRepository ingredientRepository;
    private final StockMovementRepository stockMovementRepository;

    public IngredientController(IngredientRepository ingredientRepository,
                                StockMovementRepository stockMovementRepository) {
        this.ingredientRepository = ingredientRepository;
        this.stockMovementRepository = stockMovementRepository;
    }


    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(ingredientRepository.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable int id) {
        Optional<Ingredient> ingredient = ingredientRepository.findById(id);
        if (ingredient.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("Ingredient.id=" + id + " is not found");
        }
        return ResponseEntity.ok(ingredient.get());
    }


    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getIngredientStock(
            @PathVariable int id,
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String unit) {

        if (at == null || unit == null) {
            return ResponseEntity.status(400)
                    .body("Either mandatory query parameter 'at' or 'unit' is not provided.");
        }

        Optional<Ingredient> ingredient = ingredientRepository.findById(id);
        if (ingredient.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("Ingredient.id=" + id + " is not found");
        }

        Optional<StockMovement> stock =
                stockMovementRepository.findStockByIngredientAtAndUnit(id, at, unit);

        if (stock.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("Ingredient.id=" + id + " is not found");
        }

        return ResponseEntity.ok(stock.get());
    }
}