package com.petshop.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer order in the pet shop.
 * Demonstrates encapsulation with private fields and getters/setters.
 */
public class Order {

    public enum OrderStatus {
        CONFIRMED, CANCELLED
    }

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Encapsulation: private fields
    private String orderId;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private List<Pet> pets;
    private double totalAmount;
    private String orderDate;
    private OrderStatus status;

    public Order(String orderId, String customerId, String customerName,
                 String customerPhone, List<Pet> pets, double totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.pets = new ArrayList<>(pets);
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now().format(FORMATTER);
        this.status = OrderStatus.CONFIRMED;
    }

    // Constructor for loading from file
    public Order(String orderId, String customerId, String customerName,
                 String customerPhone, List<Pet> pets, double totalAmount,
                 String orderDate, OrderStatus status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.pets = new ArrayList<>(pets);
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
    }

    // ── Getters and Setters (Encapsulation) ──────────────────────────────────

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public List<Pet> getPets() { return pets; }
    public void setPets(List<Pet> pets) { this.pets = pets; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    /**
     * Returns pet IDs as a pipe-separated string for CSV storage.
     */
    public String getPetIdsCsv() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pets.size(); i++) {
            if (i > 0) sb.append("|");
            sb.append(pets.get(i).getPetId());
        }
        return sb.toString();
    }

    /**
     * Converts the order to a CSV line.
     * Format: orderId,customerId,customerName,customerPhone,petIds,totalAmount,orderDate,status
     */
    public String toCsv() {
        return orderId + "," + customerId + "," + customerName + ","
            + customerPhone + "," + getPetIdsCsv() + ","
            + String.format("%.2f", totalAmount) + "," + orderDate + "," + status.name();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID : ").append(orderId).append("\n");
        sb.append("Customer : ").append(customerName).append(" | ").append(customerPhone).append("\n");
        sb.append("Date     : ").append(orderDate).append("\n");
        sb.append("Status   : ").append(status).append("\n");
        sb.append("Pets     :\n");
        for (Pet p : pets) {
            sb.append("  - [").append(p.getPetId()).append("] ")
              .append(p.getName()).append(" (").append(p.getAnimalType()).append(")")
              .append(" Rs.").append(String.format("%.2f", p.getPrice())).append("\n");
        }
        sb.append("Total    : Rs.").append(String.format("%.2f", totalAmount));
        return sb.toString();
    }
}
