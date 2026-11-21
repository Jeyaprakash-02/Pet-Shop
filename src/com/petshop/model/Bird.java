package com.petshop.model;

/**
 * Represents a Bird pet.
 * Extends Pet (OOP - Inheritance).
 * Overrides getAnimalType() and getPetDetails() (OOP - Polymorphism).
 */
public class Bird extends Pet {

    public Bird(String petId, String name, String breed, int age, String gender, double price) {
        super(petId, name, breed, age, gender, price);
    }

    /**
     * Overrides Pet.getAnimalType() (OOP - Polymorphism).
     */
    @Override
    public String getAnimalType() {
        return "Bird";
    }

    /**
     * Overrides Pet.getPetDetails() - Method Overriding (OOP - Polymorphism).
     * Birds have specific detail presentation.
     */
    @Override
    public String getPetDetails() {
        return "┌─────────────────────────────────────┐\n"
            + "  PET DETAILS\n"
            + "  ID       : " + getPetId() + "\n"
            + "  Name     : " + getName() + "\n"
            + "  Type     : Bird 🦜\n"
            + "  Breed    : " + getBreed() + "\n"
            + "  Age      : " + getAge() + " months\n"
            + "  Gender   : " + getGender() + "\n"
            + "  Price    : Rs." + String.format("%.2f", getPrice()) + "\n"
            + "  Status   : " + (isAvailable() ? "✔ Available" : "✘ Sold") + "\n"
            + "└─────────────────────────────────────┘";
    }
}
