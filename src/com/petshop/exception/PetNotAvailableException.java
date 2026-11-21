package com.petshop.exception;

/**
 * Custom checked exception thrown when a pet is not available for purchase.
 * Demonstrates custom exception handling (OOP - Exception Handling).
 */
public class PetNotAvailableException extends Exception {

    private final String petId;

    public PetNotAvailableException(String message) {
        super(message);
        this.petId = null;
    }

    public PetNotAvailableException(String message, String petId) {
        super(message);
        this.petId = petId;
    }

    public String getPetId() {
        return petId;
    }
}
