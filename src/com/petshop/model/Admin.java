package com.petshop.model;

/**
 * Represents an administrator in the pet shop.
 * Extends Person (OOP - Inheritance).
 * Overrides getRole() (OOP - Polymorphism).
 */
public class Admin extends Person {

    private static final String ADMIN_PASSWORD = "admin123";

    public Admin() {
        super("Administrator", "N/A");
    }

    /**
     * Validates the provided password against the fixed admin password.
     */
    public static boolean validatePassword(String password) {
        return ADMIN_PASSWORD.equals(password);
    }

    /**
     * Overrides Person.getRole() - Method Overriding (OOP - Polymorphism).
     */
    @Override
    public String getRole() {
        return "Admin";
    }

    @Override
    public String toString() {
        return "Administrator";
    }
}
