package machine.coding.lld;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Low-Level Design: Grocery Subscription System
 * 
 * This system allows customers to:
 * 1. Create and manage grocery subscriptions
 * 2. Add/remove items from subscriptions
 * 3. Schedule deliveries (weekly, bi-weekly, monthly)
 * 4. Pause/resume subscriptions
 * 5. Process automatic orders based on subscription schedules
 * 6. Handle inventory and delivery management
 */

// ========================= ENUMS =========================

enum SubscriptionStatus {
    ACTIVE, PAUSED, CANCELLED
}

enum DeliveryFrequency {
    WEEKLY(7), BI_WEEKLY(14), MONTHLY(30);
    
    private final int days;
    
    DeliveryFrequency(int days) {
        this.days = days;
    }
    
    public int getDays() {
        return days;
    }
}

enum OrderStatus {
    PENDING, CONFIRMED, PACKED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
}

enum PaymentStatus {
    PENDING, COMPLETED, FAILED, REFUNDED
}

// ========================= CORE ENTITIES =========================

class Customer {
    private final String customerId;
    private String name;
    private String email;
    private String phone;
    private Address address;
    private final List<Subscription> subscriptions;
    
    public Customer(String customerId, String name, String email, String phone, Address address) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.subscriptions = new ArrayList<>();
    }
    
    // Getters and setters
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Address getAddress() { return address; }
    public List<Subscription> getSubscriptions() { return subscriptions; }
    
    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }
}

class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    public Address(String street, String city, String state, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }
    
    // Getters
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getCountry() { return country; }
    
    @Override
    public String toString() {
        return street + ", " + city + ", " + state + " " + zipCode + ", " + country;
    }
}

class Product {
    private final String productId;
    private String name;
    private String category;
    private double price;
    private String unit; // e.g., "kg", "lbs", "pieces"
    private boolean perishable;
    
    public Product(String productId, String name, String category, double price, String unit, boolean perishable) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.unit = unit;
        this.perishable = perishable;
    }
    
    // Getters
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getUnit() { return unit; }
    public boolean isPerishable() { return perishable; }
}

class SubscriptionItem {
    private Product product;
    private int quantity;
    
    public SubscriptionItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    // Getters and setters
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
}

class Subscription {
    private final String subscriptionId;
    private final String customerId;
    private String name; // e.g., "Weekly Groceries", "Monthly Essentials"
    private final List<SubscriptionItem> items;
    private DeliveryFrequency frequency;
    private LocalDate startDate;
    private LocalDate nextDeliveryDate;
    private SubscriptionStatus status;
    private final LocalDateTime createdAt;
    
    public Subscription(String subscriptionId, String customerId, String name, DeliveryFrequency frequency, LocalDate startDate) {
        this.subscriptionId = subscriptionId;
        this.customerId = customerId;
        this.name = name;
        this.frequency = frequency;
        this.startDate = startDate;
        this.nextDeliveryDate = startDate;
        this.status = SubscriptionStatus.ACTIVE;
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters
    public String getSubscriptionId() { return subscriptionId; }
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public List<SubscriptionItem> getItems() { return items; }
    public DeliveryFrequency getFrequency() { return frequency; }
    public LocalDate getNextDeliveryDate() { return nextDeliveryDate; }
    public SubscriptionStatus getStatus() { return status; }
    
    // Business methods
    public void addItem(SubscriptionItem item) {
        items.add(item);
    }
    
    public void removeItem(String productId) {
        items.removeIf(item -> item.getProduct().getProductId().equals(productId));
    }
    
    public void updateItemQuantity(String productId, int newQuantity) {
        for (SubscriptionItem item : items) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(newQuantity);
                break;
            }
        }
    }
    
    public void pause() {
        this.status = SubscriptionStatus.PAUSED;
    }
    
    public void resume() {
        this.status = SubscriptionStatus.ACTIVE;
    }
    
    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
    }
    
    public void updateNextDeliveryDate() {
        this.nextDeliveryDate = this.nextDeliveryDate.plusDays(frequency.getDays());
    }
    
    public double getTotalPrice() {
        return items.stream().mapToDouble(SubscriptionItem::getTotalPrice).sum();
    }
}

class Order {
    private final String orderId;
    private final String customerId;
    private final String subscriptionId;
    private final List<SubscriptionItem> items;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private double totalAmount;
    private LocalDateTime orderDate;
    private LocalDate deliveryDate;
    private String deliverySlot; // e.g., "9AM-12PM"
    
    public Order(String orderId, String customerId, String subscriptionId, List<SubscriptionItem> items, LocalDate deliveryDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.subscriptionId = subscriptionId;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.orderDate = LocalDateTime.now();
        this.deliveryDate = deliveryDate;
        this.totalAmount = items.stream().mapToDouble(SubscriptionItem::getTotalPrice).sum();
    }
    
    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public String getSubscriptionId() { return subscriptionId; }
    public List<SubscriptionItem> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public String getDeliverySlot() { return deliverySlot; }
    
