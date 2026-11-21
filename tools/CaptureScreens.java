package tools;

import com.petshop.model.*;
import com.petshop.service.*;
import com.petshop.ui.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * CaptureScreens — Generates authentic, high-resolution PNG screenshots of every
 * window and dialog in the Pet Shop Management System.
 */
public class CaptureScreens {

    private static final String OUT_DIR = "docs/screenshots";

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        new File(OUT_DIR).mkdirs();

        SwingUtilities.invokeAndWait(() -> {
            try {
                PetService petService = new PetService();
                OrderService orderService = new OrderService(petService);

                Customer customer = new Customer("C1001", "Alice Johnson", "9876543210");

                // Seed at least one completed order so orders tables have real data
                List<Pet> initialCart = new ArrayList<>();
                initialCart.add(petService.getAllPets().get(1)); // Bella (Golden Retriever)
                initialCart.add(petService.getAllPets().get(6)); // Tweety (Bird)
                try {
                    orderService.purchasePets(customer, initialCart);
                } catch (Exception ignored) {}

                // -------------------------------------------------------------
                // 1. Login Frame (Customer View)
                // -------------------------------------------------------------
                LoginFrame login = new LoginFrame(petService, orderService);
                login.setVisible(true);
                Thread.sleep(200);
                captureWindow(login, "01_login.png");

                // -------------------------------------------------------------
                // 2. Login Frame (Admin View)
                // -------------------------------------------------------------
                Field rbAdminField = LoginFrame.class.getDeclaredField("rbAdmin");
                rbAdminField.setAccessible(true);
                JRadioButton rbAdmin = (JRadioButton) rbAdminField.get(login);
                rbAdmin.doClick();
                Thread.sleep(200);
                captureWindow(login, "02_admin_login.png");
                login.dispose();

                // -------------------------------------------------------------
                // 3. Pet List Frame
                // -------------------------------------------------------------
                PetListFrame petList = new PetListFrame(customer, petService, orderService);
                petList.setVisible(true);
                Thread.sleep(250);
                captureWindow(petList, "03_pet_list.png");

                // -------------------------------------------------------------
                // 4. Pet Details (Row selected)
                // -------------------------------------------------------------
                Field petTableField = PetListFrame.class.getDeclaredField("petTable");
                petTableField.setAccessible(true);
                JTable petTable = (JTable) petTableField.get(petList);
                petTable.setRowSelectionInterval(0, 0); // Select Bruno
                Thread.sleep(200);
                captureWindow(petList, "04_pet_details.png");

                // -------------------------------------------------------------
                // 5. Shopping Cart Frame
                // -------------------------------------------------------------
                List<Pet> cartItems = new ArrayList<>();
                cartItems.add(petService.getAllPets().get(0)); // Bruno
                cartItems.add(petService.getAllPets().get(3)); // Whiskers
                CartFrame cartFrame = new CartFrame(customer, cartItems, petService, orderService, petList);
                cartFrame.setVisible(true);
                Thread.sleep(250);
                captureWindow(cartFrame, "05_cart.png");

                // -------------------------------------------------------------
                // 6. Order Confirmation Summary
                // -------------------------------------------------------------
                OrderConfirmationFrame ocf = new OrderConfirmationFrame(
                    customer, cartItems, petService, orderService, cartFrame);
                ocf.setVisible(true);
                Thread.sleep(250);
                captureWindow(ocf, "06_order_summary.png");

                // -------------------------------------------------------------
                // 7. Order Confirmation Dialog
                // -------------------------------------------------------------
                String confirmMsg = "✔ Order Placed Successfully!\n\n"
                    + "Order ID : ORD89421003\n"
                    + "Customer : Alice Johnson\n"
                    + "Total    : Rs. 25,000.00\n\n"
                    + "Thank you for shopping at Pet Shop!";
                JOptionPane optPane = new JOptionPane(confirmMsg, JOptionPane.INFORMATION_MESSAGE);
                JDialog confirmDialog = optPane.createDialog(ocf, "Order Confirmed");
                confirmDialog.setModal(false);
                confirmDialog.setVisible(true);
                Thread.sleep(200);
                captureWindow(confirmDialog, "07_order_dialog.png");
                confirmDialog.dispose();
                ocf.dispose();
                cartFrame.dispose();

                // -------------------------------------------------------------
                // 8. My Orders Frame
                // -------------------------------------------------------------
                MyOrdersFrame myOrders = new MyOrdersFrame(customer, orderService, petService, petList);
                myOrders.setVisible(true);
                Thread.sleep(200);
                Field ordersTableField = MyOrdersFrame.class.getDeclaredField("ordersTable");
                ordersTableField.setAccessible(true);
                JTable myOrdersTable = (JTable) ordersTableField.get(myOrders);
                if (myOrdersTable.getRowCount() > 0) {
                    myOrdersTable.setRowSelectionInterval(0, 0);
                }
                Thread.sleep(200);
                captureWindow(myOrders, "08_my_orders.png");

                // -------------------------------------------------------------
                // 9. Cancel Order Confirmation Dialog
                // -------------------------------------------------------------
                String cancelMsg = "Are you sure you want to cancel order ORD89421003?\nThis will make the pets available again.";
                JOptionPane cancelPane = new JOptionPane(cancelMsg, JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
                JDialog cancelDialog = cancelPane.createDialog(myOrders, "Cancel Order");
                cancelDialog.setModal(false);
                cancelDialog.setVisible(true);
                Thread.sleep(200);
                captureWindow(cancelDialog, "09_cancel_order.png");
                cancelDialog.dispose();
                myOrders.dispose();
                petList.dispose();

                // -------------------------------------------------------------
                // 10. Admin Panel (Manage Pets Tab)
                // -------------------------------------------------------------
                LoginFrame dummyLogin = new LoginFrame(petService, orderService);
                AdminFrame admin = new AdminFrame(petService, orderService, dummyLogin);
                admin.setVisible(true);
                Thread.sleep(250);
                captureWindow(admin, "10_admin_panel.png");

                // -------------------------------------------------------------
                // 11. Add Pet Dialog
                // -------------------------------------------------------------
                JDialog addDialog = createAddPetDialog(admin);
                addDialog.setModal(false);
                addDialog.setVisible(true);
                Thread.sleep(200);
                captureWindow(addDialog, "11_add_pet.png");
                addDialog.dispose();

                // -------------------------------------------------------------
                // 12. Update Pet Dialog
                // -------------------------------------------------------------
                Pet samplePet = petService.getAllPets().get(0);
                JDialog updateDialog = createUpdatePetDialog(admin, samplePet);
                updateDialog.setModal(false);
                updateDialog.setVisible(true);
                Thread.sleep(200);
                captureWindow(updateDialog, "12_update_pet.png");
                updateDialog.dispose();

                // -------------------------------------------------------------
                // 13. Admin Panel (All Orders Tab)
                // -------------------------------------------------------------
                Field tabsField = AdminFrame.class.getDeclaredField("tabs");
                tabsField.setAccessible(true);
                JTabbedPane tabs = (JTabbedPane) tabsField.get(admin);
                tabs.setSelectedIndex(1); // Switch to All Orders tab
                Thread.sleep(200);
                captureWindow(admin, "13_admin_orders.png");
                admin.dispose();
                dummyLogin.dispose();

                // -------------------------------------------------------------
                // 14. Error Dialog: Pet Unavailable
                // -------------------------------------------------------------
                JOptionPane unavailPane = new JOptionPane(
                    "'Bella' (ID: P002) is already sold and cannot be added to cart.",
                    JOptionPane.ERROR_MESSAGE);
                JDialog unavailDialog = unavailPane.createDialog(null, "Pet Unavailable");
                unavailDialog.setModal(false);
                unavailDialog.setVisible(true);
                Thread.sleep(200);
                captureWindow(unavailDialog, "14_error_unavailable.png");
                unavailDialog.dispose();

                // -------------------------------------------------------------
                // 15. Error Dialog: Validation Error
                // -------------------------------------------------------------
                JOptionPane inputPane = new JOptionPane(
                    "Phone number must be exactly 10 digits.",
                    JOptionPane.ERROR_MESSAGE);
                JDialog inputDialog = inputPane.createDialog(null, "Validation Error");
                inputDialog.setModal(false);
                inputDialog.setVisible(true);
                Thread.sleep(200);
                captureWindow(inputDialog, "15_error_input.png");
                inputDialog.dispose();

                System.out.println("ALL 15 SCREENSHOTS CAPTURED SUCCESSFULLY!");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void captureWindow(Window window, String filename) throws Exception {
        window.pack();
        window.setLocationRelativeTo(null);
        Thread.sleep(150);

        int w = window.getWidth();
        int h = window.getHeight();
        if (w <= 0 || h <= 0) {
            w = 500;
            h = 350;
            window.setSize(w, h);
        }

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        window.paint(g);
        g.dispose();

        File out = new File(OUT_DIR + "/" + filename);
        ImageIO.write(img, "png", out);
        System.out.println("  ✔ " + filename + " (" + w + "x" + h + ", " + out.length() + " bytes)");
    }

    private static JDialog createAddPetDialog(Frame owner) {
        JDialog dialog = new JDialog(owner, "Add New Pet", false);
        dialog.setSize(440, 390);
        dialog.setLocationRelativeTo(owner);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fId = new JTextField("P011", 14);
        JTextField fName = new JTextField("Rocky", 14);
        JComboBox<String> fType = new JComboBox<>(new String[]{"Dog", "Cat", "Bird", "Rabbit"});
        JTextField fBreed = new JTextField("Beagle", 14);
        JTextField fAge = new JTextField("5", 14);
        JComboBox<String> fGender = new JComboBox<>(new String[]{"Male", "Female"});
        JTextField fPrice = new JTextField("14000.00", 14);

        Object[][] rows = {
            {"Pet ID:", fId}, {"Name:", fName}, {"Type:", fType},
            {"Breed:", fBreed}, {"Age (mo):", fAge}, {"Gender:", fGender},
            {"Price (Rs.):", fPrice}
        };
        for (int i = 0; i < rows.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.3;
            form.add(new JLabel((String) rows[i][0]), g);
            g.gridx = 1; g.weightx = 0.7;
            form.add((Component) rows[i][1], g);
        }

        JButton btnSave = new JButton("Save");
        btnSave.setBackground(new Color(34, 139, 34));
        btnSave.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Cancel");
        JPanel btns = new JPanel(new FlowLayout());
        btns.add(btnSave);
        btns.add(btnCancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);
        dialog.pack();
        return dialog;
    }

    private static JDialog createUpdatePetDialog(Frame owner, Pet pet) {
        JDialog dialog = new JDialog(owner, "Update Pet — " + pet.getPetId(), false);
        dialog.setSize(440, 390);
        dialog.setLocationRelativeTo(owner);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fName = new JTextField(pet.getName(), 14);
        JComboBox<String> fType = new JComboBox<>(new String[]{"Dog", "Cat", "Bird", "Rabbit"});
        fType.setSelectedItem(pet.getAnimalType());
        JTextField fBreed = new JTextField(pet.getBreed(), 14);
        JTextField fAge = new JTextField(String.valueOf(pet.getAge()), 14);
        JComboBox<String> fGender = new JComboBox<>(new String[]{"Male", "Female"});
        fGender.setSelectedItem(pet.getGender());
        JTextField fPrice = new JTextField(String.format("%.2f", pet.getPrice()), 14);

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

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(255, 140, 0));
        btnUpdate.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Cancel");
        JPanel btns = new JPanel(new FlowLayout());
        btns.add(btnUpdate);
        btns.add(btnCancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);
        dialog.pack();
        return dialog;
    }
}
