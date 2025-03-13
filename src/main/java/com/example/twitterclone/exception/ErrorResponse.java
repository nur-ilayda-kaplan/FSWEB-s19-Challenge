package com.example.twitterclone.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public ErrorResponse(int status, String message, String resourceName) {
        this.status = status;
        this.message = message;
        this.resourceName = resourceName;
    }
} 