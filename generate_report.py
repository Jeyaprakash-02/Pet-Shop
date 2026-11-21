"""
generate_report.py
Generates report/PetShop_Report.docx by filling in the OOP_MiniProject_Report_Template.docx.

Requirements:
    pip install python-docx

Usage:
    python generate_report.py
"""

import os
import shutil
import sys
from datetime import datetime

try:
    from docx import Document
    from docx.shared import Pt, Inches, RGBColor
    from docx.enum.text import WD_ALIGN_PARAGRAPH
    from docx.oxml.ns import qn
    from docx.oxml import OxmlElement
except ImportError:
    print("ERROR: python-docx is not installed.")
    print("Run: pip install python-docx")
    sys.exit(1)

TEMPLATE_PATH = r"C:\Users\syoge\Downloads\OOP_MiniProject_Report_Template.docx"
OUTPUT_PATH   = r"report\PetShop_Report.docx"

# ── Helpers ────────────────────────────────────────────────────────────────────

def add_heading(doc, text, level=1):
    doc.add_heading(text, level=level)

def add_paragraph(doc, text, bold=False, italic=False, font_size=None):
    p = doc.add_paragraph()
    run = p.add_run(text)
    run.bold   = bold
    run.italic = italic
    if font_size:
        run.font.size = Pt(font_size)
    return p

def add_code(doc, code_text):
    """Add a monospaced code block paragraph."""
    para = doc.add_paragraph()
    para.style = doc.styles["No Spacing"] if "No Spacing" in [s.name for s in doc.styles] else doc.styles["Normal"]
    run = para.add_run(code_text)
    run.font.name = "Courier New"
    run.font.size = Pt(9)
    shading = OxmlElement("w:shd")
    shading.set(qn("w:fill"), "F2F2F2")
    shading.set(qn("w:color"), "auto")
    shading.set(qn("w:val"), "clear")
    para._p.get_or_add_pPr().append(shading)
    return para

def add_table(doc, headers, rows):
    table = doc.add_table(rows=1, cols=len(headers))
    table.style = "Table Grid"
    hdr_cells = table.rows[0].cells
    for i, h in enumerate(headers):
        hdr_cells[i].text = h
        for run in hdr_cells[i].paragraphs[0].runs:
            run.bold = True
    for row in rows:
        row_cells = table.add_row().cells
        for i, val in enumerate(row):
            row_cells[i].text = str(val)

def replace_text_in_doc(doc, old, new):
    """Replace text across all paragraphs and tables in the document."""
    for para in doc.paragraphs:
        for run in para.runs:
            if old in run.text:
                run.text = run.text.replace(old, new)
    for table in doc.tables:
        for row in table.rows:
            for cell in row.cells:
                for para in cell.paragraphs:
                    for run in para.runs:
                        if old in run.text:
                            run.text = run.text.replace(old, new)

def find_and_replace_heading(doc, old_title, new_title):
    """Replace heading text if it matches old_title."""
    for para in doc.paragraphs:
        if para.style.name.startswith("Heading") and old_title.upper() in para.text.upper():
            for run in para.runs:
                run.text = run.text.replace(old_title, new_title)

# ── Main ───────────────────────────────────────────────────────────────────────

def main():
    if not os.path.exists(TEMPLATE_PATH):
        print(f"Template not found at: {TEMPLATE_PATH}")
        print("Creating report from scratch (template unavailable).")
        create_from_scratch()
        return

    print(f"Loading template: {TEMPLATE_PATH}")
    shutil.copy(TEMPLATE_PATH, OUTPUT_PATH)
    doc = Document(OUTPUT_PATH)

    # Replace TITLE on cover page(s)
    replace_text_in_doc(doc, "TITLE", "PET SHOP MANAGEMENT SYSTEM")
    replace_text_in_doc(doc, "[TITLE]", "PET SHOP MANAGEMENT SYSTEM")
    replace_text_in_doc(doc, "[PROJECT TITLE]", "PET SHOP MANAGEMENT SYSTEM")

    # Keep NAME (Reg. No) as-is — do not replace

    # Fill in content sections
    fill_sections(doc)

    os.makedirs("report", exist_ok=True)
    doc.save(OUTPUT_PATH)
    print(f"Report saved: {OUTPUT_PATH}")
    print("IMPORTANT: Open the .docx, right-click the Table of Contents, and select 'Update Field'.")


