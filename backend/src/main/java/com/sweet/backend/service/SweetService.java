package com.sweet.backend.service;

import com.sweet.backend.dto.SweetDto;
import com.sweet.backend.exception.SweetNotFoundException;
import com.sweet.backend.model.Sweet;
import com.sweet.backend.repository.SweetRepository;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SweetService {

    SweetRepository sweetRepository;

    public SweetService(SweetRepository sweetRepository) {
        this.sweetRepository = sweetRepository;
    }

    public Sweet createSweet(SweetDto sweetDto) {
        Sweet sweet = new Sweet();
        sweet.setName(sweetDto.getName());
        sweet.setCategory(sweetDto.getCategory());
        sweet.setPrice(sweetDto.getPrice());
        sweet.setQuantity(sweetDto.getQuantity());
        return sweetRepository.save(sweet);
    }

    public List<Sweet> getAllSweets() {
        return sweetRepository.findAll();
    }

    public List<Sweet> searchSweets(String name, String category, Double minPrice, Double maxPrice) {
        return sweetRepository.searchSweets(name, category, minPrice, maxPrice);
    }

    public Sweet updateSweet(Long id, SweetDto sweetDto) {
        Sweet existingSweet = sweetRepository.findById(id)
                .orElseThrow(() -> new SweetNotFoundException("Sweet not found with id: " + id));

        existingSweet.setName(sweetDto.getName());
        existingSweet.setCategory(sweetDto.getCategory());
        existingSweet.setPrice(sweetDto.getPrice());
        existingSweet.setQuantity(sweetDto.getQuantity());

        return sweetRepository.save(existingSweet);
    }

    public void deleteSweet(Long id) {
        if (!sweetRepository.existsById(id)) {
            throw new SweetNotFoundException("Sweet not found with id: " + id);
        }
        sweetRepository.deleteById(id);
    }
}