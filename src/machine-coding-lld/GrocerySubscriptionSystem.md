# Low-Level Design: Grocery Subscription System

This document outlines the low-level design for a grocery subscription service in Java. The system handles customer subscriptions, manages inventory, and automatically processes orders based on a schedule.

## Design Highlights

1.  **Separation of Concerns:** The design is broken down into clear services (`SubscriptionService`, `OrderService`, `InventoryService`, etc.), each with a single responsibility.
2.  **Entity Modeling:** Core entities like `Customer`, `Product`, `Subscription`, and `Order` are modeled as distinct classes with clear properties.
3.  **State Management:** Enums (`SubscriptionStatus`, `OrderStatus`, `Frequency`) are used to manage the state of subscriptions and orders, which makes the code cleaner and less error-prone.
4.  **Concurrency:** The `InventoryService` uses a `ConcurrentHashMap` to safely manage product stock in a multi-threaded environment (e.g., if multiple orders were processed in parallel).
5.  **Orchestration:** A central `GrocerySubscriptionSystem` class (acting as a scheduler) is responsible for simulating the passage of time and triggering the daily process of checking for due subscriptions, mimicking a real-world cron job or scheduled task.
6.  **Dependency Injection (Manual):** The services are instantiated and passed into the scheduler, which makes the system modular and easier to test.
7.  **Use Case Demo:** The `main` method provides a clear, step-by-step walkthrough of a customer signing up, creating a subscription, and the system automatically processing their order on the due date.

## Full Java Implementation

