"""
generate_report.py  —  Pet Shop Management System
Fills content into OOP_MiniProject_Report_Template.docx paragraph by paragraph,
preserving all headings, styles, cover page, and bonafide certificate.
Saves to report/PetShop_Report.docx.

Usage:
    python generate_report.py
"""

import os, shutil, copy
from docx import Document
from docx.shared import Pt, Inches, RGBColor, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml.ns import qn
from docx.oxml import OxmlElement

TEMPLATE = r"f:\petshop\report\OOP_MiniProject_Report_Template.docx"
OUTPUT   = r"f:\petshop\report\PetShop_Report.docx"
PYTHON   = r"C:\Users\jeyap\AppData\Local\Programs\Python\Python312\python.exe"

# ── Helpers ────────────────────────────────────────────────────────────────────

def shade_para(para, fill="F2F2F2"):
    pPr = para._p.get_or_add_pPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:val"), "clear")
    shd.set(qn("w:color"), "auto")
    shd.set(qn("w:fill"), fill)
    pPr.append(shd)

def add_body(doc, text, size=11, bold=False, italic=False, align=None):
    p = doc.add_paragraph(style="Normal")
    run = p.add_run(text)
    run.font.size = Pt(size)
    run.bold   = bold
    run.italic = italic
    if align:
        p.alignment = align
    return p

def add_bullet(doc, text, size=11):
    p = doc.add_paragraph(style="List Bullet")
    run = p.add_run(text)
    run.font.size = Pt(size)
    return p

def add_numbered(doc, text, size=11):
    p = doc.add_paragraph(style="List Number")
    run = p.add_run(text)
    run.font.size = Pt(size)
    return p

def add_code_block(doc, code):
    p = doc.add_paragraph(style="Normal")
    shade_para(p)
    p.paragraph_format.left_indent  = Cm(0.5)
    p.paragraph_format.right_indent = Cm(0.5)
    run = p.add_run(code)
    run.font.name = "Courier New"
    run.font.size = Pt(9)
    return p

def add_table(doc, headers, rows, col_widths=None):
    table = doc.add_table(rows=1, cols=len(headers))
    table.style = "Table Grid"
    hdr = table.rows[0].cells
    for i, h in enumerate(headers):
        hdr[i].text = h
        for run in hdr[i].paragraphs[0].runs:
            run.bold = True
            run.font.size = Pt(10)
    for row_data in rows:
        cells = table.add_row().cells
        for i, val in enumerate(row_data):
            cells[i].text = str(val)
            for run in cells[i].paragraphs[0].runs:
                run.font.size = Pt(10)
    return table

def find_para_index(doc, text_contains):
    """Return index of first paragraph whose text contains the given string."""
    for i, p in enumerate(doc.paragraphs):
        if text_contains.upper() in p.text.upper():
            return i
    return None

def insert_after(doc, ref_para, new_para_xml):
    """Insert new_para_xml element after ref_para in the document body."""
    ref_para._p.addnext(new_para_xml)

def paragraphs_after(doc, start_idx):
    """Generator of paragraphs from start_idx onwards."""
    return doc.paragraphs[start_idx:]

# ── Content inserter — appends after a heading paragraph ───────────────────────

def insert_content_after_heading(doc, heading_text, content_fn):
    """Find the heading paragraph by text, then insert content right after it."""
    idx = find_para_index(doc, heading_text)
    if idx is None:
        print(f"  WARNING: heading '{heading_text}' not found, appending at end.")
        content_fn(doc)
        return
    # We build a temporary mini-doc to get the paragraph XML nodes,
    # then splice them in after the heading.
    ref_para = doc.paragraphs[idx]
    # Collect all XML elements to insert (in reverse so addnext order is correct)
    nodes = build_content_nodes(doc, content_fn)
    for node in reversed(nodes):
        ref_para._p.addnext(node)

def build_content_nodes(doc, content_fn):
    """Call content_fn on a temp Document, return list of paragraph XML elements."""
    tmp = Document()
    content_fn(tmp)
    nodes = []
    for p in tmp.paragraphs:
        nodes.append(copy.deepcopy(p._p))
    for t in tmp.tables:
        nodes.append(copy.deepcopy(t._tbl))
    return nodes

# ── Section content functions ──────────────────────────────────────────────────

def content_problem_statement(doc):
    add_body(doc,
        "The Pet Shop Management System is a Java Swing desktop application designed to manage "
        "the daily operations of a small pet shop. The system supports two user roles — Customer "
        "and Admin — without any database or external library. All data is persisted in plain CSV "
        "text files (data/pets.txt and data/orders.txt). Customers can browse pets, add them to a "
        "cart, confirm purchases, and cancel orders. Admins can manage the pet inventory and "
        "monitor all customer orders.")

