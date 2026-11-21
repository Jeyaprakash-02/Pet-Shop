# OOP Concepts — Pet Shop Management System

| Concept | File | Class / Method | How It Is Used |
|---------|------|---------------|----------------|
| **Encapsulation** | `model/Pet.java` | `Pet` | All fields are `private`; accessed only via `getXxx()` and `setXxx()` methods |
| **Encapsulation** | `model/Person.java` | `Person` | `name` and `phoneNumber` are `private` with public getters/setters |
| **Encapsulation** | `model/Order.java` | `Order` | All order fields are `private` with getters/setters |
| **Abstraction (abstract class)** | `model/Person.java` | `Person` | Abstract class; declares abstract method `getRole()` |
| **Abstraction (abstract class)** | `model/Pet.java` | `Pet` | Abstract class; declares abstract methods `getAnimalType()` and `getPetDetails()` |
| **Abstraction (interface)** | `model/Purchasable.java` | `Purchasable` | Interface defining `purchase()`, `cancelPurchase()`, `isAvailable()` |
| **Inheritance** | `model/Customer.java` | `Customer extends Person` | Customer inherits name, phone from Person |
| **Inheritance** | `model/Admin.java` | `Admin extends Person` | Admin inherits Person's structure |
| **Inheritance** | `model/Dog.java` | `Dog extends Pet` | Dog inherits all Pet fields and Purchasable implementation |
| **Inheritance** | `model/Cat.java` | `Cat extends Pet` | Cat inherits all Pet fields |
| **Inheritance** | `model/Bird.java` | `Bird extends Pet` | Bird inherits all Pet fields |
| **Polymorphism (overriding)** | `model/Customer.java` | `getRole()` | Overrides `Person.getRole()` to return `"Customer"` |
| **Polymorphism (overriding)** | `model/Admin.java` | `getRole()` | Overrides `Person.getRole()` to return `"Admin"` |
| **Polymorphism (overriding)** | `model/Dog.java` | `getPetDetails()` | Overrides `Pet.getPetDetails()` with Dog-specific text |
| **Polymorphism (overriding)** | `model/Cat.java` | `getPetDetails()` | Overrides `Pet.getPetDetails()` with Cat-specific text |
| **Polymorphism (overriding)** | `model/Bird.java` | `getPetDetails()` | Overrides `Pet.getPetDetails()` with Bird-specific text |
| **Polymorphism (overloading)** | `service/PetService.java` | `calculateTotal()` | Two versions: `calculateTotal(List<Pet>)` and `calculateTotal(List<Pet>, double discountPercent)` |
| **Exception Handling (custom)** | `exception/PetNotAvailableException.java` | `PetNotAvailableException` | Custom checked exception thrown when a pet is already sold |
| **Exception Handling (custom)** | `exception/InvalidInputException.java` | `InvalidInputException` | Custom checked exception for invalid user input |
| **Exception Handling (try/catch/finally)** | `util/FileManager.java` | `savePets()`, `loadPets()`, etc. | All file I/O wrapped in try/catch/finally; reader/writer closed in finally block |
| **Exception Handling (JOptionPane)** | All UI frames | Various `ActionListener` handlers | Errors caught and shown with `JOptionPane.showMessageDialog()` |
| **Threading (SwingWorker)** | `service/OrderService.java` | `saveInBackground()` | `SwingWorker<Void,Void>` saves orders/pets in background so GUI stays responsive |
| **Threading (synchronized)** | `model/Pet.java` | `purchase()` | `synchronized` prevents the same pet being purchased twice concurrently |
| **Threading (synchronized)** | `service/OrderService.java` | `purchasePets()` | `synchronized` ensures atomic order creation |
| **File Handling (read)** | `util/FileManager.java` | `loadPets()`, `loadOrders()` | `BufferedReader` reads CSV files at startup |
| **File Handling (write)** | `util/FileManager.java` | `savePets()`, `saveOrders()` | `BufferedWriter` writes CSV files on every change |
| **File Handling (create)** | `util/FileManager.java` | `ensureDataFiles()` | Creates `data/pets.txt` and `data/orders.txt` if they don't exist |
| **Collections (ArrayList)** | `service/PetService.java` | `pets` field | `ArrayList<Pet>` stores all pet records |
| **Collections (HashMap)** | `service/OrderService.java` | `orders` field | `HashMap<String, Order>` maps orderId → order |
| **Collections (List for cart)** | `ui/PetListFrame.java` | `cart` field | `List<Pet>` holds the customer's selected pets |
| **Collections (Set)** | `service/PetService.java` | `soldPetIds` | `HashSet<String>` tracks which pet IDs have been sold |
| **Event Handling (ActionListener)** | All UI frames | All buttons | `ActionListener` added to every button via lambda/anonymous class |
| **Event Handling (ListSelectionListener)** | `ui/PetListFrame.java`, `ui/MyOrdersFrame.java`, `ui/AdminFrame.java` | JTable selection | `ListSelectionListener` on `JTable.getSelectionModel()` to respond to row clicks |
| **Event Handling (WindowListener)** | All frames | `addWindowListener()` | `WindowAdapter.windowClosing()` calls `orderService.saveAll()` before exit |
| **Event Handling (KeyListener)** | `ui/LoginFrame.java` | `txtName`, `txtPhone`, `txtPassword` | `KeyAdapter` triggers login on Enter key press |