def fill_sections(doc):
    """Append content to the end of the document for each required section."""

    # Problem Description
    add_heading(doc, "PROBLEM DESCRIPTION / PROBLEM STATEMENT", level=1)
    add_paragraph(doc,
        "The Pet Shop Management System is a desktop application designed to streamline "
        "the operations of a small pet shop. The system allows customers to browse available "
        "pets, add them to a shopping cart, and complete purchases. An administrator can "
        "manage the pet inventory and view all customer orders. All data is persisted "
        "between sessions using plain text CSV files.", font_size=11)

    add_heading(doc, "OBJECTIVES", level=2)
    objectives = [
        "Provide a GUI desktop interface using Java Swing for pet shop management.",
        "Implement customer login (name + phone) and admin login (fixed password).",
        "Display available pets with full details in a JTable.",
        "Allow customers to add pets to a cart, review an order summary, and confirm purchase.",
        "Allow customers to view and cancel their previous orders.",
        "Provide admin functionality to add, update, and remove pets, and view all orders.",
        "Persist all data in data/pets.txt and data/orders.txt using CSV format.",
        "Demonstrate all required OOP concepts: Encapsulation, Abstraction, Inheritance, Polymorphism, Exception Handling, Threading, File Handling, Collections, and Event Handling.",
    ]
    for obj in objectives:
        p = doc.add_paragraph(style="List Bullet")
        p.add_run(obj).font.size = Pt(11)

    add_heading(doc, "CONSTRAINT / LIMITATIONS", level=2)
    limitations = [
        "Single-machine desktop application — not a web or network application.",
        "File-based storage (data/pets.txt, data/orders.txt) — no database.",
        "No real payment processing — purchase is simulated.",
        "No online delivery or email notifications.",
        "No multi-user network access — one user at a time.",
        "Admin password is fixed and stored in source code (suitable for educational demonstration only).",
    ]
    for lim in limitations:
        p = doc.add_paragraph(style="List Bullet")
        p.add_run(lim).font.size = Pt(11)

    # OOP Concepts Applied
    add_heading(doc, "OOP CONCEPTS APPLIED", level=1)

    sections = [
        ("Encapsulation",
         "All model classes (Pet, Person, Customer, Admin, Order) declare fields as private "
         "and expose them only through public getter and setter methods. This protects the "
         "internal state of objects from unauthorized direct access.",
         'public class Pet implements Purchasable {\n'
         '    private String petId;\n'
         '    private String name;\n'
         '    private double price;\n'
         '    private boolean available;\n\n'
         '    public String getPetId() { return petId; }\n'
         '    public void setPrice(double price) { this.price = price; }\n'
         '    public boolean isAvailable() { return available; }\n'
         '}'),

        ("Inheritance",
         "Customer and Admin both extend the abstract class Person, inheriting name and "
         "phoneNumber fields. Dog, Cat, Bird, and Rabbit all extend the abstract class Pet, "
         "inheriting all pet fields and the Purchasable interface implementation.",
         'public class Customer extends Person {\n'
         '    private String customerId;\n'
         '    public Customer(String id, String name, String phone) {\n'
         '        super(name, phone); // calls Person constructor\n'
         '        this.customerId = id;\n'
         '    }\n'
         '    @Override public String getRole() { return "Customer"; }\n'
         '}\n\n'
         'public class Dog extends Pet {\n'
         '    @Override public String getAnimalType() { return "Dog"; }\n'
         '}'),

        ("Polymorphism",
         "Method Overriding: getRole() is declared abstract in Person and overridden in "
         "Customer (returns 'Customer') and Admin (returns 'Admin'). getPetDetails() is "
         "declared abstract in Pet and overridden in Dog, Cat, Bird, Rabbit with type-specific text. "
         "Method Overloading: PetService.calculateTotal() exists in two versions — one accepting "
         "only a list, and one accepting a list plus a discount percentage.",
         '// Method Overriding\n'
         '@Override public String getRole() { return "Customer"; }\n'
         '@Override public String getPetDetails() {\n'
         '    return "Type: Dog 🐕\\nBreed: " + getBreed() + ...;\n'
         '}\n\n'
         '// Method Overloading\n'
         'public double calculateTotal(List<Pet> petList) { ... }\n'
         'public double calculateTotal(List<Pet> petList, double discountPercent) { ... }'),

        ("Exception Handling",
         "Two custom checked exceptions are defined: PetNotAvailableException (thrown when "
         "a pet is already sold) and InvalidInputException (thrown when user input is invalid). "
         "All file I/O is wrapped in try/catch/finally blocks. Errors are displayed to the "
         "user using JOptionPane.showMessageDialog().",
         'public class PetNotAvailableException extends Exception {\n'
         '    private final String petId;\n'
         '    public PetNotAvailableException(String msg, String petId) {\n'
         '        super(msg); this.petId = petId;\n'
         '    }\n'
         '}\n\n'
         '// In FileManager:\n'
         'try {\n'
         '    writer = new BufferedWriter(new FileWriter(PETS_FILE));\n'
         '    for (Pet pet : pets) { writer.write(pet.toCsv()); writer.newLine(); }\n'
         '} catch (IOException e) {\n'
         '    System.err.println("Error saving pets: " + e.getMessage());\n'
         '} finally {\n'
         '    if (writer != null) try { writer.close(); } catch (IOException e) {}\n'
         '}'),

        ("Threading",
         "File saving operations are performed in a background thread using SwingWorker "
         "so the GUI never freezes. The purchasePets() method and Pet.purchase() method "
         "are declared synchronized to prevent two concurrent purchases of the same pet.",
         'private void saveInBackground() {\n'
         '    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {\n'
         '        @Override\n'
         '        protected Void doInBackground() throws Exception {\n'
         '            FileManager.saveOrders(orders);\n'
         '            petService.savePets();\n'
         '            return null;\n'
         '        }\n'
         '    };\n'
         '    worker.execute();\n'
         '}\n\n'
         '// Synchronized purchase:\n'
         'public synchronized void purchase(String customerId)\n'
         '        throws PetNotAvailableException {\n'
         '    if (!available) throw new PetNotAvailableException(...);\n'
         '    this.available = false;\n'
         '    this.soldToCustomerId = customerId;\n'
         '}'),

        ("File Handling",
         "FileManager handles all persistence. At startup, ensureDataFiles() creates the "
         "data/ directory and pets.txt/orders.txt if they don't exist. Pets and orders are "
         "stored in CSV format, one record per line. Data is loaded at startup and saved "
         "on every change and on window close.",
         '// CSV format: petId,name,animalType,breed,age,gender,price,available,soldTo,orderId\n'
         'public static void savePets(List<Pet> pets) {\n'
         '    BufferedWriter writer = null;\n'
         '    try {\n'
         '        writer = new BufferedWriter(new FileWriter(PETS_FILE));\n'
         '        for (Pet pet : pets) {\n'
         '            writer.write(pet.toCsv());\n'
         '            writer.newLine();\n'
         '        }\n'
         '    } catch (IOException e) { ... }\n'
         '    finally { if (writer != null) try { writer.close(); } catch ... }\n'
         '}'),

        ("Collections",
         "ArrayList<Pet> stores all pet records in PetService. HashMap<String, Order> in "
         "OrderService maps orderId to Order for O(1) lookup. List<Pet> is used as the "
         "customer's shopping cart. HashSet<String> tracks sold pet IDs.",
         'private List<Pet> pets = new ArrayList<>();          // PetService\n'
         'private Map<String, Order> orders = new LinkedHashMap<>(); // OrderService\n'
         'private List<Pet> cart = new ArrayList<>();           // PetListFrame\n'
         'private Set<String> soldPetIds = new HashSet<>();     // PetService'),

        ("Event Handling",
         "ActionListener is added to every button via lambda. ListSelectionListener on JTable "
         "selection models responds when the user clicks a row. WindowListener (WindowAdapter) "
         "saves all data before the application closes.",
         '// ActionListener on button:\n'
         'btnLogin.addActionListener(e -> handleLogin());\n\n'
         '// ListSelectionListener on JTable:\n'
         'petTable.getSelectionModel().addListSelectionListener(\n'
         '    new ListSelectionListener() {\n'
         '        @Override public void valueChanged(ListSelectionEvent e) {\n'
         '            if (!e.getValueIsAdjusting()) onRowSelected();\n'
         '        }\n'
         '    });\n\n'
         '// WindowListener on JFrame:\n'
         'addWindowListener(new WindowAdapter() {\n'
         '    @Override public void windowClosing(WindowEvent e) {\n'
         '        orderService.saveAll(); System.exit(0);\n'
         '    }\n'
         '});'),
    ]

    for title, explanation, code in sections:
        add_heading(doc, title, level=2)
        add_paragraph(doc, explanation, font_size=11)
        add_code(doc, code)
        doc.add_paragraph()

    # Modules and Design
    add_heading(doc, "MODULES AND DESIGN / MODULE DESCRIPTIONS", level=1)
    modules = [
        ("com.petshop (Main)",
         "The root package contains only Main.java, which is the entry point. It sets "
         "the system look-and-feel, creates PetService and OrderService instances, "
         "and launches the LoginFrame on the Swing Event Dispatch Thread."),
        ("com.petshop.model",
         "Contains all domain model classes. Person is an abstract base class for Customer "
         "and Admin. Pet is an abstract class implementing Purchasable, extended by Dog, Cat, "
         "Bird, and Rabbit. Order represents a completed purchase. Purchasable is the interface "
         "that defines the purchase contract."),
        ("com.petshop.service",
         "PetService manages the ArrayList<Pet> collection: loading from file, CRUD operations, "
         "and two overloaded calculateTotal() methods. OrderService manages the "
         "HashMap<String, Order> collection: placing synchronized orders, cancelling orders, "
         "and saving via SwingWorker."),
        ("com.petshop.util",
         "FileManager provides static utility methods for reading and writing pets.txt and "
         "orders.txt in CSV format. All I/O is wrapped in try/catch/finally blocks. It also "
         "ensures the data directory and files are created if missing."),
        ("com.petshop.exception",
         "PetNotAvailableException is thrown when a customer attempts to purchase a sold pet. "
         "InvalidInputException is thrown for invalid user input (blank fields, bad phone number, "
         "duplicate pet ID, etc.). Both extend Exception (checked exceptions)."),
        ("com.petshop.ui",
         "Contains six JFrame subclasses. LoginFrame is shown first; it routes to PetListFrame "
         "(customer) or AdminFrame (admin). PetListFrame shows the pet table and cart management. "
         "CartFrame shows selected pets and routes to OrderConfirmationFrame. MyOrdersFrame shows "
         "past orders and allows cancellation. AdminFrame provides full CRUD and order viewing."),
    ]
    for name, desc in modules:
        add_heading(doc, name, level=2)
        add_paragraph(doc, desc, font_size=11)

    # Class Design and Package Diagram
    add_heading(doc, "CLASS DESIGN DIAGRAMS AND PACKAGE DIAGRAM", level=1)
    add_paragraph(doc,
        "The class and package diagrams are defined in Mermaid format in docs/class_diagram.md "
        "and docs/package_diagram.md respectively. The Mermaid source is included below as "
        "monospaced text. To render as images, use: npx -y @mermaid-js/mermaid-cli",
        font_size=11)

    # Read class diagram mermaid source
    for mmd_file, label in [("docs/class_diagram.md", "Class Diagram"),
                              ("docs/package_diagram.md", "Package Diagram")]:
        add_heading(doc, label, level=2)
        if os.path.exists(mmd_file):
            with open(mmd_file, encoding="utf-8") as f:
                content = f.read()
            add_code(doc, content[:3000])  # truncate if very long
        else:
            add_paragraph(doc, f"[See {mmd_file}]")

    # Implementation
    add_heading(doc, "IMPLEMENTATION", level=1)
    add_paragraph(doc,
        "The application follows a layered MVC-inspired architecture:\n"
        "UI → Service → Model → FileManager/data\n\n"
        "Customer Workflow:\n"
        "1. Customer logs in with name and phone on LoginFrame.\n"
        "2. PetListFrame loads all pets from PetService (which reads pets.txt via FileManager).\n"
        "3. Customer selects pets and adds them to the cart (List<Pet>).\n"
        "4. CartFrame displays selected pets and total. Customer clicks Confirm Order.\n"
        "5. OrderConfirmationFrame shows the full summary. Customer clicks Confirm.\n"
        "6. OrderService.purchasePets() (synchronized) calls Pet.purchase() (synchronized), "
        "creates an Order, stores it in HashMap<String, Order>, and saves via SwingWorker.\n"
        "7. Confirmation dialog shown; customer returns to PetListFrame.\n\n"
        "Admin Workflow:\n"
        "1. Admin logs in with password 'admin123'.\n"
        "2. AdminFrame loads pets and orders into two JTables.\n"
        "3. Admin can add/update/remove pets using dialogs with form validation.\n"
        "4. All changes are immediately saved to data/pets.txt.\n\n"
        "File Storage:\n"
        "pets.txt  — CSV: petId,name,animalType,breed,age,gender,price,available,soldTo,orderId\n"
        "orders.txt — CSV: orderId,customerId,customerName,customerPhone,petIds,total,date,status",
        font_size=11)

    # AI Tools Used
    add_heading(doc, "AI TOOLS USED", level=1)
    add_paragraph(doc,
        "This project was developed with the assistance of Google Antigravity, "
        "powered by Claude Sonnet 4.5 (Thinking). The AI tool was used to generate "
        "the complete Java source code, documentation, build scripts, and this report. "
        "All code was reviewed for correctness and OOP concept compliance before submission.",
        font_size=11)

    # Prompts Used
    add_heading(doc, "PROMPTS USED", level=1)
    add_paragraph(doc,
        "The following prompt was used to develop this project:\n\n"
        "[Task: 'Pet Shop Management System' — 22IT321 Object Oriented Programming]\n\n"
        "Create a complete Java Swing pet shop management application with:\n"
        "- Login screen for Customer and Admin\n"
        "- Pet list with 10 seeded pets (Dog, Cat, Bird, Rabbit types)\n"
        "- Shopping cart with add/remove and total calculation\n"
        "- Order confirmation with unique order ID\n"
        "- My Orders with cancellation (restores pet availability)\n"
        "- Admin CRUD for pets and order viewing\n"
        "- OOP: Encapsulation, Abstraction, Inheritance, Polymorphism, Exception Handling,\n"
        "  Threading (SwingWorker, synchronized), File Handling (CSV), Collections, Event Handling\n"
        "- Package structure: model, service, util, exception, ui\n"
        "- Build with javac/jar; run.bat and run.sh; README.md; docs/; report/",
        font_size=10)

    # Sample Java Code
    add_heading(doc, "SAMPLE JAVA CODE", level=1)

    add_heading(doc, "OrderService.purchasePets()", level=2)
    add_code(doc,
        'public synchronized Order purchasePets(Customer customer, List<Pet> cartPets)\n'
        '        throws PetNotAvailableException, InvalidInputException {\n'
        '    // Validate customer data\n'
        '    if (customer.getName().trim().isEmpty())\n'
        '        throw new InvalidInputException("Customer name cannot be blank.");\n\n'
        '    // Check all pets are still available before marking any as sold\n'
        '    for (Pet pet : cartPets) {\n'
        '        if (!pet.isAvailable())\n'
        '            throw new PetNotAvailableException(\n'
        '                "Pet \'" + pet.getName() + "\' is no longer available.", pet.getPetId());\n'
        '    }\n'
        '    String orderId = generateOrderId();\n'
        '    double total   = petService.calculateTotal(cartPets);\n\n'
        '    // Mark each pet as purchased (synchronized in Pet)\n'
        '    for (Pet pet : cartPets) {\n'
        '        pet.purchase(customer.getCustomerId());\n'
        '        pet.setOrderId(orderId);\n'
        '    }\n'
        '    Order order = new Order(orderId, customer.getCustomerId(),\n'
        '        customer.getName(), customer.getPhoneNumber(), cartPets, total);\n'
        '    orders.put(orderId, order);\n'
        '    saveInBackground(); // SwingWorker — non-blocking\n'
        '    return order;\n'
        '}'
    )

    add_heading(doc, "FileManager.loadPets()", level=2)
    add_code(doc,
        'public static List<Pet> loadPets() {\n'
        '    List<Pet> pets = new ArrayList<>();\n'
        '    BufferedReader reader = null;\n'
        '    try {\n'
        '        reader = new BufferedReader(new FileReader(PETS_FILE));\n'
        '        String line;\n'
        '        while ((line = reader.readLine()) != null) {\n'
        '            if (line.trim().isEmpty()) continue;\n'
        '            Pet pet = parsePetLine(line);\n'
        '            if (pet != null) pets.add(pet);\n'
        '        }\n'
        '    } catch (IOException e) {\n'
        '        System.err.println("Error loading pets: " + e.getMessage());\n'
        '    } finally {\n'
        '        if (reader != null)\n'
        '            try { reader.close(); } catch (IOException e) { /* ignore */ }\n'
        '    }\n'
        '    return pets;\n'
        '}'
    )

    add_heading(doc, "PetService.calculateTotal() — Overloaded", level=2)
    add_code(doc,
        '// Version 1: simple total\n'
        'public double calculateTotal(List<Pet> petList) {\n'
        '    double total = 0;\n'
        '    for (Pet p : petList) total += p.getPrice();\n'
        '    return total;\n'
        '}\n\n'
        '// Version 2: total with discount (Method Overloading)\n'
        'public double calculateTotal(List<Pet> petList, double discountPercent) {\n'
        '    double subtotal = calculateTotal(petList);\n'
        '    return subtotal - (subtotal * discountPercent / 100.0);\n'
        '}'
    )

    # Sample Input/Output — Screenshots
    add_heading(doc, "SAMPLE INPUT/OUTPUT — SCREENSHOTS", level=1)
    screenshots_dir = "docs/screenshots"
    screenshot_captions = {
        "01_login.png": "Login Screen — Customer and Admin radio button selection",
        "03_pet_list.png": "Pet List — All pets displayed in JTable",
        "04_pet_details.png": "Pet Details — Right panel shows selected pet details",
        "05_cart.png": "Shopping Cart — Selected pets with total amount",
        "06_order_summary.png": "Order Confirmation Summary — Before confirming",
        "07_order_dialog.png": "Order Confirmation Dialog — Order ID shown after purchase",
        "08_my_orders.png": "My Orders — Customer's past orders",
        "10_admin_panel.png": "Admin Panel — Manage Pets tab",
        "13_admin_orders.png": "Admin Orders — All Orders tab",
        "14_error_unavailable.png": "Error Dialog — Pet Not Available",
    }
    if os.path.exists(screenshots_dir):
        for fname, caption in screenshot_captions.items():
            fpath = os.path.join(screenshots_dir, fname)
            if os.path.exists(fpath):
                add_paragraph(doc, caption, bold=True)
                try:
                    doc.add_picture(fpath, width=Inches(5.5))
                except Exception as e:
                    add_paragraph(doc, f"[Screenshot: {fname}]", italic=True)
                doc.add_paragraph()
    else:
        add_paragraph(doc,
            "Screenshots are located in docs/screenshots/. Run tools/CaptureScreens.java "
            "to generate them, then re-run this script.", italic=True)

    # Deployment
    add_heading(doc, "DEPLOYMENT / DEPLOYMENT PORTAL DETAILS", level=1)
    add_paragraph(doc,
        "The Pet Shop Management System is a Java Swing desktop application distributed "
        "as a runnable JAR file (PetShop.jar) through GitHub.\n\n"
        "GitHub Repository: https://github.com/Jeyaprakash-02/Pet-Shop\n\n"
        "To run the application:\n"
        "  java -jar PetShop.jar\n\n"
        "Prerequisites: Java JDK 17 or later must be installed.\n"
        "The application must be run from the project root directory so that the "
        "data/ folder is found correctly.",
        font_size=11)

    # User Manuals
    add_heading(doc, "USER MANUALS", level=1)

    add_heading(doc, "Customer Manual", level=2)
    customer_steps = [
        "Launch the application: java -jar PetShop.jar",
        "On the Login screen, select 'Customer'.",
        "Enter your name and 10-digit phone number. Click Login.",
        "The Pet List screen shows all available pets in a table.",
        "Click any pet row to view its details in the right panel.",
        "To add a pet to your cart: select it and click 'Add to Cart'.",
        "To view your cart: click 'View Cart (n)'.",
        "In the cart, you can remove a pet by selecting it and clicking 'Remove Selected'.",
        "Click 'Confirm Order' to proceed to the Order Summary screen.",
        "Review the summary and click 'Confirm Order' to place the order.",
        "An Order ID is shown in the confirmation dialog.",
        "To view past orders: click 'My Orders' on the Pet List screen.",
        "To cancel an order: select it in My Orders and click 'Cancel Order'.",
        "Cancelled orders restore the pets to Available status.",
    ]
    for step in customer_steps:
        p = doc.add_paragraph(style="List Number")
        p.add_run(step).font.size = Pt(11)

    add_heading(doc, "Admin Manual", level=2)
    admin_steps = [
        "Launch the application: java -jar PetShop.jar",
        "On the Login screen, select 'Admin'.",
        "Enter the admin password: admin123. Click Login.",
        "The Admin Panel opens with two tabs: 'Manage Pets' and 'All Orders'.",
        "To add a pet: click 'Add Pet', fill in the form (ID, name, type, breed, age, gender, price), click Save.",
        "To update a pet: select it in the table, click 'Update Pet', modify fields, click Update.",
        "To remove a pet: select it in the table, click 'Remove Pet', confirm removal.",
        "Click 'Refresh' to reload pets from file.",
        "Click the 'All Orders' tab to view all customer orders.",
        "Click 'Logout' to return to the Login screen.",
    ]
    for step in admin_steps:
        p = doc.add_paragraph(style="List Number")
        p.add_run(step).font.size = Pt(11)

    # GitHub Link
    add_heading(doc, "GITHUB LINK", level=1)
    add_paragraph(doc, "https://github.com/Jeyaprakash-02/Pet-Shop", font_size=12)

    # References
    add_heading(doc, "REFERENCES", level=1)
    references = [
        "Oracle Corporation. (2024). Java SE 17 Documentation. https://docs.oracle.com/en/java/javase/17/",
        "Oracle Corporation. (2024). The Java Tutorials — Creating a GUI with Swing. https://docs.oracle.com/javase/tutorial/uiswing/",
        "Oracle Corporation. (2024). Java Collections Framework. https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/package-summary.html",
        "Oracle Corporation. (2024). Java I/O API. https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/package-summary.html",
        "Oracle Corporation. (2024). SwingWorker. https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/javax/swing/SwingWorker.html",
        "Bloch, J. (2018). Effective Java (3rd ed.). Addison-Wesley.",
    ]
    for ref in references:
        p = doc.add_paragraph(style="List Bullet")
        p.add_run(ref).font.size = Pt(10)


def create_from_scratch():
    """Fallback: create a minimal report without the template."""
    print("Creating report from scratch (no template found).")
    doc = Document()

    # Title page
    title = doc.add_heading("PET SHOP MANAGEMENT SYSTEM", level=0)
    title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    doc.add_paragraph("Course: 22IT321 Object-Oriented Programming", style="Normal").alignment = WD_ALIGN_PARAGRAPH.CENTER
    doc.add_paragraph("NAME (Reg. No)", style="Normal").alignment = WD_ALIGN_PARAGRAPH.CENTER
    doc.add_paragraph("NOV 2026", style="Normal").alignment = WD_ALIGN_PARAGRAPH.CENTER
    doc.add_page_break()

    fill_sections(doc)

    os.makedirs("report", exist_ok=True)
    doc.save(OUTPUT_PATH)
    print(f"Report saved: {OUTPUT_PATH}")


if __name__ == "__main__":
    main()