def content_objectives(doc):
    items = [
        "Provide a GUI desktop interface using Java Swing (JFrame) for pet shop management.",
        "Implement customer login (name + 10-digit phone) and admin login (fixed password: admin123).",
        "Display all pets in a JTable with ID, name, type, breed, age, gender, price, and status.",
        "Seed 10 sample pets (Dog, Cat, Bird, Rabbit types) on first run.",
        "Allow customers to browse, view details, add/remove pets from a cart.",
        "Show order summary with pet IDs, individual prices, and total; generate a unique Order ID.",
        "Allow customers to cancel orders, restoring pet availability.",
        "Provide admin CRUD operations for pets and a full view of all customer orders.",
        "Persist all data between sessions using CSV file storage via FileManager.",
        "Demonstrate all required OOP concepts: Encapsulation, Abstraction, Inheritance, "
        "Polymorphism, Exception Handling, Threading, File Handling, Collections, Event Handling.",
    ]
    for item in items:
        add_bullet(doc, item)

def content_constraints(doc):
    items = [
        "Single-machine desktop application — not a web or network application.",
        "File-based storage (data/pets.txt, data/orders.txt) — no database used.",
        "No real payment processing — purchase is simulated within the application.",
        "No online delivery, email notifications, or external APIs.",
        "No multi-user network access — one user session at a time.",
        "Admin password is fixed and stored in source code (suitable for educational demonstration only).",
    ]
    for item in items:
        add_bullet(doc, item)

def content_inheritance(doc):
    add_body(doc,
        "Inheritance is implemented through two hierarchies. The abstract class Person is extended "
        "by Customer and Admin, inheriting name and phoneNumber fields. The abstract class Pet "
        "(implementing Purchasable) is extended by Dog, Cat, Bird, and Rabbit, each inheriting "
        "all pet attributes and the synchronized purchase logic.")
    add_code_block(doc,
        "// Person hierarchy\n"
        "public abstract class Person {\n"
        "    private String name;\n"
        "    private String phoneNumber;\n"
        "    public abstract String getRole();\n"
        "}\n\n"
        "public class Customer extends Person {\n"
        "    private String customerId;\n"
        "    public Customer(String id, String name, String phone) {\n"
        "        super(name, phone);  // calls Person constructor\n"
        "        this.customerId = id;\n"
        "    }\n"
        "    @Override public String getRole() { return \"Customer\"; }\n"
        "}\n\n"
        "// Pet hierarchy\n"
        "public class Dog extends Pet {\n"
        "    @Override public String getAnimalType() { return \"Dog\"; }\n"
        "    @Override public String getPetDetails() { return \"Type: Dog ...\"; }\n"
        "}")

def content_polymorphism(doc):
    add_body(doc,
        "Method Overriding: getRole() is abstract in Person and overridden in Customer "
        "(returns 'Customer') and Admin (returns 'Admin'). getPetDetails() is abstract in Pet "
        "and overridden in Dog, Cat, Bird, and Rabbit with type-specific detail text.\n\n"
        "Method Overloading: PetService.calculateTotal() is defined in two versions — one "
        "taking only a List<Pet>, and another taking List<Pet> plus a discount percentage.")
    add_code_block(doc,
        "// Method Overriding\n"
        "@Override public String getRole() { return \"Customer\"; }  // in Customer\n"
        "@Override public String getRole() { return \"Admin\"; }     // in Admin\n\n"
        "@Override public String getPetDetails() {               // in Dog\n"
        "    return \"Type: Dog  Breed: \" + getBreed() + ...;\n"
        "}\n\n"
        "// Method Overloading — two calculateTotal() in PetService\n"
        "public double calculateTotal(List<Pet> petList) {\n"
        "    double total = 0;\n"
        "    for (Pet p : petList) total += p.getPrice();\n"
        "    return total;\n"
        "}\n"
        "public double calculateTotal(List<Pet> petList, double discountPercent) {\n"
        "    double subtotal = calculateTotal(petList);\n"
        "    return subtotal - (subtotal * discountPercent / 100.0);\n"
        "}")

