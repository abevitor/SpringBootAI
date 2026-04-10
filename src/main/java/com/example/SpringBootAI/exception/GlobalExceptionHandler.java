package com.example.SpringBootAI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.SpringBootAI.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body(new ErrorResponse(ex.getMessage(), 404));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(UnauthorizedException ex) { 
        return ResponseEntity
               .status(HttpStatus.UNAUTHORIZED)
               .body(new ErrorResponse(ex.getMessage(), 401));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex){
        return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(new ErrorResponse(ex.getMessage(), 400));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity
               .status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(new ErrorResponse("Erro interno" + ex.getMessage(), 500));
    }
    
}
