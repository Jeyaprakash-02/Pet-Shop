package com.petshop.exception;

/**
 * Custom checked exception thrown when user input is invalid.
 * Demonstrates custom exception handling (OOP - Exception Handling).
 */
public class InvalidInputException extends Exception {

    private final String fieldName;

    public InvalidInputException(String message) {
        super(message);
        this.fieldName = null;
    }

    public InvalidInputException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
