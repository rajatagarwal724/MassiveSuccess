package lld.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBusDemo {
    public static void main(String[] args) throws InterruptedException {
        // Create event bus instance
        EventBus eventBus = new EventBus("MainEventBus");
        
        // Create and register subscribers
        OrderService orderService = new OrderService();
        PaymentService paymentService = new PaymentService();
        NotificationService notificationService = new NotificationService();
        AnalyticsService analyticsService = new AnalyticsService();
        
        eventBus.register(orderService);
        eventBus.register(paymentService);
        eventBus.register(notificationService);
        eventBus.register(analyticsService);
        
        // Demonstrate event bus functionality
        System.out.println("===== Event Bus Demo =====\n");
        
        // Create and post an order created event
        System.out.println("Creating a new order...");
        Order order = new Order("ORD-12345", "user123", 99.99);
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(order);
        eventBus.post(orderCreatedEvent);
        
        // Allow time for async events to complete
        Thread.sleep(500);
        System.out.println("\n------------------------------\n");
        
        // Create and post a payment event
        System.out.println("Processing payment...");
        Payment payment = new Payment("PAY-789", "ORD-12345", 99.99, "CREDIT_CARD");
        PaymentSuccessEvent paymentEvent = new PaymentSuccessEvent(payment);
        eventBus.post(paymentEvent);
        
        // Allow time for async events to complete
        Thread.sleep(500);
        System.out.println("\n------------------------------\n");
        
        // Create and post an order fulfilled event
        System.out.println("Fulfilling order...");
        OrderFulfilledEvent fulfilledEvent = new OrderFulfilledEvent(order);
        eventBus.post(fulfilledEvent);
        
        // Allow time for async events to complete
        Thread.sleep(500);
        System.out.println("\n------------------------------\n");
        
        // Unregister a subscriber
        System.out.println("Unregistering analytics service...");
        eventBus.unregister(analyticsService);
        
        // Post an event after unregistering a subscriber
        System.out.println("\nGenerating monthly report...");
        MonthlyReportEvent reportEvent = new MonthlyReportEvent("REPORT-001", "MAY-2025");
        eventBus.post(reportEvent);
        
        // Allow time for async events to complete
        Thread.sleep(500);
        
        // Shutdown the event bus
        eventBus.shutdown();
        System.out.println("\nEvent Bus demo completed.");
    }
}

// Core EventBus Implementation
class EventBus {
    private static final Logger logger = Logger.getLogger(EventBus.class.getName());
    private final String name;
    private final ExecutorService executor;
    private final Map<Class<?>, Set<Subscriber>> subscribersByEventType;
    private final Map<Object, Set<Class<?>>> eventTypesBySubscriber;
    
    public EventBus() {
        this("default");
    }
    