def content_exception(doc):
    add_body(doc,
        "Two custom checked exceptions are defined: PetNotAvailableException (thrown when a "
        "pet is already sold) and InvalidInputException (thrown for blank fields, invalid phone, "
        "duplicate IDs, etc.). All file I/O in FileManager uses try/catch/finally. UI frames "
        "catch these exceptions and display them via JOptionPane.showMessageDialog().")
    add_code_block(doc,
        "public class PetNotAvailableException extends Exception {\n"
        "    private final String petId;\n"
        "    public PetNotAvailableException(String msg, String petId) {\n"
        "        super(msg);\n"
        "        this.petId = petId;\n"
        "    }\n"
        "}\n\n"
        "// In FileManager — try/catch/finally:\n"
        "BufferedWriter writer = null;\n"
        "try {\n"
        "    writer = new BufferedWriter(new FileWriter(PETS_FILE));\n"
        "    for (Pet pet : pets) { writer.write(pet.toCsv()); writer.newLine(); }\n"
        "} catch (IOException e) {\n"
        "    System.err.println(\"Error saving pets: \" + e.getMessage());\n"
        "} finally {\n"
        "    if (writer != null) try { writer.close(); } catch (IOException e) {}\n"
        "}\n\n"
        "// In UI — catch and display:\n"
        "try {\n"
        "    orderService.purchasePets(customer, cart);\n"
        "} catch (PetNotAvailableException ex) {\n"
        "    JOptionPane.showMessageDialog(this, ex.getMessage(),\n"
        "        \"Pet Not Available\", JOptionPane.ERROR_MESSAGE);\n"
        "}")

def content_threading(doc):
    add_body(doc,
        "File saving is performed in a background thread using SwingWorker so the GUI never "
        "freezes during I/O. The purchasePets() method and Pet.purchase() are declared "
        "synchronized to prevent two concurrent purchases of the same pet.")
    add_code_block(doc,
        "// SwingWorker — background file save (OrderService)\n"
        "private void saveInBackground() {\n"
        "    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {\n"
        "        @Override\n"
        "        protected Void doInBackground() throws Exception {\n"
        "            FileManager.saveOrders(orders);\n"
        "            petService.savePets();\n"
        "            return null;\n"
        "        }\n"
        "        @Override\n"
        "        protected void done() {\n"
        "            try { get(); }\n"
        "            catch (Exception e) { System.err.println(e.getMessage()); }\n"
        "        }\n"
        "    };\n"
        "    worker.execute();\n"
        "}\n\n"
        "// synchronized — prevents double purchase (Pet.java)\n"
        "public synchronized void purchase(String customerId)\n"
        "        throws PetNotAvailableException {\n"
        "    if (!available)\n"
        "        throw new PetNotAvailableException(\"Pet not available.\", petId);\n"
        "    this.available = false;\n"
        "    this.soldToCustomerId = customerId;\n"
        "}")

def content_filehandling(doc):
    add_body(doc,
        "FileManager handles all file persistence using BufferedReader and BufferedWriter with "
        "try/catch/finally. At startup, ensureDataFiles() creates the data/ directory and both "
        "CSV files if they do not exist. Pets and orders are saved on every change via SwingWorker "
        "and synchronously on window close via WindowListener.\n\n"
        "File formats:\n"
        "  pets.txt  : petId,name,animalType,breed,age,gender,price,available,soldToCustomerId,orderId\n"
        "  orders.txt: orderId,customerId,customerName,customerPhone,petIds(pipe-sep),total,date,status")
    add_code_block(doc,
        "// pets.txt sample line:\n"
        "P001,Bruno,Dog,German Shepherd,12,Male,15000.00,true,,\n"
        "P004,Whiskers,Cat,Persian,6,Female,10000.00,false,C1234,ORD567890123\n\n"
        "// orders.txt sample line:\n"
        "ORD567890123,C1234,Alice,9876543210,P004|P007,12500.00,2026-11-21 10:30:00,CONFIRMED\n\n"
        "// FileManager.ensureDataFiles()\n"
        "public static void ensureDataFiles() {\n"
        "    new File(DATA_DIR).mkdirs();\n"
        "    try {\n"
        "        new File(PETS_FILE).createNewFile();\n"
        "        new File(ORDERS_FILE).createNewFile();\n"
        "    } catch (IOException e) { System.err.println(e.getMessage()); }\n"
        "}")

def content_collections(doc):
    add_body(doc,
        "Four collections are used throughout the system: ArrayList<Pet> in PetService for the "
        "pet catalogue, HashMap<String,Order> in OrderService for O(1) order lookup by ID, "
        "List<Pet> as the customer's shopping cart, and HashSet<String> to track sold pet IDs.")
    add_code_block(doc,
        "// PetService — pet catalogue\n"
        "private List<Pet> pets = new ArrayList<>();\n"
        "private Set<String> soldPetIds = new HashSet<>();\n\n"
        "// OrderService — order map\n"
        "private Map<String, Order> orders = new LinkedHashMap<>();\n\n"
        "// PetListFrame — shopping cart\n"
        "private List<Pet> cart = new ArrayList<>();\n\n"
        "// Usage in OrderService:\n"
        "orders.put(orderId, order);          // HashMap.put  — O(1)\n"
        "Order o = orders.get(orderId);        // HashMap.get  — O(1)\n"
        "List<Order> mine = new ArrayList<>(); // filter by customerId\n"
        "for (Order o : orders.values())\n"
        "    if (o.getCustomerId().equals(id)) mine.add(o);")

