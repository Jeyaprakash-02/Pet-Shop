package com.petshop.model;

/**
 * Represents a customer in the pet shop.
 * Extends Person (OOP - Inheritance).
 * Overrides getRole() (OOP - Polymorphism).
 */
public class Customer extends Person {

    private String customerId;

    public Customer(String customerId, String name, String phoneNumber) {
        super(name, phoneNumber);
        this.customerId = customerId;
    }

    // Encapsulation: getter/setter
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Overrides Person.getRole() - Method Overriding (OOP - Polymorphism).
     */
    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public String toString() {
        return "[" + customerId + "] " + getName() + " | Phone: " + getPhoneNumber();
    }
}
