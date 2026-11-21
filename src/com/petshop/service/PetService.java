package com.petshop.service;

import com.petshop.exception.InvalidInputException;
import com.petshop.model.*;
import com.petshop.util.FileManager;
import java.util.*;

/**
 * Service layer for pet management operations.
 * Demonstrates method overloading for calculateTotal (OOP - Polymorphism).
 */
public class PetService {

    // Collections: ArrayList for pet records (OOP - Collections)
    private List<Pet> pets;

    // Collections: Set for tracking sold pet IDs (OOP - Collections)
    private Set<String> soldPetIds;

    public PetService() {
        this.pets = new ArrayList<>();
        this.soldPetIds = new HashSet<>();
        loadPets();
    }

    // ── Data Loading ──────────────────────────────────────────────────────────

    /**
     * Loads pets from file; seeds defaults if file is empty.
     */
    public void loadPets() {
        FileManager.ensureDataFiles();
        List<Pet> loaded = FileManager.loadPets();
        if (loaded.isEmpty()) {
            seedDefaultPets();
            FileManager.savePets(pets);
        } else {
            pets = loaded;
        }
        // Rebuild soldPetIds
        soldPetIds.clear();
        for (Pet p : pets) {
            if (!p.isAvailable()) soldPetIds.add(p.getPetId());
        }
    }

    /**
     * Seeds 10 sample pets with varied types.
     */
    private void seedDefaultPets() {
        pets.clear();
        pets.add(new Dog("P001", "Bruno",   "German Shepherd", 12, "Male",   15000.00));
        pets.add(new Dog("P002", "Bella",   "Golden Retriever",  8, "Female", 18000.00));
        pets.add(new Dog("P003", "Max",     "Labrador",         18, "Male",   12000.00));
        pets.add(new Cat("P004", "Whiskers","Persian",           6, "Female", 10000.00));
        pets.add(new Cat("P005", "Luna",    "Siamese",          10, "Female",  8000.00));
        pets.add(new Cat("P006", "Shadow",  "Maine Coon",       14, "Male",   13000.00));
        pets.add(new Bird("P007","Tweety",  "Budgerigar",        3, "Male",    2500.00));
        pets.add(new Bird("P008","Polly",   "African Grey",      5, "Female", 20000.00));
        pets.add(new Rabbit("P009","Snowball","Holland Lop",     4, "Female",  5000.00));
        pets.add(new Rabbit("P010","Thumper","Angora",           6, "Male",    6000.00));
    }

    // ── CRUD Operations ───────────────────────────────────────────────────────

    public List<Pet> getAllPets() {
        return Collections.unmodifiableList(pets);
    }

    public List<Pet> getAvailablePets() {
        List<Pet> available = new ArrayList<>();
        for (Pet p : pets) {
            if (p.isAvailable()) available.add(p);
        }
        return available;
    }

    public Pet findById(String petId) {
        for (Pet p : pets) {
            if (p.getPetId().equalsIgnoreCase(petId)) return p;
        }
        return null;
    }

    /**
     * Adds a new pet after validating input.
     * @throws InvalidInputException if required fields are blank or price invalid
     */
    public void addPet(Pet pet) throws InvalidInputException {
        validatePet(pet);
        // Check duplicate ID
        if (findById(pet.getPetId()) != null) {
            throw new InvalidInputException("Pet ID '" + pet.getPetId() + "' already exists.", "petId");
        }
        pets.add(pet);
        FileManager.savePets(pets);
    }

    /**
     * Updates an existing pet's details.
     * @throws InvalidInputException if pet not found or input invalid
     */
    public void updatePet(Pet updated) throws InvalidInputException {
        validatePet(updated);
        for (int i = 0; i < pets.size(); i++) {
            if (pets.get(i).getPetId().equals(updated.getPetId())) {
                pets.set(i, updated);
                FileManager.savePets(pets);
                return;
            }
        }
        throw new InvalidInputException("Pet ID '" + updated.getPetId() + "' not found.", "petId");
    }

    /**
     * Removes a pet by ID.
     * @throws InvalidInputException if pet not found
     */
    public void removePet(String petId) throws InvalidInputException {
        Iterator<Pet> it = pets.iterator();
        while (it.hasNext()) {
            Pet p = it.next();
            if (p.getPetId().equals(petId)) {
                it.remove();
                FileManager.savePets(pets);
                return;
            }
        }
        throw new InvalidInputException("Pet ID '" + petId + "' not found.", "petId");
    }

    private void validatePet(Pet pet) throws InvalidInputException {
        if (pet.getPetId() == null || pet.getPetId().trim().isEmpty()) {
            throw new InvalidInputException("Pet ID cannot be blank.", "petId");
        }
        if (pet.getName() == null || pet.getName().trim().isEmpty()) {
            throw new InvalidInputException("Pet name cannot be blank.", "name");
        }
        if (pet.getPrice() <= 0) {
            throw new InvalidInputException("Price must be greater than 0.", "price");
        }
    }

    // ── Total Calculation (Method Overloading) ────────────────────────────────

    /**
     * Calculates total price from a list of pets.
     * Method Overloading version 1 (OOP - Polymorphism).
     */
    public double calculateTotal(List<Pet> petList) {
        double total = 0;
        for (Pet p : petList) {
            total += p.getPrice();
        }
        return total;
    }

    /**
     * Calculates total price from a list of pets with a discount percentage.
     * Method Overloading version 2 (OOP - Polymorphism).
     */
    public double calculateTotal(List<Pet> petList, double discountPercent) {
        double subtotal = calculateTotal(petList);
        return subtotal - (subtotal * discountPercent / 100.0);
    }

    /**
     * Saves the current pet list to file.
     */
    public void savePets() {
        FileManager.savePets(pets);
    }
}