def content_eventhandling(doc):
    add_body(doc,
        "ActionListener is added to every button via lambda or anonymous class. "
        "ListSelectionListener on JTable selection models responds when the user clicks a row "
        "(shows pet/order details). WindowListener (WindowAdapter) calls orderService.saveAll() "
        "before the application exits. KeyAdapter triggers login on Enter key press.")
    add_code_block(doc,
        "// ActionListener — button click\n"
        "btnLogin.addActionListener(e -> handleLogin());\n"
        "btnAddToCart.addActionListener(e -> addSelectedToCart());\n\n"
        "// ListSelectionListener — JTable row click\n"
        "petTable.getSelectionModel().addListSelectionListener(\n"
        "    new ListSelectionListener() {\n"
        "        @Override public void valueChanged(ListSelectionEvent e) {\n"
        "            if (!e.getValueIsAdjusting()) onRowSelected();\n"
        "        }\n"
        "    });\n\n"
        "// WindowListener — save on close\n"
        "addWindowListener(new WindowAdapter() {\n"
        "    @Override public void windowClosing(WindowEvent e) {\n"
        "        orderService.saveAll();\n"
        "        System.exit(0);\n"
        "    }\n"
        "});\n\n"
        "// KeyAdapter — Enter key on login fields\n"
        "txtName.addKeyListener(new KeyAdapter() {\n"
        "    @Override public void keyPressed(KeyEvent e) {\n"
        "        if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();\n"
        "    }\n"
        "});")

def content_modules_design(doc):
    add_body(doc,
        "The application follows a layered architecture:\n"
        "  UI Layer  →  Service Layer  →  Model Layer  →  FileManager / data/\n\n"
        "Each layer has a single responsibility. UI frames call Service methods only. "
        "Services use Model objects and FileManager. FileManager reads/writes the data files. "
        "No layer skips another.")

def content_modules_desc(doc):
    modules = [
        ("com.petshop (Main.java)",
         "Entry point. Sets system look-and-feel, instantiates PetService and OrderService, "
         "and launches LoginFrame on the Swing Event Dispatch Thread via SwingUtilities.invokeLater()."),
        ("com.petshop.model",
         "All domain objects. Person (abstract) → Customer, Admin. Pet (abstract, implements "
         "Purchasable) → Dog, Cat, Bird, Rabbit. Order holds orderId, customer info, pet list, "
         "total, date, and status (CONFIRMED/CANCELLED). Purchasable interface defines the "
         "purchase contract."),
        ("com.petshop.service",
         "PetService manages ArrayList<Pet>: load, CRUD, two overloaded calculateTotal() methods. "
         "OrderService manages HashMap<String,Order>: synchronized purchasePets(), cancelOrder(), "
         "and saveInBackground() via SwingWorker."),
        ("com.petshop.util",
         "FileManager: static methods for ensureDataFiles(), savePets(), loadPets(), "
         "saveOrders(), loadOrders(). All I/O uses BufferedReader/BufferedWriter with "
         "try/catch/finally. CSV format, one record per line."),
        ("com.petshop.exception",
         "PetNotAvailableException: thrown when a pet is already sold. "
         "InvalidInputException: thrown for blank fields, invalid phone, duplicate IDs. "
         "Both extend Exception (checked exceptions, not RuntimeException)."),
        ("com.petshop.ui",
         "Six JFrame subclasses forming the complete GUI. LoginFrame → PetListFrame or AdminFrame. "
         "PetListFrame hosts the JTable, details panel, and cart. CartFrame → OrderConfirmationFrame "
         "for purchase flow. MyOrdersFrame shows past orders and allows cancellation. "
         "AdminFrame has two JTabbedPane tabs: Manage Pets and All Orders."),
    ]
    for name, desc in modules:
        p = doc.add_paragraph(style="Normal")
        p.add_run(name).bold = True
        add_body(doc, desc)

