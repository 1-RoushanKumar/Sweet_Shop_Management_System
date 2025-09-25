package com.sweet.backend.controller;

import com.sweet.backend.dto.SweetDto;
import com.sweet.backend.model.Sweet;
import com.sweet.backend.service.SweetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
}