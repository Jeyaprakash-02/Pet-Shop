package com.petshop.model;

/**
 * Represents a Dog pet.
 * Extends Pet (OOP - Inheritance).
 * Overrides getAnimalType() and getPetDetails() (OOP - Polymorphism).
 */
public class Dog extends Pet {

    public Dog(String petId, String name, String breed, int age, String gender, double price) {
        super(petId, name, breed, age, gender, price);
    }

    /**
     * Overrides Pet.getAnimalType() (OOP - Polymorphism).
     */
    @Override
    public String getAnimalType() {
        return "Dog";
    }

    /**
     * Overrides Pet.getPetDetails() - Method Overriding (OOP - Polymorphism).
     * Dogs have specific detail presentation.
     */
    @Override
    public String getPetDetails() {
        return "┌─────────────────────────────────────┐\n"
            + "  PET DETAILS\n"
            + "  ID       : " + getPetId() + "\n"
            + "  Name     : " + getName() + "\n"
            + "  Type     : Dog 🐕\n"
            + "  Breed    : " + getBreed() + "\n"
            + "  Age      : " + getAge() + " months\n"
            + "  Gender   : " + getGender() + "\n"
            + "  Price    : Rs." + String.format("%.2f", getPrice()) + "\n"
            + "  Status   : " + (isAvailable() ? "✔ Available" : "✘ Sold") + "\n"
            + "└─────────────────────────────────────┘";
    }
}