def content_class_diagram(doc):
    add_body(doc,
        "The class diagram below shows all classes, their fields, methods, inheritance "
        "relationships, interface implementations, and associations. "
        "(Mermaid source — render with: npx -y @mermaid-js/mermaid-cli)")
    add_code_block(doc, """\
classDiagram
    class Person { <<abstract>> -name -phoneNumber +getRole()* }
    class Customer { -customerId +getRole() }
    class Admin { +validatePassword()$ }
    class Purchasable { <<interface>> +purchase() +cancelPurchase() +isAvailable() }
    class Pet { <<abstract>> -petId -name -breed -age -gender -price -available
                +getAnimalType()* +getPetDetails()* +purchase() synchronized }
    class Dog   { +getAnimalType() +getPetDetails() }
    class Cat   { +getAnimalType() +getPetDetails() }
    class Bird  { +getAnimalType() +getPetDetails() }
    class Rabbit{ +getAnimalType() +getPetDetails() }
    class Order { -orderId -customerId -pets -totalAmount -status +toCsv() }
    class PetService    { -pets:ArrayList -soldPetIds:HashSet +calculateTotal() +calculateTotal(discount) }
    class OrderService  { -orders:HashMap +purchasePets() synchronized +cancelOrder() }
    class FileManager   { +savePets()$ +loadPets()$ +saveOrders()$ +loadOrders()$ }
    Person <|-- Customer
    Person <|-- Admin
    Pet    <|-- Dog
    Pet    <|-- Cat
    Pet    <|-- Bird
    Pet    <|-- Rabbit
    Pet    ..|> Purchasable
    PetService  --> Pet
    OrderService --> Order
    OrderService --> PetService
    Order --> Pet
""")

def content_package_diagram(doc):
    add_body(doc,
        "The package diagram shows inter-package dependencies. The UI layer depends on "
        "Service, Model, and Exception packages. Service depends on Model, Util, and Exception. "
        "Util reads/writes the data/ files.")
    add_code_block(doc, """\
flowchart TD
    UI["com.petshop.ui\\n(LoginFrame, PetListFrame, CartFrame,\\nOrderConfirmationFrame, MyOrdersFrame, AdminFrame)"]
    SVC["com.petshop.service\\n(PetService, OrderService)"]
    MDL["com.petshop.model\\n(Person, Customer, Admin, Pet,\\nDog, Cat, Bird, Rabbit, Order, Purchasable)"]
    UTL["com.petshop.util\\n(FileManager)"]
    EXC["com.petshop.exception\\n(PetNotAvailableException, InvalidInputException)"]
    DAT[("data/\\npets.txt  orders.txt")]
    UI  --> SVC
    UI  --> MDL
    UI  --> EXC
    SVC --> MDL
    SVC --> UTL
    SVC --> EXC
    UTL --> DAT
""")

def content_implementation(doc):
    add_body(doc,
        "Customer Purchase Workflow:\n"
        "1. Customer logs in (LoginFrame) with name and phone. Customer object is created.\n"
        "2. PetListFrame loads all pets via PetService.loadPets() → FileManager.loadPets().\n"
        "3. Customer selects pets and adds them to List<Pet> cart.\n"
        "4. CartFrame displays cart with running total (PetService.calculateTotal()).\n"
        "5. OrderConfirmationFrame shows the full order summary.\n"
        "6. Customer confirms → OrderService.purchasePets() (synchronized):\n"
        "     a. Validates all pets still available.\n"
        "     b. Calls Pet.purchase() (synchronized) for each pet.\n"
        "     c. Creates Order, stores in HashMap<String,Order>.\n"
        "     d. Calls saveInBackground() (SwingWorker) — non-blocking.\n"
        "7. Confirmation dialog shows Order ID and total.\n\n"
        "Admin Workflow:\n"
        "1. Admin logs in with password 'admin123' (validated via Admin.validatePassword()).\n"
        "2. AdminFrame loads two JTables: all pets and all orders.\n"
        "3. Add/Update/Remove pets using modal JDialog forms with validation.\n"
        "4. All changes saved immediately to data/pets.txt.\n\n"
        "Data Persistence:\n"
        "  - Loaded at startup, saved on every change (SwingWorker), saved on close (WindowListener).\n"
        "  - pets.txt: petId,name,animalType,breed,age,gender,price,available,soldTo,orderId\n"
        "  - orders.txt: orderId,customerId,name,phone,petIds(pipe),total,date,status")

def content_ai_tools(doc):
    add_body(doc,
        "This project was developed with the assistance of Google Antigravity, powered by "
        "Claude Sonnet 4.5 (Thinking). The AI tool was used to generate the complete Java "
        "source code, Mermaid diagrams, all documentation files, build scripts, and this "
        "report. All generated code was designed to satisfy the OOP concept requirements "
        "of course 22IT321.")

