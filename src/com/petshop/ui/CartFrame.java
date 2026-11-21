package com.petshop.ui;

import com.petshop.model.Customer;
import com.petshop.model.Pet;
import com.petshop.service.OrderService;
import com.petshop.service.PetService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Displays the customer's cart (selected pets) and allows order confirmation.
 * Demonstrates ActionListener (OOP - Event Handling).
 */
public class CartFrame extends JFrame {

    private Customer customer;
    private List<Pet> cart;
    private PetService petService;
    private OrderService orderService;
    private PetListFrame parentFrame;

    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private JButton btnRemove;
    private JButton btnConfirm;
    private JButton btnBack;

    public CartFrame(Customer customer, List<Pet> cart, PetService petService,
                     OrderService orderService, PetListFrame parentFrame) {
        this.customer     = customer;
        this.cart         = cart;
        this.petService   = petService;
        this.orderService = orderService;
        this.parentFrame  = parentFrame;
        initUI();
        populateTable();
    }

    private void initUI() {
        setTitle("Pet Shop — Shopping Cart | " + customer.getName());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(750, 480);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                orderService.saveAll();
                System.exit(0);
            }
        });

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        main.setBackground(new Color(245, 248, 255));

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(255, 140, 0));
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel title = new JLabel("🛒 Shopping Cart");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        JLabel custLabel = new JLabel("Customer: " + customer.getName());
        custLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        custLabel.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        header.add(custLabel, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────────
        String[] cols = {"Pet ID", "Name", "Type", "Breed", "Age (mo)", "Gender", "Price (Rs.)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(tableModel);
        cartTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cartTable.setRowHeight(24);
        cartTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        cartTable.getTableHeader().setBackground(new Color(255, 140, 0));
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        main.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // ── South ─────────────────────────────────────────────────────────────
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(new Color(245, 248, 255));
        south.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        lblTotal = new JLabel("Total: Rs. 0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTotal.setForeground(new Color(34, 85, 170));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        south.add(lblTotal, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(new Color(245, 248, 255));

        btnBack    = makeButton("← Back",         new Color(100, 100, 100));
        btnRemove  = makeButton("Remove Selected", new Color(190, 50, 50));
        btnConfirm = makeButton("Confirm Order ✔", new Color(34, 139, 34));

        btnPanel.add(btnBack);
        btnPanel.add(btnRemove);
        btnPanel.add(btnConfirm);
        south.add(btnPanel, BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);

        add(main);

        // ── Event Listeners ───────────────────────────────────────────────────
        btnBack.addActionListener(e -> goBack());
        btnRemove.addActionListener(e -> removeSelected());
        btnConfirm.addActionListener(e -> confirmOrder());
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 34));
        return btn;
    }

    public void populateTable() {
        tableModel.setRowCount(0);
        double total = 0;
        for (Pet p : cart) {
            tableModel.addRow(new Object[]{
                p.getPetId(), p.getName(), p.getAnimalType(), p.getBreed(),
                p.getAge(), p.getGender(), String.format("%.2f", p.getPrice())
            });
            total += p.getPrice();
        }
        lblTotal.setText("Total: Rs. " + String.format("%.2f", total));
    }

    private void removeSelected() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a pet to remove.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        cart.remove(row);
        populateTable();
    }

    private void confirmOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Add pets before confirming.",
                "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Open Order Confirmation frame
        OrderConfirmationFrame ocf = new OrderConfirmationFrame(
            customer, cart, petService, orderService, this);
        ocf.setVisible(true);
        this.setVisible(false);
    }

    private void goBack() {
        parentFrame.setVisible(true);
        this.dispose();
    }
}
