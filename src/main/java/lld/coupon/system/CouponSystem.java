package lld.coupon.system;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class CouponSystem {

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
        Order newUserOrder = new Order(
                1L,
                newUser,
                Arrays.asList(new OrderItem(book, 1), new OrderItem(furniture, 1)),
                PaymentType.DEBIT_CARD
        );
        Order premiumUserOrder = new Order(
                2L,
                premiumUser,
                Arrays.asList(new OrderItem(laptop, 1), new OrderItem(book, 1)),
                PaymentType.CREDIT_CARD
        );

        // Get recommendations
        System.out.println("Coupons for new user ordering books and furniture with debit card:");
        List<Coupon> newUserRecommendations = recommendationSystem.getAvailableRecommendedCoupons(newUserOrder);
        newUserRecommendations.forEach(coupon ->
                System.out.println(coupon.getCode() + ": " + coupon.getDescription()));

        System.out.println("\nCoupons for premium user ordering electronics and books with credit card:");
        List<Coupon> premiumUserRecommendations = recommendationSystem.getAvailableRecommendedCoupons(premiumUserOrder);
        premiumUserRecommendations.forEach(coupon ->
                System.out.println(coupon.getCode() + ": " + coupon.getDescription()));

        // Demonstrate concurrent access
        System.out.println("\nDemonstrating concurrent coupon recommendations:");
        demonstrateConcurrentAccess(recommendationSystem, newUserOrder, premiumUserOrder);

    }

    public static void demonstrateConcurrentAccess(
            final CouponRecommendationSystem recommendationSystem,
            final Order newUserOrder,
            final Order premiumUserOrder
    ) {
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        final CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            executorService.submit(
                    () -> {
                        try {
                            final Order order = finalI % 2 == 0 ? newUserOrder : premiumUserOrder;
                            var coupons = recommendationSystem.getAvailableRecommendedCoupons(order);
                            System.out.println("Thread " + finalI + " received " +
                                    coupons.size() + " coupon recommendations");
                        } finally {
                            latch.countDown();
                        }

                    }
            );
        }

        try {
            latch.await();
            executorService.shutdown();
            System.out.println("All threads completed successfully");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

enum UserType {
    NEW, REGULAR, PREMIUM
}

enum PaymentType {
    CREDIT_CARD, CASH, UPI, DEBIT_CARD
}

enum ProductType {
    ELECTRONICS, GROCERY, SPORTS, BOOKS, FURNITURE
}

record User(String id, UserType userType, LocalDateTime registrationDate) {
    public User(String id, UserType userType) {
        this(id, userType, LocalDateTime.now());
    }
}

record Product(String name, double price, ProductType productType) {}

record OrderItem(Product product, Integer quantity) {
}

@Data
@AllArgsConstructor
class Order {
    private final Long orderId;
    private final User user;
    private final List<OrderItem> orderItems;
    private final PaymentType paymentType;

    public double getTotalAmount() {
        return this
                .getOrderItems()
                .stream()
                .mapToDouble(orderItem -> (orderItem.product().price() * orderItem.quantity()))
                .sum();
    }
}

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
        return order.getUser().userType() == requiredUserType;
    }
}

class PaymentTypeRule implements Rule {
    private final PaymentType requiredPaymentType;

    PaymentTypeRule(PaymentType requiredPaymentType) {
        this.requiredPaymentType = requiredPaymentType;
    }

    @Override
    public boolean evaluate(Order order) {
        return requiredPaymentType.equals(order.getPaymentType());
    }
}

class ProductTypeRule implements Rule {
    private final ProductType requiredProductType;

    public ProductTypeRule(ProductType requiredProductType) {
        this.requiredProductType = requiredProductType;
    }

    @Override
    public boolean evaluate(Order order) {
        return order
                .getOrderItems()
                .stream()
                .anyMatch(orderItem -> requiredProductType.equals(orderItem.product().productType()));
    }
}

class OrderAmountRule implements Rule {
    private final double minimumAmount;

    OrderAmountRule(double minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    @Override
    public boolean evaluate(Order order) {
        double totalSum = order
                .getOrderItems()
                .stream()
                .mapToDouble(orderItem -> (orderItem.product().price() * orderItem.quantity()))
                .sum();
        return totalSum >= minimumAmount;
    }
}

@Data
class Coupon {
    private final String code;
    private final String description;
    private final double discount;
    private List<Rule> rules;

    public Coupon(String code, String description, double discount) {
        this.code = code;
        this.description = description;
        this.discount = discount;
        this.rules = new ArrayList<>();
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    public void removeRule(Rule rule) {
        this.rules.remove(rule);
    }

    public boolean isApplicable(Order order) {
        return rules.stream().allMatch(rule -> rule.evaluate(order));
    }
}

class CouponRecommendationSystem {
    private final List<Coupon> availableCoupons;
    private final ReadWriteLock readWriteLock;
    private final Map<String, List<Coupon>> cachedRecommendedCoupons;

    public CouponRecommendationSystem() {
        this.availableCoupons = new CopyOnWriteArrayList<>();
        this.readWriteLock = new ReentrantReadWriteLock();
        this.cachedRecommendedCoupons = new ConcurrentHashMap<>();
    }

    public void addCoupon(Coupon coupon) {
        var lock = readWriteLock.writeLock();
        lock.lock();
        try {
            this.availableCoupons.add(coupon);
            cachedRecommendedCoupons.clear();
        } finally {
            lock.unlock();
        }
    }

    public void removeCoupon(Coupon coupon) {
        var lock = readWriteLock.writeLock();
        lock.lock();
        try {
            this.availableCoupons.remove(coupon);
            this.cachedRecommendedCoupons.clear();
        } finally {
            lock.unlock();
        }
    }

    public List<Coupon> getAvailableRecommendedCoupons(Order order) {
        final String cacheKey = generateCacheKey(order);
        if (cachedRecommendedCoupons.containsKey(cacheKey)) {
            return cachedRecommendedCoupons.get(cacheKey);
        }

        var lock = readWriteLock.readLock();
        lock.lock();
        try {
            final List<Coupon> recommendedCoupons = this.availableCoupons
                    .parallelStream()
                    .filter(coupon -> coupon.isApplicable(order))
                    .toList();
            cachedRecommendedCoupons.put(cacheKey, recommendedCoupons);
        } finally {
            lock.unlock();
        }
        return cachedRecommendedCoupons.get(cacheKey);
    }

    private String generateCacheKey(Order order) {
        var cacheKeyBuilder = new StringBuilder();
        cacheKeyBuilder
                .append(order.getUser().userType())
                .append(":")
                .append(order.getPaymentType())
                .append(":")
                .append(order.getTotalAmount());

        order
                .getOrderItems()
                .stream()
                .map(orderItem -> orderItem.product().productType())
                .distinct()
                .sorted(Enum::compareTo)
                .forEach(productType -> cacheKeyBuilder.append(":").append(productType));

        return cacheKeyBuilder.toString();
    }
}



