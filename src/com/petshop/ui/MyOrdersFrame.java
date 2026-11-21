package com.petshop.ui;

import com.petshop.exception.InvalidInputException;
import com.petshop.model.Customer;
import com.petshop.model.Order;
import com.petshop.model.Pet;
import com.petshop.service.OrderService;
import com.petshop.service.PetService;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Displays the customer's past orders and allows order cancellation.
 * Demonstrates ListSelectionListener and ActionListener (OOP - Event Handling).
 */
public class MyOrdersFrame extends JFrame {

    private Customer customer;
    private OrderService orderService;
    private PetService petService;
    private PetListFrame parentFrame;

    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;
    private JButton btnCancel;
    private JButton btnBack;
    private JButton btnRefresh;

    public MyOrdersFrame(Customer customer, OrderService orderService,
                         PetService petService, PetListFrame parentFrame) {
        this.customer     = customer;
        this.orderService = orderService;
        this.petService   = petService;
        this.parentFrame  = parentFrame;
        initUI();
        populateTable();
    }

    private void initUI() {
        setTitle("Pet Shop — My Orders | " + customer.getName());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(880, 540);
        setLocationRelativeTo(null);

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
        header.setBackground(new Color(100, 60, 160));
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel title = new JLabel("📦 My Orders");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        JLabel custLbl = new JLabel("Customer: " + customer.getName());
        custLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        custLbl.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        header.add(custLbl, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Order ID", "Date", "Pets", "Total (Rs.)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = new JTable(tableModel);
        ordersTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ordersTable.setRowHeight(24);
        ordersTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        ordersTable.getTableHeader().setBackground(new Color(100, 60, 160));
        ordersTable.getTableHeader().setForeground(Color.WHITE);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ListSelectionListener (OOP - Event Handling)
        ordersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) onRowSelected();
            }
        });

        JScrollPane tableScroll = new JScrollPane(ordersTable);
        tableScroll.setPreferredSize(new Dimension(560, 300));

        detailsArea = new JTextArea(10, 26);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setBackground(new Color(250, 252, 255));
        detailsArea.setBorder(BorderFactory.createTitledBorder("Order Details"));
        JScrollPane detailScroll = new JScrollPane(detailsArea);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailScroll);
        split.setDividerLocation(560);
        main.add(split, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnPanel.setBackground(new Color(245, 248, 255));
        btnBack    = makeButton("← Back",         new Color(100, 100, 100));
        btnRefresh = makeButton("Refresh",         new Color(70, 130, 180));
        btnCancel  = makeButton("Cancel Order",    new Color(190, 50, 50));
        btnPanel.add(btnBack);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnCancel);
        main.add(btnPanel, BorderLayout.SOUTH);

        add(main);

        // ActionListeners (OOP - Event Handling)
        btnBack.addActionListener(e -> goBack());
        btnRefresh.addActionListener(e -> populateTable());
        btnCancel.addActionListener(e -> cancelSelectedOrder());
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(145, 34));
        return btn;
    }

    public void populateTable() {
        tableModel.setRowCount(0);
        List<Order> myOrders = orderService.getOrdersForCustomer(customer.getCustomerId());
        for (Order o : myOrders) {
            tableModel.addRow(new Object[]{
                o.getOrderId(),
                o.getOrderDate(),
                o.getPets().size() + " pet(s)",
                String.format("%.2f", o.getTotalAmount()),
                o.getStatus().name()
            });
        }
        detailsArea.setText("");
    }

    private void onRowSelected() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) return;
        String orderId = (String) tableModel.getValueAt(row, 0);
        Order order = orderService.getOrder(orderId);
        if (order != null) detailsArea.setText(order.toString());
    }

    private void cancelSelectedOrder() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to cancel.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String orderId = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel order " + orderId + "?\nThis will make the pets available again.",
            "Cancel Order", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            orderService.cancelOrder(orderId);
            JOptionPane.showMessageDialog(this,
                "Order " + orderId + " cancelled successfully.\nPets are now available again.",
                "Order Cancelled", JOptionPane.INFORMATION_MESSAGE);
            populateTable();
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Cannot Cancel", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        parentFrame.populateTable();
        parentFrame.setVisible(true);
        this.dispose();
    }
}
