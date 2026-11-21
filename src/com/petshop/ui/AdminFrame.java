package com.petshop.ui;

import com.petshop.exception.InvalidInputException;
import com.petshop.model.*;
import com.petshop.service.OrderService;
import com.petshop.service.PetService;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

/**
 * Admin panel for managing pets and viewing all orders.
 * Demonstrates full CRUD operations and JTable with ListSelectionListener.
 * Demonstrates ActionListener and WindowListener (OOP - Event Handling).
 */
public class AdminFrame extends JFrame {

    private PetService petService;
    private OrderService orderService;
    private JFrame loginFrame;

    private JTabbedPane tabs;

    // Pets tab
    private JTable petsTable;
    private DefaultTableModel petsModel;
    private JButton btnAddPet;
    private JButton btnUpdatePet;
    private JButton btnRemovePet;
    private JButton btnRefreshPets;

    // Orders tab
    private JTable ordersTable;
    private DefaultTableModel ordersModel;
    private JButton btnRefreshOrders;

    public AdminFrame(PetService petService, OrderService orderService, JFrame loginFrame) {
        this.petService   = petService;
        this.orderService = orderService;
        this.loginFrame   = loginFrame;
        initUI();
        loadPetsTable();
        loadOrdersTable();
    }

    private void initUI() {
        setTitle("Pet Shop — Admin Panel");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);

