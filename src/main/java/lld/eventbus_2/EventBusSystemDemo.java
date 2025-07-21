package lld.eventbus_2;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

// Event class to represent different types of events
class Event {
    private String type;
    private Object data;
    private long timestamp;

    public Event(String type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public String getType() { return type; }
    public Object getData() { return data; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("Event{type='%s', data=%s, timestamp=%d}", type, data, timestamp);
    }
}

// Subscriber interface for event handlers
interface Subscriber {
    void handleEvent(Event event);
}

// EventBus class implementing the core functionality
class EventBus {
    private final Map<String, List<Subscriber>> subscribers;
    private final ExecutorService executorService;
    private final BlockingQueue<Event> eventQueue;
    private volatile boolean isRunning;

    public EventBus() {
        this.subscribers = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(5);
        this.eventQueue = new LinkedBlockingQueue<>();
        this.isRunning = true;
        startEventProcessor();
    }

    private void startEventProcessor() {
        executorService.submit(() -> {
            while (isRunning) {
                try {
                    Event event = eventQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (event != null) {
                        processEvent(event);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void subscribe(String eventType, Subscriber subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                  .add(subscriber);
        System.out.printf("Subscriber added for event type: %s%n", eventType);
    }

    public void unsubscribe(String eventType, Subscriber subscriber) {
        List<Subscriber> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers != null) {
            eventSubscribers.remove(subscriber);
            System.out.printf("Subscriber removed from event type: %s%n", eventType);
        }
    }

    public void publish(Event event) {
        try {
            eventQueue.put(event);
            System.out.printf("Event published: %s%n", event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error publishing event: " + e.getMessage());
        }
    }

    private void processEvent(Event event) {
        List<Subscriber> eventSubscribers = subscribers.get(event.getType());
        if (eventSubscribers != null) {
            for (Subscriber subscriber : eventSubscribers) {
                executorService.submit(() -> {
                    try {
                        subscriber.handleEvent(event);
                    } catch (Exception e) {
                        System.err.printf("Error processing event by subscriber: %s%n", e.getMessage());
                    }
                });
            }
        }
    }

    public void shutdown() {
        isRunning = false;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

// Example Subscriber implementations
class LoggingSubscriber implements Subscriber {
    private String name;

    public LoggingSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void handleEvent(Event event) {
        System.out.printf("[%s] Received event: %s%n", name, event);
    }
}

class DataProcessingSubscriber implements Subscriber {
    @Override
    public void handleEvent(Event event) {
        if (event.getData() instanceof String) {
            String data = (String) event.getData();
            System.out.printf("Processing data: %s%n", data.toUpperCase());
        }
    }
}

public class EventBusSystemDemo {
    public static void main(String[] args) {
        // Create EventBus instance
        EventBus eventBus = new EventBus();

        // Create subscribers
        Subscriber logger1 = new LoggingSubscriber("Logger1");
        Subscriber logger2 = new LoggingSubscriber("Logger2");
        Subscriber dataProcessor = new DataProcessingSubscriber();

        // Subscribe to events
        eventBus.subscribe("LOG", logger1);
        eventBus.subscribe("LOG", logger2);
        eventBus.subscribe("DATA", dataProcessor);

        // Publish some events
        eventBus.publish(new Event("LOG", "System started"));
        eventBus.publish(new Event("DATA", "Hello, World!"));
        eventBus.publish(new Event("LOG", "Processing data"));

        // Wait for events to be processed
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Unsubscribe one logger
        eventBus.unsubscribe("LOG", logger1);

        // Publish more events
        eventBus.publish(new Event("LOG", "Logger1 unsubscribed"));
        eventBus.publish(new Event("DATA", "Another data point"));

        // Wait for events to be processed
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Shutdown the event bus
        eventBus.shutdown();
    }
}
