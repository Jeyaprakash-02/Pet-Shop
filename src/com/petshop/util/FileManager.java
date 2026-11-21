package com.petshop.util;

import com.petshop.model.*;
import java.io.*;
import java.util.*;

/**
 * Handles file I/O for pets and orders.
 * Demonstrates file handling with try/catch/finally (OOP - File Handling, Exception Handling).
 * Uses CSV format for pets.txt and orders.txt.
 */
public class FileManager {

    private static final String DATA_DIR  = "data";
    private static final String PETS_FILE = DATA_DIR + File.separator + "pets.txt";
    private static final String ORDERS_FILE = DATA_DIR + File.separator + "orders.txt";

    /**
     * Ensures the data directory and files exist; creates them if missing.
     * Demonstrates file creation with try/catch/finally (OOP - Exception Handling).
     */
    public static void ensureDataFiles() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        File petsFile = new File(PETS_FILE);
        File ordersFile = new File(ORDERS_FILE);
        try {
            if (!petsFile.exists())   petsFile.createNewFile();
            if (!ordersFile.exists()) ordersFile.createNewFile();
        } catch (IOException e) {
            System.err.println("FileManager: Could not create data files: " + e.getMessage());
        }
    }

    // ── Pets ──────────────────────────────────────────────────────────────────

    /**
     * Saves the full pet list to data/pets.txt.
     * Uses try/catch/finally for I/O safety (OOP - Exception Handling).
     */
    public static void savePets(List<Pet> pets) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(PETS_FILE));
            for (Pet pet : pets) {
                writer.write(pet.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("FileManager: Error saving pets: " + e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException e) { /* ignore */ }
            }
        }
    }

    /**
     * Loads pets from data/pets.txt.
     * CSV format: petId,name,animalType,breed,age,gender,price,available,soldToCustomerId,orderId
     */
    public static List<Pet> loadPets() {
        List<Pet> pets = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(PETS_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Pet pet = parsePetLine(line);
                if (pet != null) pets.add(pet);
            }
        } catch (IOException e) {
            System.err.println("FileManager: Error loading pets: " + e.getMessage());
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException e) { /* ignore */ }
            }
        }
        return pets;
    }

    /**
     * Parses a CSV line into a Pet object (Dog, Cat, Bird, or generic Rabbit).
     */
    private static Pet parsePetLine(String line) {
        try {
            String[] parts = line.split(",", -1);
            if (parts.length < 10) return null;
            String petId     = parts[0].trim();
            String name      = parts[1].trim();
            String animalType= parts[2].trim();
            String breed     = parts[3].trim();
            int    age       = Integer.parseInt(parts[4].trim());
            String gender    = parts[5].trim();
            double price     = Double.parseDouble(parts[6].trim());
            boolean available= Boolean.parseBoolean(parts[7].trim());
            String soldTo    = parts[8].trim();
            String orderId   = parts[9].trim();

            Pet pet;
            switch (animalType) {
                case "Dog":    pet = new Dog(petId, name, breed, age, gender, price); break;
                case "Cat":    pet = new Cat(petId, name, breed, age, gender, price); break;
                case "Bird":   pet = new Bird(petId, name, breed, age, gender, price); break;
                case "Rabbit": pet = new Rabbit(petId, name, breed, age, gender, price); break;
                default:       pet = new Dog(petId, name, breed, age, gender, price); break;
            }
            pet.setAvailable(available);
            pet.setSoldToCustomerId(soldTo);
            pet.setOrderId(orderId);
            return pet;
        } catch (Exception e) {
            System.err.println("FileManager: Skipping invalid pet line: " + line);
            return null;
        }
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    /**
     * Saves all orders to data/orders.txt.
     * CSV format: orderId,customerId,customerName,customerPhone,petIds,totalAmount,orderDate,status
     */
    public static void saveOrders(Map<String, Order> orders) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(ORDERS_FILE));
            for (Order order : orders.values()) {
                writer.write(order.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("FileManager: Error saving orders: " + e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException e) { /* ignore */ }
            }
        }
    }

    /**
     * Loads orders from data/orders.txt.
     * Requires the full pet list to resolve pet objects by ID.
     */
    public static Map<String, Order> loadOrders(List<Pet> allPets) {
        Map<String, Order> orders = new LinkedHashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(ORDERS_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Order order = parseOrderLine(line, allPets);
                if (order != null) orders.put(order.getOrderId(), order);
            }
        } catch (IOException e) {
            System.err.println("FileManager: Error loading orders: " + e.getMessage());
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException e) { /* ignore */ }
            }
        }
        return orders;
    }

    private static Order parseOrderLine(String line, List<Pet> allPets) {
        try {
            // Format: orderId,customerId,customerName,customerPhone,petIds,totalAmount,orderDate,status
            // petIds can contain '|' separators, so split carefully
            String[] parts = line.split(",", 8);
            if (parts.length < 8) return null;

            String orderId      = parts[0].trim();
            String customerId   = parts[1].trim();
            String customerName = parts[2].trim();
            String customerPhone= parts[3].trim();
            String petIdsStr    = parts[4].trim();
            double totalAmount  = Double.parseDouble(parts[5].trim());
            String orderDate    = parts[6].trim();
            Order.OrderStatus status = Order.OrderStatus.valueOf(parts[7].trim());

            // Resolve pets by ID
            List<Pet> orderPets = new ArrayList<>();
            if (!petIdsStr.isEmpty()) {
                String[] petIds = petIdsStr.split("\\|");
                for (String petId : petIds) {
                    for (Pet p : allPets) {
                        if (p.getPetId().equals(petId.trim())) {
                            orderPets.add(p);
                            break;
                        }
                    }
                }
            }
            return new Order(orderId, customerId, customerName, customerPhone,
                             orderPets, totalAmount, orderDate, status);
        } catch (Exception e) {
            System.err.println("FileManager: Skipping invalid order line: " + line);
            return null;
        }
    }
}
