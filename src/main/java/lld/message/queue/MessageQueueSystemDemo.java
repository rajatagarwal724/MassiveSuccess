package lld.message.queue;

import lombok.Data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageQueueSystemDemo {
    private static final Logger logger = Logger.getLogger(MessageQueueSystemDemo.class.getName());
    
    public static void main(String[] args) throws InterruptedException {
        // Create the message broker
        MessageBroker broker = new MessageBrokerImpl();
        
        // Register topics
        broker.createTopic("orders");
        broker.createTopic("payments");
        broker.createTopic("notifications");
        
        // Create producers
        MessageProducer orderProducer = broker.createProducer("orders");
        MessageProducer paymentProducer = broker.createProducer("payments");
        MessageProducer notificationProducer = broker.createProducer("notifications");
        
        // Create consumer groups
        broker.createConsumerGroup("orders", "order-processors");
        broker.createConsumerGroup("orders", "order-analytics");
        broker.createConsumerGroup("payments", "payment-processors");
        broker.createConsumerGroup("notifications", "email-senders");
        
        // Create and register consumers
        MessageConsumer orderConsumer1 = broker.createConsumer("orders", "order-processors", "consumer-1");
        MessageConsumer orderConsumer2 = broker.createConsumer("orders", "order-processors", "consumer-2");
        MessageConsumer orderAnalyticsConsumer = broker.createConsumer("orders", "order-analytics", "analytics-1");
        MessageConsumer paymentConsumer = broker.createConsumer("payments", "payment-processors", "payment-1");
        MessageConsumer notificationConsumer = broker.createConsumer("notifications", "email-senders", "email-1");
        
        // Set up message handlers
        orderConsumer1.setMessageHandler(message -> {
            System.out.println("[Order Consumer 1] Processing order: " + message.getContent());
            simulateProcessing(100);
            return MessageResult.SUCCESS;
        });
        
        orderConsumer2.setMessageHandler(message -> {
            System.out.println("[Order Consumer 2] Processing order: " + message.getContent());
            simulateProcessing(150);
            return MessageResult.SUCCESS;
        });
        
        orderAnalyticsConsumer.setMessageHandler(message -> {
            System.out.println("[Order Analytics] Recording metrics for order: " + message.getContent());
            simulateProcessing(50);
            return MessageResult.SUCCESS;
        });
        
        paymentConsumer.setMessageHandler(message -> {
            System.out.println("[Payment Processor] Processing payment: " + message.getContent());
            simulateProcessing(200);
            // Simulate occasional failures
            if (Math.random() < 0.3) {
                System.out.println("[Payment Processor] Failed to process payment: " + message.getContent());
                return MessageResult.FAILED;
            }
            return MessageResult.SUCCESS;
        });
        
        notificationConsumer.setMessageHandler(message -> {
            System.out.println("[Email Sender] Sending email notification: " + message.getContent());
            simulateProcessing(80);
            return MessageResult.SUCCESS;
        });
        
        // Start consumers
        orderConsumer1.startConsuming();
        orderConsumer2.startConsuming();
        orderAnalyticsConsumer.startConsuming();
        paymentConsumer.startConsuming();
        notificationConsumer.startConsuming();
        
        System.out.println("===== Message Queue System Demo =====\n");
        
        // Simulate incoming orders
        System.out.println("Producing order messages...");
        for (int i = 1; i <= 10; i++) {
            String orderId = "ORD-" + i;
            double amount = 10.0 * i;
            
            Message orderMsg = new Message(
                UUID.randomUUID().toString(),
                "{\"orderId\":\"" + orderId + "\", \"amount\":" + amount + ", \"userId\":\"user-" + i + "\"}")
                .addHeader("type", "order_created");
            
            orderProducer.send(orderMsg);
            
            // Some orders generate payments
            if (i % 2 == 0) {
                Thread.sleep(50);  // Small delay
                Message paymentMsg = new Message(
                    UUID.randomUUID().toString(),
                    "{\"paymentId\":\"PAY-" + i + "\", \"orderId\":\"" + orderId + "\", \"amount\":" + amount + "}")
                    .addHeader("type", "payment_processed");
                
                paymentProducer.send(paymentMsg);
                
                // Successful payments generate notifications
                Thread.sleep(100);  // Small delay
                Message notificationMsg = new Message(
                    UUID.randomUUID().toString(),
                    "{\"type\":\"email\", \"to\":\"user-" + i + "@example.com\", \"subject\":\"Order Confirmed\"}")
                    .addHeader("type", "email_notification");
                
                notificationProducer.send(notificationMsg);
            }
            
            Thread.sleep(200);  // Delay between orders
        }
        
        // Let consumers process the messages
        System.out.println("\nWaiting for message processing to complete...\n");
        Thread.sleep(5000);
        
        // Display broker stats
        System.out.println("\n===== Message Broker Statistics =====");
        broker.getTopics().forEach(topic -> {
            System.out.println("\nTopic: " + topic);
            System.out.println("  Messages produced: " + broker.getMessageCount(topic));
            System.out.println("  Consumer groups: " + broker.getConsumerGroups(topic));
            
            broker.getConsumerGroups(topic).forEach(group -> {
                System.out.println("  Consumer group '" + group + "' lag: " + broker.getConsumerGroupLag(topic, group));
            });
        });
        
        // Shutdown everything
        System.out.println("\nShutting down consumers...");
        orderConsumer1.stopConsuming();
        orderConsumer2.stopConsuming();
        orderAnalyticsConsumer.stopConsuming();
        paymentConsumer.stopConsuming();
        notificationConsumer.stopConsuming();
        
        broker.shutdown();
        System.out.println("\nMessage Queue System demo completed.");
    }
    
    private static void simulateProcessing(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// ==================== Core Interfaces ====================

// Message interface and implementation
@Data
class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String id;
    private final String content;
    private final Map<String, String> headers;
    private final long timestamp;
    private int retryCount;
    
    public Message(String id, String content) {
        this.id = id;
        this.content = content;
        this.headers = new ConcurrentHashMap<>();
        this.timestamp = System.currentTimeMillis();
        this.retryCount = 0;
    }
    
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    public Message addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }
}

