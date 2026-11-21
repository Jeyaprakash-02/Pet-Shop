package com.petshop.ui;

import com.petshop.exception.InvalidInputException;
import com.petshop.exception.PetNotAvailableException;
import com.petshop.model.Customer;
import com.petshop.model.Order;
import com.petshop.model.Pet;
import com.petshop.service.OrderService;
import com.petshop.service.PetService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Displays the order summary and allows the customer to confirm the purchase.
 * Demonstrates ActionListener (OOP - Event Handling).
 * Demonstrates PetNotAvailableException and InvalidInputException handling (OOP - Exception Handling).
 */
public class OrderConfirmationFrame extends JFrame {

    private Customer customer;
    private List<Pet> cart;
    private PetService petService;
    private OrderService orderService;
    private CartFrame parentCart;

    private JTextArea summaryArea;
    private JButton btnConfirm;
    private JButton btnBack;

    public OrderConfirmationFrame(Customer customer, List<Pet> cart,
                                  PetService petService, OrderService orderService,
                                  CartFrame parentCart) {
        this.customer     = customer;
        this.cart         = cart;
        this.petService   = petService;
        this.orderService = orderService;
        this.parentCart   = parentCart;
        initUI();
        buildSummary();
    }

    private void initUI() {
        setTitle("Pet Shop — Order Confirmation");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(540, 480);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                orderService.saveAll();
                System.exit(0);
            }
        });

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        main.setBackground(new Color(245, 248, 255));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(34, 139, 34));
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel title = new JLabel("📋 Order Confirmation");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        main.add(header, BorderLayout.NORTH);

        // Summary area
        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        summaryArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        summaryArea.setBackground(new Color(250, 252, 255));
        main.add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        btnPanel.setBackground(new Color(245, 248, 255));
        btnBack    = makeButton("← Back to Cart",  new Color(100, 100, 100));
        btnConfirm = makeButton("✔ Confirm Order",  new Color(34, 139, 34));
        btnPanel.add(btnBack);
        btnPanel.add(btnConfirm);
        main.add(btnPanel, BorderLayout.SOUTH);

        add(main);

        // ActionListeners (OOP - Event Handling)
        btnBack.addActionListener(e -> goBack());
        btnConfirm.addActionListener(e -> placeOrder());
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(170, 34));
        return btn;
    }

    private void buildSummary() {
        double total = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════\n");
        sb.append("         ORDER SUMMARY\n");
        sb.append("══════════════════════════════════════\n\n");
        sb.append("Customer : ").append(customer.getName()).append("\n");
        sb.append("Phone    : ").append(customer.getPhoneNumber()).append("\n\n");
        sb.append("Selected Pets:\n");
        sb.append("──────────────────────────────────────\n");
        for (Pet p : cart) {
            sb.append(String.format("  %-6s %-12s (%-6s) Rs.%,.2f%n",
                p.getPetId(), p.getName(), p.getAnimalType(), p.getPrice()));
            total += p.getPrice();
        }
        sb.append("──────────────────────────────────────\n");
        sb.append(String.format("TOTAL    : Rs. %,.2f%n", total));
        sb.append("══════════════════════════════════════\n\n");
        sb.append("Click 'Confirm Order' to complete your purchase.");
        summaryArea.setText(sb.toString());
    }

    private void placeOrder() {
        try {
            // purchasePets throws checked exceptions (OOP - Exception Handling)
            Order order = orderService.purchasePets(customer, cart);

            // Show confirmation dialog
            String msg = "✔ Order Placed Successfully!\n\n"
                + "Order ID : " + order.getOrderId() + "\n"
                + "Customer : " + customer.getName() + "\n"
                + "Total    : Rs. " + String.format("%,.2f", order.getTotalAmount()) + "\n\n"
                + "Thank you for shopping at Pet Shop!";
            JOptionPane.showMessageDialog(this, msg, "Order Confirmed",
                JOptionPane.INFORMATION_MESSAGE);

            // Return to pet list with cleared cart
            returnToPetList();

        } catch (PetNotAvailableException ex) {
            // Custom exception displayed (OOP - Exception Handling)
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Pet Not Available", JOptionPane.ERROR_MESSAGE);
            goBack();
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } finally {
            btnConfirm.setEnabled(true);
        }
    }

    private void goBack() {
        parentCart.populateTable();
        parentCart.setVisible(true);
        this.dispose();
    }

    private void returnToPetList() {
        // Walk up to the PetListFrame via parentCart
        parentCart.dispose();
        // Re-open PetListFrame — get reference from parentCart's parent
        // For simplicity open a fresh PetListFrame
        PetListFrame plf = new PetListFrame(customer, petService, orderService);
        plf.clearCart();
        plf.setVisible(true);
        this.dispose();
    }
}