def content_prompts(doc):
    add_body(doc, "The following complete prompt was used to develop this project:", bold=True)
    prompt_text = (
        'Task: "Pet Shop Management System"\n\n'
        "This is a college mini-project for the course 22IT321 Object Oriented Programming.\n"
        "Keep it SIMPLE and GENERIC. Only the core functions of a pet shop management application.\n"
        "No database, no web app, no frameworks, no external libraries. Pure Java (JDK 17+) with Swing (JFrame).\n\n"
        "The application must run as a GUI desktop window, not as a terminal/console program.\n\n"
        "GitHub repository: https://github.com/Jeyaprakash-02/Pet-Shop.git\n\n"
        "Functional scope: Login screen (Customer / Admin), Pet list (JTable, 10 seeded pets),\n"
        "Pet selection and cart, Order confirmation with unique Order ID, My Orders with cancellation,\n"
        "Admin panel (CRUD pets, view all orders).\n\n"
        "OOP concepts: Encapsulation, Abstraction (abstract class + interface), Inheritance,\n"
        "Polymorphism (overriding + overloading), Exception Handling (custom checked exceptions),\n"
        "Threading (SwingWorker + synchronized), File Handling (CSV), Collections, Event Handling.\n\n"
        "Package structure: com.petshop / model / service / util / exception / ui\n"
        "Build: javac + jar; run.bat (Windows); run.sh (Linux/macOS).\n"
        "Docs: README.md (Mermaid diagrams), docs/requirements.md, class_diagram.md,\n"
        "package_diagram.md, oop_concepts.md, sample_output.md.\n"
        "Report: generate_report.py using python-docx from the provided DOCX template."
    )
    add_code_block(doc, prompt_text)

def content_sample_code(doc):
    # Code snippet 1
    p = doc.add_paragraph(style="Normal")
    p.add_run("1. OrderService.purchasePets() — synchronized order placement").bold = True
    add_code_block(doc,
        "public synchronized Order purchasePets(Customer customer, List<Pet> cartPets)\n"
        "        throws PetNotAvailableException, InvalidInputException {\n"
        "    if (customer.getName().trim().isEmpty())\n"
        "        throw new InvalidInputException(\"Customer name cannot be blank.\");\n\n"
        "    // Validate all pets available BEFORE marking any as sold\n"
        "    for (Pet pet : cartPets) {\n"
        "        if (!pet.isAvailable())\n"
        "            throw new PetNotAvailableException(\n"
        "                \"Pet '\" + pet.getName() + \"' is no longer available.\",\n"
        "                pet.getPetId());\n"
        "    }\n"
        "    String orderId = generateOrderId();\n"
        "    double total   = petService.calculateTotal(cartPets);\n\n"
        "    for (Pet pet : cartPets) {\n"
        "        pet.purchase(customer.getCustomerId()); // synchronized on Pet\n"
        "        pet.setOrderId(orderId);\n"
        "    }\n"
        "    Order order = new Order(orderId, customer.getCustomerId(),\n"
        "        customer.getName(), customer.getPhoneNumber(), cartPets, total);\n"
        "    orders.put(orderId, order);\n"
        "    saveInBackground();  // SwingWorker — non-blocking\n"
        "    return order;\n"
        "}")

    # Code snippet 2
    p = doc.add_paragraph(style="Normal")
    p.add_run("2. FileManager.loadPets() — CSV file reading with try/catch/finally").bold = True
    add_code_block(doc,
        "public static List<Pet> loadPets() {\n"
        "    List<Pet> pets = new ArrayList<>();\n"
        "    BufferedReader reader = null;\n"
        "    try {\n"
        "        reader = new BufferedReader(new FileReader(PETS_FILE));\n"
        "        String line;\n"
        "        while ((line = reader.readLine()) != null) {\n"
        "            if (line.trim().isEmpty()) continue;\n"
        "            Pet pet = parsePetLine(line); // Dog/Cat/Bird/Rabbit based on type\n"
        "            if (pet != null) pets.add(pet);\n"
        "        }\n"
        "    } catch (IOException e) {\n"
        "        System.err.println(\"Error loading pets: \" + e.getMessage());\n"
        "    } finally {\n"
        "        if (reader != null)\n"
        "            try { reader.close(); } catch (IOException e) { /* ignore */ }\n"
        "    }\n"
        "    return pets;\n"
        "}")

    # Code snippet 3
    p = doc.add_paragraph(style="Normal")
    p.add_run("3. PetService.calculateTotal() — Method Overloading").bold = True
    add_code_block(doc,
        "// Version 1 — simple total\n"
        "public double calculateTotal(List<Pet> petList) {\n"
        "    double total = 0;\n"
        "    for (Pet p : petList) total += p.getPrice();\n"
        "    return total;\n"
        "}\n\n"
        "// Version 2 — total with discount percentage (Method Overloading)\n"
        "public double calculateTotal(List<Pet> petList, double discountPercent) {\n"
        "    double subtotal = calculateTotal(petList);\n"
        "    return subtotal - (subtotal * discountPercent / 100.0);\n"
        "}")