// Message processing result
enum MessageResult {
    SUCCESS,
    FAILED,
    RETRY_LATER
}

// Message handler function interface
interface MessageHandler {
    MessageResult handle(Message message);
}

// Message producer interface
interface MessageProducer {
    void send(Message message);
    void send(Message message, int delaySeconds);
    String getTopic();
}

// Message consumer interface
interface MessageConsumer {
    void startConsuming();
    void stopConsuming();
    void setMessageHandler(MessageHandler handler);
    String getConsumerId();
    String getConsumerGroupId();
    String getTopic();
    long getProcessedMessageCount();
}

// Message broker interface
interface MessageBroker {
    // Topic management
    void createTopic(String topic);
    boolean deleteTopic(String topic);
    List<String> getTopics();
    long getMessageCount(String topic);
    
    // Consumer group management
    void createConsumerGroup(String topic, String groupId);
    boolean deleteConsumerGroup(String topic, String groupId);
    List<String> getConsumerGroups(String topic);
    long getConsumerGroupLag(String topic, String groupId);
    
    // Producer and consumer creation
    MessageProducer createProducer(String topic);
    MessageConsumer createConsumer(String topic, String groupId, String consumerId);
    
    // Broker management
    void shutdown();
}

// Topic implementation to store messages and manage consumer offsets
@Data
class Topic {
    private final String name;
    private final ConcurrentLinkedDeque<Message> messages;
    private final Map<String, ConsumerGroup> consumerGroups;
    private final ReadWriteLock lock;
    
    public Topic(String name) {
        this.name = name;
        this.messages = new ConcurrentLinkedDeque<>();
        this.consumerGroups = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }
    
