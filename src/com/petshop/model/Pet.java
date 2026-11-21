package com.petshop.model;

import com.petshop.exception.PetNotAvailableException;

/**
 * Abstract base class for all pets.
 * Demonstrates abstraction (OOP - Abstraction) and
 * implements Purchasable interface.
 * Extended by Dog, Cat, Bird (OOP - Inheritance).
 */
public abstract class Pet implements Purchasable {

    // Encapsulation: private fields
    private String petId;
    private String name;
    private String breed;
    private int age;         // age in months
    private String gender;
    private double price;
    private boolean available;
    private String soldToCustomerId;
    private String orderId;

    public Pet(String petId, String name, String breed, int age, String gender, double price) {
        this.petId = petId;
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.price = price;
        this.available = true;
        this.soldToCustomerId = "";
        this.orderId = "";
    }

    // ── Getters and Setters (Encapsulation) ──────────────────────────────────

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getSoldToCustomerId() { return soldToCustomerId; }
    public void setSoldToCustomerId(String soldToCustomerId) { this.soldToCustomerId = soldToCustomerId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    /**
     * Returns the animal type (e.g. "Dog", "Cat", "Bird").
     * Abstract method overridden in subclasses (OOP - Polymorphism).
     */
    public abstract String getAnimalType();

    /**
     * Returns full pet details as a formatted string.
     * Abstract method overridden in subclasses (OOP - Polymorphism).
     */
    public abstract String getPetDetails();

    // ── Purchasable interface implementation ──────────────────────────────────

    /**
     * Marks this pet as sold. Synchronized to prevent double-purchase
     * (OOP - Threading / synchronized keyword).
     */
    @Override
    public synchronized void purchase(String customerId) throws PetNotAvailableException {
        if (!available) {
            throw new PetNotAvailableException(
                "Pet '" + name + "' (ID: " + petId + ") is not available.", petId);
        }
        this.available = false;
        this.soldToCustomerId = customerId;
    }

    /**
     * Marks this pet as available again when an order is cancelled.
     */
    @Override
    public synchronized void cancelPurchase(String orderId) {
        if (this.orderId.equals(orderId)) {
            this.available = true;
            this.soldToCustomerId = "";
            this.orderId = "";
        }
    }

    /**
     * Converts the pet to a CSV string for file storage.
     * Format: petId,name,animalType,breed,age,gender,price,available,soldToCustomerId,orderId
     */
    public String toCsv() {
        return petId + "," + name + "," + getAnimalType() + "," + breed + ","
            + age + "," + gender + "," + price + "," + available
            + "," + soldToCustomerId + "," + orderId;
    }

    @Override
    public String toString() {
        return "[" + petId + "] " + name + " (" + getAnimalType() + " - " + breed + ")"
            + " | Age: " + age + " months | " + gender
            + " | Rs." + String.format("%.2f", price)
            + " | " + (available ? "Available" : "Sold");
    }
}
