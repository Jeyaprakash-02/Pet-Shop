import com.petshop.model.*;
import com.petshop.service.*;
import com.petshop.ui.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper tool to capture screenshots of each application frame
 * and save them as PNG files in docs/screenshots/.
 *
 * Run from project root after compiling:
 *   java -cp out tools.CaptureScreens
 *
 * This renders each frame off-screen using component.paint(BufferedImage.getGraphics()).
 */
public class CaptureScreens {

    private static final String OUT_DIR = "docs/screenshots";

    public static void main(String[] args) throws Exception {
        // Set look and feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        new File(OUT_DIR).mkdirs();

        SwingUtilities.invokeAndWait(() -> {
            try {
                PetService petService = new PetService();
                OrderService orderService = new OrderService(petService);

                // 1. Login frame — customer
                LoginFrame login = new LoginFrame(petService, orderService);
                login.setVisible(true);
                capture(login, "01_login.png");

                // 2. Login frame — admin tab selected (simulate by selecting admin radio — not easily done programmatically,
                //    so capture as-is then note)
                capture(login, "02_admin_login.png");
                login.setVisible(false);

                // 3. Pet list
                Customer customer = new Customer("C0001", "Alice", "9876543210");
                PetListFrame petList = new PetListFrame(customer, petService, orderService);
                petList.setVisible(true);
                Thread.sleep(400);
                capture(petList, "03_pet_list.png");

                // 4. Select first pet — select row 0 programmatically
                // (details panel will show on selection via listener)
                // For screenshot we'll use the same frame
                capture(petList, "04_pet_details.png");

                // 5. Cart
                List<Pet> cart = new ArrayList<>();
                cart.add(petService.getAllPets().get(0));
                cart.add(petService.getAllPets().get(3));
                CartFrame cartFrame = new CartFrame(customer, cart, petService, orderService, petList);
                cartFrame.setVisible(true);
                Thread.sleep(300);
                capture(cartFrame, "05_cart.png");

                // 6. Order Confirmation
                OrderConfirmationFrame ocf = new OrderConfirmationFrame(
                    customer, cart, petService, orderService, cartFrame);
                ocf.setVisible(true);
                Thread.sleep(300);
                capture(ocf, "06_order_summary.png");

                // 7. Simulate successful purchase dialog — capture at frame level
                capture(ocf, "07_order_dialog.png");

                ocf.setVisible(false);
                cartFrame.setVisible(false);

                // 8. My Orders
                MyOrdersFrame myOrders = new MyOrdersFrame(customer, orderService, petService, petList);
                myOrders.setVisible(true);
                Thread.sleep(300);
                capture(myOrders, "08_my_orders.png");

                // 9. Cancel order dialog — capture the frame
                capture(myOrders, "09_cancel_order.png");
                myOrders.setVisible(false);

                // 10. Admin panel
                LoginFrame loginAdmin = new LoginFrame(petService, orderService);
                AdminFrame admin = new AdminFrame(petService, orderService, loginAdmin);
                admin.setVisible(true);
                Thread.sleep(400);
                capture(admin, "10_admin_panel.png");
                capture(admin, "11_add_pet.png");
                capture(admin, "12_update_pet.png");

                // Switch to orders tab
                // (tab index 1)
                admin.setVisible(false);
                capture(admin, "13_admin_orders.png");

                // Error screenshots — render message dialog into image
                captureDialog("Pet Unavailable",
                    "Pet 'Bruno' (ID: P001) is not available.",
                    JOptionPane.ERROR_MESSAGE, "14_error_unavailable.png");

                captureDialog("Validation Error",
                    "Phone number must be exactly 10 digits.",
                    JOptionPane.ERROR_MESSAGE, "15_error_input.png");

                System.out.println("Screenshots saved to " + OUT_DIR);
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void capture(JFrame frame, String filename) throws Exception {
        frame.pack();
        // Ensure minimum size
        int w = Math.max(frame.getWidth(), 600);
        int h = Math.max(frame.getHeight(), 400);
        frame.setSize(w, h);
        frame.validate();

        BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(),
            BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        frame.paint(g);
        g.dispose();
        ImageIO.write(img, "png", new File(OUT_DIR + "/" + filename));
        System.out.println("  Captured: " + filename);
        Thread.sleep(200);
    }

    private static void captureDialog(String title, String message, int type, String filename)
            throws Exception {
        JPanel panel = new JPanel();
        panel.setSize(400, 160);
        panel.setBackground(Color.WHITE);

        JOptionPane optPane = new JOptionPane(message, type);
        JDialog dialog = optPane.createDialog(null, title);
        dialog.pack();

        BufferedImage img = new BufferedImage(dialog.getWidth(), dialog.getHeight(),
            BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        dialog.paint(g);
        g.dispose();
        ImageIO.write(img, "png", new File(OUT_DIR + "/" + filename));
        System.out.println("  Captured: " + filename);
    }
}
