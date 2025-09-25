package com.sweet.backend.controller;

import com.sweet.backend.dto.SweetDto;
import com.sweet.backend.model.Sweet;
import com.sweet.backend.service.SweetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/sweets")
public class SweetController {

    private final SweetService sweetService;

    public SweetController(SweetService sweetService) {
        this.sweetService = sweetService;
    }

    @PostMapping
    public ResponseEntity<Sweet> addSweet(@Valid @RequestBody SweetDto sweetDto) {
        Sweet createdSweet = sweetService.createSweet(sweetDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSweet);
    }

    @GetMapping
    public ResponseEntity<List<Sweet>> getAllSweets() {
        List<Sweet> sweets = sweetService.getAllSweets();
        return ResponseEntity.ok(sweets);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Sweet>> searchSweets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<Sweet> sweets = sweetService.searchSweets(name, category, minPrice, maxPrice);
        return ResponseEntity.ok(sweets);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sweet> updateSweet(@PathVariable Long id, @RequestBody SweetDto sweetDto) {
        Sweet updatedSweet = sweetService.updateSweet(id, sweetDto);
        return ResponseEntity.ok(updatedSweet);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSweet(@PathVariable Long id) {
        sweetService.deleteSweet(id);
        return ResponseEntity.noContent().build();
    }
}