    public EventBus(String name) {
        this.name = name;
        this.executor = Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r);
            thread.setName("EventBus-" + name + "-" + thread.getId());
            thread.setDaemon(true);
            return thread;
        });
        this.subscribersByEventType = new ConcurrentHashMap<>();
        this.eventTypesBySubscriber = new ConcurrentHashMap<>();
    }
    
    public void register(Object subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("Subscriber cannot be null");
        }
        
        synchronized (this) {
            Map<Class<?>, Set<Method>> eventHandlerMethods = findAllEventHandlerMethods(subscriber);
            
            if (eventHandlerMethods.isEmpty()) {
                logger.warning("No @Subscribe methods found for " + subscriber.getClass().getName());
                return;
            }
            
            for (Map.Entry<Class<?>, Set<Method>> entry : eventHandlerMethods.entrySet()) {
                Class<?> eventType = entry.getKey();
                Set<Method> methods = entry.getValue();
                
                Set<Subscriber> subscribers = subscribersByEventType.computeIfAbsent(
                    eventType, k -> new CopyOnWriteArraySet<>()
                );
                
                for (Method method : methods) {
                    Subscribe subscribeAnnotation = method.getAnnotation(Subscribe.class);
                    DeliveryMode deliveryMode = subscribeAnnotation.deliveryMode();
                    subscribers.add(new Subscriber(subscriber, method, deliveryMode));
                }
                
                Set<Class<?>> eventTypes = eventTypesBySubscriber.computeIfAbsent(
                    subscriber, k -> new HashSet<>()
                );
                eventTypes.add(eventType);
            }
        }
        
        logger.info("Registered subscriber: " + subscriber.getClass().getSimpleName());
    }
    
    public void unregister(Object subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("Subscriber cannot be null");
        }
        
        synchronized (this) {
            Set<Class<?>> eventTypes = eventTypesBySubscriber.remove(subscriber);
            if (eventTypes == null) {
                logger.warning("Subscriber not registered: " + subscriber.getClass().getName());
                return;
            }
            
            for (Class<?> eventType : eventTypes) {
                Set<Subscriber> subscribers = subscribersByEventType.get(eventType);
                if (subscribers != null) {
                    subscribers.removeIf(s -> s.target == subscriber);
                    if (subscribers.isEmpty()) {
                        subscribersByEventType.remove(eventType);
                    }
                }
            }
        }
        
        logger.info("Unregistered subscriber: " + subscriber.getClass().getSimpleName());
    }
    
    public void post(Object event) {
        if (event == null) {
            throw new NullPointerException("Event cannot be null");
        }
        
        Class<?> eventType = event.getClass();
        Set<Subscriber> subscribers = getSubscribers(eventType);
        
        if (subscribers.isEmpty()) {
            logger.fine("No subscribers for event: " + eventType.getSimpleName());
            return;
        }
        
        for (Subscriber subscriber : subscribers) {
            dispatch(event, subscriber);
        }
    }
    
    private Set<Subscriber> getSubscribers(Class<?> eventType) {
        Set<Subscriber> result = new HashSet<>();
        Set<Class<?>> eventTypes = new HashSet<>();
        
        eventTypes.add(eventType);
        
        // Add all parent classes and interfaces
        Class<?> superClass = eventType.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            eventTypes.add(superClass);
            superClass = superClass.getSuperclass();
        }
        
        for (Class<?> type : eventTypes) {
            Set<Subscriber> subscribers = subscribersByEventType.get(type);
            if (subscribers != null) {
                result.addAll(subscribers);
            }
        }
        
        return result;
    }
    
    private void dispatch(Object event, Subscriber subscriber) {
        switch (subscriber.deliveryMode) {
            case SYNC:
                dispatchSync(event, subscriber);
                break;
            case ASYNC:
                dispatchAsync(event, subscriber);
                break;
            case BACKGROUND:
                dispatchBackground(event, subscriber);
                break;
        }
    }
    
    private void dispatchSync(Object event, Subscriber subscriber) {
        try {
            subscriber.method.invoke(subscriber.target, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logSubscriberException(subscriber, event, e);
        }
    }
    
    private void dispatchAsync(Object event, Subscriber subscriber) {
        executor.submit(() -> {
            try {
                subscriber.method.invoke(subscriber.target, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logSubscriberException(subscriber, event, e);
            }
        });
    }
    
    private void dispatchBackground(Object event, Subscriber subscriber) {
        CompletableFuture.runAsync(() -> {
            try {
                subscriber.method.invoke(subscriber.target, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logSubscriberException(subscriber, event, e);
            }
        }, executor);
    }
    
    private void logSubscriberException(Subscriber subscriber, Object event, Exception e) {
        logger.log(Level.SEVERE, "Exception handling event " + event.getClass().getSimpleName() +
                  " in subscriber " + subscriber.target.getClass().getSimpleName() +
                  " method " + subscriber.method.getName(), e);
    }
    
    private Map<Class<?>, Set<Method>> findAllEventHandlerMethods(Object subscriber) {
        Map<Class<?>, Set<Method>> eventHandlerMethods = new HashMap<>();
        Class<?> subscriberClass = subscriber.getClass();
        
        for (Method method : subscriberClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    logger.warning("@Subscribe method " + method.getName() +
                                 " has wrong number of parameters: " + parameterTypes.length);
                    continue;
                }
                
                Class<?> eventType = parameterTypes[0];
                method.setAccessible(true);
                
                Set<Method> methods = eventHandlerMethods.computeIfAbsent(
                    eventType, k -> new HashSet<>()
                );
                methods.add(method);
            }
        }
        
        return eventHandlerMethods;
    }
    
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("EventBus shutdown completed");
    }
}

class Subscriber {
    final Object target;
    final Method method;
    final DeliveryMode deliveryMode;
    
    Subscriber(Object target, Method method, DeliveryMode deliveryMode) {
        this.target = target;
        this.method = method;
        this.deliveryMode = deliveryMode;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscriber that = (Subscriber) o;
        return Objects.equals(target, that.target) && 
               Objects.equals(method, that.method);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(target, method);
    }
}

// Subscribe annotation for marking event handler methods
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Subscribe {
    DeliveryMode deliveryMode() default DeliveryMode.SYNC;
}

// Event delivery modes
enum DeliveryMode {
    // Synchronous delivery in the same thread
    SYNC,
    
    // Asynchronous delivery in a separate thread
    ASYNC,
    
    // Background delivery with CompletableFuture
    BACKGROUND
}

// Event base interface
interface Event {
    String getEventId();
    long getTimestamp();
}

// Base implementation for events
abstract class BaseEvent implements Event {
    private final String eventId;
    private final long timestamp;
    
    public BaseEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
    
    @Override
    public String getEventId() {
        return eventId;
    }
    
    @Override
    public long getTimestamp() {
        return timestamp;
    }
}

// Domain classes and events
class Order {
    private final String orderId;
    private final String userId;
    private final double amount;
    private String status;
    
