# Requirements — Pet Shop Management System

**Course:** 22IT321 Object-Oriented Programming  
**Project:** Pet Shop Management System  
**Version:** 1.0  

---

## 1. Actors

| Actor    | Description                                      |
|----------|--------------------------------------------------|
| Customer | End user who browses pets, purchases and manages orders |
| Admin    | Shop manager who manages the pet inventory and views all orders |

---

## 2. Functional Requirements

| ID   | Requirement                                                        |
|------|--------------------------------------------------------------------|
| FR01 | The system shall display a login screen with Customer and Admin options |
| FR02 | Customers shall log in with name and phone number                  |
| FR03 | Admin shall log in with a fixed password                           |
| FR04 | The system shall display all pets with ID, name, type, breed, age, gender, price, status |
| FR05 | The system shall seed 8–10 sample pets on first run                |
| FR06 | Customers shall be able to view pet details                        |
| FR07 | Customers shall be able to add available pets to a cart            |
| FR08 | Customers shall be able to remove pets from the cart               |
| FR09 | The system shall calculate total price automatically               |
| FR10 | Customers shall be able to confirm purchase and receive an order ID |
| FR11 | The system shall prevent purchasing an already-sold pet            |
| FR12 | Customers shall view their previous orders                         |
| FR13 | Customers shall be able to cancel an order                         |
| FR14 | Cancelled orders shall restore pet availability                    |
| FR15 | Admins shall add, update, and remove pets                          |
| FR16 | Admins shall view all customer orders                              |
| FR17 | The system shall store data in `data/pets.txt` and `data/orders.txt` |
| FR18 | Data shall be loaded at startup and saved on every change and on close |

---

## 3. Non-Functional Requirements

| ID    | Requirement                                               |
|-------|-----------------------------------------------------------|
| NFR01 | The application shall run as a GUI desktop window (Swing) |
| NFR02 | No external libraries or frameworks                       |
| NFR03 | Built with plain `javac` and `jar`                        |
| NFR04 | Consistent fonts, padding, and colour scheme              |
| NFR05 | File saves shall not freeze the GUI (SwingWorker)         |
| NFR06 | Same pet shall not be sold twice (synchronized)           |

---

## 4. Use Cases

---

### UC01 — Customer Login

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC01                                                |
| Use Case Name | Customer Login                                      |
| Actor         | Customer                                            |
| Preconditions | Application is running; Login screen is displayed   |
| Main Flow     | 1. User selects "Customer" radio button. 2. Enters name and 10-digit phone. 3. Clicks Login. 4. System validates input. 5. System opens Pet List screen. |
| Alternate Flow | 4a. If name or phone is blank → error dialog. 4b. If phone is not 10 digits → error dialog. |
| Postconditions | Customer is logged in and can browse pets           |

---

### UC02 — Admin Login

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC02                                                |
| Use Case Name | Admin Login                                         |
| Actor         | Admin                                               |
| Preconditions | Application is running; Login screen is displayed   |
| Main Flow     | 1. User selects "Admin" radio button. 2. Enters password. 3. Clicks Login. 4. System validates password. 5. System opens Admin Panel. |
| Alternate Flow | 4a. If password is incorrect → error dialog.        |
| Postconditions | Admin is logged in and can manage pets and orders   |

---

### UC03 — Browse Pets

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC03                                                |
| Use Case Name | Browse Pets                                         |
| Actor         | Customer                                            |
| Preconditions | Customer is logged in; Pet List screen is open      |
| Main Flow     | 1. System loads all pets from file. 2. Pets displayed in JTable with ID, name, type, breed, age, gender, price, status. |
| Alternate Flow | None                                                |
| Postconditions | Customer can see all pets                           |

---

### UC04 — View Pet Details

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC04                                                |
| Use Case Name | View Pet Details                                    |
| Actor         | Customer                                            |
| Preconditions | Pet List screen is open                             |
| Main Flow     | 1. Customer clicks a row in the pet table. 2. System displays full pet details in the details panel. |
| Alternate Flow | None                                                |
| Postconditions | Pet details are displayed                           |

---

