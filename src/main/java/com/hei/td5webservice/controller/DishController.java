package com.hei.td5webservice.controller;

import com.hei.td5webservice.entity.Dish;
import com.hei.td5webservice.entity.Ingredient;
import com.hei.td5webservice.repository.DishRepository;
import com.hei.td5webservice.repository.IngredientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public DishController(DishRepository dishRepository,
                          IngredientRepository ingredientRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
    }


    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes() {
        return ResponseEntity.ok(dishRepository.findAll());
    }


    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable int id,
            @RequestBody(required = false) List<Ingredient> ingredients) {

        if (ingredients == null) {
            return ResponseEntity.status(400)
                    .body("Request body is mandatory.");
        }

        Optional<Dish> dish = dishRepository.findById(id);
        if (dish.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("Dish.id=" + id + " is not found");
        }


        List<Ingredient> validIngredients = new ArrayList<>();
        for (Ingredient ing : ingredients) {
            ingredientRepository.findById(ing.getId())
                    .ifPresent(validIngredients::add);
        }

        dishRepository.updateIngredients(id, validIngredients);
        return ResponseEntity.ok(dishRepository.findById(id).get());
    }
}