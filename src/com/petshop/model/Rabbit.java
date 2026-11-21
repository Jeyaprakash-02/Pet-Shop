package com.petshop.model;

/**
 * Represents a Rabbit pet.
 * Extends Pet (OOP - Inheritance).
 * Overrides getAnimalType() and getPetDetails() (OOP - Polymorphism).
 */
public class Rabbit extends Pet {

    public Rabbit(String petId, String name, String breed, int age, String gender, double price) {
        super(petId, name, breed, age, gender, price);
    }

    @Override
    public String getAnimalType() {
        return "Rabbit";
    }

    @Override
    public String getPetDetails() {
        return "┌─────────────────────────────────────┐\n"
            + "  PET DETAILS\n"
            + "  ID       : " + getPetId() + "\n"
            + "  Name     : " + getName() + "\n"
            + "  Type     : Rabbit 🐇\n"
            + "  Breed    : " + getBreed() + "\n"
            + "  Age      : " + getAge() + " months\n"
            + "  Gender   : " + getGender() + "\n"
            + "  Price    : Rs." + String.format("%.2f", getPrice()) + "\n"
            + "  Status   : " + (isAvailable() ? "✔ Available" : "✘ Sold") + "\n"
            + "└─────────────────────────────────────┘";
    }
}
