package com.petshop;

import com.petshop.service.OrderService;
import com.petshop.service.PetService;
import com.petshop.ui.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Application entry point.
 * Launches the LoginFrame on the Event Dispatch Thread.
 */
public class Main {

    public static void main(String[] args) {
        // Set system look-and-feel for a native-looking UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look-and-feel
        }

        SwingUtilities.invokeLater(() -> {
            PetService   petService   = new PetService();
            OrderService orderService = new OrderService(petService);

            LoginFrame loginFrame = new LoginFrame(petService, orderService);
            loginFrame.setVisible(true);
        });
    }
}