    // Business methods
    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
    }
    
    public void pack() {
        this.status = OrderStatus.PACKED;
    }
    
    public void ship() {
        this.status = OrderStatus.OUT_FOR_DELIVERY;
    }
    
    public void deliver() {
        this.status = OrderStatus.DELIVERED;
    }
    
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
    
    public void setDeliverySlot(String slot) {
        this.deliverySlot = slot;
    }
    
    public void markPaymentCompleted() {
        this.paymentStatus = PaymentStatus.COMPLETED;
    }
    
    public void markPaymentFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
    }
}

// ========================= SERVICES =========================

class InventoryService {
    private final Map<String, Integer> inventory; // productId -> quantity
    
    public InventoryService() {
        this.inventory = new ConcurrentHashMap<>();
    }
    
    public void addStock(String productId, int quantity) {
        inventory.merge(productId, quantity, Integer::sum);
    }
    
    public boolean checkAvailability(String productId, int requiredQuantity) {
        return inventory.getOrDefault(productId, 0) >= requiredQuantity;
    }
    
    public boolean reserveStock(String productId, int quantity) {
        int currentStock = inventory.getOrDefault(productId, 0);
        if (currentStock >= quantity) {
            inventory.put(productId, currentStock - quantity);
            return true;
        }
        return false;
    }
    
    public void releaseStock(String productId, int quantity) {
        inventory.merge(productId, quantity, Integer::sum);
    }
    
    public int getStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }
}

class PaymentService {
    public boolean processPayment(String customerId, double amount) {
        // Simulate payment processing
        // In real implementation, this would integrate with payment gateways
        System.out.println("Processing payment of $" + amount + " for customer: " + customerId);
        
        // Simulate 90% success rate
        return Math.random() > 0.1;
    }
    
    public void refundPayment(String customerId, double amount) {
        System.out.println("Refunding $" + amount + " to customer: " + customerId);
    }
}

class NotificationService {
    public void sendOrderConfirmation(String customerId, String orderId) {
        System.out.println("Sending order confirmation for order " + orderId + " to customer " + customerId);
    }
    
    public void sendDeliveryNotification(String customerId, String orderId, LocalDate deliveryDate) {
        System.out.println("Sending delivery notification for order " + orderId + " to customer " + customerId + 
                          " for delivery on " + deliveryDate);
    }
    
    public void sendSubscriptionReminder(String customerId, String subscriptionId, LocalDate nextDelivery) {
        System.out.println("Sending subscription reminder for subscription " + subscriptionId + 
                          " to customer " + customerId + ". Next delivery: " + nextDelivery);
    }
}

// ========================= MAIN SYSTEM =========================

public class GrocerySubscriptionSystem {
    private final Map<String, Customer> customers;
    private final Map<String, Product> products;
    private final Map<String, Subscription> subscriptions;
    private final Map<String, Order> orders;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final AtomicLong idGenerator;
    
    public GrocerySubscriptionSystem() {
        this.customers = new ConcurrentHashMap<>();
        this.products = new ConcurrentHashMap<>();
        this.subscriptions = new ConcurrentHashMap<>();
        this.orders = new ConcurrentHashMap<>();
        this.inventoryService = new InventoryService();
        this.paymentService = new PaymentService();
        this.notificationService = new NotificationService();
        this.idGenerator = new AtomicLong(1);
    }
    
    // Customer management
    public String registerCustomer(String name, String email, String phone, Address address) {
        String customerId = "CUST" + idGenerator.getAndIncrement();
        Customer customer = new Customer(customerId, name, email, phone, address);
        customers.put(customerId, customer);
        return customerId;
    }
    
    // Product management
    public String addProduct(String name, String category, double price, String unit, boolean perishable) {
        String productId = "PROD" + idGenerator.getAndIncrement();
        Product product = new Product(productId, name, category, price, unit, perishable);
        products.put(productId, product);
        return productId;
    }
    
    // Subscription management
    public String createSubscription(String customerId, String name, DeliveryFrequency frequency, LocalDate startDate) {
        if (!customers.containsKey(customerId)) {
            throw new IllegalArgumentException("Customer not found");
        }
        
        String subscriptionId = "SUB" + idGenerator.getAndIncrement();
        Subscription subscription = new Subscription(subscriptionId, customerId, name, frequency, startDate);
        subscriptions.put(subscriptionId, subscription);
        customers.get(customerId).addSubscription(subscription);
        
        return subscriptionId;
    }
    
    public void addItemToSubscription(String subscriptionId, String productId, int quantity) {
        Subscription subscription = subscriptions.get(subscriptionId);
        Product product = products.get(productId);
        
        if (subscription == null || product == null) {
            throw new IllegalArgumentException("Subscription or Product not found");
        }
        
        SubscriptionItem item = new SubscriptionItem(product, quantity);
        subscription.addItem(item);
    }
    
    public void removeItemFromSubscription(String subscriptionId, String productId) {
        Subscription subscription = subscriptions.get(subscriptionId);
        if (subscription != null) {
            subscription.removeItem(productId);
        }
    }
    