        // WindowListener — save on close (OOP - Event Handling)
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                orderService.saveAll();
                System.exit(0);
            }
        });

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        main.setBackground(new Color(245, 248, 255));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(60, 60, 60));
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel title = new JLabel("⚙ Admin Panel — Pet Shop Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLogout.setBackground(new Color(190, 50, 50));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        header.add(title, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // Tabs
        tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.addTab("🐾 Manage Pets", buildPetsTab());
        tabs.addTab("📦 All Orders",  buildOrdersTab());
        main.add(tabs, BorderLayout.CENTER);

        add(main);

        // ActionListeners (OOP - Event Handling)
        btnLogout.addActionListener(e -> logout());
        btnAddPet.addActionListener(e -> showAddPetDialog());
        btnUpdatePet.addActionListener(e -> showUpdatePetDialog());
        btnRemovePet.addActionListener(e -> removePet());
        btnRefreshPets.addActionListener(e -> { petService.loadPets(); loadPetsTable(); });
        btnRefreshOrders.addActionListener(e -> { orderService.loadOrders(); loadOrdersTable(); });
    }

    // ── Pets Tab ──────────────────────────────────────────────────────────────

    private JPanel buildPetsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(245, 248, 255));

        String[] cols = {"ID", "Name", "Type", "Breed", "Age (mo)", "Gender", "Price (Rs.)", "Status"};
        petsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        petsTable = new JTable(petsModel);
        petsTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        petsTable.setRowHeight(24);
        petsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        petsTable.getTableHeader().setBackground(new Color(60, 60, 60));
        petsTable.getTableHeader().setForeground(Color.WHITE);
        petsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ListSelectionListener (OOP - Event Handling)
        petsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override public void valueChanged(ListSelectionEvent e) {
                boolean sel = petsTable.getSelectedRow() >= 0;
                btnUpdatePet.setEnabled(sel);
                btnRemovePet.setEnabled(sel);
            }
        });

        panel.add(new JScrollPane(petsTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        btnPanel.setBackground(new Color(245, 248, 255));
        btnRefreshPets = makeButton("Refresh",        new Color(70, 130, 180));
        btnAddPet      = makeButton("Add Pet",         new Color(34, 139, 34));
        btnUpdatePet   = makeButton("Update Pet",      new Color(255, 140, 0));
        btnRemovePet   = makeButton("Remove Pet",      new Color(190, 50, 50));
        btnUpdatePet.setEnabled(false);
        btnRemovePet.setEnabled(false);
        btnPanel.add(btnRefreshPets);
        btnPanel.add(btnAddPet);
        btnPanel.add(btnUpdatePet);
        btnPanel.add(btnRemovePet);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void loadPetsTable() {
        petsModel.setRowCount(0);
        for (Pet p : petService.getAllPets()) {
            petsModel.addRow(new Object[]{
                p.getPetId(), p.getName(), p.getAnimalType(), p.getBreed(),
                p.getAge(), p.getGender(),
                String.format("%.2f", p.getPrice()),
                p.isAvailable() ? "Available" : "Sold"
            });
        }
    }

    // ── Add Pet Dialog ────────────────────────────────────────────────────────

    private void showAddPetDialog() {
        JDialog dialog = new JDialog(this, "Add New Pet", true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JTextField fId     = new JTextField(14);
        JTextField fName   = new JTextField(14);
        String[]   types   = {"Dog", "Cat", "Bird", "Rabbit"};
        JComboBox<String> fType = new JComboBox<>(types);
        JTextField fBreed  = new JTextField(14);
        JTextField fAge    = new JTextField(14);
        String[]   genders = {"Male", "Female"};
        JComboBox<String> fGender = new JComboBox<>(genders);
        JTextField fPrice  = new JTextField(14);

        Object[][] rows = {
            {"Pet ID:",  fId},   {"Name:",   fName},
            {"Type:",    fType}, {"Breed:",  fBreed},
            {"Age (mo)::",fAge}, {"Gender:", fGender},
            {"Price (Rs.):", fPrice}
        };
        for (int i = 0; i < rows.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.3;
            form.add(new JLabel((String) rows[i][0]), g);
            g.gridx = 1; g.weightx = 0.7;
            form.add((Component) rows[i][1], g);
        }

        JButton btnSave   = makeButton("Save", new Color(34, 139, 34));
        JButton btnCancel2 = makeButton("Cancel", new Color(100, 100, 100));
        JPanel btns = new JPanel(new FlowLayout());
        btns.add(btnSave); btns.add(btnCancel2);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);

        // ActionListener (OOP - Event Handling)
        btnSave.addActionListener(ev -> {
            try {
                String id    = fId.getText().trim();
                String nm    = fName.getText().trim();
                String type  = (String) fType.getSelectedItem();
                String breed = fBreed.getText().trim();
                int    age   = Integer.parseInt(fAge.getText().trim());
                String gender= (String) fGender.getSelectedItem();
                double price = Double.parseDouble(fPrice.getText().trim());

                Pet pet;
                switch (type) {
                    case "Cat":    pet = new Cat(id, nm, breed, age, gender, price); break;
                    case "Bird":   pet = new Bird(id, nm, breed, age, gender, price); break;
                    case "Rabbit": pet = new Rabbit(id, nm, breed, age, gender, price); break;
                    default:       pet = new Dog(id, nm, breed, age, gender, price); break;
                }
                petService.addPet(pet);
                loadPetsTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Pet added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Age and Price must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel2.addActionListener(ev -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ── Update Pet Dialog ─────────────────────────────────────────────────────

    private void showUpdatePetDialog() {
        int row = petsTable.getSelectedRow();
        if (row < 0) return;
        String petId = (String) petsModel.getValueAt(row, 0);
        Pet existing = petService.findById(petId);
        if (existing == null) return;

        JDialog dialog = new JDialog(this, "Update Pet — " + petId, true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JTextField fName   = new JTextField(existing.getName(), 14);
        String[]   types   = {"Dog", "Cat", "Bird", "Rabbit"};
        JComboBox<String> fType = new JComboBox<>(types);
        fType.setSelectedItem(existing.getAnimalType());
        JTextField fBreed  = new JTextField(existing.getBreed(), 14);
        JTextField fAge    = new JTextField(String.valueOf(existing.getAge()), 14);
        String[]   genders = {"Male", "Female"};
        JComboBox<String> fGender = new JComboBox<>(genders);
        fGender.setSelectedItem(existing.getGender());
        JTextField fPrice  = new JTextField(String.format("%.2f", existing.getPrice()), 14);

        Object[][] rows = {
            {"Name:", fName}, {"Type:", fType}, {"Breed:", fBreed},
            {"Age (mo):", fAge}, {"Gender:", fGender}, {"Price (Rs.):", fPrice}
        };
        for (int i = 0; i < rows.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.3;
            form.add(new JLabel((String) rows[i][0]), g);
            g.gridx = 1; g.weightx = 0.7;
            form.add((Component) rows[i][1], g);
        }

        JButton btnSave    = makeButton("Update", new Color(255, 140, 0));
        JButton btnCancel2 = makeButton("Cancel", new Color(100, 100, 100));
        JPanel btns = new JPanel(new FlowLayout());
        btns.add(btnSave); btns.add(btnCancel2);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);

        btnSave.addActionListener(ev -> {
            try {
                String nm    = fName.getText().trim();
                String type  = (String) fType.getSelectedItem();
                String breed = fBreed.getText().trim();
                int    age   = Integer.parseInt(fAge.getText().trim());
                String gender= (String) fGender.getSelectedItem();
                double price = Double.parseDouble(fPrice.getText().trim());

                Pet updated;
                switch (type) {
                    case "Cat":    updated = new Cat(petId, nm, breed, age, gender, price); break;
                    case "Bird":   updated = new Bird(petId, nm, breed, age, gender, price); break;
                    case "Rabbit": updated = new Rabbit(petId, nm, breed, age, gender, price); break;
                    default:       updated = new Dog(petId, nm, breed, age, gender, price); break;
                }
                updated.setAvailable(existing.isAvailable());
                updated.setSoldToCustomerId(existing.getSoldToCustomerId());
                updated.setOrderId(existing.getOrderId());

                petService.updatePet(updated);
                loadPetsTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Pet updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Age and Price must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel2.addActionListener(ev -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ── Remove Pet ────────────────────────────────────────────────────────────

    private void removePet() {
        int row = petsTable.getSelectedRow();
        if (row < 0) return;
        String petId = (String) petsModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove pet " + petId + "?",
            "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            petService.removePet(petId);
            loadPetsTable();
            JOptionPane.showMessageDialog(this, "Pet removed successfully.", "Removed", JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Orders Tab ────────────────────────────────────────────────────────────

    private JPanel buildOrdersTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(245, 248, 255));

        String[] cols = {"Order ID", "Customer", "Phone", "Pets", "Total (Rs.)", "Date", "Status"};
        ordersModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = new JTable(ordersModel);
        ordersTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ordersTable.setRowHeight(24);
        ordersTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        ordersTable.getTableHeader().setBackground(new Color(60, 60, 60));
        ordersTable.getTableHeader().setForeground(Color.WHITE);
        panel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(new Color(245, 248, 255));
        btnRefreshOrders = makeButton("Refresh Orders", new Color(70, 130, 180));
        btnPanel.add(btnRefreshOrders);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void loadOrdersTable() {
        ordersModel.setRowCount(0);
        for (Map.Entry<String, Order> entry : orderService.getAllOrders().entrySet()) {
            Order o = entry.getValue();
            ordersModel.addRow(new Object[]{
                o.getOrderId(), o.getCustomerName(), o.getCustomerPhone(),
                o.getPets().size() + " pet(s)",
                String.format("%.2f", o.getTotalAmount()),
                o.getOrderDate(), o.getStatus().name()
            });
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(145, 34));
        return btn;
    }

    private void logout() {
        orderService.saveAll();
        this.dispose();
        loginFrame.setVisible(true);
    }
}
