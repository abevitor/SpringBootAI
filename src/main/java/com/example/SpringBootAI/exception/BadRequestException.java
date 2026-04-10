package com.example.SpringBootAI.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String massage){
        super(massage);
    }
    
}
