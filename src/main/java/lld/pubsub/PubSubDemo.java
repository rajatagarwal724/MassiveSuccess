//package lld.pubsub;
//
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicInteger;
//
//
///**
// * Java Machine Coding
// * Build a Pub-Sub Library with the following capabilities:
// *
// *
// * The library should have the notion of a Topic, Publisher, and Consumer.
// * The publisher publishes to a topic, and multiple consumers can consume from a topic.
// * One topic can have multiple consumers and publishers.
// * The library should be able to manage multiple topics:
// * Create topics
// * Delete topics
// * Consumers should be able to consume when a message is received for the topic.
// * Ability to publish messages in parallel.
// * Graceful handling of exceptions.
// * Offset Management for Consumers.
// * Topics should have a max retention period beyond which messages should be deleted (topic-level property).
// * [Bonus-1] Ability to reset offset and replay messages from a particular offset.
// * [Bonus-2] Visibility into consumer/topic status based on last offset read or lag.
// */
//class Message {
//    private final int offset;
//    private final String content;
//
//    public Message(int offset, String content) {
//        this.offset = offset;
//        this.content = content;
//    }
//
//    public int getOffset() {
//        return offset;
//    }
//
//    public String getContent() {
//        return content;
//    }
//}
//
//class Topic {
//    private final String name;
//    private final Queue<Message> messages = new ConcurrentLinkedQueue<>();
//    private final Map<Subscriber, AtomicInteger> subscriberOffsets = new ConcurrentHashMap<>();
//    private final int maxRetention;
//    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
//    private final AtomicInteger currentOffset = new AtomicInteger(0);
//    private final ExecutorService consumerExecutor = Executors.newCachedThreadPool();
//
//    public Topic(String name, int maxRetention) {
//        this.name = name;
//        this.maxRetention = maxRetention;
//        startCleanupTask();
//    }
//
//    public void publish(String message) {
//        Message msg = new Message(currentOffset.getAndIncrement(), message);
//        messages.offer(msg);
//        notifySubscribers();
//    }
//
//    public void subscribe(Subscriber subscriber) {
//        subscriberOffsets.put(subscriber, new AtomicInteger(0));
//    }
//
//    private void notifySubscribers() {
//        for (Subscriber subscriber : subscriberOffsets.keySet()) {
//            consumerExecutor.submit(() -> consume(subscriber));
//        }
//    }
//
//    private void consume(Subscriber subscriber) {
//        AtomicInteger offset = subscriberOffsets.get(subscriber);
//        while (!messages.isEmpty() && messages.peek().getOffset() >= offset.get()) {
//            Message msg = messages.poll();
//            subscriber.process(msg);
//            offset.incrementAndGet();
//        }
//    }
//
//    public void resetOffset(Subscriber subscriber, int newOffset) {
//        if (subscriberOffsets.containsKey(subscriber)) {
//            subscriberOffsets.get(subscriber).set(newOffset);
//        }
//    }
//
//    public void startCleanupTask() {
//        cleanupExecutor.scheduleAtFixedRate(() -> {
//            while (!messages.isEmpty() && messages.size() > maxRetention) {
//                messages.poll();
//            }
//        }, 10, 10, TimeUnit.SECONDS);
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public int getLag(Subscriber subscriber) {
//        return currentOffset.get() - subscriberOffsets.getOrDefault(subscriber, new AtomicInteger(0)).get();
//    }
//
//    public int getLastOffset(Subscriber subscriber) {
//        return subscriberOffsets.getOrDefault(subscriber, new AtomicInteger(0)).get();
//    }
//}
//
//class Subscriber {
//    private final String id;
//
//    public Subscriber(String id) {
//        this.id = id;
//    }
//
//    public void process(Message message) {
//        System.out.println("Subscriber " + id + " consumed message: " + message.getContent());
//    }
//
//    public String getId() {
//        return id;
//    }
//}
//
//class PubSubService {
//    private final Map<String, Topic> topics = new ConcurrentHashMap<>();
//    private final ExecutorService executor = Executors.newFixedThreadPool(5);
//
//    public void createTopic(String name, int maxRetention) {
//        topics.put(name, new Topic(name, maxRetention));
//    }
//
//    public void deleteTopic(String name) {
//        topics.remove(name);
//    }
//
//    public void publish(String topicName, String message) {
//        Topic topic = topics.get(topicName);
//        if (topic != null) {
//            executor.submit(() -> topic.publish(message));
//        }
//    }
//
//    public void subscribe(String topicName, Subscriber subscriber) {
//        Topic topic = topics.get(topicName);
//        if (topic != null) {
//            topic.subscribe(subscriber);
//        }
//    }
//
//    public void resetOffset(String topicName, Subscriber subscriber, int offset) {
//        Topic topic = topics.get(topicName);
//        if (topic != null) {
//            topic.resetOffset(subscriber, offset);
//        }
//    }
//
//    public void getConsumerStatus(String topicName, Subscriber subscriber) {
//        Topic topic = topics.get(topicName);
//        if (topic != null) {
//            System.out.println("Subscriber " + subscriber.getId() + " Last Offset Read: " + topic.getLastOffset(subscriber));
//            System.out.println("Subscriber " + subscriber.getId() + " Lag: " + topic.getLag(subscriber));
//        }
//    }
//}
//
//public class PubSubDemo {
//    public static void main(String[] args) throws InterruptedException {
//        PubSubService service = new PubSubService();
//        service.createTopic("news", 10);
//
//        Subscriber sub1 = new Subscriber("User1");
//        Subscriber sub2 = new Subscriber("User2");
//
//        service.subscribe("news", sub1);
//        service.subscribe("news", sub2);
//
//        service.publish("news", "Breaking News 1");
//        service.publish("news", "Breaking News 2");
//
//        Thread.sleep(1000);
//
//        service.getConsumerStatus("news", sub1);
//        service.getConsumerStatus("news", sub2);
//    }
//}