    public void addMessage(Message message) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            messages.add(message);
        } finally {
            writeLock.unlock();
        }
    }
    
    public long getMessageCount() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return messages.size();
        } finally {
            readLock.unlock();
        }
    }
    
    public void createConsumerGroup(String groupId) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            if (!consumerGroups.containsKey(groupId)) {
                consumerGroups.put(groupId, new ConsumerGroup(groupId));
            }
        } finally {
            writeLock.unlock();
        }
    }
    
    public boolean deleteConsumerGroup(String groupId) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            return consumerGroups.remove(groupId) != null;
        } finally {
            writeLock.unlock();
        }
    }
    
    public ConsumerGroup getConsumerGroup(String groupId) {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return consumerGroups.get(groupId);
        } finally {
            readLock.unlock();
        }
    }
    
    public Set<String> getConsumerGroupIds() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return new HashSet<>(consumerGroups.keySet());
        } finally {
            readLock.unlock();
        }
    }
    
    public Message getMessageAtIndex(long index) {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            if (index >= messages.size()) {
                return null;
            }
            
            Iterator<Message> iterator = messages.iterator();
            for (long i = 0; i < index; i++) {
                iterator.next();
            }
            return iterator.next();
        } finally {
            readLock.unlock();
        }
    }
}

// Consumer group implementation to track offsets and consumers
@Data
class ConsumerGroup {
    private final String groupId;
    private final Map<String, MessageConsumerImpl> consumers;
    private final AtomicLong currentOffset;
    private final Map<String, Long> consumerOffsets;
    
    public ConsumerGroup(String groupId) {
        this.groupId = groupId;
        this.consumers = new ConcurrentHashMap<>();
        this.currentOffset = new AtomicLong(0);
        this.consumerOffsets = new ConcurrentHashMap<>();
    }
    
    public void addConsumer(MessageConsumerImpl consumer) {
        consumers.put(consumer.getConsumerId(), consumer);
        consumerOffsets.putIfAbsent(consumer.getConsumerId(), currentOffset.get());
    }
    
    public void removeConsumer(String consumerId) {
        consumers.remove(consumerId);
        // Keep the offset in case the consumer reconnects
    }
    
    public long getNextMessageIndex(String consumerId) {
        return consumerOffsets.getOrDefault(consumerId, 0L);
    }
    
    public void updateConsumerOffset(String consumerId, long newOffset) {
        consumerOffsets.put(consumerId, newOffset);
    }
    
    public long getCurrentOffset() {
        return currentOffset.get();
    }
    
    public void incrementCurrentOffset() {
        currentOffset.incrementAndGet();
    }
    
    public long getLag() {
        if (consumers.isEmpty()) {
            return 0;
        }
        
        // Find the minimum offset among all consumers
        long minOffset = Long.MAX_VALUE;
        for (long offset : consumerOffsets.values()) {
            minOffset = Math.min(minOffset, offset);
        }
        
        // Calculate lag as the difference between current offset and minimum consumer offset
        return Math.max(0, currentOffset.get() - minOffset);
    }
    
    public Collection<MessageConsumerImpl> getConsumers() {
        return Collections.unmodifiableCollection(consumers.values());
    }
}

// Message producer implementation
class MessageProducerImpl implements MessageProducer {
    private final String topic;
    private final MessageBrokerImpl broker;
    
    public MessageProducerImpl(String topic, MessageBrokerImpl broker) {
        this.topic = topic;
        this.broker = broker;
    }
    
    @Override
    public void send(Message message) {
        broker.publishMessage(topic, message);
    }
    