    public Order(String orderId, String userId, double amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = "CREATED";
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}

class Payment {
    private final String paymentId;
    private final String orderId;
    private final double amount;
    private final String method;
    private String status;
    
    public Payment(String paymentId, String orderId, double amount, String method) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = "COMPLETED";
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public String getMethod() {
        return method;
    }
    
    public String getStatus() {
        return status;
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

// Event classes
class OrderCreatedEvent extends BaseEvent {
    private final Order order;
    
    public OrderCreatedEvent(Order order) {
        this.order = order;
    }
    
    public Order getOrder() {
        return order;
    }
}

class OrderFulfilledEvent extends BaseEvent {
    private final Order order;
    
    public OrderFulfilledEvent(Order order) {
        this.order = order;
        order.setStatus("FULFILLED");
    }
    
    public Order getOrder() {
        return order;
    }
}

class PaymentSuccessEvent extends BaseEvent {
    private final Payment payment;
    
    public PaymentSuccessEvent(Payment payment) {
        this.payment = payment;
    }
    
    public Payment getPayment() {
        return payment;
    }
}

class MonthlyReportEvent extends BaseEvent {
    private final String reportId;
    private final String month;
    
    public MonthlyReportEvent(String reportId, String month) {
        this.reportId = reportId;
        this.month = month;
    }
    
    public String getReportId() {
        return reportId;
    }
    
    public String getMonth() {
        return month;
    }
}

// Example services that use the event bus
class OrderService {
    @Subscribe
    public void onOrderCreated(OrderCreatedEvent event) {
        System.out.println("[OrderService] Processing new order: " + event.getOrder().getOrderId());
    }
    
    @Subscribe(deliveryMode = DeliveryMode.ASYNC)
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        System.out.println("[OrderService] Updating order status for payment: " + 
                          event.getPayment().getOrderId());
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[OrderService] Order status updated to PAID");
    }
    
    @Subscribe
    public void onOrderFulfilled(OrderFulfilledEvent event) {
        System.out.println("[OrderService] Order fulfilled: " + event.getOrder().getOrderId() + 
                          " with status: " + event.getOrder().getStatus());
    }
}

class PaymentService {
    @Subscribe(deliveryMode = DeliveryMode.ASYNC)
    public void onOrderCreated(OrderCreatedEvent event) {
        System.out.println("[PaymentService] Preparing payment for order: " + 
                          event.getOrder().getOrderId());
        // Simulate processing time
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[PaymentService] Payment ready for processing");
    }
    
    @Subscribe
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        System.out.println("[PaymentService] Payment processed successfully: " + 
                          event.getPayment().getPaymentId());
    }
}

class NotificationService {
    @Subscribe(deliveryMode = DeliveryMode.BACKGROUND)
    public void onOrderCreated(OrderCreatedEvent event) {
        System.out.println("[NotificationService] Sending order confirmation email for: " + 
                          event.getOrder().getOrderId());
        // Simulate sending an email
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[NotificationService] Order confirmation email sent");
    }
    
    @Subscribe(deliveryMode = DeliveryMode.BACKGROUND)
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        System.out.println("[NotificationService] Sending payment receipt for: " + 
                          event.getPayment().getPaymentId());
        // Simulate sending an email
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[NotificationService] Payment receipt sent");
    }
    
    @Subscribe(deliveryMode = DeliveryMode.ASYNC)
    public void onOrderFulfilled(OrderFulfilledEvent event) {
        System.out.println("[NotificationService] Sending shipment notification for: " + 
                          event.getOrder().getOrderId());
        // Simulate sending an email
        try {
            Thread.sleep(120);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[NotificationService] Shipment notification sent");
    }
    
    @Subscribe
    public void onMonthlyReport(MonthlyReportEvent event) {
        System.out.println("[NotificationService] Sending monthly report: " + 
                          event.getReportId() + " for month: " + event.getMonth());
    }
}

class AnalyticsService {
    @Subscribe(deliveryMode = DeliveryMode.BACKGROUND)
    public void onOrderCreated(OrderCreatedEvent event) {
        System.out.println("[AnalyticsService] Recording order metrics for: " + 
                          event.getOrder().getOrderId());
    }
    
    @Subscribe(deliveryMode = DeliveryMode.BACKGROUND)
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        System.out.println("[AnalyticsService] Recording payment metrics for: " + 
                          event.getPayment().getPaymentId());
    }
    
    @Subscribe(deliveryMode = DeliveryMode.BACKGROUND)
    public void onOrderFulfilled(OrderFulfilledEvent event) {
        System.out.println("[AnalyticsService] Recording fulfillment metrics for: " + 
                          event.getOrder().getOrderId());
    }
    
    @Subscribe
    public void onMonthlyReport(MonthlyReportEvent event) {
        System.out.println("[AnalyticsService] Generating analytics for monthly report: " + 
                          event.getReportId());
    }
}
