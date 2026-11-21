package com.petshop.service;

import com.petshop.exception.InvalidInputException;
import com.petshop.exception.PetNotAvailableException;
import com.petshop.model.*;
import com.petshop.util.FileManager;
import javax.swing.SwingWorker;
import java.util.*;

/**
 * Service layer for order management.
 * Demonstrates SwingWorker for background file saving (OOP - Threading).
 * Demonstrates synchronized purchase to prevent double-buying (OOP - Threading).
 */
public class OrderService {

    // Collections: HashMap for orders (OOP - Collections)
    private Map<String, Order> orders;
    private PetService petService;

    public OrderService(PetService petService) {
        this.petService = petService;
        this.orders = new LinkedHashMap<>();
        loadOrders();
    }

    // ── Data Loading ──────────────────────────────────────────────────────────

    public void loadOrders() {
        FileManager.ensureDataFiles();
        this.orders = FileManager.loadOrders(petService.getAllPets());
    }

    // ── Purchase ──────────────────────────────────────────────────────────────

    /**
     * Purchases a list of pets for a customer.
     * Uses synchronized Pet.purchase() to prevent double-purchase.
     * Saves orders via SwingWorker (OOP - Threading).
     *
     * @throws PetNotAvailableException if any pet is not available
     * @throws InvalidInputException    if customer data is invalid
     */
    public synchronized Order purchasePets(Customer customer, List<Pet> cartPets)
            throws PetNotAvailableException, InvalidInputException {

        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new InvalidInputException("Customer name cannot be blank.", "name");
        }
        if (customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty()) {
            throw new InvalidInputException("Phone number cannot be blank.", "phone");
        }

        // Validate all pets are still available before marking any as sold
        for (Pet pet : cartPets) {
            if (!pet.isAvailable()) {
                throw new PetNotAvailableException(
                    "Pet '" + pet.getName() + "' (ID: " + pet.getPetId() + ") is no longer available.",
                    pet.getPetId());
            }
        }

        // Generate order
        String orderId = generateOrderId();
        double total   = petService.calculateTotal(cartPets);

        // Mark each pet as purchased (synchronized in Pet class)
        for (Pet pet : cartPets) {
            pet.purchase(customer.getCustomerId());
            pet.setOrderId(orderId);
        }

        Order order = new Order(orderId, customer.getCustomerId(),
                                customer.getName(), customer.getPhoneNumber(),
                                cartPets, total);
        orders.put(orderId, order);

        // Save in background thread using SwingWorker (OOP - Threading)
        saveInBackground();

        return order;
    }

    // ── Cancel Order ──────────────────────────────────────────────────────────

    /**
     * Cancels an order and restores pet availability.
     * @throws InvalidInputException if the order is not found or already cancelled
     */
    public void cancelOrder(String orderId) throws InvalidInputException {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new InvalidInputException("Order '" + orderId + "' not found.", "orderId");
        }
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new InvalidInputException("Order '" + orderId + "' is already cancelled.", "orderId");
        }

        // Restore pet availability
        for (Pet pet : order.getPets()) {
            pet.setAvailable(true);
            pet.setSoldToCustomerId("");
            pet.setOrderId("");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);

        // Save both pets and orders in background
        saveInBackground();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public Map<String, Order> getAllOrders() {
        return Collections.unmodifiableMap(orders);
    }

    public List<Order> getOrdersForCustomer(String customerId) {
        List<Order> result = new ArrayList<>();
        for (Order o : orders.values()) {
            if (o.getCustomerId().equals(customerId)) result.add(o);
        }
        return result;
    }

    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    /**
     * Generates a unique order ID with timestamp and random suffix.
     */
    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis() % 100000
               + String.format("%03d", (int)(Math.random() * 1000));
    }

    /**
     * Saves orders and pets to file in a background SwingWorker thread.
     * Demonstrates SwingWorker usage (OOP - Threading).
     */
    private void saveInBackground() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Both saves happen in background thread
                FileManager.saveOrders(orders);
                petService.savePets();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // propagate exceptions if any
                } catch (Exception e) {
                    System.err.println("Background save error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Saves all data synchronously (called on window close).
     */
    public void saveAll() {
        FileManager.saveOrders(orders);
        petService.savePets();
    }
}
