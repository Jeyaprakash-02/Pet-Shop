package com.petshop.model;

/**
 * Abstract base class for all persons in the system.
 * Demonstrates abstraction (OOP - Abstraction) and
 * is extended by Customer and Admin (OOP - Inheritance).
 */
public abstract class Person {

    // Encapsulation: private fields with getters/setters
    private String name;
    private String phoneNumber;

    public Person(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters (Encapsulation)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Abstract method overridden by subclasses to return their role.
     * Demonstrates polymorphism via method overriding (OOP - Polymorphism).
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + ": " + name + " (" + phoneNumber + ")";
    }
}