### UC05 — Add Pet to Cart

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC05                                                |
| Use Case Name | Add Pet to Cart                                     |
| Actor         | Customer                                            |
| Preconditions | Customer is on Pet List screen; at least one pet is selected |
| Main Flow     | 1. Customer selects a pet. 2. Clicks "Add to Cart". 3. System checks availability. 4. Pet is added to cart. 5. Cart count updates. |
| Alternate Flow | 3a. If pet is sold → error dialog "Pet Unavailable". 3b. If already in cart → warning dialog. |
| Postconditions | Pet is in the cart                                  |

---

### UC06 — Remove Pet from Cart

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC06                                                |
| Use Case Name | Remove Pet from Cart                                |
| Actor         | Customer                                            |
| Preconditions | Cart has at least one pet                           |
| Main Flow     | 1. Customer opens Cart. 2. Selects a pet. 3. Clicks "Remove Selected". 4. Pet removed from cart. 5. Total updated. |
| Alternate Flow | 2a. No pet selected → warning dialog.               |
| Postconditions | Pet removed from cart; total recalculated           |

---

### UC07 — Confirm Purchase

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC07                                                |
| Use Case Name | Confirm Purchase                                    |
| Actor         | Customer                                            |
| Preconditions | Cart has at least one pet                           |
| Main Flow     | 1. Customer clicks "Confirm Order" in Cart. 2. Order summary displayed. 3. Customer clicks "Confirm Order". 4. System creates order with unique ID. 5. Pets marked as sold. 6. Order saved. 7. Confirmation dialog shown. |
| Alternate Flow | 4a. If a pet became sold between cart and confirm → `PetNotAvailableException`, error shown. |
| Postconditions | Order created; pets marked as sold                  |

---

### UC08 — View My Orders

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC08                                                |
| Use Case Name | View My Orders                                      |
| Actor         | Customer                                            |
| Preconditions | Customer is logged in                               |
| Main Flow     | 1. Customer clicks "My Orders". 2. System shows all orders for this customer. 3. Customer clicks an order to see details. |
| Alternate Flow | None                                                |
| Postconditions | Customer can see their orders                       |

---

### UC09 — Cancel Order

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC09                                                |
| Use Case Name | Cancel Order                                        |
| Actor         | Customer                                            |
| Preconditions | At least one CONFIRMED order exists                 |
| Main Flow     | 1. Customer selects an order in My Orders. 2. Clicks "Cancel Order". 3. Confirmation dialog shown. 4. Order status set to CANCELLED. 5. Pets become available again. |
| Alternate Flow | 4a. Order is already cancelled → error dialog.      |
| Postconditions | Order cancelled; pets available again               |

---

### UC10 — Admin Add Pet

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC10                                                |
| Use Case Name | Admin Add Pet                                       |
| Actor         | Admin                                               |
| Preconditions | Admin is logged in                                  |
| Main Flow     | 1. Admin clicks "Add Pet". 2. Fills in ID, name, type, breed, age, gender, price. 3. Clicks Save. 4. Pet added to list and saved to file. |
| Alternate Flow | 3a. Invalid input → error dialog.                   |
| Postconditions | New pet appears in pet list                         |

---

### UC11 — Admin Update Pet

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC11                                                |
| Use Case Name | Admin Update Pet                                    |
| Actor         | Admin                                               |
| Preconditions | Admin is logged in; a pet is selected               |
| Main Flow     | 1. Admin selects a pet. 2. Clicks "Update Pet". 3. Updates fields. 4. Clicks Update. 5. Changes saved. |
| Alternate Flow | 4a. Invalid input → error dialog.                   |
| Postconditions | Pet details updated and saved                       |

---

### UC12 — Admin Remove Pet

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC12                                                |
| Use Case Name | Admin Remove Pet                                    |
| Actor         | Admin                                               |
| Preconditions | Admin is logged in; a pet is selected               |
| Main Flow     | 1. Admin selects a pet. 2. Clicks "Remove Pet". 3. Confirmation dialog. 4. Pet removed from list and file. |
| Alternate Flow | None                                                |
| Postconditions | Pet no longer appears in list                       |

---

### UC13 — Admin View Orders

| Field         | Detail                                              |
|---------------|-----------------------------------------------------|
| Use Case ID   | UC13                                                |
| Use Case Name | Admin View Orders                                   |
| Actor         | Admin                                               |
| Preconditions | Admin is logged in                                  |
| Main Flow     | 1. Admin clicks "All Orders" tab. 2. System displays all orders in JTable. |
| Alternate Flow | None                                                |
| Postconditions | Admin can see all customer orders                   |
