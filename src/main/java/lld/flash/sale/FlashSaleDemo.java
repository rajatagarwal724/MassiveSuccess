package lld.flash.sale;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FlashSaleDemo {
    public static void main(String[] args) throws InterruptedException {
        // Create a flash sale system
        FlashSaleSystem flashSaleSystem = FlashSaleSystem.getInstance();
        
        // Create products for the flash sale
        Product smartphone = new Product("P001", "Smartphone X", 599.99, 100);
        Product laptop = new Product("P002", "Laptop Pro", 1299.99, 50);
        Product headphones = new Product("P003", "Wireless Headphones", 149.99, 200);
        
        // Create a flash sale event
        LocalDateTime startTime = LocalDateTime.now().plusSeconds(5); // Start in 5 seconds
        LocalDateTime endTime = startTime.plusMinutes(10); // Last for 10 minutes
        
        FlashSale flashSale = new FlashSale("SUMMER2025", "Summer Flash Sale", startTime, endTime);
        
        // Add products to the flash sale with discounted prices
        flashSale.addProduct(smartphone, 399.99, 100);
        flashSale.addProduct(laptop, 999.99, 50);
        flashSale.addProduct(headphones, 99.99, 200);
        
        // Register the flash sale in the system
        flashSaleSystem.registerFlashSale(flashSale);
        
        // Prepare users
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            users.add(new User("U" + String.format("%03d", i), "User " + i, "user" + i + "@example.com"));
        }
        
        // Prepare random products to order
        List<Product> availableProducts = Arrays.asList(smartphone, laptop, headphones);
        
        System.out.println("Waiting for flash sale to start...");
        // Wait until the flash sale starts
        while (LocalDateTime.now().isBefore(startTime)) {
            Thread.sleep(100);
        }
        
        System.out.println("Flash sale started! Processing orders...");
        
        // Simulate concurrent users trying to place orders
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(users.size());
        
        for (User user : users) {
            executor.submit(() -> {
                try {
                    // Randomize user behavior
                    Thread.sleep(ThreadLocalRandom.current().nextInt(5000));
                    
                    // Select a random product
                    Product selectedProduct = availableProducts.get(ThreadLocalRandom.current().nextInt(availableProducts.size()));
                    int quantity = ThreadLocalRandom.current().nextInt(1, 4); // Order 1-3 items
                    
                    // Try to place an order
                    try {
                        Order order = flashSaleSystem.placeOrder(user, flashSale.getId(), selectedProduct.getId(), quantity);
                        if (order != null) {
                            // Simulate payment processing
                            PaymentResult paymentResult = flashSaleSystem.processPayment(order, new PaymentInfo("CARD", "1234567890"));
                            
                            if (paymentResult.isSuccessful()) {
                                System.out.println(user.getName() + " successfully purchased " + quantity + " " + 
                                        selectedProduct.getName() + " for $" + order.getTotalPrice());
                            } else {
                                System.out.println(user.getName() + " payment failed: " + paymentResult.getMessage());
                            }
                        }
                    } catch (FlashSaleException e) {
                        System.out.println(user.getName() + " failed to order " + selectedProduct.getName() + ": " + e.getMessage());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all simulated users to complete
        latch.await();
        executor.shutdown();
        
        // Print sale statistics
        flashSaleSystem.printSaleStatistics(flashSale.getId());
    }
}

// FlashSaleSystem - Singleton
class FlashSaleSystem {
    private static final FlashSaleSystem INSTANCE = new FlashSaleSystem();
    
    private final Map<String, FlashSale> flashSales;
    private final Map<String, Order> orders;
    private final Map<String, AtomicInteger> productOrderCounts;
    private final RateLimiter rateLimiter;
    private final InventoryManager inventoryManager;
    private final OrderProcessor orderProcessor;
    private final PaymentProcessor paymentProcessor;
    private final FraudDetectionService fraudDetectionService;
    
    private FlashSaleSystem() {
        this.flashSales = new ConcurrentHashMap<>();
        this.orders = new ConcurrentHashMap<>();
        this.productOrderCounts = new ConcurrentHashMap<>();
        this.rateLimiter = new RateLimiter(100, Duration.ofSeconds(1)); // 100 requests per second
        this.inventoryManager = new InventoryManager();
        this.orderProcessor = new OrderProcessor();
        this.paymentProcessor = new PaymentProcessor();
        this.fraudDetectionService = new FraudDetectionService();
    }
    
    public static FlashSaleSystem getInstance() {
        return INSTANCE;
    }
    
    public void registerFlashSale(FlashSale flashSale) {
        flashSales.put(flashSale.getId(), flashSale);
        
        // Initialize inventory for each product in the flash sale
        for (FlashSaleProduct product : flashSale.getProducts().values()) {
            inventoryManager.initializeInventory(flashSale.getId(), product.getProduct().getId(), product.getQuantity());
            productOrderCounts.put(flashSale.getId() + "-" + product.getProduct().getId(), new AtomicInteger(0));
        }
        
        System.out.println("Flash sale " + flashSale.getName() + " registered with " + flashSale.getProducts().size() + " products");
    }
    
    public Order placeOrder(User user, String flashSaleId, String productId, int quantity) throws FlashSaleException {
        // Apply rate limiting
        if (!rateLimiter.allowRequest(user.getId())) {
            throw new FlashSaleException("Too many requests. Please try again later.");
        }
        
        // Check if flash sale exists and is active
        FlashSale flashSale = flashSales.get(flashSaleId);
        if (flashSale == null) {
            throw new FlashSaleException("Flash sale not found");
        }
        
        if (!flashSale.isActive()) {
            throw new FlashSaleException("Flash sale is not active");
        }
        
        // Check if product exists in the flash sale
        FlashSaleProduct flashSaleProduct = flashSale.getProducts().get(productId);
        if (flashSaleProduct == null) {
            throw new FlashSaleException("Product not available in this flash sale");
        }
        
        // Check for fraud
        if (fraudDetectionService.isFraudulent(user, flashSaleProduct.getProduct(), quantity)) {
            throw new FlashSaleException("Order flagged as potentially fraudulent");
        }
        
        // Check and update inventory
        boolean inventoryReserved = inventoryManager.reserveInventory(flashSaleId, productId, quantity);
        if (!inventoryReserved) {
            throw new FlashSaleException("Insufficient inventory");
        }
        
        // Create and process order
        Order order = orderProcessor.createOrder(user, flashSale, flashSaleProduct, quantity);
        orders.put(order.getId(), order);
        
        // Update product order count
        productOrderCounts.get(flashSaleId + "-" + productId).addAndGet(quantity);
        
        return order;
    }
    
    public PaymentResult processPayment(Order order, PaymentInfo paymentInfo) {
        // Process payment
        PaymentResult result = paymentProcessor.processPayment(order, paymentInfo);
        
        // If payment failed, release the inventory
        if (!result.isSuccessful()) {
            inventoryManager.releaseInventory(order.getFlashSaleId(), order.getProductId(), order.getQuantity());
            order.setStatus(OrderStatus.PAYMENT_FAILED);
        } else {
            order.setStatus(OrderStatus.PAID);
        }
        
        return result;
    }
    
    public void printSaleStatistics(String flashSaleId) {
        FlashSale flashSale = flashSales.get(flashSaleId);
        if (flashSale == null) {
            System.out.println("Flash sale not found");
            return;
        }
        
        System.out.println("\n===== Flash Sale Statistics =====");
        System.out.println("Flash Sale: " + flashSale.getName() + " (" + flashSaleId + ")");
        System.out.println("Time: " + flashSale.getStartTime() + " to " + flashSale.getEndTime());
        System.out.println("\nProduct Sales:");
        
        for (Map.Entry<String, FlashSaleProduct> entry : flashSale.getProducts().entrySet()) {
            String productId = entry.getKey();
            FlashSaleProduct product = entry.getValue();
            int sold = productOrderCounts.getOrDefault(flashSaleId + "-" + productId, new AtomicInteger(0)).get();
            int remaining = inventoryManager.getAvailableInventory(flashSaleId, productId);
            
            System.out.println(product.getProduct().getName() + ": Sold " + sold + 
                    " / " + product.getQuantity() + " (Remaining: " + remaining + ")");
        }
        
        // Count successful orders
        long successfulOrders = orders.values().stream()
                .filter(order -> order.getFlashSaleId().equals(flashSaleId) && order.getStatus() == OrderStatus.PAID)
                .count();
        
        System.out.println("\nTotal Successful Orders: " + successfulOrders);
    }
}

// Models
class Product {
    private final String id;
    private final String name;
    private final double price;
    private final int totalInventory;
    
    public Product(String id, String name, double price, int totalInventory) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.totalInventory = totalInventory;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public int getTotalInventory() {
        return totalInventory;
    }
}

class FlashSaleProduct {
    private final Product product;
    private final double discountedPrice;
    private final int quantity;
    
    public FlashSaleProduct(Product product, double discountedPrice, int quantity) {
        this.product = product;
        this.discountedPrice = discountedPrice;
        this.quantity = quantity;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public double getDiscountedPrice() {
        return discountedPrice;
    }
    
    public int getQuantity() {
        return quantity;
    }
}

class FlashSale {
    private final String id;
    private final String name;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Map<String, FlashSaleProduct> products;
    
    public FlashSale(String id, String name, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.products = new ConcurrentHashMap<>();
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public Map<String, FlashSaleProduct> getProducts() {
        return products;
    }
    
    public void addProduct(Product product, double discountedPrice, int quantity) {
        if (quantity > product.getTotalInventory()) {
            throw new IllegalArgumentException("Flash sale quantity cannot exceed product total inventory");
        }
        products.put(product.getId(), new FlashSaleProduct(product, discountedPrice, quantity));
    }
    
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }
}

class User {
    private final String id;
    private final String name;
    private final String email;
    
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
}

enum OrderStatus {
    CREATED, PAID, PAYMENT_FAILED, CANCELLED, SHIPPED, DELIVERED
}

class Order {
    private final String id;
    private final String flashSaleId;
    private final String productId;
    private final User user;
    private final int quantity;
    private final double unitPrice;
    private final double totalPrice;
    private final LocalDateTime createdAt;
    private OrderStatus status;
    
    public Order(String id, String flashSaleId, String productId, User user, int quantity, double unitPrice) {
        this.id = id;
        this.flashSaleId = flashSaleId;
        this.productId = productId;
        this.user = user;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice * quantity;
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.CREATED;
    }
    
    public String getId() {
        return id;
    }
    
    public String getFlashSaleId() {
        return flashSaleId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public User getUser() {
        return user;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}

class PaymentInfo {
    private final String method;
    private final String details;
    
    public PaymentInfo(String method, String details) {
        this.method = method;
        this.details = details;
    }
    
    public String getMethod() {
        return method;
    }
    
    public String getDetails() {
        return details;
    }
}

class PaymentResult {
    private final boolean successful;
    private final String transactionId;
    private final String message;
    
    public PaymentResult(boolean successful, String transactionId, String message) {
        this.successful = successful;
        this.transactionId = transactionId;
        this.message = message;
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getMessage() {
        return message;
    }
}

// Services
class InventoryManager {
    private final Map<String, AtomicInteger> inventory = new ConcurrentHashMap<>();
    private final Map<String, Lock> inventoryLocks = new ConcurrentHashMap<>();
    
    public void initializeInventory(String flashSaleId, String productId, int quantity) {
        String key = getInventoryKey(flashSaleId, productId);
        inventory.put(key, new AtomicInteger(quantity));
        inventoryLocks.put(key, new ReentrantLock());
    }
    
    public boolean reserveInventory(String flashSaleId, String productId, int quantity) {
        String key = getInventoryKey(flashSaleId, productId);
        Lock lock = inventoryLocks.get(key);
        
        if (lock == null) {
            return false; // Product not found
        }
        
        lock.lock();
        try {
            AtomicInteger available = inventory.get(key);
            if (available == null || available.get() < quantity) {
                return false; // Insufficient inventory
            }
            
            // Update inventory
            return available.addAndGet(-quantity) >= 0;
        } finally {
            lock.unlock();
        }
    }
    
    public void releaseInventory(String flashSaleId, String productId, int quantity) {
        String key = getInventoryKey(flashSaleId, productId);
        AtomicInteger available = inventory.get(key);
        if (available != null) {
            available.addAndGet(quantity);
        }
    }
    
    public int getAvailableInventory(String flashSaleId, String productId) {
        String key = getInventoryKey(flashSaleId, productId);
        AtomicInteger available = inventory.get(key);
        return available != null ? available.get() : 0;
    }
    
    private String getInventoryKey(String flashSaleId, String productId) {
        return flashSaleId + "-" + productId;
    }
}

class OrderProcessor {
    private final AtomicInteger orderIdCounter = new AtomicInteger(1);
    
    public Order createOrder(User user, FlashSale flashSale, FlashSaleProduct product, int quantity) {
        String orderId = "O" + String.format("%06d", orderIdCounter.getAndIncrement());
        return new Order(orderId, flashSale.getId(), product.getProduct().getId(), user, quantity, product.getDiscountedPrice());
    }
}

class PaymentProcessor {
    private final Random random = new Random();
    
    public PaymentResult processPayment(Order order, PaymentInfo paymentInfo) {
        // Simulate payment processing with 90% success rate
        boolean success = random.nextDouble() < 0.9;
        String transactionId = success ? "TXN" + System.currentTimeMillis() : "";
        String message = success ? "Payment successful" : "Payment failed";
        
        // Simulate processing time
        try {
            Thread.sleep(100); // 100ms payment processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new PaymentResult(false, "", "Payment processing interrupted");
        }
        
        return new PaymentResult(success, transactionId, message);
    }
}

class FraudDetectionService {
    private final Map<String, Integer> userOrderCounts = new ConcurrentHashMap<>();
    private final int maxOrdersPerUser = 5; // Maximum orders per user for the same product
    
    public boolean isFraudulent(User user, Product product, int quantity) {
        String key = user.getId() + "-" + product.getId();
        
        // Update and check order count for this user-product combination
        int count = userOrderCounts.compute(key, (k, v) -> (v == null) ? quantity : v + quantity);
        
        // If the user has ordered too many of the same product, flag as potential fraud
        return count > maxOrdersPerUser;
    }
}

class RateLimiter {
    private final int maxRequests;
    private final Duration window;
    private final Map<String, Queue<Long>> requestTimestamps = new ConcurrentHashMap<>();
    
    public RateLimiter(int maxRequests, Duration window) {
        this.maxRequests = maxRequests;
        this.window = window;
    }
    
    public boolean allowRequest(String userId) {
        long now = System.currentTimeMillis();
        long windowStartTime = now - window.toMillis();
        
        // Get or create queue of timestamps for this user
        Queue<Long> timestamps = requestTimestamps.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>());
        
        // Remove timestamps that are outside the current window
        while (!timestamps.isEmpty() && timestamps.peek() < windowStartTime) {
            timestamps.poll();
        }
        
        // Check if user has exceeded the rate limit
        if (timestamps.size() >= maxRequests) {
            return false;
        }
        
        // Add current timestamp and allow the request
        timestamps.add(now);
        return true;
    }
}

class FlashSaleException extends Exception {
    public FlashSaleException(String message) {
        super(message);
    }
}