    @Override
    public void send(Message message, int delaySeconds) {
        if (delaySeconds <= 0) {
            send(message);
            return;
        }
        
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(delaySeconds * 1000L);
                send(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    @Override
    public String getTopic() {
        return topic;
    }
}

// Message consumer implementation
class MessageConsumerImpl implements MessageConsumer, Runnable {
    private static final Logger logger = Logger.getLogger(MessageConsumerImpl.class.getName());
    private static final int MAX_RETRY_COUNT = 3;
    private static final int POLL_INTERVAL_MS = 100;
    
    private final String consumerId;
    private final String groupId;
    private final String topic;
    private final MessageBrokerImpl broker;
    private final AtomicLong processedMessages;
    private volatile boolean running;
    private volatile Thread consumerThread;
    private MessageHandler messageHandler;
    
    public MessageConsumerImpl(String topic, String groupId, String consumerId, MessageBrokerImpl broker) {
        this.topic = topic;
        this.groupId = groupId;
        this.consumerId = consumerId;
        this.broker = broker;
        this.processedMessages = new AtomicLong(0);
        this.running = false;
    }
    
    @Override
    public void startConsuming() {
        if (running) {
            return;
        }
        
        running = true;
        consumerThread = new Thread(this);
        consumerThread.setName("consumer-" + topic + "-" + groupId + "-" + consumerId);
        consumerThread.start();
        logger.info("Started consumer " + consumerId + " for topic " + topic + " in group " + groupId);
    }
    
    @Override
    public void stopConsuming() {
        running = false;
        if (consumerThread != null) {
            consumerThread.interrupt();
            try {
                consumerThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.info("Stopped consumer " + consumerId + " for topic " + topic + " in group " + groupId);
    }
    
    @Override
    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }
    
    @Override
    public String getConsumerId() {
        return consumerId;
    }
    
    @Override
    public String getConsumerGroupId() {
        return groupId;
    }
    
    @Override
    public String getTopic() {
        return topic;
    }
    
    @Override
    public long getProcessedMessageCount() {
        return processedMessages.get();
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                // Get the next message to process
                Message message = broker.getNextMessage(topic, groupId, consumerId);
                
                if (message != null && messageHandler != null) {
                    // Process the message
                    MessageResult result = messageHandler.handle(message);
                    
                    switch (result) {
                        case SUCCESS:
                            // Mark message as processed and update offset
                            broker.acknowledgeMessage(topic, groupId, consumerId);
                            processedMessages.incrementAndGet();
                            break;
                        case FAILED:
                            // Check if we should retry
                            if (message.getRetryCount() < MAX_RETRY_COUNT) {
                                message.incrementRetryCount();
                                broker.retryMessage(topic, message, 5); // Retry after 5 seconds
                            } else {
                                // Message exceeded max retries, acknowledge it anyway
                                broker.acknowledgeMessage(topic, groupId, consumerId);
                                logger.warning("Message " + message.getId() + " exceeded max retry count and will be dropped");
                            }
                            break;
                        case RETRY_LATER:
                            // Requeue the message for later processing
                            message.incrementRetryCount();
                            broker.retryMessage(topic, message, 10); // Retry after 10 seconds
                            broker.acknowledgeMessage(topic, groupId, consumerId);
                            break;
                    }
                } else {
                    // No messages available, sleep for a bit
                    Thread.sleep(POLL_INTERVAL_MS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing message", e);
                try {
                    Thread.sleep(1000); // Avoid tight loop in case of persistent errors
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            }
        }
    }
}

// Message broker implementation
class MessageBrokerImpl implements MessageBroker {
    private static final Logger logger = Logger.getLogger(MessageBrokerImpl.class.getName());
    
    private final Map<String, Topic> topics;
    private final ScheduledExecutorService scheduler;
    
    public MessageBrokerImpl() {
        this.topics = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("message-broker-scheduler");
            return t;
        });
    }
    
    @Override
    public void createTopic(String topic) {
        topics.putIfAbsent(topic, new Topic(topic));
        logger.info("Created topic: " + topic);
    }
    
    @Override
    public boolean deleteTopic(String topic) {
        boolean removed = topics.remove(topic) != null;
        if (removed) {
            logger.info("Deleted topic: " + topic);
        }
        return removed;
    }
    
    @Override
    public List<String> getTopics() {
        return new ArrayList<>(topics.keySet());
    }
    
    @Override
    public long getMessageCount(String topic) {
        Topic t = topics.get(topic);
        return t != null ? t.getMessageCount() : 0;
    }
    
    @Override
    public void createConsumerGroup(String topic, String groupId) {
        Topic t = topics.get(topic);
        if (t != null) {
            t.createConsumerGroup(groupId);
            logger.info("Created consumer group " + groupId + " for topic " + topic);
        } else {
            throw new IllegalArgumentException("Topic doesn't exist: " + topic);
        }
    }
    
    @Override
    public boolean deleteConsumerGroup(String topic, String groupId) {
        Topic t = topics.get(topic);
        if (t != null) {
            boolean removed = t.deleteConsumerGroup(groupId);
            if (removed) {
                logger.info("Deleted consumer group " + groupId + " from topic " + topic);
            }
            return removed;
        }
        return false;
    }
    
    @Override
    public List<String> getConsumerGroups(String topic) {
        Topic t = topics.get(topic);
        return t != null ? new ArrayList<>(t.getConsumerGroupIds()) : new ArrayList<>();
    }
    
    @Override
    public long getConsumerGroupLag(String topic, String groupId) {
        Topic t = topics.get(topic);
        if (t != null) {
            ConsumerGroup group = t.getConsumerGroup(groupId);
            return group != null ? group.getLag() : 0;
        }
        return 0;
    }
    
    @Override
    public MessageProducer createProducer(String topic) {
        if (!topics.containsKey(topic)) {
            throw new IllegalArgumentException("Topic doesn't exist: " + topic);
        }
        return new MessageProducerImpl(topic, this);
    }
    
    @Override
    public MessageConsumer createConsumer(String topic, String groupId, String consumerId) {
        Topic t = topics.get(topic);
        if (t == null) {
            throw new IllegalArgumentException("Topic doesn't exist: " + topic);
        }
        
        ConsumerGroup group = t.getConsumerGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Consumer group doesn't exist: " + groupId);
        }
        
        MessageConsumerImpl consumer = new MessageConsumerImpl(topic, groupId, consumerId, this);
        group.addConsumer(consumer);
        return consumer;
    }
    
    @Override
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("Message broker shutdown completed");
    }
    
