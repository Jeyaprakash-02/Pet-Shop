package com.petshop.ui;

import com.petshop.model.Customer;
import com.petshop.model.Pet;
import com.petshop.service.OrderService;
import com.petshop.service.PetService;
import com.petshop.exception.InvalidInputException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Displays all available pets in a JTable.
 * Demonstrates ListSelectionListener (OOP - Event Handling).
 * Demonstrates WindowListener to save on close (OOP - Event Handling).
 */
public class PetListFrame extends JFrame {

    private Customer customer;
    private PetService petService;
    private OrderService orderService;

    private JTable petTable;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;
    private JButton btnAddToCart;
    private JButton btnViewCart;
    private JButton btnMyOrders;
    private JButton btnRefresh;
    private JLabel lblStatus;

    // Cart (List of Pet) — OOP Collections
    private List<Pet> cart;

    public PetListFrame(Customer customer, PetService petService, OrderService orderService) {
        this.customer     = customer;
        this.petService   = petService;
        this.orderService = orderService;
        this.cart         = new java.util.ArrayList<>();
        initUI();
        populateTable();
    }

    private void initUI() {
        setTitle("Pet Shop — Pet List | Welcome, " + customer.getName());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 580);
        setLocationRelativeTo(null);

        // WindowListener to save data on close (OOP - Event Handling)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                orderService.saveAll();
                System.exit(0);
            }
        });

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        main.setBackground(new Color(245, 248, 255));

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(34, 85, 170));
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel title = new JLabel("🐾 Available Pets");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        JLabel customerInfo = new JLabel("Customer: " + customer.getName() + " | " + customer.getPhoneNumber());
        customerInfo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        customerInfo.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        header.add(customerInfo, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────────
        String[] columns = {"ID", "Name", "Type", "Breed", "Age (mo)", "Gender", "Price (Rs.)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        petTable = new JTable(tableModel);
        petTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        petTable.setRowHeight(24);
        petTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        petTable.getTableHeader().setBackground(new Color(34, 85, 170));
        petTable.getTableHeader().setForeground(Color.WHITE);
        petTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        petTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // ListSelectionListener (OOP - Event Handling)
        petTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) onRowSelected();
            }
        });

        JScrollPane tableScroll = new JScrollPane(petTable);
        tableScroll.setPreferredSize(new Dimension(580, 300));

        // ── Details Area ──────────────────────────────────────────────────────
        detailsArea = new JTextArea(10, 28);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setBackground(new Color(250, 252, 255));
        detailsArea.setBorder(BorderFactory.createTitledBorder("Pet Details"));
        JScrollPane detailScroll = new JScrollPane(detailsArea);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailScroll);
        split.setDividerLocation(580);
        split.setResizeWeight(0.7);
        main.add(split, BorderLayout.CENTER);

        // ── Buttons ───────────────────────────────────────────────────────────
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(new Color(245, 248, 255));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        btnPanel.setBackground(new Color(245, 248, 255));

        btnRefresh   = makeButton("Refresh",     new Color(70, 130, 180));
        btnAddToCart = makeButton("Add to Cart", new Color(34, 139, 34));
        btnViewCart  = makeButton("View Cart (0)", new Color(255, 140, 0));
        btnMyOrders  = makeButton("My Orders",   new Color(100, 60, 160));
        JButton btnLogout = makeButton("Logout", new Color(190, 50, 50));

        btnPanel.add(btnRefresh);
        btnPanel.add(btnAddToCart);
        btnPanel.add(btnViewCart);
        btnPanel.add(btnMyOrders);
        btnPanel.add(btnLogout);

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(34, 139, 34));
        south.add(btnPanel, BorderLayout.CENTER);
        south.add(lblStatus, BorderLayout.SOUTH);
        main.add(south, BorderLayout.SOUTH);

        add(main);

        // ── Action Listeners (OOP - Event Handling) ───────────────────────────
        btnRefresh.addActionListener(e -> { petService.loadPets(); populateTable(); cart.clear(); updateCartButton(); });
        btnAddToCart.addActionListener(e -> addSelectedToCart());
        btnViewCart.addActionListener(e -> openCart());
        btnMyOrders.addActionListener(e -> openMyOrders());
        btnLogout.addActionListener(e -> logout());
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 34));
        return btn;
    }

    public void populateTable() {
        tableModel.setRowCount(0);
        List<Pet> all = petService.getAllPets();
        for (Pet p : all) {
            tableModel.addRow(new Object[]{
                p.getPetId(), p.getName(), p.getAnimalType(), p.getBreed(),
                p.getAge(), p.getGender(),
                String.format("%.2f", p.getPrice()),
                p.isAvailable() ? "Available" : "Sold"
            });
        }
        setTitle("Pet Shop — Pet List | Welcome, " + customer.getName()
                 + " | Total: " + all.size() + " pets");
    }

    private void onRowSelected() {
        int row = petTable.getSelectedRow();
        if (row < 0) return;
        String petId = (String) tableModel.getValueAt(row, 0);
        Pet pet = petService.findById(petId);
        if (pet != null) {
            detailsArea.setText(pet.getPetDetails());
        }
    }

    private void addSelectedToCart() {
        int row = petTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a pet from the list first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String petId = (String) tableModel.getValueAt(row, 0);
        Pet pet = petService.findById(petId);
        if (pet == null) return;

        if (!pet.isAvailable()) {
            JOptionPane.showMessageDialog(this,
                "'" + pet.getName() + "' is already sold and cannot be added to cart.",
                "Pet Unavailable", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Check if already in cart
        for (Pet c : cart) {
            if (c.getPetId().equals(petId)) {
                JOptionPane.showMessageDialog(this,
                    "'" + pet.getName() + "' is already in your cart.",
                    "Already in Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        cart.add(pet);
        updateCartButton();
        lblStatus.setText("✔ " + pet.getName() + " added to cart.");
    }

    private void updateCartButton() {
        btnViewCart.setText("View Cart (" + cart.size() + ")");
    }

    private void openCart() {
        CartFrame cartFrame = new CartFrame(customer, cart, petService, orderService, this);
        cartFrame.setVisible(true);
        this.setVisible(false);
    }

    private void openMyOrders() {
        MyOrdersFrame f = new MyOrdersFrame(customer, orderService, petService, this);
        f.setVisible(true);
        this.setVisible(false);
    }

    private void logout() {
        orderService.saveAll();
        this.dispose();
        LoginFrame login = new LoginFrame(petService, orderService);
        login.setVisible(true);
    }

    public void clearCart() {
        cart.clear();
        updateCartButton();
    }
}
