package com.sweet.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SweetDto {
    private String name;
    private String category;
    private double price;
    private int quantity;
}