def content_screenshots(doc):
    shots_dir = r"f:\petshop\docs\screenshots"
    captions = [
        ("01_login.png",           "Fig 1: Login Screen — Customer / Admin role selection"),
        ("02_admin_login.png",     "Fig 2: Admin Login — password field"),
        ("03_pet_list.png",        "Fig 3: Pet List — all pets in JTable"),
        ("04_pet_details.png",     "Fig 4: Pet Details — right panel on row click"),
        ("05_cart.png",            "Fig 5: Shopping Cart — selected pets with total"),
        ("06_order_summary.png",   "Fig 6: Order Confirmation Summary"),
        ("07_order_dialog.png",    "Fig 7: Order Confirmation Dialog — Order ID shown"),
        ("08_my_orders.png",       "Fig 8: My Orders — customer's order history"),
        ("09_cancel_order.png",    "Fig 9: Order Cancellation confirmation"),
        ("10_admin_panel.png",     "Fig 10: Admin Panel — Manage Pets tab"),
        ("11_add_pet.png",         "Fig 11: Add Pet dialog"),
        ("12_update_pet.png",      "Fig 12: Update Pet dialog"),
        ("13_admin_orders.png",    "Fig 13: Admin Orders — All Orders tab"),
        ("14_error_unavailable.png","Fig 14: Error — Pet Not Available"),
        ("15_error_input.png",     "Fig 15: Error — Invalid Input"),
    ]
    any_found = False
    for fname, caption in captions:
        fpath = os.path.join(shots_dir, fname)
        if os.path.exists(fpath):
            any_found = True
            add_body(doc, caption, bold=True)
            try:
                doc.add_picture(fpath, width=Inches(5.5))
            except Exception:
                add_body(doc, f"[Image: {fname}]", italic=True)
            doc.add_paragraph()
    if not any_found:
        add_body(doc,
            "Screenshots will be generated after compiling the project and running "
            "tools/CaptureScreens.java. After generating screenshots, re-run this script "
            "to embed them in the report.", italic=True)

def content_deployment(doc):
    add_body(doc,
        "The Pet Shop Management System is a Java Swing desktop application distributed as a "
        "runnable JAR file (PetShop.jar) through GitHub.\n\n"
        "GitHub Repository: https://github.com/Jeyaprakash-02/Pet-Shop\n\n"
        "Prerequisites: Java JDK 17 or later must be installed.\n"
        "Run from the project root directory (so that data/ folder is resolved correctly).")
    add_code_block(doc,
        "# Windows — build and run\n"
        ".\\run.bat\n"
        "java -jar PetShop.jar\n\n"
        "# Linux / macOS\n"
        "chmod +x run.sh\n"
        "./run.sh\n"
        "java -jar PetShop.jar\n\n"
        "# Or compile and run directly:\n"
        "javac -d out -sourcepath src src\\com\\petshop\\Main.java\n"
        "java -cp out com.petshop.Main")

def content_deployment_portal(doc):
    add_body(doc,
        "This project is hosted on GitHub. The runnable JAR (PetShop.jar) is committed to the "
        "repository root and can be downloaded and executed on any machine with JDK 17+. "
        "No deployment portal, server, or cloud platform is used — the application runs entirely "
        "on the local machine.")

def content_user_manual(doc):
    add_body(doc, "Customer Manual", bold=True)
    customer_steps = [
        "Launch: java -jar PetShop.jar  (or run run.bat on Windows).",
        "On the Login screen, select the 'Customer' radio button.",
        "Enter your full name and a 10-digit phone number. Click Login.",
        "The Pet List screen shows all pets in a JTable (available and sold).",
        "Click any row to see the full pet details in the right panel.",
        "To add a pet to your cart: select it and click 'Add to Cart'. The cart count updates.",
        "Click 'View Cart (n)' to open your cart.",
        "In the cart, select a pet and click 'Remove Selected' to remove it.",
        "Click 'Confirm Order' to view the full order summary.",
        "On the Order Confirmation screen, click 'Confirm Order' to place the order.",
        "An Order ID and total are shown in a confirmation dialog.",
        "Click 'My Orders' on the Pet List screen to see your past orders.",
        "Select an order and click 'Cancel Order' to cancel it. Pets become available again.",
        "Click 'Logout' to return to the Login screen.",
    ]
    for step in customer_steps:
        add_numbered(doc, step)

    add_body(doc, "\nAdmin Manual", bold=True)
    admin_steps = [
        "Launch: java -jar PetShop.jar.",
        "Select 'Admin' radio button on the Login screen.",
        "Enter the admin password: admin123. Click Login.",
        "The Admin Panel opens with two tabs: 'Manage Pets' and 'All Orders'.",
        "To add a pet: click 'Add Pet', fill in ID/name/type/breed/age/gender/price, click Save.",
        "To update a pet: select it, click 'Update Pet', modify fields, click Update.",
        "To remove a pet: select it, click 'Remove Pet', confirm in the dialog.",
        "Click 'Refresh' to reload pets from the data file.",
        "Click the 'All Orders' tab to view every customer order.",
        "Click 'Logout' to return to the Login screen.",
    ]
    for step in admin_steps:
        add_numbered(doc, step)