    public void pauseSubscription(String subscriptionId) {
        Subscription subscription = subscriptions.get(subscriptionId);
        if (subscription != null) {
            subscription.pause();
        }
    }
    
    public void resumeSubscription(String subscriptionId) {
        Subscription subscription = subscriptions.get(subscriptionId);
        if (subscription != null) {
            subscription.resume();
        }
    }
    
    // Order processing
    public String processSubscriptionOrder(String subscriptionId) {
        Subscription subscription = subscriptions.get(subscriptionId);
        if (subscription == null || subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            return null;
        }
        
        // Check inventory availability
        for (SubscriptionItem item : subscription.getItems()) {
            if (!inventoryService.checkAvailability(item.getProduct().getProductId(), item.getQuantity())) {
                System.out.println("Insufficient inventory for product: " + item.getProduct().getName());
                return null;
            }
        }
        
        // Create order
        String orderId = "ORD" + idGenerator.getAndIncrement();
        Order order = new Order(orderId, subscription.getCustomerId(), subscriptionId, 
                               subscription.getItems(), subscription.getNextDeliveryDate());
        
        // Reserve inventory
        for (SubscriptionItem item : subscription.getItems()) {
            inventoryService.reserveStock(item.getProduct().getProductId(), item.getQuantity());
        }
        
        // Process payment
        if (paymentService.processPayment(subscription.getCustomerId(), order.getTotalAmount())) {
            order.markPaymentCompleted();
            order.confirm();
            orders.put(orderId, order);
            
            // Update next delivery date
            subscription.updateNextDeliveryDate();
            
            // Send notifications
            notificationService.sendOrderConfirmation(subscription.getCustomerId(), orderId);
            
            return orderId;
        } else {
            // Release reserved inventory on payment failure
            for (SubscriptionItem item : subscription.getItems()) {
                inventoryService.releaseStock(item.getProduct().getProductId(), item.getQuantity());
            }
            order.markPaymentFailed();
            return null;
        }
    }
    
    // Process all due subscriptions (would be called by a scheduler)
    public void processDueSubscriptions() {
        LocalDate today = LocalDate.now();
        
        for (Subscription subscription : subscriptions.values()) {
            if (subscription.getStatus() == SubscriptionStatus.ACTIVE && 
                !subscription.getNextDeliveryDate().isAfter(today)) {
                
                String orderId = processSubscriptionOrder(subscription.getSubscriptionId());
                if (orderId != null) {
                    System.out.println("Processed subscription order: " + orderId + 
                                     " for subscription: " + subscription.getSubscriptionId());
                }
            }
        }
    }
    
    // Getters for testing and monitoring
    public Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }
    
    public Subscription getSubscription(String subscriptionId) {
        return subscriptions.get(subscriptionId);
    }
    
    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }
    
    public InventoryService getInventoryService() {
        return inventoryService;
    }
    
    // ========================= DEMO =========================
    
    public static void main(String[] args) {
        GrocerySubscriptionSystem system = new GrocerySubscriptionSystem();
        
        // Setup products
        String milkId = system.addProduct("Milk", "Dairy", 3.99, "gallon", true);
        String breadId = system.addProduct("Bread", "Bakery", 2.49, "loaf", true);
        String riceId = system.addProduct("Rice", "Grains", 4.99, "bag", false);
        
        // Add inventory
        system.getInventoryService().addStock(milkId, 100);
        system.getInventoryService().addStock(breadId, 50);
        system.getInventoryService().addStock(riceId, 200);
        
        // Register customer
        Address address = new Address("123 Main St", "Anytown", "CA", "12345", "USA");
        String customerId = system.registerCustomer("John Doe", "john@example.com", "555-1234", address);
        
        // Create subscription
        String subscriptionId = system.createSubscription(customerId, "Weekly Essentials", 
                                                         DeliveryFrequency.WEEKLY, LocalDate.now());
        
        // Add items to subscription
        system.addItemToSubscription(subscriptionId, milkId, 2);
        system.addItemToSubscription(subscriptionId, breadId, 1);
        system.addItemToSubscription(subscriptionId, riceId, 1);
        
        // Process the subscription order
        String orderId = system.processSubscriptionOrder(subscriptionId);
        
        // Display results
        System.out.println("\n=== Subscription Demo Results ===");
        System.out.println("Customer: " + system.getCustomer(customerId).getName());
        System.out.println("Subscription: " + system.getSubscription(subscriptionId).getName());
        System.out.println("Order ID: " + orderId);
        if (orderId != null) {
            Order order = system.getOrder(orderId);
            System.out.println("Order Total: $" + order.getTotalAmount());
            System.out.println("Order Status: " + order.getStatus());
            System.out.println("Payment Status: " + order.getPaymentStatus());
        }
        
        // Check inventory after order
        System.out.println("\n=== Inventory After Order ===");
        System.out.println("Milk stock: " + system.getInventoryService().getStock(milkId));
        System.out.println("Bread stock: " + system.getInventoryService().getStock(breadId));
        System.out.println("Rice stock: " + system.getInventoryService().getStock(riceId));
    }
}