```java
package machine.coding.lld;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// --- ENUMS for State Management ---

enum SubscriptionStatus {
    ACTIVE,
    PAUSED,
    CANCELLED
}

enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    FAILED
}

enum Frequency {
    WEEKLY(7),
    BI_WEEKLY(14),
    MONTHLY(30);

    private final int days;

    Frequency(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}


// --- ENTITY CLASSES ---

class Customer {
    private final String id;
    private final String name;
    private String address;

    public Customer(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
}

class Product {
    private final String id;
    private final String name;
    private double price;

    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return id.equals(((Product) obj).id);
    }
}

class SubscriptionItem {
    private final Product product;
    private int quantity;

    public SubscriptionItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return product.getPrice() * quantity; }
}

class Subscription {
    private final String id;
    private final Customer customer;
    private final List<SubscriptionItem> items;
    private Frequency frequency;
    private LocalDate nextDeliveryDate;
    private SubscriptionStatus status;

    public Subscription(String id, Customer customer, Frequency frequency, LocalDate startDate) {
        this.id = id;
        this.customer = customer;
        this.frequency = frequency;
        this.nextDeliveryDate = startDate;
        this.status = SubscriptionStatus.ACTIVE;
        this.items = new ArrayList<>();
    }

    public void addItem(Product product, int quantity) {
        this.items.add(new SubscriptionItem(product, quantity));
    }

    public void updateNextDeliveryDate() {
        this.nextDeliveryDate = this.nextDeliveryDate.plusDays(frequency.getDays());
    }
    
    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<SubscriptionItem> getItems() { return items; }
    public LocalDate getNextDeliveryDate() { return nextDeliveryDate; }
    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
}

class Order {
    private final String id;
    private final Customer customer;
    private final List<SubscriptionItem> items;
    private final double totalPrice;
    private final LocalDate orderDate;
    private OrderStatus status;

    public Order(String id, Customer customer, List<SubscriptionItem> items, LocalDate orderDate) {
        this.id = id;
        this.customer = customer;
        this.items = items;
        this.orderDate = orderDate;
        this.totalPrice = items.stream().mapToDouble(SubscriptionItem::getPrice).sum();
        this.status = OrderStatus.PENDING;
    }
    
    public String getId() { return id; }
    public double getTotalPrice() { return totalPrice; }
    public void setStatus(OrderStatus status) { this.status = status; }
    @Override
    public String toString() {
        return String.format("Order [ID=%s, Customer=%s, Items=%d, Total=%.2f, Date=%s, Status=%s]",
                id, customer.getName(), items.size(), totalPrice, orderDate, status);
    }
}


// --- SERVICE LAYER ---

class InventoryService {
    private final Map<Product, Integer> stock = new ConcurrentHashMap<>();

    public void addProduct(Product product, int quantity) {
        stock.put(product, stock.getOrDefault(product, 0) + quantity);
    }

    public boolean checkAndReserveStock(Product product, int quantity) {
        return stock.compute(product, (p, currentStock) -> {
            if (currentStock != null && currentStock >= quantity) {
                return currentStock - quantity;
            }
            return currentStock; // Return original value if not enough stock
        }) != null;
    }
    
    public int getStock(Product p) { return stock.getOrDefault(p, 0); }
}

class PaymentService {
    public boolean processPayment(Customer customer, double amount) {
        System.out.printf("Processing payment of $%.2f for customer %s...%n", amount, customer.getName());
        // In a real system, this would integrate with a payment gateway.
        // For simulation, we'll assume payments always succeed.
        System.out.println("Payment successful.");
        return true;
    }
}

class NotificationService {
    public void sendNotification(Customer customer, String message) {
        System.out.printf("NOTIFICATION to %s: %s%n", customer.getName(), message);
    }
}

class SubscriptionService {
    private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();
    
    public Subscription createSubscription(Customer customer, Frequency freq, LocalDate startDate) {
        String id = "sub-" + idGenerator.incrementAndGet();
        Subscription sub = new Subscription(id, customer, freq, startDate);
        subscriptions.put(id, sub);
        return sub;
    }
    
    public List<Subscription> getActiveSubscriptions() {
        List<Subscription> activeSubs = new ArrayList<>();
        for (Subscription sub : subscriptions.values()) {
            if (sub.getStatus() == SubscriptionStatus.ACTIVE) {
                activeSubs.add(sub);
            }
        }
        return activeSubs;
    }
}

class OrderService {
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final AtomicLong idGenerator = new AtomicLong();

    public OrderService(InventoryService is, PaymentService ps, NotificationService ns) {
        this.inventoryService = is;
        this.paymentService = ps;
        this.notificationService = ns;
    }

    public Order createOrderFromSubscription(Subscription sub, LocalDate currentDate) {
        System.out.println("\nAttempting to create order for subscription: " + sub.getId());
        List<SubscriptionItem> itemsForOrder = new ArrayList<>();
        boolean allItemsAvailable = true;

        // 1. Check inventory for all items in the subscription
        for (SubscriptionItem item : sub.getItems()) {
            if (inventoryService.checkAndReserveStock(item.getProduct(), item.getQuantity())) {
                itemsForOrder.add(item);
                System.out.printf("  - Reserved %d of %s%n", item.getQuantity(), item.getProduct().getName());
            } else {
                allItemsAvailable = false;
                System.out.printf("  - OUT OF STOCK: %s%n", item.getProduct().getName());
                notificationService.sendNotification(sub.getCustomer(), "Item " + item.getProduct().getName() + " is out of stock and was removed from your order.");
            }
        }

        if (itemsForOrder.isEmpty()) {
            notificationService.sendNotification(sub.getCustomer(), "Your scheduled order could not be created as all items were out of stock.");
            return null;
        }

        // 2. Create the order
        String orderId = "ord-" + idGenerator.incrementAndGet();
        Order order = new Order(orderId, sub.getCustomer(), itemsForOrder, currentDate);
        order.setStatus(OrderStatus.PROCESSING);

        // 3. Process payment
        if (paymentService.processPayment(order.getCustomer(), order.getTotalPrice())) {
            order.setStatus(OrderStatus.SHIPPED);
            notificationService.sendNotification(order.getCustomer(), "Your order " + order.getId() + " has been processed and shipped!");
        } else {
            order.setStatus(OrderStatus.FAILED);
            // In a real system, we would need to handle payment failure (e.g., return items to stock).
            notificationService.sendNotification(order.getCustomer(), "Payment failed for your order " + order.getId() + ".");
        }
        
        System.out.println("  - " + order);
        return order;
    }
}


// --- MAIN SCHEDULER AND DEMO ---

public class GrocerySubscriptionSystem {
    private final SubscriptionService subscriptionService;
    private final OrderService orderService;

    public GrocerySubscriptionSystem(SubscriptionService ss, OrderService os) {
        this.subscriptionService = ss;
        this.orderService = os;
    }

    public void runDailyCheck(LocalDate currentDate) {
        System.out.printf("--- Running daily check for: %s ---%n", currentDate);
        List<Subscription> activeSubs = subscriptionService.getActiveSubscriptions();
        for (Subscription sub : activeSubs) {
            if (sub.getNextDeliveryDate().isEqual(currentDate)) {
                Order order = orderService.createOrderFromSubscription(sub, currentDate);
                if (order != null) {
                    sub.updateNextDeliveryDate();
                    System.out.printf("  - Subscription %s next delivery updated to: %s%n", sub.getId(), sub.getNextDeliveryDate());
                }
            }
        }
    }

    public static void main(String[] args) {
        // 1. Setup Services
        InventoryService inventoryService = new InventoryService();
        PaymentService paymentService = new PaymentService();
        NotificationService notificationService = new NotificationService();
        SubscriptionService subscriptionService = new SubscriptionService();
        OrderService orderService = new OrderService(inventoryService, paymentService, notificationService);
        GrocerySubscriptionSystem system = new GrocerySubscriptionSystem(subscriptionService, orderService);

        // 2. Add products to inventory
        Product milk = new Product("p1", "Organic Milk", 3.50);
        Product bread = new Product("p2", "Sourdough Bread", 4.50);
        Product eggs = new Product("p3", "Free-range Eggs", 5.00);
        inventoryService.addProduct(milk, 100);
        inventoryService.addProduct(bread, 50);
        inventoryService.addProduct(eggs, 200);

        // 3. Create a customer and their subscription
        Customer customer = new Customer("c1", "John Doe", "123 Main St");
        LocalDate today = LocalDate.now();
        Subscription johnsSub = subscriptionService.createSubscription(customer, Frequency.WEEKLY, today.plusDays(1));
        johnsSub.addItem(milk, 2); // 2 cartons of milk
        johnsSub.addItem(bread, 1); // 1 loaf of bread
        System.out.printf("Created subscription %s for %s. First delivery on %s.%n", johnsSub.getId(), customer.getName(), johnsSub.getNextDeliveryDate());

        // 4. Simulate the passing of time
        for (int i = 0; i < 10; i++) {
            system.runDailyCheck(today.plusDays(i));
        }
        
        // 5. Check inventory after order
        System.out.println("\n--- Final Inventory Check ---");
        System.out.printf("Milk stock: %d%n", inventoryService.getStock(milk));
        System.out.printf("Bread stock: %d%n", inventoryService.getStock(bread));

    }
}
```