def content_github(doc):
    add_body(doc, "https://github.com/Jeyaprakash-02/Pet-Shop", bold=True)

def content_references(doc):
    refs = [
        "Oracle Corporation. (2024). Java SE 17 Documentation. "
        "https://docs.oracle.com/en/java/javase/17/",
        "Oracle Corporation. (2024). The Java Tutorials — Creating a GUI with Swing. "
        "https://docs.oracle.com/javase/tutorial/uiswing/",
        "Oracle Corporation. (2024). Java Collections Framework. "
        "https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/package-summary.html",
        "Oracle Corporation. (2024). Java I/O API. "
        "https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/package-summary.html",
        "Oracle Corporation. (2024). Class SwingWorker. "
        "https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/javax/swing/SwingWorker.html",
        "Bloch, J. (2018). Effective Java (3rd ed.). Addison-Wesley Professional.",
    ]
    for ref in refs:
        add_bullet(doc, ref)

# ── Heading → content function map ─────────────────────────────────────────────

HEADING_CONTENT = [
    # (search_text,         content_function,          exact_heading_index)
    ("PROBLEM STATEMENT",   content_problem_statement,  47),
    ("OBJECTIVES",          content_objectives,         48),
    ("CONSTRAINT",          content_constraints,        49),
    ("INHERITANCE",         content_inheritance,        51),
    ("POLYMORPHISM",        content_polymorphism,       52),
    ("EXCEPTION HANDLING",  content_exception,          53),
    ("THREADING",           content_threading,          54),
    ("FILE HANDLING",       content_filehandling,       55),
    ("COLLECTIONS",         content_collections,        56),
    ("EVENT HANDLING",      content_eventhandling,      57),
    ("MODULES AND DESIGN",  content_modules_design,     58),
    ("MODULES DESCRIPTIONS",content_modules_desc,       59),
    ("CLASS DESIGN",        content_class_diagram,      60),
    ("PACKAGE DIAGRAM",     content_package_diagram,    61),
    ("IMPLEMENTATION",      content_implementation,     62),
    ("AI TOOLS USED",       content_ai_tools,           63),
    ("PROMPTS USED",        content_prompts,            64),
    ("SAMPLE JAVA CODE",    content_sample_code,        65),
    ("SCREEN SHOTS",        content_screenshots,        66),
    ("DEPLOYMENT PORTAL",   content_deployment_portal,  68),
    ("USER MANUALS",        content_user_manual,        69),
    ("GITHUB LINK",         content_github,             70),
    ("REFERENCES",          content_references,         71),
]

# ── Main ───────────────────────────────────────────────────────────────────────

def main():
    print(f"Loading template: {TEMPLATE}")
    shutil.copy(TEMPLATE, OUTPUT)
    doc = Document(OUTPUT)

    # 1. Replace TITLE on cover page and bonafide certificate
    for para in doc.paragraphs:
        for run in para.runs:
            if "TITLE" in run.text and "BONAFIDE" not in para.text.upper():
                run.text = run.text.replace("TITLE", "PET SHOP MANAGEMENT SYSTEM")
            elif "TITLE" in run.text:
                run.text = run.text.replace("TITLE", "PET SHOP MANAGEMENT SYSTEM")
    print("  ✔ Replaced TITLE on cover page and bonafide certificate.")

    # 2. Insert content after each heading paragraph
    # We work in reverse paragraph order so indices stay stable after insertions
    # Actually, we use insert_content_after_heading which searches by text each time.
    # To be safe process from last heading to first (reverse order).
    for search_text, content_fn, _ in reversed(HEADING_CONTENT):
        print(f"  Filling: {search_text}")
        insert_content_after_heading(doc, search_text, content_fn)

    # 3. Also handle DEPLOYMENT (index 67) separately
    print("  Filling: DEPLOYMENT")
    insert_content_after_heading(doc, "DEPLOYMENT PORTAL", content_deployment)

    os.makedirs(r"f:\petshop\report", exist_ok=True)
    doc.save(OUTPUT)
    print(f"\n✅ Report saved: {OUTPUT}")
    print("   Open in Word → right-click Table of Contents → 'Update Field'")

if __name__ == "__main__":
    main()