    // Internal methods used by producers and consumers
    
    void publishMessage(String topicName, Message message) {
        Topic topic = topics.get(topicName);
        if (topic != null) {
            topic.addMessage(message);
            
            // Update the current offset for all consumer groups
            for (String groupId : topic.getConsumerGroupIds()) {
                ConsumerGroup group = topic.getConsumerGroup(groupId);
                if (group != null) {
                    group.incrementCurrentOffset();
                }
            }
            
            logger.fine("Published message " + message.getId() + " to topic " + topicName);
        } else {
            throw new IllegalArgumentException("Topic doesn't exist: " + topicName);
        }
    }
    
    Message getNextMessage(String topicName, String groupId, String consumerId) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            return null;
        }
        
        ConsumerGroup group = topic.getConsumerGroup(groupId);
        if (group == null) {
            return null;
        }
        
        long index = group.getNextMessageIndex(consumerId);
        return topic.getMessageAtIndex(index);
    }
    
    void acknowledgeMessage(String topicName, String groupId, String consumerId) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            return;
        }
        
        ConsumerGroup group = topic.getConsumerGroup(groupId);
        if (group == null) {
            return;
        }
        
        long currentIndex = group.getNextMessageIndex(consumerId);
        group.updateConsumerOffset(consumerId, currentIndex + 1);
    }
    
    void retryMessage(String topicName, Message message, int delaySeconds) {
        scheduler.schedule(() -> {
            try {
                publishMessage(topicName, message);
                logger.fine("Requeued message " + message.getId() + " to topic " + topicName);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to requeue message", e);
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }
}
