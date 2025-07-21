package lld.coupon.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class CouponRecommendationSystemDemo {
    public static void main(String[] args) {
        // Initialize the recommendation system
        CouponRecommendationSystem recommendationSystem = new CouponRecommendationSystem();
        
        // Create some example coupons with different rules
        Coupon newUserCoupon = new Coupon("NEWUSER50", "50% off for new users", 50.0);
        newUserCoupon.addRule(new UserTypeRule(UserType.NEW));
        
        Coupon creditCardCoupon = new Coupon("CREDIT10", "10% off on credit card payments", 10.0);
        creditCardCoupon.addRule(new PaymentTypeRule(PaymentType.CREDIT_CARD));
        
        Coupon premiumElectronicsCoupon = new Coupon("PREMIUM20", "20% off on electronics for premium users", 20.0);
        premiumElectronicsCoupon.addRule(new UserTypeRule(UserType.PREMIUM));
        premiumElectronicsCoupon.addRule(new ProductTypeRule(ProductType.ELECTRONICS));
        
        Coupon highValueOrderCoupon = new Coupon("BIGORDER15", "15% off on orders above $1000", 15.0);
        highValueOrderCoupon.addRule(order -> order.getTotalAmount() > 1000);
        
        // Add coupons to the recommendation system
        recommendationSystem.addCoupon(newUserCoupon);
        recommendationSystem.addCoupon(creditCardCoupon);
        recommendationSystem.addCoupon(premiumElectronicsCoupon);
        recommendationSystem.addCoupon(highValueOrderCoupon);
        
        // Create some users
        User newUser = new User("user1", UserType.NEW);
        User premiumUser = new User("user2", UserType.PREMIUM);
        
        // Create some products
        Product laptop = new Product("Laptop", 1200.0, ProductType.ELECTRONICS);
        Product book = new Product("Book", 25.0, ProductType.BOOKS);
        Product furniture = new Product("Chair", 150.0, ProductType.FURNITURE);
        
        // Create orders
        Order newUserOrder = new Order(1L, newUser, Arrays.asList(book, furniture), PaymentType.DEBIT_CARD);
        Order premiumUserOrder = new Order(2L, premiumUser, Arrays.asList(laptop, book), PaymentType.CREDIT_CARD);
        
        // Get recommendations
        System.out.println("Coupons for new user ordering books and furniture with debit card:");
        List<Coupon> newUserRecommendations = recommendationSystem.getRecommendedCoupons(newUserOrder);
        newUserRecommendations.forEach(coupon -> 
            System.out.println(coupon.getCode() + ": " + coupon.getDescription()));
        
        System.out.println("\nCoupons for premium user ordering electronics and books with credit card:");
        List<Coupon> premiumUserRecommendations = recommendationSystem.getRecommendedCoupons(premiumUserOrder);
        premiumUserRecommendations.forEach(coupon -> 
            System.out.println(coupon.getCode() + ": " + coupon.getDescription()));
        
        // Demonstrate concurrent access
        System.out.println("\nDemonstrating concurrent coupon recommendations:");
        demonstrateConcurrentAccess(recommendationSystem, newUserOrder, premiumUserOrder);
    }
    
    private static void demonstrateConcurrentAccess(
            CouponRecommendationSystem recommendationSystem, 
            Order order1, 
            Order order2) {
        int numThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            executorService.submit(() -> {
                try {
                    Order orderToUse = threadNum % 2 == 0 ? order1 : order2;
                    List<Coupon> recommendations = recommendationSystem.getRecommendedCoupons(orderToUse);
                    System.out.println("Thread " + threadNum + " received " + 
                                      recommendations.size() + " coupon recommendations");
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
            executorService.shutdown();
            System.out.println("All threads completed successfully");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted: " + e.getMessage());
        }
    }
}

// ----- Domain Models -----

enum UserType {
    NEW, REGULAR, PREMIUM
}

enum ProductType {
    ELECTRONICS, CLOTHING, BOOKS, FURNITURE, GROCERIES
}

enum PaymentType {
    CREDIT_CARD, DEBIT_CARD, WALLET, UPI, CASH_ON_DELIVERY
}

@Data
@AllArgsConstructor
class User {
    private String userId;
    private UserType userType;
    private LocalDateTime registrationDate;

    public User(String userId, UserType userType) {
        this.userId = userId;
        this.userType = userType;
        this.registrationDate = LocalDateTime.now();
    }
}

@Data
@AllArgsConstructor
class Product {
    private String name;
    private double price;
    private ProductType productType;
}

@Data
class Order {
    private Long orderId;
    private User user;
    private List<Product> products;
    private PaymentType paymentType;
    private double totalAmount;
    
    public Order(Long orderId, User user, List<Product> products, PaymentType paymentType) {
        this.orderId = orderId;
        this.user = user;
        this.products = new ArrayList<>(products);
        this.paymentType = paymentType;
        this.totalAmount = products.stream().mapToDouble(Product::getPrice).sum();
    }
    
    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }
    
    public boolean containsProductType(ProductType productType) {
        return products.stream().anyMatch(product -> product.getProductType() == productType);
    }
}

// ----- Rule Engine -----

interface Rule {
    boolean evaluate(Order order);
}

class UserTypeRule implements Rule {
    private final UserType requiredUserType;
    
    public UserTypeRule(UserType requiredUserType) {
        this.requiredUserType = requiredUserType;
    }
    
    @Override
    public boolean evaluate(Order order) {
        return order.getUser().getUserType() == requiredUserType;
    }
}

class PaymentTypeRule implements Rule {
    private final PaymentType requiredPaymentType;
    
    public PaymentTypeRule(PaymentType requiredPaymentType) {
        this.requiredPaymentType = requiredPaymentType;
    }
    
    @Override
    public boolean evaluate(Order order) {
        return order.getPaymentType() == requiredPaymentType;
    }
}

class ProductTypeRule implements Rule {
    private final ProductType requiredProductType;
    
    public ProductTypeRule(ProductType requiredProductType) {
        this.requiredProductType = requiredProductType;
    }
    
    @Override
    public boolean evaluate(Order order) {
        return order.containsProductType(requiredProductType);
    }
}

class OrderAmountRule implements Rule {
    private final double minimumAmount;
    
    public OrderAmountRule(double minimumAmount) {
        this.minimumAmount = minimumAmount;
    }
    
    @Override
    public boolean evaluate(Order order) {
        return order.getTotalAmount() >= minimumAmount;
    }
}

// ----- Coupon and Recommendation System -----

@Data
class Coupon {
    private String code;
    private String description;
    private double discountPercentage;
    private List<Rule> rules;
    
    public Coupon(String code, String description, double discountPercentage) {
        this.code = code;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.rules = new ArrayList<>();
    }
    
    public void addRule(Rule rule) {
        rules.add(rule);
    }
    
    public boolean isApplicable(Order order) {
        return rules.stream().allMatch(rule -> rule.evaluate(order));
    }
}

class CouponRecommendationSystem {
    private final List<Coupon> availableCoupons;
    private final ReadWriteLock lock;
    private final Map<String, List<Coupon>> cachedRecommendations;
    private final ExecutorService executorService;
    
    public CouponRecommendationSystem() {
        this.availableCoupons = new CopyOnWriteArrayList<>();
        this.lock = new ReentrantReadWriteLock();
        this.cachedRecommendations = new ConcurrentHashMap<>();
        this.executorService = Executors.newWorkStealingPool();
    }
    
    public void addCoupon(Coupon coupon) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            availableCoupons.add(coupon);
            // Clear cache when new coupons are added
            cachedRecommendations.clear();
        } finally {
            writeLock.unlock();
        }
    }
    
    public void removeCoupon(Coupon coupon) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            availableCoupons.remove(coupon);
            // Clear cache when coupons are removed
            cachedRecommendations.clear();
        } finally {
            writeLock.unlock();
        }
    }
    
    public List<Coupon> getRecommendedCoupons(Order order) {
        // Generate a unique key for the order to use in cache
        String cacheKey = generateCacheKey(order);
        
        // Check if recommendations for this order are already cached
        if (cachedRecommendations.containsKey(cacheKey)) {
            return cachedRecommendations.get(cacheKey);
        }
        
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            // Use parallel stream to evaluate rules concurrently
            List<Coupon> recommendations = availableCoupons.parallelStream()
                    .filter(coupon -> coupon.isApplicable(order))
                    .collect(Collectors.toList());
            
            // Cache the results
            cachedRecommendations.put(cacheKey, recommendations);
            
            return recommendations;
        } finally {
            readLock.unlock();
        }
    }
    
    // For complex rule evaluations that may take longer
    public CompletableFuture<List<Coupon>> getRecommendedCouponsAsync(Order order) {
        return CompletableFuture.supplyAsync(() -> getRecommendedCoupons(order), executorService);
    }
    
    private String generateCacheKey(Order order) {
        StringBuilder key = new StringBuilder();
        key.append(order.getUser().getUserType())
           .append(":")
           .append(order.getPaymentType())
           .append(":")
           .append(order.getTotalAmount());
        
        // Add product types to the key
        order.getProducts().stream()
              .map(Product::getProductType)
              .distinct()
              .sorted(Enum::compareTo)
              .forEach(type -> key.append(":").append(type));
        
        return key.toString();
    }
    
    // Shutdown the executor service when the system is no longer needed
    public void shutdown() {
        executorService.shutdown();
    }
}
