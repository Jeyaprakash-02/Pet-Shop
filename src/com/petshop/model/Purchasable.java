package com.petshop.model;

import com.petshop.exception.PetNotAvailableException;

/**
 * Interface representing something that can be purchased or cancelled.
 * Demonstrates abstraction via interface (OOP - Abstraction).
 */
public interface Purchasable {

    /**
     * Purchase this item.
     * @param customerId the ID of the customer making the purchase
     * @throws PetNotAvailableException if the item is not available
     */
    void purchase(String customerId) throws PetNotAvailableException;

    /**
     * Cancel the purchase of this item.
     * @param orderId the order ID to cancel
     */
    void cancelPurchase(String orderId);

    /**
     * Check whether this item is available for purchase.
     * @return true if available
     */
    boolean isAvailable();
}
