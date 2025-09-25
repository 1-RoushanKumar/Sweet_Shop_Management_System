package com.sweet.backend.service;

import com.sweet.backend.dto.SweetDto;
import com.sweet.backend.model.Sweet;
import com.sweet.backend.repository.SweetRepository;
import org.springframework.stereotype.Service;

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
}