package com.sweet.backend.exception;

public class SweetNotFoundException extends RuntimeException {
    public SweetNotFoundException(String message) {
        super(message);
    }
}