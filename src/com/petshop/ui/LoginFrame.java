package com.petshop.ui;

import com.petshop.model.Admin;
import com.petshop.model.Customer;
import com.petshop.service.OrderService;
import com.petshop.service.PetService;
import com.petshop.exception.InvalidInputException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Login screen — the first window shown to the user.
 * Demonstrates ActionListener for buttons (OOP - Event Handling).
 */
public class LoginFrame extends JFrame {

    private PetService petService;
    private OrderService orderService;

    // UI Components
    private JRadioButton rbCustomer;
    private JRadioButton rbAdmin;
    private JPanel customerPanel;
    private JPanel adminPanel;
    private JTextField txtName;
    private JTextField txtPhone;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnClear;

    public LoginFrame(PetService petService, OrderService orderService) {
        this.petService   = petService;
        this.orderService = orderService;
        initUI();
    }

    private void initUI() {
        setTitle("Pet Shop Management System — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Main Panel ────────────────────────────────────────────────────────
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        main.setBackground(new Color(245, 248, 255));

        // ── Header ────────────────────────────────────────────────────────────
        JLabel header = new JLabel("🐾 Pet Shop Management System", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(34, 85, 170));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        main.add(header, BorderLayout.NORTH);

        // ── Center Panel ──────────────────────────────────────────────────────
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(245, 248, 255));

        // Role selection
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        rolePanel.setBackground(new Color(245, 248, 255));
        rolePanel.setBorder(BorderFactory.createTitledBorder("Select Role"));

        rbCustomer = new JRadioButton("Customer", true);
        rbAdmin    = new JRadioButton("Admin");
        rbCustomer.setBackground(new Color(245, 248, 255));
        rbAdmin.setBackground(new Color(245, 248, 255));
        rbCustomer.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rbAdmin.setFont(new Font("SansSerif", Font.PLAIN, 14));

        ButtonGroup group = new ButtonGroup();
        group.add(rbCustomer);
        group.add(rbAdmin);
        rolePanel.add(rbCustomer);
        rolePanel.add(rbAdmin);
        center.add(rolePanel);
        center.add(Box.createVerticalStrut(10));

        // Customer fields
        customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBackground(new Color(245, 248, 255));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        customerPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        txtName = new JTextField(18);
        customerPanel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        customerPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtPhone = new JTextField(18);
        customerPanel.add(txtPhone, gbc);
        center.add(customerPanel);

        // Admin fields
        adminPanel = new JPanel(new GridBagLayout());
        adminPanel.setBackground(new Color(245, 248, 255));
        adminPanel.setBorder(BorderFactory.createTitledBorder("Admin Details"));
        adminPanel.setVisible(false);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 8, 5, 8);
        gbc2.fill   = GridBagConstraints.HORIZONTAL;

        gbc2.gridx = 0; gbc2.gridy = 0;
        adminPanel.add(new JLabel("Password:"), gbc2);
        gbc2.gridx = 1; gbc2.gridy = 0;
        txtPassword = new JPasswordField(18);
        adminPanel.add(txtPassword, gbc2);
        center.add(adminPanel);
        center.add(Box.createVerticalStrut(10));

        main.add(center, BorderLayout.CENTER);

        // ── Buttons ───────────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnPanel.setBackground(new Color(245, 248, 255));

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setBackground(new Color(34, 139, 34));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(110, 36));

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnClear.setPreferredSize(new Dimension(110, 36));

        btnPanel.add(btnLogin);
        btnPanel.add(btnClear);
        main.add(btnPanel, BorderLayout.SOUTH);

        add(main);

        // ── Event Handlers (OOP - Event Handling) ─────────────────────────────

        // ItemListener on radio buttons — toggle panels
        rbCustomer.addActionListener(e -> {
            customerPanel.setVisible(true);
            adminPanel.setVisible(false);
            pack();
            setSize(480, 420);
        });

        rbAdmin.addActionListener(e -> {
            customerPanel.setVisible(false);
            adminPanel.setVisible(true);
            pack();
            setSize(480, 420);
        });

        // ActionListener on Login button
        btnLogin.addActionListener(e -> handleLogin());

        // ActionListener on Clear button
        btnClear.addActionListener(e -> clearFields());

        // Allow Enter key to submit
        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        };
        txtName.addKeyListener(enterKey);
        txtPhone.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);
    }

    private void handleLogin() {
        try {
            if (rbCustomer.isSelected()) {
                loginAsCustomer();
            } else {
                loginAsAdmin();
            }
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loginAsCustomer() throws InvalidInputException {
        String name  = txtName.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty()) {
            throw new InvalidInputException("Please enter your name.", "name");
        }
        if (phone.isEmpty()) {
            throw new InvalidInputException("Please enter your phone number.", "phone");
        }
        if (!phone.matches("\\d{10}")) {
            throw new InvalidInputException("Phone number must be exactly 10 digits.", "phone");
        }

        String customerId = "C" + System.currentTimeMillis() % 10000;
        Customer customer = new Customer(customerId, name, phone);

        // Open Pet List frame for this customer
        PetListFrame frame = new PetListFrame(customer, petService, orderService);
        frame.setVisible(true);
        this.setVisible(false);
    }

    private void loginAsAdmin() throws InvalidInputException {
        String password = new String(txtPassword.getPassword()).trim();
        if (password.isEmpty()) {
            throw new InvalidInputException("Please enter the admin password.", "password");
        }
        if (!Admin.validatePassword(password)) {
            throw new InvalidInputException("Incorrect admin password. Please try again.", "password");
        }

        AdminFrame frame = new AdminFrame(petService, orderService, this);
        frame.setVisible(true);
        this.setVisible(false);
    }

    private void clearFields() {
        txtName.setText("");
        txtPhone.setText("");
        txtPassword.setText("");
    }
}
